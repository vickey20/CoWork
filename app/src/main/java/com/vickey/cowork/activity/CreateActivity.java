package com.vickey.cowork.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.vickey.cowork.CoWork;
import com.vickey.cowork.fragment.DetailsFragment;
import com.vickey.cowork.R;
import com.vickey.cowork.fragment.SelectLocationFragment;
import com.vickey.cowork.fragment.ShareFragment;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.HelperClass;

public class CreateActivity extends FragmentActivity implements View.OnClickListener,
        SelectLocationFragment.SelectLocationListener,
        DetailsFragment.DetailsListener,
        ShareFragment.ShareListener {

    private final String TAG = "CreateActivity";

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

        mViewPager.setAdapter(mAdapter);

        mCoWork = new CoWork();

        mCoWork.setCreatorID(HomeActivity.USER_ID);
    }

    @Override
    public void onLocationSet(String address, LatLng latLng) {
        mCoWork.setLocationName(address);
        mCoWork.setLocationLat(String.valueOf(latLng.latitude));
        mCoWork.setLocationLng(String.valueOf(latLng.longitude));
    }

    @Override
    public void shareEvent(int event) {

    }

    @Override
    public void onActivitySet(int activityType) {
        mCoWork.setActivityType(activityType);
    }

    @Override
    public void onDescriptionSet(String description) {
        mCoWork.setDescription(description);
    }

    @Override
    public void onNumAttendeesSet(int numAttendees) {
        mCoWork.setNumAttendees(numAttendees);
    }

    @Override
    public void onTimeSet(String time) {
        mCoWork.setTime(time);
    }

    @Override
    public void onDateSet(String date) {
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
                    HelperClass helperClass = new HelperClass(getApplicationContext());
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

}