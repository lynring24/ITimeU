package com.itti7.itimeu;

import android.support.v4.app.*;
import android.support.v4.app.ListFragment;

/**
 * Created by hyemin on 17. 8. 16.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    public SimpleFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) return new ListItemFragment();
        else if(position == 1) return new TimerFragment();
        else if(position == 2) return new StatisticsFragment();
        else if(position == 3) return new SettingFragment();
        else return new AboutFragment();
    }

    @Override
    public int getCount() {
        return 5;
    }
}
