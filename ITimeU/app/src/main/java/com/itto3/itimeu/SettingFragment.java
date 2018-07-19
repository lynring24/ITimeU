package com.itto3.itimeu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

import com.itto3.itimeu.data.SharedPreferenceUtil;

public class SettingFragment extends Fragment {
    private View mSettingView;
    private Activity mSettingActivity;
    private Context mSettingContext;

    private SeekBar mworksb, mbreaksb, mlongBreaksb, msessionNumsb; //시크바
    private EditText mworket, mbreaket, mlongBreaket, msessionNumet; //에디트텍스트 뷰
    private CheckBox msoundOncb, mvibrateOncb, continuousCheckbox; //체크박스

    public static final String WORKTIME = "worktime";
    public static final String BREAKTIME = "breaktime";
    public static final String LONGBREAKTIME = "longbreaktime";
    public static final String SESSION = "session";
    public static final String SCREENON = "screen"; //boolean, 참이면 켜짐
    public static final String SOUNDON = "sound";
    public static final String VIBRATEON = "vibrate";
    public static final String CONTINUOUS_OPTION = "continuous";

    //설정 저장에 필요한 상수(이름)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
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

        //각 체크박스
        msoundOncb = mSettingView.findViewById(R.id.sound_check);
        mvibrateOncb = mSettingView.findViewById(R.id.vibrate_check);
        continuousCheckbox = mSettingView.findViewById(R.id.setting_continuous_timer_check);

        //저장해둔 숫자 설정 불러오기
        mworket.setText(String.valueOf(SharedPreferenceUtil.get(getContext(), WORKTIME, 25)));
        mbreaket.setText(String.valueOf(SharedPreferenceUtil.get(getContext(), BREAKTIME, 5)));
        mlongBreaket.setText(String.valueOf(SharedPreferenceUtil.get(getContext(), LONGBREAKTIME, 20)));
        msessionNumet.setText(String.valueOf(SharedPreferenceUtil.get(getContext(), SESSION, 4)));

        final EditText[] editTexts = {mworket, mbreaket, mlongBreaket, msessionNumet};
        final SeekBar[] seekBars = {mworksb, mbreaksb, mlongBreaksb, msessionNumsb};
        final String[] constants = {WORKTIME, BREAKTIME, LONGBREAKTIME, SESSION};

        for (int i = 0; i < editTexts.length; i++) {
            final int finalI = i;
            editTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        seekBars[finalI].setProgress(Integer.parseInt(s.toString()));
                    } else {
                        seekBars[finalI].setProgress(1);
                        editTexts[finalI].setText("1");
                    }
                    SharedPreferenceUtil.save(getActivity(), constants[finalI],
                            Integer.parseInt(editTexts[finalI].getText().toString()));
                }
            });
        }

        //숫자-시크바 연동

        mbreaksb.setProgress(Integer.parseInt(mbreaket.getText().toString()));
        mlongBreaksb.setProgress(Integer.parseInt(mlongBreaket.getText().toString()));
        msessionNumsb.setProgress(Integer.parseInt(msessionNumet.getText().toString()));

        //체크박스 설정 불러오기
        msoundOncb.setChecked(SharedPreferenceUtil.get(getContext(), SOUNDON, true));
        mvibrateOncb.setChecked(SharedPreferenceUtil.get(getContext(), VIBRATEON, false));
        continuousCheckbox.setChecked(SharedPreferenceUtil.get(getContext(), CONTINUOUS_OPTION, false));

        mworksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                doAfterTrack(seekBar);
            }
        }); //워크타임 시크바 리스너

        mbreaksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                doAfterTrack(seekBar);
            }
        }); //브레이크타임 시크바 리스너

        mlongBreaksb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                doAfterTrack(seekBar);
            }
        }); //롱브레이크타임 시크바 리스너

        msessionNumsb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                printSelected(seekBar, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                doAfterTrack(seekBar);
            }
        }); //세션 수 시크바 리스너

        CheckBox[] checkboxes = {msoundOncb, mvibrateOncb, continuousCheckbox};
        for (CheckBox checkbox : checkboxes) {
            checkbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    saveCheckBox(buttonView, isChecked);
                }
            });
        }

        return mSettingView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void printSelected(SeekBar bar, int value) { // 이용자가 바를 누르고 있을 때의 숫자 출력
        if (bar.equals(mworksb)) {
            mworket.setText(String.valueOf(value));
        } else if (bar.equals(mbreaksb)) {
            mbreaket.setText(String.valueOf(value));
        } else if (bar.equals(mlongBreaksb)) {
            mlongBreaket.setText(String.valueOf(value));
        } else if (bar.equals(msessionNumsb)) {
            msessionNumet.setText(String.valueOf(value));
        }
    }

    private void doAfterTrack(SeekBar bar) { // 이용자가 손을 뗐을 때의 숫자 출력
        int temp;

        if (bar.equals(mworksb)) {
            temp = Integer.valueOf(mworket.getText().toString());
            if (temp < 1)
                mworket.setText("1");
            else
                mworket.setText(mworket.getText());
            SharedPreferenceUtil.save(getContext(), WORKTIME, Integer.valueOf(mworket.getText().toString()));
        } else if (bar.equals(mbreaksb)) {
            temp = Integer.valueOf(mbreaket.getText().toString());
            if (temp < 1)
                mbreaket.setText("1");
            else
                mbreaket.setText(mbreaket.getText());
            SharedPreferenceUtil.save(getContext(), BREAKTIME, Integer.valueOf(mbreaket.getText().toString()));
        } else if (bar.equals(mlongBreaksb)) {
            temp = Integer.valueOf(mlongBreaket.getText().toString());
            if (temp < 1)
                mlongBreaket.setText("1");
            else
                mlongBreaket.setText(mlongBreaket.getText());
            SharedPreferenceUtil.save(getContext(), LONGBREAKTIME, Integer.valueOf(mlongBreaket.getText().toString()));
        } else if (bar.equals(msessionNumsb)) {
            temp = Integer.valueOf(msessionNumet.getText().toString());
            if (temp < 1)
                msessionNumet.setText("1");
            else
                msessionNumet.setText(msessionNumet.getText());
            SharedPreferenceUtil.save(getContext(), SESSION, Integer.valueOf(msessionNumet.getText().toString()));
        }
    }

    private void saveCheckBox(CompoundButton cbox, boolean isChecked) {
        if (cbox.equals(msoundOncb)) {
            SharedPreferenceUtil.save(getContext(), SOUNDON, isChecked);
        } else if (cbox.equals(mvibrateOncb)) {
            SharedPreferenceUtil.save(getContext(), VIBRATEON, isChecked);
        } else if (cbox.equals(continuousCheckbox)) {
            SharedPreferenceUtil.save(getContext(), CONTINUOUS_OPTION, isChecked);
        }
    }
}
