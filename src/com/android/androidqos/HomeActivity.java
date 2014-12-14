/*************************************
 * activity to display brief overview 
 * of measurements
 * ***********************************/
package com.android.androidqos;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeActivity extends Activity implements OnClickListener, Constants{
	
	private int hSampleRate;
	private boolean hIsBound = false;
	private IntentFilter hIntentFilter;
	private IntentFilter aIntentFilter;
	
	private Timer timer = new Timer();
	private TimerTask timerTask ;
	private Handler handler ;
	private TextView cidValue, lacValue, longValue, latValue, wifiValue, batteryValue, batteryStatus, nbDataCollected, capMaxValue, storageValue, sampleRateValue;
	private ImageButton detailsGsmBtn, detailsGpsBtn, detailsWifiBtn, detailsBatteryBtn, detailsDataBtn, detailsSettingsBtn;
	private Button closeBtn;
	private int hCid;
	private int hLac;
	private double hLong;
	private double hLat;
	private int hMaxCapacity;
	private int hNbDataCollected;
	private int hNbDataSent;
	private int hMobileNoLine;
	private int hSDCardNoLine;
	private String hStorageDevice = "Mobile";
	private StringBuilder hStrWifi = new StringBuilder();
	private DecimalFormat df = new DecimalFormat("###.##");
	
	private ServiceConnection myServiceConnection = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			ServiceActivityInterface myBinder = (ServiceActivityInterface)service;
			hSampleRate = myBinder.getSampleRate();
			hCid = myBinder.getCID();
			hLac = myBinder.getLAC();
			hLong = myBinder.getLong();
			hLat = myBinder.getLat();
			hMaxCapacity = myBinder.getMaxCapacity();
			hNbDataCollected = myBinder.getNbDataCollected();
			hNbDataSent = myBinder.getNbDataSent();
			hStorageDevice = myBinder.getStorageDevice();
			hStrWifi = myBinder.getStrWifi();
			hMobileNoLine = myBinder.getMobileNoLine();
			hSDCardNoLine = myBinder.getSDCardNoLine();
			Log.i(getLocalClassName(), "connected");
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.i(getLocalClassName(), "disconnected");
		}
	};
	
	public void doBindService(){
		if(!hIsBound){
			bindService(new Intent(HomeActivity.this, MainService.class), myServiceConnection, Context.BIND_AUTO_CREATE);
			hIsBound = true ;
		}
	}
	
	public void doUnbindService(){
		if(hIsBound){
			unbindService(myServiceConnection);
			hIsBound = false;
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.home_layout);
		
		hIntentFilter = new IntentFilter();
		hIntentFilter.addAction(ACTION);		
		//Intent serviceIntent = new Intent(this, MainService.class);
		//startService(serviceIntent);
		
		aIntentFilter = new IntentFilter();
		aIntentFilter.addAction(HOME);
		
		setProgressBarIndeterminateVisibility(true);
		doBindService();
		this.registerReceiver(this.batteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		handler = new Handler();
        
         cidValue = (TextView)findViewById(R.id.textViewCidHomeValue);
         lacValue = (TextView)findViewById(R.id.textViewLacHomeValue);
         longValue = (TextView)findViewById(R.id.textViewLongHomeValue);
         latValue = (TextView)findViewById(R.id.textViewLatHomeValue);
         wifiValue = (TextView)findViewById(R.id.TextViewWifi);
         batteryValue = (TextView)findViewById(R.id.TextViewBattery);
         batteryStatus = (TextView)findViewById(R.id.textViewBatteryStatus);
         nbDataCollected = (TextView)findViewById(R.id.textViewNbreMesValue);
         capMaxValue = (TextView)findViewById(R.id.textViewCapMaxValue);
         storageValue = (TextView)findViewById(R.id.textViewStorageValue);
         sampleRateValue = (TextView)findViewById(R.id.textViewSampleRateValue);
    
         detailsGsmBtn = (ImageButton)findViewById(R.id.ImageButtonDetailsGsm);
         detailsGsmBtn.setOnClickListener(this);
         detailsGpsBtn = (ImageButton)findViewById(R.id.ImageButtonDetailsGps);
         detailsGpsBtn.setOnClickListener(this);
         detailsWifiBtn = (ImageButton)findViewById(R.id.ImageButtonDetailsWifi);
         detailsWifiBtn.setOnClickListener(this);
         detailsBatteryBtn = (ImageButton)findViewById(R.id.ImageButtonDetailsBattery);
         detailsBatteryBtn.setOnClickListener(this);
         detailsDataBtn = (ImageButton)findViewById(R.id.ImageButtonDetailsData);
         detailsDataBtn.setOnClickListener(this);
         detailsSettingsBtn = (ImageButton)findViewById(R.id.ImageButtonDetailsSettings);  
         detailsSettingsBtn.setOnClickListener(this);
         closeBtn = (Button)findViewById(R.id.buttonHomeClose);
         closeBtn.setOnClickListener(this);
	}
	

	private BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION)) {
				Log.i(HOME,"i'm active");
				this.setResultCode(Activity.RESULT_OK);
				HomeActivity.this.finish();
			}
			if(action.equals(HOME)){
				HomeActivity.this.finish();
				Log.i(HOME, "is active");
			}
		}
	};
	
	private BroadcastReceiver batteryReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
				batteryValue.setText(""+String.valueOf(intent.getIntExtra("level", 0))+" %");
				
				 int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
				   String strStatus;
				   if (status == BatteryManager.BATTERY_STATUS_CHARGING){
					   strStatus = "Charging";
				   } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING){
					   strStatus = "Dis-charging";
				   } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING){
					   strStatus = "Not charging";
				   } else if (status == BatteryManager.BATTERY_STATUS_FULL){
					   strStatus = "Full";
				   } else {
					   strStatus = "Unknown";
				   }
				   batteryStatus.setText("" + strStatus);
			}
			
		}};


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		doUnbindService();
		this.unregisterReceiver(batteryReceiver);
		this.unregisterReceiver(stopBroadcastReceiver);
		  if(timerTask!=null){
		      Log.d("TIMER", "timer canceled");
		      timerTask.cancel();
		      timer.cancel();
		      timer.purge();
	   }
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		setPreferences();
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
		this.registerReceiver(stopBroadcastReceiver, hIntentFilter);
		this.registerReceiver(stopBroadcastReceiver, aIntentFilter);
		getPreferences();
		doBindService();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		doBindService();
		displayValues();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		doUnbindService();
	}
	
	
	private void setPreferences(){
		SharedPreferences homePrefs = getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = homePrefs.edit();
		editor.putInt("cid", hCid);
		editor.putInt("lac", hLac);
		editor.putFloat("longitude", (float)hLong);
		editor.putFloat("latitude", (float)hLat);
		editor.putInt("maxCap", hMaxCapacity);
		editor.putInt("sampleRate", hSampleRate);
		editor.putString("storageDevice", hStorageDevice);
		editor.putInt("nbDataCollected", hNbDataCollected);
		editor.putInt("nbDataSent", hNbDataSent);
		editor.putInt("mobileNoLine", hMobileNoLine);
		editor.putInt("sdcardNoLine", hSDCardNoLine);
		editor.commit();
	}
	
	private void getPreferences(){
		SharedPreferences homePrefs = getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
		hCid = homePrefs.getInt("cid", hCid);
		cidValue.setText(""+String.valueOf(hCid));
		
		hLac = homePrefs.getInt("lac", hLac);
		lacValue.setText(""+String.valueOf(hLac));
		
		hLong = homePrefs.getFloat("longitude", (float)hLong);
		longValue.setText(""+String.valueOf(df.format(hLong)));
		
		hLat = homePrefs.getFloat("latitude", (float)hLat);
		latValue.setText(""+String.valueOf(df.format(hLat)));
		
		hMaxCapacity = homePrefs.getInt("maxCap", hMaxCapacity);
		capMaxValue.setText(""+String.valueOf( hMaxCapacity));
		
		hSampleRate = homePrefs.getInt("sampleRate", hSampleRate);
		sampleRateValue.setText(""+String.valueOf( hSampleRate));
		
		hStorageDevice = homePrefs.getString("storageDevice", hStorageDevice);
		storageValue.setText(""+hStorageDevice);
		
		hNbDataCollected = homePrefs.getInt("nbDataCollected", hNbDataCollected);
		nbDataCollected.setText(""+String.valueOf( hNbDataCollected));
		
	}
	
	private void displayValues() {
		// TODO Auto-generated method stub
		
		timerTask = new TimerTask() {
	        public void run() {
/*	        	
	        	ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
	            dialog.setMessage("Loading data ...");
	            dialog.show();
//*/	            
	        	try {

	        		doUnbindService();
	        		doBindService();

				} catch (Throwable t) {
					t.printStackTrace();
				}

	                handler.post(new Runnable() {

							public void run() {
	                        	try{
	                        		cidValue.setText(""+String.valueOf(hCid));
	                        		lacValue.setText(""+String.valueOf(hLac));
	                        		longValue.setText(""+String.valueOf(hLong));
	                        		latValue.setText(""+String.valueOf(hLat));
	                        		wifiValue.setText(""+hStrWifi);
	                        		sampleRateValue.setText(""+String.valueOf(hSampleRate/1000)+" s");
	                        		storageValue.setText(""+hStorageDevice);
	                        		capMaxValue.setText(""+String.valueOf(hMaxCapacity)+" KB");
	                        		nbDataCollected.setText(""+String.valueOf(hNbDataCollected));
	                        	}catch(Throwable t){
	                        		t.printStackTrace();
	                        	}
	                        }
	               });
/*	                
	                if(dialog.isShowing())
	                {
	                    dialog.dismiss();
	                }
//*/	                
	        }};
	    timer.schedule(timerTask, DELAY_TIME, UPDATE_TIME); 

	}
	
	boolean isActive;
	private void checkActiveActivity(final String activity) {
		Log.i(HOME, "start checking for active Activity ");
		Intent intent = new Intent();
		intent.setAction(activity);
		sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int result = getResultCode();

				if (result != Activity.RESULT_CANCELED) { // Activity caught it
					Log.i(HOME, "An activity caught the broadcast, result " + result);
					activeActivity(activity);
					isActive = true;
				}else{
					Log.i(HOME, "No activity did catch the broadcast.");
					noActiveActivity(activity);
					isActive = false;
				}
			}
		}, null, Activity.RESULT_CANCELED, null, null);
	}
	
	protected void noActiveActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(HOME, "no active activity");
		if(activity.equals(BATTERY)){
			Intent batteryIntent = new Intent(HomeActivity.this,BatteryActivity.class);
	        startActivity(batteryIntent);
		}else if(activity.equals(DATA)){
			Intent dataIntent = new Intent(HomeActivity.this,DataActivity.class);
	        startActivity(dataIntent);
		}else if(activity.equals(LOCATION)){
			Intent gpsIntent = new Intent(HomeActivity.this,LocationActivity.class);
	        startActivity(gpsIntent);
		}else if(activity.equals(CELL)){
			Intent gsmIntent = new Intent(HomeActivity.this,CellActivity.class);
	        startActivity(gsmIntent);
		}else if(activity.equals(SETTINGS)){
			Intent settingsIntent = new Intent(HomeActivity.this,SettingsActivity.class);
	        startActivity(settingsIntent);
		}else if(activity.equals(WIFI)){
			Intent wifiIntent = new Intent(HomeActivity.this,WifiActivity.class);
	        startActivity(wifiIntent);
		} 
	}

	protected void activeActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(HOME, "active activity");
		checkActiveActivity(activity);
	}	

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.buttonHomeClose:
			HomeActivity.this.finish();
			break;
		case R.id.ImageButtonDetailsBattery:
				checkActiveActivity(BATTERY);	
			break;
		case R.id.ImageButtonDetailsData:
			checkActiveActivity(DATA);	;
			break;
		case R.id.ImageButtonDetailsGps:
			checkActiveActivity(LOCATION);	
			break;
		case R.id.ImageButtonDetailsGsm:
			checkActiveActivity(CELL);	
			break;
		case R.id.ImageButtonDetailsSettings:
			checkActiveActivity(SETTINGS);	
			break;
		case R.id.ImageButtonDetailsWifi:
			checkActiveActivity(WIFI);	
			break;
		default:
			break;
		}
		
	}

}
