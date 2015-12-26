package com.jkhteam.jkh_monitoring.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.jkhteam.jkh_monitoring.R;
import com.jkhteam.jkh_monitoring.adapters.VPAdapterFirstRun;
import com.viewpagerindicator.CirclePageIndicator;

public class FirstRunActivity extends ActionBarActivity {

    CharSequence Titles[]={"Вода","Электричество"};
    int Numboftabs =2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        ViewPager pager;
        VPAdapterFirstRun adapter;

        adapter =  new VPAdapterFirstRun(getSupportFragmentManager(),Titles,Numboftabs);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        pager = (ViewPager) findViewById(R.id.view);
        pager.setAdapter(adapter);


        circlePageIndicator.setViewPager(pager);


    }

}
