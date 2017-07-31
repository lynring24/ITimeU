package com.itti7.itimeu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;

public class SettingActivity extends AppCompatActivity {

    private SeekBar worksb, breaksb, longBreaksb, sessionNumsb;
    private EditText worket, breaket, longBreaket, sessionNumet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        worksb = (SeekBar) findViewById(R.id.work_seek);
        breaksb = (SeekBar) findViewById(R.id.break_seek);
        longBreaksb = (SeekBar) findViewById(R.id.long_break_seek);
        sessionNumsb = (SeekBar) findViewById(R.id.session_number_seek);

        worket = (EditText) findViewById(R.id.work_time);
        breaket = (EditText) findViewById(R.id.break_time);
        longBreaket = (EditText) findViewById(R.id.long_break_time);
        sessionNumet = (EditText) findViewById(R.id.session_number);

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
        if (bar.equals(worksb)) {
            worket.setText(String.valueOf(value));
        }
        else if (bar.equals((breaksb))) {
            breaket.setText(String.valueOf(value));
        }
        else if (bar.equals(longBreaksb)) {
            longBreaket.setText(String.valueOf(value));
        }
        else if (bar.equals(sessionNumsb)) {
            sessionNumet.setText(String.valueOf(value));
        }
    }

    public void doAfterTrack(SeekBar bar) {
        if (bar.equals(worksb)) {
            worket.setText(worket.getText());
        }
        else if (bar.equals(breaksb)) {
            breaket.setText(breaket.getText());
        }
        else if (bar.equals(longBreaksb)) {
            longBreaket.setText(longBreaket.getText());
        }
        else if (bar.equals(sessionNumsb)) {
            sessionNumet.setText(sessionNumet.getText());
        }
    }


}
