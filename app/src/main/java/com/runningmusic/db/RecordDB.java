package com.runningmusic.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.runningmusic.utils.Util;

import java.util.ArrayList;

/**
 * Created by guofuming on 29/2/16.
 */
public class RecordDB {
    private SQLiteDatabase db_;
    private DBHelper dbHelper_;


    public int id;
    public float distance;
    public int song;
    public int duration;

    public RecordDB() {
        dbHelper_ = new DBHelper(Util.context());
        db_ = dbHelper_.getWritableDatabase();
    }

    public void close() {
        db_.close();
        dbHelper_.close();
    }


    private ContentValues getCVFromRecord(Record record) {
        ContentValues cv = new ContentValues();

        if (record != null) {
            cv.put("_id", record.id);
            cv.put("song", record.song);
            cv.put("distance", record.distance);
            cv.put("duration", record.duration);
        }
        return cv;
    }

    public void updateDB(Record record) {
        if (record != null && db_.isOpen()) {
            db_.replace(DBHelper.RUNNING_RECORD_NAME, null, getCVFromRecord(record));
        }
    }

    public ArrayList<Record> queryAllRecords() {
        ArrayList<Record> result = new ArrayList<Record>();
        Cursor cursor = db_.rawQuery("SELECT * FROM record", null);
        try {
            while (cursor.moveToNext()) {
                Record record = new Record();
                record.initWithCursor(cursor);
                result.add(record);
            }
        } finally {
            cursor.close();
        }
        return result;
    }


}
