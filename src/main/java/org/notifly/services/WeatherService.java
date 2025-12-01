package org.notifly.services;

import org.json.JSONObject;
import org.notifly.config.ConfigLoader;
import org.notifly.dto.WeatherInfo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {

    private final String weatherApi;
    public WeatherService(){
        this.weatherApi = ConfigLoader.getWeatherAPI();
    }


    public WeatherInfo getWeather() {
        try {
           String url = "https://api.openweathermap.org/data/2.5/weather?q=Siegen,DE&appid="+weatherApi+"&units=metric&lang=ru";

            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());

            WeatherInfo info = new WeatherInfo();
            info.temp = json.getJSONObject("main").getDouble("temp");
            info.feelsLike = json.getJSONObject("main").getDouble("feels_like");
            info.humidity = json.getJSONObject("main").getInt("humidity");
            info.wind = json.getJSONObject("wind").getDouble("speed");
            info.description = json.getJSONArray("weather").getJSONObject(0).getString("description");

            return info;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
