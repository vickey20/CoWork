package com.vickey.cowork.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vickey.cowork.CoWork;
import com.vickey.cowork.CoworkBundle;
import com.vickey.cowork.LocationClass;
import com.vickey.cowork.R;
import com.vickey.cowork.UserProfile;
import com.vickey.cowork.receiver.IntentServiceReceiver;
import com.vickey.cowork.service.CoworkIntentService;
import com.vickey.cowork.utilities.ConnectionDetector;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.HelperClass;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscoverActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, IntentServiceReceiver.Receiver, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, ResultCallback<Status> {

    public static final String TAG = DiscoverActivity.class.getSimpleName();
    public static final Float CAMERA_ZOOM_HIGH = 16f;
    public static final Float CAMERA_ZOOM_MEDIUM = 12f;
    public static final Float CAMERA_ZOOM_LOW = 10f;


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean mIsStartup = true;
    private ArrayList<Integer> mSelectedFilters;
    boolean[] mBooleanArray = {true, true, true, true, true};

    private Location mLocation;

    ProgressDialog mLoadingDialog;

    HelperClass mHelper;

    HashMap<Marker, CoworkBundle> mMarkerCoWorkHashMap;
    ArrayList<CoWork> mCoWorkArrayList;
    ArrayList<UserProfile> mUserProfileArrayList;
    String[] mActivities;
    Boolean mIsAutoCheckin;

    ArrayList<CoWork> mUserCoworkList;

    Dialog mCoworkDialog;

    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        mGoogleApiClient.connect();

        mGeofenceList = new ArrayList<>();
        mHelper = new HelperClass(getApplicationContext());
        mActivities =  getApplicationContext().getResources().getStringArray(R.array.array_activities);
    }

    @Override
    protected void onStart() {
        super.onStart();

        performLocationEnabledCheck();
        performNetworkCheck();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();
        new GetCoWorksTask().execute();
    }

    private void performLocationEnabledCheck(){
        Log.d(TAG, "performLocationEnabledCheck");
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
                                    DiscoverActivity.this,
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
                        mGoogleApiClient.connect();
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
        Log.d(TAG, "performNetworkCheck");
        if(ConnectionDetector.isConnectedToInternet(DiscoverActivity.this) == false){
            Toast.makeText(DiscoverActivity.this, "Could not connect to internet", Toast.LENGTH_LONG).show();
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (lastLocation != null) {
                LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                Geocoder coder = new Geocoder(DiscoverActivity.this);
                try {
                    ArrayList<Address> addresses = (ArrayList<Address>) coder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    String name = "";
                    for(Address add : addresses){
                        name = add.toString();
                    }
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(name)).setDraggable(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
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
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    class GetCoWorksTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            //fetch coworks
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissLoadingDialog();
        }
    }

    private void showLoadingDialog(){
         mLoadingDialog = ProgressDialog.show(DiscoverActivity.this, "Loading Coworks", "Please wait...", false, true);
    }

    private void dismissLoadingDialog(){
        if(mLoadingDialog != null)
            mLoadingDialog.dismiss();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
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

                //mMap.addMarker(new MarkerOptions().position(loc));
                if (mMap != null && mIsStartup == true) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 12.0f));
                    mIsStartup = false;
                }
            }
        });
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        CameraPosition cameraPosition = mMap.getCameraPosition();
        Float zoom = cameraPosition.zoom;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location, true);
        }
    }

    private void handleNewLocation(Location location, boolean moveCamera) {
        Log.d(TAG, "handleNewLocation: " + location.toString());

        showLoadingDialog();

        mLocation = location;
        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.setMyLocationEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(position).title("Your location")).setDraggable();
        if(moveCamera) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
        }

        requestNearbyCoworkList(location);

    }

    private void setMarkers(ArrayList<CoWork> coworkList, ArrayList<UserProfile> userProfileArrayList, Location location, Boolean isUsersSynced){
        HelperClass.deleteCache(getApplicationContext());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        mMap.clear();

        mMarkerCoWorkHashMap = new HashMap<>();

        int i = 0;
        for(CoWork coWork: coworkList) {

            if (coWork.getCreatorID().equals(HomeActivity.USER_ID) == false) {
                Log.d(TAG, "marker for cowork id: " + coWork.getCoworkID());
                latLng = new LatLng(coWork.getLocationLat(), coWork.getLocationLng());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                String title = "CoWorkID: " + coWork.getCoworkID();
                UserProfile userProfile = new UserProfile();
                if (isUsersSynced == false) {
                    userProfile = getUserProfileFromUserID(coWork.getCreatorID(), userProfileArrayList);
                    if (userProfile != null) {
                        title = title + ". " + userProfile.getName();
                    }
                } else {
                    title = title + userProfileArrayList.get(i).getName();
                }

                markerOptions.title(title);
                markerOptions.snippet("Activity: " + String.valueOf(coWork.getActivityType()));
                Marker marker = mMap.addMarker(markerOptions);
                marker.setDraggable(false);
                marker.setVisible(true);
                //marker.showInfoWindow();

                CoworkBundle coworkBundle = new CoworkBundle();
                coworkBundle.setCoWork(coWork);
                if (isUsersSynced == false) {
                    coworkBundle.setUserProfile(userProfile);
                } else {
                    coworkBundle.setUserProfile(userProfileArrayList.get(i));
                }
                mMarkerCoWorkHashMap.put(marker, coworkBundle);

                coworkBundle = null;
                userProfile = null;

                i++;
            }
        }
    }

    private UserProfile getUserProfileFromUserID(String userID, ArrayList<UserProfile> userProfileArrayList) {

        for (UserProfile userProfile : userProfileArrayList) {
            if (userID.equals(userProfile.getUserId())) {
                return userProfile;
            }
        }

        return null;
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
                connectionResult.startResolutionForResult(DiscoverActivity.this, Constants.ActivityConstants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
        handleNewLocation(location, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_discover, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.filter:
                FilterDialog dialog = new FilterDialog();
                dialog.show(getFragmentManager(), "filter_dialog");
                dialog.setCancelable(false);
                break;
        }

        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        CoworkBundle coworkBundle = mMarkerCoWorkHashMap.get(marker);

        LatLng latLng = new LatLng(coworkBundle.getCoWork().getLocationLat(), coworkBundle.getCoWork().getLocationLng());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        Log.d(TAG, "Marker CoworkId: " + coworkBundle.getCoWork().getCoworkID());

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(DiscoverActivity.this, coworkBundle.getCoWork(), coworkBundle.getUserProfile()));

        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        showCoworkDialog(marker);
    }

    private void showCoworkDialog(Marker marker) {

        final CoworkBundle coworkBundle = mMarkerCoWorkHashMap.get(marker);

        mCoworkDialog = new Dialog(DiscoverActivity.this);
        mCoworkDialog.setContentView(R.layout.layout_join_cowork_dialog);
        mCoworkDialog.setTitle("CoWork");

        TextView location = (TextView) mCoworkDialog.findViewById(R.id.location);
        location.setText(coworkBundle.getCoWork().getLocationName());

        ImageView image = (ImageView) mCoworkDialog.findViewById(R.id.imageView);
        image.setImageResource(R.mipmap.ic_launcher);

        TextView nameText = (TextView) mCoworkDialog.findViewById(R.id.name);
        nameText.setText(coworkBundle.getUserProfile().getName());

        TextView activity = (TextView) mCoworkDialog.findViewById(R.id.activity);
        activity.setText(mActivities[coworkBundle.getCoWork().getActivityType()]);

        TextView profession = (TextView) mCoworkDialog.findViewById(R.id.profession);
        profession.setText(coworkBundle.getUserProfile().getProfession());

        TextView description = (TextView) mCoworkDialog.findViewById(R.id.description);
        description.setText(coworkBundle.getCoWork().getDescription());

        TextView time = (TextView) mCoworkDialog.findViewById(R.id.textViewTime);
        time.setText(coworkBundle.getCoWork().getTime());

        TextView date = (TextView) mCoworkDialog.findViewById(R.id.textViewDate);
        date.setText(coworkBundle.getCoWork().getDate());

        final CheckBox autoCheckin = (CheckBox) mCoworkDialog.findViewById(R.id.checkboxAutoCheckin);

        Button join = (Button) mCoworkDialog.findViewById(R.id.joinButton);

        join.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUserAlreadyAttendee(coworkBundle) == false) {
                    addUserAsAttendee(coworkBundle.getCoWork().getCoworkID(), HomeActivity.USER_ID);
                    mIsAutoCheckin = autoCheckin.isChecked();
                } else {
                    Toast.makeText(DiscoverActivity.this, "You have already joined this CoWork!", Toast.LENGTH_LONG).show();
                }
            }
        });

        mCoworkDialog.show();
    }

    private boolean isUserAlreadyAttendee(CoworkBundle coworkBundle) {

        mUserCoworkList = mHelper.getUserCoworkList();
        Log.d(TAG, "cowork bundle: " + coworkBundle.getCoWork().getAttendeesID());
        for (int i = 0; i < mUserCoworkList.size(); i++) {
            Log.d(TAG, "coworkID: " + mUserCoworkList.get(i).getCoworkID());
            Log.d(TAG, "mUserCoworkList attendees: " + mUserCoworkList.get(i).getAttendeesID());
            if (coworkBundle.getCoWork().getAttendeesID().contains(mUserCoworkList.get(i).getAttendeesID())) {
                return true;
            }
        }

        return false;
    }

    public class FilterDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //mSelectedFilters = new ArrayList<>();
            if(mSelectedFilters == null) {
                mSelectedFilters = new ArrayList();  // Where we track the selected items
                mSelectedFilters.add(0, 0);
                mSelectedFilters.add(1, 1);
                mSelectedFilters.add(2, 2);
                mSelectedFilters.add(3, 3);
                mSelectedFilters.add(4, 4);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title
            builder.setTitle(R.string.title_filter_dialog)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(R.array.array_activities, mBooleanArray,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        mSelectedFilters.add(which);
                                    } else if (mSelectedFilters.contains(which)) {
                                        // Else, if the item is already in the array, remove it
                                        mSelectedFilters.remove(Integer.valueOf(which));
                                    }
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedFilter results somewhere
                            // or return them to the component that opened the dialog
                            applyFilter(mSelectedFilters);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            return builder.create();
        }
    }

    private void applyFilter(ArrayList<Integer> selectedFilters) {
        ArrayList<CoworkBundle> coworkBundleArrayList = getFilteredCoworkBundle(selectedFilters);

        if (coworkBundleArrayList.size() > 0) {
            ArrayList<CoWork> coWorks = new ArrayList<>();
            ArrayList<UserProfile> userProfiles = new ArrayList<>();

            for (CoworkBundle coworkBundle : coworkBundleArrayList) {
                coWorks.add(coworkBundle.getCoWork());
                userProfiles.add(coworkBundle.getUserProfile());
            }

            mMarkerCoWorkHashMap = null;
            coworkBundleArrayList = null;
            setMarkers(coWorks, userProfiles, mLocation, true);
        } else {
            mMap.clear();
        }
    }

    private ArrayList<CoworkBundle> getFilteredCoworkBundle(ArrayList<Integer> selectedFilters) {

        ArrayList<CoworkBundle> coworkBundleArrayList = new ArrayList<>();

        for(int i = 0; i < mCoWorkArrayList.size(); i++) {
            CoworkBundle coworkBundle = new CoworkBundle();
            CoWork coWork = mCoWorkArrayList.get(i);
            if(selectedFilters.contains(coWork.getActivityType())) {
                coworkBundle.setCoWork(coWork);
                //coWorks.add(mCoWorkArrayList.get(i));
                coworkBundle.setUserProfile(getUserProfileFromUserID(coWork.getCreatorID(), mUserProfileArrayList));
                //userProfiles.add(getUserProfileFromUserID(coWork.getCreatorID(), mUserProfileArrayList));

                coworkBundleArrayList.add(coworkBundle);
            }
        }

        return coworkBundleArrayList;
    }

    private void requestNearbyCoworkList(Location location){

        /* Starting Download Service */
        IntentServiceReceiver receiver = new IntentServiceReceiver(new Handler());
        receiver.setReceiver(DiscoverActivity.this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, CoworkIntentService.class);

        LocationClass locationClass = new LocationClass();
        locationClass.setLat(location.getLatitude());
        locationClass.setLng(location.getLongitude());

            /* Send optional extras to Download IntentService */
        intent.putExtra(CoworkIntentService.LOCATION, locationClass);
        intent.putExtra(CoworkIntentService.RECEIVER, receiver);
        intent.putExtra(CoworkIntentService.REQUEST_ID, Constants.Request.COWORK_REQUEST);
        intent.putExtra(CoworkIntentService.REQUEST_TYPE, Constants.Request.GET_NEARBY_COWORKS_FROM_SERVER);

        startService(intent);
    }

    private void addUserAsAttendee(int coworkID, String userID) {

        /* Starting Download Service */
        IntentServiceReceiver receiver = new IntentServiceReceiver(new Handler());
        receiver.setReceiver(DiscoverActivity.this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, CoworkIntentService.class);

            /* Send optional extras to Download IntentService */
        intent.putExtra(CoworkIntentService.COWORK_ID, coworkID);
        intent.putExtra(CoworkIntentService.USER_ID, userID);
        intent.putExtra(CoworkIntentService.RECEIVER, receiver);
        intent.putExtra(CoworkIntentService.REQUEST_ID, Constants.Request.COWORK_REQUEST);
        intent.putExtra(CoworkIntentService.REQUEST_TYPE, Constants.Request.ADD_USER_AS_ATTENDEE);

        startService(intent);
    }

    private void requestUserProfiles(ArrayList<CoWork> coWorks){

        /* Starting Download Service */
        IntentServiceReceiver receiver = new IntentServiceReceiver(new Handler());
        receiver.setReceiver(DiscoverActivity.this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, CoworkIntentService.class);

            /* Send optional extras to Download IntentService */
        intent.putExtra(CoworkIntentService.COWORK, coWorks);
        intent.putExtra(CoworkIntentService.RECEIVER, receiver);
        intent.putExtra(CoworkIntentService.REQUEST_ID, Constants.Request.USER_REQUEST);
        intent.putExtra(CoworkIntentService.REQUEST_TYPE, Constants.Request.GET_CORRESPONDING_USER_PROFILE_LIST);

        startService(intent);
    }

    private void setGeoFenceForCowork(CoWork coWork) {

        String dateTime = coWork.getDate() + " " + coWork.getTime();
        long expirationTime = HelperClass.getTimeInMillis(dateTime, Constants.TimeAndDate.DATE_FORMAT + " " + Constants.TimeAndDate.TIME_FORMAT) - System.currentTimeMillis();

        mGeofenceList.add(new Geofence.Builder()
        .setRequestId(String.valueOf(coWork.getCoworkID()))
        .setCircularRegion(coWork.getLocationLat(), coWork.getLocationLng(), 100)
        .setExpirationDuration(expirationTime)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
        .setLoiteringDelay(5 * 60 * 1000)
        .build());

        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent(coWork)
        ).setResultCallback(DiscoverActivity.this);

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(CoWork coWork) {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, CoworkIntentService.class);
        intent.putExtra(CoworkIntentService.REQUEST_ID, CoworkIntentService.GEOFENCE);
        intent.putExtra(CoworkIntentService.COWORK, coWork);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {

            Toast.makeText(
                    this,
                    "Geofence added",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            Log.e(TAG, "Error in adding geofence");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(TAG, "onReceiveResult:: resultCode: " + resultCode + "; resultData: " + resultData);

        switch (resultCode) {
            case CoworkIntentService.STATUS_RUNNING:

                break;

            case CoworkIntentService.STATUS_FINISHED:
                if (resultData != null) {
                    int requestType = resultData.getInt(CoworkIntentService.REQUEST_TYPE);
                    if (requestType == Constants.Request.GET_NEARBY_COWORKS_FROM_SERVER) {
                        Log.d(TAG, "Fetched successfully");
                        Toast.makeText(DiscoverActivity.this, "Got nearby coworks", Toast.LENGTH_LONG).show();
                        mCoWorkArrayList = (ArrayList<CoWork>) resultData.getSerializable(CoworkIntentService.RESULT);
                        Log.d(TAG, "cowork: " + mCoWorkArrayList);
                        requestUserProfiles(mCoWorkArrayList);
                    } else if (requestType == Constants.Request.GET_CORRESPONDING_USER_PROFILE_LIST) {
                        Toast.makeText(DiscoverActivity.this, "Fetched successfully", Toast.LENGTH_LONG).show();
                        dismissLoadingDialog();
                        mUserProfileArrayList = (ArrayList<UserProfile>) resultData.getSerializable(CoworkIntentService.RESULT);
                        setMarkers(mCoWorkArrayList, mUserProfileArrayList, mLocation, false);
                    } else if (requestType == Constants.Request.ADD_USER_AS_ATTENDEE) {
                        Toast.makeText(DiscoverActivity.this, "Successfully added to cowork!", Toast.LENGTH_LONG).show();
                        dismissLoadingDialog();
                        CoWork coWork = (CoWork) resultData.getSerializable(CoworkIntentService.RESULT);
                        if (mIsAutoCheckin == true) {
                            coWork.setAutoCheckin(1);
                            setGeoFenceForCowork(coWork);
                        }
                        mHelper.saveCoworkToDatabase(coWork);
                        if(mCoworkDialog != null) {
                            mCoworkDialog.dismiss();
                        }
                    }
                }
                break;

            case CoworkIntentService.STATUS_ERROR:
                Toast.makeText(DiscoverActivity.this, "Error fetching nearby CoWorks. Please try again...", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        Context context;
        CoWork coWork;
        UserProfile userProfile;

        public CustomInfoWindowAdapter(Context context, CoWork coWork, UserProfile userProfile) {
            this.context = context;
            this.coWork = coWork;
            this.userProfile = userProfile;
        }

        @Override
        public View getInfoContents(Marker marker) {

            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.marker_info_window_discover, null);

            TextView title = (TextView) view.findViewById(R.id.textViewTitle);
            TextView activity = (TextView) view.findViewById(R.id.textViewActivity);
            TextView address = (TextView) view.findViewById(R.id.textViewAddress);

            if (userProfile.getName() != null && userProfile.getName().equals("") == false) {
                title.setText(userProfile.getName());
            } else {
                title.setVisibility(TextView.GONE);
            }

            activity.setText(mActivities[coWork.getActivityType()]);

            if (coWork.getLocationName() != null && coWork.getLocationName().equals("") == false) {
                address.setText("Address: " + coWork.getLocationName());
            } else {
                address.setVisibility(TextView.GONE);
            }

            return view;
        }
    }
}
