package com.runningmusic.newfrag;


import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.androidquery.AQuery;
//import com.facebook.AppEventsConstants;
import com.runningmusic.adapter.GridAdapter;
import com.runningmusic.event.EventRequestEvent;
import com.runningmusic.event.FavMusicListEvent;
import com.runningmusic.music.Music;
import com.runningmusic.network.http.RunsicRestClientUsage;
import com.runningmusic.network.service.ImageSingleton;
import com.runningmusic.runninspire.R;
import com.runningmusic.service.RunsicService;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusic.view.CircleNetworkImageView;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonPageFrag extends Fragment {
    private static String TAG = PersonPageFrag.class.getName();


    private static final String KEY_TRANSITION_EFFECT = "transition_effect";
    private int mCurrentTransitionEffect = JazzyHelper.TILT;
    private JazzyRecyclerViewScrollListener jazzyScrollListener;



    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    private CoordinatorLayout.LayoutParams appBarLayoutParams;

    private RecyclerView favRecyclerView;

    private ImageLoader imageLoader;
    private CircleNetworkImageView portraitImage;

    private GridAdapter gridAdapter;

    private AQuery aQuery;

    private AppCompatActivity context;

    private ArrayList<Music> favMusicList;

    public PersonPageFrag() {

        // Required empty public constructor
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = (AppCompatActivity) this.getActivity();


        View fragmentView = inflater.inflate(R.layout.fragment_person_page2, container, false);
        collapsingToolbarLayout = (CollapsingToolbarLayout) fragmentView.findViewById(R.id.ad_collapsing_toolbar);
        toolbar = (Toolbar) fragmentView.findViewById(R.id.ad_tool_bar);
        appBarLayout = (AppBarLayout) fragmentView.findViewById(R.id.ad_app_bar);
        imageLoader = ImageSingleton.getInstance(context).getImageLoader();
        portraitImage = (CircleNetworkImageView) fragmentView.findViewById(R.id.portrait);
        favRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.person_fav_music);
        favRecyclerView.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false));


        jazzyScrollListener = new JazzyRecyclerViewScrollListener();
        favRecyclerView.setOnScrollListener(jazzyScrollListener);
//        appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//        int height = appBarLayoutParams.height;
//        height += Util.getStatusBarHeight(context);
//        appBarLayoutParams.height = height;
//        appBarLayout.setLayoutParams(appBarLayoutParams);



        appBarLayout.setMinimumHeight(appBarLayout.getHeight() + Util.getStatusBarHeight(context));
        aQuery = new AQuery(fragmentView);

        collapsingToolbarLayout.setTitle("奔跑吧音乐");
        toolbar.setCollapsible(true);

        if (getArguments() != null) {
            portraitImage.setImageUrl(getArguments().getString("headimgurl"), imageLoader);
            aQuery.id(R.id.person_header_name).text(getArguments().getString("nickname"));
            aQuery.id(R.id.person_header_id).text(getArguments().getString("city"));
        }

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

                if ((verticalOffset + 240) < 0) {
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
                fragmentManager.beginTransaction().add(R.id.content_frame, loginFragment).commit();
            }
        });
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
//        if (savedInstanceState == null) {
            SharedPreferences sharedPreferences = Util.getUserPreferences();
            if (sharedPreferences.contains("token")) {
                RunsicRestClientUsage.getInstance().getFavMusic(sharedPreferences.getString("token", "579096c3421aa90c9c3596c8"));
            } else {
                RunsicRestClientUsage.getInstance().getFavMusic("579096c3421aa90c9c3596c8");
            }
//            RunsicRestClientUsage.getInstance().getFavMusic(){
//
//            }
//        }

    }

//    @Override
//    public void onStart() {
//        super.onResume();
//        EventBus.getDefault().register(this);
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFavMusicListEvent(FavMusicListEvent favMusicListEvent) {
        favMusicList = favMusicListEvent.musicFavList;
        if (favMusicList.size()==0) {
            favMusicList = RunsicService.getInstance().musicCurrentList.getCurrentMusicList();
        }
        gridAdapter = new GridAdapter(context, R.layout.horizontal_item_recycler, favMusicList);
        favRecyclerView.setAdapter(gridAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TRANSITION_EFFECT, mCurrentTransitionEffect);
    }


    private void setupJazziness(int effect) {
        mCurrentTransitionEffect = effect;
        jazzyScrollListener.setTransitionEffect(mCurrentTransitionEffect);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRequestEvent(EventRequestEvent event) {
//        eventArrayList = event.eventList;
//        eventAdapter = new EventAdapter(context, R.layout.event_list_item_layout, event.eventList);
//        eventAdapter.setOnItemClickListener(this);
//        eventRecyclerView.setAdapter(eventAdapter);
    }
}
