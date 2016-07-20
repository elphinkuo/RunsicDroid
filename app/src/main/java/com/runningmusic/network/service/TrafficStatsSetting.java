package com.runningmusic.network.service;

import com.runningmusic.utils.Util;
import com.runningmusiclib.cppwrapper.utils.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class TrafficStatsSetting {
    private PendingIntent pendingIntent_;
    private static TrafficStatsSetting instance_;

    private TrafficStatsSetting() {
        Context context = Util.context();
        Intent intent = new Intent(context, TrafficStatsReceiver.class);
        intent.setAction(TrafficStatsManager.TRAFFIC_DAILYSTATS_ACTION);
        pendingIntent_ = PendingIntent.getBroadcast(Util.context(), 0, intent, 0);
    }

    public static TrafficStatsSetting getInstance() {
        if (instance_ == null) {
            instance_ = new TrafficStatsSetting();
        }
        return instance_;
    }

    private long getFristAlarmComingTime() {
        long endOfDay = Date.now().endOfCurrentDay().getTime();
        return endOfDay + 10 * 1000;
    }

    public void setAlarm() {
        Context context = Util.context();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, getFristAlarmComingTime(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent_);
    }

    public void cancelAlarm() {
        Context context = Util.context();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent_);
    }

}
