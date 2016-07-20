package com.runningmusic.newfrag;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.facebook.AppEventsConstants;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonPageFrag extends Fragment {
    private static String TAG = PersonPageFrag.class.getName();

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private CoordinatorLayout.LayoutParams appBarLayoutParams;

    private AQuery aQuery;

    private AppCompatActivity context;
    public PersonPageFrag() {
        // Required empty public constructor
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = (AppCompatActivity)this.getActivity();



        View fragmentView = inflater.inflate(R.layout.fragment_person_page2, container, false);
        collapsingToolbarLayout = (CollapsingToolbarLayout) fragmentView.findViewById(R.id.ad_collapsing_toolbar);
        toolbar = (Toolbar) fragmentView.findViewById(R.id.ad_tool_bar);
        appBarLayout = (AppBarLayout) fragmentView.findViewById(R.id.ad_app_bar);

//        appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//        int height = appBarLayoutParams.height;
//        height += Util.getStatusBarHeight(context);
//        appBarLayoutParams.height = height;
//        appBarLayout.setLayoutParams(appBarLayoutParams);

        appBarLayout.setMinimumHeight(appBarLayout.getHeight()+Util.getStatusBarHeight(context));
        aQuery = new AQuery(fragmentView);

        collapsingToolbarLayout.setTitle("奔跑吧音乐");
        toolbar.setCollapsible(true);


        collapsingToolbarLayout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.e(TAG, "collapsingToolbarLayout onViewAttachedToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.e(TAG, "collapsingToolbarLayout onViewDetachedFromWindow");
            }
        });

        toolbar.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.e(TAG, "toolbar onViewAttachToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.e(TAG, "toolbar onViewDetachedFromWindow");
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.e(TAG, "appBarLayout " + verticalOffset);

                if ( (verticalOffset+240)<0) {
                    aQuery.id(R.id.add_tool_bar_title).visible();
                } else {
                    aQuery.id(R.id.add_tool_bar_title).invisible();
                }
            }
        });


//        context.setSupportActionBar(toolbar);
//        context.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        aQuery.id(R.id.portrait).clickable(true).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = context.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, loginFragment).commit();
            }
        });
        return fragmentView;
    }

}
