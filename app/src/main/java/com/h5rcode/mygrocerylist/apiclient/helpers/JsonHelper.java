package com.h5rcode.mygrocerylist.apiclient.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class JsonHelper {
    public static <T> List<T> parseJSONArray(final JSONArray jsonArray, Class<T> type) {
        final int arrayLength = jsonArray.length();
        final List<T> parsedObjects = new ArrayList<>();

        for (int i = 0; i < arrayLength; i++) {
            final JSONObject jsonObject;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            parsedObjects.add(parseJSONObject(jsonObject, type));
        }
        return parsedObjects;
    }

    public static <T> T parseJSONObject(final JSONObject jsonObject, Class<T> type) {
        T parsedObject;
        try {
            parsedObject = type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (final Field field : type.getFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers)) {
                continue;
            }

            final String fieldName = field.getName();

            Object fieldValue;
            try {
                if (jsonObject.has(fieldName)) {
                    fieldValue = jsonObject.get(fieldName);
                    field.set(parsedObject, fieldValue);
                }
            } catch (JSONException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return parsedObject;
    }

    public static <T> JSONArray serializeArray(List<T> array, Class<T> type) {
        final JSONArray jsonArray = new JSONArray();
        for (final T object : array) {
            final JSONObject jsonObject = serializeObject(object, type);
            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    public static <T> JSONObject serializeObject(T object, Class<T> type) {
        final JSONObject jsonObject = new JSONObject();
        for (final Field field : type.getFields()) {
            try {
                final String fieldName = field.getName();
                final Object fieldValue = field.get(object);
                jsonObject.put(fieldName, fieldValue);
            } catch (IllegalAccessException | JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return jsonObject;
    }
}
