package com.runningmusic.network.service;

import android.database.Cursor;

import com.runningmusiclib.cppwrapper.utils.Date;

public class TrafficStats {

    public Date date;
    public long wifiBackgroundBytes;
    public long wifiForegroundBytes;
    public long mobileBackgroundBytes;
    public long mobileForegroundBytes;

    public TrafficStats() {
        date = Date.now().startOfCurrentDay();
        wifiBackgroundBytes =  0;
        wifiForegroundBytes = 0;
        mobileBackgroundBytes = 0;
        mobileForegroundBytes = 0;
    }

    public boolean initWithCursor(Cursor c) {
        try {
            date = Date.dateWithSeconds(c.getDouble(c.getColumnIndex("_id")));
            wifiBackgroundBytes = c.getLong(c.getColumnIndex("wifiBgBytes"));
            wifiForegroundBytes = c.getLong(c.getColumnIndex("wifiFgBytes"));
            mobileBackgroundBytes = c.getLong(c.getColumnIndex("mobileBgBytes"));
            mobileForegroundBytes = c.getLong(c.getColumnIndex("mobileFgBytes"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

}
