package com.h5rcode.mygrocerylist.jobs;

public interface GroceryListJobScheduler {
    void cancelJob(GroceryListJobInfo jobInfo);
    void scheduleJob(GroceryListJobInfo jobInfo);
}
