package com.itti7.itimeu;

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

import com.itti7.itimeu.data.ItemContract;
import com.itti7.itimeu.data.ItemDbHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

    /*Setting UI*/
    public static final String WORKTIME = "worktime";
    public static final String BREAKTIME = "breaktime";
    public static final String LONGBREAKTIME = "longbreaktime";
    public static final String SESSION = "session";

    private TextView mTimeText;
    private TextView mItemNameText;

    private ProgressBar mProgressBar;
    private Button mStateBttn;
    /*timer Service Component*/
    private TimerService mTimerService;

    boolean mServiceBound = false;
    private TimerHandler Timerhandler;
    private int progressBarValue = 0;
    public int runTime; // minute

    /*timer calc*/
    private Intent intent;
    //private ServiceConnection conn;
    private Thread mReadThread;
    /*store  time count*/
    private int mCountTimer;

    // Item info come from ListView
    private int mId, mStatus, mUnit, mTotalUnit;
    private String mName;

    // For access ITimeU database
    ItemDbHelper dbHelper;
    SQLiteDatabase db;
    String query;

    public TimerFragment() {
        // Required empty public constructor
    }

    BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View timerView = inflater.inflate(R.layout.fragment_timer, container, false);

        // get Timer tag and set to TimerTag
        String timerTag = getTag();
        ((MainActivity) getActivity()).setTimerTag(timerTag);

        //get ItemDbHelper to get SQLITEDB.getWritableDB()
        dbHelper = new ItemDbHelper(getActivity());

        mItemNameText = timerView.findViewById(R.id.job_name_txt);
        /*progressBar button init*/
        mProgressBar = (ProgressBar) timerView.findViewById(R.id.progressBar);
        mStateBttn = (Button) timerView.findViewById(R.id.state_bttn_view);
        mStateBttn.setOnClickListener(stateChecker);
        mStateBttn.setEnabled(false);
        /*Time Text Initialize */
        mTimeText = (TextView) timerView.findViewById(R.id.time_txt_view);
        /*progressBar button init*/
        mProgressBar = (ProgressBar) timerView.findViewById(R.id.progressBar);
        mProgressBar.bringToFront(); // bring the progressbar to the top


        /*동적 리시버 구현 */
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onUnitFinish();
            }
        };

        /*init timer count */
        mCountTimer = 1;

        /*init shared prefernce*/
        PrefUtil.save(getContext(), "COUNT", mCountTimer);


        db = dbHelper.getWritableDatabase();
        query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET unit = '" + mUnit + "', status = '";
        // if all the units are  completed
        if (mUnit == mTotalUnit) {
            //UPDATE DB  mStatus = 2
            query = query + ItemContract.ItemEntry.STATUS_DONE + "' WHERE _ID = '" + mId + "';";
            // if the last break of the list just end go back to the listFragment
            if (mCountTimer == mUnit * 2) {
                //if finished, set the button disable
                mStateBttn.setEnabled(false);
                // Change Fragment TimerFragment -> ListItemFragment ->
                MainActivity mainActivity = (MainActivity) getActivity();
                (mainActivity).getViewPager().setCurrentItem(0);
            }
        } else {
            //UPDATE DB  mStatus = 0
            query = query + ItemContract.ItemEntry.STATUS_TODO + "' WHERE _ID = '" + mId + "';";
        }

        db.execSQL(query);
        db.close();

            /*List Item unit count update*/
        MainActivity mainActivity = (MainActivity) getActivity();
        String listTag = mainActivity.getListTag();
        ListItemFragment listItemFragment = (ListItemFragment) mainActivity.getSupportFragmentManager().findFragmentByTag(listTag);
        listItemFragment.listUiUpdateFromDb();

        return timerView;
    }

    public void onUnitFinish() {
        stopUpdateLeftTime();
        // UPDATE mCountTimner range 1..8
        // if Long Break Time has just finished, change to 1
        mCountTimer++;
        int sessionNum = PrefUtil.get(getContext(), SESSION, 4) * 2;
        if (mCountTimer == sessionNum + 1)
            mCountTimer = 1;

        PrefUtil.save(getContext(), "COUNT", mCountTimer);

        //change the button text to 'start'
        mStateBttn.setText("start");

        //set the ListItemText for the next session
        if (mCountTimer % 2 == 1)
            mItemNameText.setText(mName);
        else {
            mUnit++; //if the last session WAS work ,increase mUnit
            if (mCountTimer % sessionNum == 0)
                mItemNameText.setText("Long Break Time");
            else
                mItemNameText.setText("Break Time");
        }

        //store mUnit and mStatus
        query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET unit = '" + mUnit + "', status = '";
        // if all the units are  completed
        if (mUnit == mTotalUnit) {
            //UPDATE DB  mStatus = 2
            query = query + ItemContract.ItemEntry.STATUS_DONE + "' WHERE _ID = '" + mId + "';";
            // if the last break of the list just end go back to the listFragment
            if (mCountTimer % 2 == 1) {
                //if finished, set the button disable
                mStateBttn.setEnabled(false);
                // Change Fragment TimerFragment -> ListItemFragment ->
                MainActivity mainActivity = (MainActivity) getActivity();
                (mainActivity).getViewPager().setCurrentItem(0);
            }
        } else {
            //UPDATE DB  mStatus = 0
            query = query + ItemContract.ItemEntry.STATUS_TODO + "' WHERE _ID = '" + mId + "';";
        }
        dbUpdate(query);

        //after the unit values has been updated
        //turn the value to false;
        TimerService.mTimerServiceFinished = false;
    }

    public void updateListFragment() {
        /*List Item unit count update*/
        MainActivity mainActivity = (MainActivity) getActivity();
        String listTag = mainActivity.getListTag();
        ListItemFragment listItemFragment = (ListItemFragment) mainActivity.getSupportFragmentManager().findFragmentByTag(listTag);
        listItemFragment.listUiUpdateFromDb();
    }

    @Override
    public void onStart() {
        super.onStart();
        intent = new Intent(getActivity(), TimerService.class);

        if (TimerService.mTimerServiceFinished == true) {
            onUnitFinish();
        }


        /*TimerService Intent Listener*/
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
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
            mProgressBar.setProgress(0);
            Timerhandler.removeMessages(0);
            mItemNameText.setText("");
            mStateBttn.setEnabled(false);
            mServiceBound = false;
        }
    };


    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mReceiver, new IntentFilter(mTimerService.strReceiver));
    }

    public void dbUpdate(String query) {
        db = dbHelper.getWritableDatabase();
        db.execSQL(query);
        db.close();

        /*List Item unit count update*/
        updateListFragment();
    }

    public void setStatusToDo() {
        /*set mStatus to TO DO(0)*/
        if (mStateBttn.getText().toString().equals("stop")) {
            query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET status = '" + ItemContract.ItemEntry.STATUS_TODO + "' WHERE _ID = '" + mId + "';";
            dbUpdate(query);
        }
    }

    Button.OnClickListener stateChecker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mStateBttn.getText().toString().equals("start")) { // checked

                //mUnit will be intialize when list item is clicked
                if (mServiceBound) {
                /* set mStatus DB to DO(1)*/

                    query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET status = '" + ItemContract.ItemEntry.STATUS_DO + "' WHERE _ID = '" + mId + "';";
                    dbUpdate(query);

                    mCountTimer = PrefUtil.get(getContext(), "COUNT", 1);
                    if (mCountTimer % ((PrefUtil.get(getContext(), SESSION, 4) * 2)) == 0) // assign time by work,short & long break
                        runTime = PrefUtil.get(getContext(), LONGBREAKTIME, 20);
                    else if (mCountTimer % 2 == 1)
                        runTime = PrefUtil.get(getContext(), WORKTIME, 25);
                    else
                        runTime = PrefUtil.get(getContext(), BREAKTIME, 5);

                    mProgressBar.setMax(runTime * 60 + 2); // setMax by sec
                    Timerhandler = new TimerHandler();
                    updateLeftTime();
                    mTimerService.setRunTimeTaskName(runTime, mItemNameText.getText().toString());
                    mStateBttn.setText(R.string.stop);
                    Timerhandler.sendEmptyMessage(0);

                }
            } else {
                getActivity().stopService(intent); //stop service
                stopUpdateLeftTime();
                mTimerService.stopCountNotification();
                mProgressBar.setProgress(0);
                Timerhandler.removeMessages(0);
                progressBarValue = 0; //must be set 0
                mStateBttn.setText(R.string.start);

                /*set mStatus to TO DO(0)*/
                query = "UPDATE " + ItemContract.ItemEntry.TABLE_NAME + " SET status = '" + ItemContract.ItemEntry.STATUS_TODO + "' WHERE _ID = '" + mId + "';";
                dbUpdate(query);
            }
        }
    };

    public void updateLeftTime() {

        mReadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    //check out if it is still available
                    if (getActivity() == null)
                        return;

                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTimeText.setText(mTimerService.getTime());
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
        mTimeText.setText("");
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
                Timerhandler.sendEmptyMessageDelayed(0, 1000); //increase by sec
            } else { // Timer must be finished
                mProgressBar.setProgress(0);
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

    /**
     * This function set TimerFragment once listItem was clicked
     */


    public void setTimerFragment(int mId, int mStatus, int mUnit, int mTotalUnit, String mName) {
        this.mId = mId;
        this.mStatus = mStatus;
        this.mUnit = mUnit;
        this.mTotalUnit = mTotalUnit;
        this.mName = mName;
        this.mStateBttn.setEnabled(true);
        if (mCountTimer % 2 == 1) {
            //should keep setting when the breakTimer hasn't run yet
            mItemNameText.setText(mName);
        }
    }

    public void setDeleteItemDisable(int dId) {
        //once the Item became deleted
        if (dId == mId) {
            /*set the button disable*/
            mStateBttn.setEnabled(false);
            mItemNameText.setText("Deleted");
        }
    }
}
