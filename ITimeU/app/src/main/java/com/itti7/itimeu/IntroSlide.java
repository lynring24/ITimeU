package com.itti7.itimeu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

/**
 * Created by hyemin on 17. 9. 12.
 */

public class IntroSlide extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage introSliderPage1 = new SliderPage();
        introSliderPage1.setTitle(getString(R.string.intro_page1_title));
        introSliderPage1.setDescription(getString(R.string.intro_page1_description));
        introSliderPage1.setImageDrawable(R.drawable.intro_remember);
        introSliderPage1.setBgColor(Color.DKGRAY);
        addSlide(AppIntroFragment.newInstance(introSliderPage1));

        SliderPage introSliderPage2 = new SliderPage();
        introSliderPage2.setTitle(getString(R.string.intro_page2_title));
        introSliderPage2.setDescription(getString(R.string.intro_page2_description));
        introSliderPage2.setImageDrawable(R.drawable.intro_task);
        introSliderPage2.setBgColor(Color.DKGRAY);
        addSlide(AppIntroFragment.newInstance(introSliderPage2));

        SliderPage introSliderPage3 = new SliderPage();
        introSliderPage3.setTitle(getString(R.string.intro_page3_title));
        introSliderPage3.setDescription(getString(R.string.intro_page3_description));
        introSliderPage3.setImageDrawable(R.drawable.intro_short_break_time);
        introSliderPage3.setBgColor(Color.DKGRAY);
        addSlide(AppIntroFragment.newInstance(introSliderPage3));

        SliderPage introSliderPage4 = new SliderPage();
        introSliderPage4.setTitle(getString(R.string.intro_page4_title));
        introSliderPage4.setDescription(getString(R.string.intro_page4_description));
        introSliderPage4.setImageDrawable(R.drawable.intro_long_break_time);
        introSliderPage4.setBgColor(Color.DKGRAY);
        addSlide(AppIntroFragment.newInstance(introSliderPage4));

        SliderPage introSliderPage5 = new SliderPage();
        introSliderPage5.setTitle(getString(R.string.intro_page5_title));
        introSliderPage5.setDescription(getString(R.string.intro_page5_description));
        introSliderPage5.setImageDrawable(R.drawable.intro_check);
        introSliderPage5.setBgColor(Color.DKGRAY);
        addSlide(AppIntroFragment.newInstance(introSliderPage5));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
