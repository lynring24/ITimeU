package com.itti7.itimeu;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    // ViewPager for movement between tabs
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.list).setText("list"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.clock).setText("timer"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.statistics).setText("statistics"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.setting).setText("setting"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.about).setText("about"));

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
                = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
    }
}
