package com.runningmusic.network.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusiclib.cppwrapper.LocalPlaceManagerWrapper;
import com.runningmusiclib.cppwrapper.utils.Date;

public class TrafficStatsReceiver extends BroadcastReceiver {

    public static final String TAG = "TrafficStatsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TrafficStatsManager.TRAFFIC_DAILYSTATS_ACTION)) {
            Log.i(TAG, "receive alarm " + Util.dateFormat(Date.now(), null));
            TrafficStatsManager.getInstance().statsAtEndOfTheDay();

            LocalPlaceManagerWrapper.automaticIdentificationPoi();

            // 打包一天的数据,一天切换时上传数据
        }
    }

}
