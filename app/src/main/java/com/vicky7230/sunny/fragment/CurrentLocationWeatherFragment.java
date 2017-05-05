package com.vicky7230.sunny.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.widget.IconTextView;
import com.vicky7230.sunny.R;
import com.vicky7230.sunny.activity.WeatherActivity;
import com.vicky7230.sunny.pojo.LatLon;
import com.vicky7230.sunny.retrofitPojo.Forecast.Forecast;
import com.vicky7230.sunny.retrofitPojo.Forecast.List;
import com.vicky7230.sunny.retrofitPojo.Weather.CurrentWeather;
import com.vicky7230.sunny.utils.RetrofitApi;
import com.vicky7230.sunny.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vicky7230.sunny.activity.WeatherActivity.DEGREE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentLocationWeatherFragment extends Fragment {

    private static final String TAG = CurrentLocationWeatherFragment.class.getSimpleName();

    private DateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private DateFormat destinationDateFormat = new SimpleDateFormat("hh a\nEEE, d MMM", Locale.ENGLISH);

    private TextView tempTextView;
    private TextView tempHighTextView;
    private TextView tempLowTextView;
    private TextView locationTextView;
    private TextView weatherTextView;
    private TextView windTextView;

    private IconTextView weatherIcon;
    private IconTextView timeIcon;


    public CurrentLocationWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_current_location_weather, container, false);


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

    private void getCurrentLocationWeatherData(String lat, String lon) {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<CurrentWeather> currentWeatherCall = apiInterface.getCurrentLocationWeather(
                lat,
                lon,
                RetrofitApi.API_KEY,
                WeatherActivity.METRIC
        );

        currentWeatherCall.enqueue(new Callback<CurrentWeather>() {
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

        timeIcon.setText("{ion-android-time 20sp} " + Util.getTimeFromUnixTimeStamp(currentWeather.getDt()));

        tempTextView.setText(currentWeather.getMain().getTemp().toString() + DEGREE + "C");

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


    private void getCurrentLocationForecastData(String lat, String lon) {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<Forecast> forecastCall = apiInterface.getCurrentLocationForecast(
                lat,
                lon,
                RetrofitApi.API_KEY,
                WeatherActivity.METRIC,
                "5"
        );

        forecastCall.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {

                if (response.isSuccessful()) {

                    Forecast forecast = response.body();

                    displayForecast(forecast);

                } else {

                    if (getActivity() != null)
                        Toast.makeText(getActivity(), "Some Error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {

                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Network Error.", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void displayForecast(Forecast forecast) {

        int i = 1;
        for (List item : forecast.getList()) {

            IconTextView timeTextView = (IconTextView) getView().findViewById(getResources().getIdentifier("forecast_time_" + i, "id", getActivity().getPackageName()));
            try {
                timeTextView.setText("{ion-android-time 20sp}  " + destinationDateFormat.format(sourceDateFormat.parse(item
                        .getDtTxt())) + "");
            } catch (ParseException e) {

                Log.e(TAG, "Error parsing date : " + e);
            }

            IconTextView weatherIcon = (IconTextView) getView().findViewById(getResources().getIdentifier("forecast_icon_" + i, "id", getActivity().getPackageName()));

            if (item.getWeather().get(0).getIcon().equals("01d"))
                weatherIcon.setText("{mc-sun-o}");
            else if (item.getWeather().get(0).getIcon().equals("01n"))
                weatherIcon.setText("{mc-moon}");
            else if (item.getWeather().get(0).getIcon().equals("02d"))
                weatherIcon.setText("{mc-sun-cloud-o}");
            else if (item.getWeather().get(0).getIcon().equals("02n"))
                weatherIcon.setText("{mc-moon-cloud}");
            else if (item.getWeather().get(0).getIcon().equals("03d"))
                weatherIcon.setText("{mc-cloud-o}");
            else if (item.getWeather().get(0).getIcon().equals("03n"))
                weatherIcon.setText("{mc-cloud}");
            else if (item.getWeather().get(0).getIcon().equals("04d"))
                weatherIcon.setText("{mc-cloud-o}");
            else if (item.getWeather().get(0).getIcon().equals("04n"))
                weatherIcon.setText("{mc-cloud}");
            else if (item.getWeather().get(0).getIcon().equals("09d"))
                weatherIcon.setText("{mc-cloud-rain-o}");
            else if (item.getWeather().get(0).getIcon().equals("09n"))
                weatherIcon.setText("{mc-cloud-rain}");
            else if (item.getWeather().get(0).getIcon().equals("10d"))
                weatherIcon.setText("{mc-cloud-drop-o}");
            else if (item.getWeather().get(0).getIcon().equals("10n"))
                weatherIcon.setText("{mc-cloud-drop}");
            else if (item.getWeather().get(0).getIcon().equals("11d"))
                weatherIcon.setText("{mc-cloud-double-thunder2-o}");
            else if (item.getWeather().get(0).getIcon().equals("11n"))
                weatherIcon.setText("{mc-cloud-double-thunder}");
            else if (item.getWeather().get(0).getIcon().equals("13d"))
                weatherIcon.setText("{mc-cloud-snow3-o}");
            else if (item.getWeather().get(0).getIcon().equals("13n"))
                weatherIcon.setText("{mc-cloud-snow2}");
            else if (item.getWeather().get(0).getIcon().equals("50d") || item.getWeather().get(0).getIcon().equals("50n"))
                weatherIcon.setText("{mc-sea-o}");

            TextView forecastWeatherTextView = (TextView) getView().findViewById(getResources().getIdentifier("forecast_weather_" + i, "id", getActivity().getPackageName()));
            forecastWeatherTextView.setText("" + item.getMain().getTemp().toString() + DEGREE + "C" + "\n" + item.getWeather().get(0).getMain());

            ++i;
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
    public void onLatLonUpdate(LatLon latLon) {

        getCurrentLocationWeatherData(latLon.getLat(), latLon.getLon());
        getCurrentLocationForecastData(latLon.getLat(), latLon.getLon());

    }
}
