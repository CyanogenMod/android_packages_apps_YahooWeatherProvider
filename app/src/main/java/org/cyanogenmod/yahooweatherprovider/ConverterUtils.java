/**
 * Copyright (C) 2016 The CyanogenMod Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cyanogenmod.yahooweatherprovider;

import org.cyanogenmod.yahooweatherprovider.yahoo.response.Admin3;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Forecast;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Place;

import java.util.ArrayList;
import java.util.List;

import cyanogenmod.weather.WeatherInfo;
import cyanogenmod.weather.WeatherLocation;

public class ConverterUtils {

    public static ArrayList<WeatherInfo.DayForecast> convertForecastsToDayForecasts(List<Forecast> forecasts) {
        ArrayList<WeatherInfo.DayForecast> ret = new ArrayList<>();
        for (Forecast forecast : forecasts) {
            WeatherInfo.DayForecast dayForecast = new WeatherInfo.DayForecast.Builder(
                    Integer.parseInt(forecast.getCode()))
                    .setHigh(Double.parseDouble(forecast.getHigh()))
                    .setLow(Double.parseDouble(forecast.getLow()))
                    .build();
            ret.add(dayForecast);
        }
        return ret;
    }

    public static List<WeatherLocation> convertPlacesToWeatherLocations(List<Place> places) {
        List<WeatherLocation> ret = new ArrayList<>();
        for (Place place : places) {
            Admin3 admin3 = place.getAdmin();
            if (admin3 != null && admin3.getContent() != null) {
                WeatherLocation weatherLocation = new WeatherLocation.Builder(place.getWoeid(),
                        admin3.getContent()).build();
                ret.add(weatherLocation);
            }
        }
        return ret;
    }
}
