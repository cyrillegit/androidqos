/**************************************
 * activity to display mobile location
 * ************************************/

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

public class LocationActivity extends Activity implements OnClickListener, Constants{
	
	private TextView textLong;
	private TextView textLat;
	private TextView textAlt;
	private TextView textSpeed;
	private TextView textTime;
	private TextView textAccuracy;
	private TextView textProvider;
	private ImageButton btnGpsNext;
	private ImageButton btnGpsPrevious;
	private Button btnLocationHome;
	private TextView textNbSat;
	private TextView textFixSat;
	
	private TimerTask timerTask;
	private Timer gpsTimer;
	private Handler handler ;
	private IntentFilter lIntentFilter;
	private IntentFilter aIntentFilter;
	
	private double gLongitude;
	private double gLatitude;
	private double gAltitude;
	private String gTime = "";
	private double gSpeed;
	private float gAccuracy;
	private String gProvider;
	
	private boolean gIsBound = false;
	private int lNbSat;
	private int lFixSat;
	
	private ServiceConnection gpsServiceConnection = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder gpsService) {
			// TODO Auto-generated method stub
			ServiceActivityInterface myBinder = (ServiceActivityInterface)gpsService;
			gLongitude = myBinder.getLong();
			gLatitude = myBinder.getLat();
			gAltitude = myBinder.getAlt();
			gTime = myBinder.getTheTime();
			gSpeed = myBinder.getTheSpeed();
			gAccuracy = myBinder.getTheAccuracy();
			gProvider = myBinder.getTheProvider();
			lNbSat = myBinder.getNbSat();
			lFixSat = myBinder.getfixSat();
			Log.i(getLocalClassName(), "connected: ");
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.i(getLocalClassName(), "disconnected");
		}
	};
	
	public void doBindService(){
		if(!gIsBound){
			bindService(new Intent(LocationActivity.this, MainService.class), gpsServiceConnection, Context.BIND_AUTO_CREATE);
			gIsBound = true ;
		}
	}
	
	public void doUnbindService(){
		if(gIsBound){
			unbindService(gpsServiceConnection);
			gIsBound = false;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		
		lIntentFilter = new IntentFilter();
		lIntentFilter.addAction(ACTION);		
		Intent serviceIntent = new Intent(this, MainService.class);
		startService(serviceIntent);
		
		aIntentFilter = new IntentFilter();
		aIntentFilter.addAction(LOCATION);	
		
		setProgressBarIndeterminateVisibility(true);
		handler = new Handler();
		gpsTimer = new Timer();
		
		btnGpsNext = (ImageButton)findViewById(R.id.ImageButtonGpsNext);
		btnGpsNext.setOnClickListener(this);
		btnGpsPrevious = (ImageButton)findViewById(R.id.ImageButtonGpsPrevious);
		btnGpsPrevious.setOnClickListener(this);
		btnLocationHome = (Button)findViewById(R.id.buttonLocationHome);
		btnLocationHome.setOnClickListener(this);
		
		textLong = (TextView)findViewById(R.id.textViewLongitudeValue);
		textLat = (TextView)findViewById(R.id.textViewLatitudeValue);
		textAlt = (TextView)findViewById(R.id.textViewAltitudeValue);
		textSpeed = (TextView)findViewById(R.id.textViewSpeedValue);
		textTime = (TextView)findViewById(R.id.textViewTimeValue);
		textAccuracy = (TextView)findViewById(R.id.textViewAccuracyValue);
		textProvider = (TextView)findViewById(R.id.textViewProviderValue);
		textNbSat = (TextView)findViewById(R.id.textViewNbSatValue);
		textFixSat = (TextView)findViewById(R.id.textViewFixSatValue);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		doUnbindService();
		this.unregisterReceiver(stopBroadcastReceiver);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		doUnbindService();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		doBindService();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doBindService();
		this.registerReceiver(stopBroadcastReceiver, lIntentFilter);
		this.registerReceiver(stopBroadcastReceiver, aIntentFilter);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		doBindService();

		display();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		doUnbindService();
	}
	

	private BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION)) {
				Log.i("Location","i'm active");
				this.setResultCode(Activity.RESULT_OK);
				LocationActivity.this.finish();
			}
			if(action.equals(LOCATION)){
				LocationActivity.this.finish();
				Log.i(LOCATION, "is active");
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
	                        	textLong.setText(""+String.valueOf(gLongitude));
	                        	textLat.setText(""+String.valueOf(gLatitude));
	                        	textAlt.setText(""+String.valueOf(gAltitude));
	                        	textSpeed.setText(""+String.valueOf(gSpeed));
	                        	textTime.setText(""+gTime);
	                        	textAccuracy.setText(""+String.valueOf(gAccuracy));
	                        	textProvider.setText(""+gProvider);
	                        	textNbSat.setText(""+String.valueOf(lNbSat));
	                        	textFixSat.setText(""+String.valueOf(lFixSat));
	                        }
	               });
	        }};
	    gpsTimer.schedule(timerTask, DELAY_TIME, UPDATE_TIME); 
	}
	
	private void checkActiveActivity(final String activity) {
		Log.i(LOCATION, "start checking for active Activity ");
		Intent intent = new Intent();
		intent.setAction(activity);
		sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int result = getResultCode();

				if (result != Activity.RESULT_CANCELED) { // Activity caught it
					Log.i(LOCATION, "An activity caught the broadcast, result " + result);
					activeActivity(activity);
				}else{
					Log.i(LOCATION, "No activity did catch the broadcast.");
					noActiveActivity(activity);
				}
			}
		}, null, Activity.RESULT_CANCELED, null, null);
	}
	
	protected void noActiveActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(LOCATION, "no active activity");
		if(activity.equals(HOME)){
			Intent homeIntent = new Intent(LocationActivity.this,HomeActivity.class);
	        startActivity(homeIntent);
		}else if(activity.equals(CELL)){
			Intent cellIntent = new Intent(LocationActivity.this,CellActivity.class);
	        startActivity(cellIntent);
		}else if(activity.equals(WIFI)){
			Intent wifiIntent = new Intent(LocationActivity.this,WifiActivity.class);
	        startActivity(wifiIntent);
		}
	}

	protected void activeActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(LOCATION, "active activity");
		checkActiveActivity(activity);
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.ImageButtonGpsNext:
			checkActiveActivity(WIFI);
	    break;
		case R.id.ImageButtonGpsPrevious:
			checkActiveActivity(CELL);
	    break;
		case R.id.buttonLocationHome:
			checkActiveActivity(HOME);
	    break;
		default:
		break;
		}	
		
	}

}
