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

package org.cyanogenmod.yahooweatherprovider.yahoo;

import android.net.Uri;

public class YQLQueryCreator {
    private final static String YQL_FORECAST_QUERY =
            Uri.encode("select * from weather.forecast where woeid in");
    private final static String YQL_LOCATION_QUERY =
            Uri.encode("select woeid, postal, admin1, admin2, admin3, " +
                    "locality1, locality2, country from geo.places where " +
                    "(placetype = 7 or placetype = 8 or placetype = 9 " +
                    "or placetype = 10 or placetype = 11 or placetype = 20) and text = ");

    private YQLQueryCreator(){
    }

    public static String getForecastQuery(String state, String city) {
        String params = "(select woeid from geo.places(1) where text=\"" + city + "," + state
                + "\") and u='f'";
        return YQL_FORECAST_QUERY + Uri.encode(params);
    }

    public static String getLocationQuery(String input) {
        String params = "\"" + input + "\"";
        return YQL_LOCATION_QUERY + Uri.encode(params);
    }
}
