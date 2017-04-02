package com.h5rcode.mygrocerylist.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
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
import com.h5rcode.mygrocerylist.services.GroceryListService;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class EditItemDialogFragment extends DialogFragment {

    private static final String ARG_ITEM = "ARG_ITEM";
    private static final String TAG = EditItemDialogListener.class.getName();

    @Inject
    GroceryListService groceryListService;

    private GroceryItem _groceryItem;
    private EditItemDialogListener _editItemDialogListener;

    public static EditItemDialogFragment newInstance(final GroceryItem item) {
        final Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);

        final EditItemDialogFragment fragment = new EditItemDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyGroceryListApp) getActivity().getApplication()).getServiceComponent().inject(this);

        final Bundle args = getArguments();
        _groceryItem = (GroceryItem) args.getSerializable(ARG_ITEM);
        _editItemDialogListener = (EditItemDialogListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity activity = getActivity();
        final Dialog dialog = new Dialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_edit_item);
        dialog.setTitle(_groceryItem.label);

        final Spinner spinnerCategory = (Spinner) dialog.findViewById(R.id.spinner_edit_category);
        final TextView textMinimumQuantity = (TextView) dialog.findViewById(R.id.text_minimum_quantity);
        final TextView textCurrentQuantity = (TextView) dialog.findViewById(R.id.text_current_quantity);
        final TextView textDesiredQuantity = (TextView) dialog.findViewById(R.id.text_desired_quantity);

        final Button buttonDecrementCurrentQuantity = (Button) dialog.findViewById(R.id.button_decrement_current_quantity);
        final Button buttonIncrementCurrentQuantity = (Button) dialog.findViewById(R.id.button_increment_current_quantity);
        final Button buttonDecrementMinimumQuantity = (Button) dialog.findViewById(R.id.button_decrement_minimum_quantity);
        final Button buttonIncrementMinimumQuantity = (Button) dialog.findViewById(R.id.button_increment_minimum_quantity);
        final Button buttonDecrementDesiredQuantity = (Button) dialog.findViewById(R.id.button_decrement_desired_quantity);
        final Button buttonIncrementDesiredQuantity = (Button) dialog.findViewById(R.id.button_increment_desired_quantity);
        final Button okButton = (Button) dialog.findViewById(R.id.button_ok);

        textCurrentQuantity.setText(String.valueOf(_groceryItem.currentQuantity));
        textMinimumQuantity.setText(String.valueOf(_groceryItem.minimumQuantity));
        textDesiredQuantity.setText(String.valueOf(_groceryItem.desiredQuantity));

        buttonDecrementCurrentQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_groceryItem.currentQuantity > 0) {
                    _groceryItem.currentQuantity--;
                    textCurrentQuantity.setText(String.valueOf(_groceryItem.currentQuantity));
                }
            }
        });
        buttonIncrementCurrentQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _groceryItem.currentQuantity++;
                textCurrentQuantity.setText(String.valueOf(_groceryItem.currentQuantity));
            }
        });

        buttonDecrementMinimumQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_groceryItem.minimumQuantity > 0) {
                    _groceryItem.minimumQuantity--;
                    textMinimumQuantity.setText(String.valueOf(_groceryItem.minimumQuantity));
                }
            }
        });
        buttonIncrementMinimumQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_groceryItem.minimumQuantity < _groceryItem.desiredQuantity) {
                    _groceryItem.minimumQuantity++;
                    textMinimumQuantity.setText(String.valueOf(_groceryItem.minimumQuantity));
                }
            }
        });

        buttonDecrementDesiredQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_groceryItem.desiredQuantity > 0 && _groceryItem.desiredQuantity > _groceryItem.minimumQuantity) {
                    _groceryItem.desiredQuantity--;
                    textDesiredQuantity.setText(String.valueOf(_groceryItem.desiredQuantity));
                }
            }
        });
        buttonIncrementDesiredQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _groceryItem.desiredQuantity++;
                textDesiredQuantity.setText(String.valueOf(_groceryItem.desiredQuantity));
            }
        });

        Observable<List<GroceryItemCategory>> categoriesObservable = Observable.fromCallable(new Callable<List<GroceryItemCategory>>() {
            @Override
            public List<GroceryItemCategory> call() throws Exception {
                return groceryListService.getCategoryItemCategories();
            }
        });

        DisposableObserver<List<GroceryItemCategory>> categoriesObserver = new DisposableObserver<List<GroceryItemCategory>>() {
            @Override
            public void onNext(List<GroceryItemCategory> value) {
                final CategoryListAdapter categoryListAdapter = new CategoryListAdapter(getContext(), value);
                spinnerCategory.setAdapter(categoryListAdapter);
                for (int i = 0; i < value.size(); i++) {
                    final GroceryItemCategory category = value.get(i);
                    if (category.code.equals(_groceryItem.categoryCode)) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "An error occurred when loading categories.", e);
                View view = getView();
                if (view != null) {
                    Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onComplete() {
                // Do nothing.
            }
        };

        categoriesObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoriesObserver);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final GroceryItemCategory category = (GroceryItemCategory) spinnerCategory.getItemAtPosition(position);
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

                Observable<GroceryItemUpdateResult> observableUpdate = Observable.fromCallable(new Callable<GroceryItemUpdateResult>() {
                    @Override
                    public GroceryItemUpdateResult call() throws Exception {
                        int updateStatusCode = groceryListService.updateGroceryItem(_groceryItem);

                        if (updateStatusCode == 200) {
                            return GroceryItemUpdateResult.createSuccessfulUpdateResult(_groceryItem);
                        } else {
                            GroceryItem groceryItem = groceryListService.getGroceryItem(_groceryItem.id);
                            return GroceryItemUpdateResult.createFailedUpdateResult(groceryItem);
                        }
                    }
                });

                DisposableObserver<GroceryItemUpdateResult> updateObserver = new DisposableObserver<GroceryItemUpdateResult>() {
                    @Override
                    public void onNext(GroceryItemUpdateResult updateResult) {
                        GroceryItem groceryItem = updateResult.getGroceryItem();

                        if (updateResult.updateSucceeded()) {
                            _groceryItem.version = groceryItem.version;
                            _editItemDialogListener.onItemEdited(_groceryItem);
                            dismiss();
                        } else {
                            _groceryItem.minimumQuantity = groceryItem.minimumQuantity;
                            _groceryItem.currentQuantity = groceryItem.currentQuantity;
                            _groceryItem.desiredQuantity = groceryItem.desiredQuantity;
                            _groceryItem.version = groceryItem.version;

                            textCurrentQuantity.setText(String.valueOf(_groceryItem.currentQuantity));
                            textMinimumQuantity.setText(String.valueOf(_groceryItem.minimumQuantity));
                            textDesiredQuantity.setText(String.valueOf(_groceryItem.desiredQuantity));

                            Toast.makeText(getContext(), R.string.message_item_edited_by_other_user, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        // Do nothing.
                    }
                };

                observableUpdate
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(updateObserver);

                /*
                itemService.updateItem(_groceryItem, new OnOperationCompletedListener<GroceryItem>() {
                    @Override
                    public void operationCompleted(GroceryItem result) {
                        _groceryItem.version = result.version;
                        _editItemDialogListener.onItemEdited(_groceryItem);
                        dismiss();
                    }

                    @Override
                    public void operationFailed(Exception e) {
                        if (e instanceof VolleyError) {
                            final VolleyError volleyError = (VolleyError) e;
                            if (volleyError.networkResponse.statusCode == 412) {
                                itemService.getItem(_groceryItem.id, new OnOperationCompletedListener<GroceryItem>() {
                                    @Override
                                    public void operationCompleted(GroceryItem result) {
                                        _groceryItem.minimumQuantity = result.minimumQuantity;
                                        _groceryItem.currentQuantity = result.currentQuantity;
                                        _groceryItem.desiredQuantity = result.desiredQuantity;
                                        _groceryItem.version = result.version;

                                        textCurrentQuantity.setText(String.valueOf(_groceryItem.currentQuantity));
                                        textMinimumQuantity.setText(String.valueOf(_groceryItem.minimumQuantity));
                                        textDesiredQuantity.setText(String.valueOf(_groceryItem.desiredQuantity));

                                        Toast.makeText(getContext(), R.string.message_item_edited_by_other_user, Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void operationFailed(Exception e) {
                                        if (e instanceof VolleyError) {
                                            final VolleyError volleyError1 = (VolleyError) e;
                                            if (volleyError1.networkResponse.statusCode == 404) {
                                                Toast.makeText(getContext(), R.string.message_item_removed_by_other_user, Toast.LENGTH_LONG).show();
                                                _editItemDialogListener.onItemRemoved(_groceryItem);
                                                dismiss();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
                */
            }
        });

        return dialog;
    }

    public interface EditItemDialogListener {
        void onItemEdited(final GroceryItem item);

        void onItemRemoved(final GroceryItem item);
    }
}
