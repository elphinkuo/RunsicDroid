package com.runningmusic.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Log;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by guofuming on 1/4/16.
 */
public class MusicListDetailActivity extends AppCompatActivity {
    private static String TAG = MusicListDetailActivity.class.getName();

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private CirclePageIndicator pageIndicator;

    private RecyclerView songSheetDetail;
    private LinearLayoutManager layoutManager;

    private CoordinatorLayout coordinatorLayout;
    private LinearLayout anchorLayout;
    private static CollapsingToolbarLayout collapsingToolbarLayout;

    private ViewPager.OnPageChangeListener pageChangeListener;



//    public static final String EXTRA_NAME = "cheese_name";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list_detail);

        Intent intent = getIntent();
        viewPager = (ViewPager) findViewById(R.id.musiclist_detail_viewpager);
        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        pageIndicator = (CirclePageIndicator) findViewById(R.id.musiclist_detail_indicator);
        pageIndicator.setViewPager(viewPager);
        pageIndicator.setRadius(12);
        pageIndicator.setStrokeWidth(3);
        pageIndicator.setGap(10);

        songSheetDetail = (RecyclerView) findViewById(R.id.song_sheet_list);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        songSheetDetail.setLayoutManager(layoutManager);



        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        anchorLayout = (LinearLayout) findViewById
                (R.id.anchor_run);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);


        loadBackdrop();
        initListener();
        viewPager.addOnPageChangeListener(pageChangeListener);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                Log.e(TAG, "total scroll Range is " + appBarLayout.getTotalScrollRange());
//                Log.e(TAG, "verticalOffSet is " + verticalOffset);
//                if ((verticalOffset +959)<=0 ) {
//                    Log.e(TAG, "bring Child to Front");
//                    anchorLayout.getParent().requestLayout();
//                    for (int i = 0; i < coordinatorLayout.getChildCount(); i ++) {
//                        Log.e(TAG, "before child order "+ i + " is " + coordinatorLayout.getChildAt(i));
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            Log.e(TAG, "before child order "+ i + " elevation is " + coordinatorLayout.getChildAt(i).getElevation());
//                            Log.e(TAG, "before child order " + i + " Z is " + coordinatorLayout.getChildAt(i).getZ());
//                            Log.e(TAG, "before child order "+ i + " TranslationZ is " + coordinatorLayout.getChildAt(i).getTranslationZ());
//
//
//                        }
//
//                    }
//                    ((ViewGroup)coordinatorLayout).bringChildToFront(coordinatorLayout.getChildAt(2));
//                    coordinatorLayout.updateViewLayout(anchorLayout, anchorLayout.getLayoutParams());
//                    for (int i = 0; i < coordinatorLayout.getChildCount(); i ++) {
//                        Log.e(TAG, "after child order "+ i + " is " + coordinatorLayout.getChildAt(i));
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            Log.e(TAG, "after child order "+ i + " elevation is " + coordinatorLayout.getChildAt(i).getElevation());
//                        }
//
//
//                    }
//                }
//            }
//
//        });

    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.musiclist_detail_back);
//        Glide.with(this).load(Cheeses.getRandomCheeseDrawable()).centerCrop().into(imageView);
    }

    public void initListener() {
        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
                } else {
                    collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }


    /**
     * 暂时不现实右上角设置按钮
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }


    class ViewPagerAdapter extends PagerAdapter {
        private static final int COUNT = 2;
        private View[] mViews = new View[COUNT];

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position < 0 || position >= COUNT) {
                return null;
            }
            if (mViews[position] == null) {
                final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                switch (position) {
                    case 0: {
                        mViews[0] = inflater.inflate(R.layout.musiclist_detail_pager1, null);
                        Log.e(TAG, "position is" + position);
                        AQuery query2 = new AQuery(mViews[0]);
                        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);

//                        collapsingToolbarLayout.setTitleEnabled(true);
//                        query2.id(R.id.rm_welcome_title).text("智能感应").typeface(sanfranciscoTypeface);
//                        query2.id(R.id.rm_welcome_text).text("精准感应实时步频  记录跑步里程时长").typeface(sanfranciscoTypeface);
                        break;
                    }
                    case 1: {
                        mViews[1] = inflater.inflate(R.layout.musiclist_detail_pager2, null);
                        Log.e(TAG, "position is" + position);
                        AQuery query2 = new AQuery(mViews[1]);
                        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

//                        collapsingToolbarLayout.setTitleEnabled(false);
//                        query2.id(R.id.rm_welcome_title).text("完美对接").typeface(sanfranciscoTypeface);
//                        query2.id(R.id.rm_welcome_text).text("音乐节奏与步频共轨契合  潜力随律动一路攀升").typeface(sanfranciscoTypeface);
                        break;
                    }
                    default:
                        break;

                }
                container.addView(mViews[position]);
            }
            return mViews[position];
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return COUNT;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg1 != null && arg1.equals(arg0);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.e(TAG, "destroy " + position);
        }

    }
}
