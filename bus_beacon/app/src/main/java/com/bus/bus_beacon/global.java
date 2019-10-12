package com.bus.bus_beacon;

import android.app.Application;

public class global extends Application {
    public double lng=0.0,lat=0.0;

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

}
