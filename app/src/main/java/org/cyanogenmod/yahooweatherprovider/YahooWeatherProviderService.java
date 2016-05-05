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

package org.cyanogenmod.yahooweatherprovider;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.cyanogenmod.yahooweatherprovider.yahoo.WeakReferenceHandler;
import org.cyanogenmod.yahooweatherprovider.yahoo.YahooWeatherRequestCallback;
import org.cyanogenmod.yahooweatherprovider.yahoo.YahooWeatherResponseListener;
import org.cyanogenmod.yahooweatherprovider.yahoo.YahooWeatherServiceManager;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Atmosphere;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Channel;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Condition;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Forecast;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Location;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Place;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Query;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Results;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.Wind;
import org.cyanogenmod.yahooweatherprovider.yahoo.response.YQLResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import cyanogenmod.providers.WeatherContract;
import cyanogenmod.weather.RequestInfo;
import cyanogenmod.weather.WeatherInfo;
import cyanogenmod.weather.WeatherLocation;
import cyanogenmod.weatherservice.ServiceRequest;
import cyanogenmod.weatherservice.ServiceRequestResult;
import cyanogenmod.weatherservice.WeatherProviderService;
import retrofit2.Call;

public class YahooWeatherProviderService extends WeatherProviderService
        implements YahooWeatherResponseListener {

    private static final String TAG = YahooWeatherProviderService.class.getSimpleName();
    private static final int SERVICE_REQUEST_CANCELLED = -1;
    private static final int SERVICE_REQUEST_SUBMITTED = 0;

    @Inject
    public YahooWeatherServiceManager mYahooWeatherServiceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        YahooCMWeatherApplication.get(this).inject(this);
    }

    @Override
    protected void onRequestSubmitted(ServiceRequest serviceRequest) {
        Log.d(TAG, "Received service request: " + serviceRequest.toString());
        Message request = mHandler.obtainMessage(SERVICE_REQUEST_SUBMITTED, serviceRequest);
        request.sendToTarget();
    }

    @Override
    protected void onRequestCancelled(ServiceRequest serviceRequest) {
        Log.d(TAG, "Received service request cancelled: " + serviceRequest.toString());
        Message request = mHandler.obtainMessage(SERVICE_REQUEST_CANCELLED, serviceRequest);
        request.sendToTarget();
    }

    private final NonLeakyMessageHandler mHandler = new NonLeakyMessageHandler(this);

    private static class NonLeakyMessageHandler
            extends WeakReferenceHandler<YahooWeatherProviderService> {
        public NonLeakyMessageHandler(YahooWeatherProviderService reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(YahooWeatherProviderService reference,
                                     Message inputMessage) {
            ServiceRequest serviceRequest = (ServiceRequest)inputMessage.obj;
            switch (inputMessage.what) {
                case SERVICE_REQUEST_SUBMITTED:
                    RequestInfo requestInfo = serviceRequest.getRequestInfo();
                    switch (requestInfo.getRequestType()) {
                        case RequestInfo.TYPE_WEATHER_BY_WEATHER_LOCATION_REQ:
                        case RequestInfo.TYPE_WEATHER_BY_GEO_LOCATION_REQ:
                            reference.handleWeatherRequest(serviceRequest);
                            break;
                        case RequestInfo.TYPE_LOOKUP_CITY_NAME_REQ:
                            reference.handleLookupRequest(serviceRequest);
                            break;
                        default:
                            //Don't support anything else, fail.
                            serviceRequest.fail();
                            break;
                    }
                    break;
                case SERVICE_REQUEST_CANCELLED:
                    //TODO; Implement
                    break;
                default:
                    //Don't support anything else, fail.
                    if (serviceRequest != null) {
                        serviceRequest.fail();
                    }
            }
        }
    }

    private void handleLookupRequest(ServiceRequest serviceRequest) {
        final RequestInfo requestInfo = serviceRequest.getRequestInfo();

        String cityName = requestInfo.getCityName();

        if (TextUtils.isEmpty(cityName)) {
            Log.d(TAG, "Null citname return");
            serviceRequest.fail();
            return;
        }

        Call<YQLResponse> wundergroundCall = mYahooWeatherServiceManager.lookupCity(cityName);
        wundergroundCall.enqueue(new YahooWeatherRequestCallback(serviceRequest, this));
    }


    private void handleWeatherRequest(final ServiceRequest serviceRequest) {
        final RequestInfo requestInfo = serviceRequest.getRequestInfo();
        Log.d(TAG, "Received weather request info: " + requestInfo.toString());

        if (requestInfo.getRequestType() == RequestInfo.TYPE_WEATHER_BY_GEO_LOCATION_REQ) {
            android.location.Location location = requestInfo.getLocation();
            if (location == null) {
                LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_HIGH);
                location = locationManager.getLastKnownLocation(locationManager.getBestProvider(
                        criteria, false));
            }
            handleRequestByGeoLocation(location, serviceRequest);
        } else {
            WeatherLocation weatherLocation = requestInfo.getWeatherLocation();
            handleRequestByWeatherLocation(weatherLocation, serviceRequest);
        }
    }

    /**
     * Enqueue request by geolocation (lat/long)
     */
    private void handleRequestByGeoLocation(android.location.Location location,
            final ServiceRequest serviceRequest) {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            Address address = addresses.get(0);
            Call<YQLResponse> wundergroundCall =
                    mYahooWeatherServiceManager.query(address.getCountryName(),
                            address.getLocality());
            wundergroundCall.enqueue(new YahooWeatherRequestCallback(serviceRequest, this));
        } catch (IOException e) {
            Log.d(TAG, "Failed to get addresses");
            serviceRequest.fail();
        }
    }

    /**
     * Enqueue request by weatherlocation
     */
    private void handleRequestByWeatherLocation(WeatherLocation weatherLocation,
            final ServiceRequest serviceRequest) {

        Call<YQLResponse> wundergroundCall = null;
        if (weatherLocation.getCity() != null) {
            wundergroundCall =
                    mYahooWeatherServiceManager.query(weatherLocation.getState(),
                            weatherLocation.getCity());
        }
        //TODO: Add postal code support
        //else if (weatherLocation.getPostalCode() != null) {
        //  wundergroundCall =
        //            mYahooWeatherServiceManager.query(weatherLocation.getPostalCode());
        //}
        else {
            Log.e(TAG, "Unable to handle service request");
            serviceRequest.fail();
            return;
        }

        wundergroundCall.enqueue(new YahooWeatherRequestCallback(serviceRequest, this));
    }

    @Override
    public void processYahooWeatherResponse(YQLResponse yqlResponse, ServiceRequest serviceRequest) {
        switch (serviceRequest.getRequestInfo().getRequestType()) {
            case RequestInfo.TYPE_WEATHER_BY_WEATHER_LOCATION_REQ:
            case RequestInfo.TYPE_WEATHER_BY_GEO_LOCATION_REQ:
                processWeatherRequest(yqlResponse, serviceRequest);
                break;
            case RequestInfo.TYPE_LOOKUP_CITY_NAME_REQ:
                processCityLookupRequest(yqlResponse, serviceRequest);
                break;
            default:
                //Don't support anything else, fail.
                serviceRequest.fail();
        }
    }

    private void processWeatherRequest(YQLResponse yqlResponse, ServiceRequest serviceRequest) {
        final Query queryResponse = yqlResponse.getQuery();
        final Results results =  queryResponse.getResults();

        if (results == null) {
            Log.d(TAG, "Null query results, return");
            serviceRequest.fail();
            return;
        }
        final Channel channel = results.getChannel();
        final Location location = channel.getLocation();
        final Condition condition = channel.getItem().getCondition();
        final Atmosphere atmosphere = channel.getAtmosphere();
        final Wind wind = channel.getWind();
        final Forecast[] forecasts = channel.getItem().getForecast();

        final WeatherInfo.Builder weatherInfoBuilder = new WeatherInfo.Builder(location.getCity(),
                Double.parseDouble(condition.getTemp()),
                WeatherContract.WeatherColumns.TempUnit.FAHRENHEIT);

        // Set current weather condition code
        weatherInfoBuilder.setWeatherCondition(Integer.parseInt(condition.getCode()));

        // Set humidity
        weatherInfoBuilder.setHumidity(Double.parseDouble(atmosphere.getHumidity()));

        // Set wind arguments
        weatherInfoBuilder.setWind(Double.parseDouble(wind.getSpeed()),
                Double.parseDouble(wind.getDirection()),
                WeatherContract.WeatherColumns.WindSpeedUnit.MPH);

        // Set high and low for today from the simple forecast days
        weatherInfoBuilder.setTodaysHigh(Double.parseDouble(forecasts[0].getHigh()));
        weatherInfoBuilder.setTodaysLow(Double.parseDouble(forecasts[0].getHigh()));

        ArrayList<WeatherInfo.DayForecast> forecastList = ConverterUtils.convertForecastsToDayForecasts(
                Arrays.asList(forecasts));
        // Remove today
        forecastList.remove(0);

        weatherInfoBuilder.setForecast(forecastList);

        ServiceRequestResult serviceRequestResult =
                new ServiceRequestResult.Builder(weatherInfoBuilder.build())
                        .build();
        serviceRequest.complete(serviceRequestResult);
    }

    private void processCityLookupRequest(YQLResponse yqlResponse, ServiceRequest serviceRequest) {
        final Query queryResponse = yqlResponse.getQuery();
        final Results results =  queryResponse.getResults();

        if (results == null) {
            Log.d(TAG, "Null query results, return");
            serviceRequest.fail();
            return;
        }

        final Place[] places = results.getPlace();

        if (places == null) {
            Log.d(TAG, "Null places, return");
            serviceRequest.fail();
            return;
        }

        ServiceRequestResult serviceRequestResult =
                new ServiceRequestResult.Builder(ConverterUtils.convertPlacesToWeatherLocations(
                        Arrays.asList(places))).build();
        serviceRequest.complete(serviceRequestResult);
    }
}
