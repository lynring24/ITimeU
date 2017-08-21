package com.itti7.itimeu;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TimerService extends Service {

    private String mLeftTime;
    private int runTime;
    private boolean timerSwitch = false;

    public TimerService() {
    }

    public String getTime() {
        return timerSwitch?mLeftTime:"00:00";
    }
    public boolean getRun(){ return timerSwitch;}

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
            new CountDownTimer(runTime*1000 * 60, 1000) { // minute
                public void onTick(long millisUntilFinished) {
                    if (timerSwitch) {
                        Log.i("Timer", "Timer OnTick--->");
                        String min = String.format("%02d", (millisUntilFinished) / (1000 * 60));
                        String sec = String.format("%02d", (millisUntilFinished / 1000) % 60);
                        mLeftTime = min + ":" + sec;
                        if (runTime >= 60) {
                            String hour = String.format("%02d", (millisUntilFinished / (1000 * 60 * 60)));
                            mLeftTime = hour + ":" + mLeftTime;
                          /*  Noti*/
                        }
                    }
                }

                public void onFinish() {
                    mLeftTime="00:00";
                    timerSwitch=false;
                    Log.i("SendBroadCast","SendBroadCast---------------------------------------------------->");
                    Toast.makeText(getApplicationContext(),getPackageName(), Toast.LENGTH_SHORT).show(); //Testor 코드
                    Intent sendIntent = new Intent(getPackageName()+"SEND_BROAD_CAST");
                    sendIntent.putExtra("TIME", runTime);
                    sendBroadcast(sendIntent);
                    Log.i("SendBroadCast","SendBroadCast GoGO---------------------------------------------------->");
                    /////////////////////////////////////////////////ALARM N VIBRATION//////////////////////////////////////////////////////////////////////////////////////////////
                    Log.i("Timer", "Timer Finish--->");
                }
            }.start();
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runTime  = intent.getIntExtra("RUNTIME",1);
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }
}
