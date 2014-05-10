package com.luxoft.paveltask.app.model;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import android.content.Context;

public class DBManager {

    private static volatile DBManager instance;

    private DBHelper helper;

    private DBManager() {
    }

    synchronized public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (helper == null)
            helper = OpenHelperManager.getHelper(context, DBHelper.class);
    }

    public void release() {
        if (helper != null)
            OpenHelperManager.releaseHelper();
    }

    public DBHelper getHelper() {
        return helper;
    }
}