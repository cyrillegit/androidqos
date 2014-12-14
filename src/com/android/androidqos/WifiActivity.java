/****************************************
 * activity to display wifi measurements
 * **************************************/
package com.android.androidqos;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class WifiActivity extends Activity implements OnClickListener,Constants{

	private TextView wifiDescription;
	private TimerTask timerTask;
	private Handler handler ;
	private Timer wifiTimer;
	private boolean wIsBound = false;
    private StringBuilder wStrDetailsWifi = new StringBuilder();
	private IntentFilter wIntentFilter;
	private IntentFilter aIntentFilter;
	
	private ImageButton btnWifiNext;
	private ImageButton btnWifiPrevious;
	private Button btnWifiHome;

	private ServiceConnection wifiServiceConnection = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder wifiService) {
			// TODO Auto-generated method stub
			ServiceActivityInterface myBinder = (ServiceActivityInterface)wifiService;
			wStrDetailsWifi  = myBinder.getStrDetailsWifi();
			Log.i(getLocalClassName(), "connected!");
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.i(getLocalClassName(), "disconnected");
		}
	};
	
	public void doBindService(){
		if(!wIsBound){
			bindService(new Intent(WifiActivity.this, MainService.class), wifiServiceConnection, Context.BIND_AUTO_CREATE);
			wIsBound = true ;
		}
	}
	
	public void doUnbindService(){
		if(wIsBound){
			unbindService(wifiServiceConnection);
			wIsBound = false;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.wifi_layout);
		
		wIntentFilter = new IntentFilter();
		wIntentFilter.addAction(ACTION);		
		Intent serviceIntent = new Intent(this, MainService.class);
		startService(serviceIntent);
		
		aIntentFilter = new IntentFilter();
		aIntentFilter.addAction(WIFI);
		
		setProgressBarIndeterminateVisibility(true);
		handler = new Handler();
		wifiTimer = new Timer();
	
		wifiDescription = (TextView)findViewById(R.id.textViewWifiDescription);
		btnWifiNext = (ImageButton)findViewById(R.id.ImageButtonWifiNext);
		btnWifiNext.setOnClickListener(this);
		btnWifiPrevious = (ImageButton)findViewById(R.id.ImageButtonWifiPrevious);
		btnWifiPrevious.setOnClickListener(this);
		btnWifiHome = (Button)findViewById(R.id.buttonWifiHome);
		btnWifiHome.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		wifiTimer.cancel();
		wifiTimer.purge();
		timerTask.cancel();
		this.unregisterReceiver(stopBroadcastReceiver);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		doUnbindService();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doBindService();
		this.registerReceiver(stopBroadcastReceiver, wIntentFilter);
		this.registerReceiver(stopBroadcastReceiver, aIntentFilter);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		display();
	}

	private BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION)) {
				Log.i(WIFI,"i'm active");
				this.setResultCode(Activity.RESULT_OK);
				WifiActivity.this.finish();
			}
			if(action.equals(WIFI)){
				WifiActivity.this.finish();
				Log.i(WIFI, "is active");
			}
		}
	};
	
	
	public void display(){

		timerTask = new TimerTask() {

			public void run() {
	        	try {
	        		doUnbindService();
	        		doBindService();

				} catch (Throwable t) {
					t.printStackTrace();
				}
	                handler.post(new Runnable() {
	                        public void run() {	                        	
	                         wifiDescription.setText(""+wStrDetailsWifi);
	                        }
	               });
	        }};
	    wifiTimer.schedule(timerTask, DELAY_TIME, UPDATE_TIME); 
	}

	private void checkActiveActivity(final String activity) {
		Log.i(WIFI, "start checking for active Activity ");
		Intent intent = new Intent();
		intent.setAction(activity);
		sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int result = getResultCode();

				if (result != Activity.RESULT_CANCELED) { // Activity caught it
					Log.i(WIFI, "An activity caught the broadcast, result " + result);
					activeActivity(activity);
				}else{
					Log.i(WIFI, "No activity did catch the broadcast.");
					noActiveActivity(activity);
				}
			}
		}, null, Activity.RESULT_CANCELED, null, null);
	}
	
	protected void noActiveActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(WIFI, "no active activity");
		if(activity.equals(HOME)){
			Intent homeIntent = new Intent(WifiActivity.this,HomeActivity.class);
	        startActivity(homeIntent);
		}else if(activity.equals(BATTERY)){
			Intent batteryIntent = new Intent(WifiActivity.this,BatteryActivity.class);
	        startActivity(batteryIntent);
		}else if(activity.equals(LOCATION)){
			Intent locationIntent = new Intent(WifiActivity.this,LocationActivity.class);
	        startActivity(locationIntent);
		}
	}

	protected void activeActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(WIFI, "active activity");
		checkActiveActivity(activity);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.ImageButtonWifiNext:
			checkActiveActivity(BATTERY);
	    break;
		case R.id.ImageButtonWifiPrevious:
			checkActiveActivity(LOCATION);
	    break;
		case R.id.buttonWifiHome:
			checkActiveActivity(HOME);
	    break;
		default:
		break;
		}	
		
	}
}
