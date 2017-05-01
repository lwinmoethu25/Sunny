package com.vicky7230.sunny.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vicky7230.sunny.R;

import java.util.ArrayList;

/**
 * Created by agrim on 1/5/17.
 */

public class CityListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> cityArrayList;


    public CityListAdapter(Context context, ArrayList<String> cityArrayList) {
        this.context = context;
        this.cityArrayList = cityArrayList;
    }

    @Override
    public int getCount() {
        return cityArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.city_list_view_item, parent, false);
        }

        TextView cityTextView = (TextView) convertView.findViewById(R.id.city_name);
        cityTextView.setText(cityArrayList.get(position));

        TextView countryTextView = (TextView) convertView.findViewById(R.id.country_name);
        countryTextView.setText("India");

        View divider = convertView.findViewById(R.id.divider);
        if (position == (cityArrayList.size() - 1))
            divider.setVisibility(View.GONE);
        else
            divider.setVisibility(View.VISIBLE);

        return convertView;
    }
}
