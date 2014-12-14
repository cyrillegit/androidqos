/*******************************
 * class to get mobile location 
 * *****************************/
package com.android.androidqos;

import java.util.Date;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocationMeasures implements LocationListener{
	
	private static final String tag = "LocationMeasures";
	private Service gService;
	private LocationManager locationManager = null;
	private Location location;
	
	private double longitude;
	private double latitude;
	private double altitude;
	private float speed = 0;
	private float accuracy = 0;
	private String provider = "";
	
	private Random random = new Random();
	protected boolean gIsSimu = false ;
	private double[]longArray = {40.45, 41.76, 42.98, 45.69};
	private double[]latArray = {73.59, 74.98 , 75.56, 78.59};
	private double[]altArray = { 752.31, 856.12 , 965.02, 626.58};
	private Date date;
	
	public LocationMeasures(Service service, boolean isSimu){
		this.gService = service ;
		this.gIsSimu = isSimu;
		
		if(!gIsSimu){
				locationManager = (LocationManager)gService.getSystemService(Context.LOCATION_SERVICE);
					
					 location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					longitude = location.getLongitude();
					latitude = location.getLatitude();
					altitude = location.getAltitude();
					speed = location.getSpeed();
					accuracy = location.getAccuracy();
					 provider = "NETWORK";
					Log.i(tag, "location:  "+location);
				//	Toast.makeText(gService.getBaseContext(), "latitude: "+latitude+"\n location: "+location, Toast.LENGTH_LONG).show();		
		}
	}
	
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	
		updateWithNewLocation(location);
		
		Toast.makeText(gService.getBaseContext(), "location changed ", Toast.LENGTH_LONG).show();
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(gService.getBaseContext(), "GPS disabled", Toast.LENGTH_LONG).show();
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(gService.getBaseContext(), "GPS enabled", Toast.LENGTH_LONG).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Toast.makeText(gService.getBaseContext(), "status changed", Toast.LENGTH_LONG).show();
	}
//*	
	 public void updateWithNewLocation(Location location) {
		 
		 		this.location = location;
//*
	           latitude = location.getLatitude();
	           longitude = location.getLongitude();
	   			altitude = (double)location.getAltitude();
	   			accuracy = location.getAccuracy();
	   			speed = location.getSpeed();
	   			
	   			Log.i(tag, "location:  "+location);
	   			Toast.makeText(gService.getBaseContext(), "longitude: "+longitude, Toast.LENGTH_LONG).show();
	    }
	
//*/	
	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setLong(double longitude) {
		this.longitude = longitude;
	}
	
	public void setLat(double latitude) {
		this.latitude = latitude;
	}

	public double getLong(){
		if(!gIsSimu){
		Log.i("gpsLong", "long: "+longitude);
			return longitude;
		}else{
			return longArray[random.nextInt(4)];
		}
	}  
	
	public double getLat(){
		if(!gIsSimu){
			return latitude;
		}else{
			return latArray[random.nextInt(4)];
		}
	}
	
	public double getAlt(){
		if(!gIsSimu){
			return altitude;
		}else{
			return altArray[random.nextInt(4)];
		}
	}
	
	public double getTheSpeed(){
		if(!gIsSimu){
			return speed;
		}else{
			return 27;
		}
	}
	
	public String getTheTime(){
		 date = new Date();
		return date.toString(); 
	}

	public float getTheAccuracy() {
		if(!gIsSimu){
			return accuracy;
		}else{
			return (float) 452.1;
		}
	}
	public String getTheProvider(){
		if(!gIsSimu){
			return provider;
		}else{
			return "SIMU";
		}
	}
}
