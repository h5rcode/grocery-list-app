package com.h5rcode.mygrocerylist.fragments.addgroceryitem;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;

public interface AddGroceryItemPresenter {

    void onCreateDialog();

    void setAddGroceryItemView(AddGroceryItemView addGroceryItemView);

    void saveGroceryItem(GroceryItem groceryItem);
}
