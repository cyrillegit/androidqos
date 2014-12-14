/***************************************
 * activity to display cell measurements 
 * *************************************/

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

public class CellActivity extends Activity implements OnClickListener, Constants{
	
	private TextView cidView, lacView, mccView, mncView, andNumView, imsiView, imeiView; 
	private TextView signalStrengthView;
	private TextView bitErrorRateView;
	private TextView operatorNameView;
	private TextView networkTypeView;
	private ImageButton btnCellNext;
	private ImageButton btnCellPrevious;
	private Button btnCellHome;
	
	private TimerTask timerTask;
	private Timer cellTimer;
	private Handler handler;
	
	private int cSignalStrength;
	private int cBitErrorRate;
	private int cCid;
	private int cLac;
	private String cMcc;
	private String cMnc;
	private String cImsi;
	private String cImei;
	private String cAndroidNumber;
	private String cOperatorName;
	private String cNetworkType;
	private IntentFilter cIntentFilter;
	private IntentFilter aIntentFilter;
	
	private boolean cIsBound = false;
	
	/** get data from the service */
	private ServiceConnection cellServiceConnection = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder cellService) {
			// TODO Auto-generated method stub
			ServiceActivityInterface myBinder = (ServiceActivityInterface)cellService;
			cCid = myBinder.getCID();
			cLac = myBinder.getLAC();
			cMcc = myBinder.getMCC();
			cMnc = myBinder.getMNC();
			cImsi = myBinder.getIMSI();
			cImei = myBinder.getIMEI();
			cSignalStrength = myBinder.getSignalStrength();
			cBitErrorRate = myBinder.getBitErrorRate();
			cAndroidNumber = myBinder.getAndroidNumber();
			cOperatorName = myBinder.getOperatorName();
			cNetworkType = myBinder.getTheNetworkType();
			Log.i(getLocalClassName(), "connected!");
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.i(getLocalClassName(), "disconnected!");
		}
	};
	
	/** bind to the service */
	public void doBindService(){
		if(!cIsBound){
			bindService(new Intent(CellActivity.this, MainService.class), cellServiceConnection, Context.BIND_AUTO_CREATE);
			cIsBound = true ;
		}
	}
	
	/** unbind to the service */
	public void doUnbindService(){
		if(cIsBound){
			unbindService(cellServiceConnection);
			cIsBound = false;
		}
	}
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.cell_layout);
        
		cIntentFilter = new IntentFilter();
		cIntentFilter.addAction(ACTION);		
		Intent serviceIntent = new Intent(this, MainService.class);
		startService(serviceIntent);
		
		aIntentFilter = new IntentFilter();
		aIntentFilter.addAction(CELL);

        setProgressBarIndeterminateVisibility(true);
        doBindService();
		handler = new Handler();
		cellTimer = new Timer();
        
		btnCellNext = (ImageButton)findViewById(R.id.ImageButtonCellNext);
		btnCellNext.setOnClickListener(this);
		btnCellPrevious = (ImageButton)findViewById(R.id.ImageButtonCellPrevious);
		btnCellPrevious.setOnClickListener(this);
		btnCellHome = (Button)findViewById(R.id.buttonCellHome);
		btnCellHome.setOnClickListener(this);
		
        cidView = (TextView)findViewById(R.id.textViewCidValue);
        lacView = (TextView)findViewById(R.id.textViewLacValue);
        mccView = (TextView)findViewById(R.id.textViewMccValue);
        mncView = (TextView)findViewById(R.id.textViewMncValue);
        andNumView = (TextView)findViewById(R.id.textViewAndNumValue);
        imsiView = (TextView)findViewById(R.id.textViewImsiValue);
        imeiView = (TextView)findViewById(R.id.textViewImeiValue);
        signalStrengthView = (TextView)findViewById(R.id.textViewSignalStrengthValue);
        bitErrorRateView = (TextView)findViewById(R.id.textViewBitErrorRateValue);
        operatorNameView = (TextView)findViewById(R.id.textViewOperatorNameValue);
        networkTypeView = (TextView)findViewById(R.id.textViewNetworkTypeValue);
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		timerTask.cancel();
		cellTimer.cancel();
		cellTimer.purge();
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
		this.registerReceiver(stopBroadcastReceiver, cIntentFilter);
		this.registerReceiver(stopBroadcastReceiver, aIntentFilter);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		display();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		doUnbindService();
	}

	private void checkActiveActivity(final String activity) {
		Log.i(CELL, "start checking for active Activity ");
		Intent intent = new Intent();
		intent.setAction(activity);
		sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int result = getResultCode();

				if (result != Activity.RESULT_CANCELED) { // Activity caught it
					Log.i(CELL, "An activity caught the broadcast, result " + result);
					activeActivity(activity);
				}else{
					Log.i(CELL, "No activity did catch the broadcast.");
					noActiveActivity(activity);
				}
			}
		}, null, Activity.RESULT_CANCELED, null, null);
	}
	
	protected void noActiveActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(CELL, "no active activity");
		if(activity.equals(HOME)){
			Intent homeIntent = new Intent(CellActivity.this,HomeActivity.class);
	        startActivity(homeIntent);
		}else if(activity.equals(LOCATION)){
			Intent gpsIntent = new Intent(CellActivity.this,LocationActivity.class);
	        startActivity(gpsIntent);
		}else if(activity.equals(SETTINGS)){
			Intent settingsIntent = new Intent(CellActivity.this,SettingsActivity.class);
	        startActivity(settingsIntent);
		}
	}

	protected void activeActivity(String activity) {
		// TODO Auto-generated method stub
		Log.i(CELL, "active activity");
		checkActiveActivity(activity);
	}

	private BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION)) {
				Log.i(CELL,"i'm active");
				this.setResultCode(Activity.RESULT_OK);
				CellActivity.this.finish();
			}
			if(action.equals(CELL)){
				//BatteryActivity.this.onResume();
				CellActivity.this.finish();
				Log.i(CELL, "is active");
			}
		}
	};	

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.ImageButtonCellNext:
			checkActiveActivity(LOCATION);
	    break;
		case R.id.ImageButtonCellPrevious:
			checkActiveActivity(SETTINGS);
	    break;
		case R.id.buttonCellHome:
			checkActiveActivity(HOME);
	    break;
		default:
		break;
		}	
	}
	
	/** method to display data */
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
	                         
			                        cidView.setText(""+String.valueOf(cCid));
			                        lacView.setText(""+String.valueOf(cLac));
			                        mccView.setText(""+cMcc);
			                        mncView.setText(""+cMnc);
			                        imsiView.setText(""+cImsi);
			                        imeiView.setText(""+cImei);
			                        andNumView.setText(""+cAndroidNumber);
			                        signalStrengthView.setText(""+String.valueOf(cSignalStrength));
			                        bitErrorRateView.setText(""+String.valueOf(cBitErrorRate));
			                        operatorNameView.setText(""+cOperatorName);
			                        networkTypeView.setText(""+cNetworkType);

	                        }
	               });
	        }};
	    cellTimer.schedule(timerTask, DELAY_TIME, UPDATE_TIME); 
	}

}
