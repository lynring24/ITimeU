package com.itti7.itimeu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

    /*Setting UI*/
    private View header;
    /* timer value */
    private TextView mTimeText;
    private String mWorkTime; //R.id.work_time
    private String mBreakTime; //R.id.work_time
    private String mLongBreakTime; //R.id.work_time
    private ProgressBar mProgressBar;
    private Button mStateBttn;
    /*timer Service Component*/
    private TimerService mTimerService;
    boolean mBound = false;
    private TimerHandler handler;
    private int progressBarValue = 0;
    public int runTime; // minute
    /*timer calc*/
    private Intent intent;
    private ServiceConnection conn;
    private Thread mReadThread;
    /*store  time count*/
    private int mCountTimer;

    public TimerFragment() {
        // Required empty public constructor
    }

    BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View timerView = inflater.inflate(R.layout.activity_timer, container, false);
        init();
        mStateBttn = (Button) timerView.findViewById(R.id.state_bttn_view);
        mStateBttn.setOnClickListener(stateChecker);
        /*Time Text Initialize */
        mTimeText = (TextView) timerView.findViewById(R.id.time_txt_view);
        /*progressBar button init*/
        mProgressBar = (ProgressBar) timerView.findViewById(R.id.progressBar);
        mProgressBar.bringToFront(); // bring the progressbar to the top

        /* 브로드캐스트의 액션을 등록하기 위한 인텐트 필터 */
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(getActivity().getPackageName() + "SEND_BROAD_CAST");

        /*동적 리시버 구현 */
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("TimerFragment", "------------------------------------------------------->TimerFragment onReceive()");
                String mPlayedTime = "" + intent.getIntExtra("TIME", 1);
/*                mProgressBar.setProgress(0);
                progressBarValue=0;
                mCountTimer++;
                startTimer();*/
                 if(mPlayedTime.equals(mWorkTime)){
                     mTimeText.setText("");
                     mProgressBar.setProgress(0);
                     progressBarValue=0;
                     mCountTimer++;
                    startTimer();
                }
            }
        };
        getActivity().registerReceiver(mReceiver, intentfilter);
        return timerView;
    }

    private void init() {
        intent = new Intent(getActivity(), TimerService.class);
        /*init timer count */
       mCountTimer=1;
        /*work time 을 갖고 오기위해 inflater*/
        header = getActivity().getLayoutInflater().inflate(R.layout.activity_setting, null, false);
       /* mWorkTime = ((EditText) header.findViewById(R.id.work_time)).getText().toString();
        mBreakTime = ((EditText) header.findViewById(R.id.break_time)).getText().toString();
        mLongBreakTime = ((EditText) header.findViewById(R.id.long_break_time)).getText().toString();*/
        mWorkTime = "1";
        mBreakTime = "2";
        mLongBreakTime = "3";
        /*TimerService connection*/
        conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mTimerService = null;
                mBound = false;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("TimerFragment", "------------------------------------------------------->TimerFragment onServiceConnected()");
                mTimerService = ((TimerService.MyBinder) service).getService();
                mBound = true;
            }
        };
        /*TimerService Intent Listener*/
        getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    Button.OnClickListener stateChecker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mStateBttn.getText().toString().equals("start")) { // checked
                Log.i("TimerFragment", "------------------------------------------------------->TimerFragment stateChecker() Start");
                if (mBound)
                    startTimer();
            } else {
                Log.i("TimerFragment", "------------------------------------------------------->TimerFragment stateChecker() Stop");
                Log.i("TimerFragment", "----------------------->Timer Stopped");
                getActivity().stopService(intent); //stop service
                mReadThread.interrupt();
                mTimerService.stopTimer();
                mProgressBar.setProgress(0);
                handler.removeMessages(0);
                progressBarValue = 0; //must be set 0
                Log.i("TimerFragment", "----------------------->Service stop");
                mStateBttn.setText(R.string.start);
                /*go back to liSt*/
            }
        }
    };

    public void startTimer() {
        Log.i("Fragment", "--------------------------------------------->startTimer()");
        if (mCountTimer % 8 == 0) // assign time by work,short & long break
            runTime = Integer.parseInt(mLongBreakTime);
        else if (mCountTimer % 2 == 1)
            runTime = Integer.parseInt(mWorkTime);
        else
            runTime = Integer.parseInt(mBreakTime);

        mProgressBar.setMax(runTime * 60 + 2); // setMax by sec
        handler = new TimerHandler();
        updateTimerText();
       /* intent.putExtra("RUNTIME", runTime);
        getActivity().startService(intent);*/
       mTimerService.startTimer(runTime);
        mStateBttn.setText(R.string.stop);
        handler.sendEmptyMessage(0);
    }

    public void updateTimerText() {
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
        Log.i("TimerFragment", "------------------------------------------------------->TimerFragment ReadThreadStart()");
        mReadThread.start();
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

    @Override
    public void onPause() {
        super.onPause();
        if (mBound) {
            getActivity().unregisterReceiver(mReceiver);
            mBound = false;
        }
    }
}