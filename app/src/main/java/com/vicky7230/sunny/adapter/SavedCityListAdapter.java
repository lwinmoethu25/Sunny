package com.vicky7230.sunny.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.vicky7230.sunny.R;
import com.vicky7230.sunny.pojo.Remove;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by agrim on 1/5/17.
 */

public class SavedCityListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> cityArrayList;


    public SavedCityListAdapter(Context context, ArrayList<String> cityArrayList) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.saved_city_list_view_item, parent, false);
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

        IconTextView removeButton = (IconTextView) convertView.findViewById(R.id.remove_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(new Remove(position));

            }
        });

        return convertView;
    }
}
