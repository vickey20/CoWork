package com.vickey.cowork;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class CreateActivity extends FragmentActivity implements View.OnClickListener,
                                                        SelectLocationFragment.SelectLocationListener,
                                                        DetailsFragment.DetailsListener,
                                                        ShareFragment.ShareListener {

    private final int ACCESS_PERMISSION_DENIED = 101;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 201;

    private final String TAG = "CreateActivity";

    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mEditor;

    //UI widgets
    private ViewPager mViewPager;
    private TextView mNext;
    private TextView mPrev;

    private int mTracker = 0;
    private static final int NUM_ITEMS = 3;
    private MyAdapter mAdapter;
    FragmentManager mFragmentManager;

    private CoWork mCoWork;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPrev = (TextView) findViewById(R.id.textViewPrevious);
        mNext = (TextView) findViewById(R.id.textViewNext);

        mPrev.setOnClickListener(CreateActivity.this);
        mNext.setOnClickListener(CreateActivity.this);

        mFragmentManager = getSupportFragmentManager();
        mViewPager.setOffscreenPageLimit(3);
        mAdapter = new MyAdapter(mFragmentManager);

        mSharedPref = getSharedPreferences(getString(R.string.create_cowork_shared_pref), Context.MODE_PRIVATE);
        int p = mSharedPref.getInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_UNGRANTED);
        if(p == 0){
            checkPermission();
        }
        else{
            mViewPager.setAdapter(mAdapter);
        }

        mCoWork = new CoWork();

        mCoWork.setCreatorID(HomeActivity.USER_ID);
    }

    @Override
    public void setLocation(String address, LatLng latLng) {
        mCoWork.setLocationName(address);
        mCoWork.setLocationLat(String.valueOf(latLng.latitude));
        mCoWork.setLocationLng(String.valueOf(latLng.longitude));
    }

    @Override
    public void shareEvent(int event) {

    }

    @Override
    public void setActivityType(int activityType) {
        mCoWork.setActivityType(activityType);
    }

    @Override
    public void setDescription(String description) {
        mCoWork.setDescription(description);
    }

    @Override
    public void setNumAttendees(int numAttendees) {
        mCoWork.setNumAttendees(numAttendees);
    }

    @Override
    public void setTime(String time) {
        mCoWork.setTime(time);
    }

    @Override
    public void setDate(String date) {
        mCoWork.setDate(date);
    }

    public static class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {

            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0){
                return SelectLocationFragment.newInstance();
            }
            else if(position == 1){
                return DetailsFragment.newInstance();
            }
            else{
                return ShareFragment.newInstance();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textViewPrevious:
                if(mTracker > 0){
                    mViewPager.setCurrentItem(--mTracker);
                }
                if(mTracker == 0){
                    mPrev.setEnabled(false);
                }
                break;

            case R.id.textViewNext:
                ++mTracker;
                if(mTracker > 0 && mTracker < 3){
                    mViewPager.setCurrentItem(mTracker);
                    if(mTracker == 2){
                        mNext.setText("Done");
                    }
                    else{
                        mNext.setText("Next");
                    }
                }
                if(mTracker > 0){
                    mPrev.setEnabled(true);
                }

                if(mTracker == 3){
                    HelperClass helperClass = new HelperClass(CreateActivity.this);
                    ProgressDialog pd = ProgressDialog.show(CreateActivity.this, "CoWork", "Saving...", false, false);
                    if(helperClass.saveCoworkToDatabase(mCoWork) == 1){
                        if(pd != null){
                            pd.cancel();
                        }
                        finish();
                    }
                }

                break;
        }
    }


    private void checkPermission(){
        //Check app permission for accessing location
        int permissionCheck = ContextCompat.checkSelfPermission(CreateActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(CreateActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.

                    Log.d(TAG, "permission granted");

                    mEditor = mSharedPref.edit();
                    mEditor.putInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_GRANTED);
                    mEditor.commit();
                    mViewPager.setAdapter(mAdapter);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    mEditor = mSharedPref.edit();
                    mEditor.putInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_UNGRANTED);
                    mEditor.commit();

                    //finish this activity as user has denied permission
                    // TODO: 12/14/2015 Remove this later and let user type an address instead of using current location
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}