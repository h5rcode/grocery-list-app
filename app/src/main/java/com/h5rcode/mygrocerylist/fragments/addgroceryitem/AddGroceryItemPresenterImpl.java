package com.h5rcode.mygrocerylist.fragments.addgroceryitem;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;
import com.h5rcode.mygrocerylist.services.GroceryListService;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AddGroceryItemPresenterImpl implements AddGroceryItemPresenter {

    private final GroceryListService _groceryListService;
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private AddGroceryItemView _addGroceryItemView;

    public AddGroceryItemPresenterImpl(GroceryListService groceryListService) {
        _groceryListService = groceryListService;
    }

    public void setAddGroceryItemView(AddGroceryItemView addGroceryItemView) {
        _addGroceryItemView = addGroceryItemView;
    }

    @Override
    public void saveGroceryItem(final GroceryItem groceryItem) {

        Observable<GroceryItem> observableSave = Observable.fromCallable(new Callable<GroceryItem>() {
            @Override
            public GroceryItem call() throws Exception {
                return _groceryListService.addGroceryItem(groceryItem);
            }
        });

        DisposableObserver<GroceryItem> saveObserver = new DisposableObserver<GroceryItem>() {
            @Override
            public void onNext(GroceryItem value) {
                _addGroceryItemView.onGroceryItemSaved(value);
            }

            @Override
            public void onError(Throwable e) {
                _addGroceryItemView.onAddItemError(e);
            }

            @Override
            public void onComplete() {
                // Do nothing.
            }
        };

        observableSave
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(saveObserver);

        _disposables.add(saveObserver);
    }

    @Override
    public void onCreateDialog() {
        Observable<List<GroceryItemCategory>> categoriesObservable = Observable.fromCallable(new Callable<List<GroceryItemCategory>>() {
            @Override
            public List<GroceryItemCategory> call() throws Exception {
                return _groceryListService.getCategoryItemCategories();
            }
        });

        DisposableObserver<List<GroceryItemCategory>> categoriesObserver = new DisposableObserver<List<GroceryItemCategory>>() {
            @Override
            public void onNext(List<GroceryItemCategory> groceryItemCategories) {
                _addGroceryItemView.onGroceryItemCategoriesLoaded(groceryItemCategories);
            }

            @Override
            public void onError(Throwable e) {
                _addGroceryItemView.onLoadGroceryItemCategoriesError(e);
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

        _disposables.add(categoriesObserver);
    }

    @Override
    public void onDestroy() {
        _disposables.dispose();
    }
}
