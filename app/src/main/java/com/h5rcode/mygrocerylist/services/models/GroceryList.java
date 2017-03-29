package com.h5rcode.mygrocerylist.services.models;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;

import java.util.List;

public class GroceryList {
    private List<GroceryItemCategory> _categories;
    private List<GroceryItem> _items;

    public GroceryList(List<GroceryItemCategory> categories, List<GroceryItem> items) {
        _categories = categories;
        _items = items;
    }

    public List<GroceryItemCategory> getGroceryItemCategories() {
        return _categories;
    }

    public List<GroceryItem> getGroceryItems() {
        return _items;
    }
}
