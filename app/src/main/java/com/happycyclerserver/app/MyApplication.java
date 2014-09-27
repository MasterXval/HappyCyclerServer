package com.happycyclerserver.app;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.happycyclerserver.util.Help;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Help.setAppCtx(getApplicationContext());
        Help.setMainThreadHandler(new Handler(Looper.getMainLooper()));
    }
}
