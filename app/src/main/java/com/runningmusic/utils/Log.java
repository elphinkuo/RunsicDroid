package com.runningmusic.utils;

import android.content.pm.ApplicationInfo;

/**
 * Created by guofuming on 18/1/16.
 */
public class Log {
//    public static final boolean LOG = !((Util.context().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0);
    public static final boolean LOG = true;

    public static void i(String tag, String string) {
        if (LOG) {
            android.util.Log.i(tag, string);
        }
    }

    public static void e(String tag, String string) {
        if (LOG) {
            android.util.Log.e(tag, string);
        }
    }

    public static void d(String tag, String string) {
        if (LOG) {
            android.util.Log.d(tag, string);
        }
    }

    public static void v(String tag, String string) {
        if (LOG) {
            android.util.Log.v(tag, string);
        }
    }

    public static void w(String tag, String string) {
        if (LOG) {
            android.util.Log.w(tag, string);
        }
    }

    public static void e(String tag, String string, Exception e) {
        if (LOG) {
            android.util.Log.w(tag, string, e);
        }
    }
}
