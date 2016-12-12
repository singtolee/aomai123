package com.aomai123.aomai123.API;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aomai123.aomai123.database.DBProducts;

/**
 * Created by Nang Juann on 1/9/2016.
 */
public class ApiKey extends Application {
    private static ApiKey sInstance;

    private static DBProducts pDatabase;

    public static ApiKey getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public synchronized static DBProducts getWritableDatabase() {
        if (pDatabase == null) {
            pDatabase = new DBProducts(getAppContext());
        }
        return pDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        pDatabase = new DBProducts(this);
    }
}
