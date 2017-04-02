package com.h5rcode.mygrocerylist.fragments;


import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;

public class GroceryItemUpdateResult {
    private GroceryItem _groceryItem;
    private boolean _updateSucceeded;

    private GroceryItemUpdateResult(GroceryItem groceryItem, boolean updateSucceeded) {
        _groceryItem = groceryItem;
        _updateSucceeded = updateSucceeded;
    }

    public static GroceryItemUpdateResult createSuccessfulUpdateResult(GroceryItem groceryItem) {
        return new GroceryItemUpdateResult(groceryItem, true);
    }

    public static GroceryItemUpdateResult createFailedUpdateResult(GroceryItem groceryItem) {
        return new GroceryItemUpdateResult(groceryItem, false);
    }

    public boolean updateSucceeded() {
        return _updateSucceeded;
    }

    public GroceryItem getGroceryItem() {
        return _groceryItem;
    }
}