package com.h5rcode.mygrocerylist.apiclient;

import com.h5rcode.mygrocerylist.apiclient.models.ApiResponse;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;
import com.h5rcode.mygrocerylist.apiclient.models.ItemsQuantityRatioInfo;

import java.util.List;

public interface GroceryListClient {
    GroceryItem addGroceryItem(GroceryItem groceryItem);

    void deleteGroceryItem(long id);

    List<GroceryItemCategory> getGroceryItemCategories();

    ItemsQuantityRatioInfo getItemsQuantityRatioInfo();

    GroceryItem getItem(long id);

    List<GroceryItem> getGroceryItems();

    ApiResponse<GroceryItem> updateGroceryItem(GroceryItem groceryItem);
}
