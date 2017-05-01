package com.vicky7230.sunny.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.joanzapata.iconify.widget.IconTextView;
import com.vicky7230.sunny.R;
import com.vicky7230.sunny.retrofitPojo.currentWeather.CurrentWeather;
import com.vicky7230.sunny.utils.RetrofitApi;
import com.vicky7230.sunny.utils.Util;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = WeatherActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 200;
    public static final String METRIC = "metric";

    private GoogleApiClient googleApiClient;

    @SuppressWarnings("FieldCanBeLocal")
    private Location lastLocation;

    private TextView tempTextView;
    private TextView tempHighTextView;
    private TextView tempLowTextView;
    private TextView locationTextView;
    private TextView weatherTextView;
    private TextView windTextView;

    private IconTextView weatherIcon;
    private IconTextView timeIcon;

    private LocationRequest locationRequest;
    private FloatingActionButton addCityButton;

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

        tempTextView = (TextView) findViewById(R.id.temp);
        tempHighTextView = (TextView) findViewById(R.id.temp_high);
        tempLowTextView = (TextView) findViewById(R.id.temp_low);
        locationTextView = (TextView) findViewById(R.id.location);
        weatherIcon = (IconTextView) findViewById(R.id.weather_icon);
        weatherTextView = (TextView) findViewById(R.id.weather);
        windTextView = (TextView) findViewById(R.id.wind);
        timeIcon = (IconTextView) findViewById(R.id.time_icon);

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

    private void showAddCityActivity() {

        startActivity(new Intent(this, AddCityActivity.class));

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
            getCurrentWeatherData(String.valueOf(lastLocation.getLatitude()), String.valueOf(lastLocation.getLongitude()));

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
        getCurrentWeatherData(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

    }

    private void getCurrentWeatherData(String lat, String lon) {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<CurrentWeather> responseBodyCall = apiInterface.getCurrentWeather(
                lat,
                lon,
                RetrofitApi.API_KEY,
                METRIC
        );

        responseBodyCall.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {

                if (response.isSuccessful()) {


                    CurrentWeather currentWeather = response.body();

                    displayCurrentWeather(currentWeather);


                } else {

                    Toast.makeText(WeatherActivity.this, "Some Error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

                Toast.makeText(WeatherActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayCurrentWeather(CurrentWeather currentWeather) {

        timeIcon.setText("{ion-android-time 20sp} " + Util.getCurrentTime());

        tempTextView.setText(currentWeather.getMain().getTemp().toString());

        locationTextView.setText(currentWeather.getName() + "," + currentWeather.getSys().getCountry());

        weatherTextView.setText(currentWeather.getWeather().get(0).getMain());

        tempHighTextView.setText("H " + currentWeather.getMain().getTempMax().toString());
        tempLowTextView.setText("  L " + currentWeather.getMain().getTempMin().toString());

        windTextView.setText("Winds : " + currentWeather.getWind().getSpeed().toString() + " m/s");

        if (currentWeather.getWeather().get(0).getIcon().equals("01d"))
            weatherIcon.setText("{mc-sun-o}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("01n"))
            weatherIcon.setText("{mc-moon}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("02d"))
            weatherIcon.setText("{mc-sun-cloud-o}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("02n"))
            weatherIcon.setText("{mc-moon-cloud}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("03d"))
            weatherIcon.setText("{mc-cloud-o}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("03n"))
            weatherIcon.setText("{mc-cloud}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("04d"))
            weatherIcon.setText("{mc-cloud-o}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("04n"))
            weatherIcon.setText("{mc-cloud}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("09d"))
            weatherIcon.setText("{mc-cloud-rain-o}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("09n"))
            weatherIcon.setText("{mc-cloud-rain}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("10d"))
            weatherIcon.setText("{mc-cloud-drop-o}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("10n"))
            weatherIcon.setText("{mc-cloud-drop}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("11d"))
            weatherIcon.setText("{mc-cloud-double-thunder2-o}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("11n"))
            weatherIcon.setText("{mc-cloud-double-thunder}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("13d"))
            weatherIcon.setText("{mc-cloud-snow3-o}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("13n"))
            weatherIcon.setText("{mc-cloud-snow2}");
        else if (currentWeather.getWeather().get(0).getIcon().equals("50d") || currentWeather.getWeather().get(0).getIcon().equals("50n"))
            weatherIcon.setText("{mc-sea-o}");
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
