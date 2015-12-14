package com.vickey.cowork;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    //userID should be accessible everywhere.
    static int USER_ID;

    //Shared preferences to retrieve userID
    SharedPreferences mSharedPreferences;

    //UI widgets
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ScrollView mScrollView;
    LinearLayout mLinearRecycler;
    Button mButtonCreate;
    Button mButtonDiscover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Get userID from preferences
        mSharedPreferences = getSharedPreferences(getString(R.string.login_shared_pref), Context.MODE_PRIVATE);
        USER_ID = mSharedPreferences.getInt(Constants.PreferenceKeys.KEY_USER_ID, 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mScrollView = (ScrollView) findViewById(R.id.scrollViewCoworkingSpace);

        mButtonCreate = (Button) findViewById(R.id.buttonCreateCowork);
        mButtonDiscover = (Button) findViewById(R.id.buttonDiscoverCowork);

        mButtonCreate.setOnClickListener(HomeActivity.this);
        mButtonDiscover.setOnClickListener(HomeActivity.this);

        /**
         * If user is associated with any coworking space,
         * show the recycler view, else show the starting template.
         */

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CardViewAdapter();
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.buttonCreateCowork:
                intent = new Intent(HomeActivity.this, CreateActivity.class);
                startActivity(intent);
                break;

            case R.id.buttonDiscoverCowork:
                intent = new Intent(HomeActivity.this, DiscoverActivity.class);
                startActivity(intent);
                break;
        }
    }
}
