package com.runningmusic.network.service;

import android.database.Cursor;


public class TrafficStats {

    public long wifiBackgroundBytes;
    public long wifiForegroundBytes;
    public long mobileBackgroundBytes;
    public long mobileForegroundBytes;

    public TrafficStats() {
        wifiBackgroundBytes =  0;
        wifiForegroundBytes = 0;
        mobileBackgroundBytes = 0;
        mobileForegroundBytes = 0;
    }

    public boolean initWithCursor(Cursor c) {
        try {
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
