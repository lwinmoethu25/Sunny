package com.vicky7230.sunny.pojo;

/**
 * Created by agrim on 2/5/17.
 */

public class LatLon {

    String lat;
    String lon;


    public LatLon(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
