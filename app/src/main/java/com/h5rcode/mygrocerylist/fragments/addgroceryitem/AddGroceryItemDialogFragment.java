package com.h5rcode.mygrocerylist.fragments.addgroceryitem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.h5rcode.mygrocerylist.MyGroceryListApp;
import com.h5rcode.mygrocerylist.R;
import com.h5rcode.mygrocerylist.adapters.CategoryListAdapter;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;

import java.util.List;

import javax.inject.Inject;

public class AddGroceryItemDialogFragment extends DialogFragment implements AddGroceryItemView {
    private static final String TAG = AddGroceryItemDialogFragment.class.getName();
    private AddItemDialogListener _addItemDialogListener;

    private Spinner _spinnerCategory;

    @Inject
    AddGroceryItemPresenter addGroceryItemPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyGroceryListApp) getActivity().getApplication()).getServiceComponent().inject(this);

        addGroceryItemPresenter.setAddGroceryItemView(this);

        _addItemDialogListener = (AddItemDialogListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_item, null);

        final EditText textLabel = (EditText) view.findViewById(R.id.edit_text_label);
        _spinnerCategory = (Spinner) view.findViewById(R.id.spinner_category);

        final EditText textMinimumQuantity = (EditText) view.findViewById(R.id.edit_text_minimum_quantity);
        final EditText textDesiredQuantity = (EditText) view.findViewById(R.id.edit_text_desired_quantity);
        final EditText textCurrentQuantity = (EditText) view.findViewById(R.id.edit_text_current_quantity);

        final Button saveButton = (Button) view.findViewById(R.id.button_save_item);
        Button cancelButton = (Button) view.findViewById(R.id.button_cancel_add_item);

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveButton.setEnabled(false);

                Editable editableLabel = textLabel.getText();
                GroceryItemCategory category = (GroceryItemCategory) _spinnerCategory.getSelectedItem();
                Editable editableMinimumQuantity = textMinimumQuantity.getText();
                Editable editableDesiredQuantity = textDesiredQuantity.getText();
                Editable editableCurrentQuantity = textCurrentQuantity.getText();

                boolean isInputValid = true;
                if (editableLabel == null || editableLabel.toString().equals("")) {
                    isInputValid = false;
                    textLabel.setError(getString(R.string.info_mandatory_field));
                }

                int minimumQuantity = 0;
                if (editableMinimumQuantity == null || editableMinimumQuantity.toString().equals("")) {
                    isInputValid = false;
                    textMinimumQuantity.setError(getString(R.string.info_mandatory_field));
                } else {
                    minimumQuantity = Integer.parseInt(textMinimumQuantity.getText().toString());
                }

                int desiredQuantity = 0;
                if (editableDesiredQuantity == null || editableDesiredQuantity.toString().equals("")) {
                    isInputValid = false;
                    textDesiredQuantity.setError(getString(R.string.info_mandatory_field));
                } else {
                    desiredQuantity = Integer.parseInt(textDesiredQuantity.getText().toString());
                }

                if (desiredQuantity < minimumQuantity) {
                    isInputValid = false;
                    textDesiredQuantity.setError(getString(R.string.info_desired_quantity_must_be_greater_than_minimum_quantity));
                }

                if (editableCurrentQuantity == null || editableCurrentQuantity.toString().equals("")) {
                    isInputValid = false;
                    textCurrentQuantity.setError(getString(R.string.info_mandatory_field));
                }

                if (isInputValid) {
                    String label = editableLabel.toString().trim();
                    int currentQuantity = Integer.parseInt(textCurrentQuantity.getText().toString());

                    GroceryItem item = new GroceryItem();
                    item.label = label;
                    item.minimumQuantity = minimumQuantity;
                    item.desiredQuantity = desiredQuantity;
                    item.currentQuantity = currentQuantity;
                    item.categoryCode = category.code;

                    addGroceryItemPresenter.saveGroceryItem(item);
                } else {
                    saveButton.setEnabled(true);
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        addGroceryItemPresenter.onCreateDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.title_dialog_add_item);
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        addGroceryItemPresenter.onDestroy();
    }

    @Override
    public void onGroceryItemCategoriesLoaded(List<GroceryItemCategory> groceryItemCategories) {
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(getContext(), groceryItemCategories);
        _spinnerCategory.setAdapter(categoryListAdapter);
    }

    @Override
    public void onLoadGroceryItemCategoriesError(Throwable e) {
        Log.e(TAG, "An error occurred when loading the grocery item categories.", e);
        Toast.makeText(getContext(), getString(R.string.error_loading_grocery_list_categories, e.getMessage()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGroceryItemSaved(GroceryItem groceryItem) {
        _addItemDialogListener.onSave(groceryItem);
        dismiss();
    }

    @Override
    public void onAddItemError(Throwable e) {
        Log.e(TAG, "An error occurred when saving the grocery item.", e);
        Toast.makeText(getContext(), getString(R.string.error_adding_item, e.getMessage()), Toast.LENGTH_LONG).show();
    }

    public interface AddItemDialogListener {
        void onSave(GroceryItem item);
    }
}
