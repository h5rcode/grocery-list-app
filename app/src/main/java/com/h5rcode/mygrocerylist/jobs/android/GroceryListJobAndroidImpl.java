package com.h5rcode.mygrocerylist.jobs.android;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.h5rcode.mygrocerylist.MyGroceryListApp;
import com.h5rcode.mygrocerylist.configuration.JobConfiguration;
import com.h5rcode.mygrocerylist.jobs.GroceryListJob;
import com.h5rcode.mygrocerylist.services.GroceryListService;

import javax.inject.Inject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GroceryListJobAndroidImpl extends JobService implements GroceryListJob.GroceryListJobListener {

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
    public boolean onStartJob(JobParameters params) {
        _jobParameters = params;
        return _groceryListJob.startJob();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @Override
    public void onJobFinished() {
        jobFinished(_jobParameters, false);
    }
}
