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
    }

    public void printSelected(SeekBar bar, int value) {
        switch (bar.getId()) {
            case R.id.work_seek:
                EditText et = (EditText) findViewById(R.id.work_time);
                et.setText(String.valueOf(value));
        }
    }

    public void doAfterTrack(SeekBar bar) {
        switch (bar.getId()) {
            case R.id.work_seek:
                EditText et = (EditText) findViewById(R.id.work_time);
                et.setText(et.getText());
        }
    }


}
