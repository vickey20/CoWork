package com.vickey.cowork;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vikram on 11/19/2015.
 */
public class Helper {

    private final String TAG = "Helper";

    public String getPlaceFromLocation(LatLng latLng){

        JSONParser jsonParser = new JSONParser();

        String urlStr = Constants.Urls.API_GET_PLACE_FROM_LOCATION
                     + "location=" + latLng.latitude + "," + latLng.longitude
                     + "&rankby=distance"
                     + "&key=" + Constants.API_KEY;

        String jsonResp = jsonParser.getJSONFromUrl(urlStr);

        Log.d(TAG, "getPlaceFromLocation: response: " + jsonResp);

        return jsonResp;
    }
}
