package com.runningmusic.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.runningmusic.application.BaseActivity;
import com.runningmusic.runninspire.R;

/**
 * Created by guofuming on 14/3/16.
 */
public class HistoryRecordActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG = HistoryRecordActivity.class.getName();

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_record);
        recyclerView = (RecyclerView)findViewById(R.id.history_list);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

    }



    @Override
    public void onClick(View v) {

    }
}
