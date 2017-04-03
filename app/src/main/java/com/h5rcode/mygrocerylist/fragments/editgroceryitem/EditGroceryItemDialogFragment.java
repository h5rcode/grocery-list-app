package com.h5rcode.mygrocerylist.fragments.editgroceryitem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.List;

import javax.inject.Inject;

public class EditGroceryItemDialogFragment extends DialogFragment implements EditGroceryItemView {

    private static String ARG_ITEM = "ARG_ITEM";
    private static String TAG = EditItemDialogListener.class.getName();

    private Spinner _spinnerCategory;
    private TextView _textMinimumQuantity;
    private TextView _textCurrentQuantity;
    private TextView _textDesiredQuantity;

    @Inject
    EditGroceryItemPresenter editGroceryItemPresenter;

    private GroceryItem _groceryItem;
    private EditItemDialogListener _editItemDialogListener;

    public static EditGroceryItemDialogFragment newInstance(GroceryItem item) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);

        EditGroceryItemDialogFragment fragment = new EditGroceryItemDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyGroceryListApp) getActivity().getApplication()).getServiceComponent().inject(this);

        editGroceryItemPresenter.setEditGroceryItemView(this);

        Bundle args = getArguments();
        _groceryItem = (GroceryItem) args.getSerializable(ARG_ITEM);
        _editItemDialogListener = (EditItemDialogListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_item, null);

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
        Button okButton = (Button) view.findViewById(R.id.button_ok);

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

        editGroceryItemPresenter.onCreateDialog();

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

        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editGroceryItemPresenter.onUpdateGroceryItem(_groceryItem);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(_groceryItem.label);
        builder.setView(view);

        return builder.create();
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
        _groceryItem.version = groceryItem.version;
        _editItemDialogListener.onItemEdited(_groceryItem);
        dismiss();
    }

    @Override
    public void groceryItemUpdatedByAnOtherUser(GroceryItem groceryItem) {
        _groceryItem.minimumQuantity = groceryItem.minimumQuantity;
        _groceryItem.currentQuantity = groceryItem.currentQuantity;
        _groceryItem.desiredQuantity = groceryItem.desiredQuantity;
        _groceryItem.version = groceryItem.version;

        _editItemDialogListener.onItemEdited(_groceryItem);

        _textCurrentQuantity.setText(String.valueOf(_groceryItem.currentQuantity));
        _textMinimumQuantity.setText(String.valueOf(_groceryItem.minimumQuantity));
        _textDesiredQuantity.setText(String.valueOf(_groceryItem.desiredQuantity));

        Toast.makeText(getContext(), R.string.message_item_edited_by_other_user, Toast.LENGTH_LONG).show();
    }

    @Override
    public void groceryItemRemovedByAnOtherUser(GroceryItem groceryItem) {
        Toast.makeText(getContext(), R.string.message_item_removed_by_other_user, Toast.LENGTH_LONG).show();
        _editItemDialogListener.onItemRemoved(_groceryItem);
        dismiss();
    }

    @Override
    public void onLoadGroceryItemCategoriesError(Throwable e) {
        Log.e(TAG, "An error occurred when loading the grocery item categories.", e);
        Toast.makeText(getContext(), getString(R.string.error_loading_grocery_list_categories, e.getMessage()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpdateGroceryItemError(Throwable e) {
        Log.e(TAG, "An error occurred when updating an item.", e);
        Toast.makeText(getContext(), getString(R.string.error_updating_item, e.getMessage()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editGroceryItemPresenter.onDestroy();
    }

    public interface EditItemDialogListener {
        void onItemEdited(GroceryItem item);

        void onItemRemoved(GroceryItem item);
    }
}
