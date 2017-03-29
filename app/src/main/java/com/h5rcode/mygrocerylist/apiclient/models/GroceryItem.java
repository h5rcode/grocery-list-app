package com.h5rcode.mygrocerylist.apiclient.models;

import java.io.Serializable;

/**
 * A grocery item.
 */
public final class GroceryItem implements Serializable {

    public long id;
    public String label;
    public int currentQuantity;
    public int minimumQuantity;
    public int version;
    public int desiredQuantity;
    public String categoryCode;
}
