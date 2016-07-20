package com.runningmusic.runninspire;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusic.view.AutoScrollViewPager;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;

public class WelcomeActivity extends Activity implements View.OnClickListener {
    private static String TAG = WelcomeActivity.class.getName();

    private boolean DEBUG_RUNNING = true;
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private TextView mTextView;
    private ImageView mSkipBottom;
    private TextView mRunningTextview;
    private AutoScrollViewPager viewPager;
    private CirclePageIndicator indicator;
    private Activity context;

    protected AQuery aQuery;

    private ViewPagerAdapter viewPagerAdapter;
    private int position;
    private Typeface sanfranciscoTypeface;

    private boolean mVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        aQuery = new AQuery(this);

        context = this;
        mSurfaceView = (SurfaceView) findViewById(R.id.welcome_video);

        mMediaPlayer = MediaPlayer.create(this, R.raw.running_video);

        mSurfaceView.getHolder().setKeepScreenOn(true);
        mSurfaceView.getHolder().addCallback(new SurfaceListener());
        mTextView = (TextView) findViewById(R.id.bt_skip_register);
        mSkipBottom = (ImageView) findViewById(R.id.bt_skip_bottom);
        mRunningTextview = (TextView) findViewById(R.id.slogan_running);

        sanfranciscoTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/SF-UI-Display-Bold.otf");
        mRunningTextview.setTypeface(sanfranciscoTypeface);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            //系统通知栏透明
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //系统底部透明
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            if (Util.checkDeviceHasNavigationBar(this)) {
                int height = Util.getNavigationBarHeight(this);
                ViewGroup.LayoutParams layoutParams = mSkipBottom.getLayoutParams();
                layoutParams.height = height;
                mSkipBottom.setLayoutParams(layoutParams);

            }
        }
        mSurfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);




        mTextView.setOnClickListener(this);
        viewPagerAdapter = new ViewPagerAdapter();
        viewPager = (AutoScrollViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setSwipeScrollDurationFactor(1.0);
        viewPager.setInterval(1500);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);

        //自定义indicator
//        indicator = (CirclePageIndicator) findViewById(R.id.share_indicator);
//        indicator.setCentered(true);
//        indicator.setRadius((float) 10.0);
//        indicator.setViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer==null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.running_video);
        }
        viewPager.startAutoScroll();
    }

    private class SurfaceListener implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (mMediaPlayer==null) {
                    mMediaPlayer = MediaPlayer.create(context, R.raw.running_video);
                }
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
//            if (mMediaPlayer.isPlaying())
//                mMediaPlayer.stop();
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer=null;
            }
        }
    }

    private void play() throws IOException {
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setDisplay(mSurfaceView.getHolder());

        mMediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer!=null) {
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
        Log.e(TAG, "onDestroy");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        viewPager.stopAutoScroll();
        if (mMediaPlayer!=null) {
            mMediaPlayer.release();
            mMediaPlayer=null;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//		case R.id.bt_login:
//			Intent intent1 = new Intent();
//			intent1.setClass(this, MainFragmentActivity.class);
//			this.startActivity(intent1);
//			break;

            case R.id.bt_skip_register:
                Intent intent = new Intent();
                if (DEBUG_RUNNING) {
                    intent.setClass(this, AdStartActivity.class);
                } else {
                    intent.setClass(this, StartActivity.class);
                }
                this.startActivity(intent);
                this.finish();
                break;


        }
    }

    /*
     * 页面适配器
     */
    class ViewPagerAdapter extends PagerAdapter {
        private static final int COUNT = 3;
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
                        mViews[0] = inflater.inflate(R.layout.viewpager_rm_welcome, null);
                        Log.e(TAG, "position is" + position);
                        AQuery query2 = new AQuery(mViews[0]);
                        query2.id(R.id.rm_welcome_title).text("智能感应").typeface(sanfranciscoTypeface);
                        query2.id(R.id.rm_welcome_text).text("精准感应实时步频  记录跑步里程时长").typeface(sanfranciscoTypeface);
                        break;
                    }
                    case 1: {
                        mViews[1] = inflater.inflate(R.layout.viewpager_rm_welcome, null);
                        Log.e(TAG, "position is" + position);
                        AQuery query2 = new AQuery(mViews[1]);
                        query2.id(R.id.rm_welcome_title).text("完美对接").typeface(sanfranciscoTypeface);
                        query2.id(R.id.rm_welcome_text).text("音乐节奏与步频共轨契合  潜力随律动一路攀升").typeface(sanfranciscoTypeface);
                        break;
                    }
                    case 2: {
                        mViews[2] = inflater.inflate(R.layout.viewpager_rm_welcome, null);
                        Log.e(TAG, "position is" + position);
                        AQuery query2 = new AQuery(mViews[2]);
                        query2.id(R.id.rm_welcome_title).text("精臻乐库").typeface(sanfranciscoTypeface);
                        query2.id(R.id.rm_welcome_text).text("谨心采选离线私房乐库 音乐不止律动不怠");
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
//            if (object != null && object.equals(mViews[position])) {
//                container.removeView((View) object);
//            }
        }

    }
}