package com.runningmusic.music;

import android.database.Cursor;

import com.runningmusic.db.JSONParceble;
import com.runningmusic.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guofuming on 21/1/16.
 */
public class Music implements JSONParceble{

    public String key;
    public String title;
    public String artist;
    public String album;
    public String kbps;
    public String coverURL;
    public String audioURL;
    public String fileName;
    public boolean favourite;
    public int tempo;
    public int duration;


    @Override
    public boolean initWithJSONObject(JSONObject obj) {

        try {
            key = obj.getString("key");
            title = obj.getString("title");
            artist = obj.getString("artist");
            album = obj.getString("album");
            coverURL = obj.getString("cover_url");
            audioURL = obj.getString("audio_url");
            kbps = obj.getString("kbps");
            tempo = obj.getInt("tempo");
            favourite = obj.getBoolean("favorite");
            fileName = Util.generateFileName(audioURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean initWithCursor(Cursor cursor) {
        try {
            key = cursor.getString(cursor.getColumnIndex("_id"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            artist = cursor.getString(cursor.getColumnIndex("artist"));
            album = cursor.getString(cursor.getColumnIndex("album"));
            kbps = cursor.getString(cursor.getColumnIndex("kpbs"));
            coverURL = cursor.getString(cursor.getColumnIndex("coverURL"));
            audioURL = cursor.getString(cursor.getColumnIndex("audioURL"));
            tempo = cursor.getInt((cursor.getColumnIndex("tempo")));
            fileName = cursor.getString(cursor.getColumnIndex("fileName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
