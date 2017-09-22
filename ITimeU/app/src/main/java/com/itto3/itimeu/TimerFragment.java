package com.itto3.itimeu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itto3.itimeu.data.ItemContract;
import com.itto3.itimeu.data.ItemDbHelper;
import com.itto3.itimeu.data.SharedPreferenceUtil;
import com.itto3.itimeu.data.TimerDbUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {
    /*Setting UI*/
    public static final String WORKTIME = "worktime";
    public static final String BREAKTIME = "breaktime";
    public static final String LONGBREAKTIME = "longbreaktime";
    public static final String SESSION = "session";

    private TextView leftTime;
    private TextView mItemNameText;

    private ProgressBar progressBar;
    private Button stateButton;
    /*timer Service Component*/
    private TimerService mTimerService;

    boolean mServiceBound = false;
    private TimerHandler timerHandler;
    private int progressBarValue = 0;
    public int runTime; // minute

    /*timer calc*/
    private Intent intent;
    //private ServiceConnection conn;
    private Thread mReadThread;
    /*store  time count*/
    private int timerCounter;

    // Item info come from ListView
    private int mId, mStatus, mUnit, mTotalUnit;
    private String mName;

    public TimerFragment() {} // Required empty public constructor

    BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View timerView = inflater.inflate(R.layout.fragment_timer, container, false);

        // get Timer tag and set to TimerTag
        String timerTag = getTag();
        ((MainActivity) getActivity()).setTimerTag(timerTag);

        mItemNameText = timerView.findViewById(R.id.job_name_txt);
        /*progressBar button init*/
        progressBar = (ProgressBar) timerView.findViewById(R.id.progressBar);
        stateButton = (Button) timerView.findViewById(R.id.state_bttn_view);
        stateButton.setOnClickListener(stateChecker);
        stateButton.setEnabled(false);
        /*Time Text Initialize */
        leftTime = (TextView) timerView.findViewById(R.id.time_txt_view);
        /*progressBar button init*/
        progressBar = (ProgressBar) timerView.findViewById(R.id.progressBar);
        progressBar.bringToFront(); // bring the progressbar to the top


        /*동적 리시버 구현 */
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onUnitFinish();
            }
        };

        /*init timer count */
        timerCounter = 1;

        /*init shared prefernce*/
        SharedPreferenceUtil.save(getContext(), "COUNT", timerCounter);

        return timerView;
    }

    public void onUnitFinish() {
        stopUpdateLeftTime();
        // UPDATE mCountTimner range 1..8
        // if Long Break Time has just finished, change to 1
        timerCounter++;
        int sessionNumber = SharedPreferenceUtil.get(getContext(), SESSION, 4) * 2;
        if (timerCounter == sessionNumber + 1)
            timerCounter = 1;

        SharedPreferenceUtil.save(getContext(), "COUNT", timerCounter);

        stateButton.setText("start");

        if (!isWorkTime())
            mUnit++;
        setTimerTimeName();

        storeUnitStatus();

        //after the unit values has been updated set false to ServiceFinished
        TimerService.mTimerServiceFinished = false;
    }

    public void changeScreenToList() {
        MainActivity mainActivity = (MainActivity) getActivity();
        (mainActivity).getViewPager().setCurrentItem(0);
    }

    public void storeUnitStatus() {
        //store mUnit and mStatus
        if (isTaskComplete()) {
            TimerDbUtil.update(getContext(), mUnit, ItemContract.ItemEntry.STATUS_DONE, mId);
            if (isWorkTime()) {
                stateButton.setEnabled(false);
                changeScreenToList();
            }
        } else {
            TimerDbUtil.update(getContext(), mUnit, ItemContract.ItemEntry.STATUS_TODO, mId);
        }
    }

    public boolean isTaskComplete() {
        return mUnit == mTotalUnit;
    }

    public void setTimerTimeName() {
        timerCounter = SharedPreferenceUtil.get(getContext(), "COUNT", 1);
        if (isLongBreakTime()) {// assign time by work,short & long break
            runTime = SharedPreferenceUtil.get(getContext(), LONGBREAKTIME, 20);
            mItemNameText.setText("Long Break Time");
        } else if (isWorkTime()) {
            runTime = SharedPreferenceUtil.get(getContext(), WORKTIME, 25);
            mItemNameText.setText(mName);
        } else {
            runTime = SharedPreferenceUtil.get(getContext(), BREAKTIME, 5);
            mItemNameText.setText("Break Time");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        intent = new Intent(getActivity(), TimerService.class);

        if (TimerService.mTimerServiceFinished == true) {
            onUnitFinish();
        }
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /*Defines callbacks for service binding, passed to bindService()*/
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            mTimerService = ((TimerService.MyBinder) service).getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mTimerService = null;
            //mTimerService.stopCountNotification();
            progressBar.setProgress(0);
            timerHandler.removeMessages(0);
            mItemNameText.setText("");
            stateButton.setEnabled(false);
            mServiceBound = false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, new IntentFilter(mTimerService.strReceiver));
    }

    public void setStatusToDo() {
        /*set mStatus to TO DO(0)*/
        if (stateButton.getText().toString().equals("stop")) {
            TimerDbUtil.update(getContext(), ItemContract.ItemEntry.STATUS_TODO, mId, false);
        }
    }

    public boolean isLongBreakTime() {
        int session = SharedPreferenceUtil.get(getContext(), SESSION, 4) * 2;
        return timerCounter % session == 0;
    }

    public boolean isWorkTime() {
        return timerCounter % 2 == 1;
    }

    Button.OnClickListener stateChecker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (stateButton.getText().toString().equals("start")) { // checked

                //mUnit will be intialize when list item is clicked
                if (mServiceBound) {
                /* set mStatus DB to DO(1)*/
                    TimerDbUtil.update(getContext(), ItemContract.ItemEntry.STATUS_DO, mId, false);
                    setTimerTimeName();
                    progressBar.setMax(runTime * 60 + 2); // setMax by sec
                    timerHandler = new TimerHandler();
                    mTimerService.setRunTimeTaskName(runTime, mItemNameText.getText().toString());
                    updateLeftTime();
                    stateButton.setText(R.string.stop);
                    timerHandler.sendEmptyMessage(0);
                }
            } else {
                mTimerService.stopCountNotification();
                getActivity().stopService(intent); //stop service
                stopUpdateLeftTime();
                progressBar.setProgress(0);
                timerHandler.removeMessages(0);
                progressBarValue = 0; //must be set 0
                stateButton.setText(R.string.start);
                /*set mStatus to TO DO(0)*/
                TimerDbUtil.update(getContext(), ItemContract.ItemEntry.STATUS_TODO, mId, false);
            }
        }
    };

    public void updateLeftTime() {
        mReadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mTimerService.getRun()) {
                    //check out if it is still available
                    if (getActivity() == null)
                        return;

                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                leftTime.setText(mTimerService.getTime());
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace(); //back to list
                    }
                }
            }
        });
        mReadThread.start();
    }

    public void stopUpdateLeftTime() {
        mReadThread.interrupt();
        leftTime.setText("");
    }

    public class TimerHandler extends Handler {
        TimerHandler() {
            super();
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            if (mTimerService.getRun()) {
                progressBarValue++;
                progressBar.bringToFront();
                progressBar.setProgress(progressBarValue);
                timerHandler.sendEmptyMessageDelayed(0, 1000); //increase by sec
            } else { // Timer must be finished
                progressBar.setProgress(0);
                progressBarValue = 0;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        setStatusToDo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceBound) {
            mTimerService.stopService(intent);
            getActivity().unbindService(mConnection);
            mServiceBound = false;
        }
    }

    public void setTimerFragment(int mId, int mStatus, int mUnit, int mTotalUnit, String mName) {
        this.mId = mId;
        this.mStatus = mStatus;
        this.mUnit = mUnit;
        this.mTotalUnit = mTotalUnit;
        this.mName = mName;
        this.stateButton.setEnabled(true);
        if (timerCounter % 2 == 1) {
            //should keep setting when the breakTimer hasn't run yet
            mItemNameText.setText(mName);
        }
    }

    public void setDeleteItemDisable(int dId) {
        //once the Item became deleted
        if (dId == mId) {
            /*set the button disable*/
            stateButton.setEnabled(false);
            mItemNameText.setText("Deleted");
        }
    }
}

