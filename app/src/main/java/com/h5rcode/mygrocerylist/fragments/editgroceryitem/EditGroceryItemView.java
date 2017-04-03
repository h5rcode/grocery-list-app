package com.h5rcode.mygrocerylist.fragments.editgroceryitem;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;

import java.util.List;

public interface EditGroceryItemView {
    void onGroceryItemCategoriesLoaded(List<GroceryItemCategory> groceryItemCategories);

    void groceryItemUpdateSucceeded(GroceryItem groceryItem);

    void groceryItemUpdatedByAnOtherUser(GroceryItem groceryItem);

    void groceryItemRemovedByAnOtherUser(GroceryItem groceryItem);

    void onLoadGroceryItemCategoriesError(Throwable e);

    void onUpdateGroceryItemError(Throwable e);
}
