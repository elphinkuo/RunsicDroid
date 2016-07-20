package com.runningmusic.db;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by guofuming on 29/2/16.
 */
public class Record {

    public int id;
    public float distance;
    public int song;
    public int duration;
    public ArrayList<String> recordUrl;


    public Record() {
        distance = 0;
        song = 0;
        duration = 0;
    }


    public boolean initWithCursor(Cursor c) {
        try {
            distance = c.getFloat(c.getColumnIndex("distance"));
            song = c.getInt(c.getColumnIndex("song"));
            duration = c.getInt(c.getColumnIndex("duration"));
            id = c.getInt(c.getColumnIndex("id_"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
