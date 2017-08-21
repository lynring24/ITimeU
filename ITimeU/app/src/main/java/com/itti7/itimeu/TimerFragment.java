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
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {
    /*timer Service Component*/
    private TimerService mTimerService;
    boolean mBound = false;
    /*Setting UI*/
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
    private Thread mReadThread;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View timerView = inflater.inflate(R.layout.activity_timer, container, false);
        init();
        mCountTimer = 0;
        mStateBttn = (Button) timerView.findViewById(R.id.state_bttn_view);
        mStateBttn.setOnClickListener(stateChecker);
        /*Time Text Initialize */
        mTimeText = (TextView) timerView.findViewById(R.id.time_txt_view);
        /*progressBar button init*/
        mProgressBar = (ProgressBar) timerView.findViewById(R.id.progressBar);
        mProgressBar.bringToFront(); // bring the progressbar to the top

        /*runTime = Integer.parseInt(mWorkTime);*/

        return timerView;
    }

    private void init() {
        intent = new Intent(getActivity(), TimerService.class);
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
                mTimerService = ((TimerService.MyBinder) service).getService();
            }
        };
        getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    Button.OnClickListener stateChecker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mStateBttn.getText().toString().equals("start")) { // checked
                 startTimer();
                if(runTime==Integer.parseInt(mWorkTime))
                    startTimer();
            } else {
                getActivity().stopService(intent); //stop service
                mReadThread.interrupt();
                mTimerService.stopTimer();
                mProgressBar.setProgress(0);
                handler.removeMessages(0);
                mCountTimer--;
                progressBarValue = 0; //must be set 0
                Log.v("TimerFragment", "Service stop--->");
                mStateBttn.setText(R.string.start);
                /*go back to liSt*/

            }
        }
    };

    public void listenTimer() {
        mReadThread = new Thread(new Runnable() {
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
        });
        mReadThread.start();
    }

    public void startTimer() {
        Toast.makeText(getContext(),""+mCountTimer, Toast.LENGTH_SHORT).show(); //Testor 코드
        mCountTimer++;
        if (mCountTimer % 8 == 0) // assign time by work,short & long break
            runTime = Integer.parseInt(mLongBreakTime);
        else if (mCountTimer % 2 == 1)
            runTime = Integer.parseInt(mWorkTime);
        else
            runTime = Integer.parseInt(mBreakTime);
        runTime = 1; //Testor Code
        mProgressBar.setMax(runTime * 60+2); // setMax by sec
        handler = new TimerHandler();
        intent.putExtra("RUNTIME", runTime);
        getActivity().startService(intent);
        listenTimer(); //catch up timer
        mStateBttn.setText(R.string.stop);
        handler.sendEmptyMessage(0);
    }

    public class TimerHandler extends Handler {
        TimerHandler() {
            super();
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            if (mTimerService.getRun()) {
                progressBarValue++;
                mProgressBar.bringToFront();
                mProgressBar.setProgress(progressBarValue);
                handler.sendEmptyMessageDelayed(0, 1000); //increase by sec
            } else { // Timer must be finished
                mProgressBar.setProgress(0);
                progressBarValue = 0;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            getActivity().unbindService(conn);
            mBound = false;
        }
    }
}