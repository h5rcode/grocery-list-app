package com.h5rcode.mygrocerylist.dependencies.components;

import com.h5rcode.mygrocerylist.dependencies.modules.ServiceModule;
import com.h5rcode.mygrocerylist.fragments.addgroceryitem.AddGroceryItemFragment;
import com.h5rcode.mygrocerylist.fragments.editgroceryitem.EditGroceryItemFragment;
import com.h5rcode.mygrocerylist.fragments.grocerylist.GroceryListFragment;
import com.h5rcode.mygrocerylist.fragments.settings.SettingsFragment;
import com.h5rcode.mygrocerylist.jobs.GroceryListJob;
import com.h5rcode.mygrocerylist.jobs.android.GroceryListJobAndroidImpl;
import com.h5rcode.mygrocerylist.jobs.firebase.GroceryListJobFirebaseImpl;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = ServiceModule.class)
@Singleton
public interface ServiceComponent {
    void inject(GroceryListFragment groceryListFragment);

    void inject(EditGroceryItemFragment editGroceryItemFragment);

    void inject(AddGroceryItemFragment addGroceryItemFragment);

    void inject(SettingsFragment settingsFragment);

    void inject(GroceryListJob groceryListJob);

    void inject(GroceryListJobAndroidImpl groceryListJobAndroid);

    void inject(GroceryListJobFirebaseImpl groceryListJobFirebase);
}
