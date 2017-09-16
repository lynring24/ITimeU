package com.itti7.itimeu;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static com.itti7.itimeu.SettingFragment.SCREENON;
import static com.itti7.itimeu.SettingFragment.SOUNDON;
import static com.itti7.itimeu.SettingFragment.VIBRATEON;
import static java.security.AccessController.getContext;

public class TimerService extends Service {
    public static String strReceiver = "com.TimerService.receiver";
    private String mLeftTime;
    private int runTime;
    private boolean timerSwitch = false;
    private CountDownTimer timer;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNM;
    private final int NOTIFYID=001;

    public static boolean mTimerServiceFinished = false;

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
            timer = new CountDownTimer(runTime * 1000 * 60, 1000) { // minute
                public void onTick(long millisUntilFinished) {
                    if (timerSwitch) {
                        long runTimeInSecond = millisUntilFinished/1000;
                        String hour = String.format("%02d", (runTimeInSecond/(60*60)));
                        String min = String.format("%02d", ((runTimeInSecond%(60*60))/60));
                        String sec = String.format("%02d", ((runTimeInSecond%(60*60))%60));
                        mLeftTime = min + ":" + sec;
                        if (runTime >= 60) {
                            mLeftTime = hour + ":" + mLeftTime;
                        }
                    }
                    mBuilder.setContentText(mLeftTime);
                    mNM.notify(NOTIFYID, mBuilder.build());
                }

                public void onFinish() {
                    if (timerSwitch) {         //send only if it has finished
                        mLeftTime = "00:00";
                        mBuilder.setContentText(mLeftTime);
                        mBuilder.setSubText("FINISHED");
                        mNM.notify(NOTIFYID, mBuilder.build());

                        timerSwitch = false;
                        if(timer!=null)
                            timer.cancel();
                        //여기쯤*********************
                        ringTimerEndAlarm();
                        //*************************
                        mTimerServiceFinished =true;

                        Intent sendIntent = new Intent(strReceiver);  // notice the end of Timer to Fragment
                        sendBroadcast(sendIntent);
                    }

                }

            };
            timer.start();
        }
    };
    public void setTimeName(int time,String name) {
        runTime=time;
        timerSwitch = true;
        handler.post(runnable);
        showNotification(name);
    }
    private void showNotification(String name) {

        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent, 0);


        // Set the info for the views that show in the notification panel.
        mBuilder = new NotificationCompat.Builder(TimerService.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(name)
                .setContentText(mLeftTime);

        mBuilder.setContentIntent(contentIntent);
        // Send the notification.
        mNM = (NotificationManager)TimerService.this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNM.notify(NOTIFYID, mBuilder.build());
    }

    public void stopCountNotification() {
        timerSwitch = false;
        if(timer!=null)
            timer.cancel();
        if(mNM!=null)
            mNM.cancel(NOTIFYID);

    }

    @Override
    public boolean onUnbind(Intent intent) {

        super.onUnbind(intent);
        stopCountNotification();

        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

    private void ringTimerEndAlarm() {
        boolean isVibrateOn = PrefUtil.get(this, VIBRATEON, false);
        boolean isSoundOn = PrefUtil.get(this, SOUNDON, true);
//        boolean isScreenOn = PrefUtil.get(this, SCREENON, false);

        if (isVibrateOn) {
            if (Build.VERSION.SDK_INT >= 26) {
                ((Vibrator) this.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                ((Vibrator) this.getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
            }
        }

        if (isSoundOn) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ring = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ring.play();
        }

    }

    public class MyBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }
}
