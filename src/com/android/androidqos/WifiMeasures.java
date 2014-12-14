/*********************************
 * class to get wifi measurements
 * *******************************/
package com.android.androidqos;

import java.util.List;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class WifiMeasures implements Constants{
	
	private Service wService ;

	StringBuilder strDetailsWifi = new StringBuilder("");
	StringBuilder strWifi = new StringBuilder("");
	StringBuilder strFileWifi = new StringBuilder("");
	
	private String[] bssidArray = {"08:00:07:A9:B2:FC","07:00:08:A5:D7:A9","03:01:08:E4:A9:D9","1F:08:08:E5:D7:8D"};
	private String[] ssidArray = {"Wifi_1","Wifi_2", "Wifi_3", "Wifi_4"};
	private int[] frequencyArray = {1152, 4556, 3548, 2568};
	private int[] levelArray = {4,2,3,1};
	private String[] capacityArray = {"WAP2","WAP1", "WAP3", "WAP4"};
	
	private boolean wIsSimu = false ;
	private Random  random = new Random();
	
	public WifiMeasures(Service service , boolean isSimu){
		this.wService = service ;
		this.wIsSimu = isSimu;
		if(!wIsSimu){
			WifiManager wifiManager = (WifiManager)wService.getSystemService(Context.WIFI_SERVICE);
			wifiManager.startScan();
			if(wifiManager != null){
				if(wifiManager.isWifiEnabled()){
					List<ScanResult> listScan = wifiManager.getScanResults();
					for(ScanResult scanResult : listScan){
						strFileWifi.append(scanResult.SSID).append(",\t ").append(scanResult.BSSID).append(",\t ").append(scanResult.frequency).append(",\t ").append((scanResult.level)).append(",\t ").append(scanResult.capabilities).append("|\t ");
						strDetailsWifi.append("\n").append(scanResult.SSID).append("|").append(scanResult.BSSID).append("|").append(scanResult.frequency).append("|").append((scanResult.level)).append(" dBm").append("|").append(scanResult.capabilities).append("\n");
						strWifi.append("\n").append(scanResult.SSID).append(" | ").append((scanResult.level)).append(" dBm");
					}
				}else{
					// Toast.makeText(wService.getBaseContext(), "Wifi is off, turn it on !", Toast.LENGTH_LONG).show();
					Log.i(WIFI, "Wifi is off, turn it on !");
				}
			}
		}else{
			for(int i=0;i<random.nextInt(4);i++){
					strFileWifi.append(ssidArray[ random.nextInt(4)]).append(", ").append(bssidArray[random.nextInt(4)]).append(", ").append(frequencyArray[random.nextInt(4)]).append(", ").append(levelArray[random.nextInt(4)]).append(", ").append(capacityArray [random.nextInt(4)]).append("| ");					
					strDetailsWifi.append(ssidArray[ random.nextInt(4)]).append("|").append(bssidArray[random.nextInt(4)]).append("|").append(frequencyArray[random.nextInt(4)]).append("|").append(levelArray[random.nextInt(4)]).append("|").append(capacityArray [random.nextInt(4)]).append("\n");
					strWifi.append("\n").append(ssidArray[random.nextInt(4)]).append(" | ").append(levelArray[random.nextInt(4)]);
			}
		}
	}

	public StringBuilder getStrFileWifi() {
		return strFileWifi;
	}

	public StringBuilder getStrDetailsWifi() {
		return strDetailsWifi;
	}

	public StringBuilder getStrWifi() {
		return strWifi;
	}

}
