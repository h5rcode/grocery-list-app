package com.h5rcode.mygrocerylist.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.h5rcode.mygrocerylist.constants.PreferenceName;

import javax.inject.Inject;

public class JobConfigurationImpl implements JobConfiguration {
    private final static int DEFAULT_MINUTES_BETWEEN_QUANTITY_CHECKS = 30;

    private final Context _context;

    @Inject
    public JobConfigurationImpl(Context context) {
        _context = context;
    }

    @Override
    public int getMinutesBetweenQuantityChecks() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        String preferenceValue = preferences.getString(PreferenceName.MINUTES_BETWEEN_QUANTITY_CHECKS, null);

        int minutesBetweenQuantityChecks;
        if (preferenceValue == null) {
            minutesBetweenQuantityChecks = DEFAULT_MINUTES_BETWEEN_QUANTITY_CHECKS;
        } else {
            minutesBetweenQuantityChecks = Integer.parseInt(preferenceValue);

            if (minutesBetweenQuantityChecks < 1) {
                throw new RuntimeException("Invalid setting: " + PreferenceName.MINUTES_BETWEEN_QUANTITY_CHECKS + " = " + preferenceValue);
            }
        }

        return minutesBetweenQuantityChecks;
    }

    @Override
    public boolean isItemsQuantityRatioAboveMax() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        return preferences.getBoolean(PreferenceName.IS_ITEMS_QUANTITY_RATIO_ABOVE_MAX, false);
    }

    @Override
    public void setIsItemsQuantityRatioAboveMax(boolean isItemsQuantityRatioAboveMax) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(PreferenceName.IS_ITEMS_QUANTITY_RATIO_ABOVE_MAX, isItemsQuantityRatioAboveMax);
        editor.apply();
    }
}
