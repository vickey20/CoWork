package com.vickey.cowork;

import java.io.Serializable;

/**
 * Created by vikramgupta on 4/19/16.
 */
public class LocationClass implements Serializable{
    Double lat;
    Double lng;

    public LocationClass() {
        lat = 0.0;
        lng = 0.0;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
