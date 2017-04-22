package com.h5rcode.mygrocerylist.fragments.editgroceryitem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.h5rcode.mygrocerylist.MyGroceryListApp;
import com.h5rcode.mygrocerylist.R;
import com.h5rcode.mygrocerylist.adapters.CategoryListAdapter;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

public class EditGroceryItemFragment extends Fragment implements EditGroceryItemView {
    public static final String EXTRA_ITEM_EDITION_RESULT = "EXTRA_ITEM_EDITION_RESULT";
    public static final String EXTRA_ITEM_EDITED = "EXTRA_ITEM_EDITED";
    public static final String EXTRA_ITEM_TO_EDIT = "EXTRA_ITEM_TO_EDIT";

    private static final String TAG = EditGroceryItemFragment.class.getName();

    private Spinner _spinnerCategory;
    private TextView _textMinimumQuantity;
    private TextView _textCurrentQuantity;
    private TextView _textDesiredQuantity;

    @Inject
    EditGroceryItemPresenter editGroceryItemPresenter;

    private GroceryItem _groceryItem;
    private MenuItem _menuItemSave;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        FragmentActivity activity = getActivity();
        ((MyGroceryListApp) activity.getApplication()).getServiceComponent().inject(this);

        editGroceryItemPresenter.setEditGroceryItemView(this);

        Intent intent = activity.getIntent();

        _groceryItem = (GroceryItem) intent.getSerializableExtra(EXTRA_ITEM_TO_EDIT);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_grocery_item, menu);

        _menuItemSave = menu.findItem(R.id.edit_grocery_item_menu_save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_grocery_item_menu_save:
                _menuItemSave.setEnabled(false);
                editGroceryItemPresenter.onUpdateGroceryItem(_groceryItem);
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
        View view = inflater.inflate(R.layout.fragment_edit_grocery_item, null);

        _spinnerCategory = (Spinner) view.findViewById(R.id.spinner_edit_category);
        _textMinimumQuantity = (TextView) view.findViewById(R.id.text_minimum_quantity);
        _textCurrentQuantity = (TextView) view.findViewById(R.id.text_current_quantity);
        _textDesiredQuantity = (TextView) view.findViewById(R.id.text_desired_quantity);

        Button buttonDecrementCurrentQuantity = (Button) view.findViewById(R.id.button_decrement_current_quantity);
        Button buttonIncrementCurrentQuantity = (Button) view.findViewById(R.id.button_increment_current_quantity);
        Button buttonDecrementMinimumQuantity = (Button) view.findViewById(R.id.button_decrement_minimum_quantity);
        Button buttonIncrementMinimumQuantity = (Button) view.findViewById(R.id.button_increment_minimum_quantity);
        Button buttonDecrementDesiredQuantity = (Button) view.findViewById(R.id.button_decrement_desired_quantity);
        Button buttonIncrementDesiredQuantity = (Button) view.findViewById(R.id.button_increment_desired_quantity);

        editGroceryItemPresenter.onCreateDialog();

        _textCurrentQuantity.setText(String.valueOf(_groceryItem.currentQuantity));
        _textMinimumQuantity.setText(String.valueOf(_groceryItem.minimumQuantity));
        _textDesiredQuantity.setText(String.valueOf(_groceryItem.desiredQuantity));

        buttonDecrementCurrentQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentQuantity(-1);
            }
        });
        buttonIncrementCurrentQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentQuantity(1);
            }
        });

        buttonDecrementMinimumQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMinimumQuantity(-1);
            }
        });
        buttonIncrementMinimumQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMinimumQuantity(1);
            }
        });

        buttonDecrementDesiredQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDesiredQuantity(-1);
            }
        });
        buttonIncrementDesiredQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDesiredQuantity(1);
            }
        });

        _spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GroceryItemCategory category = (GroceryItemCategory) _spinnerCategory.getItemAtPosition(position);
                _groceryItem.categoryCode = category.code;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        activity.setTitle(_groceryItem.label);
    }

    private void updateDesiredQuantity(int increment) {
        int newDesiredQuantity = _groceryItem.desiredQuantity + increment;
        if (newDesiredQuantity > 0 && newDesiredQuantity > _groceryItem.minimumQuantity) {
            _groceryItem.desiredQuantity = newDesiredQuantity;
            _textDesiredQuantity.setText(String.valueOf(_groceryItem.desiredQuantity));
        }
    }

    private void updateMinimumQuantity(int increment) {
        int newMinimumQuantity = _groceryItem.minimumQuantity + increment;
        if (newMinimumQuantity > 0 && newMinimumQuantity <= _groceryItem.desiredQuantity) {
            _groceryItem.minimumQuantity = newMinimumQuantity;
            _textMinimumQuantity.setText(String.valueOf(_groceryItem.minimumQuantity));
        }
    }

    private void updateCurrentQuantity(int increment) {
        int newQuantity = _groceryItem.currentQuantity + increment;

        if (newQuantity >= 0) {
            _groceryItem.currentQuantity = newQuantity;
            _textCurrentQuantity.setText(String.valueOf(_groceryItem.currentQuantity));
        }
    }

    @Override
    public void onGroceryItemCategoriesLoaded(List<GroceryItemCategory> groceryItemCategories) {
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(getContext(), groceryItemCategories);
        _spinnerCategory.setAdapter(categoryListAdapter);
        for (int i = 0; i < groceryItemCategories.size(); i++) {
            GroceryItemCategory category = groceryItemCategories.get(i);
            if (category.code.equals(_groceryItem.categoryCode)) {
                _spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void groceryItemUpdateSucceeded(GroceryItem groceryItem) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ITEM_EDITION_RESULT, ItemEditionResult.SUCCESS);
        data.putExtra(EXTRA_ITEM_EDITED, groceryItem);

        Activity activity = getActivity();
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }

    @Override
    public void groceryItemUpdatedByAnOtherUser(GroceryItem groceryItem) {
        _groceryItem.minimumQuantity = groceryItem.minimumQuantity;
        _groceryItem.currentQuantity = groceryItem.currentQuantity;
        _groceryItem.desiredQuantity = groceryItem.desiredQuantity;
        _groceryItem.version = groceryItem.version;

        _textCurrentQuantity.setText(String.valueOf(_groceryItem.currentQuantity));
        _textMinimumQuantity.setText(String.valueOf(_groceryItem.minimumQuantity));
        _textDesiredQuantity.setText(String.valueOf(_groceryItem.desiredQuantity));

        Toast.makeText(getContext(), R.string.message_item_edited_by_other_user, Toast.LENGTH_LONG).show();

        _menuItemSave.setEnabled(true);
    }

    @Override
    public void groceryItemRemovedByAnOtherUser(GroceryItem groceryItem) {
        Toast.makeText(getContext(), R.string.message_item_removed_by_other_user, Toast.LENGTH_LONG).show();

        Intent data = new Intent();
        data.putExtra(EXTRA_ITEM_EDITION_RESULT, ItemEditionResult.ITEM_REMOVED_BY_ANOTHER_USER);
        data.putExtra(EXTRA_ITEM_EDITED, _groceryItem);

        Activity activity = getActivity();
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
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
    public void onUpdateGroceryItemError(Throwable e) {
        Log.e(TAG, "An error occurred when updating an item.", e);

        View view = getView();
        if (view != null) {
            Snackbar.make(view, getString(R.string.error_updating_item, e.getMessage()), Snackbar.LENGTH_LONG).show();
        }
    }

    public enum ItemEditionResult implements Serializable {
        SUCCESS,
        ITEM_REMOVED_BY_ANOTHER_USER
    }
}
