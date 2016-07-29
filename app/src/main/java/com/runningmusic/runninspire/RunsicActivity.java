package com.runningmusic.runninspire;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
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

import java.util.Date;
import java.util.List;
import java.util.Timer;


public class RunsicActivity extends FragmentActivity implements SensorEventListener, AMapLocationListener, View.OnClickListener, StaticMusicPlayFragment.OnStaticMusicPlayFragmentClose, MoveMapFragment.OnMapFragmentClose {

    public String TAG = RunsicActivity.class.getName();

    protected PulseView pulse;

    private AMapLocationClient locationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation lastLocation;
    private static final String ROTATION = "rotation";
    private boolean mIsMapClose = true;
    private boolean mIsPlayStaticClose = true;
    private boolean mMoveMusicPlay = true;
    private static int currentTempo = 0;
    private static int songSum;
    private static boolean pausedBool;
    private static boolean bpmAutoBool;
    private static int runType = 0;
    private static Music currentMusic;
    private static boolean mapCornerStatus;


    private Handler loopHandler;
    private Runnable runnable;


    private static final int ID_OPEN_GPS_DIALOG = 1;
    private static final int ID_WEAK_GPS_DIALOG = 2;
    private static final int ID_COMPLETE_GPS_SHORTDISTANCE_DIALOG = 3;
    private static final int ID_COMPLETE_GPS_LONGDISTANCE_DIALOG = 4;
    private boolean isGPSOpen_ = false;


    private Dialog enterGPSDialog;
    private AlertDialog.Builder enterGPSDialogBuilder;
    private Dialog openGPSDialog;
    private Dialog weakGPSDialog;
    private Dialog completeGPSShortDistanceDialog;
    private Dialog completeGPSLongDistanceDialog;

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
    private Chronometer mRunTimer;

    private static long lastRunPause;

    public static long runTimerBaseSender;


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
        Intent intent = getIntent();
        runType = intent.getIntExtra("run_type", 0);

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
        loopHandler = new Handler();

        lastRunPause = 0;

        mRunTimer.setBase(SystemClock.elapsedRealtime());
        mRunTimer.start();
        musicPlayCallback = RunsicService.getInstance();

    }

    @Override
    protected Dialog onCreateDialog( int id ) {
        Log.e(TAG, "on Create Dialog");
        Dialog dialog = null;

        switch (id) {
            case ID_OPEN_GPS_DIALOG:
                dialog = openGPSDialog(this);
                break;
            case ID_WEAK_GPS_DIALOG:
                dialog = weakGPSDialog(this);
                break;
            case ID_COMPLETE_GPS_SHORTDISTANCE_DIALOG:
                dialog = completeGPSShortDistanceDialog(this);
                break;
            case ID_COMPLETE_GPS_LONGDISTANCE_DIALOG:
                dialog = completeGPSLongDistanceDialog(this);
                break;

        }

        if (dialog != null) {
            Log.i(TAG, dialog.toString());
        } else {
            Log.i(TAG, "dialog = null");
        }

        return dialog;

    }


    /*
     * GPS运动中 检查是否打开了GPS 如果未打开 提示用户打开
     */
    private Dialog openGPSDialog( Context mContext ) {
        openGPSDialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.manual_custom_dialog, null);
        AQuery queryDialog = new AQuery(view);
        queryDialog.id(R.id.title_text).text("未开启GPS").typeface(highNumberTypeface);
        queryDialog.id(R.id.content_text).text("此功能需要打开GPS").typeface(highNumberTypeface).visibility(View.VISIBLE);
        queryDialog.id(R.id.ok_button).text("去打开").typeface(highNumberTypeface).clicked(new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                gpsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(gpsIntent);
            }

        });
        queryDialog.id(R.id.middle_button).visibility(View.GONE);
        queryDialog.id(R.id.cancel_button).text("退出").typeface(highNumberTypeface).clicked(new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                try {

                    // TODO: 27/7/16 不保存运动 退出
//                    loopHandler.removeCallbacks(runnable, null);
//                    manualActivity_ = ActivityManagerWrapper.getCurrentManualActivity();
//                    ActivityManagerWrapper.stopManualActivity();
//                    ActivityManagerWrapper.removeActivity(manualActivity_);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }

        });
        openGPSDialog.setContentView(view);
        openGPSDialog.setCancelable(true);
        return openGPSDialog;

    }

    /*
     * GPS运动中 检查GPS信号是否很弱，如果信号弱 提示用户更换位置
     */
    private Dialog weakGPSDialog( Context mContext ) {
        // AlertDialog.Builder dialogBuilder = new
        // AlertDialog.Builder(mContext);
        // dialogBuilder.setTitle("GPS信号弱").setMessage("您的GPS信号较弱，请在室外使用，避开高楼大厦").setPositiveButton("知道了",
        // new DialogInterface.OnClickListener() {
        //
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // dialog.dismiss();
        // }
        // });

        weakGPSDialog = new Dialog(this, R.style.Dialog_NoBackground);
        View view = LayoutInflater.from(this).inflate(R.layout.manual_custom_dialog, null);
        AQuery queryDialog = new AQuery(view);
        queryDialog.id(R.id.title_text).text("请在室外使用，并尽量避开高大建筑。或确认设备是否可用GPS功能。").typeface(highNumberTypeface);
        queryDialog.id(R.id.content_text).visibility(View.GONE);
        queryDialog.id(R.id.ok_button).text("知道了").typeface(highNumberTypeface).clicked(new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                weakGPSDialog.dismiss();
            }

        });
        queryDialog.id(R.id.middle_button).visibility(View.GONE);
        queryDialog.id(R.id.cancel_button).visibility(View.GONE);

        weakGPSDialog.setContentView(view);
        weakGPSDialog.setCancelable(true);
        return weakGPSDialog;
    }

    /*
     * GPS运动 结束时 距离小于200m的提示或者100步时的提示
     */
    private Dialog completeGPSShortDistanceDialog( Context mContext ) {

        completeGPSShortDistanceDialog = new Dialog(this, R.style.Dialog_NoBackground);
        View view = LayoutInflater.from(this).inflate(R.layout.manual_custom_dialog, null);
        AQuery queryDialog = new AQuery(view);
        queryDialog.id(R.id.title_text).text("运动数据过少, 不再坚持下吗 ?").typeface(highNumberTypeface);
        queryDialog.id(R.id.content_text).visibility(View.GONE);
        queryDialog.id(R.id.ok_button).text("退出").typeface(highNumberTypeface).clicked(new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                try {
                    // TODO: 27/7/16 不保存运动 退出
                    stopPlayAndRunNotSave();

                } catch (Exception e) {
                    e.printStackTrace();

                }

                completeGPSShortDistanceDialog.dismiss();

            }

        });
        queryDialog.id(R.id.middle_button).gone();
        queryDialog.id(R.id.cancel_button).text("继续").typeface(highNumberTypeface).clicked(new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                completeGPSShortDistanceDialog.dismiss();
            }

        });

        completeGPSShortDistanceDialog.setContentView(view);
        completeGPSShortDistanceDialog.setCancelable(true);

        return completeGPSShortDistanceDialog;
    }

    /*
     * GPS运动 结束时 距离大于200M的提示
     */
    private Dialog completeGPSLongDistanceDialog( Context mContext ) {
        completeGPSLongDistanceDialog = new Dialog(this, R.style.Dialog_NoBackground);
        View view = LayoutInflater.from(this).inflate(R.layout.manual_custom_dialog, null);
        AQuery queryDialog = new AQuery(view);
        queryDialog.id(R.id.title_text).text("是否结束本次运动").typeface(highNumberTypeface);
        queryDialog.id(R.id.content_text).visibility(View.GONE);
        queryDialog.id(R.id.ok_button).text("结束").typeface(highNumberTypeface).clicked(new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                MobclickAgent.onEvent(context, "event_save_manual_activity");
                try {
                    stopPlayAndRunSave();
                    // TODO: 27/7/16 保存运动 退出
                } catch (Exception e) {
                    e.printStackTrace();

                }

                completeGPSLongDistanceDialog.dismiss();

            }

        });
        queryDialog.id(R.id.middle_button).visibility(View.GONE);
        queryDialog.id(R.id.cancel_button).text("取消").typeface(highNumberTypeface).clicked(new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                completeGPSLongDistanceDialog.dismiss();
            }

        });

        completeGPSLongDistanceDialog.setContentView(view);
        completeGPSLongDistanceDialog.setCancelable(true);
        return completeGPSLongDistanceDialog;
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

        mRunTimer.setBase(SystemClock.elapsedRealtime() + lastRunPause);
        mRunTimer.start();
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

        boolean ret = checkGPS();
        Log.e(TAG, "ret is " + ret);

        if (!ret) {
            showDialog(ID_OPEN_GPS_DIALOG);
        } else if (ret && (openGPSDialog != null)) {
            dismissDialog(ID_OPEN_GPS_DIALOG);
        }
        checkGPSPermission();

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


//            mMusicPlayTimer.setBase(SystemClock.elapsedRealtime() + lastMusicPause);
//            mMusicPlayTimer.start();
//        }
        MobclickAgent.onResume(this);

        Log.i(TAG, "APP onResume");
//            manualActivity_ = ActivityManagerWrapper.getCurrentManualActivity();
//            Log.i(TAG, "acttivity start time is " + manualActivity_.getStartTime() + "----------" + manualActivity_.getStartLong());
//            Log.e(TAG, "distance is " + manualActivity_.getDistance());
        Log.e(TAG, "on Resume lastRunPause is " + lastRunPause);

        runnable = new Runnable() {

            @Override
            public void run() {
                refresh();
                loopHandler.postDelayed(this, 1000);
                Log.e("", "loop timer test");
            }
        };
        loopHandler.postDelayed(runnable, 1000);
    }

    private void refresh() {

        aQuery_.id(R.id.distance_data).text(String.format("%.2f", SportTracker.getDistance() / 1000));
        aQuery_.id(R.id.pace_data).text(Util.getPaceValue(SportTracker.getSpeed()));
        mapCornerStatus = !mapCornerStatus;
        if (mapCornerStatus) {
            aQuery_.id(R.id.map_corner_icon).background(R.mipmap.icon_map_bright);
        } else {
            aQuery_.id(R.id.map_corner_icon).background(R.mipmap.icon_map_dark);
        }


    }

    private boolean checkGPS() {
        LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (null != locationManager && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            isGPSOpen_ = true;
            Log.i(TAG, "GPSOPEN" + isGPSOpen_);

        } else {
            isGPSOpen_ = false;
            Log.i(TAG, "GPSOPEN" + isGPSOpen_);
        }

        return isGPSOpen_;
    }

    private void checkGPSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        0);
            }
        }
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
        onPlayStaticClose();
        aQuery_.id(R.id.runninspire_header).visible();

//        aQuery_.id(R.id.music_control_panel_static).visibility(View.INVISIBLE);
//        aQuery_.id(R.id.music_control_panel_move).visibility(View.VISIBLE);
    }


    public void onMapOpen() {

        //Header 按钮动画
        mIsMapClose = false;
        Fragment mapFragment = new MoveMapFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("timer", mRunTimer.getBase());
        mapFragment.setArguments(bundle);
        musicListFragmentID = mapFragment.getId();
        FragmentTransaction transactionTop = fragmentManager.beginTransaction();
        transactionTop.setCustomAnimations(R.anim.slidein_fromtop, R.anim.slideout_fromtop, R.anim.slidein_fromtop, R.anim.slideout_fromtop);
        transactionTop.add(R.id.fragment_container_top, mapFragment);
        transactionTop.addToBackStack("MusicList");
        transactionTop.commit();

        aQuery_.id(R.id.runninspire_header).backgroundColor(Color.argb(79, 0, 0, 0));
        //底部进度条与控制栏隐藏
        musicControlMove.setVisibility(View.INVISIBLE);


    }

    public void onMapClose() {

        //Header 按钮动画
        mIsMapClose = true;
        //关闭MusicList Fragment
        fragmentManager.popBackStack();
        //底部进度条与控制栏显示
        aQuery_.id(R.id.runninspire_header).backgroundColor(Color.TRANSPARENT);


        musicControlMove.setVisibility(View.VISIBLE);

    }

    public void onPlayStaticOpen() {
        //打开 StaticMusicPlayFragment
        Fragment musicPlayFragment = new StaticMusicPlayFragment();
        musicStaticListFragmentID = musicPlayFragment.getId();
        FragmentTransaction transactionBottom = fragmentManager.beginTransaction();
        transactionBottom.setCustomAnimations(R.anim.slidein_frombottom_for_activity, R.anim.slideout_frombottom, R.anim.slidein_frombottom_for_activity, R.anim.slideout_frombottom);
//        transactionBottom.add(musicPlayFragment, "StaticMusicPlay");
        transactionBottom.add(R.id.fragment_container_top, musicPlayFragment);
        transactionBottom.addToBackStack("StaticMusicPlay");
        transactionBottom.commit();
        //隐藏HeaderView
        aQuery_.id(R.id.runninspire_header).gone();
//        musicHeader.setVisibility(View.INVISIBLE);
    }

    public void onPlayStaticClose() {
        //关闭 StaticMusicPlayFragment

//        mIsPlayStaticClose = true;
        fragmentManager.popBackStack();

        //显示HeaderView
        aQuery_.id(R.id.runninspire_header).gone();
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
                if (currentMusic == null) {
                    return;
                }
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
                    Log.i(TAG, "click on list corner icon" + mIsMapClose);
                }
                if (mIsMapClose) {
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


            case R.id.move_stop_button:
                boolean checkShortDistance = checkShortDistance();
                if (checkShortDistance) {
                    showDialog(ID_COMPLETE_GPS_SHORTDISTANCE_DIALOG);
                } else {
                    showDialog(ID_COMPLETE_GPS_LONGDISTANCE_DIALOG);
                }
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

    synchronized public void stopPlayAndRunNotSave() {
        end();
        lastRunPause = 0;
        currentTempo = 0;
        musicPlayCallback.onMusicStop();
        SportTracker.reset();
        this.finish();
    }

    synchronized public void stopPlayAndRunSave() {
        end();
        lastRunPause = 0;
        currentTempo = 0;

        musicPlayCallback.onMusicStop();
        String base64String = Base64.encodeBase64String(SportTracker.getData());

        SharedPreferences sharedPreferences = Util.getUserPreferences();
        RunsicRestClientUsage.getInstance().saveSportToServer(sharedPreferences.getString("token", "579096c3421aa90c9c3596c8"), base64String);

        Record record = new Record();
        try {
            Messages.Sport sport = Messages.Sport.parseFrom(SportTracker.getData());

            Intent intent = new Intent();
            intent.setClass(this, RunResultActivity.class);
            if (sport.getExtra().getLocationCount() > 20) {
                intent.putExtra("showmap", true);
            } else {
                intent.putExtra("showmap", false);
            }

            startActivity(intent);

            this.finish();


        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            this.finish();
        }


//        Messages.Sport.parseFrom(SportTracker.getData()).getType() == Messages.Sport.Type.CYCLING;
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

        aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.move_pause);
    }

    public void startTrack() {
        SportTracker.start(new Date().getTime());
    }

    public void setType( int type ) {
        if (type == 1) {
            SportTracker.setType(Messages.Sport.Type.INDOOR_RUNNING.name());
        } else {
            SportTracker.setType(Messages.Sport.Type.RUNNING.name());
        }
    }

    public void end() {
        try {
            SportTracker.end(new Date().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private boolean checkShortDistance() {
        try {
            if (SportTracker.getDistance() == 0 || SportTracker.getStep() == 0) {
                return true;
            } else return SportTracker.getStep() < 5;

        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    @Override
    public void onMapFragmentClose() {
        onMapClose();
    }
}
