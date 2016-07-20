package com.runningmusic.network.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

import com.runningmusic.utils.Log;
  
public class NetworkChangeReceiver extends BroadcastReceiver {  
  
	static long lastChangeTime_ = 0;
    @Override  
    public void onReceive(Context context, Intent intent) {  
        State wifiState = null;  
        State mobileState = null;  
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
        if (wifiState != null && mobileState != null  
                && State.CONNECTED != wifiState  
                && State.CONNECTED == mobileState) 
        {  
        	Log.i("zhouhan", "3G connected");
        	
        } 
        else if (wifiState != null && mobileState != null  
                && State.CONNECTED != wifiState  
                && State.CONNECTED != mobileState) 
        {
        	Log.i("zhouhan", "network disconnected");
        }
        else if (wifiState != null && State.CONNECTED == wifiState) 
        {
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastChangeTime_ > 10000) {
//				BatchDataManager.getInstance().recover();
//				lastChangeTime_ = currentTime;
			}
 }  
  
    }  
  
}  
