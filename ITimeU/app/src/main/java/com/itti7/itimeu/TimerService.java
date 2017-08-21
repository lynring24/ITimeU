package com.itti7.itimeu;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TimerService extends Service {

    private static final String TAG="Test";
    private int timer = 0;
    private boolean timerSwitch = false;

    public TimerService() {
    }

    public int getTime() {
        return timer;
    }

    public void startTimer() {
        timerSwitch = true;
        handler.post(runnable);
    }

    public void stopTimer() {
        timerSwitch = false;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(timerSwitch == true){
                timer++;
                System.out.println(timer);
                handler.postDelayed(runnable, 1000);
            }
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStart--->");
        startTimer();
//        String data = intent.getStringExtra("input");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service onDestroy--->");
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service onCreate--->");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MyBinder();
    }

    public class MyBinder extends Binder{
        public TimerService getService(){
            return TimerService.this;
        }
    }

}
