package com.itti7.itimeu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {
    /*timer Service Component*/
    private TimerService mTimerService;
    boolean mBound = false;
    /*UI Inflator*/
    private View header;
   /* timer value */
    private TextView mTimeText;
    private String mWorkTime; //R.id.work_time
    private String mBreakTime; //R.id.work_time
    private String mLongBreakTime; //R.id.work_time
    private ProgressBar mProgressBar;
    private Button mStateBttn;

    private TimerHandler handler;
    private int progressBarValue = 0;
    public int runTime; // minute
    /*timer calc*/
    private Intent intent;
    private int mCountTimer; //
    private ServiceConnection conn;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View timerView = inflater.inflate(R.layout.activity_timer, container, false);
        init();
        mCountTimer=0;
        mStateBttn = (Button) timerView.findViewById(R.id.state_bttn_view);
        mStateBttn.setOnClickListener(stateChecker);
        /*Time Text Initialize */
        mTimeText = (TextView) timerView.findViewById(R.id.time_txt_view);
        /*progressBar button init*/
        mProgressBar = (ProgressBar) timerView.findViewById(R.id.progressBar);
        mProgressBar.bringToFront(); // bring the progressbar to the top

        /*runTime = Integer.parseInt(mWorkTime);*/
 /*       mCalcTimer = new Timer(runTime * 1000 * 60, 1000);
        handler = new TimerHandler();
        handler.sendEmptyMessage(0);*/
        return timerView;
    }

    private void init() {
        intent = new Intent(getActivity(), TimerService.class);
       /* getActivity().startService(intent);*/
        /*work time 을 갖고 오기위해 inflater*/
        header = getActivity().getLayoutInflater().inflate(R.layout.activity_setting, null, false);
        mWorkTime = ((EditText) header.findViewById(R.id.work_time)).getText().toString();
        mBreakTime = ((EditText) header.findViewById(R.id.break_time)).getText().toString();
        mLongBreakTime = ((EditText) header.findViewById(R.id.long_break_time)).getText().toString();
        conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mTimerService = ((TimerService.MyBinder)service).getService();
            }
        };
        getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    Button.OnClickListener stateChecker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v("TimerFragment", "got Clicked--->");
            if (mStateBttn.getText().toString().equals("START")) {
                Log.v("TimerFragment", "start Clicked--->");
                /*mCountTimer++;
                if(mCountTimer%8==0) // assign time by work,short & long break
                    runTime=Integer.parseInt(mLongBreakTime);
                else if(mCountTimer%2==0)
                    runTime=Integer.parseInt(mWorkTime);
                else
                    runTime=Integer.parseInt(mBreakTime);
                Log.v("TimerFragment", "Service call--->");
                intent.putExtra("RUNTIME",runTime); //call service

                getActivity().startService(intent);
                handler = new TimerHandler();
                listenTimer(); //catch up timer*/
                mStateBttn.setText(R.string.stop);
            }
            else {
                Log.v("TimerFragment", "Clicked stop--->");
                /*handler.sendEmptyMessage(0);
                getActivity().stopService(intent); //stop service
                Log.v("TimerFragment", "Service stop--->");
                mTimeText.setText("");*/
                mStateBttn.setText(R.string.start);
            }
        }
    };

    public void listenTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTimeText.setText(mTimerService.getTime());
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public class TimerHandler extends Handler {
        TimerHandler() {
            super();
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            progressBarValue++;
            mProgressBar.setMax(runTime * 60); // setMax by sec
            mProgressBar.setProgress(progressBarValue);
            handler.sendEmptyMessageDelayed(0, 1000); //increase by sec
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBound) {
            getActivity().unbindService(conn);
            mBound = false;
        }
    }
}