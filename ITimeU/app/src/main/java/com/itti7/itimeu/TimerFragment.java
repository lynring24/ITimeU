package com.itti7.itimeu;


import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {
    /*timer components*/
    private TextView mTimeText;
    private ProgressBar mProgressBar;
    private Button mStateBttn;
    /*button state value*/
    final boolean STATE_PLAY=true;
    final boolean STATE_STOP=false;
    private boolean state=STATE_STOP;
    /*progressBar state value*/
    private Handler handler;
    private int progressBarValue = 0;
    /*timer calc*/
    private CountDownTimer mCalcTimer;
    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View timerView = inflater.inflate(R.layout.activity_timer, container, false);

        /*progressBar button init*/
        mProgressBar = (ProgressBar)timerView.findViewById(R.id.progressBar);
        mStateBttn = (Button)timerView.findViewById(R.id.state_bttn_view);
        mStateBttn.setOnClickListener(stateChecker);

        /*Time Text Initialize */
        mTimeText = (TextView)timerView.findViewById(R.id.time_txt_view);

        final int time = 25;

        mCalcTimer = new CountDownTimer(time*1000*60,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String hour = String.format("%02d",(millisUntilFinished / (1000*60*60)) );
                String min = String.format("%02d",(millisUntilFinished) / (1000*60) );
                String sec = String.format("%02d",(millisUntilFinished/1000) %60);
                mTimeText.setText(hour+":"+min+":"+sec);
//              mTimeText.setText("seconds remaining: " + millisUntilFinished / 1000); //TesterCode
            }
            @Override
            public void onFinish() {
                        /*alarm or vibration*/
                // We want the alarm to go off 30 seconds from now.
               mTimeText.setText("done!");
            }
        };
        handler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                if(state)
                {
                    progressBarValue+=1; // match to sec
                }
                mProgressBar.setProgress(progressBarValue);
                handler.sendEmptyMessageDelayed(0, 1000); //increase by sec
            }
        };

        handler.sendEmptyMessage(0);
        return timerView;
    }

    Button.OnClickListener stateChecker =new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            state=!state;
            if(state==STATE_PLAY){
                mCalcTimer.start();
                mStateBttn.setText(R.string.stop);
            }
            else{
                progressBarValue=0;
                mTimeText.setText("");
                mCalcTimer.cancel();
                mStateBttn.setText(R.string.start);
            }
        }
    };


}
