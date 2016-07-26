package com.runningmusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidquery.AQuery;
import com.runningmusic.runninspire.R;
import com.runningmusic.runninspire.RunsicActivity;
import com.runningmusic.service.RunsicService;

public class StartRun extends Activity {

    private AQuery aQuery;
    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_run);
        aQuery = new AQuery(this);
        context = this;

        aQuery.id(R.id.online_switcher).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aQuery.id(R.id.online_switcher).background(R.drawable.start_run_red);
                aQuery.id(R.id.offline_switcher).background(R.drawable.start_run_grey);
            }
        });
        aQuery.id(R.id.offline_switcher).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aQuery.id(R.id.online_switcher).background(R.drawable.start_run_grey);
                aQuery.id(R.id.offline_switcher).background(R.drawable.start_run_green);
            }
        });
        aQuery.id(R.id.start_run_cancel).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
        aQuery.id(R.id.indoor_icon).clickable(true).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, RunsicActivity.class);
                startActivity(intent);
                Intent motionIntent = new Intent(context, RunsicService.class);
                startService(motionIntent);

            }
        });
        aQuery.id(R.id.outdoor_icon).clickable(true).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(context, RunsicActivity.class);
                startActivity(intent);

                Intent motionIntent = new Intent(context, RunsicService.class);
                startService(motionIntent);

            }
        });


    }
}
