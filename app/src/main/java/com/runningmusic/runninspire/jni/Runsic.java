package com.runningmusic.runninspire.jni;

import android.hardware.SensorEvent;

import com.runningmusic.utils.Log;

/**
 * Created by guofuming on 16/5/16.
 */
public class Runsic {

    static {
        System.loadLibrary("runsic");
    }

    private static Runsic singleton;


    public synchronized static Runsic getInstance() {
        if (singleton == null)
            singleton = new Runsic();
        return singleton;
    }
    private native void _init(int sampleRate);

    private native boolean _sample(long current, float x, float y, float z);

    private native int _getBpm();

    private native float _getEnergy();

    private native boolean _isReady(int bpm);

    /**
     * 进行数据采集并检查是否有跳动
     */
    public boolean sample(SensorEvent event) {
        return _sample(event.timestamp / 1000000, event.values[0], event.values[1], event.values[2]);
    }

    /**
     * 获取最近的 BPM
     */
    public int getBpm() {
        return _getBpm();
    }

    public float getEnergy() {
        return _getEnergy();
    }

    public boolean isReady(int bpm) {
        Log.e("Runsic", "judge ready or not========" + bpm);
        return _isReady(bpm);
    }

    public Runsic() {
        _init(50);
    }
}
