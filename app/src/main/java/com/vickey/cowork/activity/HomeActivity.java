package com.vickey.cowork.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vickey.cowork.adapter.CardViewAdapter;
import com.vickey.cowork.CoWork;
import com.vickey.cowork.R;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.HelperClass;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "HomeActivity";

    //userID should be accessible everywhere.
    static String USER_ID;

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 201;

    //Shared preferences to retrieve userID
    SharedPreferences mLoginSharedPref, mDatabaseSharedPref, mDefaultSharedPref;
    SharedPreferences.Editor mEditor;

    //UI widgets
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ScrollView mScrollView;
    TextView mTextViewCreate, mTextViewDiscover;
    CardView mCardViewStartup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        //Get userID from preferences
        mLoginSharedPref = getSharedPreferences(getString(R.string.login_shared_pref), Context.MODE_PRIVATE);
        USER_ID = mLoginSharedPref.getString(Constants.PreferenceKeys.KEY_USER_ID, "");

        mDatabaseSharedPref = getSharedPreferences(getString(R.string.database_shared_pref), Context.MODE_PRIVATE);
        if(mDatabaseSharedPref.getInt(Constants.PreferenceKeys.DATABASE_CREATION_FLAG, 0) == Constants.MyDatabase.DATABASE_NOT_CREATED){
            HelperClass helperClass = new HelperClass(getApplicationContext());
            if(helperClass.initializeDatabase() == 1){
                SharedPreferences.Editor editor = mDatabaseSharedPref.edit();
                editor.putInt(Constants.PreferenceKeys.DATABASE_CREATION_FLAG, Constants.MyDatabase.DATABASE_CREATED);
            }
        }

        mDefaultSharedPref = getSharedPreferences(getString(R.string.create_cowork_shared_pref), Context.MODE_PRIVATE);
        int p = mDefaultSharedPref.getInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_DENIED);
        if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && p == 0){
            checkLocationPermission();
        }

        mCardViewStartup = (CardView) findViewById(R.id.cardViewStartup);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mScrollView = (ScrollView) findViewById(R.id.scrollViewCoworkingSpace);

        mTextViewCreate = (TextView) findViewById(R.id.textViewCreateCowork);
        mTextViewDiscover = (TextView) findViewById(R.id.textViewDiscoverCowork);

        mTextViewCreate.setOnClickListener(HomeActivity.this);
        mTextViewDiscover.setOnClickListener(HomeActivity.this);

        /**
         * If user is associated with any coworking space,
         * show the recycler view, else show the starting template.
         */

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //HelperClass.setCoworkAlarm(getApplicationContext(), -1, 10 * 1000);

        HelperClass.getTimeInMillis("Sat, Apr 9 22:10 PM", Constants.TimeAndDate.DATE_FORMAT + " " + Constants.TimeAndDate.TIME_FORMAT);

    }

    @Override
    protected void onResume() {
        super.onResume();
        HelperClass helperClass = new HelperClass(getApplicationContext());
        ArrayList<CoWork> coWorks = helperClass.getUserCoworkList();

        if(coWorks.size() > 0){
            mAdapter = new CardViewAdapter(getApplicationContext(), coWorks);
            mRecyclerView.setAdapter(mAdapter);
            mCardViewStartup.setVisibility(CardView.GONE);
            mRecyclerView.setVisibility(RecyclerView.VISIBLE);
        }
        else{
            mRecyclerView.setVisibility(RecyclerView.GONE);
            mCardViewStartup.setVisibility(CardView.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                Intent i = new Intent(HomeActivity.this, ViewProfileActivity.class);
                startActivity(i);
                return true;
            case R.id.instantCowork:
                int p = mDefaultSharedPref.getInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_DENIED);
                if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && p == 0){
                    checkLocationPermission();
                } else {
                    startActivity(new Intent(HomeActivity.this, InstantCreateActivity.class));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int p = mDefaultSharedPref.getInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_DENIED);
        switch (v.getId()){
            case R.id.textViewCreateCowork:
                if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && p == 0){
                    checkLocationPermission();
                } else {
                    intent = new Intent(HomeActivity.this, CreateActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.textViewDiscoverCowork:
                if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && p == 0){
                    checkLocationPermission();
                } else {
                    intent = new Intent(HomeActivity.this, DiscoverActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private void checkLocationPermission(){
        //Check app permission for accessing location
        int permissionCheck = ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Log.d(TAG, "checkLocationPermission:: shouldShowRequestPermissionRationale");
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            } else {
                Log.d(TAG, "checkLocationPermission:: requestPermissions");
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(HomeActivity.this,
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

                    Log.d(TAG, "permission granted");

                    mEditor = mDefaultSharedPref.edit();
                    mEditor.putInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_GRANTED);
                    mEditor.commit();
                } else {

                    mEditor = mDefaultSharedPref.edit();
                    mEditor.putInt(Constants.PreferenceKeys.KEY_PERMISSION_ACCESS_FINE_LOCATION, Constants.Permissions.PERMISSION_DENIED);
                    mEditor.commit();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
