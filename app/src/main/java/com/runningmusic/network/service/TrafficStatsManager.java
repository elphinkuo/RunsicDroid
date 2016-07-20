package com.runningmusic.network.service;

import com.runningmusic.application.WearelfApplication;
import com.runningmusic.db.TrafficDB;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusiclib.cppwrapper.utils.Date;

import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Process;

public class TrafficStatsManager {
    public static final String TAG = "TrafficStatsManager";
    public static String TRAFFIC_DAILYSTATS_ACTION = "cn.ledongli.ldl.broadcast.traffic";
    enum WifiState {unknown, connected, disconnected}

    enum TrafficType {mobileForeground, mobileBackground, wifiForeground, wifiBackground}

    final static int TYPE_NONE = -1;
    private WifiState lastWifiState_ = WifiState.unknown;
    private static int lastNetworkType_;
    private static long txBytes_;
    private static long rxBytes_;
    private static long lastTxBytes_;
    private static long lastRxBytes_;
    private static com.runningmusic.network.service.TrafficStats trafficStats_;
    private static TrafficDB trafficDB_;
    private static TrafficStatsManager instance_;

    private TrafficStatsManager() {

        if (NetStatus.isNetworkEnable()) {
            if (NetStatus.isWifiEnabled() && NetStatus.isWifi()) {
                lastNetworkType_ = ConnectivityManager.TYPE_WIFI;
                lastWifiState_ = WifiState.connected;
            } else if (NetStatus.is2G() || NetStatus.is3G()) {
                lastNetworkType_ = ConnectivityManager.TYPE_MOBILE;
                lastWifiState_ = WifiState.disconnected;
            }
        } else {
            lastNetworkType_ = TYPE_NONE;
            lastWifiState_ = WifiState.disconnected;
        }

        trafficDB_ = new TrafficDB();
        trafficStats_ = trafficDB_.queryTrafficStats(Date.now());

        int uid = Process.myUid();
        txBytes_ = TrafficStats.getUidTxBytes(uid);
        rxBytes_ = TrafficStats.getUidRxBytes(uid);
        lastTxBytes_ = txBytes_;
        lastRxBytes_ = rxBytes_;
    }

    public synchronized static TrafficStatsManager getInstance() {
        if (instance_ == null) {
            instance_ = new TrafficStatsManager();
        }
        return instance_;
    }

    public void setNetworkType(int type) {
        lastNetworkType_ = type;
    }

    private void prepare() {
        int uid = Process.myUid();
        rxBytes_ = TrafficStats.getUidRxBytes(uid);
        txBytes_ = TrafficStats.getUidTxBytes(uid);
        //        Log.i(TAG, "uid = " + uid + ", rxBytes = " + rxBytes_ + ", txBytes = " + txBytes_);
    }

    private void wifiStats() {
        if (lastWifiState_ == WifiState.disconnected) {
            return;
        }
        prepare();
        if (WearelfApplication.isBackground()) {
            Log.i(TAG, "wifi disconnected, background " + Util.dateFormat(Date.now(), null));
            trafficStats_.wifiBackgroundBytes += (rxBytes_ - lastRxBytes_);
            trafficStats_.wifiBackgroundBytes += (txBytes_ - lastTxBytes_);
            log(TrafficType.wifiBackground);
        } else {
            Log.i(TAG, "wifi disconnected, foreground " + Util.dateFormat(Date.now(), null));
            trafficStats_.wifiForegroundBytes += (rxBytes_ - lastRxBytes_);
            trafficStats_.wifiForegroundBytes += (txBytes_ - lastTxBytes_);
            log(TrafficType.wifiForeground);
        }
        trafficDB_.updateDB(trafficStats_);
        lastRxBytes_ = rxBytes_;
        lastTxBytes_ = txBytes_;
        lastWifiState_ = WifiState.disconnected;
    }

    private void mobileStats() {
        if (lastWifiState_ == WifiState.connected) {
            return;
        }
        prepare();
        if (WearelfApplication.isBackground()) {
            Log.i(TAG, "wifi connected, background " + Util.dateFormat(Date.now(), null));
            trafficStats_.mobileBackgroundBytes += (rxBytes_ - lastRxBytes_);
            trafficStats_.mobileBackgroundBytes += (txBytes_ - lastTxBytes_);
            log(TrafficType.mobileBackground);
        } else {
            Log.i(TAG, "wifi connected, foreground " + Util.dateFormat(Date.now(), null));
            trafficStats_.mobileForegroundBytes += (rxBytes_ - lastRxBytes_);
            trafficStats_.mobileForegroundBytes += (txBytes_ - lastTxBytes_);
            log(TrafficType.mobileForeground);
        }
        trafficDB_.updateDB(trafficStats_);
        lastRxBytes_ = rxBytes_;
        lastTxBytes_ = txBytes_;
        lastWifiState_ = WifiState.connected;
    }

    private void foregroundStats() {
        prepare();
        if (lastNetworkType_ == ConnectivityManager.TYPE_WIFI) {
            Log.i(TAG, "enter background, wifi " + Util.dateFormat(Date.now(), null));
            trafficStats_.wifiForegroundBytes += (rxBytes_ - lastRxBytes_);
            trafficStats_.wifiForegroundBytes += (txBytes_ - lastTxBytes_);
            log(TrafficType.wifiForeground);
        } else if (lastNetworkType_ == ConnectivityManager.TYPE_MOBILE) {
            Log.i(TAG, "enter background, mobile " + Util.dateFormat(Date.now(), null));
            trafficStats_.mobileForegroundBytes += (rxBytes_ - lastRxBytes_);
            trafficStats_.mobileForegroundBytes += (txBytes_ - lastTxBytes_);
            log(TrafficType.mobileForeground);
        }
        trafficDB_.updateDB(trafficStats_);
        lastRxBytes_ = rxBytes_;
        lastTxBytes_ = txBytes_;
    }

    private void backgroundStats() {
        prepare();
        if (lastNetworkType_ == ConnectivityManager.TYPE_WIFI) {
            Log.i(TAG, "enter foreground, wifi " + Util.dateFormat(Date.now(), null));
            trafficStats_.wifiBackgroundBytes += (rxBytes_ - lastRxBytes_);
            trafficStats_.wifiBackgroundBytes += (txBytes_ - lastTxBytes_);
            log(TrafficType.wifiBackground);
        } else if (lastNetworkType_ == ConnectivityManager.TYPE_MOBILE) {
            Log.i(TAG, "enter foreground, mobile " + Util.dateFormat(Date.now(), null));
            trafficStats_.mobileBackgroundBytes += (rxBytes_ - lastRxBytes_);
            trafficStats_.mobileBackgroundBytes += (txBytes_ - lastTxBytes_);
            log(TrafficType.mobileBackground);
        }
        trafficDB_.updateDB(trafficStats_);
        lastRxBytes_ = rxBytes_;
        lastTxBytes_ = txBytes_;
    }

    private void log(TrafficType type) {
        String msg = null;
        switch (type) {
            case mobileBackground:
                msg = "mobileBackground Bytes: " + trafficStats_.mobileBackgroundBytes;
                break;
            case mobileForeground:
                msg = "mobileForeground Bytes: " + trafficStats_.mobileForegroundBytes;
                break;
            case wifiBackground:
                msg = "wifiBackground Bytes: " + trafficStats_.wifiBackgroundBytes;
                break;
            case wifiForeground:
                msg = "wifiForeground Bytes: " + trafficStats_.wifiForegroundBytes;
                break;
            default:
                break;
        }
        if (msg != null) {
            Log.i(TAG, msg);
        }
    }

    public void onWifiConnected() {
        mobileStats();
    }

    public void onWifiDisconnected() {
        wifiStats();
    }

    public void onAppBackground() {
        foregroundStats();
    }

    public void onAppForeground() {
        backgroundStats();
    }

    public void statsAtEndOfTheDay() {
        //每天结束时统计流量
        if (WearelfApplication.isBackground()) {
            backgroundStats();
            foregroundStats();
        } else {
            foregroundStats();
            backgroundStats();
        }
        trafficStats_ = trafficDB_.queryTrafficStats(Date.now());
    }
    public void onAppDestroy() {
        //我们不会主动结束service，因此onDestroy如果被调用，一定是在后台
        backgroundStats();
        trafficDB_.close();
    }

}
