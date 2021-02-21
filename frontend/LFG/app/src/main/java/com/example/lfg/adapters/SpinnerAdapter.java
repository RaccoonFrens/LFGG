package com.example.lfg.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    int resource;

    public SpinnerAdapter(@NonNull Context context, int resource, int layout) {
        super(context, resource, layout);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        return position!=0;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tvOption = (TextView) view;
        if(position == 0)
            tvOption.setTextColor(Color.GRAY);
        else
            tvOption.setTextColor(Color.BLACK);
        return view;
    }
}
