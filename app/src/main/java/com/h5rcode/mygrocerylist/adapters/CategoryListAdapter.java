package com.h5rcode.mygrocerylist.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;

import java.util.List;

/**
 * GroceryItemCategory list adapter.
 */
public class CategoryListAdapter extends ArrayAdapter<GroceryItemCategory> {
    private final LayoutInflater mLayoutInflater;

    public CategoryListAdapter(Context context, List<GroceryItemCategory> objects) {
        super(context, 0, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        final GroceryItemCategory groceryItemCategory = getItem(position);

        final TextView textViewLabel = (TextView) convertView.findViewById(android.R.id.text1);
        textViewLabel.setText(groceryItemCategory.label);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
