/**
 * Copyright (C) 2016 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cyanogenmod.yahooweatherprovider.yahoo;

import android.util.Log;

import org.cyanogenmod.yahooweatherprovider.yahoo.response.YQLResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YahooWeatherServiceManager {
    private final static String TAG = "YahooServiceManager";
    private final static boolean DEBUG = true;//Log.isLoggable(TAG, Log.VERBOSE);

    private final YahooServiceInterface mYahooServiceInterface;

    public YahooWeatherServiceManager() {
        Retrofit baseAdapter = buildRestAdapter();
        mYahooServiceInterface = baseAdapter.create(YahooServiceInterface.class);
    }

    public Call<YQLResponse> query(String state, String city) {
        String forecastQuery = YQLQueryCreator.getForecastQuery(state, city);
        return mYahooServiceInterface.query(forecastQuery);
    }

    public Call<YQLResponse> lookupCity(String city) {
        String locationQuery = YQLQueryCreator.getLocationQuery(city);
        return mYahooServiceInterface.query(locationQuery);
    }

    private Retrofit buildRestAdapter() {
        final OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        if (DEBUG) {
            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        final OkHttpClient client = builder.build();

        final String baseUrl = "https://query.yahooapis.com";
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
