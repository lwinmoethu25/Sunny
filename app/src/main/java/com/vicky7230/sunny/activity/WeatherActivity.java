package com.vicky7230.sunny.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.vicky7230.sunny.R;
import com.vicky7230.sunny.adapter.ViewPagerAdapter;
import com.vicky7230.sunny.couchDB.CouchBaseHelper;
import com.vicky7230.sunny.fragment.CityWeatherFragment;
import com.vicky7230.sunny.fragment.CurrentWeatherFragment;
import com.vicky7230.sunny.pojo.LatLon;
import com.vicky7230.sunny.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = WeatherActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 200;
    public static final String METRIC = "metric";
    public static final String CITY = "city";
    private static final int REQUEST_CODE = 345;

    private GoogleApiClient googleApiClient;

    @SuppressWarnings("FieldCanBeLocal")
    private Location lastLocation;
    private LocationRequest locationRequest;
    @SuppressWarnings("FieldCanBeLocal")
    private FloatingActionButton addCityButton;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Util.night())
            setTheme(R.style.AppThemeNight);

        setContentView(R.layout.activity_weather);

        if (Util.night())
            changeRings();

        checkLocationStatus();

        init();
    }


    private void init() {

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(10);
        setupViewPager(viewPager);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        addCityButton = (FloatingActionButton) findViewById(R.id.add_city_button);
        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCityActivity();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new CurrentWeatherFragment());

        Database database = CouchBaseHelper.openCouchBaseDB(this);
        ArrayList<String> citiesArrayList = CouchBaseHelper.getCitiesFromTheDB(database);

        for (String city : citiesArrayList) {

            Bundle bundle = new Bundle();
            bundle.putString(CITY, city);

            Log.d(TAG, "city name : " + city);

            CityWeatherFragment cityWeatherFragment = new CityWeatherFragment();
            cityWeatherFragment.setArguments(bundle);

            viewPagerAdapter.addFragment(cityWeatherFragment);

        }

        viewPager.setAdapter(viewPagerAdapter);
    }

    private void showAddCityActivity() {

        startActivityForResult(new Intent(this, AddCityActivity.class), REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {


                /*Database database = CouchBaseHelper.openCouchBaseDB(this);
                ArrayList<String> citiesArrayList = CouchBaseHelper.getCitiesFromTheDB(database);


                if (viewPagerAdapter.getCount() < citiesArrayList.size()) {

                    Bundle bundle = new Bundle();
                    bundle.putString(CITY, citiesArrayList.get(citiesArrayList.size() - 1));

                    CityWeatherFragment cityWeatherFragment = new CityWeatherFragment();
                    cityWeatherFragment.setArguments(bundle);

                    viewPagerAdapter.addFragment(cityWeatherFragment);
                    viewPagerAdapter.notifyDataSetChanged();
                }*/

            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        requestLocationPermissionsAndStartLocationService();
    }

    private void requestLocationPermissionsAndStartLocationService() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        } else {

            googleApiClient.connect();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    //Permissions were granted
                    googleApiClient.connect();

                } else {

                    Toast.makeText(this, "Permissions Denied, Cannot continue.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //noinspection MissingPermission
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (lastLocation != null) {

            Log.d(TAG, "Lat : " + String.valueOf(lastLocation.getLatitude()) + ", Lon : " + String.valueOf(lastLocation.getLongitude()));

            //post the lat long values to current weather fragment
            EventBus.getDefault().post(new LatLon(String.valueOf(lastLocation.getLatitude()), String.valueOf(lastLocation.getLongitude())));
        }

        //noinspection MissingPermission
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.d(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        //TODO
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "Lat : " + String.valueOf(location.getLatitude()) + ", Lon : " + String.valueOf(location.getLongitude()));
        //post the lat long values to current weather fragment
        EventBus.getDefault().post(new LatLon(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())));

    }


    public void changeRings() {

        LinearLayout rings = (LinearLayout) findViewById(R.id.rings);
        rings.setBackgroundResource(R.drawable.circular_rings_dark);

        int colorWhite = Color.parseColor("#FFFFFF");
        FloatingActionButton sunAndMoon = (FloatingActionButton) findViewById(R.id.sun_and_moon);
        sunAndMoon.setBackgroundTintList(ColorStateList.valueOf(colorWhite));

        int colorDark = Color.parseColor("#403C48");
        FloatingActionButton addCityButton = (FloatingActionButton) findViewById(R.id.add_city_button);
        addCityButton.setBackgroundTintList(ColorStateList.valueOf(colorDark));

    }

    public void checkLocationStatus() {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationDisabledAlert();

        }
    }

    private void showLocationDisabledAlert() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Your LOCATION seems to be disabled, this app requires location to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert = builder.create();

        alert.show();
    }
}
