package com.vickey.cowork.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.vickey.cowork.adapter.CardViewAdapter;
import com.vickey.cowork.CoWork;
import com.vickey.cowork.R;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.HelperClass;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    //userID should be accessible everywhere.
    static String USER_ID;

    //Shared preferences to retrieve userID
    SharedPreferences mLoginSharedPref, mDatabaseSharedPref;

    //UI widgets
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ScrollView mScrollView;
    LinearLayout mLinearRecycler;
    Button mButtonCreate;
    Button mButtonDiscover;
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
            HelperClass helperClass = new HelperClass(HomeActivity.this);
            if(helperClass.initializeDatabase() == 1){
                SharedPreferences.Editor editor = mDatabaseSharedPref.edit();
                editor.putInt(Constants.PreferenceKeys.DATABASE_CREATION_FLAG, Constants.MyDatabase.DATABASE_CREATED);
            }
        }

        mCardViewStartup = (CardView) findViewById(R.id.cardViewStartup);
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

    }

    @Override
    protected void onResume() {
        super.onResume();

        HelperClass helperClass = new HelperClass(HomeActivity.this);
        ArrayList<CoWork> coWorks = helperClass.getUserCoworkList();

        if(coWorks.size() > 0){
            mAdapter = new CardViewAdapter(HomeActivity.this, coWorks);
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
                startActivity(new Intent(HomeActivity.this, InstantCreateActivity.class));
                return true;
        }

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
