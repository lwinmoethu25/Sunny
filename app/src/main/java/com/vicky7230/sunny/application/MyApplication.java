package com.vicky7230.sunny.application;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.IoniconsModule;
import com.joanzapata.iconify.fonts.MeteoconsModule;
import com.vicky7230.sunny.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by agrim on 29/4/17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Iconify
                .with(new IoniconsModule())
                .with(new MeteoconsModule());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NunitoSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

    }
}
