package com.h5rcode.mygrocerylist.configuration;

public interface JobConfiguration {
    int getMinutesBetweenQuantityChecks();

    boolean isItemsQuantityRatioAboveMax();

    void setIsItemsQuantityRatioAboveMax(boolean isItemsQuantityRatioAboveMax);
}
