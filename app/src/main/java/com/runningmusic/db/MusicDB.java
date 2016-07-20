package com.runningmusic.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.runningmusic.music.Music;
import com.runningmusic.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guofuming on 21/1/16.
 */
public class MusicDB {


    private SQLiteDatabase db_;
    private DBHelper dbHelper_;

    public MusicDB() {
        dbHelper_ = new DBHelper(Util.context());
        db_ = dbHelper_.getWritableDatabase();
    }

    public void close() {
        db_.close();
        dbHelper_.close();
    }

    private ContentValues getCVFromMusic(Music music) {
        ContentValues cv = new ContentValues();

        if (music != null) {
            cv.put("_id", music.key);
            cv.put("title", music.title);
            cv.put("artist", music.artist);
            cv.put("album", music.album);
            cv.put("coverURL", music.coverURL);
            cv.put("audioURL", music.audioURL);
            cv.put("kpbs", music.kbps);
            cv.put("tempo", music.tempo);
            cv.put("fileName", music.fileName);
        }
        return cv;
    }

    public void updateDB(Music music) {
        if (music != null) {
            db_.replace(DBHelper.MUSIC_TABLE_NAME, null, getCVFromMusic(music));
        }
    }

    public void updateDB(ArrayList<Music> musicList) {
        if (musicList!=null) {
            db_.beginTransaction();
            try {
                for (int i = 0; i < musicList.size(); i++) {
                    Music music = musicList.get(i);
                    db_.replace(DBHelper.MUSIC_TABLE_NAME, null, getCVFromMusic(music));
                }
            } finally {
                db_.endTransaction();
            }
        }
    }

    public ArrayList<Music> queryAllMusics() {
        ArrayList<Music> result = new ArrayList<Music>();
        Cursor cursor = db_.rawQuery("SELECT * FROM music", null);
        try {
            while (cursor.moveToNext()) {
                Music music = new Music();
                music.initWithCursor(cursor);
                result.add(music);
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public ArrayList<Music> queryCachedMusic(List<String> fileNames) {
        ArrayList<Music> result = new ArrayList<>();

        Cursor cursor = db_.rawQuery("SELECT * FROM music", null);
        try {
            while (cursor.moveToNext()) {
                String fileName = cursor.getString((cursor.getColumnIndex("fileName")));
                if (fileNames.contains(fileName)) {
                    Music music = new Music();
                    music.initWithCursor(cursor);
                    result.add(music);
                }
            }
        } finally {
            cursor.close();
        }

        return result;
    }



}
