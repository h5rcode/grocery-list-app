package com.h5rcode.mygrocerylist.fragments.grocerylist;

import com.h5rcode.mygrocerylist.services.GroceryListService;
import com.h5rcode.mygrocerylist.services.models.GroceryList;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GroceryListPresenterImpl implements GroceryListPresenter {
    private final GroceryListService _groceryListService;

    private GroceryListView _groceryListView;

    public GroceryListPresenterImpl(GroceryListService groceryListService) {
        _groceryListService = groceryListService;
    }

    @Override
    public void loadGroceryList() {
        Observable<GroceryList> groceryListObservable = Observable.fromCallable(new Callable<GroceryList>() {
            @Override
            public GroceryList call() throws Exception {
                return _groceryListService.getGroceryList();
            }
        });

        Observer<GroceryList> groceryListObserver = new Observer<GroceryList>() {
            @Override
            public void onSubscribe(Disposable d) {
                // Do nothing.
            }

            @Override
            public void onNext(GroceryList groceryList) {
                _groceryListView.onGroceryListLoaded(groceryList);
            }

            @Override
            public void onError(Throwable e) {
                _groceryListView.onLoadGroceryListError(e);
            }

            @Override
            public void onComplete() {
                // Do nothing.
            }
        };

        groceryListObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groceryListObserver);
    }

    @Override
    public void onDeleteItem(final long id) {
        Observable<Void> observableDeletion = Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> e) throws Exception {
                _groceryListService.deleteGroceryItem(id);
                e.onComplete();
            }
        });

        observableDeletion
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Do nothing.
                    }

                    @Override
                    public void onNext(Void value) {
                        // Do nothing.
                    }

                    @Override
                    public void onError(Throwable e) {
                        _groceryListView.onDeleteItemError(e);
                    }

                    @Override
                    public void onComplete() {
                        _groceryListView.onItemDeleted(id);
                    }
                });
    }

    @Override
    public void setGroceryListView(GroceryListView groceryListView) {
        _groceryListView = groceryListView;
    }
}
