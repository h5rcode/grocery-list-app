package com.h5rcode.mygrocerylist.fragments.editgroceryitem;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;
import com.h5rcode.mygrocerylist.services.GroceryListService;
import com.h5rcode.mygrocerylist.services.models.GroceryItemUpdateResult;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class EditGroceryItemPresenterImpl implements EditGroceryItemPresenter {

    private final GroceryListService _groceryListService;
    private EditGroceryItemView _editGroceryItemView;

    public EditGroceryItemPresenterImpl(GroceryListService groceryListService) {

        _groceryListService = groceryListService;
    }

    @Override
    public void setEditGroceryItemView(EditGroceryItemView editGroceryItemView) {
        _editGroceryItemView = editGroceryItemView;
    }

    @Override
    public void onCreateDialog() {
        Observable<List<GroceryItemCategory>> categoriesObservable = Observable.fromCallable(new Callable<List<GroceryItemCategory>>() {
            @Override
            public List<GroceryItemCategory> call() throws Exception {
                return _groceryListService.getGroceryItemCategories();
            }
        });

        DisposableObserver<List<GroceryItemCategory>> categoriesObserver = new DisposableObserver<List<GroceryItemCategory>>() {
            @Override
            public void onNext(List<GroceryItemCategory> groceryItemCategories) {
                _editGroceryItemView.onGroceryItemCategoriesLoaded(groceryItemCategories);
            }

            @Override
            public void onError(Throwable e) {
                _editGroceryItemView.onLoadGroceryItemCategoriesError(e);
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
    }

    @Override
    public void onUpdateGroceryItem(final GroceryItem groceryItem) {
        Observable<GroceryItemUpdateResult> observableUpdate = Observable.fromCallable(new Callable<GroceryItemUpdateResult>() {
            @Override
            public GroceryItemUpdateResult call() throws Exception {
                return _groceryListService.updateGroceryItem(groceryItem);
            }
        });

        Observer<GroceryItemUpdateResult> updateObserver = new Observer<GroceryItemUpdateResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                // Do nothing.
            }

            @Override
            public void onNext(GroceryItemUpdateResult updateResult) {
                GroceryItem groceryItem = updateResult.getGroceryItem();

                int responseStatusCode = updateResult.getResponseStatusCode();
                if (responseStatusCode == 200) {
                    _editGroceryItemView.groceryItemUpdateSucceeded(groceryItem);
                } else if (responseStatusCode == 412) {
                    _editGroceryItemView.groceryItemUpdatedByAnOtherUser(groceryItem);
                } else if (responseStatusCode == 404) {
                    _editGroceryItemView.groceryItemRemovedByAnOtherUser(groceryItem);
                }
            }

            @Override
            public void onError(Throwable e) {
                _editGroceryItemView.onUpdateGroceryItemError(e);
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
    }
}
