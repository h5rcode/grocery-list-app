package com.h5rcode.mygrocerylist;

import android.app.Application;

import com.h5rcode.mygrocerylist.dependencies.components.DaggerServiceComponent;
import com.h5rcode.mygrocerylist.dependencies.components.ServiceComponent;
import com.h5rcode.mygrocerylist.dependencies.modules.ServiceModule;

public class MyGroceryListApp extends Application {

    private ServiceComponent mServiceComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mServiceComponent = DaggerServiceComponent.builder()
                .serviceModule(new ServiceModule(this))
                .build();
    }

    public ServiceComponent getServiceComponent() {
        return mServiceComponent;
    }
}
