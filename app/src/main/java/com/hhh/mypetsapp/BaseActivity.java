package com.hhh.mypetsapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    private Locale mCurrentLocale;

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentLocale = getResources().getConfiguration().locale;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Locale locale = new Locale.Builder().setLanguage(getLocale(this)).build();

        if (!locale.equals(mCurrentLocale)) {
            mCurrentLocale = locale;
            Locale.setDefault(mCurrentLocale);
            android.content.res.Configuration config = new android.content.res.Configuration();
            config.locale = mCurrentLocale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            recreate();
        }
    }

    public static String getLocale(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String lang = sharedPreferences.getString("language", "Русский");
        switch (lang) {
            case "English":
                lang = "en";
                break;
            case "Русский":
                lang = "ru";
                break;
        }
        return lang;
    }
}
