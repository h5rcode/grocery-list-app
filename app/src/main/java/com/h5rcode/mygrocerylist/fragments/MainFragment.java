package com.h5rcode.mygrocerylist.fragments;

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
import com.h5rcode.mygrocerylist.services.GroceryListService;
import com.h5rcode.mygrocerylist.services.models.GroceryList;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends Fragment {
    private static final int ITEM_DELETE = 1;

    @Inject
    GroceryListService groceryListService;

    private static String TAG = MainFragment.class.getName();
    private CompositeDisposable _disposables = new CompositeDisposable();

    private GroceryListAdapter _groceryListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        _groceryListAdapter = new GroceryListAdapter(getContext());

        ((MyGroceryListApp) getActivity().getApplication()).getServiceComponent().inject(this);

        Observable<GroceryList> elementsObservable = Observable.fromCallable(new Callable<GroceryList>() {
            @Override
            public GroceryList call() throws Exception {
                return groceryListService.getGroceryList();
            }
        });

        DisposableObserver<GroceryList> elementsObserver = new DisposableObserver<GroceryList>() {
            @Override
            public void onNext(GroceryList groceryList) {
                _groceryListAdapter.initialize(groceryList);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "An error occurred when loading the grocery list.", e);
                View view = getView();
                if (view != null) {
                    Snackbar.make(view, getString(R.string.error_loading_grocery_list), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onComplete() {
                // Do nothing.
            }
        };

        _disposables.add(elementsObserver);

        elementsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(elementsObserver);
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
            }
        });

        ListView listView = (ListView) view.findViewById(R.id.item_list);
        listView.setAdapter(_groceryListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroceryElementViewModel viewModel = (GroceryElementViewModel) parent.getItemAtPosition(position);

                if (viewModel instanceof GroceryItemViewModel) {
                    GroceryItem groceryItem = ((GroceryItemViewModel) viewModel).getGroceryItem();
                    Log.d(TAG, "Clicked grocery item " + groceryItem.label);
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

                Observable<Void> deletionObservable = Observable.create(new ObservableOnSubscribe<Void>() {
                    @Override
                    public void subscribe(ObservableEmitter<Void> e) throws Exception {
                        groceryListService.deleteGroceryItem(id);
                        e.onComplete();
                    }
                });

                DisposableObserver<Void> deletionObserver = new DisposableObserver<Void>() {
                    @Override
                    public void onNext(Void value) {
                        // Do nothing.
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error on delete.", e);
                        View view = getView();
                        if (view != null) {
                            Snackbar.make(view, R.string.error_deleting_item, Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onComplete() {
                        GroceryItemViewModel viewModel = (GroceryItemViewModel) _groceryListAdapter.getItem(info.position);
                        _groceryListAdapter.removeItem(viewModel.getGroceryItem());

                        final String message = getString(R.string.action_item_delete, groceryItem.label);
                        View view = getView();
                        if (view != null) {
                            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                        }
                    }
                };

                deletionObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(deletionObserver);

                _disposables.add(deletionObserver);

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
        _disposables.dispose();
    }
}
