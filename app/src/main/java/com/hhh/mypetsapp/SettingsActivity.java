package com.hhh.mypetsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity{

    private SharedPreferences defPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean key = defPref.getBoolean("theme", false);
        if (key){
            //dark
            setTheme(R.style.Theme_MyPetsApp_Dark);
        }
        else {
            //light
            setTheme(R.style.Theme_MyPetsApp);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public static class SettingsFragment extends PreferenceFragmentCompat{
        private SharedPreferences.OnSharedPreferenceChangeListener listener;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    getActivity().recreate();
                }
            };
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        }

        @Override
        public void onPause() {
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
            super.onPause();
        }
    }
}