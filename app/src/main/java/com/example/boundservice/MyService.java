package com.example.boundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    private final IBinder localBinder = new LocalBinder();

    ScheduledExecutorService mSheduledExecutorService;
    public static final String TAG = "MyService";
    public static final String INCREASE_PROGRESS = "INCREASE_PROGRESS";

    public MyService() {
        mSheduledExecutorService = Executors.newScheduledThreadPool(1);

        mSheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.ProgressReceiver.SIMPLE_ACTION);
                intent.putExtra(INCREASE_PROGRESS, increaseValue());
                sendBroadcast(intent);
            }
        }, 1000, 2000, TimeUnit.MILLISECONDS);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public int increaseValue() {
        return 20;
    }

    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.ProgressReceiver.SIMPLE_ACTION);
                intent.putExtra(INCREASE_PROGRESS, increaseValue());
                sendBroadcast(intent);
            }
        }, 1000, 2000, TimeUnit.MILLISECONDS);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mSheduledExecutorService.shutdownNow();
        Log.d(TAG, "onDestroy MyService");
    }
}
