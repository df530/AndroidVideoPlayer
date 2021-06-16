package com.example.avp.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import com.example.avp.model.Model;
import com.google.gson.Gson;

import java.io.Serializable;

import static com.example.avp.ui.Constants.APP_PREFERENCES;
import static com.example.avp.ui.Constants.APP_PREFERENCES_MODEL;
import static com.example.avp.ui.Constants.hasVisited;

public class JsonStateSaveLoader implements StateSaveLoader {
    private final SharedPreferences preferences;
    private final Gson gson;

    public JsonStateSaveLoader(ContextWrapper contextWrapper) {
        preferences = contextWrapper.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @Override
    public boolean isSavedStateExist() {
        return preferences.getBoolean(hasVisited, false);
    }

    @Override
    public void writeSerializable(String key, Serializable value) {
        SharedPreferences.Editor prefsEditor = preferences.edit();

        String jsonModel = gson.toJson(value);
        prefsEditor
                .putString(key, jsonModel)
                .putBoolean(hasVisited, true) // mark, that state was saved
                .apply();
    }

    @Override
    public Object readSerializable(String key, Class<?> classForRead) {
        String jsonModel = preferences.getString(key, "");
        return gson.fromJson(jsonModel, classForRead);
    }
}
