package com.runningmusic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "webCache.db";
	private static final int DB_VERSION = 4;
	public static final String TRAFFIC_TABLE_NAME = "traffic";
	public static final String MUSIC_TABLE_NAME = "music";
	public static final String RUNNING_RECORD_NAME = "record";
	public static final String MUSIC_RECORD_LIST = "musiclist";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + MUSIC_TABLE_NAME
				+ "(_id TEXT PRIMARY KEY," + "title TEXT,"
				+ "artist TEXT," + "album TEXT," + "kpbs TEXT,"
				+ "coverURL TEXT," + "audioURL TEXT," + "tempo INTEGER,"
				+ "duration INTEGER," + "fileName TEXT)");
		db.execSQL("create table if not exists " + TRAFFIC_TABLE_NAME
				+ "(_id DOUBLE PRIMARY KEY," + "wifiFgBytes INTEGER,"
				+ "wifiBgBytes INTEGER," + "mobileFgBytes INTEGER,"
				+ "mobileBgBytes INTEGER)");
		db.execSQL("create table if not exists " + RUNNING_RECORD_NAME
				+ "(_id INTEGER PRIMARY KEY," + "song INTEGER,"
				+ "distance INTEGER," + "duration INTEGER)");
//		db.execSQL("create table if not exists" + MUSIC_RECORD_LIST
//				+ "(_id INTEGER PRIMARY KEY," + "song INTEGER,"
//				+ "distance REAL," + "duration TEXT");
	}

	public void clearData() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("drop table if exists " + MUSIC_TABLE_NAME);
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 2) {
			upgradeToVersion3(db);
			oldVersion += 1;
		}

	}

	private void upgradeToVersion3(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + TRAFFIC_TABLE_NAME
				+ "(_id DOUBLE PRIMARY KEY," + "wifiFgBytes INTEGER,"
				+ "wifiBgBytes INTEGER," + "mobileFgBytes INTEGER,"
				+ "mobileBgBytes INTEGER)");
	}

}
