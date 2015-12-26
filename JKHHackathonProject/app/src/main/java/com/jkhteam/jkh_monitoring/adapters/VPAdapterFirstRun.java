package com.jkhteam.jkh_monitoring.adapters;

/**
 * Created by don on 18.12.2015.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.TextView;

import com.jkhteam.jkh_monitoring.fragments.FirstRunFragment_1;
import com.jkhteam.jkh_monitoring.fragments.FirstRunFragment_2;


public class VPAdapterFirstRun extends FragmentPagerAdapter {

    CharSequence Titles[];
    int NumbOfTabs;

    public VPAdapterFirstRun(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0)
        {
            FirstRunFragment_1 initScreenFragment_1 = new FirstRunFragment_1();

            return initScreenFragment_1;
        }
        else
        {
            FirstRunFragment_2 initScreenFragment_2 = new FirstRunFragment_2();
            return initScreenFragment_2;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
