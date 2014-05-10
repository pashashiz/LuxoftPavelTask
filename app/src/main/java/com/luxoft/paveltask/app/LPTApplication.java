package com.luxoft.paveltask.app;

import android.app.Application;

import com.luxoft.paveltask.app.model.DBHelper;
import com.luxoft.paveltask.app.model.DBManager;

public class LPTApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DBManager.getInstance().init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DBManager.getInstance().release();
    }

}
