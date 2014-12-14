/****************************************
 * Service that collects Cell, Gps and 
 * wifi measurements
 * **************************************/

package com.android.androidqos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class MainService extends Service implements Constants{
	
	private CellMeasures cellMeasures ;
	private WifiMeasures wifiMeasures;
	private NotificationManager notificationManager ;
	private LocationManager locationManager;
	private WifiReceiver wifiReceiver;
	
	private Timer mTimer=new Timer();  
	private final IBinder mBinder = new ServiceBinder();
	private MyAsyncTask mAsyncTask;
	private TimerTask mTimerTask;
	
	private double mLatitude;
	private double mLongitude;
	private double mAltitude;
	private double mSpeed;
	private String mTime;
	private float mAccuracy;
	private String mProvider;
	
    private int mCid ;
	private int mLac;
	private int mBitErrorRate = 0;
	private int mSignalStrength = 0;
	private String mAndroidNumber;
	private String mImsi;
	private String mImei;
	private String mOperatorName;
	private String mMnc;
	private String mMcc;
	
	private int mNbDataCollected = 0;
	private int mNbDataSent = 0;

	private static boolean mIsSimu = true;
	private static boolean mIsCell = true;
	private static boolean mIsLocation = true;
	private static boolean mIsWifi = true;
	private static int mSampleRate = SAMPLE_RATE;
	private static String mStorageDevice = "SDCARD";
	private StringBuilder mStrFileWifi;
	private StringBuilder mStrWifi;
	private StringBuilder mStrDetailsWifi;
	private String mNetworkType = "";
	private long mDataMobile = 0;
	private long mFileSizeSDCard = 0;
	private int mNoLine = 0;
	private int noData = 0;
	private String dataToStore = "";
	
	private int sdNoLine = 0;
	private int mNbSat;
	private int mFixSat;
	private int rebootTime = 0;
	
	private String mLocationTime;

	private GpsStatus.Listener mGpsStatusListener;
	private LocationListener mGpsLocationListener;
	public boolean mIsLocationAvailable = false;

	private TelephonyManager telephonyManager;
	private PhoneStateListener mSignalListener;
	private boolean mIsGpsEnabled = false;
	private IntentFilter mIntentFilter;
	private Intent dialogIntent;
	
	/** binder that enables activities to get data from the service */
	public class ServiceBinder extends Binder implements ServiceActivityInterface{
		
		public int getSampleRate() {
			// TODO Auto-generated method stub
			return mSampleRate;
		}

		public int getCID() {
			// TODO Auto-generated method stub
			return mCid;
		}

		public int getLAC() {
			// TODO Auto-generated method stub
			return mLac;
		}

		public double getLong() {
			// TODO Auto-generated method stub
			return mLongitude;
		}

		public double getLat() {
			// TODO Auto-generated method stub
			return mLatitude;
		}

		public String getMCC() {
			// TODO Auto-generated method stub
			return mMcc;
		}

		public String getMNC() {
			// TODO Auto-generated method stub
			return mMnc;
		}

		public String getOperatorName() {
			// TODO Auto-generated method stub
			return mOperatorName;
		}

		public String getIMEI() {
			// TODO Auto-generated method stub
			return mImei;
		}

		public String getIMSI() {
			// TODO Auto-generated method stub
			return mImsi;
		}

		public String getAndroidNumber() {
			// TODO Auto-generated method stub
			return mAndroidNumber;
		}

		public int getSignalStrength() {
			// TODO Auto-generated method stub
			return mSignalStrength;
		}

		public int getBitErrorRate() {
			// TODO Auto-generated method stub
			return mBitErrorRate;
		}

		public double getAlt() {
			// TODO Auto-generated method stub
			return mAltitude;
		}

		public int getNbDataCollected() {
			// TODO Auto-generated method stub
			return mNbDataCollected;
		}

		public int getNbDataSent() {
			// TODO Auto-generated method stub
			return mNbDataSent;
		}

		public int getMaxCapacity() {
			// TODO Auto-generated method stub
			return MAX_CAPACITY;
		}

		public double getTheSpeed() {
			// TODO Auto-generated method stub
			return mSpeed;
		}

		public String getTheTime() {
			// TODO Auto-generated method stub
			return mTime;
		}

		public boolean getIsSimu() {
			// TODO Auto-generated method stub
			return mIsSimu;
		}

		public String getStorageDevice() {
			// TODO Auto-generated method stub
			return mStorageDevice;
		}

		public StringBuilder getStrWifi() {
			// TODO Auto-generated method stub
			return mStrWifi;
		}

		public StringBuilder getStrDetailsWifi() {
			// TODO Auto-generated method stub
			return mStrDetailsWifi;
		}

		public float getTheAccuracy() {
			// TODO Auto-generated method stub
			return mAccuracy;
		}

		public String getTheNetworkType() {
			// TODO Auto-generated method stub
			return mNetworkType ;
		}

		public String getTheProvider() {
			// TODO Auto-generated method stub
			return mProvider;
		}

		public long getSizeDataMobile() {
			// TODO Auto-generated method stub
			return mDataMobile;
		}

		public long getSizeDataSDCard() {
			// TODO Auto-generated method stub
			return mFileSizeSDCard;
		}

		public int getMobileNoLine() {
			// TODO Auto-generated method stub
			return mNoLine;
		}

		public int getSDCardNoLine() {
			// TODO Auto-generated method stub
			return sdNoLine;
		}

		public int getNbSat() {
			// TODO Auto-generated method stub
			return mNbSat;
		}

		public int getfixSat() {
			// TODO Auto-generated method stub
			return mFixSat;
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub				
		return mBinder;
	}
	
	public void onCreate(){
		super.onCreate();
		
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(ACTION);

		telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

		if (notificationManager!=null){
			notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true){
				Log.i(MAIN, "gps status ");
				mIsGpsEnabled  = true;
				gpsStatusListener();
				gpsLocationListener();

			}else{
				Toast.makeText(getBaseContext(), "GPS disabled!", Toast.LENGTH_LONG).show();
			}
			
			mSignalListener = new PhoneStateListener(){

				@Override
				public void onSignalStrengthsChanged(SignalStrength signalStrength) {
					// TODO Auto-generated method stub
					super.onSignalStrengthsChanged(signalStrength);
					getSignalStrengths(signalStrength);
					Log.i(MAIN, "signal changed");
				}
				
			};
			
        /** activate Wifi getScanResults receiver */               
        wifiReceiver = new WifiReceiver();
        registerWifiReceiver();

		cellMeasures = new CellMeasures(MainService.this, mIsSimu);
		wifiMeasures = new WifiMeasures(MainService.this, mIsSimu);
		
		// getMainPreferences();
		
		mAsyncTask = new MyAsyncTask();
		mAsyncTask.execute();	

		enableGpsListeners();
		enableSignalListeners();
	}

	class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
        	wifiMeasures = new WifiMeasures(MainService.this, mIsSimu);
        }
    }
	
	private void registerWifiReceiver(){
		this.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		setPreferences();
		try{
			mTimer.cancel();
			mTimer.purge();
			mTimerTask.cancel();
			mAsyncTask.cancel(true);
		}catch(Throwable t){
			Log.i(MAIN, "cancel failed!");
			t.printStackTrace();
		}
		try{
			// cancelNotification();
			disableGpsListeners();
			disableSignalListeners();
	        this.unregisterReceiver(wifiReceiver);
			this.unregisterReceiver(stopBroadcastReceiver);
		}catch(Throwable t){
			Log.i(MAIN, "cancel listeners failed!");
			t.printStackTrace();
		}
        try{
        	this.stopSelf();
        }catch(Throwable t){
        	Log.i(MAIN, "stop service failed!");
        	t.printStackTrace();
        }
	}
	/********************************************************
	 * method to get parameters sent from others activities 
	 * ******************************************************/
	@Override
	
	public int onStartCommand(Intent sIntent, int flags, int startId) {
		// TODO Auto-generated method stub	
		//enableGpsListeners();
		//enableSignalListeners();
		this.registerReceiver(stopBroadcastReceiver, mIntentFilter);
			getMainPreferences();
		try{
			if(sIntent.hasExtra("isSimu")){
						Bundle bundle = sIntent.getExtras();
								mIsSimu = (boolean)bundle.getBoolean("isSimu", mIsSimu);
								mIsCell = (boolean)bundle.getBoolean("isCell", mIsCell);
								mIsLocation = (boolean)bundle.getBoolean("isGps", mIsLocation);
								mIsWifi = (boolean)bundle.getBoolean("isWifi", mIsWifi);
								mSampleRate  = bundle.getInt("sampleRate", SAMPLE_RATE);
								mStorageDevice  = bundle.getString("storageDevice");

					Log.i(MAIN, "mIsLocationEnabled: "+mIsLocationAvailable);			
			}else if(sIntent.hasExtra("dataCollected")){
				Bundle bundle = sIntent.getExtras();
				String answer = bundle.getString("dataCollected");
				if(answer.equals("Yes")){
					mNbDataCollected = 0;
				}
			}else if(sIntent.hasExtra("dataSent")){
				Bundle bundle = sIntent.getExtras();
				String answer = bundle.getString("dataSent");
				if(answer.equals("Yes")){
					mNbDataSent = 0;
				}
			}else if(sIntent.hasExtra("dataInMobile")){
				Bundle bundle = sIntent.getExtras();
				String answer = bundle.getString("dataInMobile");
				if(answer.equals("Yes")){
					deleteMobileFile();
				}
			}else if(sIntent.hasExtra("dataInSDCard")){
				Bundle bundle = sIntent.getExtras();
				String answer = bundle.getString("dataInSDCard");
				if(answer.equals("Yes")){
					deleteSDCardFile(SDCARD);
				}
			}else if(sIntent.hasExtra("clearAll")){
				Bundle bundle = sIntent.getExtras();
				String answer = bundle.getString("clearAll");
				if(answer.equals("Yes")){
					mNbDataCollected = 0;
					mNbDataSent = 0;
					sdNoLine = 0;
					mNoLine = 0;
					deleteMobileFile();	
					deleteSDCardFile(SDCARD);					
				}
			}else if(sIntent.hasExtra("stopService")){
				Bundle bundle = sIntent.getExtras();
				String answer = bundle.getString("stopService");
				if(answer.equals("Yes")){
					checkActiveActivity();
				}
			}else if(sIntent.hasExtra(REBOOT)){
				Bundle bundle = sIntent.getExtras();
				boolean answer = bundle.getBoolean(REBOOT, false);
				if(!answer){
					reboot(HOME);
				}
			}
		}catch(Throwable t){
			Toast.makeText(getBaseContext(), "Error!", Toast.LENGTH_LONG).show();
			t.printStackTrace();
		}
		return Service.START_STICKY;
	}
	
	private void setPreferences(){
		SharedPreferences homePrefs = getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = homePrefs.edit();
		editor.putInt("mobileNoLine", mNoLine);
		editor.putInt("sdcardNoLine", sdNoLine);
		editor.putInt("nbDataSent", mNbDataSent);
		editor.commit();
	}
	
	private void getMainPreferences(){
		SharedPreferences mainServicePrefs = getSharedPreferences(PREFS, Activity.MODE_PRIVATE);

		mNbDataCollected = mainServicePrefs.getInt("nbDataCollected", mNbDataCollected);
		mNbDataSent = mainServicePrefs.getInt("nbDataSent", mNbDataSent);
		mSampleRate = mainServicePrefs.getInt("sampleRate", mSampleRate);
		mNoLine = mainServicePrefs.getInt("mobileNoLine", mNoLine);
		sdNoLine = mainServicePrefs.getInt("sdcardNoLine", sdNoLine);
		mIsSimu = mainServicePrefs.getBoolean("isSimu", mIsSimu);
		mIsCell = mainServicePrefs.getBoolean("isCell", mIsCell);
		mIsLocation = mainServicePrefs.getBoolean("isGps", mIsLocation);
		mIsWifi = mainServicePrefs.getBoolean("isWifi", mIsWifi);
		mStorageDevice = mainServicePrefs.getString("storageDevice", mStorageDevice);
	}

//*	
	private void showNotification(int rebootTime){
		
		notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
      //  String titleNotif = " Reboot ";
      //  String textNotif = "New Task ... "; 
    	Notification notification = new Notification(R.drawable.icon, "reboot in "+String.valueOf((REBOOT_TIME - rebootTime)*mSampleRate/1000)+" s", System.currentTimeMillis());  
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(MainService.this, MainService.class), 0);
        notification.setLatestEventInfo(this, "", "", pendingIntent);
      //  notification.vibrate = new long[] {0,200,100,200,100,200};
        notificationManager.notify(NOTIF_MAIN, notification);
 
	}
//*/
//*
	private void cancelNotification(int NOTIF){
		
    	notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    	notificationManager.cancel(NOTIF);
	}
//*/	
	private void enableGpsListeners(){
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true){
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mGpsLocationListener);
			locationManager.addGpsStatusListener(mGpsStatusListener);
		}
	}
	
	private void disableGpsListeners(){
		mIsLocationAvailable = false;
		mIsGpsEnabled = false;
		if(mGpsStatusListener != null){
			locationManager.removeGpsStatusListener(mGpsStatusListener);
		}
		
		if(mGpsLocationListener != null){
			locationManager.removeUpdates(mGpsLocationListener);
		}
	}
	
	private void enableSignalListeners(){
		telephonyManager.listen(mSignalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
	
	private void disableSignalListeners(){
		telephonyManager.listen(mSignalListener, PhoneStateListener.LISTEN_NONE);
	}
	
	private void getSignalStrengths(SignalStrength signalStrength) {
		// TODO Auto-generated method stub
		mSignalStrength = signalStrength.getGsmSignalStrength();
		mBitErrorRate  = signalStrength.getGsmBitErrorRate();
		// Log.i(tag, "SigStr: "+mSignalStrength);
	}
	

	private void checkActiveActivity() {
		Log.i(MAIN, "start checking for active Activity ");
		Intent intent = new Intent();
		intent.setAction(ACTION);
		sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int result = getResultCode();

				if (result != Activity.RESULT_CANCELED) { // Activity caught it
					Log.i(MAIN, "An activity caught the broadcast, result " + result);
					activeActivity();
				}else{
					Log.i(MAIN, "No activity did catch the broadcast.");
					noActiveActivity();
				}
			}
		}, null, Activity.RESULT_CANCELED, null, null);
	}
	
	protected void noActiveActivity() {
		// TODO Auto-generated method stub
		Log.i(MAIN, "no active activity"); 
		cancelNotification(Startup.NOTIF_STARTUP);
		 MainService.this.onDestroy();      
	}

	protected void activeActivity() {
		// TODO Auto-generated method stub
		Log.i(MAIN, "active activity");
		checkActiveActivity();
	}
	
	private BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION)) {
				Log.i("MainService","i'm active");
				this.setResultCode(Activity.RESULT_OK);
				noActiveActivity();
			}	
		}
	};
	
	private void reboot(String activity){

		if(!(activity.equals(null))){
			if(activity.equals(STARTUP)){
					dialogIntent = new Intent(getBaseContext(), Startup.class);
			}else if(activity.equals(HOME)){
				dialogIntent = new Intent(getBaseContext(), HomeActivity.class);		
			}
			Bundle mBundle = new Bundle();
			mBundle.putBoolean(REBOOT, true);
			dialogIntent.putExtras(mBundle);
			dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(dialogIntent);
			Log.i(MAIN, "service reboot");
		}
		
	}

	/** **********************************
	 * method to collect Radio cell data 
	 * ***********************************/
	private void getCellMeasures(){
		 if(mIsCell){
			try{
				cellMeasures = new CellMeasures(MainService.this, mIsSimu);
			 
				 mCid = cellMeasures.getCID();
				 mLac = cellMeasures.getLAC();
				 mMnc = cellMeasures.getMNC();
				 mMcc = cellMeasures.getMCC();
				 mImsi = cellMeasures.getIMSI();
				 mImei = cellMeasures.getIMEI();
				 mAndroidNumber = cellMeasures.getAndroidNumber();
				 mOperatorName = cellMeasures.getOperatorName();
				 mNetworkType = cellMeasures.getTheNetworkType();
				mTime = new Date().toString();	

			}catch(Throwable t){
				t.printStackTrace();
			}
		 }
	 }
	 
	/** ********************************
	 * method to collect Location data 
	 * *********************************/
	 private void getLocationMeasures(Location mLocation){

			/** method to collect gps measurements */
			if(mIsLocation){
							try{
							// TODO Auto-generated method stub
								mAccuracy = mLocation.getAccuracy();								
									mIsLocationAvailable = true;
									mLongitude = mLocation.getLongitude();
									mLatitude = mLocation.getLatitude();
									mAltitude = mLocation.getAltitude();
									mSpeed = mLocation.getSpeed();								
									mLocationTime = getGpsTime(mLocation.getTime());
									mProvider = mLocation.getProvider();
							}catch(Throwable t){
								t.printStackTrace();
							}
			}
	 }
	 
	 /*******************************
	  * method to collect Wifi data 
	  * *****************************/
	 private void getWifiMeasures(){
		 
			/** method to collect Wifi measurements */
			if(mIsWifi){
					   try{
							// TODO Auto-generated method stub
							wifiMeasures = new WifiMeasures(MainService.this, mIsSimu);

							mStrFileWifi = wifiMeasures.getStrFileWifi();
							mStrWifi = wifiMeasures.getStrWifi();
							mStrDetailsWifi = wifiMeasures.getStrDetailsWifi();
						}catch(Throwable t){
							t.printStackTrace();
						}
				}	
	 }
	 
     // converts gps time into String
     private String getGpsTime(long gpsTime) {
             Time mygpstime = new Time();
             mygpstime.set(gpsTime);
             return mygpstime.format("%Y%m%d%H%M%S");
     }
     
     private void gpsStatusListener(){
    	 
			mGpsStatusListener = new GpsStatus.Listener() {
				
				public void onGpsStatusChanged(int event) {
					// TODO Auto-generated method stub
					if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
						GpsStatus gpsStatus = locationManager.getGpsStatus(null);
						Iterable<GpsSatellite> gpsSat = gpsStatus.getSatellites();
						Iterator<GpsSatellite> it = gpsSat.iterator();
						int nbSat = 0;
						int fixSat = 0;
						while(it.hasNext()){
							 nbSat++;// number of satellites
							GpsSatellite gpsSatellite = (GpsSatellite) it.next();
							if(gpsSatellite.usedInFix()){
								fixSat++; // number of satellites used to compute the recent position
							}							
						}
						if(fixSat<3){
							mIsLocationAvailable = false;
						}
						setmNbSat(nbSat);
						setmFixSat(fixSat);
					}	
				}
			};
     }
     
     private void gpsLocationListener(){
			
			mGpsLocationListener = new LocationListener(){

				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub
					getLocationMeasures(location);
					// Log.i(tag, "location changed");
					// Toast.makeText(getBaseContext(), "location has changed!", Toast.LENGTH_LONG).show();
		
				}

				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					mIsGpsEnabled = false;
					mIsLocationAvailable = false;
					Toast.makeText(getBaseContext(), "GPS is disabled!", Toast.LENGTH_SHORT).show();
					// disableGpsListeners();
				}

				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					mIsGpsEnabled = true;
					Toast.makeText(getBaseContext(), "GPS is enabled!", Toast.LENGTH_SHORT).show();
					locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
					gpsStatusListener();
					gpsLocationListener();
					enableGpsListeners();
				}

				public void onStatusChanged(String provider, int status, Bundle extras) {
					// TODO Auto-generated method stub
					Toast.makeText(getBaseContext(), "GPS status changed!", Toast.LENGTH_LONG).show();
		            switch (status) {
		            case LocationProvider.AVAILABLE:
		            	Toast.makeText(getBaseContext(), "GPS available again!", Toast.LENGTH_SHORT).show();
		            	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		            	mIsGpsEnabled = true;
		            	gpsStatusListener();
		            	gpsLocationListener();
		            	enableGpsListeners();
		                break;
		            case LocationProvider.OUT_OF_SERVICE:
		            	Toast.makeText(getBaseContext(), "GPS out of service!", Toast.LENGTH_SHORT).show();
		            	 disableGpsListeners();
		            	 mIsLocationAvailable = false;
		            	 mIsGpsEnabled = false;
		                break;
		            case LocationProvider.TEMPORARILY_UNAVAILABLE:
		            	Toast.makeText(getBaseContext(), "GPS temporarily unavailable!", Toast.LENGTH_SHORT).show();
		            	mIsLocationAvailable = false;
		            	mIsGpsEnabled = false;
		                break;
		            }
				}};
     }
 
	 private String getLabel(){
		 
	String label =  "**********************************************************************************************************************\n" +
					"* IMSI: "+mImsi+"  IMEI: "+mImei+"  Android Number: "+mAndroidNumber+"  Operator Name: "+mOperatorName+"\n"+
					"**********************************************************************************************************************\n"+
					"Time\t LocationTime\t CID\t LAC\t MCC\t MNC\t SignalStrenth(dBm)\t BitErrorRate\t NetworkType\t  Latitude\t Longitude\t Altitude(m)\t Speed(m/s)\t Accuracy(m)\t Provider\t" +
					" SSID\t BSSID\t Frequency(MHz)\t Level(dBm)\t Capacity | SSID, ...\n\n";
	return label;
	 }
	 
	 private String getDataToStore(){
		 
		 String data  = ""+mTime+"\t "+mLocationTime+"\t "+mCid+"\t "+mLac+"\t "+mMcc+"\t "+mMnc+"\t "+mSignalStrength+"\t "+mBitErrorRate+"\t "+mNetworkType+"\t "
				 +mLatitude+"\t "+mLongitude+"\t "+mAltitude+"\t "+mSpeed+"\t "+mAccuracy+"\t "+mProvider+"\t "+mStrFileWifi.toString()+"\n";
		return data;		 
	 }
	 
	
		/**********************************************
		 * method to store data in Mobile memory
		 * ********************************************/
	 private void storeInMobileMemory(){
		 if(noData<NODATA){
			 dataToStore = dataToStore+getDataToStore();
			 noData++;
		 }else{
			 		 dataToStore = dataToStore+getDataToStore();
					 noData = 0;
				 File mobileFile = new File(getFilesDir()+File.separator+MOBILE);
					try{
						FileOutputStream fOut = openFileOutput(MOBILE, Context.MODE_APPEND);
					    OutputStreamWriter osw = new OutputStreamWriter(fOut);  
					    if((mobileFile.length()==0)){
						 getCellMeasures();
					    	osw.write(getLabel());
					    }
					    osw.write(dataToStore);
					    osw.flush();
					    osw.close(); 
					    mDataMobile = mobileFile.length();
					    if(mobileFile.length()>=MAX_CAPACITY*1024){
					    	mDataMobile = (int) mobileFile.length();
					    	mobileFile.delete();  	
					    }
					    dataToStore = "";
					}catch(Throwable t){
						t.printStackTrace();
					}
		 }
	 }
		/**********************************************
		 * method to store data in SD Card
		 * ********************************************/
	 private void storeInSDCardMemory(String file){
		 if(noData<NODATA){
			 dataToStore = dataToStore+getDataToStore();
			 noData++;
		 }else{
			 		 dataToStore = dataToStore+getDataToStore();
					 noData = 0;
				 try {
					 File sdFile = new File(Environment.getExternalStorageDirectory(),file);
					 sdFile.createNewFile();
					 FileOutputStream fOut = new FileOutputStream(sdFile,true);
					 OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
					 if(sdFile.length()==0){
						 getCellMeasures();
						 myOutWriter.write(getLabel());
					 }
					 myOutWriter.write(dataToStore);
					 myOutWriter.close();
					 fOut.close();
					// mIsToRead = true;
					// mDataSDCard = sdFile.length();
					// if(sdFile.length()>=MAX_CAPACITY*1024){
						// mDataSDCard = (int) sdFile.length();
						// sdFile.delete();
					// }
					 storeAllInSDCardMemory(dataToStore);
					 dataToStore = "";
				}catch (Exception e) {
						e.printStackTrace();
				}
		 }
	 }

	 /**********************************************
	  * read a line from a file in mobile memory
	  **********************************************/
/*	 
	 private String readLineFromMobileMemory(){
		 String mData = "";
		 try{
			 InputStream in = openFileInput(MOBILE);
			 if(in!=null){
				 BufferedReader mReader = new BufferedReader(new InputStreamReader(in));
				 for(int i=0; i<mNoLine ; i++){
					 mReader.readLine();
				 }
				 if(((mData = mReader.readLine()) != null)){
					 mNoLine++;
				 }else if(mReader.readLine() == null){
					mNoLine = 0;
					deleteMobileFile();					
				 }
				 mReader.close();
			 }
		 }catch(Throwable t){
			 t.printStackTrace();
			 Log.i("mread", "file not found!");
		 }
		 return mData;
	 }
//*/	 
	 /**************************************
	  * read a line from a file in SDCard 
	  **************************************/
/*	 
	 private String readLineFromSDCard(){
		 String mData = "";
		 try{
			 File inSDFile = new File(Environment.getExternalStorageDirectory(),SDCARD);
			 FileInputStream fIn = new FileInputStream(inSDFile);
			 BufferedReader sdReader = new BufferedReader(new InputStreamReader(fIn));
			 for(int i=0; i<sdNoLine ; i++){
				 sdReader.readLine();
			 }
			 if((mData = sdReader.readLine()) != null){
				 sdNoLine++;
			 }else if(sdReader.readLine() == null){
				 sdNoLine = 0;
				 deleteSDFile(SDCARD);
			 }
			 sdReader.close();
		 }catch(Exception e){
			 e.printStackTrace();
			 Log.i("mread", "file not found!");
			 mIsToRead = false;
		 }
		return mData;		 
	 }
//*/	
	 /**********************************************************
	  * send data read from a device storage to the webService
	  * ********************************************************/
/*	 
	 private void sendDataToWebService(){
		 
		 switch(DEVICE){
		 case 0:
			 if(mNoData<NODATA){
				 Log.i("sdread", "sdline: "+sdNoLine +" ; data line: "+sdLine);
			 	 sdLine = sdLine+readLineFromSDCard()+"\n";
				 mNoData ++;
			 }else{
				 	sdLine = sdLine+readLineFromSDCard();
				 	mNoData = 0;
				 	if(sdLine.equals(null)){
				 		mIsToRead = false;
				 	}
					readStore(sdLine);
					sdLine = "";
			 }
			 break;
		 case 1:
			 	String mLine = readLineFromMobileMemory();
				Log.i("mread", "mline: "+mNoLine+" ; data line: "+mLine);
			 break;
		 default:
			break;
		 }
	 }
//*/
//*	 
	 private void storeAllInSDCardMemory(String dataToStore){
		 try {
			 File sdFile = new File(Environment.getExternalStorageDirectory(),SDCARDALL);
			 sdFile.createNewFile();
			 FileOutputStream fOut = new FileOutputStream(sdFile,true);
			 OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			 myOutWriter.write(dataToStore);
			 myOutWriter.close();
			 fOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			 }
	 }
//*/	 
	 private void deleteSDCardFile(String file){
		 File sdFile = new File(Environment.getExternalStorageDirectory(),file);
			sdFile.delete(); 
	 }
	 
	 private void deleteMobileFile(){
			File mobileFile = new File(getFilesDir()+File.separator+MOBILE);
			mobileFile.delete(); 
	 }
	 
	 private long getFileSize(String file){
		 File sdFile = new File(Environment.getExternalStorageDirectory(),file);
		return sdFile.length();
		 
	 }

		/**********************************************
		 * Class to collect data in background thread
		 * ********************************************/
		private class MyAsyncTask extends AsyncTask<Void, Integer, Void>{

			@Override
			protected void onCancelled() {
				// TODO Auto-generated method stub
				super.onCancelled();
				Log.i(MAIN, "cancelled");
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				Log.i(MAIN, "onPost");
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				if(mIsGpsEnabled && ((REBOOT_TIME - rebootTime) <= 3)){				
						showNotification(rebootTime);
						cancelNotification(NOTIF_MAIN);
				}
			}

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				mTimerTask = new TimerTask(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						getCellMeasures();
						getWifiMeasures();
						Log.i(MAIN, "isGpsEnabled: "+mIsGpsEnabled+" Location available: "+mIsLocationAvailable+" isSimu: "+mIsSimu);
						
						if(!mIsSimu && mIsGpsEnabled && !mIsLocationAvailable){
							rebootTime++;
							onPreExecute();
							Log.i(MAIN, "rebootTime: "+rebootTime);	
							if(mIsGpsEnabled && (rebootTime >= REBOOT_TIME )){
								Log.i(MAIN, "reboot");
								rebootTime = 0;
								reboot("Startup");
							}
						}
								if(mIsLocationAvailable || mIsSimu){
									rebootTime = 0;
									Log.i(MAIN, "Location available: "+mIsLocationAvailable+" isSimu: "+mIsSimu);
					                   mNbDataCollected ++;
										if((mNbDataCollected%5)==0){
											mNbDataSent++;											
										}

										if(mStorageDevice.equals("MOBILE")){						
											storeInMobileMemory();
										}else{
											storeInSDCardMemory(SDCARD);
											mFileSizeSDCard = getFileSize(SDCARD);
											if(mFileSizeSDCard > MAX_CAPACITY*1024){
												deleteSDCardFile(SDCARD);
											}
										}
						}else{
							Log.i(MAIN, "Location unavailable: "+mIsLocationAvailable);
							}
						// Log.i(tag, "isToRead: "+mIsToRead);
						//if(mIsToRead){
						//	sendDataToWebService();
						//}
						publishProgress();
					}};
					mTimer.scheduleAtFixedRate(mTimerTask, DELAY_INTERVAL, mSampleRate);
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}
		}
		public int getmNbSat() {
			return mNbSat;
		}

		public void setmNbSat(int mNbSat) {
			this.mNbSat = mNbSat;
		}

		public int getmFixSat() {
			return mFixSat;
		}

		public void setmFixSat(int mFixSat) {
			this.mFixSat = mFixSat;
		}	
}
