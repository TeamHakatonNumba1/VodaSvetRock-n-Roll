package com.jkhteam.jkh_monitoring.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkhteam.jkh_monitoring.R;
import com.jkhteam.jkh_monitoring.activities.MainActivity;
import com.jkhteam.jkh_monitoring.adapters.RVAdapter;
import com.jkhteam.jkh_monitoring.model.ElectricSupplySiteParser;


public class TabPower extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_power_fragment,container,false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayoutPower);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        RVAdapter adapter = new RVAdapter(MainActivity.getNews(ElectricSupplySiteParser.SOURCE_CODE));
        rv.setAdapter(adapter);
        return v;
    }

    public void stopRefreshing(){
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        Log.d("TabPower", "Refreshing stopped");
    }

    @Override
    public void onRefresh() {
        Log.d("TabPower", "Refreshing...");
        MainActivity sActivity = (MainActivity) getActivity();
        sActivity.requestNews();
    }
}