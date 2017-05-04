package com.vicky7230.sunny.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.couchbase.lite.Database;
import com.vicky7230.sunny.R;
import com.vicky7230.sunny.adapter.SavedCityListAdapter;
import com.vicky7230.sunny.couchDB.CouchBaseHelper;
import com.vicky7230.sunny.pojo.Remove;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class SavedCitiesActivity extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private Toolbar toolbar;
    @SuppressWarnings("FieldCanBeLocal")
    private ListView cityListView;
    private ArrayList<String> savedCityArrayList;
    private SavedCityListAdapter savedCityListAdapter;

    private boolean cityRemoved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_cities);

        init();
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cityListView = (ListView) findViewById(R.id.city_list_view);

        Database database = CouchBaseHelper.openCouchBaseDB(this);
        savedCityArrayList = CouchBaseHelper.getCitiesFromTheDB(database);
        savedCityListAdapter = new SavedCityListAdapter(this, savedCityArrayList);
        cityListView.setAdapter(savedCityListAdapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                if (cityRemoved) {
                    setResult(RESULT_OK);
                }

                finish();

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemoveButtonClicked(Remove remove) {

        Database database = CouchBaseHelper.openCouchBaseDB(this);
        CouchBaseHelper.removeCityFromDB(database, savedCityArrayList.get(remove.getPosition()));

        savedCityArrayList.remove(remove.getPosition());
        savedCityListAdapter.notifyDataSetChanged();

        cityRemoved = true;
    }


    @Override
    public void onBackPressed() {

        if (cityRemoved) {
            setResult(RESULT_OK);
        }

        super.onBackPressed();
    }
}

