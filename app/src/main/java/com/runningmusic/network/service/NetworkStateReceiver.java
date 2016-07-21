package com.runningmusic.network.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;

public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            String msg = null;
            switch (state) {
                case WifiManager.WIFI_STATE_DISABLED:
                    msg = "WIFI is disabled";
                    Util.wifiConnectivity = false;
//                    TrafficStatsManager.getInstance().onWifiDisconnected();
                break;
                case WifiManager.WIFI_STATE_DISABLING:
                    msg = "WIFI is disabling ...";
                    Util.wifiConnectivity = false;
                break;
                case WifiManager.WIFI_STATE_ENABLED:
                    msg = "WIFI is enabled";
                    Util.wifiConnectivity = true;
//                    TrafficStatsManager.getInstance().setNetworkType(ConnectivityManager.TYPE_WIFI);
//                    TrafficStatsManager.getInstance().onWifiConnected();

                break;
                case WifiManager.WIFI_STATE_ENABLING:
                    msg = "WIFI is enabling ...";
                    Util.wifiConnectivity = false;
                break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    msg = "[ERROR] WIFI state is unknown";
                    Util.wifiConnectivity = false;
                break;
                default:
                break;
            }
            if (msg != null) {
                if (Util.DEBUG)
                    Log.i("FM", msg);
            }
        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                String msg = null;
                switch (networkInfo.getState()) {
                    case DISCONNECTED:
                        msg = "Mobile 2G/3G is disabled";
                        Util.wifiConnectivity = false;
                        break;
                    case DISCONNECTING:
                        msg = "Mobile 2G/3G disabling ...";
                        Util.wifiConnectivity = false;
                        break;
                    case CONNECTED:
                        msg = "Mobile 2G/3G is enabled";
                        Util.mobileConnectivity = true;
//                        TrafficStatsManager.getInstance().setNetworkType(ConnectivityManager.TYPE_MOBILE);
                    break;
                    case CONNECTING:
                        msg = "Mobile 2G/3G is enabling ...";
                    break;
                    case SUSPENDED:
                        msg = "Mobile 2G/3G is suspended";
                        Util.wifiConnectivity = false;
                        break;
                    case UNKNOWN:
                        msg = "[ERROR] Mobile state is unknown";
                        Util.wifiConnectivity = false;
                        break;
                    default:
                    break;
                }
                if (msg != null) {
                    if (Util.DEBUG)
                        Log.i("FM", msg);
                }
            }
        }
    }

}
