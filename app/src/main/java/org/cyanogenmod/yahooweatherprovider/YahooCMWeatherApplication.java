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

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

public class YahooCMWeatherApplication extends Application {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        mObjectGraph = ObjectGraph.create(new YahooModule(this));
    }

    public static YahooCMWeatherApplication get(Context context) {
        return (YahooCMWeatherApplication) context.getApplicationContext();
    }

    public final void inject(Object object) {
        mObjectGraph.inject(object);
    }

}
