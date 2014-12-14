/**************************************
 * activity to display battery data 
 * ************************************/

package com.android.androidqos;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class BatteryActivity extends Activity implements OnClickListener, Constants{

	private TextView batteryLevel;
	private TextView batteryVoltage;
	private TextView batteryTemperature;
	private TextView batteryTechnology;
	private TextView batteryStatus;
	private TextView batteryHealth;
	private ImageButton btnBatteryNext;
	private ImageButton btnBatteryPrevious;
	private Button btnBatteryHome;
	private IntentFilter bIntentFilter;
	private IntentFilter aIntentFilter;

	public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.battery_layout);
	       
			bIntentFilter = new IntentFilter();
			bIntentFilter.addAction(ACTION);		
			Intent serviceIntent = new Intent(this, MainService.class);
			startService(serviceIntent);
			
			aIntentFilter = new IntentFilter();
			aIntentFilter.addAction(BATTERY);

			btnBatteryNext = (ImageButton)findViewById(R.id.ImageButtonBatteryNext);
			btnBatteryNext.setOnClickListener(this);
			btnBatteryPrevious = (ImageButton)findViewById(R.id.ImageButtonBatteryPrevious);
			btnBatteryPrevious.setOnClickListener(this);
			btnBatteryHome = (Button)findViewById(R.id.buttonBatteryHome);
			btnBatteryHome.setOnClickListener(this);
			
	       batteryLevel = (TextView)findViewById(R.id.textViewLevelValue);
	       batteryVoltage = (TextView)findViewById(R.id.textViewVoltageValue);
	       batteryTemperature = (TextView)findViewById(R.id.textViewTemperatureValue);
	       batteryTechnology = (TextView)findViewById(R.id.textViewTechnologyValue);
	       batteryStatus = (TextView)findViewById(R.id.textViewStatusValue);
	       batteryHealth = (TextView)findViewById(R.id.textViewHealthValue);
	      
	       this.registerReceiver(this.myBatteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.registerReceiver(stopBroadcastReceiver, bIntentFilter);
		this.registerReceiver(this.myBatteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		this.registerReceiver(stopBroadcastReceiver, aIntentFilter);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		this.registerReceiver(this.myBatteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		this.unregisterReceiver(myBatteryReceiver);
	}
	
	

	private BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION)) {
				Log.i("Battery","i'm active");
				this.setResultCode(Activity.RESULT_OK);
				BatteryActivity.this.finish();
			}	
			
			if(action.equals(BATTERY)){
				BatteryActivity.this.finish();
				Log.i(BATTERY, "is active");
			}
		}
	};
	
	private void checkActiveActivity(final String activity) {
		Log.i(BATTERY, "start checking for active Activity ");
		Intent intent = new Intent();
		intent.setAction(activity);
		sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int result = getResultCode();

				if (result != Activity.RESULT_CANCELED) { // Activity caught it
					Log.i(BATTERY, "An activity caught the broadcast, result " + result);
					activeActivity(activity);
				}else{
					Log.i(BATTERY, "No activity did catch the broadcast.");
					noActiveActivity(activity);
				}
			}
		}, null, Activity.RESULT_CANCELED, null, null);
	}
	
	protected void noActiveActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(BATTERY, "no active activity");
		if(activity.equals(HOME)){
			Intent homeIntent = new Intent(BatteryActivity.this,HomeActivity.class);
	        startActivity(homeIntent);
		}else if(activity.equals(WIFI)){
			Intent wifiIntent = new Intent(BatteryActivity.this,WifiActivity.class);
	        startActivity(wifiIntent);
		}else if(activity.equals(DATA)){
			Intent dataIntent = new Intent(BatteryActivity.this,DataActivity.class);
	        startActivity(dataIntent);
		}
	}

	protected void activeActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(BATTERY, "active activity");
		checkActiveActivity(activity);
	}

	

	private BroadcastReceiver myBatteryReceiver = new BroadcastReceiver(){

	 @Override
	 public void onReceive(Context context, Intent intent) {
	  // TODO Auto-generated method stub

	  if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
			   batteryLevel.setText(""+ String.valueOf(intent.getIntExtra("level", 0)) + "%");
			   batteryVoltage.setText("" + String.valueOf((float)intent.getIntExtra("voltage", 0)/1000) + "V");
			   batteryTemperature.setText(""+ String.valueOf((float)intent.getIntExtra("temperature", 0)/10) + "c");
			   batteryTechnology.setText("" + intent.getStringExtra("technology"));
			  
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
			  
			   int health = intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
			   String strHealth;
			   if (health == BatteryManager.BATTERY_HEALTH_GOOD){
				   strHealth = "Good";
			   } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT){
				   strHealth = "Over Heat";
			   } else if (health == BatteryManager.BATTERY_HEALTH_DEAD){
				   strHealth = "Dead";
			   } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE){
				   strHealth = "Over Voltage";
			   } else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE){
				   strHealth = "Unspecified Failure";
			   } else{
				   strHealth = "Unknown";
			   }
			   batteryHealth.setText("" + strHealth);
			  
	  	}
	 }
  };

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.ImageButtonBatteryNext:
			checkActiveActivity(DATA);
	    break;
		case R.id.ImageButtonBatteryPrevious:
			checkActiveActivity(WIFI);
	    break;
		case R.id.buttonBatteryHome:
			checkActiveActivity(HOME);
	    break;
		default:
		break;
		}	
		
	}
}
