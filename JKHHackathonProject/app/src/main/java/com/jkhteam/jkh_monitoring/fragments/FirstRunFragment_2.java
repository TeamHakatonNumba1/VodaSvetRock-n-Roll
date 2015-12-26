package com.jkhteam.jkh_monitoring.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jkhteam.jkh_monitoring.activities.MainActivity;

import com.jkhteam.jkh_monitoring.R;

/**
 * Created by don on 16.12.2015.
 */
public class FirstRunFragment_2 extends Fragment {
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.firstrun_fragment_2, container, false);
        Button button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        //TODO
        return v;
    }
}
