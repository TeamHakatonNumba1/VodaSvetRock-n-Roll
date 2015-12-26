package com.jkhteam.jkh_monitoring.activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.jkhteam.jkh_monitoring.R;

public class SettingsActivity extends PreferenceActivity {
    public static String street_name;
    public static String house_number;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        street_name = prefs.getString(getString(R.string.street_key), "");
        house_number = prefs.getString(getString(R.string.house_key), "");
        Log.d("tag", "tag");

    }
}
