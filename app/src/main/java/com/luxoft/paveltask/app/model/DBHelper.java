package com.luxoft.paveltask.app.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.luxoft.paveltask.app.model.entity.GeoName;

import java.sql.SQLException;

/**
 * Data base helper (ORM Lite)
 * @author Pavel Pohrebniy
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    public static final String TAG = "DBHelper";

    private static final String DATABASE_NAME = "luxoftPavelTask.db";
    private static final int DATABASE_VERSION = 4;

    private Dao<GeoName, String> daoGeoName = null;

    /**
     * Constructor
     * @param context Application context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, GeoName.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can not create table");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, GeoName.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Can not upgrade table");
        }
    }

    @Override
    public void close() {
        super.close();
        daoGeoName = null;
    }

    /**
     * Get geo name DAO
     * @return Geo name DAO
     * @throws SQLException
     */
    public Dao<GeoName, String> getDaoGeoName() throws SQLException {
        if (daoGeoName == null)
            daoGeoName = getDao(GeoName.class);
        return daoGeoName;
    }

}
