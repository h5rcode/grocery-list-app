package com.h5rcode.mygrocerylist.services.models;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;

public class GroceryItemUpdateResult {
    private final int _responseStatusCode;
    private final GroceryItem _groceryItem;

    public GroceryItemUpdateResult(int responseStatusCode, GroceryItem groceryItem) {
        _responseStatusCode = responseStatusCode;
        _groceryItem = groceryItem;
    }

    public GroceryItem getGroceryItem() {
        return _groceryItem;
    }

    public int getResponseStatusCode() {
        return _responseStatusCode;
    }
}
