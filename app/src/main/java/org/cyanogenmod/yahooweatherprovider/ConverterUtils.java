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

import org.cyanogenmod.yahooweatherprovider.yahoo.response.Admin1;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Admin2;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Admin3;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Forecast;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Locality1;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Place;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Postal;

import java.util.ArrayList;
import java.util.List;

import cyanogenmod.weather.WeatherInfo;
import cyanogenmod.weather.WeatherLocation;
import cyanogenmod.providers.WeatherContract.WeatherColumns.WeatherCode;

public class ConverterUtils {

    // see https://developer.yahoo.com/weather/documentation.html#codes
    private static final int CONDITION_CODE_TABLE[] = {
            WeatherCode.TORNADO,                    //0  tornado
            WeatherCode.TROPICAL_STORM,             //1  tropical storm
            WeatherCode.HURRICANE,                  //2  hurricane
            WeatherCode.SEVERE_THUNDERSTORMS,       //3  severe thunderstorms
            WeatherCode.THUNDERSTORMS,              //4  thunderstorms
            WeatherCode.MIXED_RAIN_AND_SNOW,        //5  mixed rain and snow
            WeatherCode.MIXED_RAIN_AND_SLEET,       //6  mixed rain and sleet
            WeatherCode.MIXED_SNOW_AND_SLEET,       //7  mixed snow and sleet
            WeatherCode.FREEZING_DRIZZLE,           //8  freezing drizzle
            WeatherCode.DRIZZLE,                    //9  drizzle
            WeatherCode.FREEZING_RAIN,              //10 freezing rain
            WeatherCode.SHOWERS,                    //11 showers
            WeatherCode.SHOWERS,                    //12 showers
            WeatherCode.SNOW_FLURRIES,              //13 snow flurries
            WeatherCode.LIGHT_SNOW_SHOWERS,         //14 light snow showers
            WeatherCode.BLOWING_SNOW,               //15 blowing snow
            WeatherCode.SNOW,                       //16 snow
            WeatherCode.HAIL,                       //17 hail
            WeatherCode.SLEET,                      //18 sleet
            WeatherCode.DUST,                       //19 dust
            WeatherCode.FOGGY,                      //20 foggy
            WeatherCode.HAZE,                       //21 haze
            WeatherCode.SMOKY,                      //22 smoky
            WeatherCode.BLUSTERY,                   //23 blustery
            WeatherCode.WINDY,                      //24 windy
            WeatherCode.COLD,                       //25 cold
            WeatherCode.CLOUDY,                     //26 cloudy
            WeatherCode.MOSTLY_CLOUDY_NIGHT,        //27 mostly cloudy (night)
            WeatherCode.MOSTLY_CLOUDY_DAY,          //28 mostly cloudy (day)
            WeatherCode.PARTLY_CLOUDY_NIGHT,        //29 partly cloudy (night)
            WeatherCode.PARTLY_CLOUDY_DAY,          //30 partly cloudy (day)
            WeatherCode.CLEAR_NIGHT,                //31 clear (night)
            WeatherCode.SUNNY,                      //32 sunny
            WeatherCode.FAIR_NIGHT,                 //33 fair (night)
            WeatherCode.FAIR_DAY,                   //34 fair (day)
            WeatherCode.MIXED_RAIN_AND_HAIL,        //35 mixed rain and hail
            WeatherCode.HOT,                        //36 hot
            WeatherCode.ISOLATED_THUNDERSTORMS,     //37 isolated thunderstorms
            WeatherCode.SCATTERED_THUNDERSTORMS,    //38 scattered thunderstorms
            WeatherCode.SCATTERED_THUNDERSTORMS,    //39 scattered thunderstorms
            WeatherCode.SCATTERED_SHOWERS,          //40 scattered showers
            WeatherCode.HEAVY_SNOW,                 //41 heavy snow
            WeatherCode.SCATTERED_SNOW_SHOWERS,     //42 scattered snow showers
            WeatherCode.HEAVY_SNOW,                 //43 heavy snow
            WeatherCode.PARTLY_CLOUDY,              //44 partly cloudy
            WeatherCode.THUNDERSHOWER,              //45 thundershowers
            WeatherCode.SNOW_SHOWERS,               //46 snow showers
            WeatherCode.ISOLATED_THUNDERSHOWERS,    //47 isolated thundershowers
    };

    public static ArrayList<WeatherInfo.DayForecast> convertForecastsToDayForecasts(
            List<Forecast> forecasts, int max) {
        ArrayList<WeatherInfo.DayForecast> ret = new ArrayList<>();
        int i = 0;
        for (Forecast forecast : forecasts) {
            if (i > max) {
                break;
            }
            WeatherInfo.DayForecast dayForecast = new WeatherInfo.DayForecast.Builder(convertConditionCodeToWeatherCondition(
                    Integer.parseInt(forecast.getCode())))
                    .setHigh(Double.parseDouble(forecast.getHigh()))
                    .setLow(Double.parseDouble(forecast.getLow()))
                    .build();
            ret.add(dayForecast);
            i++;
        }
        return ret;
    }

    public static List<WeatherLocation> convertPlacesToWeatherLocations(List<Place> places) {
        List<WeatherLocation> ret = new ArrayList<>();
        for (Place place : places) {
            Postal postal = place.getPostal();
            Admin1 admin1 = place.getAdmin1();
            if (admin1 != null && admin1.getContent() != null) {
                WeatherLocation weatherLocation = new WeatherLocation.Builder(admin1.getWoeid(),
                        admin1.getContent())
                        .setCountry(place.getCountry().getContent())
                        .setCountryId(place.getCountry().getCode())
                        .setPostalCode(postal == null ? "" : postal.getContent())
                        .build();
                ret.add(weatherLocation);
            }

            Admin2 admin2 = place.getAdmin2();
            if (admin2 != null && admin2.getContent() != null) {
                WeatherLocation weatherLocation = new WeatherLocation.Builder(admin2.getWoeid(),
                        admin2.getContent())
                        .setCountry(place.getCountry().getContent())
                        .setCountryId(place.getCountry().getCode())
                        .setPostalCode(postal == null ? "" : postal.getContent())
                        .build();
                ret.add(weatherLocation);
            }

            Admin3 admin3 = place.getAdmin3();
            if (admin3 != null && admin3.getContent() != null) {
                WeatherLocation weatherLocation = new WeatherLocation.Builder(admin3.getWoeid(),
                        admin3.getContent())
                        .setCountry(place.getCountry().getContent())
                        .setCountryId(place.getCountry().getCode())
                        .setPostalCode(postal == null ? "" : postal.getContent())
                        .build();
                ret.add(weatherLocation);
            }

            Locality1 locality1 = place.getLocality1();
            if (locality1 != null && locality1.getContent() != null) {
                WeatherLocation weatherLocation = new WeatherLocation.Builder(
                        locality1.getWoeid(),
                        locality1.getContent())
                        .setCountry(place.getCountry().getContent())
                        .setCountryId(place.getCountry().getCode())
                        .setPostalCode(postal == null ? "" : postal.getContent())
                        .build();
                ret.add(weatherLocation);
            }
        }
        return ret;
    }

    public static int convertConditionCodeToWeatherCondition(int conditionCode) {
        if ((0 <= conditionCode) && (conditionCode < CONDITION_CODE_TABLE.length)) {
            return CONDITION_CODE_TABLE[conditionCode];
        } else {
            return WeatherCode.NOT_AVAILABLE;
        }
    }
}
