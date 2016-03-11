package com.vickey.cowork.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.vickey.cowork.R;
import com.vickey.cowork.utilities.ConnectionDetector;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.HelperClass;

import java.io.IOException;
import java.util.ArrayList;

public class InstantCreateActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = InstantCreateActivity.class.getSimpleName();

    SharedPreferences mSharedPref;
    SharedPreferences.Editor mEditor;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private HelperClass mHelper;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_create);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        mSharedPref = getSharedPreferences(getString(R.string.create_cowork_shared_pref), Context.MODE_PRIVATE);
        int p = mSharedPref.getInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_DENIED);
        if(p == 0){
            checkPermission();
        }
        else{
            mGoogleApiClient.connect();
        }
        showLoadingDialog();
        mHelper = new HelperClass(InstantCreateActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        performLocationEnabledCheck();
        performNetworkCheck();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void checkPermission(){
        //Check app permission for accessing location
        int permissionCheck = ContextCompat.checkSelfPermission(InstantCreateActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(InstantCreateActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {

                ActivityCompat.requestPermissions(InstantCreateActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.ActivityConstants.PERMISSION_REQUEST_ACCESS_FINE_LOCATION);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.ActivityConstants.PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "permission granted");

                    mEditor = mSharedPref.edit();
                    mEditor.putInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_GRANTED);
                    mEditor.commit();
                } else {

                    mEditor = mSharedPref.edit();
                    mEditor.putInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_DENIED);
                    mEditor.commit();

                    // TODO: 12/14/2015 Remove this later and let user type an address instead of using current location
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void performLocationEnabledCheck(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        Log.d(TAG, "Location available");
                        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {
                            mGoogleApiClient.connect();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        //showAlertDialog(status);
                        Log.d(TAG, "Location unavailable, go to settings");
                        try {
                            status.startResolutionForResult(
                                    InstantCreateActivity.this,
                                    Constants.ActivityConstants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Log.d(TAG, "Settings unavailable");
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case Constants.ActivityConstants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Log.d(TAG, "Location turned on.");
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d(TAG, "Location not turned on.");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void performNetworkCheck(){
        if(ConnectionDetector.isConnectedToInternet(InstantCreateActivity.this) == false){
            Toast.makeText(InstantCreateActivity.this, "Could not connect to internet", Toast.LENGTH_LONG).show();
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (lastLocation != null) {
                LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                Geocoder coder = new Geocoder(InstantCreateActivity.this);
                try {
                    ArrayList<Address> addresses = (ArrayList<Address>) coder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    String name = "";
                    for(Address add : addresses){
                        name = add.toString();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else {
            if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {
                mGoogleApiClient.connect();
            }
        }
    }

    private void initializeViews() {

    }

    private void showLoadingDialog(){
        mProgressDialog = ProgressDialog.show(InstantCreateActivity.this, "Getting Location", "Please wait...", false, false);
    }

    private void dismissLoadingDialog(){
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, "handleNewLocation: " + location.toString());
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        try {
            if(ConnectionDetector.isConnectedToInternet(InstantCreateActivity.this)) {
                Geocoder coder = new Geocoder(InstantCreateActivity.this);
                ArrayList<Address> addressList = (ArrayList<Address>) coder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                Address addr = addressList.get(0);
                Log.d(TAG, "address: " + addr);
                String address = addr.getAddressLine(0) + ", " + addr.getAddressLine(1);
                Log.d(TAG, "address fine: " + address);
                dismissLoadingDialog();
            }
            else {
                Toast.makeText(InstantCreateActivity.this, "Could not connect to internet", Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(InstantCreateActivity.this, Constants.ActivityConstants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
}
