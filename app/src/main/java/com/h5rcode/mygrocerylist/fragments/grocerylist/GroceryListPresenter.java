package com.h5rcode.mygrocerylist.fragments.grocerylist;

public interface GroceryListPresenter {
    void loadGroceryList();

    void onDeleteItem(long id);

    void setGroceryListView(GroceryListView groceryListView);
}
