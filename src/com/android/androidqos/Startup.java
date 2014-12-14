/*********************************************
 * Activity to start up the application
 * *******************************************/
package com.android.androidqos;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Startup extends Activity implements Constants{
	
   public boolean isReboot = false;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activeActivity();
        this.finish();
    }
    
	private void checkActiveActivity() {
		Log.i(STARTUP, "start checking for active Activity ");
		Intent intent = new Intent();
		intent.setAction(ACTION);
		sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int result = getResultCode();
				 if(intent.hasExtra(REBOOT)){
						Bundle bundle = intent.getExtras();
						isReboot = bundle.getBoolean(REBOOT, false);
						Log.i(STARTUP+"_0", ""+intent+" reboot: "+isReboot);
				 }
				if (result != Activity.RESULT_CANCELED) { // Activity caught it
					isReboot = true;
					Log.i(STARTUP, "An activity caught the broadcast, result " + result);
					activeActivity();
				}else{
					Log.i(STARTUP, "No activity did catch the broadcast.");
					noActiveActivity();
				}
			}
		}, null, Activity.RESULT_CANCELED, null, null);
	}
	
	protected void noActiveActivity() {
		// TODO Auto-generated method stub
		Log.i(STARTUP, "no active activity");
		cancelNotification();
		createNotification();
		startMainService();		
	}

	protected void activeActivity() {
		// TODO Auto-generated method stub
		Log.i(STARTUP, "active activity");
		checkActiveActivity();
	}
	
	private void startMainService(){
        
		Intent serviceIntent = new Intent(Startup.this,MainService.class);
		Bundle sBundle = new Bundle();
		sBundle.putBoolean(REBOOT, isReboot);
		serviceIntent.putExtras(sBundle);
		serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(serviceIntent);
	}

    
    //*
  	private void createNotification() {
  		// TODO Auto-generated method stub
  		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
  		Notification notification = new Notification(R.drawable.icon, "Android QoS App launched ...", System.currentTimeMillis());
  		Intent notificationIntent = new Intent(Startup.this, HomeActivity.class);
  		notificationIntent.setFlags(Intent.FLAG_FROM_BACKGROUND|Intent.FLAG_ACTIVITY_SINGLE_TOP);
  		PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, 0);
  		String titleNotif = "Android QoS ";
  		String textNotif = "Android QoS ";
  		notification.setLatestEventInfo(this, titleNotif, textNotif, pendingIntent);
  		notification.vibrate = new long[]{0,200,100,200,100,200};
  		notification.flags|=Notification.FLAG_NO_CLEAR;
  		notificationManager.notify(NOTIF_STARTUP, notification);
   
  	}
  	
	private void cancelNotification(){
		
    	NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    	notificationManager.cancel(NOTIF_STARTUP);
	}
	
}