package com.vickey.cowork;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

public class SelectLocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private final int REQUEST_CHECK_SETTINGS = 100;
    private final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private String TAG = "SelectLocationFragment";

    private SelectLocationListener mListener;

    private SupportMapFragment mSupportMapFragment;

    Context mContext;

    SharedPreferences mSharedPref;
    SharedPreferences.Editor mEditor;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Geocoder mCoder;

    private Marker mMarker;
    private boolean mIsStartup = true;

    private Vibrator mVibrator;

    public SelectLocationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mCoder = new Geocoder(mContext);
        mVibrator = (Vibrator) getActivity().getSystemService(mContext.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_location, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = getContext();
        mCoder = new Geocoder(mContext);

        setUpMapIfNeeded();
        mGoogleApiClient.connect();

        performLocationEnabledCheck();
        performNetworkCheck();
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
                                    getActivity(),
                                    REQUEST_CHECK_SETTINGS);
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
            case REQUEST_CHECK_SETTINGS:
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
        if(ConnectionDetector.isConnectedToInternet(mContext) == false){
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (lastLocation != null) {
                LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                Geocoder coder = new Geocoder(mContext);
                try {
                    ArrayList<Address> addresses = (ArrayList<Address>) coder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    String name = "";
                    for(Address add : addresses){
                        name = add.toString();
                    }
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(name)).setDraggable(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {

        FragmentManager fm = getChildFragmentManager();
        mSupportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mSupportMapFragment).commit();
        }

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null && mSupportMapFragment != null) {

            Log.d(TAG, "map: " + mSupportMapFragment);

            // Try to obtain the map from the SupportMapFragment.
            mMap = mSupportMapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        Log.d(TAG, "setUpMap");
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(loc));
                if (mMap != null && mIsStartup == true) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                    mIsStartup = false;
                }
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Geocoder coder = new Geocoder(mContext);
                try {
                    ArrayList<Address> addressList = (ArrayList<Address>) coder.getFromLocation(latLng.latitude,latLng.longitude,1);

                    if(mVibrator != null){
                        mVibrator.vibrate(50);
                    }

                    Address addr = addressList.get(0);
                    Log.d(TAG, "address: " + addr);
                    String address = addr.getAddressLine(0) + ", " + addr.getAddressLine(1) + ", " + addr.getAddressLine(2);
                    Log.d(TAG, "address fine: " + address);
                    Log.d(TAG, "lat lng: " + latLng.latitude + ", " + latLng.longitude);

                    new BackgroundTask().execute(latLng);

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(address)).setDraggable(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMyLocationEnabled(true);

        Toast.makeText(mContext, getString(R.string.toast_drop_pin_message), Toast.LENGTH_LONG).show();
    }


    /**
     * Create a new instance of SelectLocationFragment
     */
    static SelectLocationFragment newInstance() {
        SelectLocationFragment f = new SelectLocationFragment();
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SelectLocationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d(TAG, "onConnected");

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        };
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, "handleNewLocation: " + location.toString());
        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.setMyLocationEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(position).title("Your location")).setDraggable();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectedSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        handleNewLocation(location);
    }

    public interface SelectLocationListener {
        public void onSelectLocationEvent(int code);
    }

    class BackgroundTask extends AsyncTask<LatLng, Void, String>{

        @Override
        protected String doInBackground(LatLng... params) {
            Helper helper = new Helper();
            String resp = helper.getPlaceFromLocation(params[0]);
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}
