package com.h5rcode.mygrocerylist.services;

import com.h5rcode.mygrocerylist.apiclient.GroceryListClient;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;
import com.h5rcode.mygrocerylist.services.models.GroceryList;

import java.util.List;

public class GroceryListServiceImpl implements GroceryListService {

    private GroceryListClient _groceryListClient;

    public GroceryListServiceImpl(GroceryListClient groceryListClient) {
        _groceryListClient = groceryListClient;
    }

    @Override
    public void deleteGroceryItem(long id) {
        _groceryListClient.deleteItem(id);
    }

    @Override
    public GroceryList getGroceryList() {

        List<GroceryItem> items = _groceryListClient.getItems();
        List<GroceryItemCategory> categories = _groceryListClient.getCategories();

        return new GroceryList(categories, items);
    }
}
