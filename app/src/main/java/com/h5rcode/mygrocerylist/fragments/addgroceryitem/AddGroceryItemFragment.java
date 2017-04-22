package com.h5rcode.mygrocerylist.fragments.addgroceryitem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.h5rcode.mygrocerylist.MyGroceryListApp;
import com.h5rcode.mygrocerylist.R;
import com.h5rcode.mygrocerylist.adapters.CategoryListAdapter;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;

import java.util.List;

import javax.inject.Inject;

public class AddGroceryItemFragment extends Fragment implements AddGroceryItemView {
    public static String EXTRA_NEW_ITEM = "EXTRA_NEW_ITEM";
    private static String TAG = AddGroceryItemFragment.class.getName();

    private Spinner _spinnerCategory;

    @Inject
    AddGroceryItemPresenter addGroceryItemPresenter;
    private EditText _textLabel;
    private EditText _textMinimumQuantity;
    private EditText _textDesiredQuantity;
    private EditText _textCurrentQuantity;
    private MenuItem _menuItemSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ((MyGroceryListApp) getActivity().getApplication()).getServiceComponent().inject(this);

        addGroceryItemPresenter.setAddGroceryItemView(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_grocery_item, menu);

        _menuItemSave = menu.findItem(R.id.add_grocery_item_menu_save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_grocery_item_menu_save:
                saveGroceryItem();
                return true;

            case android.R.id.home:
                Activity activity = getActivity();
                activity.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_grocery_item, null);

        _textLabel = (EditText) view.findViewById(R.id.edit_text_label);
        _spinnerCategory = (Spinner) view.findViewById(R.id.spinner_category);

        _textMinimumQuantity = (EditText) view.findViewById(R.id.edit_text_minimum_quantity);
        _textDesiredQuantity = (EditText) view.findViewById(R.id.edit_text_desired_quantity);
        _textCurrentQuantity = (EditText) view.findViewById(R.id.edit_text_current_quantity);

        addGroceryItemPresenter.onCreateDialog();

        return view;
    }

    private void saveGroceryItem() {
        _menuItemSave.setEnabled(false);

        Editable editableLabel = _textLabel.getText();
        GroceryItemCategory category = (GroceryItemCategory) _spinnerCategory.getSelectedItem();
        Editable editableMinimumQuantity = _textMinimumQuantity.getText();
        Editable editableDesiredQuantity = _textDesiredQuantity.getText();
        Editable editableCurrentQuantity = _textCurrentQuantity.getText();

        boolean isInputValid = true;
        if (editableLabel == null || editableLabel.toString().equals("")) {
            isInputValid = false;
            _textLabel.setError(getString(R.string.info_mandatory_field));
        }

        int minimumQuantity = 0;
        if (editableMinimumQuantity == null || editableMinimumQuantity.toString().equals("")) {
            isInputValid = false;
            _textMinimumQuantity.setError(getString(R.string.info_mandatory_field));
        } else {
            minimumQuantity = Integer.parseInt(_textMinimumQuantity.getText().toString());
        }

        int desiredQuantity = 0;
        if (editableDesiredQuantity == null || editableDesiredQuantity.toString().equals("")) {
            isInputValid = false;
            _textDesiredQuantity.setError(getString(R.string.info_mandatory_field));
        } else {
            desiredQuantity = Integer.parseInt(_textDesiredQuantity.getText().toString());
        }

        if (desiredQuantity < minimumQuantity) {
            isInputValid = false;
            _textDesiredQuantity.setError(getString(R.string.info_desired_quantity_must_be_greater_than_minimum_quantity));
        }

        if (editableCurrentQuantity == null || editableCurrentQuantity.toString().equals("")) {
            isInputValid = false;
            _textCurrentQuantity.setError(getString(R.string.info_mandatory_field));
        }

        if (isInputValid) {
            String label = editableLabel.toString().trim();
            int currentQuantity = Integer.parseInt(_textCurrentQuantity.getText().toString());

            GroceryItem item = new GroceryItem();
            item.label = label;
            item.minimumQuantity = minimumQuantity;
            item.desiredQuantity = desiredQuantity;
            item.currentQuantity = currentQuantity;
            item.categoryCode = category.code;

            addGroceryItemPresenter.saveGroceryItem(item);
        } else {
            _menuItemSave.setEnabled(true);
        }
    }

    @Override
    public void onGroceryItemCategoriesLoaded(List<GroceryItemCategory> groceryItemCategories) {
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(getContext(), groceryItemCategories);
        _spinnerCategory.setAdapter(categoryListAdapter);
    }

    @Override
    public void onLoadGroceryItemCategoriesError(Throwable e) {
        Log.e(TAG, "An error occurred when loading the grocery item categories.", e);

        View view = getView();
        if (view != null) {
            Snackbar.make(view, getString(R.string.error_loading_grocery_list_categories, e.getMessage()), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGroceryItemSaved(GroceryItem groceryItem) {
        Intent data = new Intent();
        data.putExtra(EXTRA_NEW_ITEM, groceryItem);

        FragmentActivity activity = getActivity();
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }

    @Override
    public void onAddItemError(Throwable e) {
        Log.e(TAG, "An error occurred when saving the grocery item.", e);

        View view = getView();
        if (view != null) {
            Snackbar.make(view, getString(R.string.error_adding_item, e.getMessage()), Snackbar.LENGTH_LONG).show();
        }
    }
}
