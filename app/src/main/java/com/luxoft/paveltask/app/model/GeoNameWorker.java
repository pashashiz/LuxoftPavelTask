package com.luxoft.paveltask.app.model;

import com.j256.ormlite.dao.Dao;
import com.luxoft.paveltask.app.model.entity.GeoName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GeoNameWorker {

    private Dao<GeoName, String> daoGeoName;

    public GeoNameWorker() {
        try {
            daoGeoName = DBManager.getInstance().getHelper().getDaoGeoName();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<GeoName> getGeoNames() {
        List<GeoName> geoNames = null;
        try {
            geoNames = daoGeoName.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return geoNames;
    }

    public void addGeoNames(List<GeoName> geoNames) {
        for (GeoName geoName: geoNames)
            try {
                daoGeoName.create(geoName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    public List<GeoName> getFilteredNewGeoNames(List<GeoName> geoNames) {
        List<GeoName> newGeoNames = new ArrayList<GeoName>();
        List<GeoName> savedGeoNames = getGeoNames();
        if (geoNames != null)
            for (GeoName geoName: geoNames)
                if (!savedGeoNames.contains(geoName))
                    newGeoNames.add(geoName);
        return newGeoNames;
    }

}