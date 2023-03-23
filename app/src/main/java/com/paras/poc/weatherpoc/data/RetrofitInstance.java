package com.paras.poc.weatherpoc.data;

import static com.paras.poc.weatherpoc.util.Constants.BASE_URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    public static RetrofitInstance weatherInstance;

    public WeatherApi weatherApi;
    RetrofitInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherApi = retrofit.create(WeatherApi.class);
    }

    public static RetrofitInstance getWeatherInstance() {
        if(weatherInstance == null) {
            weatherInstance =new RetrofitInstance();
        }
        return weatherInstance;
    }
}
