package com.example.wattanasuk_suvisa_s2532981.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.wattanasuk_suvisa_s2532981.R;
import com.example.wattanasuk_suvisa_s2532981.model.Situation;

import java.util.ArrayList;

public class SituationAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<Situation> situations = new ArrayList<>();

    public SituationAdapter(Context context) {
        this.context = context;
    }


    public void setSituations(ArrayList<Situation> newSituations) {
        situations = newSituations;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() { return situations.size(); }

    @Override
    public Situation getItem(int position) { return situations.get(position); }

    @Override
    public long getItemId(int position) { return position; }


    @Override
    public View getView(int position, View row, ViewGroup parent) {
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.item_situation, parent, false);
        }

        Situation s = situations.get(position);

        TextView title = row.findViewById(R.id.rowTitle);
        TextView description = row.findViewById(R.id.rowDescription);
        TextView info = row.findViewById(R.id.rowInfo);

        title.setText(s.getRoad() + " · " + s.getNiceType());
        description.setText(s.getDescription());
        info.setText(s.getCategory() + " · Started " + s.getValidity().getStartText());

        return row;
    }
}