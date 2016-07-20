package com.runningmusic.runninspire;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.androidquery.AQuery;
import com.runningmusic.db.Record;
import com.runningmusic.db.RecordDB;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusiclib.cppwrapper.ActivityManagerWrapper;
import com.runningmusiclib.cppwrapper.utils.Date;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;

public class StartActivity extends Activity implements View.OnClickListener {
    private static String TAG = StartActivity.class.getName();

    private Typeface highNumberTypeface;
    private AQuery aQuery_;

    public static RecordDB recordDB;
    private static int timeSum;
    private static int songSum;
    private static int distanceSum;
    private ArrayList<com.runningmusiclib.cppwrapper.Activity> activities;

    private ArrayList<Record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        UmengUpdateAgent.update(this);
        aQuery_ = new AQuery(this);
        recordDB = new RecordDB();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            //系统通知栏透明
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //系统底部透明
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }

        highNumberTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/tradegothicltstdbdcn20.ttf");
        aQuery_.id(R.id.start_button).typeface(highNumberTypeface);
        aQuery_.id(R.id.start_time_value).typeface(highNumberTypeface);
        aQuery_.id(R.id.start_distance_value).typeface(highNumberTypeface);
        aQuery_.id(R.id.start_song_value).typeface(highNumberTypeface);
        aQuery_.id(R.id.start_button).clickable(true).clicked(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        activities = ActivityManagerWrapper.getRGMActivitiesBetween(Date.dateWithMilliSeconds(Util.getSetupTime()), Date.now());
        records = recordDB.queryAllRecords();
        Log.e(TAG, "activities is " + activities);
        distanceSum = 0;
        timeSum = 0;
        songSum = 0;
//        if (activities != null) {
//            for (com.runningmusiclib.cppwrapper.Activity activity : activities) {
//                distanceSum += activity.getStep();
//                timeSum += activity.getDuration();
//            }
//        }

        if (records != null) {
            for (Record record : records) {
                distanceSum += record.distance;
                timeSum += record.duration;
                songSum += record.song;
            }
        }
        Log.e(TAG, "distanceSum is " + distanceSum + "timeSum is " + timeSum);

        aQuery_.id(R.id.start_distance_value).text(String.format("%.02f", (float) distanceSum * 0.6 / 1000));

        aQuery_.id(R.id.start_time_value).text(Util.getTimeForShow(timeSum));

        aQuery_.id(R.id.start_song_value).text("" + songSum);



    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
//                ActivityManagerWrapper.stopManualActivity();
                Intent intent = new Intent();
                intent.setClass(this, RunningMusicActivity.class);
                intent.putExtra("fromStart", true);
                this.startActivity(intent);
                break;
        }
    }

    public static void updateRecordDB(Record record) {
        recordDB.updateDB(record);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                if (Util.DEBUG) {
                    Log.v(TAG, "Permission is granted");
                }
                return true;
            } else {

                if (Util.DEBUG) {
                    Log.v(TAG, "Permission is revoked");
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            if (Util.DEBUG) {
                Log.v(TAG, "Permission is granted");
            }
            return true;
        }


    }
}
