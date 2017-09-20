package com.itto7.itimeu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // ViewPager for movement between tabs
    ViewPager viewPager;

    // Tags of Fragments
    String mTimerTag;
    String mListTag;
    String mStatisticsTag;

    /** Getter/Setter of TimerTag */
    public void setTimerTag(String timerTag) {
        mTimerTag = timerTag;
    }

    public String getTimerTag(){
        return mTimerTag;
    }
    /** Getter/Setter of ListTag */
    public void setListTag(String listTag) {
        mListTag = listTag;
    }

    public String getListTag(){
        return mListTag;
    }

    public String getStatisticsTag() {
        return mStatisticsTag;
    }

    public void setStatisticsTag(String statisticsTag) {
        mStatisticsTag = statisticsTag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Add tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.list_selector).setText(R.string.tab_list));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.timer_selector).setText(R.string.tab_timer));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.statistics_selector).
                setText(R.string.tab_statistics));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.setting_selector).
                setText(R.string.tab_setting));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.about_selector).setText(R.string.tab_about));

        // ViewPager for swiping and navigation to the selected tab
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setBackgroundColor(Color.WHITE);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Hide keyboard
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Setting pager adapter
        SimpleFragmentPagerAdapter adapter
                = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);

        showIntroSlideWhenFirst();
    }

    /**
     * @return  Return existing viewpager
     * */
    public ViewPager getViewPager() {
        if (null == viewPager) {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
        }
        return viewPager;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        TimerFragment timerFragment = (TimerFragment) getSupportFragmentManager().findFragmentByTag(mTimerTag);
        timerFragment.setStatusToDo();
    }

    void showIntroSlideWhenFirst(){
        //  Declare a new thread to do a preference check
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    final Intent introSlideIntent = new Intent(MainActivity.this, IntroSlide.class);

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            startActivity(introSlideIntent);
                        }
                    });

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        thread.start();
    }
}
