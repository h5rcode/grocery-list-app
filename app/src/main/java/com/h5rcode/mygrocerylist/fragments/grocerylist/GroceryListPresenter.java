package com.h5rcode.mygrocerylist.fragments.grocerylist;

public interface GroceryListPresenter {
    void onCreate();

    void onDeleteItem(long id);

    void onDestroy();

    void setGroceryListView(GroceryListView groceryListView);
}
