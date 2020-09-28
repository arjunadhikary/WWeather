package com.arjun.weather;

import com.arjun.weather.Model.FiveDaysWeather;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Weather {

    @GET("forecast")
    Call<FiveDaysWeather> getAllWeatherData(@Query("q") String query,
                                            @Query("appid")String apiKey,
                                            @Query("units")String unit);

}
