/********************************
 * activity to manage settings 
 * ******************************/

package com.android.androidqos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;

public class SettingsActivity extends Activity implements OnClickListener, Constants{
	
	private IntentFilter sIntentFilter;

	private CheckBox cellCheckBox;
	private CheckBox gpsCheckBox;
	private CheckBox wifiCheckBox;
	private Button defaultBtn;
	private Button applyBtn;
	private Button cancelBtn;
	private CheckBox modeCheckBox;
	private final String[]choices = {"Yes","No"};
	
	private static boolean sIsSimu = true;
	private static boolean sIsCell = true;
	private static boolean sIsWifi = true;
	private static boolean sIsGps = true;
	private int sSampleRate;
	private String sStorageDevice = "SDCARD";
	private int sSendData = 0;
	private int sStopAppBattery = 0;
	private String choice = null;

	private int aSampleRate;
	private String aStorageDevice = "SDCARD";
	private int aSendData;
	private int aStopAppBattery;

	private Spinner storageSpinner;
	private Spinner sendDataSpinner;
	private Spinner stopAppBatterySpinner;
	private Spinner sampleRateSpinner;
	private ImageButton btnSettingsNext;
	private ImageButton btnSettingsPrevious;
	private Button btnSettingsHome;
	private Button btnStopService;
	private IntentFilter aIntentFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		
		sIntentFilter = new IntentFilter();
		sIntentFilter.addAction(ACTION);		
		Intent serviceIntent = new Intent(this, MainService.class);
		startService(serviceIntent);
		
		aIntentFilter = new IntentFilter();
		aIntentFilter.addAction(SETTINGS);

		storageSpinner = (Spinner)findViewById(R.id.spinnerStorage);
		sendDataSpinner = (Spinner)findViewById(R.id.spinnerSendData);
		stopAppBatterySpinner = (Spinner)findViewById(R.id.spinnerStopApp);
		sampleRateSpinner = (Spinner)findViewById(R.id.spinnerSampleRate);
		
		ArrayAdapter<CharSequence> storageAdapter = ArrayAdapter.createFromResource(this, R.array.device,R.layout.spinner_layout);
		storageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		storageSpinner.setAdapter(storageAdapter);
		
		ArrayAdapter<CharSequence> sendDataAdapter = ArrayAdapter.createFromResource(this, R.array.sendData, R.layout.spinner_layout);
		sendDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sendDataSpinner.setAdapter(sendDataAdapter);
		
		ArrayAdapter<CharSequence> stopAppBatteryAdapter = ArrayAdapter.createFromResource(this, R.array.stopAppBattery, R.layout.spinner_layout);
		stopAppBatteryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stopAppBatterySpinner.setAdapter(stopAppBatteryAdapter);
		
		ArrayAdapter<CharSequence> sampleRateAdapter = ArrayAdapter.createFromResource(this, R.array.sampleRate, R.layout.spinner_layout);
		sampleRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sampleRateSpinner.setAdapter(sampleRateAdapter);
		
		btnSettingsNext = (ImageButton)findViewById(R.id.imageButtonSettingsNext);
		btnSettingsNext.setOnClickListener(this);
		btnSettingsPrevious = (ImageButton)findViewById(R.id.imageButtonSettingsPrevious);
		btnSettingsPrevious.setOnClickListener(this);
		cellCheckBox = (CheckBox)findViewById(R.id.checkBoxCell);
		cellCheckBox.setOnClickListener(this);
		gpsCheckBox = (CheckBox)findViewById(R.id.checkBoxGps);
		gpsCheckBox.setOnClickListener(this);
		wifiCheckBox = (CheckBox)findViewById(R.id.checkBoxWifi);
		wifiCheckBox.setOnClickListener(this);
		defaultBtn = (Button)findViewById(R.id.buttonSettingsDefault);
		defaultBtn.setOnClickListener(this);
		modeCheckBox = (CheckBox)findViewById(R.id.checkBoxMode);
		modeCheckBox.setOnClickListener(this);
		applyBtn = (Button)findViewById(R.id.buttonSettingsApply);
		applyBtn.setOnClickListener(this);
		cancelBtn = (Button)findViewById(R.id.buttonSettingsCancel);
		cancelBtn.setOnClickListener(this);
		btnSettingsHome = (Button)findViewById(R.id.buttonSettingsHome);
		btnSettingsHome.setOnClickListener(this);
		btnStopService = (Button)findViewById(R.id.buttonStopApp);
		btnStopService.setOnClickListener(this);
		
		storageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view,int i, long l) {
				// TODO Auto-generated method stub
				switch(i){
				case 0:
					aStorageDevice = "MOBILE";
					break;
				case 1:
					aStorageDevice = "SDCARD";
					break;
				default:
					break;
				}
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		sendDataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view,int i, long l) {
				// TODO Auto-generated method stub
				aSendData = i;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		stopAppBatterySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				// TODO Auto-generated method stub
				aStopAppBattery = i;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		sampleRateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				// TODO Auto-generated method stub
				aSampleRate = 10000*(i+1);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(stopBroadcastReceiver);
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
		this.registerReceiver(stopBroadcastReceiver, sIntentFilter);
		this.registerReceiver(stopBroadcastReceiver, aIntentFilter);
		getPreferences();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		getPreferences();
		cellCheckBox.setChecked(sIsCell);
		gpsCheckBox.setChecked(sIsGps);
		wifiCheckBox.setChecked(sIsWifi);
		modeCheckBox.setChecked(sIsSimu);
		
		sampleRateSpinner.setSelection((sSampleRate/10000)-1);
		if(sStorageDevice.equals("MOBILE")){
			storageSpinner.setSelection(0);
		}else{
			storageSpinner.setSelection(1);
		}
		sendDataSpinner.setSelection(sSendData);
		stopAppBatterySpinner.setSelection(sStopAppBattery);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	private void setPreferences(){
		SharedPreferences settingsPrefs = getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settingsPrefs.edit();
		editor.putInt("sampleRate", sSampleRate);
		editor.putString("storageDevice", sStorageDevice);
		editor.putBoolean("isCell", sIsCell);
		editor.putBoolean("isGps", sIsGps);
		editor.putBoolean("isWifi", sIsWifi);
		editor.putBoolean("isSimu", sIsSimu);
		editor.putInt("sendData", sSendData);
		editor.putInt("stopAppBattery", sStopAppBattery);
		editor.commit();
	}
	
	private void getPreferences(){
		SharedPreferences settingsPrefs = getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
		sSampleRate = settingsPrefs.getInt("sampleRate", sSampleRate);
		sStorageDevice = settingsPrefs.getString("storageDevice", sStorageDevice);
		sIsCell = settingsPrefs.getBoolean("isCell", sIsCell);
		sIsGps = settingsPrefs.getBoolean("isGps", sIsGps);
		sIsWifi = settingsPrefs.getBoolean("isWifi", sIsWifi);
		sIsSimu = settingsPrefs.getBoolean("isSimu", sIsSimu);
		sSendData = settingsPrefs.getInt("sendData", sSendData);
		sStopAppBattery = settingsPrefs.getInt("stopAppBattery", sStopAppBattery);
	}
	
	/** method to apply settings to the App */
	private void applySettings(){
		
		sStorageDevice = aStorageDevice;
		sSampleRate = aSampleRate;
		sSendData = aSendData;
		sStopAppBattery = aStopAppBattery;
	}
	
	private void confirmChoice(final String subject){
		new AlertDialog.Builder(SettingsActivity.this)
		.setItems(choices, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				choice = choices[which];
				Intent dIntent = new Intent(SettingsActivity.this, MainService.class);
				Bundle dBundle = new Bundle();
				dBundle.putString(subject, choice);
				dIntent.putExtras(dBundle);
				startService(dIntent);
			}
		}).setTitle("confirm your choice").create().show();
	}

	private BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION)) {
				Log.i(SETTINGS,"i'm active");
				this.setResultCode(Activity.RESULT_OK);
				SettingsActivity.this.finish();
			}
			if(action.equals(SETTINGS)){
				SettingsActivity.this.finish();
				Log.i(SETTINGS, "is active");
			}
		}
	};
	
	private void checkActiveActivity(final String activity) {
		Log.i(SETTINGS, "start checking for active Activity ");
		Intent intent = new Intent();
		intent.setAction(activity);
		sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int result = getResultCode();

				if (result != Activity.RESULT_CANCELED) { // Activity caught it
					Log.i(SETTINGS, "An activity caught the broadcast, result " + result);
					activeActivity(activity);
				}else{
					Log.i(SETTINGS, "No activity did catch the broadcast.");
					noActiveActivity(activity);
				}
			}
		}, null, Activity.RESULT_CANCELED, null, null);
	}
	
	protected void noActiveActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(SETTINGS, "no active activity");
		if(activity.equals(HOME)){
			Intent homeIntent = new Intent(SettingsActivity.this,HomeActivity.class);
	        startActivity(homeIntent);
		}else if(activity.equals(CELL)){
			Intent cellIntent = new Intent(SettingsActivity.this,CellActivity.class);
	        startActivity(cellIntent);
		}else if(activity.equals(DATA)){
			Intent dataIntent = new Intent(SettingsActivity.this,DataActivity.class);
	        startActivity(dataIntent);
		}
	}

	protected void activeActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(SETTINGS, "active activity");
		checkActiveActivity(activity);
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.buttonSettingsHome:
			checkActiveActivity(HOME);
	    break;
		case R.id.imageButtonSettingsNext:
			checkActiveActivity(CELL);
	    break;
		case R.id.imageButtonSettingsPrevious:
			checkActiveActivity(DATA);
	    break;
		case R.id.checkBoxCell:
			if(((CheckBox)v).isChecked()){
				cellCheckBox.setChecked(true);
				sIsCell = true;
			}else{
				cellCheckBox.setChecked(false);
				sIsCell = false;	
			}
			break;
		case R.id.checkBoxGps:
			if(((CheckBox)v).isChecked()){
				gpsCheckBox.setChecked(true);
				sIsGps = true;
			}else{
				gpsCheckBox.setChecked(false);
				sIsGps = false;
			}
			break;
		case R.id.checkBoxWifi:
			if(((CheckBox)v).isChecked()){
				wifiCheckBox.setChecked(true);
				sIsWifi = true;
			}else{
				wifiCheckBox.setChecked(false);
				sIsWifi = false;	
			}
			break;
		case R.id.buttonSettingsDefault:
			cellCheckBox.setChecked(true);
			sIsCell = true ;
			gpsCheckBox.setChecked(true);
			sIsGps = true ;
			wifiCheckBox.setChecked(true);
			sIsSimu = true ;
			sampleRateSpinner.setSelection(0);
			storageSpinner.setSelection(1);
			sendDataSpinner.setSelection(0);
			stopAppBatterySpinner.setSelection(0);
			break;
		case R.id.checkBoxMode:
			if(((CheckBox)v).isChecked()){
				modeCheckBox.setChecked(true);
				sIsSimu = true;
			}else{
				modeCheckBox.setChecked(false);
				sIsSimu = false;	
			}
			break;
		case R.id.buttonSettingsApply:
			applySettings();
			setPreferences();
			Intent sIntent = new Intent(SettingsActivity.this,MainService.class);
			Bundle sBundle = new Bundle();
			sBundle.putBoolean("isSimu", sIsSimu);
			sBundle.putBoolean("isCell", sIsCell);
			sBundle.putBoolean("isGps", sIsGps);
			sBundle.putBoolean("isWifi", sIsWifi);
			sBundle.putInt("sampleRate", sSampleRate);
			sBundle.putString("storageDevice", sStorageDevice);
			sIntent.putExtras(sBundle);
			startService(sIntent);
			
			break;
		case R.id.buttonSettingsCancel:
			getPreferences();
			cellCheckBox.setChecked(sIsCell);
			gpsCheckBox.setChecked(sIsGps);
			wifiCheckBox.setChecked(sIsWifi);
			modeCheckBox.setChecked(sIsSimu);
			
			sampleRateSpinner.setSelection((sSampleRate/5000)-1);
			if(sStorageDevice.equals("MOBILE")){
				storageSpinner.setSelection(0);
			}else{
				storageSpinner.setSelection(1);
			}
			sendDataSpinner.setSelection(sSendData);
			stopAppBatterySpinner.setSelection(sStopAppBattery);
			break;
		case R.id.buttonStopApp:
			confirmChoice("stopService");
			break;
		default:
		break;
		}	
		
	}

}
