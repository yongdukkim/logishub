package com.logishub.mobile.launcher.v5.Common;

import android.content.Context;
import android.content.SharedPreferences;

import com.logishub.mobile.launcher.v5.MainActivity;

public class PrefsUtil {
    public synchronized static void setValue(String key, String value) {
        SharedPreferences prefs = MainActivity.mContext.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = prefs.edit();
        edt.putString(key, value);
        edt.commit();
    }

    public synchronized static void setValue(String key, int value) {
        SharedPreferences prefs = MainActivity.mContext.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = prefs.edit();
        edt.putInt(key, value);
        edt.commit();
    }

    public static String getValue(String key, String defaultValue) {
        SharedPreferences prefs = MainActivity.mContext.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    public static int getValue(String key, int defaultValue) {
        SharedPreferences prefs = MainActivity.mContext.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }

    public static boolean contains(String key) {
        SharedPreferences prefs = MainActivity.mContext.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        return prefs.contains(key);
    }

    public static void removeAllPreferences(){
        SharedPreferences pref = MainActivity.mContext.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
