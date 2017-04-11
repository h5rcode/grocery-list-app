package com.h5rcode.mygrocerylist.apiclient;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.h5rcode.mygrocerylist.apiclient.helpers.JsonHelper;
import com.h5rcode.mygrocerylist.apiclient.models.ApiResponse;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;
import com.h5rcode.mygrocerylist.apiclient.models.ItemsQuantityRatioInfo;
import com.h5rcode.mygrocerylist.configuration.ClientConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GroceryListClientVolley implements GroceryListClient {

    private final RequestQueue _requestQueue;
    private ClientConfiguration _clientConfiguration;

    public GroceryListClientVolley(ClientConfiguration clientConfiguration, final RequestQueue requestQueue) {
        _clientConfiguration = clientConfiguration;
        _requestQueue = requestQueue;
    }

    @Override
    public GroceryItem addGroceryItem(GroceryItem groceryItem) {
        final URL itemsUrl = getUrl("items");

        final JSONObject jsonRequest = JsonHelper.serializeObject(groceryItem, GroceryItem.class);

        final RequestFuture<JSONObject> future = RequestFuture.newFuture();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                itemsUrl.toString(),
                jsonRequest,
                future,
                future
        );

        _requestQueue.add(jsonObjectRequest);

        GroceryItem returnedGroceryItem;
        try {
            JSONObject jsonObject = future.get();
            returnedGroceryItem = JsonHelper.parseJSONObject(jsonObject, GroceryItem.class);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return returnedGroceryItem;
    }

    @Override
    public void deleteGroceryItem(long id) {
        final URL url = getUrl("items/" + id);
        final RequestFuture<String> future = RequestFuture.newFuture();
        final StringRequest jsonObjectRequest = new StringRequest(
                Request.Method.DELETE,
                url.toString(),
                future,
                future);

        _requestQueue.add(jsonObjectRequest);

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GroceryItemCategory> getGroceryItemCategories() {
        final URL url = getUrl("categories");
        final RequestFuture<JSONArray> future = RequestFuture.newFuture();
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url.toString(),
                null,
                future,
                future);

        _requestQueue.add(jsonArrayRequest);

        JSONArray jsonArray;
        try {
            jsonArray = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return JsonHelper.parseJSONArray(jsonArray, GroceryItemCategory.class);
    }

    @Override
    public ItemsQuantityRatioInfo getItemsQuantityRatioInfo() {
        final URL url = getUrl("info/low-quantities-ratio");
        final RequestFuture<JSONObject> future = RequestFuture.newFuture();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url.toString(),
                null,
                future,
                future);

        _requestQueue.add(jsonObjectRequest);

        JSONObject jsonObject;
        try {
            jsonObject = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return JsonHelper.parseJSONObject(jsonObject, ItemsQuantityRatioInfo.class);
    }

    @Override
    public GroceryItem getItem(long id) {
        final URL url = getUrl("items/" + id);
        final RequestFuture<JSONObject> future = RequestFuture.newFuture();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url.toString(),
                null,
                future,
                future);

        _requestQueue.add(jsonObjectRequest);

        GroceryItem groceryItem;
        try {
            JSONObject jsonObject = future.get();
            groceryItem = JsonHelper.parseJSONObject(jsonObject, GroceryItem.class);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof VolleyError && ((VolleyError) cause).networkResponse.statusCode == 404) {
                groceryItem = null;
            } else {
                throw new RuntimeException(e);
            }
        }

        return groceryItem;
    }

    @Override
    public List<GroceryItem> getGroceryItems() {
        final URL categoriesUrl = getUrl("items");
        final RequestFuture<JSONArray> future = RequestFuture.newFuture();
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                categoriesUrl.toString(),
                null,
                future,
                future);

        _requestQueue.add(jsonArrayRequest);

        JSONArray jsonArray;
        try {
            jsonArray = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return JsonHelper.parseJSONArray(jsonArray, GroceryItem.class);
    }

    @Override
    public ApiResponse<GroceryItem> updateGroceryItem(GroceryItem groceryItem) {
        final URL categoriesUrl = getUrl("items");

        final JSONObject jsonRequest = JsonHelper.serializeObject(groceryItem, GroceryItem.class);

        final RequestFuture<JSONObject> future = RequestFuture.newFuture();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                categoriesUrl.toString(),
                jsonRequest,
                future,
                future
        );

        _requestQueue.add(jsonObjectRequest);

        GroceryItem returnedGroceryItem;
        int updateStatusCode;
        try {
            JSONObject jsonObject = future.get();
            returnedGroceryItem = JsonHelper.parseJSONObject(jsonObject, GroceryItem.class);
            updateStatusCode = 200;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof VolleyError) {
                updateStatusCode = ((VolleyError) cause).networkResponse.statusCode;
                returnedGroceryItem = null;
            } else {
                throw new RuntimeException(e);
            }
        }

        return new ApiResponse<>(updateStatusCode, returnedGroceryItem);
    }

    @NonNull
    private URL getUrl(String route) {
        final URL url;
        final URL groceryListUrl = _clientConfiguration.getGroceryListUrl();
        try {
            url = new URL(groceryListUrl, route);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return url;
    }
}
