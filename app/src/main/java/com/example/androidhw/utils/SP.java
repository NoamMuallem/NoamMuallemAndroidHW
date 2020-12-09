package com.example.androidhw.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SP {
    private static SP instance;
    private SharedPreferences prefs;

    private SP(Context context) {
        prefs = context.getSharedPreferences("MY_SP", Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        //to allow init only for the first time - singleton design pattern
        //not because there is one instance but because we wont to be able to
        //instantiate only one and to use only that one instance
        if (instance == null) {
            //if we tak context a reference to context in some activity still exists
            //so to allow java garbage collector to delete unheeded activities
            //we take the application context from the context passed to here
            instance = new SP(context.getApplicationContext());
        }
    }

    public static SP getInstance() {
        return instance;
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String def) {
        return prefs.getString(key, def);
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int def) {
        return prefs.getInt(key, def);
    }

    public void removeKey(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
