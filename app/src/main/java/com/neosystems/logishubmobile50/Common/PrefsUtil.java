package com.neosystems.logishubmobile50.Common;

import android.content.Context;
import android.content.SharedPreferences;

import com.neosystems.logishubmobile50.MainActivity;

public class PrefsUtil {
    public synchronized static void setValue(String key, String value) {
        SharedPreferences prefs = MainActivity.context.getSharedPreferences("com.neosystems.logishubmobile50.MainActivity", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = prefs.edit();
        edt.putString(key, value);
        edt.commit();
    }

    public synchronized static void setValue(String key, int value) {
        SharedPreferences prefs = MainActivity.context.getSharedPreferences("com.neosystems.logishubmobile50.MainActivity", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = prefs.edit();
        edt.putInt(key, value);
        edt.commit();
    }

    public static String getValue(String key, String defaultValue) {
        SharedPreferences prefs = MainActivity.context.getSharedPreferences("com.neosystems.logishubmobile50.MainActivity", Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    public static int getValue(String key, int defaultValue) {
        SharedPreferences prefs = MainActivity.context.getSharedPreferences("com.neosystems.logishubmobile50.MainActivity", Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }

    public static boolean contains(String key) {
        SharedPreferences prefs = MainActivity.context.getSharedPreferences("com.neosystems.logishubmobile50.MainActivity", Context.MODE_PRIVATE);
        return prefs.contains(key);
    }
}
