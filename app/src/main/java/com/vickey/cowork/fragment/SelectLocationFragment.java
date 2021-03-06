package com.vickey.cowork.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.TextView;
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
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vickey.cowork.PlaceInfo;
import com.vickey.cowork.adapter.PlaceAutocompleteAdapter;
import com.vickey.cowork.R;
import com.vickey.cowork.utilities.ConnectionDetector;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.HelperClass;

import java.io.IOException;
import java.util.ArrayList;

public class SelectLocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private String TAG = "SelectLocationFragment";

    private SelectLocationListener mListener;

    private SupportMapFragment mSupportMapFragment;

    Context mContext;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Geocoder mCoder;

    private boolean mIsStartup = true;
    private boolean mIsLocationSelectedByUser = false;

    private Vibrator mVibrator;

    private AutoCompleteTextView mAutoSearch;
    PlaceAutocompleteAdapter mAutoCompleteAdapter;

    private LatLngBounds mBounds;

    public SelectLocationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getBaseContext();
        mCoder = new Geocoder(mContext);
        mVibrator = (Vibrator) getActivity().getSystemService(mContext.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_location, container, false);

        mAutoSearch = (AutoCompleteTextView) v.findViewById(R.id.textViewSearch);
        mAutoSearch.setOnItemClickListener(mAutocompleteClickListener);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), 1 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        mGoogleApiClient.connect();
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAutoCompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(mContext, "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            Place place = places.get(0);

            String snippet = formatPlaceDetails(getResources(),
                    place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()).toString();

            // Format details of the place for display and show it in a TextView.
            Log.d(TAG, "mUpdatePlaceDetailsCallback: " + place.getName() + "\n" + "Rating: " + place.getRating() + "\n "
                    + snippet);

            PlaceInfo placeInfo = new PlaceInfo();

            placeInfo.setLatLng(place.getLatLng());
            placeInfo.setId(place.getId());
            placeInfo.setName(place.getName().toString());
            placeInfo.setAddress(place.getAddress().toString());
            placeInfo.setPhone(place.getPhoneNumber().toString());
            placeInfo.setWebsite(place.getWebsiteUri());
            placeInfo.setRating(place.getRating());

            mListener.onLocationSet(placeInfo);

            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mContext, placeInfo));

            showMarker(placeInfo);

            places.release();

            mIsLocationSelectedByUser = true;
        }
    };

    private Spanned formatPlaceDetails(Resources res, CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.d(TAG, res.getString(R.string.place_details, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, address, phoneNumber,
                websiteUri));
    }

    private void showMarker(PlaceInfo placeInfo) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeInfo.getLatLng(), 16));
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions().position(placeInfo.getLatLng());
        Marker marker = mMap.addMarker(markerOptions);
        marker.setDraggable(true);
        marker.showInfoWindow();
    }

    @Override
    public void onStart() {
        super.onStart();

        performLocationEnabledCheck();
        performNetworkCheck();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        mContext = getContext();
        mCoder = new Geocoder(mContext);

        setUpMapIfNeeded();
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
                        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {
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
                                    getActivity(),
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
                        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {
                            mGoogleApiClient.connect();
                        }
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
            Toast.makeText(getActivity(), "Could not connect to internet", Toast.LENGTH_LONG).show();
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
        else {
            if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        Log.d(TAG, "setUpMapIfNeeded");
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
                //set bounds for place search nearby
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng loc1 = new LatLng(location.getLatitude() - 0.5, location.getLongitude() - 0.1);
                LatLng loc2 = new LatLng(location.getLatitude() + 0.5, location.getLongitude() + 0.5);
                mBounds = new LatLngBounds(loc1, loc2);

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

                if (ConnectionDetector.isConnectedToInternet(getActivity())) {

                    String address = getAddressFromLocation(latLng);

                    if (mVibrator != null) {
                        mVibrator.vibrate(50);
                    }
                    PlaceInfo placeInfo = new PlaceInfo();
                    placeInfo.setLatLng(latLng);
                    placeInfo.setName("");
                    placeInfo.setAddress(address);
                    mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mContext, placeInfo));

                    showMarker(placeInfo);

                    mListener.onLocationSet(placeInfo);

                    mIsLocationSelectedByUser = true;

                } else {
                    Toast.makeText(getActivity(), "Could not connect to internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMyLocationEnabled(true);

        Toast.makeText(mContext, getString(R.string.toast_drop_pin_message), Toast.LENGTH_LONG).show();
    }

    private String getAddressFromLocation(LatLng latLng) {
        Geocoder coder = new Geocoder(mContext);
        try {
            ArrayList<Address> addressList = (ArrayList<Address>) coder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            Address addr = addressList.get(0);
            Log.d(TAG, "address: " + addr);
            String address = addr.getAddressLine(0) + ", " + addr.getAddressLine(1);
            Log.d(TAG, "address fine: " + address);

            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Create a new instance of SelectLocationFragment
     */
    public static SelectLocationFragment newInstance() {
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

        Log.d(TAG, "onConnected: " + mGoogleApiClient.isConnected());

        mAutoCompleteAdapter = new PlaceAutocompleteAdapter(mContext, mGoogleApiClient, mBounds, null);
        mAutoSearch.setAdapter(mAutoCompleteAdapter);

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location, true);
        }
    }

    private void handleNewLocation(Location location, boolean moveCamera) {
        Log.d(TAG, "handleNewLocation: " + location.toString());
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.setMyLocationEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(position).title("Your location")).setDraggable();
        if (moveCamera) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
        String address = getAddressFromLocation(position);

        PlaceInfo placeInfo = new PlaceInfo();
        placeInfo.setName("");
        placeInfo.setAddress(address);
        placeInfo.setLatLng(position);

        /*
         * Set this location only if user has not manually selected any location.
         * To avoid overwriting the user selected location when a location update is received.
         */
        if (mIsLocationSelectedByUser == false) {
            mListener.onLocationSet(placeInfo);
        }
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
                connectionResult.startResolutionForResult(getActivity(), Constants.ActivityConstants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
        LatLng loc1 = new LatLng(location.getLatitude() - 0.5, location.getLongitude() - 0.5);
        LatLng loc2 = new LatLng(location.getLatitude() + 0.5, location.getLongitude() + 0.5);
        mBounds = new LatLngBounds(loc1, loc2);
        handleNewLocation(location, false);
    }

    public interface SelectLocationListener {
        void onLocationSet(PlaceInfo placeInfo);
    }

    class BackgroundTask extends AsyncTask<LatLng, Void, String>{

        @Override
        protected String doInBackground(LatLng... params) {
            HelperClass helper = new HelperClass(mContext);
            String resp = helper.getPlaceFromLocation(params[0]);
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    /*class AutoCompleteTask extends AsyncTask<String, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Helper helper = new Helper();
            ArrayList<String> resp = helper.getAddressesFromInput(params[0]);
            return resp;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);

            mAutoCompleteAdapter.addAll();
            mAutoCompleteAdapter.notifyDataSetChanged();
        }
    }*/

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        Context context;
        PlaceInfo placeInfo;

        public CustomInfoWindowAdapter(Context context, PlaceInfo placeInfo) {
            this.context = context;
            this.placeInfo = placeInfo;
        }

        @Override
        public View getInfoContents(Marker marker) {

            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.marker_info_window, null);

            TextView title = (TextView) view.findViewById(R.id.textViewTitle);
            TextView address = (TextView) view.findViewById(R.id.textViewAddress);
            TextView phone = (TextView) view.findViewById(R.id.textViewPhone);
            TextView website = (TextView) view.findViewById(R.id.textViewWebsite);
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

            if (placeInfo.getName() != null && placeInfo.getName().equals("") == false) {
                title.setText(placeInfo.getName());
            } else {
                title.setVisibility(TextView.GONE);
            }

            if (placeInfo.getAddress() != null && placeInfo.getAddress().equals("") == false) {
                address.setText("Address: " + placeInfo.getAddress());
            } else {
                address.setVisibility(TextView.GONE);
            }

            if (placeInfo.getPhone() != null && placeInfo.getPhone().equals("") == false) {
                phone.setText("Phone: " + placeInfo.getPhone());
            } else {
                phone.setVisibility(TextView.GONE);
            }

            if (placeInfo.getWebsite() != null) {
                website.setText("Website: " + placeInfo.getWebsite());
            } else {
                website.setVisibility(TextView.GONE);
            }

            if (placeInfo.getRating() != 0.0f) {
                ratingBar.setRating(placeInfo.getRating() * 10);
            } else {
                ratingBar.setVisibility(RatingBar.GONE);
            }

            return view;
        }
    }
}
