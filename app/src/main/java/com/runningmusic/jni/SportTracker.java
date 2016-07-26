package com.runningmusic.jni;

/**
 * Created by guofuming on 21/7/16.
 */
public class SportTracker {

    static {
        System.loadLibrary("runsic");
    }

    public static native boolean pushAcceleration(long timestamp, float x, float y, float z);
    public static native boolean pushLocation(long timestamp, double latitude, double longitude, float acc, float speed);
    public static native double getSpeed();
    public static native double getDistance();
    public static native byte[] getData();
    public static native byte[] getExtra();
    public static native int getBpm();
    public static native int getStep();
    public static native void start(long timestamp);
    public static native void end(long timestamp);
    public static native void pause(long timestamp);
    public static native void resume(long timestamp);
    public static native boolean checkBpm(int bpm);
    public static native void reset();
    public static native void setType(String type);

}
