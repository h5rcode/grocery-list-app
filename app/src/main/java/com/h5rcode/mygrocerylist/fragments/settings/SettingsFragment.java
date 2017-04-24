package com.h5rcode.mygrocerylist.fragments.settings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.h5rcode.mygrocerylist.MyGroceryListApp;
import com.h5rcode.mygrocerylist.R;
import com.h5rcode.mygrocerylist.configuration.JobConfiguration;
import com.h5rcode.mygrocerylist.constants.PreferenceName;
import com.h5rcode.mygrocerylist.jobs.GroceryListJobInfo;
import com.h5rcode.mygrocerylist.jobs.GroceryListJobScheduler;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final int GroceryListJobId = 1;
    private static final String TAG = SettingsFragment.class.getName();

    @Inject
    GroceryListJobScheduler _groceryListJobScheduler;

    @Inject
    JobConfiguration _jobConfiguration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();

        ((MyGroceryListApp) activity.getApplication()).getServiceComponent().inject(this);

        addPreferencesFromResource(R.xml.settings);

        findPreference(PreferenceName.GROCERY_LIST_URL).setOnPreferenceChangeListener(this);
        findPreference(PreferenceName.ENABLE_QUANTITY_CHECKS).setOnPreferenceChangeListener(this);
        findPreference(PreferenceName.MINUTES_BETWEEN_QUANTITY_CHECKS).setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean isNewValueCorrect = false;

        if (PreferenceName.GROCERY_LIST_URL.equals(preference.getKey())) {
            isNewValueCorrect = isValidUrl((String) newValue);
        } else if (PreferenceName.ENABLE_QUANTITY_CHECKS.equals(preference.getKey())) {
            isNewValueCorrect = true;

            boolean enableQuantityChecks = (boolean) newValue;

            if (enableQuantityChecks) {
                int minutesBetweenQuantityChecks = _jobConfiguration.getMinutesBetweenQuantityChecks();
                scheduleGroceryListJob(minutesBetweenQuantityChecks);
            } else {
                cancelGroceryListJob();
            }
        } else if (PreferenceName.MINUTES_BETWEEN_QUANTITY_CHECKS.equals(preference.getKey())) {
            int minutesBetweenQuantityChecks = Integer.parseInt((String) newValue);
            isNewValueCorrect = minutesBetweenQuantityChecks >= 1;

            if (isNewValueCorrect) {
                scheduleGroceryListJob(minutesBetweenQuantityChecks);
            }
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

    private void cancelGroceryListJob() {
        Log.i(TAG, "Cancelling job.");
        GroceryListJobInfo jobInfo = new GroceryListJobInfo(0, GroceryListJobId);
        _groceryListJobScheduler.cancelJob(jobInfo);
    }

    private void scheduleGroceryListJob(int minutesBetweenQuantityChecks) {
        Log.i(TAG, "Scheduling the job to run every " + minutesBetweenQuantityChecks + " minutes.");

        int intervalMillis = minutesBetweenQuantityChecks * 60 * 1000;
        GroceryListJobInfo jobInfo = new GroceryListJobInfo(intervalMillis, GroceryListJobId);
        _groceryListJobScheduler.scheduleJob(jobInfo);
    }
}
