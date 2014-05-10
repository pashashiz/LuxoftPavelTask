package com.luxoft.paveltask.app.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.luxoft.paveltask.app.model.entity.GeoName;
import com.luxoft.paveltask.app.model.entity.Status;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class GeoNamesService {

    static public final String ERR_CONNECTION = "Error connection";
    static public final String ERR_JSON_SCHEME = "Error json scheme";

    static public final String TAG = "GeoNamesService";

    static public final String SCHEME = "http";
    static public final String HOST = "api.geonames.org";
    static public final String CITIES_PATH = "citiesJSON";
    static public final String TEST_REQUEST = "{\"geonames\":[{\"fcodeName\":\"capital of a political entity\",\"toponymName\":\"Mexico City\",\"countrycode\":\"MX\",\"fcl\":\"P\",\"fclName\":\"city, village,...\",\"name\":\"Mexiko-Stadt\",\"wikipedia\":\"en.wikipedia.org/wiki/Mexico_City\",\"lng\":-99.12766456604,\"fcode\":\"PPLC\",\"geonameId\":3530597,\"lat\":19.428472427036,\"population\":12294193},{\"fcodeName\":\"capital of a political entity\",\"toponymName\":\"Manila\",\"countrycode\":\"PH\",\"fcl\":\"P\",\"fclName\":\"city, village,...\",\"name\":\"Manila\",\"wikipedia\":\"en.wikipedia.org/wiki/Manila\",\"lng\":120.9822,\"fcode\":\"PPLC\",\"geonameId\":1701668,\"lat\":14.6042,\"population\":10444527},{\"fcodeName\":\"capital of a political entity\",\"toponymName\":\"Dhaka\",\"countrycode\":\"BD\",\"fcl\":\"P\",\"fclName\":\"city, village,...\",\"name\":\"Dhaka\",\"wikipedia\":\"en.wikipedia.org/wiki/Dhaka\",\"lng\":90.40743827819824,\"fcode\":\"PPLC\",\"geonameId\":1185241,\"lat\":23.710395616597037,\"population\":10356500},{\"fcodeName\":\"capital of a political entity\",\"toponymName\":\"Seoul\",\"countrycode\":\"KR\",\"fcl\":\"P\",\"fclName\":\"city, village,...\",\"name\":\"Seoul\",\"wikipedia\":\"en.wikipedia.org/wiki/Seoul\",\"lng\":126.9784,\"fcode\":\"PPLC\",\"geonameId\":1835848,\"lat\":37.566,\"population\":10349312},{\"fcodeName\":\"capital of a political entity\",\"toponymName\":\"Jakarta\",\"countrycode\":\"ID\",\"fcl\":\"P\",\"fclName\":\"city, village,...\",\"name\":\"Jakarta\",\"wikipedia\":\"en.wikipedia.org/wiki/Jakarta\",\"lng\":106.84513092041016,\"fcode\":\"PPLC\",\"geonameId\":1642911,\"lat\":-6.214623197035775,\"population\":8540121},{\"fcodeName\":\"capital of a political entity\",\"toponymName\":\"Tokyo\",\"countrycode\":\"JP\",\"fcl\":\"P\",\"fclName\":\"city, village,...\",\"name\":\"Tokio\",\"wikipedia\":\"de.wikipedia.org/wiki/Tokyo\",\"lng\":139.69171,\"fcode\":\"PPLC\",\"geonameId\":1850147,\"lat\":35.6895,\"population\":8336599},{\"fcodeName\":\"capital of a political entity\",\"toponymName\":\"Taipei\",\"countrycode\":\"TW\",\"fcl\":\"P\",\"fclName\":\"city, village,...\",\"name\":\"Taipeh\",\"wikipedia\":\"de.wikipedia.org/wiki/Taipei\",\"lng\":121.531846,\"fcode\":\"PPLC\",\"geonameId\":1668341,\"lat\":25.047763,\"population\":7871900},{\"fcodeName\":\"capital of a political entity\",\"toponymName\":\"Bogotá\",\"countrycode\":\"CO\",\"fcl\":\"P\",\"fclName\":\"city, village,...\",\"name\":\"Bogotá\",\"wikipedia\":\"en.wikipedia.org/wiki/Bogot%C3%A1\",\"lng\":-74.08175468444824,\"fcode\":\"PPLC\",\"geonameId\":3688689,\"lat\":4.609705849789108,\"population\":7674366},{\"fcodeName\":\"capital of a political entity\",\"toponymName\":\"Beijing\",\"countrycode\":\"CN\",\"fcl\":\"P\",\"fclName\":\"city, village,...\",\"name\":\"Peking\",\"wikipedia\":\"en.wikipedia.org/wiki/Beijing\",\"lng\":116.397228240967,\"fcode\":\"PPLC\",\"geonameId\":1816670,\"lat\":39.9074977414405,\"population\":7480601},{\"fcodeName\":\"capital of a political entity\",\"toponymName\":\"Hong Kong\",\"countrycode\":\"HK\",\"fcl\":\"P\",\"fclName\":\"city, village,...\",\"name\":\"Hong Kong\",\"wikipedia\":\"en.wikipedia.org/wiki/Hong_Kong\",\"lng\":114.157691001892,\"fcode\":\"PPLC\",\"geonameId\":1819729,\"lat\":22.2855225817732,\"population\":7012738}]}";

    public List<NameValuePair> demoCitiesParams = new ArrayList<NameValuePair>() {{
        add(new BasicNameValuePair("north", "44.1"));
        add(new BasicNameValuePair("south", "-9.9"));
        add(new BasicNameValuePair("east", "-22.4"));
        add(new BasicNameValuePair("west", "55.2"));
        add(new BasicNameValuePair("lang", "de"));
        add(new BasicNameValuePair("username", "demo"));
    }};

    private Status status;

    private static GeoNamesService instance;

    private GeoNamesService() {}

    public static synchronized GeoNamesService getInstance() {
        if (instance == null)
            instance = new GeoNamesService();
        return instance;
    }

    public Status getStatus() {
        return status;
    }

    public List<GeoName> getGeoNameCities(List<NameValuePair> citiesParams) throws ServiceException {
        status = null;
        List<GeoName> geoNames = null;
        DefaultHttpClient hc = new DefaultHttpClient();
        ResponseHandler<String> res = new BasicResponseHandler();
        URI uri = buildURI(SCHEME, HOST, CITIES_PATH, citiesParams);
        try {
            String response = hc.execute(new HttpGet(uri), res);
            Log.i(TAG, "Response from service (" + uri.toString() + "): " + response);
            // Imitation receive data from server
            // response = TEST_REQUEST;
            try {
                geoNames = parseListGeoNames(response);
            } catch (ServiceException se) {
                status = parseStatus(response);
            }
        } catch (IOException e) {
            throw new ServiceException(ERR_CONNECTION, e);
        }
        return geoNames;
    }

    private URI buildURI(String scheme, String host, String path, List<NameValuePair> params) {
        URI uri;
        String spec = String.format("%s://%s/%s?%s", scheme, host, path, URLEncodedUtils.format(params, "utf-8"));
        try {
            uri = new URI(spec);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }

    protected List<GeoName> parseListGeoNames(String data) throws ServiceException {
        List<GeoName> geoNames;
        try {
            JSONObject root = new JSONObject(data);
            geoNames = createListGeoNames(root.getJSONArray("geonames"));
        } catch (JSONException e) {
            throw new ServiceException(ERR_JSON_SCHEME, e);
        }
        return geoNames;
    }

    private List<GeoName> createListGeoNames(JSONArray jsonGeoNames) throws ServiceException {
        List<GeoName> geoNames = new ArrayList<GeoName>();
        for (int i = 0; i < jsonGeoNames.length(); i++) {
            try {
                JSONObject jsonGeoName = jsonGeoNames.getJSONObject(i);
                geoNames.add(createGeoName(jsonGeoName));
            } catch (JSONException e) {
                throw new ServiceException(ERR_JSON_SCHEME, e);
            }
        }
        return geoNames;
    }

    private GeoName createGeoName(JSONObject data) throws ServiceException {
        GeoName geoName;
        try {
            geoName = new Gson().fromJson(data.toString(), GeoName.class);
        } catch (JsonSyntaxException e) {
            throw new ServiceException(ERR_JSON_SCHEME, e);
        }
        return geoName;
    }

    public Status parseStatus(String data) throws ServiceException {
        Status status;
        try {
            JSONObject root = new JSONObject(data);
            status = createStatus(root.getJSONObject("status"));
        } catch (JSONException e) {
            throw new ServiceException(ERR_JSON_SCHEME, e);
        }
        return status;
    }

    public Status createStatus(JSONObject data) throws ServiceException {
        Status status;
        try {
            status = new Gson().fromJson(data.toString(), Status.class);
        } catch (JsonSyntaxException e) {
            throw new ServiceException(ERR_JSON_SCHEME, e);
        }
        return status;
    }

}