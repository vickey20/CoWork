package com.vickey.cowork.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vickey.cowork.CoWork;
import com.vickey.cowork.PlaceInfo;
import com.vickey.cowork.fragment.CoworkHistoryFragment;
import com.vickey.cowork.fragment.DetailsFragment;
import com.vickey.cowork.R;
import com.vickey.cowork.fragment.SelectLocationFragment;
import com.vickey.cowork.fragment.ShareFragment;
import com.vickey.cowork.receiver.IntentServiceReceiver;
import com.vickey.cowork.service.CoworkIntentService;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.CustomViewPager;
import com.vickey.cowork.utilities.HelperClass;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener,
        SelectLocationFragment.SelectLocationListener,
        CoworkHistoryFragment.CoworkHistoryListener,
        DetailsFragment.DetailsListener,
        ShareFragment.ShareListener,
        IntentServiceReceiver.Receiver {

    private final String TAG = "CreateActivity";
    public static final String LAUNCH_MODE = "LAUNCH_MODE";

    public static final int LAUNCH_MODE_NEW_COWORK = 1;
    public static final int LAUNCH_MODE_EXISTING_COWORK = 2;

    //UI widgets
    private CustomViewPager mViewPager;
    private TextView mNext;
    private TextView mPrev;

    private int mTracker = 0;
    private static final int NUM_ITEMS = 3;
    private MyAdapter mAdapter;
    FragmentManager mFragmentManager;

    public static CoWork mCoWork;

    private boolean isLocationSet, isCoworkSelected;

    ProgressDialog mProgressDialog;

    public static int mLaunchMode = 1;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        HelperClass.deleteCache(getApplicationContext());

        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
        getSupportActionBar().setTitle(R.string.select_location_fragment);

        mPrev = (TextView) findViewById(R.id.textViewPrevious);
        mNext = (TextView) findViewById(R.id.textViewNext);

        mViewPager.setPagingEnabled(false);

        mPrev.setOnClickListener(CreateActivity.this);
        mNext.setOnClickListener(CreateActivity.this);

        mPrev.setVisibility(TextView.GONE);

        mFragmentManager = getSupportFragmentManager();
        mAdapter = new MyAdapter(mFragmentManager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mLaunchMode = extras.getInt(LAUNCH_MODE);
        }

        mCoWork = new CoWork();
        mCoWork.setCreatorID(HomeActivity.USER_ID);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public void onLocationSet(PlaceInfo placeInfo) {
        Log.d(TAG, "onLocationSet: " + placeInfo.getAddress());
        if (placeInfo.getName() != null && placeInfo.getName().equals("") == false) {
            mCoWork.setLocationName(placeInfo.getName() + "\n" + placeInfo.getAddress());
        } else {
            mCoWork.setLocationName("Address: " + placeInfo.getAddress());
        }
        mCoWork.setLocationLat(placeInfo.getLatLng().latitude);
        mCoWork.setLocationLng(placeInfo.getLatLng().longitude);

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

    @Override
    public void onCoworkSelected(CoWork cowork) {
        mCoWork = cowork;
        isCoworkSelected = true;
        Log.d(TAG, "onCoworkSelected()");
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

            if (position == 0 && mLaunchMode == LAUNCH_MODE_NEW_COWORK){
                return SelectLocationFragment.newInstance();
            }
            else if (position == 0 && mLaunchMode == LAUNCH_MODE_EXISTING_COWORK) {
                return CoworkHistoryFragment.newInstance();
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
                if(mTracker == 0 && mLaunchMode == LAUNCH_MODE_NEW_COWORK && isLocationSet == false) {
                    Toast.makeText(CreateActivity.this, "Please select a location...", Toast.LENGTH_LONG).show();
                }
                else if (mTracker == 0 && mLaunchMode == LAUNCH_MODE_EXISTING_COWORK && isCoworkSelected == false) {
                    Toast.makeText(CreateActivity.this, "Please select a cowork...", Toast.LENGTH_LONG).show();
                }
                else {
                    ++mTracker;
                    if (mTracker > 0 && mTracker < 3) {
                        mViewPager.setCurrentItem(mTracker);
                    }

                    if (mTracker == 3) {
                        mProgressDialog = ProgressDialog.show(CreateActivity.this, "CoWork", "Saving...", false, false);
                        sendCoworkToServer(mCoWork);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Handle fragment's result callbacks here.
        if (requestCode == Constants.ActivityConstants.REQUEST_CHECK_SETTINGS) {
            SelectLocationFragment fragment = (SelectLocationFragment) mAdapter.getItem(0);
            fragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendCoworkToServer(CoWork coWork) {
        /* Starting Download Service */
        IntentServiceReceiver receiver = new IntentServiceReceiver(new Handler());
        receiver.setReceiver(CreateActivity.this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, CoworkIntentService.class);

            /* Send optional extras to Download IntentService */
        intent.putExtra(CoworkIntentService.COWORK, coWork);
        intent.putExtra(CoworkIntentService.RECEIVER, receiver);
        intent.putExtra(CoworkIntentService.REQUEST_ID, Constants.Request.COWORK_REQUEST);
        intent.putExtra(CoworkIntentService.REQUEST_TYPE, Constants.Request.SEND_COWORK_TO_SERVER);

        startService(intent);
    }

    private void saveCoworkToDatabase(CoWork coWork) {
        HelperClass helperClass = new HelperClass(getApplicationContext());
        if(helperClass.saveCoworkToDatabase(mCoWork) != 1) {
            Log.d(TAG, "Error saving locally");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(TAG, "onReceiveResult:: resultCode: " + resultCode + "; resultData: " + resultData);

        switch (resultCode) {
            case CoworkIntentService.STATUS_RUNNING:

                break;

            case CoworkIntentService.STATUS_FINISHED:
                Toast.makeText(CreateActivity.this, "Saved successfully!", Toast.LENGTH_LONG).show();
                if (resultData != null) {
                    int requestType = resultData.getInt(CoworkIntentService.REQUEST_TYPE);
                    if (requestType == Constants.Request.SEND_COWORK_TO_SERVER) {
                        int coworkId = resultData.getInt(CoworkIntentService.RESULT, -1);
                        mCoWork.setCoworkID(coworkId);
                        saveCoworkToDatabase(mCoWork);
                    }
                }
                finish();
                break;

            case CoworkIntentService.STATUS_ERROR:
                Toast.makeText(CreateActivity.this, "Error saving CoWork. Please try again...", Toast.LENGTH_LONG).show();
                break;
        }

        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
    }
}