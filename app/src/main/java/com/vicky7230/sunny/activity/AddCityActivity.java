package com.vicky7230.sunny.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.couchbase.lite.Database;
import com.vicky7230.sunny.R;
import com.vicky7230.sunny.adapter.CityListAdapter;
import com.vicky7230.sunny.couchDB.CouchBaseHelper;
import com.vicky7230.sunny.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddCityActivity extends AppCompatActivity {

    public static final String NEW_CITY_ADDED = "newCityAdded";
    private EditText cityEditText;
    private ListView cityListView;

    private ArrayList<String> allCityArrayList;
    private ArrayList<String> suggestionsArrayList;
    private CityListAdapter cityListAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Util.night())
            setTheme(R.style.AppThemeNight);
        setContentView(R.layout.activity_add_city);

        init();
    }

    private void init() {

        String[] cityArray = getResources().getStringArray(R.array.india_top_places);
        allCityArrayList = new ArrayList<>(Arrays.asList(cityArray));
        suggestionsArrayList = new ArrayList<>(Arrays.asList(cityArray));
        cityListAdapter = new CityListAdapter(this, suggestionsArrayList);
        cityListView = (ListView) findViewById(R.id.city_list_view);
        cityListView.setAdapter(cityListAdapter);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Database database = CouchBaseHelper.openCouchBaseDB(AddCityActivity.this);

                if (CouchBaseHelper.getCitiesFromTheDB(database).size() < 4) {

                    CouchBaseHelper.saveCityInDB(database, suggestionsArrayList.get(position));

                    Intent intent = new Intent();
                    intent.putExtra(NEW_CITY_ADDED, suggestionsArrayList.get(position));

                    setResult(RESULT_OK, intent);

                } else {

                    setResult(RESULT_OK);

                }

                finish();

            }
        });

        cityEditText = (EditText) findViewById(R.id.city_edit_text);

        cityEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (cityEditText.getRight() - cityEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        cityEditText.setText("");//clear the text

                        return true;
                    }
                }
                return false;
            }
        });

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                suggestionsArrayList.clear();

                for (String city : allCityArrayList) {

                    if (city.toLowerCase().startsWith(cityEditText.getText().toString().toLowerCase()))
                        suggestionsArrayList.add(city);
                }

                cityListAdapter.notifyDataSetChanged();

            }
        });
    }
}
