package com.h5rcode.mygrocerylist.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.h5rcode.mygrocerylist.R;
import com.h5rcode.mygrocerylist.constants.PreferenceName;

import java.net.MalformedURLException;
import java.net.URL;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        findPreference(PreferenceName.GROCERY_LIST_URL).setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean isNewValueCorrect = false;

        if (PreferenceName.GROCERY_LIST_URL.equals(preference.getKey())) {
            isNewValueCorrect = isValidUrl((String) newValue);
        }

        return isNewValueCorrect;
    }

    private boolean isValidUrl(String newValue) {
        boolean isValidUrl;
        try {
            new URL(newValue);
            isValidUrl = true;
        } catch (MalformedURLException e) {
            isValidUrl = false;
        }
        return isValidUrl;
    }
}
