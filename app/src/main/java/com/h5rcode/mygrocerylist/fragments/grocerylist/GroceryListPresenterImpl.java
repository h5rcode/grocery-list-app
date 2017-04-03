package com.h5rcode.mygrocerylist.fragments.grocerylist;

import com.h5rcode.mygrocerylist.services.GroceryListService;
import com.h5rcode.mygrocerylist.services.models.GroceryList;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class GroceryListPresenterImpl implements GroceryListPresenter {
    private final GroceryListService _groceryListService;
    private final CompositeDisposable _disposables = new CompositeDisposable();

    private GroceryListView _groceryListView;

    public GroceryListPresenterImpl(GroceryListService groceryListService) {
        _groceryListService = groceryListService;
    }

    @Override
    public void onCreate() {
        Observable<GroceryList> elementsObservable = Observable.fromCallable(new Callable<GroceryList>() {
            @Override
            public GroceryList call() throws Exception {
                return _groceryListService.getGroceryList();
            }
        });

        DisposableObserver<GroceryList> elementsObserver = new DisposableObserver<GroceryList>() {
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

        _disposables.add(elementsObserver);

        elementsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(elementsObserver);
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

        DisposableObserver<Void> deletionObserver = new DisposableObserver<Void>() {
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
                _disposables.remove(this);
                dispose();
            }
        };

        _disposables.add(deletionObserver);

        observableDeletion
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deletionObserver);
    }

    @Override
    public void onDestroy() {
        _disposables.dispose();
    }

    @Override
    public void setGroceryListView(GroceryListView groceryListView) {
        _groceryListView = groceryListView;
    }
}
