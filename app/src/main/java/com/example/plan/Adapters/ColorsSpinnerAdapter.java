package com.example.plan.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plan.R;

import java.util.ArrayList;

public class ColorsSpinnerAdapter extends BaseAdapter {

    private int mColors[];
    private LayoutInflater mInflater;
    private ArrayList<Integer> colorsList;

    public ColorsSpinnerAdapter(Context applicationContext, int[] colors) {

        colorsList = new ArrayList<>();
        for (int i:colors){
            colorsList.add(i);
        }
        mColors = colors;
        mInflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return mColors.length;
    }

    @Override
    public Object getItem(int position) {
        return colorsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.colors_spinner_item, parent,false);
        }
        ImageView color_image = convertView.findViewById(R.id.imageView);

        color_image.setImageResource(mColors[position]);
        return convertView;

    }
}