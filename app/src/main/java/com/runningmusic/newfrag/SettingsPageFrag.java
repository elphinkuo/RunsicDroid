package com.runningmusic.newfrag;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.runningmusic.activity.WebViewActivity;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Constants;
import com.runningmusic.utils.Log;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsPageFrag extends Fragment {
    private static String TAG = SettingsPageFrag.class.getName();

    private AQuery aQuery;
    private AppCompatActivity context;
    public SettingsPageFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_page, container, false);
        aQuery = new AQuery(view);
        context = (AppCompatActivity) this.getActivity();
        aQuery.id(R.id.about_bar).clickable(true).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick settings about bar");
                Intent intent = new Intent();
                intent.setClass(context, WebViewActivity.class);
                intent.putExtra("title", "关于奔跑吧音乐");
                intent.putExtra("url", Constants.TENCENT_APP_URL_OLD);
                startActivity(intent);
            }
        });



        return view;

    }

}
