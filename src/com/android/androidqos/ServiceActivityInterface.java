/******************************************
 * interface to bind service to activities
 * ****************************************/

package com.android.androidqos;

public interface ServiceActivityInterface {
	
	public int getSampleRate();
	
	public int getCID();
	public int getLAC();
	public String getMCC();
	public String getMNC();
	public String getOperatorName();
	public String getIMEI();
	public String getIMSI();
	public String getAndroidNumber();
	public int getSignalStrength();
	public int getBitErrorRate();
	public String getTheNetworkType();
	
	public double getLong();
	public double getLat();
	public double getAlt();
	public double getTheSpeed();
	public String getTheTime();
	public float getTheAccuracy();
	public StringBuilder getStrWifi();
	public StringBuilder getStrDetailsWifi();
	
	public int getNbDataCollected();
	public int getNbDataSent();
	public int getMaxCapacity();
	public long getSizeDataMobile();
	public long getSizeDataSDCard();
	public int getMobileNoLine();
	public int getSDCardNoLine();
	
	public String getTheProvider();
	public boolean getIsSimu();
	public String getStorageDevice();
	
	public int getNbSat();
	public int getfixSat();
	
}
