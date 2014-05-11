package com.luxoft.paveltask.app.model;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import android.content.Context;

/**
 * Database manager (ORM Lite)
 * @author Pavel Pohrebniy
 */
public class DBManager {

    private static volatile DBManager instance;

    private DBHelper helper;

    private DBManager() {}

    /**
     * Get database manager instance (singleton)
     * @return {@link DBManager}
     */
    synchronized public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    /**
     * Initialization of database manager
     * @param context Application context
     */
    public void init(Context context) {
        if (helper == null)
            helper = OpenHelperManager.getHelper(context, DBHelper.class);
    }

    /**
     * Release of resources
     */
    public void release() {
        if (helper != null)
            OpenHelperManager.releaseHelper();
    }

    /**
     * Get helper for database working
     * @return {@link DBHelper}
     */
    public DBHelper getHelper() {
        return helper;
    }
}