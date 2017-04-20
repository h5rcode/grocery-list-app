package com.h5rcode.mygrocerylist.fragments.settings;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.h5rcode.mygrocerylist.MyGroceryListApp;
import com.h5rcode.mygrocerylist.R;
import com.h5rcode.mygrocerylist.configuration.JobConfiguration;
import com.h5rcode.mygrocerylist.constants.PreferenceName;
import com.h5rcode.mygrocerylist.jobs.GroceryListJob;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final int GroceryListJobId = 1;
    private static final String TAG = SettingsFragment.class.getName();

    @Inject
    JobConfiguration _jobConfiguration;

    private JobScheduler _jobScheduler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();

        ((MyGroceryListApp) activity.getApplication()).getServiceComponent().inject(this);

        _jobScheduler = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);

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
                scheduleGroceryListJob(getActivity(), minutesBetweenQuantityChecks);
            } else {
                Log.i(TAG, "Cancelling job.");
                _jobScheduler.cancel(GroceryListJobId);
            }
        } else if (PreferenceName.MINUTES_BETWEEN_QUANTITY_CHECKS.equals(preference.getKey())) {
            int minutesBetweenQuantityChecks = Integer.parseInt((String) newValue);
            isNewValueCorrect = minutesBetweenQuantityChecks >= 1;

            if (isNewValueCorrect) {
                scheduleGroceryListJob(getActivity(), minutesBetweenQuantityChecks);
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

    private void scheduleGroceryListJob(Activity activity, int minutesBetweenQuantityChecks) {
        Log.i(TAG, "Scheduling the job to run every " + minutesBetweenQuantityChecks + " minutes.");

        ComponentName componentName = new ComponentName(activity.getPackageName(), GroceryListJob.class.getName());

        int intervalMillis = minutesBetweenQuantityChecks * 60 * 1000;

        JobInfo.Builder builder = new JobInfo.Builder(GroceryListJobId, componentName)
                .setPeriodic(intervalMillis)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);

        JobInfo jobInfo = builder.build();

        _jobScheduler.cancel(GroceryListJobId);

        if (_jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_FAILURE) {
            throw new RuntimeException("Could not schedule job.");
        }
    }
}
