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
        if(position == 0) return new ListFragment();
        else return new TimerFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
