package com.itti7.itimeu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;

public class SettingActivity extends AppCompatActivity {

    private SeekBar worksb, breaksb, longBreaksb, sessionNumsb;
    private int workTime = 25;
    private int breakTime = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        worksb = (SeekBar) findViewById(R.id.work_seek);
        breaksb = (SeekBar) findViewById(R.id.break_seek);
        longBreaksb = (SeekBar) findViewById(R.id.long_break_seek);
        sessionNumsb = (SeekBar) findViewById(R.id.session_number_seek);

        worksb.setProgress(workTime);
        breaksb.setProgress(breakTime);

        worksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        });

        breaksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        });

        longBreaksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        });

        sessionNumsb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        });
    }

    public void printSelected(SeekBar bar, int value) {
        EditText et;
        switch (bar.getId()) {
            case R.id.work_seek:
                et = (EditText) findViewById(R.id.work_time);
                et.setText(String.valueOf(value));
                break;
            case R.id.break_seek:
                et = (EditText) findViewById(R.id.break_time);
                et.setText(String.valueOf(value));
                break;
            case R.id.long_break_seek:
                et = (EditText) findViewById(R.id.long_break_time);
                et.setText(String.valueOf(value));
                break;
            case R.id.session_number_seek:
                et = (EditText) findViewById(R.id.session_number);
                et.setText(String.valueOf(value));
                break;
        }
    }

    public void doAfterTrack(SeekBar bar) {
        EditText et;
        switch (bar.getId()) {
            case R.id.work_seek:
                et = (EditText) findViewById(R.id.work_time);
                et.setText(et.getText());
                break;
            case R.id.break_seek:
                et = (EditText) findViewById(R.id.break_time);
                et.setText(et.getText());
                break;
            case R.id.long_break_seek:
                et = (EditText) findViewById(R.id.long_break_time);
                et.setText(et.getText());
                break;
            case R.id.session_number_seek:
                et = (EditText) findViewById(R.id.session_number);
                et.setText(et.getText());
                break;
        }
    }


}
