package com.happycyclerserver.util;

import android.util.Log;

import com.happycyclerserver.app.BuildConfig;

public class Logger {

    private final static String TAG_PREFIX = "- HappyCyclerServer - ";


    public static void v(Object caller, String msg) {
        Log.d(TAG_PREFIX + caller.getClass().getSimpleName(), msg);
    }

    public static void e(Object caller, String msg) {
        msg = Thread.currentThread().getName() + " | " + msg;
        Log.e(TAG_PREFIX + caller.getClass().getSimpleName(), msg);
    }

    public static void e(Object caller, String msg, Throwable e) {
        msg = Thread.currentThread().getName() + " | " + msg;
        Log.e(TAG_PREFIX + caller.getClass().getSimpleName(), msg, e);
    }

}
