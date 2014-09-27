package com.happycyclerserver.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.happycyclerserver.util.Help;
import com.happycyclerserver.util.Logger;

public class BleService extends Service {

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void startForeground() {
        Logger.v(this, "startForegroundIfNecessary()");

        NotificationCompat.Builder builder = notificationBuilder();
        builder.setContentText(getString(R.string.ble_service_content))
                .setContentTitle(getString(R.string.ble_service_title));
        startForeground(222222, builder.build());
    }

    public void stopForeground() {
        Logger.v(this, "stopForeground()");
        stopForeground(true);
    }

    private static NotificationCompat.Builder notificationBuilder() {
        final Context context = Help.appCtx();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setLocalOnly(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setSmallIcon(R.drawable.ic_launcher);
        return builder;
    }

}
