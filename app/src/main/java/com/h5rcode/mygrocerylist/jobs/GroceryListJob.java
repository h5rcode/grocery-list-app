package com.h5rcode.mygrocerylist.jobs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.h5rcode.mygrocerylist.MyGroceryListApp;
import com.h5rcode.mygrocerylist.R;
import com.h5rcode.mygrocerylist.activities.GroceryListActivity;
import com.h5rcode.mygrocerylist.apiclient.models.ItemsQuantityRatioInfo;
import com.h5rcode.mygrocerylist.configuration.JobConfiguration;
import com.h5rcode.mygrocerylist.services.GroceryListService;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class GroceryListJob extends JobService {

    private static final String TAG = GroceryListJob.class.getName();

    @Inject
    GroceryListService groceryListService;

    @Inject
    JobConfiguration jobConfiguration;

    private final CompositeDisposable _disposables = new CompositeDisposable();

    @Override
    public void onCreate() {
        super.onCreate();

        ((MyGroceryListApp) getApplication()).getServiceComponent().inject(this);
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.i(TAG, "Starting job.");

        Observable<ItemsQuantityRatioInfo> observable = Observable.fromCallable(new Callable<ItemsQuantityRatioInfo>() {
            @Override
            public ItemsQuantityRatioInfo call() throws Exception {
                return groceryListService.getItemsQuantityRatioInfo();
            }
        });

        DisposableObserver<ItemsQuantityRatioInfo> observer = new DisposableObserver<ItemsQuantityRatioInfo>() {
            @Override
            public void onNext(ItemsQuantityRatioInfo itemsQuantityRatioInfo) {
                checkRatioAndNotifyIfNecessary(itemsQuantityRatioInfo);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "An error has occurred in the grocery list job.", e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "Job finished.");
                jobFinished(params, false);
            }
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        _disposables.add(observer);

        return true;
    }

    private void checkRatioAndNotifyIfNecessary(ItemsQuantityRatioInfo itemsQuantityRatioInfo) {
        double ratio = itemsQuantityRatioInfo.ratio;
        double maxItemsInLowQuantityRatio = itemsQuantityRatioInfo.maxItemsInLowQuantityRatio;

        boolean isRatioAboveMax = ratio > maxItemsInLowQuantityRatio;
        boolean previousItemsQuantityRatioWasAboveMax = jobConfiguration.isItemsQuantityRatioAboveMax();
        if (isRatioAboveMax && !previousItemsQuantityRatioWasAboveMax) {
            Intent resultIntent = new Intent(this, GroceryListActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_notification_icon)
                            .setContentTitle(getString(R.string.activity_main))
                            .setContentText(getString(R.string.notification_message))
                            .setContentIntent(resultPendingIntent)
                            .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        }

        jobConfiguration.setIsItemsQuantityRatioAboveMax(isRatioAboveMax);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        _disposables.dispose();
        return false;
    }
}
