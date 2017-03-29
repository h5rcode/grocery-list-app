package com.h5rcode.mygrocerylist.adapters.viewmodels;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;

public class GroceryItemViewModel implements GroceryElementViewModel {

    private final GroceryItem _groceryItem;

    public GroceryItemViewModel(GroceryItem groceryItem) {
        _groceryItem = groceryItem;
    }

    public GroceryItem getGroceryItem() {
        return _groceryItem;
    }
}
