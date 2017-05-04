package com.vicky7230.sunny.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.widget.IconTextView;
import com.vicky7230.sunny.R;
import com.vicky7230.sunny.activity.WeatherActivity;
import com.vicky7230.sunny.pojo.LatLon;
import com.vicky7230.sunny.retrofitPojo.currentWeather.CurrentWeather;
import com.vicky7230.sunny.utils.RetrofitApi;
import com.vicky7230.sunny.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentWeatherFragment extends Fragment {

    private TextView tempTextView;
    private TextView tempHighTextView;
    private TextView tempLowTextView;
    private TextView locationTextView;
    private TextView weatherTextView;
    private TextView windTextView;

    private IconTextView weatherIcon;
    private IconTextView timeIcon;


    public CurrentWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);


        tempTextView = (TextView) view.findViewById(R.id.temp);
        tempHighTextView = (TextView) view.findViewById(R.id.temp_high);
        tempLowTextView = (TextView) view.findViewById(R.id.temp_low);
        locationTextView = (TextView) view.findViewById(R.id.location);
        weatherIcon = (IconTextView) view.findViewById(R.id.weather_icon);
        weatherTextView = (TextView) view.findViewById(R.id.weather);
        windTextView = (TextView) view.findViewById(R.id.wind);
        timeIcon = (IconTextView) view.findViewById(R.id.time_icon);

        return view;


    }

    private void getCurrentWeatherData(String lat, String lon) {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<CurrentWeather> responseBodyCall = apiInterface.getCurrentWeather(
                lat,
                lon,
                RetrofitApi.API_KEY,
                WeatherActivity.METRIC
        );

        responseBodyCall.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {

                if (response.isSuccessful()) {

                    CurrentWeather currentWeather = response.body();

                    displayCurrentWeather(currentWeather);

                } else {

                    if (getActivity() != null)
                    Toast.makeText(getActivity(), "Some Error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Network Error.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayCurrentWeather(CurrentWeather currentWeather) {

        timeIcon.setText("{ion-android-time 20sp} " + Util.getCurrentTime());

        tempTextView.setText(currentWeather.getMain().getTemp().toString());

        locationTextView.setText(currentWeather.getName() + "," + currentWeather.getSys().getCountry());

        weatherTextView.setText(currentWeather.getWeather().get(0).getDescription());

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
    public void onLatLonUpdate(LatLon latLon) {

        getCurrentWeatherData(latLon.getLat(), latLon.getLon());
    }
}
