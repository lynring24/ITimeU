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
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class TimerService extends Service {

    private String mLeftTime;
    private int runTime;
    private boolean timerSwitch = false;
    private CountDownTimer timer;
    private NotificationManager mNM;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    public TimerService() {

    }
    public void setRunTime(int time) {
        runTime = time;
        timerSwitch = true;
        // showNotification(mLeftTime);
        // handler.post(runnable);
    }
    public String getTime() {
        return timerSwitch ? mLeftTime : "00:00";
    }

    public boolean getRun() {
        return timerSwitch;
    }
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                this.post(runnable);
            } catch (Exception e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }
    //Handler handler = new Handler();
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



    public void stopTimer() {
        //Log.i("Timer", "------------------------------------------------------->Timer stopTimer");
        timerSwitch = false;
        //handler.removeMessages(0);
        mServiceHandler.removeMessages(0);
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
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg); //start

        // If we get killed, after returning from here, restart
        // return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onCreate() {
        Log.i("TimerService", "------------------------------------------------------->TimerService onCreate()");
       // mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    private void showNotification(CharSequence text) {
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getService(this, 0,
                new Intent(this, TimerFragment.class), 0);

        // Set the info for the views that show in the notification panel.
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
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
