package com.jkhteam.jkh_monitoring.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jkhteam.jkh_monitoring.fragments.TabWater;
import com.jkhteam.jkh_monitoring.fragments.TabPower;
import android.util.Log;




public class VPAdapterMain extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int NumbOfTabs;

    public VPAdapterMain(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0)
        {
            TabWater waterTab = new TabWater();
            return waterTab;
        }
        else
        {
            TabPower powerTab = new TabPower();
            return powerTab;
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

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}