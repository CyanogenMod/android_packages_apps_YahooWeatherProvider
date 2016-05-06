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

import cyanogenmod.providers.WeatherContract;
import cyanogenmod.weather.WeatherInfo;
import cyanogenmod.weather.WeatherLocation;

import static cyanogenmod.providers.WeatherContract.WeatherColumns.WeatherCode.ISOLATED_THUNDERSHOWERS;
import static cyanogenmod.providers.WeatherContract.WeatherColumns.WeatherCode.NOT_AVAILABLE;
import static cyanogenmod.providers.WeatherContract.WeatherColumns.WeatherCode.SCATTERED_SNOW_SHOWERS;
import static cyanogenmod.providers.WeatherContract.WeatherColumns.WeatherCode.SCATTERED_THUNDERSTORMS;

public class ConverterUtils {

    public static ArrayList<WeatherInfo.DayForecast> convertForecastsToDayForecasts(
            List<Forecast> forecasts, int max) {
        ArrayList<WeatherInfo.DayForecast> ret = new ArrayList<>();
        int i = 0;
        for (Forecast forecast : forecasts) {
            if (i > max) {
                break;
            }
            WeatherInfo.DayForecast dayForecast = new WeatherInfo.DayForecast.Builder(offset(
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

    public static int offset(int conditionCode) {
        if (conditionCode <= WeatherContract.WeatherColumns.WeatherCode.SHOWERS) {
            return conditionCode;
        } else if (conditionCode <= SCATTERED_THUNDERSTORMS) {
            return conditionCode - 1;
        } else if (conditionCode <= SCATTERED_SNOW_SHOWERS) {
            return conditionCode - 2;
        } else if (conditionCode <= ISOLATED_THUNDERSHOWERS) {
            return conditionCode - 3;
        } else {
            return NOT_AVAILABLE;
        }
    }
}
