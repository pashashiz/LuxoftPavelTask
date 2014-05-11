package com.luxoft.paveltask.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.luxoft.paveltask.app.model.GeoNameWorker;
import com.luxoft.paveltask.app.model.entity.GeoName;
import com.luxoft.paveltask.app.service.GeoNamesService;
import com.luxoft.paveltask.app.service.ServiceException;
import com.luxoft.paveltask.app.view.adapter.GeoNameArrayAdapter;

import java.util.List;

/**
 * Geo names activity
 * @author Pavel Pohrebniy
 */
public class GeoNamesActivity extends ActionBarActivity {

    public static final String TAG = "GeoNamesActivity";
    public static final int ID_MENU_UPDATE = 1;

    /**
     * On update geo names interface
     */
    public interface OnUpdateGeoNames {
        public void onUpdateGeoNames(String errorMessage);
    }

    /**
     * Update geo names task
     */
    class UpdateGeoNamesTask extends AsyncTask<OnUpdateGeoNames, Void, String> {

        OnUpdateGeoNames updateCallback;

        @Override
        protected String doInBackground(OnUpdateGeoNames... args) {
            updateCallback = args[0];
            String errorMessage = null;
            GeoNamesService service = GeoNamesService.getInstance();
            List<GeoName> geoNames;
            try {
                geoNames = service.getGeoNameCities(service.demoCitiesParams);
                Log.i(TAG, "Geo names, received from the server: " + geoNames);
                if (geoNames != null && geoNames.size() > 0) {
                    GeoNameWorker worker = new GeoNameWorker();
                    List<GeoName> newGeoNames = worker.getFilteredNewGeoNames(geoNames);
                    Log.i(TAG, "Geo names, added to data base: " + newGeoNames);
                    if (newGeoNames != null)
                        worker.addGeoNames(newGeoNames);
                }
                else if (service.getStatus() != null)
                    errorMessage = service.getStatus().getMessage();
            } catch (ServiceException e) {
                String error = e.getMessage();
                if (error.equals(GeoNamesService.ERR_CONNECTION))
                    errorMessage = e.getMessage();
                else
                    e.printStackTrace();
            }
            return errorMessage;
        }

        @Override
        protected void onPostExecute(String errorMessage) {
            if (updateCallback != null)
                updateCallback.onUpdateGeoNames(errorMessage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_names);
        ListView list = (ListView) findViewById(R.id.agn_list);
        System.out.println("LIST: " + list);
        List<GeoName> geoNames = new GeoNameWorker().getGeoNames();
        Log.i(TAG, "Geo names, on create: " + geoNames);
        arrayAdapter = new GeoNameArrayAdapter(this, geoNames);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GeoName geoName = (GeoName) adapterView.getItemAtPosition(i);
                if (geoName != null && geoName.getWikipedia() != null) {
                    Uri address = Uri.parse("http://" + (geoName.getWikipedia()));
                    Intent browser= new Intent(Intent.ACTION_VIEW, address);
                    startActivity(browser);
                }
            }
        });
        runUpdateTask();
    }

    ArrayAdapter<GeoName> arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(1, ID_MENU_UPDATE, Menu.NONE, "Update")
                .setIcon(R.drawable.ic_menu_connecting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ID_MENU_UPDATE:
                runUpdateTask();
                Toast.makeText(GeoNamesActivity.this, "Updating list...", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void runUpdateTask() {
        new UpdateGeoNamesTask().execute(new OnUpdateGeoNames() {
            @Override
            public void onUpdateGeoNames(String errorMessage) {
                if (errorMessage == null) {
                    List<GeoName> geoNames = new GeoNameWorker().getGeoNames();
                    Log.i(TAG, "Update geo names: " + geoNames);
                    arrayAdapter.clear();
                    for(GeoName geoName: geoNames)
                        arrayAdapter.add(geoName);
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GeoNamesActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}