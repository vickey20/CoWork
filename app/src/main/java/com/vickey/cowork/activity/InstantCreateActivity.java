package com.vickey.cowork.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.vickey.cowork.CoWork;
import com.vickey.cowork.R;
import com.vickey.cowork.utilities.ConnectionDetector;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.HelperClass;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InstantCreateActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public static final String TAG = InstantCreateActivity.class.getSimpleName();

    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mEditor;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private HelperClass mHelper;

    private ProgressDialog mProgressDialog;

    private TextView mTextViewAddress, mTextViewTime, mTextViewDate;
    private Spinner mSpinnerActivity;
    private Button mButtonCreateCowork;

    private String[] mActivities;
    private double mLatitude, mLongitude;
    private String mAddress, mTime, mDate;
    private int mActivityPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_create);

        mTextViewAddress = (TextView) findViewById(R.id.textViewLocationName);
        mTextViewTime = (TextView) findViewById(R.id.textViewTime);
        mTextViewDate = (TextView) findViewById(R.id.textViewDate);
        mSpinnerActivity = (Spinner) findViewById(R.id.spinnerActivity);
        mButtonCreateCowork = (Button) findViewById(R.id.buttonCreateInstantCowork);

        mButtonCreateCowork.setOnClickListener(this);
        mTextViewTime.setOnClickListener(this);
        mTextViewDate.setOnClickListener(this);

        mTextViewAddress.setText("Locating you...");
        mActivities =  getResources().getStringArray(R.array.array_activities);

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

        mSpinnerActivity.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selected activity: " + mActivities[position]);
                mActivityPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapterActivity = new ArrayAdapter<String>(InstantCreateActivity.this, android.R.layout.simple_spinner_dropdown_item, mActivities);
        mSpinnerActivity.setAdapter(adapterActivity);

        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
        mTime = sdf.format(c.getTime());
        mTextViewTime.setText(mTime);

        sdf = new SimpleDateFormat("EEE, MMM dd");
        mDate = sdf.format(c.getTime());
        mTextViewDate.setText(mDate);

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
        mProgressDialog = ProgressDialog.show(InstantCreateActivity.this, "Getting Location", "Please wait...", false, true);
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
                mAddress = addr.getAddressLine(0) + ", " + addr.getAddressLine(1);
                Log.d(TAG, "address fine: " + mAddress);
                dismissLoadingDialog();
                mTextViewAddress.setText(mAddress);

                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewTime:
                DialogFragment timeFragment = new StartTimePicker();
                timeFragment.show(getFragmentManager(), "start_time_picker");
                break;

            case R.id.textViewDate:
                DialogFragment dialogFragment = new StartDatePicker();
                dialogFragment.show(getFragmentManager(), "start_date_picker");
                break;

            case R.id.buttonCreateInstantCowork:
                CoWork coWork = createCowork();

                mHelper = new HelperClass(InstantCreateActivity.this);
                ProgressDialog pd = ProgressDialog.show(InstantCreateActivity.this, "CoWork", "Saving...", false, false);
                if(mHelper.saveCoworkToDatabase(coWork) == 1){
                    if(pd != null){
                        pd.cancel();
                    }
                    if (mGoogleApiClient.isConnected()) {
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                        mGoogleApiClient.disconnect();
                    }

                    finish();
                } else {
                    Toast.makeText(InstantCreateActivity.this, "Could not create CoWork...", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private CoWork createCowork() {
        CoWork coWork = new CoWork();
        coWork.setCreatorID(HomeActivity.USER_ID);
        coWork.setLocationLat(String.valueOf(mLatitude));
        coWork.setLocationLng(String.valueOf(mLongitude));
        coWork.setLocationName(mAddress);
        coWork.setActivityType(mActivityPosition);
        coWork.setTime(mTime);
        coWork.setDate(mDate);
        return coWork;
    }

    class StartTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO Auto-generated method stub

            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Use the current date as the default date in the picker
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, false);

            return dialog;

        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {

                calSet.add(Calendar.DATE, 1);
            }

            updateTimeField(calSet);
        }
    }


    public void updateTimeField(Calendar calendar) {
        mTime = (String) DateFormat.format("hh:mm a", calendar.getTime());
        mTextViewTime.setText(mTime);
    }


    class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO Auto-generated method stub

            Calendar c = Calendar.getInstance();
            int startYear = c.get(Calendar.YEAR);
            int startMonth = c.get(Calendar.MONTH);
            int startDay = c.get(Calendar.DAY_OF_MONTH);

            // Use the current date as the default date in the picker
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, startYear, startMonth, startDay);

            return dialog;

        }
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            updateDateField(year, monthOfYear + 1, dayOfMonth);
        }
    }

    public void updateDateField(int startYear, int startMonth, int startDay) {

        try {
            SimpleDateFormat initial = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
            Date date = initial.parse("" + startMonth + "-" + startDay + "-" + startYear +"");

            SimpleDateFormat finalFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);

            mDate = finalFormat.format(date);

            Log.d("MainActivity", "date: " + mDate);
            mTextViewDate.setText(mDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
