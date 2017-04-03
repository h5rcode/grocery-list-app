package com.h5rcode.mygrocerylist.fragments.addgroceryitem;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;

import java.util.List;

public interface AddGroceryItemView {
    void onGroceryItemCategoriesLoaded(List<GroceryItemCategory> groceryItemCategories);

    void onLoadGroceryItemCategoriesError(Throwable e);

    void onGroceryItemSaved(GroceryItem groceryItem);

    void onAddItemError(Throwable e);
}
