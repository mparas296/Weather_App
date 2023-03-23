package com.paras.poc.weatherpoc.ui;

import static com.paras.poc.weatherpoc.util.Constants.API_KEY;
import static com.paras.poc.weatherpoc.util.Constants.REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.paras.poc.weatherpoc.R;
import com.paras.poc.weatherpoc.data.RetrofitInstance;
import com.paras.poc.weatherpoc.model.WeatherModel;
import com.paras.poc.weatherpoc.util.DownloadImage;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ScrollView searchScrollView, displayScrollView;
    TextInputEditText inputText;
    ShapeableImageView weather_icon;
    MaterialTextView locationTxt, tempTxt, currTimeTxt, minTempTxt, maxTempTxt, pressureTxt, humidityTxt, speedTxt, directionTxt, sunriseTxt, sunsetTxt;
    WeatherModel weatherModel;
    MaterialButton search_btn, searchByLastLocation_btn, searchByCurrentLocation_btn, searchByLocation_btn;
    ProgressBar progressBar;
    FusedLocationProviderClient fusedLocationProviderClient;
    String city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchScrollView = (ScrollView) findViewById(R.id.scroll_view_search);
        displayScrollView = (ScrollView) findViewById(R.id.scroll_view_display);

        locationTxt = (MaterialTextView) findViewById(R.id.location);
        weather_icon = (ShapeableImageView) findViewById(R.id.weather_icon);
        tempTxt = (MaterialTextView) findViewById(R.id.temperature);
        currTimeTxt = (MaterialTextView) findViewById(R.id.current_date);
        minTempTxt = (MaterialTextView) findViewById(R.id.min_temperature);
        maxTempTxt = (MaterialTextView) findViewById(R.id.max_temperature);
        pressureTxt = (MaterialTextView) findViewById(R.id.pressure);
        humidityTxt = (MaterialTextView) findViewById(R.id.humidity);
        speedTxt = (MaterialTextView) findViewById(R.id.wind_speed);
        directionTxt = (MaterialTextView) findViewById(R.id.wind_direction);
        sunriseTxt = (MaterialTextView) findViewById(R.id.sunrise_time);
        sunsetTxt = (MaterialTextView) findViewById(R.id.sunset_time);

        progressBar = (ProgressBar) findViewById(R.id.progress_circular);

        //TODO Implement Search by Current Location
        progressBar.setVisibility(View.VISIBLE);
        displayScrollView.setVisibility(View.INVISIBLE);
        searchScrollView.setVisibility(View.INVISIBLE);
        searchByCurrentLocation();
        //TODO Implement Search by Location

        //TODO Implement Search by Last Location

    }

    private void searchByCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //showProgressBar();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    double lat, lon;
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();

                                    addresses = geocoder.getFromLocation(lat, lon, 1);
                                    String cityName = addresses.get(0).getLocality();
                                    locationTxt.setText("Location: " + cityName);

                                    Log.e("Current Location",cityName);

                                    //TODO Call Weather by city
                                    getWeatherData(cityName);

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                            else {
                                city = getLastSearchedCity();

                                if(!city.equals("")) {
                                    Toast.makeText(MainActivity.this, "Last searched location data found!" +
                                            "\nShowing weather data of that City.", Toast.LENGTH_SHORT).show();
                                    getWeatherData(city);
                                }
                                else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.this, "No last searched location data found!", Toast.LENGTH_SHORT).show();
                                    displayScrollView.setVisibility(View.INVISIBLE);
                                    searchScrollView.setVisibility(View.VISIBLE);

                                    inputText = (TextInputEditText) findViewById(R.id.input_text);
                                    search_btn = (MaterialButton) findViewById(R.id.search_btn);

                                    search_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            city = inputText.getText().toString();
                                            if(!city.equals("")) {
                                                storeLastSearchedCity(city);
                                                getWeatherData(city);
                                            }
                                            else {
                                                Toast.makeText(MainActivity.this, "City name not valid!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
        else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                searchByCurrentLocation();
            }
            else {
                Toast.makeText(this, "Required Location Permission Denied!", Toast.LENGTH_SHORT).show();
                //TODO Call by last searched location
                city = getLastSearchedCity();

                if(!city.equals("")) {
                    Toast.makeText(this, "Last searched location data found!" +
                            "\nShowing weather data of that City.", Toast.LENGTH_SHORT).show();
                    getWeatherData(city);
                }
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, "No last searched location data found!", Toast.LENGTH_SHORT).show();
                    displayScrollView.setVisibility(View.INVISIBLE);
                    searchScrollView.setVisibility(View.VISIBLE);

                    inputText = (TextInputEditText) findViewById(R.id.input_text);
                    search_btn = (MaterialButton) findViewById(R.id.search_btn);

                    search_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            city = inputText.getText().toString();
                            storeLastSearchedCity(city);
                            getWeatherData(city);
                        }
                    });
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public String getActualDate(long epoch) {

        String actualDate, hrsTime, actualTime = "";

        Instant instant = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instant = Instant.ofEpochSecond(epoch);
        }/*
        System.out.println(instant);*/
        actualDate = instant.toString();
        int index = actualDate.indexOf('T');
        hrsTime = actualDate.substring(index+1,index+3);
        int hrs = Integer.parseInt(hrsTime);
        actualDate = actualDate.substring(index + 3, actualDate.length() - 1);
        if(hrs <= 12) {
            actualTime = actualTime + hrs + actualDate + " A.M.";
        }
        else {
            actualTime = actualTime + (hrs-12) + actualDate + " P.M.";
        }
        return actualTime;
    }

    public  void getWeatherData(String City) {
        RetrofitInstance.getWeatherInstance().weatherApi.getWeather(City,API_KEY).enqueue(new Callback<WeatherModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {

                if(response.code() > 400 && response.code() < 500) {
                    Toast.makeText(MainActivity.this, "Please enter a valid city!", Toast.LENGTH_SHORT).show();
                }
                else {
                    weatherModel = response.body();

                /*Getting desired values from the API & Converting them to standard
                units & then displaying it in the UI*/

                    double curTempC, minTempC, maxTempC;
                    String icon, cityName;
                    cityName = "" +City.charAt(0);
                    cityName = cityName.toUpperCase();
                    cityName = cityName + City.substring(1);
                    locationTxt.setText("Location: " + cityName);
                    curTempC = (double) weatherModel.getMain().getTemp() - 273.15;
                    minTempC = (double) weatherModel.getMain().getTemp_min() - 273.15;
                    maxTempC = (double) weatherModel.getMain().getTemp_max() - 273.15;
                    tempTxt.setText("Current Temperature:\n" + Double.toString(Math.round(curTempC)) + " ℃");
                    currTimeTxt.setText("Last Updated:\n" + getActualDate((weatherModel.getDt())+(weatherModel.getTimezone())));
                    minTempTxt.setText("Min Temperature:\n" + Double.toString(Math.round(minTempC)) + " ℃");
                    maxTempTxt.setText("Max Temperature:\n" + Double.toString(Math.round(maxTempC)) + " ℃");
                    pressureTxt.setText("Air Pressure:\n" + Double.toString(weatherModel.getMain().getPressure())+ " hPa");
                    humidityTxt.setText("Humidity:\n" + Double.toString(weatherModel.getMain().getHumidity()) + " %");
                    speedTxt.setText("Wind Speed:\n" + Double.toString(weatherModel.getWind().getSpeed()) + " km/h");
                    directionTxt.setText("Wind Direction:\n" + Double.toString(weatherModel.getWind().getDeg()) + " °");
                    sunriseTxt.setText("Sunrise:\n" + getActualDate((weatherModel.getSys().getSunrise())+(weatherModel.getTimezone())));
                    sunsetTxt.setText("Sunset:\n" + getActualDate((weatherModel.getSys().getSunset())+(weatherModel.getTimezone())));

                    icon = weatherModel.getWeather().get(0).getIcon();


                    new DownloadImage(weather_icon)
                            .execute("https://openweathermap.org/img/wn/" + icon + "@2x.png");

                    progressBar.setVisibility(View.INVISIBLE);
                    searchScrollView.setVisibility(View.INVISIBLE);
                    displayScrollView.setVisibility(View.VISIBLE);

                    searchByLastLocation_btn = (MaterialButton) findViewById(R.id.search_location_btn);
                    searchByCurrentLocation_btn = (MaterialButton) findViewById(R.id.search_city_btn);
                    searchByLocation_btn = (MaterialButton) findViewById(R.id.search_query_btn);

                    searchByLastLocation_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            city = getLastSearchedCity();

                            if(!city.equals("")) {
                                Toast.makeText(MainActivity.this, "Last searched location data found!" +
                                        "\nShowing weather data of that City.", Toast.LENGTH_SHORT).show();
                                getWeatherData(city);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "No last searched location data found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    searchByCurrentLocation_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            searchByCurrentLocation();
                        }
                    });

                    searchByLocation_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            displayScrollView.setVisibility(View.INVISIBLE);
                            searchScrollView.setVisibility(View.VISIBLE);

                            inputText = (TextInputEditText) findViewById(R.id.input_text);
                            search_btn = (MaterialButton) findViewById(R.id.search_btn);

                            search_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    city = inputText.getText().toString();
                                    if(!city.equals("")) {
                                        storeLastSearchedCity(city);
                                        getWeatherData(city);
                                    }
                                    else {
                                        city = getLastSearchedCity();
                                        Toast.makeText(MainActivity.this, "Please Enter a valid city name!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                    Toast.makeText(MainActivity.this, "Location Data Updated!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                Log.e("weather api","onFailure: " + t.getLocalizedMessage());
                Toast.makeText(MainActivity.this, "Weather Api call failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void storeLastSearchedCity(String city) {

        SharedPreferences sharedPreferences = getSharedPreferences("WeatherAppSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        spEditor.putString("City Name", city);
        spEditor.apply();
    }

    public String getLastSearchedCity() {
        String lastCity;
        SharedPreferences sharedPreferences = getSharedPreferences("WeatherAppSharedPref", MODE_PRIVATE);
        lastCity = sharedPreferences.getString("City Name","");
        return lastCity;
    }

}