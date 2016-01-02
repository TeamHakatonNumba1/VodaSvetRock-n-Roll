package com.jkhteam.jkh_monitoring.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;


import com.jkhteam.jkh_monitoring.activities.MainActivity;
import com.jkhteam.jkh_monitoring.model.News;

import java.util.List;

import com.jkhteam.jkh_monitoring.R;
import com.jkhteam.jkh_monitoring.model.News;

/**
 * Created by don on 30.11.2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.NewsViewHolder> {
    List<News> newsList;

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView relation;
        CardView cardView;
        TextView date;
        TextView text;
        public NewsViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            date = (TextView) itemView.findViewById(R.id.date);
            text = (TextView) itemView.findViewById(R.id.text);
            relation = (TextView) itemView.findViewById(R.id.relation);
        }
    }

    public RVAdapter(List<News> newsList){
        this.newsList = newsList;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        NewsViewHolder nvh = new NewsViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        holder.text.setText(newsList.get(position).getText());
        holder.date.setText(DateFormat.format("dd.MM.yyyy", newsList.get(position).getDate()).toString());
        if (newsList.get(position).isRelatedToUser()) {
            holder.relation.setText("В вашем районе");
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        if (newsList == null) return 0;
        return newsList.size();
    }

    
}
