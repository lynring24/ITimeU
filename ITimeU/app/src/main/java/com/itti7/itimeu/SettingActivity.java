package com.itti7.itimeu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;

public class SettingActivity extends AppCompatActivity {

    private SeekBar worksb;
    private int workTime = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        worksb = (SeekBar) findViewById(R.id.work_seek);
        worksb.setProgress(workTime);

        worksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack();
            }

        });
    }

    public void printSelected(int value) {
        EditText et = (EditText) findViewById(R.id.work_time);
        et.setText(String.valueOf(value));
    }

    public void doAfterTrack() {
        EditText et = (EditText) findViewById(R.id.work_time);
        et.setText(et.getText());
    }
}
