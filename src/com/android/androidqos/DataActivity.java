/*****************************************
 * activity to display evolution of data
 * ***************************************/
package com.android.androidqos;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DataActivity extends Activity implements OnClickListener, Constants{
	
	private TextView nbDataCollected;
	private TextView nbDataSent;
	private TextView maximumCapacity;
	private TextView dataInMobile;
	private TextView dataInSD;
	private TextView usedCapacity;
	
	private ProgressBar progressBarLevel;
	private TimerTask timerTask;
	private Timer dataTimer ;
	private Handler handler ;
	private boolean dIsBound = false;
	private int dNbDataCollected;
	private int dNbDataSent;
	private int dMaxCapacity;
    private float dDataInMobile = 0;
	private float dDataInSDCard = 0;
	private String dStorageDevice;
	private final String[]choices = {"Yes","No"};
	
	private ImageButton btnDataNext;
	private ImageButton btnDataPrevious;
	private Button btnDataHome;
	private Button btnDataCollectedReset;
	private Button btnDataSentReset;
	private Button btnDataInMobileDelete;
	private Button btnDataInSDCardDelete;
	private Button btnDataResetAll;
	private IntentFilter dIntentFilter;
	private IntentFilter aIntentFilter;
	
	private DecimalFormat df = new DecimalFormat("###.##");

	/** get data from the service */
	private ServiceConnection dataServiceConnection = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder cellService) {
			// TODO Auto-generated method stub
			ServiceActivityInterface myBinder = (ServiceActivityInterface)cellService;
			dNbDataCollected = myBinder.getNbDataCollected();
			dNbDataSent = myBinder.getNbDataSent();
			dMaxCapacity = myBinder.getMaxCapacity();
			dDataInMobile = myBinder.getSizeDataMobile();
			dDataInSDCard = myBinder.getSizeDataSDCard();
			dStorageDevice = myBinder.getStorageDevice();
			Log.i(getLocalClassName(), "connected");
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.i(getLocalClassName(), "disconnected");
		}
	};
	
	/** bind to the service */
	public void doBindService(){
		if(!dIsBound){
			bindService(new Intent(DataActivity.this, MainService.class), dataServiceConnection, Context.BIND_AUTO_CREATE);
			dIsBound = true ;
		}
	}
	
	/** unbind to the service */
	public void doUnbindService(){
		if(dIsBound){
			unbindService(dataServiceConnection);
			dIsBound = false;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.data_layout);
		
		dIntentFilter = new IntentFilter();
		dIntentFilter.addAction(ACTION);		
		Intent serviceIntent = new Intent(this, MainService.class);
		startService(serviceIntent);
		
		aIntentFilter = new IntentFilter();
		aIntentFilter.addAction(DATA);

		setProgressBarIndeterminateVisibility(true);
		doBindService();
		dataTimer = new Timer();
		handler = new Handler();
		
		btnDataNext = (ImageButton)findViewById(R.id.imageButtonDataNext);
		btnDataNext.setOnClickListener(this);
		btnDataPrevious = (ImageButton)findViewById(R.id.imageButtonDataPrevious);
		btnDataPrevious.setOnClickListener(this);
		btnDataHome = (Button)findViewById(R.id.buttonDataHome);
		btnDataHome.setOnClickListener(this);	
		btnDataCollectedReset = (Button)findViewById(R.id.buttonDataCollectedReset);
		btnDataCollectedReset.setOnClickListener(this);
		btnDataSentReset = (Button)findViewById(R.id.buttonDataSentReset);
		btnDataSentReset.setOnClickListener(this);
		btnDataInMobileDelete = (Button)findViewById(R.id.buttonDataInMobile);
		btnDataInMobileDelete.setOnClickListener(this);
		btnDataInSDCardDelete = (Button)findViewById(R.id.buttonDataInSDCard);
		btnDataInSDCardDelete.setOnClickListener(this);
		btnDataResetAll = (Button)findViewById(R.id.buttonClearAll);
		btnDataResetAll.setOnClickListener(this);	
		
		nbDataCollected = (TextView)findViewById(R.id.textViewNbDataCollectedValue);
		nbDataSent = (TextView)findViewById(R.id.textViewNbDataSentValue);
		maximumCapacity = (TextView)findViewById(R.id.textViewMaximumCapacityValue);
		dataInMobile = (TextView)findViewById(R.id.textViewDataInMobileValue);
		dataInSD  = (TextView)findViewById(R.id.textViewDataInSDCardValue);
		usedCapacity = (TextView)findViewById(R.id.textViewLevelTransferValue);
		progressBarLevel = (ProgressBar)findViewById(R.id.progressBarLevel);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		doUnbindService();
		this.unregisterReceiver(stopBroadcastReceiver);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		doBindService();
		displayPeriodically();
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		doUnbindService();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		setPreferences();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.registerReceiver(stopBroadcastReceiver, dIntentFilter);
		this.registerReceiver(stopBroadcastReceiver, aIntentFilter);
		getPreferences();		
	}
	

	private BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION)) {
				Log.i(DATA,"i'm active");
				this.setResultCode(Activity.RESULT_OK);
				DataActivity.this.finish();
			}	
			if(action.equals(DATA)){
				DataActivity.this.finish();
				Log.i(DATA, "is active");
			}
		}
	};
	

	private void setPreferences(){
		SharedPreferences dataPrefs = getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = dataPrefs.edit();
		editor.putFloat("dataInMobile", dDataInMobile);
		editor.putFloat("dataInSDCard", dDataInSDCard);
		editor.putInt("nbDataCollected", dNbDataCollected);
		editor.putInt("nbDataSent", dNbDataSent);
		editor.commit();
	}
	
	private void getPreferences(){
		SharedPreferences dataPrefs = getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
		dDataInMobile = dataPrefs.getFloat("dataInMobile", dDataInMobile);	
		dDataInSDCard = dataPrefs.getFloat("dataInSDCard", dDataInSDCard);
    	dataInMobile.setText(""+String.valueOf(df.format((float)dDataInMobile/(float)1024))+" KB");
    	dataInSD.setText(""+String.valueOf(df.format((float)dDataInSDCard/(float)1024))+" KB");
	}
	
	private void displayValues(){
		
    	nbDataCollected.setText(""+String.valueOf(dNbDataCollected));
    	nbDataSent.setText(""+String.valueOf(dNbDataSent));
    	maximumCapacity.setText(""+String.valueOf(dMaxCapacity)+" KB");
    	dataInMobile.setText(""+String.valueOf(df.format((float)dDataInMobile/(float)1024))+" KB");
    	dataInSD.setText(""+String.valueOf(df.format((float)dDataInSDCard/(float)1024))+" KB");
    	if(dStorageDevice.equals("Mobile")){
    		usedCapacity.setText(""+String.valueOf(df.format(((float)(dDataInMobile)/(float)(dMaxCapacity*1024))*100))+" %");
    		progressBarLevel.setProgress((int) (((float)(dDataInMobile)/(float)(dMaxCapacity*1024))*100));
    	}else{
    		usedCapacity.setText(""+String.valueOf(df.format(((float)(dDataInSDCard)/(float)(dMaxCapacity*1024))*100))+" %");
    		progressBarLevel.setProgress((int) (((float)(dDataInSDCard)/(float)(dMaxCapacity*1024))*100));
    	}
	}
	
	private void displayPeriodically(){

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
								displayValues();
	                       }
	               });
	        }};
	    dataTimer.schedule(timerTask, DELAY_TIME, UPDATE_TIME); 

	}
	
	 String choice = null;
	private void confirmChoice(final String subject){
		new AlertDialog.Builder(DataActivity.this)
		.setItems(choices, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				choice = choices[which];
				Intent dIntent = new Intent(DataActivity.this, MainService.class);
				Bundle dBundle = new Bundle();
				dBundle.putString(subject, choice);
				dIntent.putExtras(dBundle);
				startService(dIntent);
				applyChoice(choice, subject);
			}
		}).setTitle("confirm your choice").create().show();
	}
	
	private void applyChoice(String choice, String subject){
		if(choice.equals("Yes")){
			if(subject.equals("dataCollected")){
				dNbDataCollected = 0;
			}else if(subject.equals("dataSent")){
				dNbDataSent = 0;
			}else if(subject.equals("dataInSDCard")){
				dDataInSDCard = 0;
			}else if(subject.equals("dataInMobile")){
				dDataInMobile = 0;
			}else if(subject.equals("clearAll")){
				dNbDataCollected = 0;
				dNbDataSent = 0;
				dDataInSDCard = 0;
				dDataInMobile = 0;
			}
			displayValues();
		}
	}

	private void checkActiveActivity(final String activity) {
		Log.i(DATA, "start checking for active Activity ");
		Intent intent = new Intent();
		intent.setAction(activity);
		sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int result = getResultCode();

				if (result != Activity.RESULT_CANCELED) { // Activity caught it
					Log.i(DATA, "An activity caught the broadcast, result " + result);
					activeActivity(activity);
				}else{
					Log.i(DATA, "No activity did catch the broadcast.");
					noActiveActivity(activity);
				}
			}
		}, null, Activity.RESULT_CANCELED, null, null);
	}
	
	protected void noActiveActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(DATA, "no active activity");
		if(activity.equals(HOME)){
			Intent homeIntent = new Intent(DataActivity.this,HomeActivity.class);
	        startActivity(homeIntent);
		}else if(activity.equals(BATTERY)){
			Intent batteryIntent = new Intent(DataActivity.this,BatteryActivity.class);
	        startActivity(batteryIntent);
		}else if(activity.equals(SETTINGS)){
			Intent settingsIntent = new Intent(DataActivity.this,SettingsActivity.class);
	        startActivity(settingsIntent);
		}
	}

	protected void activeActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(DATA, "active activity");
		checkActiveActivity(activity);
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){	
		case R.id.imageButtonDataNext:
			checkActiveActivity(SETTINGS);
	    break;
		case R.id.imageButtonDataPrevious:
			checkActiveActivity(BATTERY);
	    break;
		case R.id.buttonDataHome:
			checkActiveActivity(HOME);	
	    break;	    
		case R.id.buttonDataCollectedReset:
			 confirmChoice("dataCollected");
	    break;
		case R.id.buttonDataSentReset:
			confirmChoice("dataSent");
	    break;
		case R.id.buttonDataInMobile:
			confirmChoice("dataInMobile");
	    break;
		case R.id.buttonDataInSDCard:
			confirmChoice("dataInSDCard");
	    break;
		case R.id.buttonClearAll:
			confirmChoice("clearAll");
	    break;	    
		default:
		break;
		}	
		
	}

}
