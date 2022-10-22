package com.hhh.mypetsapp;

import static com.hhh.mypetsapp.BaseActivity.getLocale;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.Locale;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setLocale();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLocale();
    }


    private void setLocale() {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(getLocale(this));
        if (!configuration.locale.equals(locale)) {
            Locale.setDefault(locale);
            configuration.locale = locale;
            configuration.setLocale(locale);
            configuration.setLayoutDirection(locale);
            resources.updateConfiguration(configuration, dm);
            getBaseContext().getResources().updateConfiguration(configuration,
                    getBaseContext().getResources().getDisplayMetrics());
        }
    }
}
