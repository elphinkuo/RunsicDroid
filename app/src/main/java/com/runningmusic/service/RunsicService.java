package com.runningmusic.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.telephony.TelephonyManager;

import com.runningmusic.application.WearelfApplication;
import com.runningmusic.db.MusicDB;
import com.runningmusic.music.CurrentMusic;
import com.runningmusic.music.CurrentMusicList;
import com.runningmusic.music.Music;
import com.runningmusic.music.MusicPlayCallback;
import com.runningmusic.music.MusicRequestCallback;
import com.runningmusic.music.PGCMusicList;
import com.runningmusic.network.NetworkMode;
import com.runningmusic.network.http.RunsicRestClient;
import com.runningmusic.network.http.RunsicRestClientUsage;
import com.runningmusic.network.service.NetworkStateReceiver;
import com.runningmusic.network.service.TrafficStatsManager;
import com.runningmusic.network.service.TrafficStatsSetting;
import com.runningmusic.runninspire.BeatsCache;
import com.runningmusic.runninspire.RunningMusicActivity;
import com.runningmusic.runninspire.jni.Runsic;
import com.runningmusic.utils.Constants;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusic.videocache.HttpProxyCacheServer;
import com.runningmusic.videocache.StorageUtils;
import com.runningmusic.videocache.file.Files;
import com.runningmusic.videocache.file.Md5FileNameGenerator;
import com.runningmusiclib.cppwrapper.ActivityManagerWrapper;
import com.runningmusiclib.cppwrapper.ServiceLauncher;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class RunsicService extends Service implements SensorEventListener, Observer, NetworkMode, MusicPlayCallback, MusicRequestCallback {
    private static String TAG = RunsicService.class.getName();

    private static boolean stopMotion = false;
    private SensorManager sensorManager_;
    private Sensor sensor_;
    private MotionTracker mm_;
    private TrafficStatsManager trafficStatsManager_;
    private TrafficStatsSetting trafficStatsSetting_;
    private NetworkStateReceiver networkStateReceiver_;

    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;


    public ArrayList<Music> musicPGCList;
    public PGCMusicList pgcMusicList;
    public CurrentMusicList musicCurrentList;
    public CurrentMusic musicCurrent;
    public int currentMusicTempo = 0;

    private HttpProxyCacheServer proxy;

    private DownloadManager downloadManager;
    private static MusicDB musicDB;
    private Md5FileNameGenerator fileNameGenerator;

    public static final int MSG_PLAY_ONTEMPO = 1;
    public static final int MSG_PLAYLIST_ONTEMPO = 2;
    public static final int MSG_PGC_LIST = 3;
    public static final int MSG_PLAYLIST_ONTEMPO_NEXT = 4;
    public static final int MSG_DOWNLOAD = 5;

    private static final int MESSAGE_UPDATE = 1;
    private static final int MESSAGE_NOTIFICATION = 2;


    public static BeatsCache beatsCache = new BeatsCache();

    @Override
    public void update(Observable observable, Object data) {

        if (Util.DEBUG)
            Log.e(TAG, "current music change======Runsic Service Receive");
        currentMusicTempo = musicCurrent.getCurrentMusic().tempo;
        String musicURL = musicCurrent.getCurrentMusic().audioURL;
        try {
            mediaPlayer.reset();
//            String musicURLCache = proxy.getProxyUrl(musicURL);
//            Log.e(TAG, "clientsCount is " + proxy.getClientsMap());
            if (Util.DEBUG)
                Log.e(TAG, "just set URL");
            mediaPlayer.setDataSource(this, Uri.parse(musicURL));
            if (Util.DEBUG)
                Log.e(TAG, "just");
            mediaPlayer.prepare();
            mediaPlayer.start();
            musicDB.updateDB(musicCurrent.getCurrentMusic());
//            if (Util.DEBUG)
//                Log.e(TAG, "Music Duration is " + mediaPlayer.getDuration());
//            musicCurrent.getCurrentMusic().duration = mediaPlayer.getDuration();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PLAY_ONTEMPO:
                    int tempo = (int) msg.obj;
//                    playOnTempo(tempo);
                    getTempoList(tempo);
                    break;

                case MSG_PLAYLIST_ONTEMPO:
                    int bpm = (int) msg.obj;
                    currentMusicTempo = bpm;
                    musicCurrentList.clear();
                    getTempoList(bpm);
                    break;

                case MSG_DOWNLOAD:
                    if (Util.DEBUG)
                        Log.e(TAG, "Download received");
                    startDownload();
                    break;

            }

        }
    }

    private final BroadcastReceiver runningReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Util.DEBUG)
                Log.i(TAG, "" + intent.getAction());
            if (Constants.STEPSTATE.equals(intent.getAction())) {
                // 处理走路提醒
//				Log.i(TAG, "get the broadcast, step	message");

                StepMessage stepMessage = intent.getParcelableExtra(Constants.MESSAGE_STEPSTATE);
                if (stepMessage != null) {
                    // send update message
//                    Message msg = mHandler.obtainMessage();
//                    msg.what = MESSAGE_NOTIFICATION;
//                    msg.obj = stepMessage;
//                    msg.sendToTarget();
                }

            }

        }
    };

//    public class MyHandler extends Handler {
//        public MyHandler() {
//            super();
//        }
//
//        // 子类必须重写此方法,接受数据
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case MESSAGE_UPDATE:
//                    // 此处可以更新UI
//                    // updateTodayStats();
//                    break;
//                case MESSAGE_NOTIFICATION:
//                    // updateTodayNotification((StepMessage) msg.obj);
//                    updateStepNotification((StepMessage) msg.obj);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

    final Messenger mMessenger = new Messenger(new ServiceHandler());


    @Override
    public IBinder onBind(Intent intent) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.STEPSTATE);
        registerReceiver(runningReceiver, intentFilter);
        return mMessenger.getBinder();
    }


    @Override
    public void onCreate() {
        if (Util.DEBUG)
            Log.e(TAG, "onCreate");

        // 启动流量监控
        startTrafficStats();
        // 创建用户
//		createUser();
        // 初始化c++服务
        ServiceLauncher.launch(Util.context());
        ActivityManagerWrapper.checkStatsOnStart();
        MobclickAgent.openActivityDurationTrack(false);
//        MobclickAgent.updateOnlineConfig(Util.context());
        // 开启运动跟踪
        startMotionTrack();
        // 开启位置跟踪
//		startLocationTrack();
//		int userid = Util.userId();
//		Log.i("QQ_HEALTH", "" + userid);
        // 开始上传DailyStats
//		DailyStatsUploader.getInstance().start();
        // 开始检测屏幕关闭开启
        registerScreenActionBroadcastReceiver();
        // 开始更新pm2.5值
//        PM2d5Manager.getInstance().start();
        // 开始上传达标天数
        instance_ = this;


        initMusicPlayer();


        //init the sensorManger
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);


        //数据初始化
        musicPGCList = new ArrayList<>();

        musicCurrent = new CurrentMusic();
        musicCurrentList = new CurrentMusicList();
        pgcMusicList = new PGCMusicList();

        Util.setContext(this);
        proxy = WearelfApplication.getProxy(this);
        addCurrentMusicObserver(this);

        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        musicDB = new MusicDB();
        fileNameGenerator = new Md5FileNameGenerator();

    }

    public void addCurrentMusicObserver(Observer observer) {
        musicCurrent.addObserver(observer);
    }

    public void addCurrentListObserver(Observer observer) {
        musicCurrentList.addObserver(observer);
    }

    public void addPGCListObserver(Observer observer) {
        pgcMusicList.addObserver(observer);
    }

    public void deleteCurrentMusicObserver(Observer observer) {
        musicCurrent.deleteObserver(observer);
    }

    private void startMotionTrack() {
        initMotionTracker();
        mm_ = MotionTracker.getInstance();
        setActive();
    }

    /**
     * 开始记录位置
     */
    private void startLocationTrack() {
//		locationTracker_ = LocationTracker.getInstance();
//		locationTracker_.startCoarseTrack();
    }

    private void initMotionTracker() {
        sensorManager_ = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_ = sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void createUser() {
        SharedPreferences sPreferences = Util.getUserPreferences();
        String pCodeString = sPreferences.getString(Constants.DEVICE_ID, null);

        if (pCodeString == null) {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            Editor editor = sPreferences.edit();
            String pc = tm.getDeviceId();
            editor.putString(Constants.DEVICE_ID, pc);
            editor.commit();
        }

        RunsicRestClientUsage.getInstance().addUser();
        RunsicRestClientUsage.getInstance().updateinfo();
    }

    private void startTrafficStats() {
        trafficStatsManager_ = TrafficStatsManager.getInstance();
        trafficStatsSetting_ = TrafficStatsSetting.getInstance();
        trafficStatsSetting_.setAlarm();
        networkStateReceiver_ = new NetworkStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver_, filter);
    }

    private void stopTrafficStats() {
        unregisterReceiver(networkStateReceiver_);
        trafficStatsSetting_.cancelAlarm();
        trafficStatsManager_.onAppDestroy();
    }

    public void pauseMotionTracker() {
        sensorManager_.unregisterListener(mm_);
    }

    public void resumeMotionTracker() {
        if (stopMotion) {
            PartialWakeLock.getInstance().releaseWakeLock();
            return;
        }
        registerAccListener();
    }

    public void registerAccListener() {
        if (sensorManager_ != null) {
            sensorManager_.registerListener(mm_, sensor_, 10000);
        }
    }

    public void unregisterAccListener() {
        if (sensorManager_ != null) {
            sensorManager_.unregisterListener(mm_);
        }
    }

    @Override
    public void onDestroy() {
        // Toast.makeText(this, "My Service Stoped", Toast.LENGTH_LONG).show();
        MobclickAgent.onEvent(this, "MOTION_SERVICE_DESTORY");
        if (Util.DEBUG)
            Log.e("RunsicService", "Service onDestroy");

        if (sensorManager_ != null) {
            pauseMotionTracker();
            sensorManager_ = null;
        }

//		if (locationTracker_ != null) {
////			locationTracker_.stopTrack();
//			locationTracker_ = null;
//		}

        if (heartBeatSetting_ != null) {
            heartBeatSetting_.cancelAlarm();
            heartBeatSetting_ = null;
        }

        stopTrafficStats();

        PartialWakeLock.getInstance().releaseWakeLock();

        RunsicRestClient.cancel();

        unregisterScreenActionBroadcastReceiver();
        unregisterReceiver(runningReceiver);
        musicCurrent.deleteObservers();
        // Debug.stopMethodTracing();
        // Log.i("wangyikang", "debug trace start");

    }

    @Override
    public void onStart(Intent intent, int startid) {
        // Toast.makeText(this, "My Service Start", Toast.LENGTH_LONG).show();
        if (Util.DEBUG)
            Log.i("RunsicService", "Service onStart");
        if (instance_ == null) {
            instance_ = this;
        }

        //一种Native Debug方法
        // Debug.startMethodTracing("load", 64*1024*1024);
        // Log.i("wangyikang", "debug trace start");
    }

    public void motionEventInterrupted() {
        MobclickAgent.onEvent(this, "RECORD_INTERRUPTED");
    }

    HeartBeatSetting heartBeatSetting_ = HeartBeatSetting.getInstance();
    private static RunsicService instance_;

    public static RunsicService getInstance() {
        return instance_;
    }

    public void setSleepy() {
        if (Util.DEBUG)
            Log.i("RunsicService", "setSleepy");
        pauseMotionTracker();
        heartBeatSetting_.cancelAlarm();
        heartBeatSetting_.setAlarm();

        PartialWakeLock.getInstance().releaseWakeLock();
    }

    public void setActive() {
        if (EnvironmentDetector.getInstance().isPartialWakeLockAcc()) {
            PartialWakeLock.getInstance().acquireWakeLock(Util.context());
        }
        if (Util.DEBUG)
            Log.i("RunsicService", "setActive");
        heartBeatSetting_.cancelAlarm();
        this.resumeMotionTracker();
    }

    private BroadcastReceiver powerKeyReceiver_ = null;

    private void registerScreenActionBroadcastReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        powerKeyReceiver_ = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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

    public void initMusicPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int index = musicCurrentList.getCurrentMusicList().indexOf(musicCurrent.getCurrentMusic());
                if (index < musicCurrentList.getCurrentMusicList().size()) {
                    musicCurrent.setCurrentMusic(musicCurrentList.getCurrentMusicList().get(index + 1));
                } else {
                    getTempoList(musicCurrent.getCurrentMusic().tempo);
                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                getTempoList(150);
                if (Util.DEBUG)
                    Log.e(TAG, "MediaPlayer on Error");
                return false;
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });



    }



    public void playOnTempo(int tempo) {
        if (Util.DEBUG)
            Log.e(TAG, "PLAY ON TEMPO REQUEST + TEMPO IS " + tempo);
        currentMusicTempo = tempo;
        RunsicRestClientUsage.getInstance().getTempoList(tempo);
    }

    public void motionMusicChange(int tempo) {
        if (!Util.OFFLINE) {
            int bpm = tempo;
            musicCurrentList.getCurrentMusicList().clear();
            getTempoList(bpm);
        } else {
            Music currentM = musicCurrent.getCurrentMusic();
            for (Music music: musicCurrentList.getCurrentMusicList()) {
                if (Math.abs(music.tempo - tempo) < Math.abs(currentM.tempo - tempo)) {
                    currentM = music;
                }

            }
            musicCurrent.setCurrentMusic(currentM);
        }

    }

    public void onLineMusicChange(int tempo) {
        getTempoListNoChangeCuurent(tempo);
    }



    public void playOnTempoCallback(Music music) {
        musicCurrent.setCurrentMusic(music);
    }

    public void getPGCList() {
        musicPGCList.clear();
        musicPGCList = new ArrayList<>();
        RunsicRestClientUsage.getInstance().getPGCList();
    }

    public void addPGCList(Music music) {
        musicPGCList.add(music);
    }

    public void setPGCListChange() {
        if (Util.DEBUG)
            Log.e(TAG, "set PGC CHANGE " + musicPGCList);
        pgcMusicList.setPGCMusicList(musicPGCList);
    }

    public void getTempoList(int tempo) {
        if (Util.DEBUG)
            Log.e(TAG, "GET TEMPO LIST");
        currentMusicTempo = tempo;
        RunningMusicActivity.setCurrentTempo(tempo);
        RunsicRestClientUsage.getInstance().getTempoList(tempo);
    }

    public void getTempoListNoChangeCuurent(int tempo) {
        tempo = currentMusicTempo;
        RunsicRestClientUsage.getInstance().getMoreTempoList(tempo);
    }

    @Override
    public boolean onPlayonTempoListCallback() {
        this.setCurrentMusicObservable();
        return true;
    }

    @Override
    public boolean onPlayonTempoMoreListCallback() {
        this.setCurrentMusicListObservable();
        return true;
    }

    public void addCurrentList(Music music) {
        musicCurrentList.addMusic(music);
    }

    public void setCurrentMusicObservable() {
        if (Util.DEBUG)
            Log.e(TAG, "set CurrentMusicList " + musicCurrentList);
        musicCurrentList.setCurrentMusicList(musicCurrentList.getCurrentMusicList());
        musicCurrent.setCurrentMusic(musicCurrentList.getCurrentMusicList().get(0));
    }

    public void setCurrentMusicListObservable() {
        musicCurrentList.setCurrentMusicList(musicCurrentList.getCurrentMusicList());
        if (!musicCurrentList.getCurrentMusicList().contains(musicCurrent.getCurrentMusic())) {
            musicCurrentList.getCurrentMusicList().add(0, musicCurrent.getCurrentMusic());
        }
    }




    public boolean getPlayerStatus() {
        if (mediaPlayer == null) {
            initMusicPlayer();
            return false;
        } else {
            return mediaPlayer.isPlaying();
        }
    }

    public void startDownload() {
        if (Util.DEBUG)
            Log.e(TAG, "musicPGCList size is================" + musicPGCList.size());
        for (int i = 0; i < musicPGCList.size(); i++) {
            String url = proxy.getProxyUrl(musicPGCList.get(i).audioURL);
//            musicDB.updateDB(music);
        }

    }



//    public void updateStepNotification(StepMessage sm) {
//        switch (sm.notificationType) {
//
//            case StepMessage.NOTIFICATION_NONE:
//
//                break;
//            case StepMessage.NOTIFICATION_FAKE_STEP:
//                if (Util.DEBUG)
//                    Log.i(TAG, "step status" + sm.notificationType);
//                break;
//            case StepMessage.NOTIFICATION_FAKE_STOP:
//                if (Util.DEBUG)
//                    Log.i(TAG, "step status" + sm.notificationType);
//                break;
//            case StepMessage.NOTIFICATION_REAL_STEP:
//                if (Util.DEBUG)
//                    Log.i(TAG, "step status" + sm.notificationType);
//                if (sm.bpm > 250) {
//                    return;
//                }
//                int bpm = Util.getFixedBpm(sm.bpm);
//                beatsCache.add(bpm);
//                if (Util.DEBUG)
//                    Log.i(TAG, "===============================================SO BPM IS " + bpm);
//                float variance = beatsCache.getVariance();
//                if (Util.DEBUG)
//                    Log.i(TAG, "bpm="+bpm+" variance="+variance);
////                如果符合切歌逻辑 则更换播放音乐
//                if (beatsCache.getSwitchable(bpm)) {
//                    if (Util.DEBUG)
//                        Log.i(TAG, "========================================SWITCH");
//                    motionMusicChange(bpm);
//                } else {
//                    if (Util.DEBUG)
//                        Log.i(TAG, "=========================================NO SWITCH");
//                }
//                break;
//            case StepMessage.NOTIFICATION_REAL_STOP:
////                Log.i(TAG, "step status" + sm.notificationType);
//                break;
//            case StepMessage.NOTIFICATION_START_REAL_STEP:
////                Log.i(TAG, "step status" + sm.notificationType);
//                break;
//            default:
//
//                break;
//        }
//
//    }

    public ArrayList<Music> queryCachedMusic(List<String> fileNames) {
        ArrayList<Music> musics = musicDB.queryCachedMusic(fileNames);
        for (Music music: musics) {
            Log.e(TAG, "" + music.title);
        }
        Collections.sort(musics, new Util.TempoComparator());
        return musics;
    }

    @Override
    public void onOffLineMode() {
        Util.OFFLINE = true;
        File file = StorageUtils.getIndividualCacheDirectory(this.getApplicationContext());
        List<String> files = Files.getListFilesName(file);
        ArrayList<Music> musics = queryCachedMusic(files);
        Log.e(TAG, "Cached Music Size is " + musics.size());
        Log.e(TAG, "musics are " + musics);
        this.musicCurrentList.setCurrentMusicList(musics);
    }

    @Override
    public void onOnLineMode() {
        Util.OFFLINE = false;
            onLineMusicChange(musicCurrent.getCurrentMusic().tempo);
    }

    @Override
    public boolean onPrevious() {
        int position = musicCurrentList.getCurrentMusicList().indexOf(musicCurrent.getCurrentMusic());
        Log.e(TAG, "previous position is " + position);
        if (position <= 0 || position >= musicCurrentList.getCurrentMusicList().size()) {
            return false;
        } else {
            musicCurrent.setCurrentMusic(musicCurrentList.getCurrentMusicList().get(position - 1));
            return true;
        }
    }

    @Override
    public boolean onNext() {
        int position = musicCurrentList.getCurrentMusicList().indexOf(musicCurrent.getCurrentMusic());
        Log.e(TAG, "next position is " + position);

        if (position < 0 || position >=(musicCurrentList.getCurrentMusicList().size()-1)) {
            return false;
        } else {
            musicCurrent.setCurrentMusic(musicCurrentList.getCurrentMusicList().get(position + 1));
            return true;
        }

    }

    @Override
    public boolean onMusicPause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMusicGoOn() {
        if (mediaPlayer != null && musicCurrentList.getCurrentMusicList()!=null) {
            if (musicCurrentList.getCurrentMusicList().size()!=0) {
                mediaPlayer.start();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMusicStop() {
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
            return true;
        }
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}