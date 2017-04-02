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
    public int updateGroceryItem(GroceryItem groceryItem) {
        return _groceryListClient.updateGroceryItem(groceryItem);
    }

    @Override
    public GroceryItem getGroceryItem(long id) {
        return _groceryListClient.getItem(id);
    }
}
