package com.example.serba.hygenechecker.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by serba on 27/02/2018.
 */

public class Geocode {
    private double latitude;
    private double longitude;

    public Geocode() {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng toLatLong() {
        return new LatLng(latitude, longitude);
    }
}
