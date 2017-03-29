package com.h5rcode.mygrocerylist.apiclient;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;

import java.util.List;

public interface GroceryListClient {
    void addItem(GroceryItem groceryItem);

    void deleteItem(long id);

    List<GroceryItemCategory> getCategories();

    GroceryItem getItem(long id);

    List<GroceryItem> getItems();

    void updateItem(GroceryItem groceryItem);
}
