package com.vicky7230.sunny.couchDB;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class CouchBaseHelper {

    private static final String TAG = CouchBaseHelper.class.getSimpleName();
    private static final String DB_NAME = "sunny_db";

    private static final String CITIES_DOCUMENT_ID = "cities";

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

    public static void saveCityInDB(Database database, String city) {

        Document citiesDocument = database.getDocument(CITIES_DOCUMENT_ID);
        Map<String, Object> citiesMap = new HashMap<>();

        if (citiesDocument.getProperties() != null)
            citiesMap.putAll(citiesDocument.getProperties());

        citiesMap.put(city, city);

        try {
            citiesDocument.putProperties(citiesMap);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting cities in DB : " + e);
        }
    }

    public static void removeCityFromDB(Database database, String city) {

        Document citiesDocument = database.getDocument(CITIES_DOCUMENT_ID);
        Map<String, Object> citiesMap = new HashMap<>();

        if (citiesDocument.getProperties() != null)
            citiesMap.putAll(citiesDocument.getProperties());

        citiesMap.remove(city);

        try {
            citiesDocument.putProperties(citiesMap);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting cities in DB : " + e);
        }
    }

    public static ArrayList<String> getCitiesFromTheDB(Database database) {

        Document citiesDocument = database.getDocument(CITIES_DOCUMENT_ID);
        Map<String, Object> citiesMap = new HashMap<>();

        if (citiesDocument.getProperties() != null)
            citiesMap.putAll(citiesDocument.getProperties());

        ArrayList<String> citiesArrayList = new ArrayList<>();

        Iterator it = citiesMap.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();

            if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                ;
            else {

                citiesArrayList.add(pair.getValue().toString());
            }
        }

        Collections.sort(citiesArrayList);

        return citiesArrayList;
    }
}
