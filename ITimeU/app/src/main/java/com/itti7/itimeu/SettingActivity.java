package com.itti7.itimeu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;

public class SettingActivity extends AppCompatActivity {

    private SeekBar mworksb, mbreaksb, mlongBreaksb, msessionNumsb; //시크바
    private EditText mworket, mbreaket, mlongBreaket, msessionNumet; //에디트텍스트 뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mworksb = (SeekBar) findViewById(R.id.work_seek);
        mbreaksb = (SeekBar) findViewById(R.id.break_seek);
        mlongBreaksb = (SeekBar) findViewById(R.id.long_break_seek);
        msessionNumsb = (SeekBar) findViewById(R.id.session_number_seek);

        mworket = (EditText) findViewById(R.id.work_time);
        mbreaket = (EditText) findViewById(R.id.break_time);
        mlongBreaket = (EditText) findViewById(R.id.long_break_time);
        msessionNumet = (EditText) findViewById(R.id.session_number);

        mworksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        });

        mbreaksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        });

        mlongBreaksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        });

        msessionNumsb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        if (bar.equals(mworksb)) {
            mworket.setText(String.valueOf(value));
        }
        else if (bar.equals((mbreaksb))) {
            mbreaket.setText(String.valueOf(value));
        }
        else if (bar.equals(mlongBreaksb)) {
            mlongBreaket.setText(String.valueOf(value));
        }
        else if (bar.equals(msessionNumsb)) {
            msessionNumet.setText(String.valueOf(value));
        }
    }

    public void doAfterTrack(SeekBar bar) {
        if (bar.equals(mworksb)) {
            mworket.setText(mworket.getText());
        }
        else if (bar.equals(mbreaksb)) {
            mbreaket.setText(mbreaket.getText());
        }
        else if (bar.equals(mlongBreaksb)) {
            mlongBreaket.setText(mlongBreaket.getText());
        }
        else if (bar.equals(msessionNumsb)) {
            msessionNumet.setText(msessionNumet.getText());
        }
    }


}
