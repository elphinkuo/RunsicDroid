package com.runningmusic.runninspire;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.androidquery.AQuery;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.protobuf.InvalidProtocolBufferException;
import com.runningmusic.adapter.GridAdapter;
import com.runningmusic.db.Record;
import com.runningmusic.db.RecordDB;
import com.runningmusic.event.BPMEvent;
import com.runningmusic.event.FavMusicListEvent;
import com.runningmusic.event.LocationChangedEvent;
import com.runningmusic.fragment.MoveMapFragment;
import com.runningmusic.fragment.MusicList;
import com.runningmusic.fragment.OnBackPressedListener;
import com.runningmusic.fragment.StaticMusicPlayFragment;
import com.runningmusic.jni.SportTracker;
import com.runningmusic.music.Music;
import com.runningmusic.music.MusicPlayCallback;
import com.runningmusic.network.NetworkMode;
import com.runningmusic.network.http.RunsicRestClientUsage;
import com.runningmusic.network.service.ImageSingleton;
import com.runningmusic.service.RunsicService;
import com.runningmusic.service.StepMessage;
import com.runningmusic.utils.Constants;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusic.view.ArcProgress;
import com.runningmusic.view.DonutProgress;
import com.runningmusic.view.PulseView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;


public class RunningMusicActivity extends FragmentActivity implements SensorEventListener, AMapLocationListener, View.OnClickListener, View.OnTouchListener, Observer, MusicList.onMusicListCloseListener, StaticMusicPlayFragment.OnStaticMusicPlayFragmentClose {

    public String TAG = RunningMusicActivity.class.getName();

    protected int musicBpm = 126;
    protected PulseView pulse;

    private AMapLocationClient locationClient;
    private AMapLocationClientOption mLocationOption;
//    private OnLocationChangedListener mListener;
    private AMapLocation lastLocation;
    private static final String ROTATION = "rotation";
    private boolean mIsListClose = true;
    private boolean mIsPlayStaticClose = true;
    private boolean mMoveMusicPlay = true;
    private boolean allowMobile = true;
    private static int currentTempo = 0;
    private static int songSum;
    private static int CENTER_BPM=0;
    private static int CENTER_PACE=1;
    private int centerStatus=0;

    private NetworkMode networkMode;
    private MusicPlayCallback musicPlayCallback;

    private static long stopTimeRecord;


    //Animation
    private AnimatorSet bubbleAnimatorSet_;
    private AnimatorSet ShrinkAnimatorSet_;

    private static int musicListFragmentID;
    private static int musicStaticListFragmentID;

    //communicate with RunsicService
    private Messenger serviceMessenger = null;
    private boolean mBound;

    public AQuery aQuery_;

    private TextView distance;
    private TextView distanceUnit;
    private float distanceValue;
    private TextView listCornerIcon;
    private NetworkImageView currentMusicThumb;
    private ImageLoader imageLoader;
    private RelativeLayout musicControlMove;
    private RelativeLayout musicControlStatic;
    private RelativeLayout musicHeader;
    private Chronometer mRunTimer;

    private static long lastRunPause;
    private static long lastMusicPause;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;




    private Typeface highNumberTypeface;

    private FrameLayout containerLayout;

    private FragmentManager fragmentManager;

    private Context context;


    private MyHandler mHandler;
    private static final int MESSAGE_UPDATE = 1;
    private static final int MESSAGE_NOTIFICATION = 2;
    private RecordDB recordDB;
    private DonutProgress donutProgress;

    private AnimatorSet toStopAnimator;
    private AnimatorSet afterStopAnimator;


    //菜单按钮动画
    private Timer timer;
    private Timer stopTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.runnin_spire_layout);

        EventBus.getDefault().register(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        initLocation();
        start();



        aQuery_ = new AQuery(this);
        context = this;
        listCornerIcon = (TextView) findViewById(R.id.list_corner_icon);
        distance = (TextView) findViewById(R.id.distance_data);
        distanceUnit = (TextView) findViewById(R.id.distance_data_unit);
        musicHeader = (RelativeLayout) findViewById(R.id.runninspire_header);
        musicControlMove = (RelativeLayout) findViewById(R.id.music_control_panel_move);
        currentMusicThumb = (NetworkImageView) findViewById(R.id.music_playing_cover_thumb);
        imageLoader = ImageSingleton.getInstance(this).getImageLoader();
        donutProgress = (DonutProgress) findViewById(R.id.donut_progress);

        pulse = (PulseView) findViewById(R.id.pulse_view);
//        pulse.setBpm(126);

        mRunTimer = (Chronometer) findViewById(R.id.time_data);

        fragmentManager = getSupportFragmentManager();

        listCornerIcon.setOnClickListener(this);
        musicControlMove.setOnClickListener(this);

        recordDB = new RecordDB();

        aQuery_.id(R.id.music_move_play_or_pause).clickable(true).clicked(this);
        aQuery_.id(R.id.music_control_panel_static).clickable(true).clicked(this);
        aQuery_.id(R.id.list_corner_right).clickable(true).clicked(this);
        aQuery_.id(R.id.bpm_up).clickable(true).clicked(this);
        aQuery_.id(R.id.bpm_down).clickable(true).clicked(this);
        aQuery_.id(R.id.stop_button).clickable(true).clicked(this);
        aQuery_.id(R.id.bubble_background).clickable(true).clicked(this);
        aQuery_.id(R.id.music_like).clickable(true).clicked(this);
//        stopButton = (Button) this.findViewById(R.id.stop_button);
//        stopButton.setOnTouchListener(this);
        highNumberTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/tradegothicltstdbdcn20.ttf");
        distance.setTypeface(highNumberTypeface);
        distanceUnit.setTypeface(highNumberTypeface);
        mRunTimer.setTypeface(highNumberTypeface);
        aQuery_.id(R.id.pageview_circle_big_tv).typeface(highNumberTypeface);

        lastRunPause = 0;
        lastMusicPause = 0;

//        Intent intent = this.getIntent();
//        if (intent.hasExtra("fromStart")) {
//            if (serviceConnection != null) {
//                currentTempo = 140;
//                playOnTempo(currentTempo);
//            }
//
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            //系统通知栏透明
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            RelativeLayout.LayoutParams headerParams = (RelativeLayout.LayoutParams) musicHeader.getLayoutParams();
            headerParams.setMargins(0, (int) Util.dp2px(this.getResources(), 24), 0, 0);
//            musicHeader.setPadding(0,Util.getStatusBarHeight(this),0,0);
            //系统底部透明
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        mHandler = new MyHandler();

        mRunTimer.setBase(SystemClock.elapsedRealtime());
        mRunTimer.start();
//        ActivityManagerWrapper.startManualActivity(ActivityType.kActivityCommuteRunning);

        networkMode = RunsicService.getInstance();
        musicPlayCallback = RunsicService.getInstance();


    }


    @Override
    public void update(Observable observable, Object data) {

        Music music = RunsicService.getInstance().musicCurrent.getCurrentMusic();
        aQuery_.id(R.id.music_playing_title).text(music.title);
        aQuery_.id(R.id.music_playing_artist).text(music.artist);
        aQuery_.id(R.id.music_control_current_bpm).text("" + music.tempo);
        currentMusicThumb.setImageUrl(music.coverURL, imageLoader);
        final float duration = music.duration;

        songSum += 1;
        if (Util.DEBUG) {
            Log.i(TAG, "MUSIC DURATION IS " + duration + "========" + "Timer Period is " + 10000 / duration);
        }
        if (timer != null) {
            timer.cancel();
            timer = new Timer();
        }



        aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.stop);


    }

    /**
     * PGC MUSIC List 页面的回调
     *
     * @param position
     */
    @Override
    public void onMusicListClose(int position) {
        onListClose();
        if (position != -1) {
            Music currentMusic = RunsicService.getInstance().musicPGCList.get(position);

            RunsicService.getInstance().musicCurrent.setCurrentMusic(currentMusic);
            RunsicService.getInstance().musicCurrentList.setCurrentMusicList(RunsicService.getInstance().musicPGCList);
//            RunsicService.getInstance().musicCurrentList = RunsicService.getInstance().musicPGCList;
        }
    }

    /**
     * Static Music List 页面的回掉
     */
    @Override
    public void onStaticMusicPlayFragmentClose() {
        onPlayStaticClose();
        aQuery_.id(R.id.music_control_panel_static).visibility(View.INVISIBLE);
        aQuery_.id(R.id.music_control_panel_move).visibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.stop_button:


                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stopTimer = new Timer();
                    stopTimeRecord = SystemClock.currentThreadTimeMillis();
                    Log.e(TAG, "" + stopTimeRecord);
                    donutProgress.setProgress(0);
                    stopTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (donutProgress.getProgress() < 100) {
                                        donutProgress.setProgress(donutProgress.getProgress() + 1);
                                    } else {
                                        stopTimer.cancel();
                                        stopRun();
                                    }

                                }
                            });
                        }
                    }, 0, 20);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopTimer.cancel();
                    Log.e(TAG, "" + SystemClock.currentThreadTimeMillis());
                    if ((SystemClock.currentThreadTimeMillis() - stopTimeRecord) > 200) {

                    } else {
                        donutProgress.setProgress(0);
                        afterStopAnimator.start();
                        aQuery_.id(R.id.stop_button).clickable(false);
                        aQuery_.id(R.id.bubble_background).clickable(true);
                        aQuery_.id(R.id.donut_progress).invisible();
                    }
                    return true;
                }


        }
        return false;
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        Log.e(TAG, "location is " + location);
        if (lastLocation == null || lastLocation.getLatitude() != location.getLatitude() || lastLocation.getLongitude() != location.getLongitude()) {
            Toast.makeText(getApplicationContext(), String.format("%f, %f, %.0f",
                    location.getLatitude(), location.getLongitude(), location.getAccuracy()), Toast.LENGTH_SHORT).show();


            SportTracker.pushLocation(new Date().getTime(), location.getLatitude(), location.getLongitude(), location.getSpeed());
            lastLocation = location;

            SportTracker.getDistance();
            SportTracker.getSpeed();

            Log.e(TAG, "LOCATION CHANGE DISTANCE IS " + SportTracker.getDistance() + " SPEED IS " + SportTracker.getSpeed());
            EventBus.getDefault().post(new LocationChangedEvent(SportTracker.getDistance(), SportTracker.getSpeed()));

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    @Override
//    public void activate( OnLocationChangedListener onLocationChangedListener ) {
//        mListener = onLocationChangedListener;
//        if (locationClient == null) {
//            locationClient = new AMapLocationClient(this);
//            mLocationOption = new AMapLocationClientOption();
//            //设置定位监听
//            locationClient.setLocationListener(this);
//            //设置为高精度定位模式
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//            //设置定位参数
//            locationClient.setLocationOption(mLocationOption);
//            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//            // 在定位结束后，在合适的生命周期调用onDestroy()方法
//            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//            locationClient.startLocation();
//        }
//    }
//
//    @Override
//    public void deactivate() {
//        mListener = null;
//        if (locationClient != null) {
//            locationClient.stopLocation();
//            locationClient.onDestroy();
//        }
//        locationClient = null;
//    }

//    @Override
//    public boolean onLongClick(View v) {
//        switch(v.getId()) {
//            case R.id.stop_button:
//                stopTimer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                donutProgress.setProgress(donutProgress.getProgress() + 1);
//                            }
//                        });
//                    }
//                }, 0, 10);
//
//
//                return true;
//
//        }
//        return false;
//    }


    public class MyHandler extends Handler {
        public MyHandler() {
            super();
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_UPDATE:
                    // 此处可以更新UI
                    // updateTodayStats();
                    break;
                case MESSAGE_NOTIFICATION:
                    // updateTodayNotification((StepMessage) msg.obj);
//                    updateStepNotification((StepMessage) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

    private final BroadcastReceiver runningReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Util.DEBUG) {
                Log.i(TAG, "" + intent.getAction());
            }
            if (Constants.STEPSTATE.equals(intent.getAction())) {
                // 处理走路提醒
//				Log.i(TAG, "get the broadcast, step	message");

                StepMessage stepMessage = intent.getParcelableExtra(Constants.MESSAGE_STEPSTATE);
                if (stepMessage != null) {
                    // send update message
                    Message msg = mHandler.obtainMessage();
                    msg.what = MESSAGE_NOTIFICATION;
                    msg.obj = stepMessage;
                    msg.sendToTarget();
                }

            }
//            else if (MYACTION.equals(intent.getAction())) {
//				Log.i(TAG, "get the broadcast, stats refresh");
//
//                Message msg = mHandler.obtainMessage();
//                msg.what = MESSAGE_UPDATE;
//                msg.sendToTarget();
//            }

        }
    };


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            mBound = true;

            if (currentTempo != 0) {
//                playOnTempo(currentTempo);
            } else {
                currentTempo = 126;
//                        (new Random().nextInt()) % 100 + 90;
                playOnTempo(currentTempo);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
            mBound = false;
        }
    };




    @Override
    protected void onStart() {
        super.onStart();
        if (Util.DEBUG) {
            Log.e(TAG, "onStart");
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        bindService(new Intent(this, RunsicService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RunningMusic Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.runningmusic.runninspire/http/host/path")
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.DEBUG) {
            Log.e(TAG, "onResume");
        }

//        mMoveMusicPlay = RunsicService.getInstance().getPlayerStatus();
//        if (!mMoveMusicPlay) {
//            Log.e(TAG, "music play status is " + mMoveMusicPlay + "lastPause is " + lastRunPause);

        musicPlayCallback.onMusicGoOn();

        if (RunsicService.getInstance().musicCurrent.getCurrentMusic() != null) {
            Music music = RunsicService.getInstance().musicCurrent.getCurrentMusic();
            aQuery_.id(R.id.music_playing_title).text(music.title);
            aQuery_.id(R.id.music_playing_artist).text(music.artist);
            aQuery_.id(R.id.music_control_current_bpm).text("" + music.tempo);
            currentMusicThumb.setImageUrl(music.coverURL, imageLoader);
        }

//            mRunTimer.setBase(SystemClock.elapsedRealtime() + lastRunPause);
//            mRunTimer.start();
//            mMusicPlayTimer.setBase(SystemClock.elapsedRealtime() + lastMusicPause);
//            mMusicPlayTimer.start();
//        }
        MobclickAgent.onResume(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.STEPSTATE);
        intentFilter.addAction(Constants.SONG_CHANGED_ON_TEMPLE);
        registerReceiver(runningReceiver, intentFilter);
        if (Util.DEBUG) {
            Log.i(TAG, "APP onResume");
//            manualActivity_ = ActivityManagerWrapper.getCurrentManualActivity();
//            Log.i(TAG, "acttivity start time is " + manualActivity_.getStartTime() + "----------" + manualActivity_.getStartLong());
//            Log.e(TAG, "distance is " + manualActivity_.getDistance());
            Log.e(TAG, "on Resume lastRunPause is " + lastRunPause);
        }

//        if (lastRunPause == 0) {
//        manualActivity_ = ActivityManagerWrapper.getCurrentManualActivity();

//        mRunTimer.setBase(SystemClock.elapsedRealtime() + (long)manualActivity_.getDuration());
//        } else {
//            mRunTimer.setBase(SystemClock.elapsedRealtime() - lastRunPause + mRunTimer.getBase());
//        }
//        mRunTimer.start();

        RunsicService.getInstance().addCurrentMusicObserver(this);

        if (RunsicService.getInstance().getPlayerStatus()) {
            aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.stop);
        } else {
            aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.play);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.DEBUG) {
            Log.e(TAG, "onPause");
        }
        MobclickAgent.onPause(this);
        this.unregisterReceiver(runningReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.DEBUG) {
            Log.e(TAG, "onStop");
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RunningMusic Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.runningmusic.runninspire/http/host/path")
        );
        if (Util.DEBUG) {
            Log.i(TAG, "ON STOP");
        }
        if (mBound) {
            unbindService(serviceConnection);
            mBound = false;
        }
        lastRunPause = SystemClock.elapsedRealtime();

        if (Util.DEBUG) {
            Log.e(TAG, "lastRunPause is " + lastRunPause);
        }
        RunsicService.getInstance().deleteCurrentMusicObserver(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

//    public void updateStepNotification(StepMessage sm) {
//
////        distance.setText(""+ActivityManagerWrapper.getCurrentManualActivity().getStep());
//        switch (sm.notificationType) {
//
//            case StepMessage.NOTIFICATION_NONE:
//                step();
//                break;
//            case StepMessage.NOTIFICATION_FAKE_STEP:
//                step();
//                int fakeBpm = currentTempo - 3 + (new Random().nextInt()) % 6;
//                aQuery_.id(R.id.pageview_circle_big_tv).text("" + fakeBpm);
//                if (Util.DEBUG) {
//                    Log.i(TAG, "step status" + sm.notificationType);
//                }
//                break;
//            case StepMessage.NOTIFICATION_FAKE_STOP:
//                step();
//                if (Util.DEBUG) {
//                    Log.i(TAG, "step status" + sm.notificationType);
//                }
//                break;
//            case StepMessage.NOTIFICATION_REAL_STEP:
//                if (Util.DEBUG) {
//                    Log.i(TAG, "step status" + sm.notificationType);
//                }
//                int bpm = Util.getFixedBpm(sm.bpm);
//                aQuery_.id(R.id.pageview_circle_big_tv).text("" + bpm);
//                step();
//                break;
//            case StepMessage.NOTIFICATION_REAL_STOP:
//                stop();
//                if (Util.DEBUG) {
//
//                }
//                aQuery_.id(R.id.pageview_circle_big_tv).text("" + currentTempo);
//                break;
//            case StepMessage.NOTIFICATION_START_REAL_STEP:
//                if (Util.DEBUG) {
//                    Log.i(TAG, "step status" + sm.notificationType);
//                }
//                step();
//                break;
//            default:
//                break;
//        }
//
//    }

    private void playOnTempo(int tempo) {
        if (Util.DEBUG) {
            Log.e(TAG, "PLAY=ON=TEMPO========" + tempo);
        }
        if (!mBound) {
            if (Util.DEBUG) {
                Log.e(TAG, "not bound");
            }
            return;
        }
        currentTempo = tempo;
        Message msg = Message.obtain(null, RunsicService.MSG_PLAY_ONTEMPO, tempo);

        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void playListOnTempo(int tempo) {
        if (!mBound) return;
        currentTempo = tempo;
        aQuery_.id(R.id.pageview_circle_big_tv).text("" + tempo);

        Message msg = Message.obtain(null, RunsicService.MSG_PLAYLIST_ONTEMPO, tempo);
        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onListOpen() {

        //Header 按钮动画
        mIsListClose = false;

        // 打开MuiscList Fragment
//        Fragment listFragment = new MusicList();
//        musicListFragmentID = listFragment.getId();
//        FragmentTransaction transactionTop = fragmentManager.beginTransaction();
//        transactionTop.setCustomAnimations(R.anim.slidein_fromtop, R.anim.slideout_fromtop, R.anim.slidein_fromtop, R.anim.slideout_fromtop);
//        transactionTop.add(R.id.fragment_container_top, listFragment);
//        transactionTop.addToBackStack("MusicList");
//        transactionTop.commit();

        Fragment mapFragment = new MoveMapFragment();
        musicListFragmentID = mapFragment.getId();
        FragmentTransaction transactionTop = fragmentManager.beginTransaction();
        transactionTop.setCustomAnimations(R.anim.slidein_fromtop, R.anim.slideout_fromtop, R.anim.slidein_fromtop, R.anim.slideout_fromtop);
        transactionTop.add(R.id.fragment_container_top, mapFragment);
        transactionTop.addToBackStack("MusicList");
        transactionTop.commit();


        //底部进度条与控制栏隐藏
        musicControlMove.setVisibility(View.INVISIBLE);

        aQuery_.id(R.id.list_corner_right).invisible();

    }

    public void onListClose() {

        //Header 按钮动画
        mIsListClose = true;

        //关闭MusicList Fragment
        fragmentManager.popBackStack();

        //底部进度条与控制栏显示

        musicControlMove.setVisibility(View.VISIBLE);

        aQuery_.id(R.id.list_corner_right).visible();

    }

    public void onPlayStaticOpen() {
        //打开 StaticMusicPlayFragment
        mIsPlayStaticClose = false;
        Fragment musicPlayFragment = new StaticMusicPlayFragment();
        musicStaticListFragmentID = musicPlayFragment.getId();
        FragmentTransaction transactionBottom = fragmentManager.beginTransaction();
        transactionBottom.setCustomAnimations(R.anim.slidein_frombottom_for_activity, R.anim.slideout_frombottom, R.anim.slidein_frombottom_for_activity, R.anim.slideout_frombottom);
        transactionBottom.add(R.id.fragment_container_top, musicPlayFragment);
        transactionBottom.addToBackStack("StaticMusicPlay");
        transactionBottom.commit();

        //隐藏HeaderView
        musicHeader.setVisibility(View.INVISIBLE);
    }

    public void onPlayStaticClose() {
        //关闭 StaticMusicPlayFragment

        mIsPlayStaticClose = true;
        fragmentManager.popBackStack();

        //显示HeaderView
        musicHeader.setVisibility(View.VISIBLE);
    }

//
//    private void fadeInFullCircle() {
//
////        bubbleAnimatorSet_.start();
//
////        ObjectAnimator.ofFloat(aQuery_.id(R.id.circle_image_full).getView(), "alpha", 1.0f, 0.3f).setDuration(250).start();
//    }

    private void fadeOutFullCircle() {
//        ObjectAnimator.ofFloat(aQuery_.id(R.id.circle_image_full).getView(), "alpha", 1.0f, 0.3f).setDuration(250).start();
    }

//    private void step() {
//        fadeInFullCircle();
//    }
//
//    private void stop() {
////        bubbleAnimatorSet_.cancel();
//    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putLong("lastRunPause", lastRunPause);
//        savedInstanceState.putLong("lastMusicPause", lastMusicPause);
    }

    @Override
    public void onClick(View v) {
        if (Util.DEBUG) {
            Log.i(TAG, "" + v.getId());
        }
        switch (v.getId()) {
            case R.id.music_like:
                SharedPreferences sharedPreferences = Util.getUserPreferences();
                if (sharedPreferences.contains("token")) {
                    RunsicRestClientUsage.getInstance().getFavMusic(sharedPreferences.getString("token", "579096c3421aa90c9c3596c8"));
                } else {
                    RunsicRestClientUsage.getInstance().getFavMusic("579096c3421aa90c9c3596c8");
                }            case R.id.list_corner_icon:
                if (Util.DEBUG) {
                    Log.i(TAG, "click on list corner icon" + mIsListClose);
                }
                if (mIsListClose) {
                    onListOpen();
                } else {
                    onListClose();
                }

                break;

            case R.id.music_move_play_or_pause:
                mMoveMusicPlay = RunsicService.getInstance().getPlayerStatus();
                if (!mMoveMusicPlay) {


                    Log.e(TAG, "music play status is " + mMoveMusicPlay + "lastPause is " + lastRunPause);
                    musicPlayCallback.onMusicGoOn();
                    aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.stop);
                    mRunTimer.setBase(SystemClock.elapsedRealtime() + lastRunPause);
                    mRunTimer.start();
                    aQuery_.id(R.id.stop_button).invisible();
                } else {
                    Log.e(TAG, "music play status is " + mMoveMusicPlay + "lastPause is " + lastRunPause);
                    musicPlayCallback.onMusicPause();
                    aQuery_.id(R.id.music_move_play_or_pause).background(R.mipmap.play);
                    lastRunPause = mRunTimer.getBase() - SystemClock.elapsedRealtime();
                    mRunTimer.stop();

                    aQuery_.id(R.id.stop_button).visible();

                }

                break;

            case R.id.music_control_panel_move:
                if (Util.DEBUG) {
                    Log.i(TAG, "music_control_panel_move onClick");
                }

                if (RunsicService.getInstance().musicCurrentList.getCurrentMusicList().size() == 0) {
                    Log.e(TAG, "Music Current List size is 0!!!");
                    return;
                }
                if (mIsPlayStaticClose) {
                    onPlayStaticOpen();
                    aQuery_.id(R.id.music_control_panel_move).visibility(View.INVISIBLE);
                    aQuery_.id(R.id.music_control_panel_static).visibility(View.VISIBLE);
                } else {
                    onPlayStaticClose();
                    aQuery_.id(R.id.music_control_panel_static).visibility(View.INVISIBLE);
                    aQuery_.id(R.id.music_control_panel_move).visibility(View.VISIBLE);
                }

                break;
            case R.id.music_control_panel_static:

                if (mIsPlayStaticClose) {
                    onPlayStaticOpen();
                    aQuery_.id(R.id.music_control_panel_move).visibility(View.INVISIBLE);
                    aQuery_.id(R.id.music_control_panel_static).visibility(View.VISIBLE);
                } else {
                    onPlayStaticClose();
                    aQuery_.id(R.id.music_control_panel_static).visibility(View.INVISIBLE);
                    aQuery_.id(R.id.music_control_panel_move).visibility(View.VISIBLE);
                }

                break;

            case R.id.list_corner_right:
                if (mIsListClose) {
                    if (allowMobile) {
                        allowMobile = false;
                        if (Util.wifiConnectivity == true) {
                            aQuery_.id(R.id.list_corner_right).background(R.mipmap.off_line);
                            Toast toast = Toast.makeText(this, "移动流量已关闭，切换至Wifi网络", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            aQuery_.id(R.id.list_corner_right).background(R.mipmap.off_line);
                            Toast toast = Toast.makeText(this, "移动流量已关闭，离线曲库迅速加入接力", Toast.LENGTH_SHORT);
                            networkMode.onOffLineMode();
                            toast.show();
                        }
                    } else {
                        allowMobile = true;
                        aQuery_.id(R.id.list_corner_right).background(R.mipmap.on_line);
                        Toast toast = Toast.makeText(this, "移动流量已开启，线上曲库续航自由无碍", Toast.LENGTH_SHORT);
                        toast.show();
                        if (Util.mobileConnectivity == true) {
                            networkMode.onOnLineMode();
                        } else if (Util.mobileConnectivity == false && Util.mobileConnectivity == false) {
                            Toast toast1 = Toast.makeText(this, "网络无连接 离线模式", Toast.LENGTH_SHORT);
                            networkMode.onOffLineMode();
                            toast1.show();
                        }

                    }
                } else {
                    if (Util.DEBUG) {
                        Log.i(TAG, "list corner right clicked");
                    }
                    Toast toast = Toast.makeText(this, "私人唱片馆 离线更新中", Toast.LENGTH_SHORT);
                    toast.show();

                    Message msg = Message.obtain(null, RunsicService.MSG_DOWNLOAD);

                    try {
                        serviceMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

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
                    playListOnTempo(currentTempo);
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
                    playListOnTempo(currentTempo);
                } else {
                    boolean ret = musicPlayCallback.onPrevious();
                    if (!ret) {
                        Toast toast = Toast.makeText(this, "离线歌曲中已经没有更低的节奏", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                break;

            case R.id.stop_button:


                afterStopAnimator.start();
                aQuery_.id(R.id.stop_button).clickable(false);
                aQuery_.id(R.id.bubble_background).clickable(true);
                break;

            case R.id.bubble_background:
                Log.e(TAG, "bubble background on Click");

                toStopAnimator.start();
                aQuery_.id(R.id.stop_button).visible().clickable(true);
                aQuery_.id(R.id.bubble_background).clickable(false);
                aQuery_.id(R.id.donut_progress).visible();


                break;

            default:

                break;

        }
    }


    public void showMovePanel() {
        musicControlMove.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
//        Log.e(TAG, "fragmentList size is " + fragmentList.size());
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

    public static void setCurrentTempo(int tempo) {
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
    }

    public void stopRun() {
        lastRunPause = 0;
        currentTempo = 0;
        if (mBound) {
            unbindService(serviceConnection);
            mBound = false;
        }
//        ActivityManagerWrapper.stopManualActivity();
//        manualActivity_ = ActivityManagerWrapper.getCurrentManualActivity();
//        ActivityManagerWrapper.storeActivity(manualActivity_);

//                Record record = new Record();
//                record.duration = manualActivity_.getWalkingDuration()
//                StartActivity.updateDB()
//        Log.e(TAG, "duration is " + manualActivity_.getDuration());
//        Log.e(TAG, "Walking duration is " + manualActivity_.getWalkingDuration());

        musicPlayCallback.onMusicStop();

        Record record = new Record();
//        record.duration = (int) manualActivity_.getDuration();
//        record.distance = manualActivity_.getStep();
        record.song = songSum;

        /**
         * 更新数据库
         */
//        StartActivity.updateRecordDB(record);


//        StartActivity.updateRecordDB(record);
        Intent intent = new Intent();
        intent.setClass(this, ShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("duration", record.duration);
        bundle.putInt("distance", (int) record.distance);
        bundle.putInt("song", record.song);
        intent.putExtras(bundle);
        this.startActivity(intent);

        this.finish();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBPMEvent(BPMEvent bpmEvent) {
        Log.e(TAG, "GET MESSAGE STEP IS " + bpmEvent.step + " GET MESSAGE BPM IS " + bpmEvent.bpm);
//        favMusicList = favMusicListEvent.musicFavList;
//        gridAdapter = new GridAdapter(context, R.layout.horizontal_item_recycler, favMusicList);
//        favRecyclerView.setAdapter(gridAdapter);
    }

    public void start() {
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

    public boolean checkBpm(int bpm) {
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
        locationClient.startLocation();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.e(TAG, "" + event);
        if (SportTracker.pushAcceleration(
                event.timestamp / 1000000, event.values[0], event.values[1], event.values[2])) {
//            onStep(SportTracker.getStep(), SportTracker.getBpm());
            int bpm = SportTracker.getBpm();
//            Log.e(TAG, "step is "+SportTracker.getStep() + " bpm is " + SportTracker.getBpm());
            aQuery_.id(R.id.pulse_number).text(""+bpm);
            if (SportTracker.checkBpm(RunsicService.getInstance().currentMusicTempo)) {
                RunsicService.getInstance().playOnTempo(bpm);
            }
//            if (centerStatus==0) {
//                aQuery_.id(R.id.pulse_number).text(SportTracker.getBpm());
//            } else {
//
//            }

            //传当前歌曲的BPM
//            SportTracker.checkBpm()
        }

//        if (Runsic.getInstance().sample(event)) {
//            Log.e(TAG, "SENSOR CHANGED");
//            int bpm = Runsic.getInstance().getBpm();
//            Log.i(TAG, "===============================================Qiuxiang BPM IS " + bpm);
//            if (bpm != 0 && Runsic.getInstance().isReady(currentMusicTempo)) {
//                Log.i(TAG, "切换到 " + bpm + " BPM");
//                Runsic.getInstance().getBpm();
//                motionMusicChange(bpm);
//                currentMusicTempo = bpm;
//            }
//        }
    }
}
