package com.h5rcode.mygrocerylist.jobs;

public class GroceryListJobInfo {
    private final long _intervalMillis;
    private final int _jobId;

    public GroceryListJobInfo(long intervalMillis, int jobId) {
        _intervalMillis = intervalMillis;
        _jobId = jobId;
    }

    public long getIntervalMillis() {
        return _intervalMillis;
    }

    public int getJobId() {
        return _jobId;
    }
}
