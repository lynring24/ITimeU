package com.itti7.itimeu;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
/*timer components*/
    private TextView timeText;
    private ProgressBar progressBar;
    private Button stateBttn;
/*button state value*/
    final boolean STATE_PLAY=true;
    final boolean STATE_STOP=false;
    private boolean state=STATE_STOP;
/*progressBar state value*/
    Handler handler;
    int progressBarValue = 0;

    public TimerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View timerView = inflater.inflate(R.layout.activity_timer, container, false);


        /*progressBar button init*/
        progressBar = (ProgressBar)timerView.findViewById(R.id.progressBar);
        stateBttn = (Button)timerView.findViewById(R.id.state_bttn_view);
        stateBttn.setOnClickListener(stateChecker);
        timeText = (TextView)timerView.findViewById(R.id.time_txt_view);
        handler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                if(state)
                {
                    progressBarValue++;
                }
                progressBar.setProgress(progressBarValue);
                timeText.setText("25 : 00");
/*                timeText.setText(String.valueOf(progressBarValue/60)+":"+String.valueOf(progressBarValue%60));*/
                handler.sendEmptyMessageDelayed(0, 100);
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
                stateBttn.setText(R.string.stop);
            }
            else{
                progressBarValue=0;
                timeText.setText(R.string.time);
                stateBttn.setText(R.string.start);
            }
        }
    };
}
