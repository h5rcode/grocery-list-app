package com.h5rcode.mygrocerylist.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.h5rcode.mygrocerylist.constants.PreferenceName;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

public class ClientConfigurationImpl implements ClientConfiguration {

    private final Context mContext;

    @Inject
    public ClientConfigurationImpl(Context context) {
        mContext = context;
    }

    @Override
    public URL getGroceryListUrl() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String serverAddress = preferences.getString(PreferenceName.GROCERY_LIST_URL, null);

        URL url;
        if (serverAddress == null) {
            url = null;
        } else {
            try {
                url = new URL(serverAddress);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return url;
    }
}
