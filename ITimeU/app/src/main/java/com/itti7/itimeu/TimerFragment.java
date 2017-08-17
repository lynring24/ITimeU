package com.itti7.itimeu;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
    /*timer components*/
    private View header;
    private TextView mTimeText;
    private String mWorkTime; //R.id.work_time
    private String mBreakTime; //R.id.work_time
    private String mLongBreakTime; //R.id.work_time
    private ProgressBar mProgressBar;
    private Button mStateBttn;
    /*button state value*/
    final boolean STATE_PLAY=true;
    final boolean STATE_STOP=false;
    private boolean state=STATE_STOP;
    /*progressBar state value*/
    private TimerHandler handler;
    private int progressBarValue = 0;
    /*timer calc*/

    private Timer mCalcTimer;
    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View timerView = inflater.inflate(R.layout.fragment_timer, container, false);

        /*progressBar button init*/
        mProgressBar = (ProgressBar)timerView.findViewById(R.id.progressBar);
        mStateBttn = (Button)timerView.findViewById(R.id.state_bttn_view);
        mStateBttn.setOnClickListener(stateChecker);

        /*Time Text Initialize */
        mTimeText = (TextView)timerView.findViewById(R.id.time_txt_view);

        /*work time 을 갖고 오기위해 inflater*/
        header = getActivity().getLayoutInflater().inflate(R.layout.fragment_setting, null, false);

        mWorkTime = ((EditText) header.findViewById(R.id.work_time)).getText().toString();
        mBreakTime= ((EditText) header.findViewById(R.id.break_time)).getText().toString();
        mLongBreakTime= ((EditText) header.findViewById(R.id.long_break_time)).getText().toString();

        final int time = Integer.parseInt(mWorkTime);
        mCalcTimer = new Timer(2*1000*60,1000);

        handler = new TimerHandler();
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

    public class Timer extends CountDownTimer{
        Timer(long total,long interval){
            super(total,interval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            String hour = String.format("%02d",(millisUntilFinished / (1000*60*60)) );
            String min = String.format("%02d",(millisUntilFinished) / (1000*60) );
            String sec = String.format("%02d",(millisUntilFinished/1000) %60);
            mTimeText.setText(hour+":"+min+":"+sec);
        }
        @Override
        public void onFinish() {
                        /*alarm or vibration*/
            // We want the alarm to go off 30 seconds from now.
            handler.removeMessages(0);
            final int time = Integer.parseInt(mBreakTime);
            mCalcTimer = new Timer(time*1000*60,1000);

        }
    }
    public class TimerHandler extends Handler{
        TimerHandler(){
            super();
        }
        @Override
        public void handleMessage(android.os.Message msg)
        {
            if(state)
            {
                progressBarValue++; // match to sec
            }

            mProgressBar.bringToFront(); // bring the progressbar to the top
            mProgressBar.setProgress(progressBarValue);
            handler.sendEmptyMessageDelayed(0, 1000); //increase by sec
        }
    }
}
