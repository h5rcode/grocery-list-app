package com.h5rcode.mygrocerylist.jobs.firebase;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.h5rcode.mygrocerylist.jobs.GroceryListJobInfo;
import com.h5rcode.mygrocerylist.jobs.GroceryListJobScheduler;

import java.util.concurrent.TimeUnit;

public class GroceryListJobSchedulerFirebaseImpl implements GroceryListJobScheduler {

    private final FirebaseJobDispatcher _dispatcher;

    public GroceryListJobSchedulerFirebaseImpl(Context context) {
        _dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
    }

    @Override
    public void cancelJob(GroceryListJobInfo jobInfo) {
        _dispatcher.cancel(String.valueOf(jobInfo.getJobId()));
    }

    @Override
    public void scheduleJob(GroceryListJobInfo jobInfo) {
        int periodicity = (int) TimeUnit.MILLISECONDS.toSeconds(jobInfo.getIntervalMillis());
        int toleranceInterval = (int) TimeUnit.SECONDS.toSeconds(15);

        String jobTag = String.valueOf(jobInfo.getJobId());
        _dispatcher.cancel(jobTag);

        Job myJob = _dispatcher.newJobBuilder()
                .setService(GroceryListJobFirebaseImpl.class)
                .setTag(jobTag)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(true)
                .build();

        _dispatcher.mustSchedule(myJob);
    }
}
