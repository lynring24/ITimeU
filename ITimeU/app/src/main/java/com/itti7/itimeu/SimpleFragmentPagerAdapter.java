package com.itti7.itimeu;

import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.app.ListFragment;

/**
 * Created by hyemin on 17. 8. 16.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    int mNumbOfTabs;

    public SimpleFragmentPagerAdapter(FragmentManager fm, int NumbOfTabs){
        super(fm);
        this.mNumbOfTabs = NumbOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new ListItemFragment();
            case 1: return new TimerFragment();
            case 2: return new StatisticsFragment();
            case 3: return new SettingFragment();
            case 4: return new AboutFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumbOfTabs;
    }
}
