package com.h5rcode.mygrocerylist.jobs.firebase;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.h5rcode.mygrocerylist.MyGroceryListApp;
import com.h5rcode.mygrocerylist.configuration.JobConfiguration;
import com.h5rcode.mygrocerylist.jobs.GroceryListJob;
import com.h5rcode.mygrocerylist.services.GroceryListService;

import javax.inject.Inject;

public class GroceryListJobFirebaseImpl extends JobService implements GroceryListJob.GroceryListJobListener {

    @Inject
    GroceryListService _groceryListService;

    @Inject
    JobConfiguration _jobConfiguration;

    private GroceryListJob _groceryListJob;
    private JobParameters _jobParameters;

    @Override
    public void onCreate() {
        super.onCreate();
        ((MyGroceryListApp) getApplication()).getServiceComponent().inject(this);

        _groceryListJob = new GroceryListJob(getBaseContext(), this, _groceryListService, _jobConfiguration);
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        _jobParameters = job;
        _groceryListJob.startJob();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    @Override
    public void onJobFinished() {
        jobFinished(_jobParameters, false);
    }
}
