package com.itti7.itimeu;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class TimerService extends Service {

    private String mLeftTime;
    private int runTime;
    private boolean timerSwitch = false;
    private CountDownTimer timer;
    private NotificationManager mNM;

    public TimerService() {

    }

    public String getTime() {
        return timerSwitch ? mLeftTime : "00:00";
    }

    public boolean getRun() {
        return timerSwitch;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //Log.i("Timer", "------------------------------------------------------->Timer run()");
            timer = new CountDownTimer(runTime * 1000 * 60, 1000) { // minute
                public void onTick(long millisUntilFinished) {
                    if (timerSwitch) {
                        //Log.i("Timer", "------------------------------------------------------>Timer OnTick"); //checked
                        String min = String.format("%02d", (millisUntilFinished) / (1000 * 60));
                        String sec = String.format("%02d", (millisUntilFinished / 1000) % 60);
                        mLeftTime = min + ":" + sec;
                        if (runTime >= 60) {
                            String hour = String.format("%02d", (millisUntilFinished / (1000 * 60 * 60)));
                            mLeftTime = hour + ":" + mLeftTime;
                        }
                    }
                }

                public void onFinish() {
                    Log.i("Timer", "------------------------------------------------------->Timer onFinish");
                    if (timerSwitch) {         //send only if it has finished
                        mLeftTime = "00:00";
                        /////////////////////////////////////////////////ALARM N VIBRATION//////////////////////////////////////////////////////////////////////////////////////////////
                        timerSwitch = false;
                        Log.i("Timer", "------------------------------------------------------->Timer start send Intent");
                        Intent sendIntent = new Intent(getPackageName() + "SEND_BROAD_CAST");  // notice the end of Timer to Fragment
                        sendBroadcast(sendIntent);
                        Log.i("Timer", "------------------------------------------------------->Timer finish send Intent");
                    }
                }
            };
            timer.start();
        }
    };

    public void startTimer(int time) {
        runTime = time;
        timerSwitch = true;
        showNotification(mLeftTime);
        handler.post(runnable);
    }

    public void stopTimer() {
        //Log.i("Timer", "------------------------------------------------------->Timer stopTimer");
        timerSwitch = false;
        handler.removeMessages(0);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Log.i("Timer", "------------------------------------------------------->TimeronUnbind");
        stopTimer();
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Timer", "------------------------------------------------------->Timer onStartCommand");
        runTime = intent.getIntExtra("RUNTIME", 1);
        Log.i("RUNTIME", "------------------------------------------------------->RUNTIME : " + runTime);
//        timerSwitch = true;
//        handler.post(runnable);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
    }

    private void showNotification(CharSequence text) {
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getService(this, 0,
                new Intent(this, TimerFragment.class), 0);

        // Set the info for the views that show in the notification panel.
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getText(R.string.app_name))
                        .setContentText(text);
        mBuilder.setContentIntent(contentIntent);
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(0, mBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public TimerService getService() {
            //Log.i("Timer", "------------------------------------------------------->Timer getService()");
            return TimerService.this;
        }
    }
}
