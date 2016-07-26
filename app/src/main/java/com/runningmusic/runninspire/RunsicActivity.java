package com.runningmusic.runninspire;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Xml;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.toolbox.ImageLoader;
import com.androidquery.AQuery;
import com.autonavi.amap.mapcore.Convert;
import com.google.protobuf.InvalidProtocolBufferException;
import com.runningmusic.db.Record;
import com.runningmusic.db.RecordDB;
import com.runningmusic.event.BPMEvent;
import com.runningmusic.event.CurrentMusicEvent;
import com.runningmusic.fragment.MoveMapFragment;
import com.runningmusic.fragment.OnBackPressedListener;
import com.runningmusic.fragment.StaticMusicPlayFragment;
import com.runningmusic.jni.SportTracker;
import com.runningmusic.music.Music;
import com.runningmusic.music.MusicPlayCallback;
import com.runningmusic.network.NetworkMode;
import com.runningmusic.network.http.RunsicRestClientUsage;
import com.runningmusic.network.service.ImageSingleton;
import com.runningmusic.service.RunsicService;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusic.view.CircleNetworkImageView;
import com.runningmusic.view.PulseView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.net.utils.Base64;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Timer;


public class RunsicActivity extends FragmentActivity implements SensorEventListener, AMapLocationListener, View.OnClickListener, StaticMusicPlayFragment.OnStaticMusicPlayFragmentClose {

    public String TAG = RunsicActivity.class.getName();

    protected PulseView pulse;

    private AMapLocationClient locationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation lastLocation;
    private static final String ROTATION = "rotation";
    private boolean mIsListClose = true;
    private boolean mIsPlayStaticClose = true;
    private boolean mMoveMusicPlay = true;
    private static int currentTempo = 0;
    private static int songSum;
    private static boolean pausedBool;
    private static boolean bpmAutoBool;
    private static Music currentMusic;

    private NetworkMode networkMode;
    private MusicPlayCallback musicPlayCallback;

    private static int musicListFragmentID;
    private static int musicStaticListFragmentID;


    public AQuery aQuery_;
    private float distanceValue;
    private ImageView mapCornerIcon;
    private CircleNetworkImageView currentMusicThumb;
    private ImageLoader imageLoader;
    private RelativeLayout musicControlMove;
    private RelativeLayout musicControlStatic;
    private RelativeLayout musicHeader;
    private Chronometer mRunTimer;

    private static long lastRunPause;
    private static long lastMusicPause;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;


    public static Typeface highNumberTypeface;

    private FrameLayout containerLayout;

    private FragmentManager fragmentManager;

    private Context context;


    private RecordDB recordDB;
//    private DonutProgress donutProgress;


    //菜单按钮动画
    private Timer timer;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.runnin_spire_layout);
        EventBus.getDefault().register(this);
        // 屏幕休眠检测
        registerScreenActionBroadcastReceiver();

        aQuery_ = new AQuery(this);
        recordDB = new RecordDB();


        context = this;
        mapCornerIcon = (ImageView) findViewById(R.id.map_corner_icon);
        musicControlMove = (RelativeLayout) findViewById(R.id.music_control_panel_move);
        currentMusicThumb = (CircleNetworkImageView) findViewById(R.id.music_playing_cover_thumb);
        imageLoader = ImageSingleton.getInstance(this).getImageLoader();

        pulse = (PulseView) findViewById(R.id.pulse_view);

        startRunAndPlay();


        fragmentManager = getSupportFragmentManager();

        mapCornerIcon.setOnClickListener(this);
        musicControlMove.setOnClickListener(this);


        aQuery_.id(R.id.music_move_play_or_pause).clickable(true).clicked(this);
        aQuery_.id(R.id.music_playing_cover_thumb).clickable(true).clicked(this);
        aQuery_.id(R.id.music_playing_title).clickable(true).clicked(this);
        aQuery_.id(R.id.music_playing_artist).clickable(true).clicked(this);
        aQuery_.id(R.id.music_like).clickable(true).clicked(this);
        aQuery_.id(R.id.list_corner_right).clickable(true).clicked(this);
        aQuery_.id(R.id.bpm_up).clickable(true).clicked(this);
        aQuery_.id(R.id.bpm_down).clickable(true).clicked(this);
        aQuery_.id(R.id.move_stop_button).clickable(true).clicked(this);
        aQuery_.id(R.id.music_like).clickable(true).clicked(this);
        aQuery_.id(R.id.map_corner_icon).clickable(true).clicked(this);
        aQuery_.id(R.id.bpm_lock_panel).background(R.mipmap.bpm_on).clickable(true).clicked(this);
        aQuery_.id(R.id.music_playing_next).clickable(true).clicked(this);

        highNumberTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/tradegothicltstdbdcn20.ttf");

        lastRunPause = 0;

        mRunTimer.setBase(SystemClock.elapsedRealtime());
        mRunTimer.start();
        musicPlayCallback = RunsicService.getInstance();


    }


    synchronized private void startRunAndPlay() {
        mRunTimer = (Chronometer) findViewById(R.id.time_data);
        mRunTimer.start();
        mRunTimer.getBase();
        initMotionSensor();
        initLocation();
        startTrack();
        pausedBool = false;
        bpmAutoBool = true;
        RunsicRestClientUsage.getInstance().getTempoList(130);
    }

    @Override
    public void onLocationChanged( AMapLocation location ) {
        Log.e(TAG, "on Location Changed");
        if (lastLocation == null || lastLocation.getLatitude() != location.getLatitude() || lastLocation.getLongitude() != location.getLongitude()) {
            Toast.makeText(getApplicationContext(), String.format("%f, %f, %.0f",
                    location.getLatitude(), location.getLongitude(), location.getAccuracy()), Toast.LENGTH_SHORT).show();

            SportTracker.pushLocation(new Date().getTime(), location.getLatitude(), location.getLongitude(), (float) 0, location.getSpeed());
            lastLocation = location;
            SportTracker.getDistance();
            SportTracker.getSpeed();

            Log.e(TAG, "LOCATION CHANGE DISTANCE IS " + SportTracker.getDistance() + " SPEED IS " + SportTracker.getSpeed());
//            EventBus.getDefault().post(new LocationChangedEvent(SportTracker.getDistance(), SportTracker.getSpeed()));

        }
    }


    @Override
    public void onAccuracyChanged( Sensor sensor, int accuracy ) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (Util.DEBUG) {
            Log.e(TAG, "onStart");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.DEBUG) {
            Log.e(TAG, "onResume");
        }

        if (pausedBool) {
            aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.move_pause);
        } else {
            aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.move_play);
        }

//        mMoveMusicPlay = RunsicService.getInstance().getPlayerStatus();
//        if (!mMoveMusicPlay) {
//            Log.e(TAG, "music play status is " + mMoveMusicPlay + "lastPause is " + lastRunPause);

//        musicPlayCallback.onMusicGoOn();

//        if (RunsicService.getInstance().musicCurrent!= null) {
//            Music music = RunsicService.getInstance().musicCurrent.getCurrentMusic();
//            aQuery_.id(R.id.music_playing_title).text(music.title);
//            aQuery_.id(R.id.music_playing_artist).text(music.artist);
//            aQuery_.id(R.id.music_control_current_bpm).text("" + music.tempo);
//            currentMusicThumb.setImageUrl(music.coverURL, imageLoader);
//        }

//            mRunTimer.setBase(SystemClock.elapsedRealtime() + lastRunPause);
//            mRunTimer.start();
//            mMusicPlayTimer.setBase(SystemClock.elapsedRealtime() + lastMusicPause);
//            mMusicPlayTimer.start();
//        }
        MobclickAgent.onResume(this);

        Log.i(TAG, "APP onResume");
//            manualActivity_ = ActivityManagerWrapper.getCurrentManualActivity();
//            Log.i(TAG, "acttivity start time is " + manualActivity_.getStartTime() + "----------" + manualActivity_.getStartLong());
//            Log.e(TAG, "distance is " + manualActivity_.getDistance());
        Log.e(TAG, "on Resume lastRunPause is " + lastRunPause);
    }


//
//


    @Override
    protected void onPause() {
        super.onPause();
        if (Util.DEBUG) {
            Log.e(TAG, "onPause");
        }
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.DEBUG) {
            Log.e(TAG, "onStop");
        }

        EventBus.getDefault().unregister(this);
        if (Util.DEBUG) {
            Log.i(TAG, "ON STOP");
        }

        lastRunPause = SystemClock.elapsedRealtime();

        if (Util.DEBUG) {
            Log.e(TAG, "lastRunPause is " + lastRunPause);
        }

    }


    private void playOnTempo( int tempo ) {
        if (Util.DEBUG) {
            Log.e(TAG, "PLAY=ON=TEMPO========" + tempo);
        }


    }

    /**
     * Static Music List 页面的回掉
     */
    @Override
    public void onStaticMusicPlayFragmentClose() {
        onMapOpen();
        aQuery_.id(R.id.music_control_panel_static).visibility(View.INVISIBLE);
        aQuery_.id(R.id.music_control_panel_move).visibility(View.VISIBLE);
    }


    public void onMapOpen() {

        //Header 按钮动画
        mIsListClose = false;

        Fragment mapFragment = new MoveMapFragment();
        musicListFragmentID = mapFragment.getId();
        FragmentTransaction transactionTop = fragmentManager.beginTransaction();
        transactionTop.setCustomAnimations(R.anim.slidein_fromtop, R.anim.slideout_fromtop, R.anim.slidein_fromtop, R.anim.slideout_fromtop);
        transactionTop.add(R.id.fragment_container_top, mapFragment);
        transactionTop.addToBackStack("MusicList");
        transactionTop.commit();


        //底部进度条与控制栏隐藏
        musicControlMove.setVisibility(View.INVISIBLE);


    }

    public void onMapClose() {

        //Header 按钮动画
        mIsListClose = true;
        //关闭MusicList Fragment
        fragmentManager.popBackStack();
        //底部进度条与控制栏显示

        musicControlMove.setVisibility(View.VISIBLE);

    }

    public void onPlayStaticOpen() {
        //打开 StaticMusicPlayFragment
        Fragment musicPlayFragment = new StaticMusicPlayFragment();
        musicStaticListFragmentID = musicPlayFragment.getId();
        FragmentTransaction transactionBottom = fragmentManager.beginTransaction();
        transactionBottom.setCustomAnimations(R.anim.slidein_frombottom_for_activity, R.anim.slideout_frombottom, R.anim.slidein_frombottom_for_activity, R.anim.slideout_frombottom);
        transactionBottom.add(R.id.fragment_container_top, musicPlayFragment);
        transactionBottom.addToBackStack("StaticMusicPlay");
        transactionBottom.commit();
        //隐藏HeaderView
        aQuery_.id(R.id.runninspire_header).gone();
//        musicHeader.setVisibility(View.INVISIBLE);
    }

    public void onPlayStaticClose() {
        //关闭 StaticMusicPlayFragment

        mIsPlayStaticClose = true;
        fragmentManager.popBackStack();

        //显示HeaderView
        musicHeader.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState( Bundle savedInstanceState ) {
        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putLong("lastRunPause", lastRunPause);
//        savedInstanceState.putLong("lastMusicPause", lastMusicPause);
    }

    @Override
    public void onClick( View v ) {
        if (Util.DEBUG) {
            Log.i(TAG, "" + v.getId());
        }
        switch (v.getId()) {

            case R.id.bpm_lock_panel:
                if (bpmAutoBool) {
                    aQuery_.id(R.id.bpm_lock_panel).background(R.mipmap.bpm_off);
                    aQuery_.id(R.id.move_minus).visible();
                    aQuery_.id(R.id.move_plus).visible();
                    bpmAutoBool = false;
                } else {
                    aQuery_.id(R.id.bpm_lock_panel).background(R.mipmap.bpm_on);
                    aQuery_.id(R.id.move_minus).invisible();
                    aQuery_.id(R.id.move_plus).invisible();
                    bpmAutoBool = true;
                }
                break;
            case R.id.music_like:
                Log.e(TAG, "music like onclicked");
                SharedPreferences sharedPreferences = Util.getUserPreferences();
                if (!currentMusic.favourite) {
                    RunsicRestClientUsage.getInstance().setLikeMusic(sharedPreferences.getString("token", "579096c3421aa90c9c3596c8"), currentMusic.key);
                    currentMusic.favourite = true;
                    aQuery_.id(R.id.music_like).background(R.drawable.like_red);
                } else {
                    RunsicRestClientUsage.getInstance().setUnLikeMusic(sharedPreferences.getString("token", "579096c3421aa90c9c3596c8"), currentMusic.key);
                    currentMusic.favourite = true;
                    aQuery_.id(R.id.music_like).background(R.drawable.like);
                }
                break;
            case R.id.map_corner_icon:
                if (Util.DEBUG) {
                    Log.i(TAG, "click on list corner icon" + mIsListClose);
                }
                if (mIsListClose) {
                    onMapOpen();
                } else {
                    onMapClose();
                }

                break;

            case R.id.music_move_play_or_pause:

                if (!pausedBool) {
                    pauseMusicAndSport();
                } else {
                    goingOnMusicAndSport();
                }
                break;

            /**
             * 重构思路
             *      变更为根据是否暂停状态来去执行 运动和音乐的动作，而不是之前的反之 因为无法判断是根据音乐的状态来执行 还是根据运动的状态来执行
             *      所以每一次的运动都由应用层来维持一个状态机
             */
//                mMoveMusicPlay = RunsicService.getInstance().getPlayerStatus();
//                if (!mMoveMusicPlay) {
//
//                    Log.e(TAG, "music play status is " + mMoveMusicPlay + "lastPause is " + lastRunPause);
//                    musicPlayCallback.onMusicGoOn();
//                    aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.stop);
//                    mRunTimer.setBase(SystemClock.elapsedRealtime() + lastRunPause);
//                    mRunTimer.start();
//                    aQuery_.id(R.id.move_stop_button).invisible();
//                } else {
//                    Log.e(TAG, "music play status is " + mMoveMusicPlay + "lastPause is " + lastRunPause);
//                    musicPlayCallback.onMusicPause();
//                    aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.play);
//                    lastRunPause = mRunTimer.getBase() - SystemClock.elapsedRealtime();
//                    mRunTimer.stop();
//
//                    aQuery_.id(R.id.move_stop_button).visible();
//
//                }


            case R.id.music_playing_cover_thumb:
                onStaticFragmentJumpClick();
                break;
            case R.id.music_playing_title:
                onStaticFragmentJumpClick();
                break;
            case R.id.music_playing_artist:
                onStaticFragmentJumpClick();
                break;
            case R.id.list_corner_right:

                // TODO: 25/7/16   Share Component Add here
                break;

            case R.id.bpm_up:
                if (Util.DEBUG) {
                    Log.i(TAG, "BPM UP CLICKED ");
                }
                if (!Util.OFFLINE) {
                    currentTempo = RunsicService.getInstance().currentMusicTempo;
                    currentTempo += 5;
                    if (currentTempo > 190) {
                        currentTempo = 190;
                    }
                    aQuery_.id(R.id.music_control_current_bpm).text("" + currentTempo);
                    playOnTempo(currentTempo);
                } else {
                    boolean ret = musicPlayCallback.onNext();
                    if (!ret) {
                        Toast toast = Toast.makeText(this, "离线歌曲中已经没有更高的节奏", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }


                break;

            case R.id.bpm_down:
                if (Util.DEBUG) {
                    Log.i(TAG, "BPM DOWN CLICKED");
                }
                if (!Util.OFFLINE) {
                    currentTempo = RunsicService.getInstance().currentMusicTempo;
                    currentTempo -= 5;
                    if (currentTempo < 60) {
                        currentTempo = 60;
                    }
                    aQuery_.id(R.id.music_control_current_bpm).text("" + currentTempo);
                    playOnTempo(currentTempo);
                } else {
                    boolean ret = musicPlayCallback.onPrevious();
                    if (!ret) {
                        Toast toast = Toast.makeText(this, "离线歌曲中已经没有更低的节奏", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                break;

            case R.id.move_stop_button:
                stopPlayAndRun();
                break;

            case R.id.music_playing_next:
                Log.e(TAG, "NEXT ONCLICK");
                RunsicService.getInstance().onNext();

            default:

                break;

        }
    }


    /**
     * 调起StaticMusicFragment
     */
    synchronized private void onStaticFragmentJumpClick() {
        onPlayStaticOpen();
//        aQuery_.id(R.id.music_control_panel_move).visibility(View.INVISIBLE);
//        aQuery_.id(R.id.music_control_panel_static).visibility(View.VISIBLE);
    }

    /**
     * NEW 继续音乐与运动
     */
    synchronized private void goingOnMusicAndSport() {
        mRunTimer.setBase(SystemClock.elapsedRealtime() + lastRunPause);
        mRunTimer.start();
        aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.move_pause);
        aQuery_.id(R.id.move_screen_lock).visible().clickable(true).clicked(this);
        aQuery_.id(R.id.move_stop_button).invisible().clickable(false);
        aQuery_.id(R.id.bpm_lock_panel).visible();
        pausedBool = false;
        RunsicService.getInstance().onMusicGoOn();
    }

    /**
     * NEW 暂停音乐与运动
     */
    synchronized private void pauseMusicAndSport() {
        lastRunPause = mRunTimer.getBase() - SystemClock.elapsedRealtime();

        mRunTimer.stop();
        aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.move_play);
        aQuery_.id(R.id.move_screen_lock).invisible().clickable(false);
        aQuery_.id(R.id.move_stop_button).visible().clickable(true).clicked(this);
        aQuery_.id(R.id.bpm_lock_panel).invisible();
        pausedBool = true;
        RunsicService.getInstance().onMusicPause();
    }


    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        Log.e(TAG, "fragmentList size is " + fragmentList.size());
        if (fragmentList == null) {
            goToHomeScreen();
        }
        if (fragmentList != null) {

            boolean hiden = true;
            if (Util.DEBUG) {
                Log.e(TAG, "fragmentList size is " + fragmentList.size());
            }
            for (Fragment fragment : fragmentList) {
                if (fragment != null) {
                    ((OnBackPressedListener) fragment).onBackPressed();
                    hiden = hiden && fragment.isHidden();
                }
            }
            if (hiden == true) {
                goToHomeScreen();
            }
        }

    }


    public void goToHomeScreen() {
        if (Util.DEBUG) {
            Log.e(TAG, "goToHomeScreen");
        }
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public static void setCurrentTempo( int tempo ) {
        currentTempo = tempo;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        manualActivity_ = ActivityManagerWrapper.getCurrentManualActivity();
//        ActivityManagerWrapper.stopManualActivity();
//        ActivityManagerWrapper.removeActivity(manualActivity_);
        EventBus.getDefault().unregister(this);
        mRunTimer.stop();
        mRunTimer = null;
        locationClient.onDestroy();
        RunsicService.getInstance().onDestroy();
    }

    public void stopPlayAndRun() {
        end();
        lastRunPause = 0;
        currentTempo = 0;

        musicPlayCallback.onMusicStop();
        String base64String = Base64.encodeBase64String(SportTracker.getData());

        SharedPreferences sharedPreferences = Util.getUserPreferences();
        RunsicRestClientUsage.getInstance().saveSportToServer(sharedPreferences.getString("token", "579096c3421aa90c9c3596c8"), base64String);

        Record record = new Record();
//        record.duration = (int) manualActivity_.getDuration();
//        record.distance = manualActivity_.getStep();
//        record.song = songSum;

        /**
         * 更新数据库
         */
//        StartActivity.updateRecordDB(record);


//        StartActivity.updateRecordDB(record);
//        Intent intent = new Intent();
//        intent.setClass(this, ShareActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt("duration", record.duration);
//        bundle.putInt("distance", (int) record.distance);
//        bundle.putInt("song", record.song);
//        intent.putExtras(bundle);
//        this.startActivity(intent);
//
//        this.finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBPMEvent( BPMEvent bpmEvent ) {
        Log.e(TAG, "GET MESSAGE STEP IS " + bpmEvent.step + " GET MESSAGE BPM IS " + bpmEvent.bpm);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCurrentMusicEvent( CurrentMusicEvent event ) {
        Music music = event.currentMusic;
        currentMusic = music;
        aQuery_.id(R.id.music_playing_title).text(music.title);
        aQuery_.id(R.id.music_playing_artist).text(music.artist);
        aQuery_.id(R.id.music_control_current_bpm).text("" + music.tempo);
        if (!music.favourite) {
            aQuery_.id(R.id.music_like).background(R.drawable.like);
        } else {
            aQuery_.id(R.id.music_like).background(R.drawable.like_red);
        }
        currentMusicThumb.setImageUrl(music.coverURL, imageLoader);
        final float duration = music.duration;
        setCurrentTempo(music.tempo);

        songSum += 1;
        if (Util.DEBUG) {
            Log.i(TAG, "MUSIC DURATION IS " + duration + "========" + "Timer Period is " + 10000 / duration);
        }
        if (timer != null) {
            timer.cancel();
            timer = new Timer();
        }

        aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.move_pause);
    }

    public void startTrack() {
        SportTracker.start(new Date().getTime());
    }

    public void setType() {
    }

    public void end() {
        SportTracker.end(new Date().getTime());
    }

    public void pause() {
        SportTracker.pause(new Date().getTime());
    }

    public void resume() {
        SportTracker.resume(new Date().getTime());
    }

    public boolean checkBpm( int bpm ) {
        return SportTracker.checkBpm(bpm);
    }


    public Messages.Sport getData() {
        try {
            return Messages.Sport.parseFrom(SportTracker.getData());
        } catch (InvalidProtocolBufferException e) {
            return null;
        }
    }

    private void initLocation() {
        locationClient = new AMapLocationClient(getApplicationContext());
        locationClient.setLocationListener(this);
        AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
        aMapLocationClientOption.setGpsFirst(true);
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClient.setLocationOption(aMapLocationClientOption);
        locationClient.startLocation();
    }

    public void initMotionSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    public void onSensorChanged( SensorEvent event ) {
//        Log.e(TAG, "on Sensor Changed");
        if (SportTracker.pushAcceleration(
                event.timestamp / 1000000, event.values[0], event.values[1], event.values[2])) {
            int bpm = SportTracker.getBpm();
            aQuery_.id(R.id.pulse_number).text("" + bpm);
            pulse.pulse();
            if (SportTracker.checkBpm(currentTempo)) {
                RunsicService.getInstance().playOnTempo(bpm);
            }
        }

    }


    public void registerAccListener() {
        if (sensorManager != null) {
            sensorManager.registerListener(this, accelerometerSensor, 50000);
        }
    }

    public void unregisterAccListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private BroadcastReceiver powerKeyReceiver_ = null;

    /**
     * 防止某些机器锁屏时会中指Acc加速度传感器
     */
    private void registerScreenActionBroadcastReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        powerKeyReceiver_ = new BroadcastReceiver() {
            @Override
            public void onReceive( Context context, Intent intent ) {
                String strAction = intent.getAction();

                if (strAction.equals(Intent.ACTION_SCREEN_OFF)) {
                    // 用于防止某些机型关闭屏幕后关闭加速度计
                    unregisterAccListener();
                    registerAccListener();
                }

                if (strAction.equals(Intent.ACTION_SCREEN_ON)) {
                }
            }
        };

        Util.context().registerReceiver(powerKeyReceiver_, intentFilter);
    }

    private void unregisterScreenActionBroadcastReceiver() {
        try {
            Util.context().unregisterReceiver(powerKeyReceiver_);
        } catch (IllegalArgumentException e) {
            powerKeyReceiver_ = null;
        }
    }
}