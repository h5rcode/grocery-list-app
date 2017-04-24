package com.h5rcode.mygrocerylist.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.h5rcode.mygrocerylist.R;
import com.h5rcode.mygrocerylist.adapters.viewmodels.GroceryCategoryViewModel;
import com.h5rcode.mygrocerylist.adapters.viewmodels.GroceryElementViewModel;
import com.h5rcode.mygrocerylist.adapters.viewmodels.GroceryItemViewModel;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;
import com.h5rcode.mygrocerylist.services.models.GroceryList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroceryListAdapter extends BaseAdapter {
    private final List<GroceryItemCategory> _groceryItemCategories = new ArrayList<>();
    private final List<GroceryItem> _groceryItems = new ArrayList<>();

    private final List<GroceryElementViewModel> _viewModels = new ArrayList<>();
    private final LayoutInflater _layoutInflater;

    private final int _redColor;
    private final int _lightGreenColor;

    public GroceryListAdapter(Context context) {

        Resources resources = context.getResources();
        _redColor = ResourcesCompat.getColor(resources, R.color.red, null);
        _lightGreenColor = ResourcesCompat.getColor(resources, R.color.lightGreen, null);

        _layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void initialize(GroceryList groceryList) {
        _groceryItems.clear();
        _groceryItemCategories.clear();

        _groceryItemCategories.addAll(groceryList.getGroceryItemCategories());
        _groceryItems.addAll(groceryList.getGroceryItems());
        computeViewModelsAndNotifyDataSetChanged();
    }

    public void addGroceryItem(GroceryItem groceryItem) {
        _groceryItems.add(groceryItem);
        computeViewModelsAndNotifyDataSetChanged();
    }

    public void removeGroceryItem(long id) {
        GroceryItem groceryItem = getGroceryItem(id);
        _groceryItems.remove(groceryItem);
        computeViewModelsAndNotifyDataSetChanged();
    }

    public void updateGroceryItem(GroceryItem newGroceryItem) {
        long id = newGroceryItem.id;

        GroceryItem oldGroceryItem = getGroceryItem(id);

        int index = _groceryItems.indexOf(oldGroceryItem);
        _groceryItems.remove(index);
        _groceryItems.add(index, newGroceryItem);

        computeViewModelsAndNotifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return _viewModels.size();
    }

    @Override
    public Object getItem(int position) {
        return _viewModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroceryElementViewModel viewModel = (GroceryElementViewModel) getItem(position);

        View view;
        if (viewModel instanceof GroceryItemViewModel) {
            view = getItemView(parent, (GroceryItemViewModel) viewModel);
        } else if (viewModel instanceof GroceryCategoryViewModel) {
            view = getCategoryView(parent, (GroceryCategoryViewModel) viewModel);
        } else {
            throw new RuntimeException();
        }
        return view;
    }

    private void computeViewModels() {
        _viewModels.clear();

        Map<String, List<GroceryItem>> itemsByCategory = new HashMap<>();

        for (GroceryItem item : _groceryItems) {
            String categoryCode = item.categoryCode;

            List<GroceryItem> categoryItems = itemsByCategory.get(categoryCode);
            if (categoryItems == null) {
                categoryItems = new ArrayList<>();
                itemsByCategory.put(categoryCode, categoryItems);
            }

            categoryItems.add(item);
        }

        for (GroceryItemCategory groceryItemCategory : _groceryItemCategories) {
            String categoryCode = groceryItemCategory.code;

            List<GroceryItem> categoryItems = itemsByCategory.get(categoryCode);
            if (categoryItems != null) {
                if (categoryItems.isEmpty()) {
                    itemsByCategory.remove(categoryCode);
                } else {

                    GroceryElementViewModel groceryCategoryViewModel = new GroceryCategoryViewModel(groceryItemCategory);
                    _viewModels.add(groceryCategoryViewModel);

                    for (GroceryItem item : categoryItems) {
                        GroceryItemViewModel groceryItemViewModel = new GroceryItemViewModel(item);
                        _viewModels.add(groceryItemViewModel);
                    }
                }
            }
        }
    }

    private void computeViewModelsAndNotifyDataSetChanged() {
        computeViewModels();
        notifyDataSetChanged();
    }

    @Nullable
    private GroceryItem getGroceryItem(long id) {
        GroceryItem foundGroceryItem = null;
        for (GroceryItem groceryItem : _groceryItems) {
            if (groceryItem.id == id) {
                foundGroceryItem = groceryItem;
                break;
            }
        }

        return foundGroceryItem;
    }

    @NonNull
    private View getItemView(ViewGroup parent, GroceryItemViewModel viewModel) {
        GroceryItem item = viewModel.getGroceryItem();
        View convertView = _layoutInflater.inflate(R.layout.view_grocery_item, parent, false);

        TextView textItemLabel = (TextView) convertView.findViewById(R.id.item_label);
        TextView textItemState = (TextView) convertView.findViewById(R.id.item_state);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);

        int color;
        if (item.currentQuantity >= item.minimumQuantity) {
            color = _lightGreenColor;
        } else {
            color = _redColor;
        }

        progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        textItemLabel.setText(item.label);
        textItemState.setText(String.format(Locale.getDefault(), "%d / %d", item.currentQuantity, item.desiredQuantity));
        progressBar.setMax(item.desiredQuantity);
        progressBar.setProgress(item.currentQuantity);

        return convertView;
    }

    @NonNull
    private View getCategoryView(ViewGroup parent, GroceryCategoryViewModel viewModel) {
        View convertView = _layoutInflater.inflate(R.layout.view_grocery_item_category, parent, false);

        TextView textCategoryLabel = (TextView) convertView.findViewById(R.id.category_label);

        GroceryItemCategory groceryItemCategory = viewModel.getCategory();
        textCategoryLabel.setText(groceryItemCategory.label);

        return convertView;
    }
}
