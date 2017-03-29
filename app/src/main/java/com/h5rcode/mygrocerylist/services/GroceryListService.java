package com.h5rcode.mygrocerylist.services;

import com.h5rcode.mygrocerylist.services.models.GroceryList;

public interface GroceryListService {
    void deleteGroceryItem(long id);

    GroceryList getGroceryList();
}
