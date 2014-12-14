package com.android.androidqos;

public interface Constants {
	
	public static final int NOTIF_STARTUP = 0010;
	public static final int NOTIF_MAIN = 0011;
	public static final int MAX_CAPACITY = 128;
	
	public static final String STARTUP = "Startup";
	public static final String BATTERY = "BatteryActivity";
	public static final String MAIN = "MainService";
	public static final String CELL = "CellActivity";
	public static final String DATA = "DataActivity";
	public static final String HOME = "HomeActivity";
	public static final String LOCATION = "LocationActivity";
	public static final String SETTINGS = "SettingsActivity";
	public static final String WIFI = "WifiActivity";
	
	public static final String ACTION = "ACTIVE";
	public static final String PREFS = "PREFERENCES";
	
	public static final long MIN_TIME = 0;
	public static final float MIN_DISTANCE = 0;
	public static final long DELAY_TIME = 300;
	public static final long UPDATE_TIME = 20000;
	public static final int NODATA = 5;
	public static final int REBOOT_TIME = 10;
	public static final String REBOOT = "IS_REBOOT";

	public static final long DELAY_INTERVAL = 0000;
	public static final int SAMPLE_RATE = 10000;
	
	public static final String MOBILE = "MobileData.txt";
	public static final String SDCARD = "SDCardData.txt";
	public static final String SDCARDALL = "SDCardAllData.txt";


}
