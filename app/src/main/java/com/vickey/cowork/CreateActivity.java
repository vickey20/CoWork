package com.vickey.cowork;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener,
                                                        SelectLocationFragment.OnFragmentInteractionListener,
                                                        DetailsFragment.OnFragmentInteractionListener,
                                                        ShareFragment.OnFragmentInteractionListener {

    //UI widgets
    ViewPager mViewPager;
    TextView mNext;
    TextView mPrev;

    int count = 0;
    int tracker = 0;
    static final int NUM_ITEMS = 3;
    MyAdapter mAdapter;
    int currentItem;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPrev = (TextView) findViewById(R.id.textViewPrevious);
        mNext = (TextView) findViewById(R.id.textViewNext);

        mPrev.setOnClickListener(CreateActivity.this);
        mNext.setOnClickListener(CreateActivity.this);

        fm = getSupportFragmentManager();
        mViewPager.setOffscreenPageLimit(3);
        mAdapter = new MyAdapter(fm);

        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

                break;

            case R.id.textViewNext:

                break;
        }
    }
}