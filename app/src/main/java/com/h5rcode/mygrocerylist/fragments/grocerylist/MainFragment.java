package com.h5rcode.mygrocerylist.fragments.grocerylist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.h5rcode.mygrocerylist.adapters.GroceryListAdapter;
import com.h5rcode.mygrocerylist.adapters.viewmodels.GroceryElementViewModel;
import com.h5rcode.mygrocerylist.adapters.viewmodels.GroceryItemViewModel;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.fragments.addgroceryitem.AddGroceryItemDialogFragment;
import com.h5rcode.mygrocerylist.fragments.editgroceryitem.EditGroceryItemDialogFragment;
import com.h5rcode.mygrocerylist.services.models.GroceryList;

import javax.inject.Inject;

public class MainFragment extends Fragment implements AddGroceryItemDialogFragment.AddItemDialogListener, EditGroceryItemDialogFragment.EditItemDialogListener, GroceryListView {
    private static final int ITEM_DELETE = 1;
    private static final String TAG = MainFragment.class.getName();
    private static final String TAG_EDIT_ITEM_FRAGMENT = "TAG_EDIT_ITEM_FRAGMENT";
    private static final String TAG_ADD_ITEM_FRAGMENT = "TAG_ADD_ITEM_FRAGMENT";

    @Inject
    GroceryListPresenter _groceryListPresenter;

    private GroceryListAdapter _groceryListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        _groceryListAdapter = new GroceryListAdapter(getContext());

        ((MyGroceryListApp) getActivity().getApplication()).getServiceComponent().inject(this);

        _groceryListPresenter.setGroceryListView(this);
        _groceryListPresenter.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        FloatingActionButton buttonAddGroceryItem = (FloatingActionButton) view.findViewById(R.id.button_add_item);
        buttonAddGroceryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add button clicked.");

                AddGroceryItemDialogFragment dialog = new AddGroceryItemDialogFragment();
                dialog.setTargetFragment(MainFragment.this, 0);
                dialog.show(getFragmentManager(), TAG_ADD_ITEM_FRAGMENT);
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
                    EditGroceryItemDialogFragment dialog = EditGroceryItemDialogFragment.newInstance(groceryItem);
                    dialog.setTargetFragment(MainFragment.this, 0);
                    dialog.show(getFragmentManager(), TAG_EDIT_ITEM_FRAGMENT);
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
    public void onDestroy() {
        super.onDestroy();
        _groceryListPresenter.onDestroy();
    }

    @Override
    public void onItemEdited(GroceryItem item) {
        _groceryListAdapter.updateGroceryItem(item);
    }

    @Override
    public void onItemRemoved(GroceryItem item) {
        _groceryListAdapter.removeGroceryItem(item.id);
    }

    @Override
    public void onGroceryListLoaded(GroceryList groceryList) {
        _groceryListAdapter.initialize(groceryList);
    }

    @Override
    public void onItemDeleted(long id) {
        _groceryListAdapter.removeGroceryItem(id);
    }

    @Override
    public void onLoadGroceryListError(Throwable e) {
        Log.e(TAG, "An error occurred when loading the grocery list.", e);
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

    @Override
    public void onSave(GroceryItem groceryItem) {
        _groceryListAdapter.addGroceryItem(groceryItem);
    }
}
