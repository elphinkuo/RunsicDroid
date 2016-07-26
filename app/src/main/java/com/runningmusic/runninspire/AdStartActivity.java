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
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgentJSInterface;

public class AdStartActivity extends AppCompatActivity {

    private static String TAG = AdStartActivity.class.getName();
    private AQuery aQuery;
    private Activity context;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_start);
        aQuery = new AQuery(this);
        context = this;
        handler = new Handler();
        Window window = this.getWindow();
        //系统通知栏透明
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        aQuery.id(R.id.ad_start_enjoy).clickable(true).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, RunninspireMainActivity.class);
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (handler != null) {
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setClass(context, RunninspireMainActivity.class);
                    startActivity(intent);
                }

            }, 2500);

        } else {
            handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setClass(context, RunninspireMainActivity.class);
                    startActivity(intent);
                }

            }, 2500);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
