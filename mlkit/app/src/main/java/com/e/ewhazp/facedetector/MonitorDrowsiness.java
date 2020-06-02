package com.e.ewhazp.facedetector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MonitorDrowsiness extends Service {

    private static final String TAG = "MonitorDrowsiness";

    cd1Timer cd1timer = cd1Timer.getInstancecd1();
    cd3Timer cd3timer = cd3Timer.getInstancecd3();

    static Timer timer = new Timer();
    TimerTask monitoringUser = new TimerTask() {
        @Override
        public void run() {
            if(!cd1timer.isCount1sisRunning()){
                cd1timer.start();
            }
            if(!cd3timer.isCount3sisRunning()){
                cd3timer.start();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //timer.schedule(monitoringUser,0,30);//매 프레임마다 감지
        Log.e(TAG,"start onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"Service onDestroy");
        timer.cancel();
        super.onDestroy();
    }

    public MonitorDrowsiness() {
        Log.e(TAG,"Service start");
        timer.schedule(monitoringUser,0,30);//매 프레임마다 감지


    }

    @Override
    public void onCreate() {
        Log.e(TAG,"실행되는거맞냐고");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}