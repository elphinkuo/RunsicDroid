package com.runningmusic.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.androidquery.AQuery;
import com.runningmusic.runninspire.R;

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
    }
}