package com.h5rcode.mygrocerylist.apiclient;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;

import java.util.List;

public interface GroceryListClient {
    void addItem(GroceryItem groceryItem);

    void deleteGroceryItem(long id);

    List<GroceryItemCategory> getGroceryItemCategories();

    GroceryItem getItem(long id);

    List<GroceryItem> getGroceryItems();

    int updateGroceryItem(GroceryItem groceryItem);
}
