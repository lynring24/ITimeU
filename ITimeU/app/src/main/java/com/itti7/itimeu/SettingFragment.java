package com.itti7.itimeu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;


import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends Fragment {

    private View mSettingView;
    private Activity mSettingActivity;
    private Context mSettingContext;

    private SeekBar mworksb, mbreaksb, mlongBreaksb, msessionNumsb; //시크바
    private static EditText mworket, mbreaket, mlongBreaket, msessionNumet; //에디트텍스트 뷰
    private static CheckBox msoundOn, mvibrateOn, monScreen;

    public static final String PREFNAME = "SETTING_PREFERENCE";
    public static final String WORKTIME = "worktime";
    public static final String BREAKTIME = "breaktime";
    public static final String LONGBREAKTIME = "longbreaktime";
    public static final String SESSION = "session";
    //설정 저장에 필요한 상수(이름)

    public static EditText getMworket() {
        return mworket;
    }

    public static EditText getMbreaket() {
        return mbreaket;
    }

    public static EditText getMlongBreaket() {
        return mlongBreaket;
    }

    public static EditText getMsessionNumet() {
        return msessionNumet;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mSettingView = inflater.inflate(R.layout.fragment_setting, container, false);
        mSettingActivity = getActivity();
        mSettingContext = mSettingView.getContext();

        mworksb = mSettingView.findViewById(R.id.work_seek);
        mbreaksb = mSettingView.findViewById(R.id.break_seek);
        mlongBreaksb = mSettingView.findViewById(R.id.long_break_seek);
        msessionNumsb = mSettingView.findViewById(R.id.session_number_seek);
        /////////각 시크바

        mworket = mSettingView.findViewById(R.id.work_time);
        mbreaket = mSettingView.findViewById(R.id.break_time);
        mlongBreaket = mSettingView.findViewById(R.id.long_break_time);
        msessionNumet = mSettingView.findViewById(R.id.session_number);
        ///////각 에디트텍스트

        monScreen = mSettingView.findViewById(R.id.screen_check);
        msoundOn = mSettingView.findViewById(R.id.sound_check);
        mvibrateOn = mSettingView.findViewById(R.id.vibrate_check);

        //저장해둔 설정 불러오기
        SharedPreferences pref = getActivity().getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        mworket.setText(String.valueOf(pref.getInt(WORKTIME, 25)));
        mbreaket.setText(String.valueOf(pref.getInt(BREAKTIME, 5)));
        mlongBreaket.setText(String.valueOf(pref.getInt(LONGBREAKTIME, 20)));
        msessionNumet.setText(String.valueOf(pref.getInt(SESSION, 4)));

        mworksb.setProgress(pref.getInt(WORKTIME, 25));
        mbreaksb.setProgress(pref.getInt(BREAKTIME, 5));
        mlongBreaksb.setProgress(pref.getInt(LONGBREAKTIME, 20));
        msessionNumsb.setProgress(pref.getInt(SESSION, 4));

        mworksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        }); //워크타임 시크바 리스너

        mbreaksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        }); //브레이크타임 시크바 리스터

        mlongBreaksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        }); //롱브레이크타임 시크바 리스너

        msessionNumsb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar)  {
                doAfterTrack(seekBar);
            }
        }); //세션 수 시크바 리스너

        return mSettingView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void printSelected(SeekBar bar, int value) { // 이용자가 바를 누르고 있을 때의 숫자 출력
        if (bar.equals(mworksb)) {
            mworket.setText(String.valueOf(value));
        }
        else if (bar.equals(mbreaksb)) {
            mbreaket.setText(String.valueOf(value));
        }
        else if (bar.equals(mlongBreaksb)) {
            mlongBreaket.setText(String.valueOf(value));
        }
        else if (bar.equals(msessionNumsb)) {
            msessionNumet.setText(String.valueOf(value));
        }
    }

    public void doAfterTrack(SeekBar bar) { // 이용자가 손을 뗐을 때의 숫자 출력
        SharedPreferences pref = getActivity().getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (bar.equals(mworksb)) {
            mworket.setText(mworket.getText());
            editor.putInt(WORKTIME, Integer.valueOf(mworket.getText().toString()));
        }
        else if (bar.equals(mbreaksb)) {
            mbreaket.setText(mbreaket.getText());
            editor.putInt(BREAKTIME, Integer.valueOf(mbreaket.getText().toString()));
        }
        else if (bar.equals(mlongBreaksb)) {
            mlongBreaket.setText(mlongBreaket.getText());
            editor.putInt(LONGBREAKTIME, Integer.valueOf(mlongBreaket.getText().toString()));
        }
        else if (bar.equals(msessionNumsb)) {
            msessionNumet.setText(msessionNumet.getText());
            editor.putInt(SESSION, Integer.valueOf(msessionNumet.getText().toString()));
        }
        editor.commit();
    }

} //end of class
