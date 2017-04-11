package com.h5rcode.mygrocerylist.dependencies.modules;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.h5rcode.mygrocerylist.MyGroceryListApp;
import com.h5rcode.mygrocerylist.apiclient.GroceryListClient;
import com.h5rcode.mygrocerylist.apiclient.GroceryListClientVolley;
import com.h5rcode.mygrocerylist.configuration.ClientConfiguration;
import com.h5rcode.mygrocerylist.configuration.ClientConfigurationImpl;
import com.h5rcode.mygrocerylist.configuration.JobConfiguration;
import com.h5rcode.mygrocerylist.configuration.JobConfigurationImpl;
import com.h5rcode.mygrocerylist.fragments.addgroceryitem.AddGroceryItemPresenter;
import com.h5rcode.mygrocerylist.fragments.addgroceryitem.AddGroceryItemPresenterImpl;
import com.h5rcode.mygrocerylist.fragments.editgroceryitem.EditGroceryItemPresenter;
import com.h5rcode.mygrocerylist.fragments.editgroceryitem.EditGroceryItemPresenterImpl;
import com.h5rcode.mygrocerylist.fragments.grocerylist.GroceryListPresenter;
import com.h5rcode.mygrocerylist.fragments.grocerylist.GroceryListPresenterImpl;
import com.h5rcode.mygrocerylist.services.GroceryListService;
import com.h5rcode.mygrocerylist.services.GroceryListServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    private final MyGroceryListApp mMyGroceryListApp;

    public ServiceModule(MyGroceryListApp myGroceryListApp) {
        mMyGroceryListApp = myGroceryListApp;
    }

    @Provides
    @Singleton
    RequestQueue provideRequestQueue(Context context) {
        return Volley.newRequestQueue(context);
    }

    @Provides
    Context provideContext() {
        return mMyGroceryListApp;
    }

    @Provides
    ClientConfiguration provideGroceryListClientConfiguration(Context context) {
        return new ClientConfigurationImpl(context);
    }

    @Provides
    JobConfiguration provideJobConfiguration(Context context) {
        return new JobConfigurationImpl(context);
    }

    @Provides
    GroceryListClient provideCategoryService(ClientConfiguration clientConfiguration, RequestQueue requestQueue) {
        return new GroceryListClientVolley(clientConfiguration, requestQueue);
    }

    @Provides
    GroceryListService provideGroceryListService(GroceryListClient groceryListClient) {
        return new GroceryListServiceImpl(groceryListClient);
    }

    @Provides
    GroceryListPresenter provideGroceryListPresenter(GroceryListService groceryListService) {
        return new GroceryListPresenterImpl(groceryListService);
    }

    @Provides
    AddGroceryItemPresenter provideAddGroceryItemPresenter(GroceryListService groceryListService) {
        return new AddGroceryItemPresenterImpl(groceryListService);
    }

    @Provides
    EditGroceryItemPresenter provideEditGroceryItemPresenter(GroceryListService groceryListService) {
        return new EditGroceryItemPresenterImpl(groceryListService);
    }
}
