package com.vickey.cowork.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vickey.cowork.utilities.AlarmReceiver;

/**
 * Created by vikramgupta on 3/29/16.
 */
public class CoworkService extends Service {

    static final String TAG = "CoworkService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int coworkID = extras.getInt(AlarmReceiver.COWORK_ID);
                Log.d(TAG, "coworkID: " + coworkID);

                setLocationListener();

            }
        }
        return START_REDELIVER_INTENT;
    }

    private void setLocationListener() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60*1000, 100, locationListener);
    }

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.

            Log.d(TAG, "onLocationChanged: Lat: " + location.getLatitude() + "Lng: " + location.getLongitude() );
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
