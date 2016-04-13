package com.vickey.cowork;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.vickey.cowork.activity.HomeActivity;
import com.vickey.cowork.activity.LoginActivity;
import com.vickey.cowork.utilities.Constants;

/**
 * Created by vikramgupta on 4/9/16.
 */
public class CoworkApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //get shared preference reference.
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.login_shared_pref), Context.MODE_PRIVATE);

        //If already logged in, launch HomeActivity.
        if(sharedPref.getInt(Constants.PreferenceKeys.KEY_LOGIN_FLAG, Constants.Login.LOGIN_FALSE) == Constants.Login.LOGIN_FALSE){
             startActivity(new Intent(CoworkApp.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }
}
