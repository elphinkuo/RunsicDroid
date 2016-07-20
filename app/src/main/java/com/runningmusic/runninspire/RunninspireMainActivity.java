package com.runningmusic.runninspire;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.runningmusic.activity.StartRun;
import com.runningmusic.fragment.MusicList;
import com.runningmusic.newfrag.EventListFrag;
import com.runningmusic.newfrag.MainGoFrag;
import com.runningmusic.newfrag.MusicListFrag;
import com.runningmusic.newfrag.PersonPageFrag;
import com.runningmusic.newfrag.SettingsPageFrag;
import com.runningmusic.utils.Log;

public class RunninspireMainActivity extends AppCompatActivity {

    public static String TAG = RunninspireMainActivity.class.getName();


    public Fragment contentFragment_;
    private FragmentTabHost mTabHost;

    public static String currentTab_ = "我的";


    private LayoutInflater layoutInflater;
    private AQuery aQuery_;

    @SuppressWarnings("rawtypes")
    private final Class fragmentArray[] = {
            PersonPageFrag.class, MusicList.class, MainGoFrag.class, EventListFrag.class, SettingsPageFrag.class
    };

    private int mImageViewArray[] = {R.drawable.tab_person_btn, R.drawable.tab_music_btn, R.drawable.main_go, R.drawable.tab_event_btn, R.drawable.tab_more_btn};

    private final String mTextviewArray[] = {"我的", "曲库", "GO", "活动", "更多"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aQuery_ = new AQuery(this);
        initView();
        Window window = this.getWindow();
        //系统通知栏透明
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        aQuery_.id(R.id.tab_go).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRun();
//                MainGoFrag frag = new MainGoFrag();
//                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, frag).commit();
            }
        });
    }


    private void initView() {
        layoutInflater = LayoutInflater.from(this);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.content_frame);

        int countFragment = fragmentArray.length;

        for (int i = 0; i < countFragment; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));

            mTabHost.addTab(tabSpec, fragmentArray[i], null);

            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.tab_bar_background);
            mTabHost.getTabWidget().setStripEnabled(false);
        }

        mTabHost.getTabWidget().getChildTabViewAt(2).setEnabled(false);
        TabWidget mTabWidget = (TabWidget) findViewById(android.R.id.tabs);

        mTabWidget.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                selectTabItem(tabId);
            }
        });
    }


    @SuppressLint("InflateParams")
    private View getTabItemView(int index) {

        View view = layoutInflater.inflate(R.layout.tab_item_layout, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.tab_textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment.getClass() == MainGoFrag.class || fragment.getClass() == MusicListFrag.class
                || fragment.getClass() == MainGoFrag.class || fragment.getClass() == EventListFrag.class
                || fragment.getClass() == SettingsPageFrag.class) {
            contentFragment_ = fragment;
        }
    }

    @Override
    public void onBackPressed() {
        if (currentTab_.equals("我的") || currentTab_.equals("曲库") || currentTab_.equals("GO") || currentTab_.equals("活动") || currentTab_.equals("更多")) {
            finish();
        }
    }


    /**
     * Tab Bar的选择按键逻辑
     * @param tabId
     */
    public void selectTabItem(String tabId) {

        currentTab_ = tabId;
        contentFragment_ = getSupportFragmentManager().findFragmentByTag(currentTab_);
        Log.e(TAG, "contentFragment is " + contentFragment_);
        Log.e(TAG, "currentTab is " + currentTab_);
        if (contentFragment_!=null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, contentFragment_).commit();
        }

    }

    /**
     * 开始跑步
     */
    public void startRun() {
        Intent intent = new Intent();
        intent.setClass(this, StartRun.class);
        startActivity(intent);
    }
}


