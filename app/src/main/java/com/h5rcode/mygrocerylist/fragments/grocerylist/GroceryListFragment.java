package com.h5rcode.mygrocerylist.fragments.grocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.h5rcode.mygrocerylist.MyGroceryListApp;
import com.h5rcode.mygrocerylist.R;
import com.h5rcode.mygrocerylist.activities.AddGroceryItemActivity;
import com.h5rcode.mygrocerylist.activities.EditGroceryItemActivity;
import com.h5rcode.mygrocerylist.adapters.GroceryListAdapter;
import com.h5rcode.mygrocerylist.adapters.viewmodels.GroceryElementViewModel;
import com.h5rcode.mygrocerylist.adapters.viewmodels.GroceryItemViewModel;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.fragments.addgroceryitem.AddGroceryItemFragment;
import com.h5rcode.mygrocerylist.fragments.editgroceryitem.EditGroceryItemFragment;
import com.h5rcode.mygrocerylist.services.models.GroceryList;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

public class GroceryListFragment extends Fragment implements GroceryListView {
    private static final int ITEM_DELETE = 1;
    private static final String TAG = GroceryListFragment.class.getName();
    private static final int REQUEST_ADD_ITEM = 1;
    private static final int REQUEST_EDIT_ITEM = 2;

    @Inject
    GroceryListPresenter _groceryListPresenter;

    private SwipeRefreshLayout _swipeRefreshLayout;
    private GroceryListAdapter _groceryListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        _groceryListAdapter = new GroceryListAdapter(getContext());

        ((MyGroceryListApp) getActivity().getApplication()).getServiceComponent().inject(this);

        _groceryListPresenter.setGroceryListView(this);
        _groceryListPresenter.loadGroceryList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);

        _swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.item_list_swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _groceryListPresenter.loadGroceryList();
            }
        });

        FloatingActionButton buttonAddGroceryItem = (FloatingActionButton) view.findViewById(R.id.button_add_item);
        buttonAddGroceryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddGroceryItemActivity.class);
                startActivityForResult(intent, REQUEST_ADD_ITEM);
            }
        });

        ListView listView = (ListView) view.findViewById(R.id.item_list);
        listView.setAdapter(_groceryListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Object listItem = parent.getItemAtPosition(position);


                if (listItem instanceof GroceryItemViewModel) {
                    GroceryItemViewModel viewModel = (GroceryItemViewModel) listItem;
                    GroceryItem groceryItem = viewModel.getGroceryItem();

                    Intent intent = new Intent(getContext(), EditGroceryItemActivity.class);
                    intent.putExtra(EditGroceryItemFragment.EXTRA_ITEM_TO_EDIT, groceryItem);

                    startActivityForResult(intent, REQUEST_EDIT_ITEM);
                }
            }
        });

        registerForContextMenu(listView);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        GroceryElementViewModel element = (GroceryElementViewModel) _groceryListAdapter.getItem(info.position);

        if (element instanceof GroceryItemViewModel) {
            GroceryItem item = ((GroceryItemViewModel) element).getGroceryItem();
            menu.setHeaderTitle(item.label);
            menu.add(Menu.NONE, ITEM_DELETE, Menu.NONE, R.string.menu_grocery_item_delete);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        final boolean result;

        switch (menuItem.getItemId()) {
            case ITEM_DELETE:
                final GroceryItemViewModel groceryItemViewModel = (GroceryItemViewModel) _groceryListAdapter.getItem(info.position);
                final GroceryItem groceryItem = groceryItemViewModel.getGroceryItem();
                final long id = groceryItem.id;
                _groceryListPresenter.onDeleteItem(id);
                result = true;
                break;

            default:
                result = super.onContextItemSelected(menuItem);
                break;
        }

        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_ITEM) {
            if (resultCode == RESULT_OK) {
                GroceryItem groceryItem = (GroceryItem) data.getSerializableExtra(AddGroceryItemFragment.EXTRA_NEW_ITEM);
                _groceryListAdapter.addGroceryItem(groceryItem);
            }
        } else if (requestCode == REQUEST_EDIT_ITEM) {
            if (resultCode == RESULT_OK) {
                EditGroceryItemFragment.ItemEditionResult result = (EditGroceryItemFragment.ItemEditionResult) data.getSerializableExtra(EditGroceryItemFragment.EXTRA_ITEM_EDITION_RESULT);
                GroceryItem groceryItem = (GroceryItem) data.getSerializableExtra(EditGroceryItemFragment.EXTRA_ITEM_EDITED);

                if (result == EditGroceryItemFragment.ItemEditionResult.ITEM_REMOVED_BY_ANOTHER_USER) {
                    _groceryListAdapter.removeGroceryItem(groceryItem.id);
                } else if (result == EditGroceryItemFragment.ItemEditionResult.SUCCESS) {
                    data.getSerializableExtra(EditGroceryItemFragment.EXTRA_ITEM_EDITION_RESULT);

                    _groceryListAdapter.updateGroceryItem(groceryItem);
                }
            }
        }
    }

    @Override
    public void onGroceryListLoaded(GroceryList groceryList) {
        _groceryListAdapter.initialize(groceryList);
        _swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemDeleted(long id) {
        _groceryListAdapter.removeGroceryItem(id);
    }

    @Override
    public void onLoadGroceryListError(Throwable e) {
        Log.e(TAG, "An error occurred when loading the grocery list.", e);

        _swipeRefreshLayout.setRefreshing(false);

        View view = getView();
        if (view != null) {
            Snackbar.make(view, getString(R.string.error_loading_grocery_list, e.getMessage()), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDeleteItemError(Throwable e) {
        Log.e(TAG, "An error occurred when deleting an item.", e);
        View view = getView();
        if (view != null) {
            Snackbar.make(view, getString(R.string.error_deleting_item, e.getMessage()), Snackbar.LENGTH_LONG).show();
        }
    }
}
