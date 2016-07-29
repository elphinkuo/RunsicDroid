package com.runningmusic.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.runningmusic.db.MusicDB;
import com.runningmusic.event.CurrentListEvent;
import com.runningmusic.event.CurrentMusicEvent;
import com.runningmusic.event.TempoListResult;
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
import com.runningmusic.player.AudioPlayer;
import com.runningmusic.runninspire.BeatsCache;
import com.runningmusic.runninspire.RunsicActivity;
import com.runningmusic.utils.Constants;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusic.videocache.HttpProxyCacheServer;
import com.runningmusic.videocache.StorageUtils;
import com.runningmusic.videocache.file.Files;
import com.runningmusic.videocache.file.Md5FileNameGenerator;
import com.runningmusic.view.Blur;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class RunsicService extends Service implements MusicPlayCallback, MusicRequestCallback {
    private static String TAG = RunsicService.class.getName();

    private static boolean stopMotion = false;
    private SensorManager sensorManager_;
    private Sensor sensor_;
    private NetworkStateReceiver networkStateReceiver_;

    private MediaPlayer mediaPlayer;

    private AudioPlayer mediaPlayerNew;

    public CurrentMusicList musicCurrentList;
    public CurrentMusic musicCurrent;
    public int currentMusicTempo = 0;

    private static MusicDB musicDB;
    private Md5FileNameGenerator fileNameGenerator;


    @Override
    public void onCreate() {
        if (Util.DEBUG)
            Log.e(TAG, "onCreate");
        EventBus.getDefault().register(this);
        instance_ = this;
        initMusicPlayer();
        //init the sensorManger

        //数据初始化

        musicCurrent = new CurrentMusic();
        musicCurrentList = new CurrentMusicList();

        Util.setContext(this);

        musicDB = new MusicDB();
        fileNameGenerator = new Md5FileNameGenerator();



    }


//    }

    @Override
    public void onDestroy() {
        // Toast.makeText(this, "My Service Stoped", Toast.LENGTH_LONG).show();
        MobclickAgent.onEvent(this, "MOTION_SERVICE_DESTORY");
        if (Util.DEBUG)
            Log.e("RunsicService", "Service onDestroy");
        PartialWakeLock.getInstance().releaseWakeLock();

        EventBus.getDefault().unregister(this);
        RunsicRestClient.cancel();


    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
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
//        pauseMotionTracker();
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
//        this.resumeMotionTracker();
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


        mediaPlayerNew = new AudioPlayer();
        mediaPlayerNew.setOnCompletionListener(new AudioPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                int index = musicCurrentList.getCurrentMusicList().indexOf(musicCurrent.getCurrentMusic());
                if (index < musicCurrentList.getCurrentMusicList().size()) {
                    musicCurrent.setCurrentMusic(musicCurrentList.getCurrentMusicList().get(index + 1));
                } else {
                    getTempoList(musicCurrent.getCurrentMusic().tempo);
                }
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





    public void playOnTempoCallback(Music music) {
        musicCurrent.setCurrentMusic(music);
    }

    public void getTempoList(int tempo) {
        if (Util.DEBUG)
            Log.e(TAG, "GET TEMPO LIST");
        currentMusicTempo = tempo;
        RunsicActivity.setCurrentTempo(tempo);
        RunsicRestClientUsage.getInstance().getTempoList(tempo);
    }

//    public void getTempoListNoChangeCuurent(int tempo) {
//        tempo = currentMusicTempo;
//        RunsicRestClientUsage.getInstance().getMoreTempoList(tempo);
//    }

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

//    public ArrayList<Music> queryCachedMusic(List<String> fileNames) {
//        ArrayList<Music> musics = musicDB.queryCachedMusic(fileNames);
//        for (Music music: musics) {
//            Log.e(TAG, "" + music.title);
//        }
////        Collections.sort(musics, new Util.TempoComparator());
//        return musics;
//    }

//    @Override
//    public void onOffLineMode() {
//        Util.OFFLINE = true;
//        File file = StorageUtils.getIndividualCacheDirectory(this.getApplicationContext());
//        List<String> files = Files.getListFilesName(file);
//        ArrayList<Music> musics = queryCachedMusic(files);
//        Log.e(TAG, "Cached Music Size is " + musics.size());
//        Log.e(TAG, "musics are " + musics);
//        this.musicCurrentList.setCurrentMusicList(musics);
//    }
//
//    @Override
//    public void onOnLineMode() {
//        Util.OFFLINE = false;
//            onLineMusicChange(musicCurrent.getCurrentMusic().tempo);
//    }

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
//            musicCurrent.setCurrentMusic(musicCurrentList.getCurrentMusicList().get(position + 1));
            EventBus.getDefault().post(new CurrentMusicEvent(musicCurrentList.getCurrentMusicList().get(position + 1)));
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


    @Subscribe
    public void onTempoListResult(TempoListResult result){
        this.musicCurrentList.setCurrentMusicList(result.musicArrayList);
        EventBus.getDefault().post(new CurrentListEvent(result.musicArrayList));
    }

    @Subscribe
    public void onCurrentMusicEvent(CurrentMusicEvent musicEvent) {
        this.musicCurrent.setCurrentMusic(musicEvent.currentMusic);
        this.currentMusicTempo = musicEvent.currentMusic.tempo;
        playMusic(musicEvent.currentMusic);
    }


    public void playMusic(Music music) {
        if (Util.DEBUG)
            Log.e(TAG, "current music change======Runsic Service Receive");
        currentMusicTempo = music.tempo;
        String musicURL = music.audioURL;

        try {
            mediaPlayer.reset();
            if (Util.DEBUG)
                Log.e(TAG, "just set URL "+ musicURL);
            mediaPlayer.setDataSource(this, Uri.parse(musicURL));
            if (Util.DEBUG)
                Log.e(TAG, "just");
            mediaPlayer.prepare();
            mediaPlayer.start();

            //数据库相关
            musicDB.updateDB(music);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}