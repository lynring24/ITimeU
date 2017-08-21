package com.itti7.itimeu;

import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by lynring24 on 2017-08-21.
 */

public class Timer extends CountDownTimer {
    private String mLeftTime;
    private long mMinute;

    Timer(long total, long interval) {
        super(total, interval);
        Log.i("Timer", "Timer Start--->");
        mMinute = total;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        String min = String.format("%02d", (millisUntilFinished) / (1000 * 60));
        String sec = String.format("%02d", (millisUntilFinished / 1000) % 60);
        mLeftTime = min + ":" + sec;
        if (mMinute >= 60) {
            String hour = String.format("%02d", (millisUntilFinished / (1000 * 60 * 60)));
            mLeftTime = hour + ":" + mLeftTime;
        }
    }

    @Override
    public void onFinish() {
        /*alarm or vibration*/
        Log.i("Timer", "Timer Finish--->");
    }

    public String getTime() {
        return mLeftTime;
    }
}
