package com.vicky7230.sunny.utils;


import com.vicky7230.sunny.retrofitPojo.Forecast.Forecast;
import com.vicky7230.sunny.retrofitPojo.Weather.CurrentWeather;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class RetrofitApi {

    public static String baseUrl = "http://api.openweathermap.org/data/2.5/";
    public static final String API_KEY = "4802d4430bd39239e33caae09471a83c";
    public static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static ApiInterface getApiInterfaceInstance() {
        return getRetrofitInstance().create(ApiInterface.class);
    }

    public interface ApiInterface {

        @GET("weather")
        Call<CurrentWeather> getCurrentLocationWeather(
                @Query("lat") String lat,
                @Query("lon") String lon,
                @Query("appid") String appId,
                @Query("units") String units
        );


        @GET("weather")
        Call<CurrentWeather> getCityWeather(
                @Query("q") String cityName,
                @Query("appid") String appId,
                @Query("units") String units
        );

        @GET("forecast")
        Call<Forecast> getCurrentLocationForecast(
                @Query("lat") String lat,
                @Query("lon") String lon,
                @Query("appid") String appId,
                @Query("units") String units,
                @Query("cnt") String count
        );

        @GET("forecast")
        Call<Forecast> getCityForecast(
                @Query("q") String cityName,
                @Query("appid") String appId,
                @Query("units") String units,
                @Query("cnt") String count
        );

    }
}