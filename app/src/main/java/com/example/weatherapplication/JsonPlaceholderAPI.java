package com.example.weatherapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceholderAPI {
    @GET("data/2.5/weather")
    Call<CityWeather> getCityWeather(
            @Query("q") String cityName,
            @Query("APPID") String appid,
            @Query("units") String units
    );
}
