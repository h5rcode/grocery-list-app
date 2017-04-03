package com.h5rcode.mygrocerylist.fragments.grocerylist;

import com.h5rcode.mygrocerylist.services.models.GroceryList;

public interface GroceryListView {

    void onGroceryListLoaded(GroceryList groceryList);

    void onItemDeleted(long id);

    void onLoadGroceryListError(Throwable e);

    void onDeleteItemError(Throwable e);
}
