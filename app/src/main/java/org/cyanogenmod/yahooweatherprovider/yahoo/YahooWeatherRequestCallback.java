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

import android.util.Log;

import org.cyanogenmod.yahooweatherprovider.yahoo.response.YQLResponse;

import cyanogenmod.weatherservice.ServiceRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YahooWeatherRequestCallback implements Callback<YQLResponse> {
    private static final String TAG = YahooWeatherRequestCallback.class.getSimpleName();
    private ServiceRequest mServiceRequest;
    private YahooWeatherResponseListener mYahooWeatherResponseListener;

    public YahooWeatherRequestCallback(ServiceRequest serviceRequest,
                                       YahooWeatherResponseListener yahooWeatherdResponseListener) {
        mServiceRequest = serviceRequest;
        mYahooWeatherResponseListener = yahooWeatherdResponseListener;
    }

    @Override
    public void onResponse(Call<YQLResponse> call, Response<YQLResponse> response) {
        if (response.isSuccessful()) {
            Log.d(TAG, "Received response:\n" + response.body().toString());
            YQLResponse wundergroundReponse = response.body();
            if (wundergroundReponse == null) {
                Log.d(TAG, "Null wu reponse, return");
                mServiceRequest.fail();
                return;
            }
            mYahooWeatherResponseListener.processYahooWeatherResponse(
                    wundergroundReponse, mServiceRequest);
        } else {
            Log.d(TAG, "Response " + response.toString());
        }
    }

    @Override
    public void onFailure(Call<YQLResponse> call, Throwable t) {
        Log.d(TAG, "Failure " + t.toString());
        mServiceRequest.fail();
    }
}
