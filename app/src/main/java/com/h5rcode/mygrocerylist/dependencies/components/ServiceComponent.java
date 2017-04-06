package com.h5rcode.mygrocerylist.dependencies.components;

import com.h5rcode.mygrocerylist.dependencies.modules.ServiceModule;
import com.h5rcode.mygrocerylist.fragments.addgroceryitem.AddGroceryItemDialogFragment;
import com.h5rcode.mygrocerylist.fragments.editgroceryitem.EditGroceryItemDialogFragment;
import com.h5rcode.mygrocerylist.fragments.grocerylist.GroceryListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = ServiceModule.class)
@Singleton
public interface ServiceComponent {
    void inject(GroceryListFragment groceryListFragment);

    void inject(EditGroceryItemDialogFragment editGroceryItemDialogFragment);

    void inject(AddGroceryItemDialogFragment addGroceryItemDialogFragment);
}
