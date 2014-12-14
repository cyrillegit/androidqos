/*******************************************
 *  Broadcastreceiver that enables to start 
 *  the App from the boot of the mobile 
 *  ****************************************/
package com.android.androidqos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.i("OnBootReceiver", "Boot finished");
		
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			Intent myIntent = new Intent(context, Startup.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(myIntent);
		}
		Log.i("OnBootReceiver", "App started");
		
	}

}
