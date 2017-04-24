package com.h5rcode.mygrocerylist.jobs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.h5rcode.mygrocerylist.R;
import com.h5rcode.mygrocerylist.activities.GroceryListActivity;
import com.h5rcode.mygrocerylist.apiclient.models.ItemsQuantityRatioInfo;
import com.h5rcode.mygrocerylist.configuration.JobConfiguration;
import com.h5rcode.mygrocerylist.services.GroceryListService;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.NOTIFICATION_SERVICE;

public class GroceryListJob {
    private static final String TAG = GroceryListJob.class.getName();

    private final Context _context;
    private final GroceryListJobListener _groceryListJobListener;
    private final GroceryListService _groceryListService;
    private final JobConfiguration _jobConfiguration;

    public GroceryListJob(Context context, GroceryListJobListener groceryListJobListener, GroceryListService groceryListService, JobConfiguration jobConfiguration) {
        _context = context;
        _groceryListJobListener = groceryListJobListener;
        _groceryListService = groceryListService;
        _jobConfiguration = jobConfiguration;
    }

    public boolean startJob() {
        Log.i(TAG, "Starting job.");

        Observable<ItemsQuantityRatioInfo> observable = Observable.fromCallable(new Callable<ItemsQuantityRatioInfo>() {
            @Override
            public ItemsQuantityRatioInfo call() throws Exception {
                return _groceryListService.getItemsQuantityRatioInfo();
            }
        });

        Observer<ItemsQuantityRatioInfo> observer = new Observer<ItemsQuantityRatioInfo>() {

            @Override
            public void onSubscribe(Disposable d) {
                // Do nothing.
            }

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

                _groceryListJobListener.onJobFinished();
            }
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        return true;
    }

    private void checkRatioAndNotifyIfNecessary(ItemsQuantityRatioInfo itemsQuantityRatioInfo) {
        double ratio = itemsQuantityRatioInfo.ratio;
        double maxItemsInLowQuantityRatio = itemsQuantityRatioInfo.maxItemsInLowQuantityRatio;

        boolean isRatioAboveMax = ratio > maxItemsInLowQuantityRatio;
        boolean previousItemsQuantityRatioWasAboveMax = _jobConfiguration.isItemsQuantityRatioAboveMax();
        if (isRatioAboveMax && !previousItemsQuantityRatioWasAboveMax) {
            onRatioIsAboveMax();
        }

        _jobConfiguration.setIsItemsQuantityRatioAboveMax(isRatioAboveMax);
    }

    private void onRatioIsAboveMax() {
        Intent resultIntent = new Intent(_context, GroceryListActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        _context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(_context)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle(_context.getString(R.string.activity_main))
                        .setContentText(_context.getString(R.string.notification_message))
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) _context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public interface GroceryListJobListener {

        void onJobFinished();
    }
}
