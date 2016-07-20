package com.runningmusic.runninspire;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.androidquery.AQuery;

public class AdStartActivity extends AppCompatActivity {

    private static String TAG = AdStartActivity.class.getName();
    private AQuery aQuery;
    private Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_start);
        aQuery = new AQuery(this);
        context = this;

        Window window = this.getWindow();
        //系统通知栏透明
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        aQuery.id(R.id.ad_start_enjoy).clickable(true).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, RunninspireMainActivity.class);
                startActivity(intent);
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(context, RunninspireMainActivity.class);
                startActivity(intent);
            }

        }, 2500);
    }
}
