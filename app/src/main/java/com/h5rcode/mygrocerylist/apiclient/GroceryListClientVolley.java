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
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItem;
import com.h5rcode.mygrocerylist.apiclient.models.GroceryItemCategory;
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
    public void addItem(GroceryItem groceryItem) {

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

        JSONObject jsonObject;
        try {
            jsonObject = future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return JsonHelper.parseJSONObject(jsonObject, GroceryItem.class);
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
    public int updateGroceryItem(GroceryItem groceryItem) {
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

        int updateStatusCode;
        try {
            future.get();
            updateStatusCode = 200;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof VolleyError) {
                updateStatusCode = ((VolleyError) cause).networkResponse.statusCode;
            } else {
                throw new RuntimeException(e);
            }
        }

        return updateStatusCode;
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
