package com.h5rcode.mygrocerylist.jobs.android;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.h5rcode.mygrocerylist.jobs.GroceryListJobInfo;
import com.h5rcode.mygrocerylist.jobs.GroceryListJobScheduler;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GroceryListJobSchedulerAndroidImpl implements GroceryListJobScheduler {

    private final Context _context;
    private final JobScheduler _jobScheduler;

    public GroceryListJobSchedulerAndroidImpl(Context context) {
        _jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        _context = context;
    }

    @Override
    public void cancelJob(GroceryListJobInfo jobInfo) {
        _jobScheduler.cancel(jobInfo.getJobId());
    }

    @Override
    public void scheduleJob(GroceryListJobInfo jobInfo) {
        int jobId = jobInfo.getJobId();

        ComponentName componentName = new ComponentName(_context.getPackageName(), GroceryListJobAndroidImpl.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(jobId, componentName)
                .setPeriodic(jobInfo.getIntervalMillis())
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        _jobScheduler.cancel(jobId);

        if (_jobScheduler.schedule(builder.build()) == JobScheduler.RESULT_FAILURE) {
            throw new RuntimeException("Could not schedule job.");
        }
    }
}
