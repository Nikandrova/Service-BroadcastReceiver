package com.example.boundservice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "ServiceConnection";
    MyService myService;
    boolean isBound = false;
    ProgressBar progressBar;
    Button startService;
    Button reduceValue;

    ProgressReceiver progressReceiver;

    Intent intentService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);

        startService = findViewById(R.id.btnStartService);
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBound == false) {
                    progressBar.setProgress(0);
                    registerReceiver();

                    intentService = new Intent(getApplicationContext(), MyService.class);
                    bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);
                }
            }
        });

        reduceValue = findViewById(R.id.btnReduceValueProgressBar);
        reduceValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressBar.getProgress() < 50)
                    progressBar.setProgress(0);
                else {
                    progressBar.setProgress(progressBar.getProgress() - 50);
                }
            }
        });

        progressReceiver = new ProgressReceiver();
    }

    private void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter(ProgressReceiver.SIMPLE_ACTION);
        registerReceiver(progressReceiver, intentFilter);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(LOG_TAG, "MainActivity onServiceConnected");
            MyService.LocalBinder binder = (MyService.LocalBinder) iBinder;
            myService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(LOG_TAG, "MainActivity onServiceDisconnected");
            isBound = false;
        }
    };

    public class ProgressReceiver extends BroadcastReceiver {

        public static final String SIMPLE_ACTION = "com.example.boundservice.SIMPLE_ACTION";

        @Override
        public void onReceive(Context context, Intent intent) {
            int increaseValue = intent.getIntExtra(MyService.INCREASE_PROGRESS, 0);

            if (progressBar.getProgress() >= 100) {
                Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                stopServiceAndReceiver();
            } else {
                progressBar.setProgress(progressBar.getProgress() + increaseValue);
                Log.d(LOG_TAG, "progress is " + progressBar.getProgress());
            }
        }

        private void stopServiceAndReceiver(){
            unbindService(serviceConnection);
            isBound = false;
            myService.stopService(intentService);
            unregisterReceiver(progressReceiver);
        }
    }
}
