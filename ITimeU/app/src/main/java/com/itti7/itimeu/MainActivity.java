package com.itti7.itimeu;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    // ViewPager for movement between tabs
    ViewPager viewPager;

    // Tag of TimerFragment
    String mTimerTag;

    /** Getter/Setter of TimerTag */
    public void setTimerTag(String timerTag) {
        mTimerTag = timerTag;
    }

    public String getTimerTag(){
        return mTimerTag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.list).setText(R.string.tab_list));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.clock).setText(R.string.tab_timer));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.statistics).
                setText(R.string.tab_statistics));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.setting).
                setText(R.string.tab_setting));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.about).setText(R.string.tab_about));

        // ViewPager for swiping and navigation to the selected tab
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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
}
