package com.paras.poc.weatherpoc.model;

import static com.paras.poc.weatherpoc.util.Constants.API_KEY;
import static org.junit.Assert.*;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.paras.poc.weatherpoc.data.RetrofitInstance;
import com.paras.poc.weatherpoc.data.WeatherApi;
import com.paras.poc.weatherpoc.ui.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.Equals;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class WeatherModelTest {

    String json = "{\n" +
            "  \"coord\": {\n" +
            "    \"lon\": 80.35,\n" +
            "    \"lat\": 26.4667\n" +
            "  },\n" +
            "  \"weather\": [\n" +
            "    {\n" +
            "      \"id\": 800,\n" +
            "      \"main\": \"Clear\",\n" +
            "      \"description\": \"clear sky\",\n" +
            "      \"icon\": \"01d\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"base\": \"stations\",\n" +
            "  \"main\": {\n" +
            "    \"temp\": 305.93,\n" +
            "    \"feels_like\": 304.02,\n" +
            "    \"temp_min\": 305.93,\n" +
            "    \"temp_max\": 305.93,\n" +
            "    \"pressure\": 1006,\n" +
            "    \"humidity\": 22,\n" +
            "    \"sea_level\": 1006,\n" +
            "    \"grnd_level\": 991\n" +
            "  },\n" +
            "  \"visibility\": 10000,\n" +
            "  \"wind\": {\n" +
            "    \"speed\": 5.52,\n" +
            "    \"deg\": 300,\n" +
            "    \"gust\": 7.19\n" +
            "  },\n" +
            "  \"clouds\": {\n" +
            "    \"all\": 9\n" +
            "  },\n" +
            "  \"dt\": 1679563779,\n" +
            "  \"sys\": {\n" +
            "    \"country\": \"IN\",\n" +
            "    \"sunrise\": 1679531999,\n" +
            "    \"sunset\": 1679575839\n" +
            "  },\n" +
            "  \"timezone\": 19800,\n" +
            "  \"id\": 1267995,\n" +
            "  \"name\": \"Kanpur\",\n" +
            "  \"cod\": 200\n" +
            "}";

    WeatherModel weatherModel;

    @Before
    public void setUp() throws Exception {
        weatherModel = new Gson().fromJson(json, WeatherModel.class);

    }

    @Test
    public void TestWeatherModel() {
        assertNotNull(weatherModel);
    }

    @Test
    public void testCoordinates() {
        assertEquals(weatherModel.getCoord().getLat(),26.4667,0);
        assertEquals(weatherModel.getCoord().getLon(),80.35,0);
    }

    @Test
    public void testWeather() {
        assertEquals(weatherModel.weather.get(0).getId(),800);
        assertEquals(weatherModel.weather.get(0).getMain(),"Clear");
        assertEquals(weatherModel.weather.get(0).getIcon(),"01d");
        assertEquals(weatherModel.weather.get(0).getDescription(),"clear sky");
    }

    @Test
    public void testMain() {
        assertEquals(weatherModel.getMain().temp,305.93,0);
        assertEquals(weatherModel.getMain().feels_like,304.02,0);
        assertEquals(weatherModel.getMain().temp_min,305.93,0);
        assertEquals(weatherModel.getMain().temp_max,305.93,0);
        assertEquals(weatherModel.getMain().pressure,1006);
        assertEquals(weatherModel.getMain().humidity,22);
    }

    @Test
    public void testVisibility() {
        assertEquals(weatherModel.getVisibility(),10000);
    }

    @Test
    public void testWind() {
        assertEquals(weatherModel.getWind().getSpeed(),5.52,0);
        assertEquals(weatherModel.getWind().getDeg(),300);
    }

    @Test
    public void testCloud() {
        assertEquals(weatherModel.getClouds().getAll(),9);
    }

    @Test
    public void testDate() {
        assertEquals(weatherModel.getDt(),1679563779);
    }

    @Test
    public void testSys() {
        assertEquals(weatherModel.getSys().getCountry(),"IN");
        assertEquals(weatherModel.getSys().getSunrise().toString(),"1679531999");
        assertEquals(weatherModel.getSys().getSunset().toString(),"1679575839");
    }

    @Test
    public void testTimeZone() {
        assertEquals(weatherModel.getTimezone().toString(),"19800");
    }

    @Test
    public void testId() {
        assertEquals(weatherModel.getId(),1267995);
    }

    @Test
    public void testCity() {
        assertEquals(weatherModel.getName(),"Kanpur");
    }

    @Test
    public void testCode() {
        assertEquals(weatherModel.getCod(),200);
    }
}