package com.runningmusic.db;

import com.runningmusic.network.service.TrafficStats;
import com.runningmusic.utils.Util;
import com.runningmusiclib.cppwrapper.utils.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TrafficDB {

    private DBHelper dbHelper_;
    private SQLiteDatabase db_;

    public TrafficDB() {
        dbHelper_ = new DBHelper(Util.context());
        db_ = dbHelper_.getWritableDatabase();
    }

    public void close() {
        db_.close();
        dbHelper_.close();
    }

    private ContentValues getCVFromTrafficStats(TrafficStats ts) {
        ContentValues cv = new ContentValues();
        if (ts != null) {
            cv.put("_id", ts.date.seconds());
            cv.put("wifiFgBytes", ts.wifiForegroundBytes);
            cv.put("wifiBgBytes", ts.wifiBackgroundBytes);
            cv.put("mobileFgBytes", ts.mobileForegroundBytes);
            cv.put("mobileBgBytes", ts.mobileBackgroundBytes);
        }
        return cv;
    }

    public void updateDB(TrafficStats ts) {
        if (ts != null && db_.isOpen()) {
            db_.replace(DBHelper.TRAFFIC_TABLE_NAME, null, getCVFromTrafficStats(ts));
        }
    }

    public TrafficStats queryTrafficStats(Date date) {
        Cursor c = db_.rawQuery("select * from " + DBHelper.TRAFFIC_TABLE_NAME + " where _id=" + date.startOfCurrentDay().seconds(), null);
        TrafficStats result = new TrafficStats();
        try {
            if (c.moveToFirst()) {
                result.initWithCursor(c);
            }
        } finally {
            c.close();
        }
        return result;
    }
}
