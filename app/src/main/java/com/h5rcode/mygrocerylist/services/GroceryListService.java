package com.h5rcode.mygrocerylist.services;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;
import com.h5rcode.mygrocerylist.services.models.GroceryList;

import java.util.List;

public interface GroceryListService {
    void deleteGroceryItem(long id);

    GroceryList getGroceryList();

    List<GroceryItemCategory> getCategoryItemCategories();

    int updateGroceryItem(GroceryItem groceryItem);

    GroceryItem getGroceryItem(long id);
}
