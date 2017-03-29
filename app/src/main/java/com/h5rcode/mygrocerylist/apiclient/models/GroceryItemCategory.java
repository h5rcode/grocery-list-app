package com.h5rcode.mygrocerylist.apiclient.models;

import java.io.Serializable;

/**
 * Category of grocery items.
 */
public final class GroceryItemCategory implements Serializable {

    public String code;
    public String label;
    public int order;
}
