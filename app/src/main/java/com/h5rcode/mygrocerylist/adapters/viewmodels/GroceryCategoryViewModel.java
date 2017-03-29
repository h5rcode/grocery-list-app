package com.h5rcode.mygrocerylist.adapters.viewmodels;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;

public class GroceryCategoryViewModel implements GroceryElementViewModel {
    private final GroceryItemCategory _GroceryItem_category;

    public GroceryCategoryViewModel(GroceryItemCategory groceryItemCategory) {
        _GroceryItem_category = groceryItemCategory;
    }

    public GroceryItemCategory getCategory() {
        return _GroceryItem_category;
    }
}
