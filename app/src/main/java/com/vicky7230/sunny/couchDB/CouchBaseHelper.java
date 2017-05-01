package com.vicky7230.sunny.couchDB;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CouchBaseHelper {

    private static final String TAG = CouchBaseHelper.class.getSimpleName();
    private static final String DB_NAME = "sunny_db";

    private static final String LOCATION_DOCUMENT_ID = "location";

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    /**
     * Open database connection
     *
     * @param context context
     * @return database
     */
    public static Database openCouchBaseDB(Context context) {

        Manager manager = null;
        Database database = null;

        try {

            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);

        } catch (IOException e) {

            Log.e(TAG, "Error while opening connection to CouchDB : " + e);

        }

        try {

            database = manager != null ? manager.getDatabase(DB_NAME) : null;

        } catch (CouchbaseLiteException e) {

            Log.e(TAG, "Error while opening connection to CouchDB : " + e);

        }

        return database;
    }


    public static void saveLatLonInDB(Database database, Location location) {

        Document locationDocument = database.getDocument(LOCATION_DOCUMENT_ID);
        Map<String, Object> locationMap = new HashMap<>();

        if (locationDocument.getProperties() != null)
            locationMap.putAll(locationDocument.getProperties());//put old data from the document in the new locationMap( update )

        locationMap.put(LATITUDE, location.getLatitude());
        locationMap.put(LONGITUDE, location.getLongitude());

        try {
            locationDocument.putProperties(locationMap);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting location in DB : " + e);
        }

    }


    public static Map<String, Object> getLatLonFromDB(Database database) {

        Document locationDocument = database.getDocument(LOCATION_DOCUMENT_ID);
        Map<String, Object> locationMap = new HashMap<>();

        if (locationDocument.getProperties() != null)
            locationMap.putAll(locationDocument.getProperties());//put old data from the document in the new locationMap( update )

        return locationMap;

    }
}
