package com.vickey.cowork.activity;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.vickey.cowork.CoWork;
import com.vickey.cowork.PlaceInfo;
import com.vickey.cowork.fragment.DetailsFragment;
import com.vickey.cowork.R;
import com.vickey.cowork.fragment.SelectLocationFragment;
import com.vickey.cowork.fragment.ShareFragment;
import com.vickey.cowork.utilities.CustomViewPager;
import com.vickey.cowork.utilities.HelperClass;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener,
        SelectLocationFragment.SelectLocationListener,
        DetailsFragment.DetailsListener,
        ShareFragment.ShareListener {

    private final String TAG = "CreateActivity";

    //UI widgets
    private CustomViewPager mViewPager;
    private TextView mNext;
    private TextView mPrev;

    private int mTracker = 0;
    private static final int NUM_ITEMS = 3;
    private MyAdapter mAdapter;
    FragmentManager mFragmentManager;

    public static CoWork mCoWork;

    private boolean isLocationSet;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
        getSupportActionBar().setTitle(R.string.select_location_fragment);

        mPrev = (TextView) findViewById(R.id.textViewPrevious);
        mNext = (TextView) findViewById(R.id.textViewNext);

        mViewPager.setPagingEnabled(false);

        mPrev.setOnClickListener(CreateActivity.this);
        mNext.setOnClickListener(CreateActivity.this);

        mPrev.setVisibility(TextView.GONE);

        mFragmentManager = getSupportFragmentManager();
        mViewPager.setOffscreenPageLimit(3);
        mAdapter = new MyAdapter(mFragmentManager);

        mViewPager.setAdapter(mAdapter);

        mCoWork = new CoWork();

        mCoWork.setCreatorID(HomeActivity.USER_ID);
    }

    @Override
    public void onLocationSet(PlaceInfo placeInfo) {
        Log.d(TAG, "onLocationSet: " + placeInfo.getAddress());
        if (placeInfo.getName() != null && placeInfo.getName().equals("") == false) {
            mCoWork.setLocationName(placeInfo.getName() + ". " + placeInfo.getAddress());
        } else {
            mCoWork.setLocationName("Address: " + placeInfo.getAddress());
        }
        mCoWork.setLocationLat(String.valueOf(placeInfo.getLatLng().latitude));
        mCoWork.setLocationLng(String.valueOf(placeInfo.getLatLng().longitude));

        isLocationSet = true;
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
                break;

            case R.id.textViewNext:
                if(mTracker == 0 && isLocationSet == false) {
                    Toast.makeText(CreateActivity.this, "Please select a location...", Toast.LENGTH_LONG).show();
                }
                else {
                    ++mTracker;
                    if (mTracker > 0 && mTracker < 3) {
                        mViewPager.setCurrentItem(mTracker);
                    }

                    if (mTracker == 3) {
                        HelperClass helperClass = new HelperClass(getApplicationContext());
                        ProgressDialog pd = ProgressDialog.show(CreateActivity.this, "CoWork", "Saving...", false, false);
                        if (helperClass.saveCoworkToDatabase(mCoWork) == 1) {
                            if (pd != null) {
                                pd.cancel();
                            }
                            finish();
                        }
                    }
                }
                break;
        }

        if(mTracker == 0) {
            getSupportActionBar().setTitle(R.string.select_location_fragment);
            mPrev.setVisibility(TextView.GONE);
            mNext.setText("Next");
        } else if(mTracker == 1) {
            getSupportActionBar().setTitle(R.string.details_fragment);
            mPrev.setVisibility(TextView.VISIBLE);
            mNext.setText("Next");
        } else if(mTracker == 2) {
            getSupportActionBar().setTitle(R.string.share_fragment);
            mPrev.setVisibility(TextView.VISIBLE);
            mNext.setText("Done");
        }
    }

}