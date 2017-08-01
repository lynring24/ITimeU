package com.itti7.itimeu;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TimerFragment extends AppCompatActivity {
    private TextView timeText;
    private ProgressBar progressBar;
    private Button stateBttn;

    final boolean STATE_PLAY=true;
    final boolean STATE_STOP=false;
    private boolean state=STATE_STOP;

    Handler handler;
    int progressBarValue = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_timer);
        /*Toolbar init*/
        Toolbar myToolbar = (Toolbar) findViewById(R.id.menu_toolbar);
        setSupportActionBar(myToolbar);

        /*progressBar button init*/
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        stateBttn = (Button)findViewById(R.id.state_bttn_view);
        stateBttn.setOnClickListener(stateChecker);
        timeText = (TextView)findViewById(R.id.time_txt_view);
        handler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                if(state)
                {
                    progressBarValue++;
                }
                progressBar.setProgress(progressBarValue);
                timeText.setText(String.valueOf(progressBarValue/60)+":"+String.valueOf(progressBarValue%60));

                handler.sendEmptyMessageDelayed(0, 100);
            }
        };

        handler.sendEmptyMessage(0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId(); //pressed item ID
        if (id == R.id.action_setting) {
                /*Intent passTo  = new Intent(getApplicationContext(),SettingActivity);
                * startActivity(passTo);*/
//            Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show(); // test code
            return true;
        }
        if (id == R.id.action_list) {
            /*Intent passTo  = new Intent(getApplicationContext(),ListActivity);
                * startActivity(passTo);*/
            return true;
        }
        if (id == R.id.action_statistics) {
            /*Intent passTo  = new Intent(getApplicationContext(),StatisticsActivity);
                * startActivity(passTo);*/
            return true;
        }
        return super.onOptionsItemSelected(item);
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
