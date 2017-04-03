package com.h5rcode.mygrocerylist.services;

import com.h5rcode.mygrocerylist.apiclient.GroceryListClient;
import com.h5rcode.mygrocerylist.apiclient.models.ApiResponse;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;
import com.h5rcode.mygrocerylist.services.models.GroceryItemUpdateResult;
import com.h5rcode.mygrocerylist.services.models.GroceryList;

import java.util.List;

public class GroceryListServiceImpl implements GroceryListService {

    private GroceryListClient _groceryListClient;

    public GroceryListServiceImpl(GroceryListClient groceryListClient) {
        _groceryListClient = groceryListClient;
    }

    @Override
    public void deleteGroceryItem(long id) {
        _groceryListClient.deleteGroceryItem(id);
    }

    @Override
    public GroceryList getGroceryList() {

        List<GroceryItem> items = _groceryListClient.getGroceryItems();
        List<GroceryItemCategory> categories = _groceryListClient.getGroceryItemCategories();

        return new GroceryList(categories, items);
    }

    @Override
    public List<GroceryItemCategory> getCategoryItemCategories() {
        return _groceryListClient.getGroceryItemCategories();
    }

    @Override
    public GroceryItemUpdateResult updateGroceryItem(GroceryItem groceryItem) {
        ApiResponse<GroceryItem> apiResponse = _groceryListClient.updateGroceryItem(groceryItem);

        int responseStatusCode = apiResponse.getResponseStatusCode();
        GroceryItem mostRecentGroceryItem;
        if (responseStatusCode == 200) {
            mostRecentGroceryItem = apiResponse.getResponseBody();
        } else if (responseStatusCode == 412) {
            mostRecentGroceryItem = _groceryListClient.getItem(groceryItem.id);

            if (mostRecentGroceryItem == null) {
                responseStatusCode = 404;
            }
        } else {
            throw new RuntimeException("Unhandled response status code: " + responseStatusCode);
        }

        return new GroceryItemUpdateResult(responseStatusCode, mostRecentGroceryItem);
    }

    @Override
    public GroceryItem getGroceryItem(long id) {
        return _groceryListClient.getItem(id);
    }

    @Override
    public GroceryItem addGroceryItem(GroceryItem groceryItem) {
        return _groceryListClient.addGroceryItem(groceryItem);
    }
}
