package com.example.evrouteplannerapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        int count = getPreferenceScreen().getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference pref = getPreferenceScreen().getPreference(i);
            String value = sharedPreferences.getString(pref.getKey(), "");
            setSummary(pref, value);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference pref = findPreference(key);
        if (pref != null) {
            String value = sharedPreferences.getString(key, "");
            setSummary(pref, value);
        }
    }

    private void setSummary(Preference pref, String value) {

        if (value != null) {

            if (pref instanceof EditTextPreference)
                pref.setSummary(value);
            else if (pref instanceof ListPreference) {

                if (value.equals(getString(R.string.pref_distance_unit_miles)))
                    pref.setSummary(getString(R.string.pref_distance_unit_miles_label));
                else if (value.equals(getString(R.string.pref_distance_unit_km)))
                    pref.setSummary(getString(R.string.pref_distance_unit_km_label));
            }
        }
        else {

            if (pref instanceof EditTextPreference)
                pref.setSummary(getString(R.string.pref_distance_default));
            else if (pref instanceof ListPreference)
                pref.setSummary(getString(R.string.pref_distance_unit_miles_label));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
