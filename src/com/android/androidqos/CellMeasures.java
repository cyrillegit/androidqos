/*************************************
 * class to get cell measurements 
 * ***********************************/
package com.android.androidqos;

import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class CellMeasures implements Constants{

	private Service cService;
	
	private TelephonyManager telephonyManager;
	private int networkType ; 
	
	private boolean cIsSimu = false ;
	private Random random = new Random();

	 	String imei ="359881030314356", imsi = "310995000000000", androidNumber = "87974CE4541BD94D5F55";
		int []cidArray = {0x43d4,0x04f2,0x04f1,0x04f0,0xb172,0x10fa,0x10f8};
		int []lacArray = { 0x0060, 0x005e, 0x58e2, 0x103c};
		String mcc = "208";
		String mnc = "15" ;

	public CellMeasures(Service service, boolean isSimu){
		this.cService = service;
		this.cIsSimu = isSimu;
		if(!cIsSimu){
			telephonyManager = (TelephonyManager)cService.getSystemService(Context.TELEPHONY_SERVICE);				
		}
	}
	
	public int getCID(){
		if(!cIsSimu){
				GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
				return (cellLocation.getCid() & 0x0000FFFF);
			}else{
				return cidArray[random.nextInt(7)];
			}
	}
	
	public int getLAC(){
		if(!cIsSimu){
				GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
				return (cellLocation.getLac() & 0x0000FFFF);
			}else{
				return lacArray[random.nextInt(4)];
			}
	}
	
	public String getMCC(){
		if(!cIsSimu){
			TelephonyManager myTelephonyManager = (TelephonyManager)cService.getSystemService(Context.TELEPHONY_SERVICE);
				return myTelephonyManager.getNetworkOperator().substring(0, 3);
			}else{
				return mcc;
			}
	}
	
	public String getMNC(){
		if(!cIsSimu){
			return telephonyManager.getNetworkOperator().substring(3);
		}else{
			return mnc;
		}
	}
	
	public String getOperatorName(){
		if(!cIsSimu){
			return telephonyManager.getNetworkOperatorName();
		}else{
			return "Free";
		}
		
	}
	
	public String getIMEI(){
		if(!cIsSimu){
			return telephonyManager.getDeviceId();
		}else{
			return imei;
		}
			
	}
	
	public String getIMSI(){
		if(!cIsSimu){
			return telephonyManager.getSubscriberId();
		}else{
			return imsi;
		}
			
	}
	
	public String getAndroidNumber(){
		if(!cIsSimu){
			return Secure.getString(cService.getContentResolver(), Secure.ANDROID_ID);
		}else{
			return androidNumber;
		}
	}

	public String getTheNetworkType(){
		String network = "";
			if(!cIsSimu){
				networkType = telephonyManager.getNetworkType();
		
				switch (networkType)
				{
				case 7:
				    network = "1xRTT";
				    break; 	    
				case 4:
					network = "CDMA";
				    break;      
				case 2:
					network = "EDGE";
				    break;  
				case 14:
					network = "eHRPD";
				    break;      
				case 5:
					network = "EVDO rev. 0";
				    break;  
				case 6:
					network = "EVDO rev. A";
				    break;  
				case 12:
					network = "EVDO rev. B";
				    break;  
				case 1:
					network = "GPRS";
				    break;      
				case 8:
					network = "HSDPA";
				    break;      
				case 10:
					network = "HSPA";
				    break;          
				case 15:
					network = "HSPA+";
				    break;          
				case 9:
					network = "HSUPA";
				    break;          
				case 11:
					network = "iDen";
				    break;
				case 13:
					network = "LTE";
				    break;
				case 3:
					network = "UMTS";
				    break;          
				case 0:
					network = "Unknown";
				    break;
				 default:
					 break;
				}	
			}else{
				network = "3G";
			}
			return network;
		
	}
}


