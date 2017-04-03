package com.h5rcode.mygrocerylist.fragments.editgroceryitem;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;

public interface EditGroceryItemPresenter {

    void setEditGroceryItemView(EditGroceryItemView editGroceryItemView);

    void onCreateDialog();

    void onUpdateGroceryItem(GroceryItem groceryItem);

    void onDestroy();
}
