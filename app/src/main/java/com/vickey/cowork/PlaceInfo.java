package com.vickey.cowork;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vikramgupta on 3/27/16.
 */
public class PlaceInfo {

    LatLng latLng;
    String name;
    String id;
    String address;
    String phone;
    Uri website;
    float rating;

    void PlaceInfo () {
        latLng = null;
        name = "";
        id = "";
        address = "";
        phone = "";
        website = null;
        rating = 0.0f;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Uri getWebsite() {
        return website;
    }

    public void setWebsite(Uri website) {
        this.website = website;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
