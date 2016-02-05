package com.vickey.cowork.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.vickey.cowork.CoWork;
import com.vickey.cowork.UserProfile;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vikram on 11/19/2015.
 */
public class HelperClass {

    private final String TAG = "HelperClass";

    private SharedPreferences mSp;
    private SharedPreferences.Editor mEditor;

    private SQLiteDatabase mDatabase;

    private Context mContext;

    public HelperClass(Context context){
        mContext = context;
        mSp = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSp.edit();
    }

    public int initializeDatabase() {

        try {

            Log.d(TAG, "initializeDatabase() :: creating database: " + Constants.MyDatabase.QUERY_CREATE_TABLE_COWORK);
            mDatabase = mContext.openOrCreateDatabase(Constants.MyDatabase.DATABASE_NAME, mContext.MODE_PRIVATE, null);

            mDatabase.execSQL(Constants.MyDatabase.QUERY_CREATE_TABLE_COWORK);
            mDatabase.execSQL(Constants.MyDatabase.QUERY_CREATE_TABLE_USER_PROFILE);

            mDatabase.close();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public void deleteDatabase(){
        mContext.deleteDatabase(Constants.MyDatabase.DATABASE_NAME);
        mEditor.putInt(Constants.PreferenceKeys.DATABASE_CREATION_FLAG, 0);
        mEditor.putInt(Constants.PreferenceKeys.TABLE_COWORK_POPULATED_FLAG, 0);
        mEditor.commit();

        Toast.makeText(mContext, "Database recreated", Toast.LENGTH_LONG).show();
    }

    public ArrayList<CoWork> getUserCoworkList() {

        ArrayList<CoWork> dataList = new ArrayList<CoWork>();

        try {
            mDatabase = mContext.openOrCreateDatabase(Constants.MyDatabase.DATABASE_NAME, mContext.MODE_PRIVATE, null);
            Cursor cur = mDatabase.rawQuery("SELECT * FROM "+Constants.MyDatabase.TABLE_NAME_COWORK, null);

            if(cur != null)
            {
                //cur.moveToNext();

                if(cur.moveToFirst())
                {
                    do
                    {
                        CoWork coWork = new CoWork();
                        coWork.setCoworkID(cur.getInt(cur.getColumnIndex(Constants.MyDatabase.FIELD_COWORK_ID)));
                        coWork.setCreatorID(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_CREATOR_ID)));
                        coWork.setAttendeesID(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_ATTENDEES_ID)));
                        coWork.setNumAttendees(cur.getInt(cur.getColumnIndex(Constants.MyDatabase.FIELD_NUM_ATTENDEES)));
                        coWork.setLocationName(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_lOCATION_NAME)));
                        coWork.setLocationLat(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_LOCATION_LATITUDE)));
                        coWork.setLocationLng(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_LOCATION_LONGITUDE)));
                        coWork.setTime(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_TIME)));
                        coWork.setDate(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_DATE)));
                        coWork.setActivityType(cur.getInt(cur.getColumnIndex(Constants.MyDatabase.FIELD_ACTIVITY_TYPE)));
                        coWork.setDescription(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_DESCRIPTION)));
                        coWork.setFinished(cur.getInt(cur.getColumnIndex(Constants.MyDatabase.FIELD_FINISHED)));
                        coWork.setCanceled(cur.getInt(cur.getColumnIndex(Constants.MyDatabase.FIELD_CANCELED)));

                        dataList.add(coWork);
                        Log.d(TAG, "Location name: " + coWork.getLocationName());
                        Log.d(TAG, "Location activity: " + coWork.getActivityType());

                    }while(cur.moveToNext());
                }

                cur.close();

            }

            mDatabase.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return dataList;
    }

    public int saveCoworkToDatabase(CoWork coWork){

        try {

            ContentValues values = new ContentValues();

            mDatabase = mContext.openOrCreateDatabase(Constants.MyDatabase.DATABASE_NAME, mContext.MODE_PRIVATE, null);

            values.put(Constants.MyDatabase.FIELD_COWORK_ID, coWork.getCoworkID());
            values.put(Constants.MyDatabase.FIELD_CREATOR_ID, coWork.getCreatorID());
            values.put(Constants.MyDatabase.FIELD_ATTENDEES_ID, coWork.getAttendeesID());
            values.put(Constants.MyDatabase.FIELD_NUM_ATTENDEES, coWork.getNumAttendees());
            values.put(Constants.MyDatabase.FIELD_lOCATION_NAME, coWork.getLocationName());
            values.put(Constants.MyDatabase.FIELD_LOCATION_LATITUDE, coWork.getLocationLat());
            values.put(Constants.MyDatabase.FIELD_LOCATION_LONGITUDE, coWork.getLocationLng());
            values.put(Constants.MyDatabase.FIELD_TIME, coWork.getTime());
            values.put(Constants.MyDatabase.FIELD_DATE, coWork.getDate());
            values.put(Constants.MyDatabase.FIELD_ACTIVITY_TYPE, coWork.getActivityType());
            values.put(Constants.MyDatabase.FIELD_DESCRIPTION, coWork.getDescription());
            values.put(Constants.MyDatabase.FIELD_FINISHED, coWork.getFinished());
            values.put(Constants.MyDatabase.FIELD_CANCELED, coWork.getCanceled());

            mDatabase.insert(Constants.MyDatabase.TABLE_NAME_COWORK, null, values);

            mDatabase.close();


        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public int saveUserProfileToDB(UserProfile userProfile){
        try {

            ContentValues values = new ContentValues();

            mDatabase = mContext.openOrCreateDatabase(Constants.MyDatabase.DATABASE_NAME, mContext.MODE_PRIVATE, null);

            values.put(Constants.MyDatabase.FIELD_USER_ID, userProfile.getUserId());
            values.put(Constants.MyDatabase.FIELD_NAME, userProfile.getName());
            values.put(Constants.MyDatabase.FIELD_PROFESSION, userProfile.getProfession());
            values.put(Constants.MyDatabase.FIELD_EMAIL, userProfile.getEmail());
            values.put(Constants.MyDatabase.FIELD_BIRTHDAY, userProfile.getBirthday());
            values.put(Constants.MyDatabase.FIELD_GENDER, userProfile.getGender());

            // Convert the image into byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            userProfile.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, out);
            byte[] buffer = out.toByteArray();

            values.put(Constants.MyDatabase.FIELD_PHOTO, buffer);
            values.put(Constants.MyDatabase.FIELD_LOGINTYPE, userProfile.getLoginType());

            mDatabase.insert(Constants.MyDatabase.TABLE_NAME_USER_PROFILE, null, values);

            mDatabase.close();


        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public UserProfile getUserProfileFromDB(){
        UserProfile userProfile = new UserProfile();

        try {
            mDatabase = mContext.openOrCreateDatabase(Constants.MyDatabase.DATABASE_NAME, mContext.MODE_PRIVATE, null);
            Cursor cur = mDatabase.rawQuery("SELECT * FROM "+Constants.MyDatabase.TABLE_NAME_USER_PROFILE, null);

            if(cur != null)
            {
                //cur.moveToNext();

                if(cur.moveToFirst())
                {
                    do
                    {
                        userProfile.setUserId(cur.getInt(cur.getColumnIndex(Constants.MyDatabase.FIELD_USER_ID)));
                        userProfile.setName(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_NAME)));
                        userProfile.setEmail(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_EMAIL)));
                        userProfile.setProfession(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_PROFESSION)));
                        userProfile.setGender(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_GENDER)));
                        userProfile.setBirthday(cur.getString(cur.getColumnIndex(Constants.MyDatabase.FIELD_BIRTHDAY)));
                        byte[] blob = cur.getBlob(cur.getColumnIndex(Constants.MyDatabase.FIELD_PHOTO));
                        userProfile.setPhoto(BitmapFactory.decodeByteArray(blob, 0, blob.length));
                        userProfile.setLoginType(cur.getInt(cur.getColumnIndex(Constants.MyDatabase.FIELD_LOGINTYPE)));

                    }while(cur.moveToNext());
                }

                cur.close();

            }

            mDatabase.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return userProfile;
    }

    public void uploadDb(){

    }

    public String getPlaceFromLocation(LatLng latLng){

        JSONParser jsonParser = new JSONParser();

        String urlStr = Constants.Urls.API_GET_PLACE_FROM_LOCATION
                + "location=" + latLng.latitude + "," + latLng.longitude
                + "&radius=5000"
                + "&sensor=true"
                + "&key=" + Constants.API_KEY;

        Log.d(TAG, "getPlaceFromLocation: url: " + urlStr);

        String jsonResp = jsonParser.getJSONFromUrl(urlStr);

        Log.d(TAG, "getPlaceFromLocation: response: " + jsonResp);

        return jsonResp;
    }

    public void saveProfileToPreference(UserProfile profile){

        mEditor.putInt(Constants.PreferenceKeys.KEY_LOGIN_FLAG, Constants.Login.LOGIN_TRUE);
        mEditor.putInt(Constants.PreferenceKeys.KEY_USER_LOGIN_TYPE, Constants.LoginType.LOGIN_TYPE_FACEBOOK);

        mEditor.putString(Constants.PreferenceKeys.KEY_USER_NAME, profile.getName());
        mEditor.putString(Constants.PreferenceKeys.KEY_USER_BIRTHDAY, profile.getBirthday());
        mEditor.putString(Constants.PreferenceKeys.KEY_USER_EMAIL, profile.getEmail());
        mEditor.putString(Constants.PreferenceKeys.KEY_USER_GENDER, profile.getGender());
        mEditor.putString(Constants.PreferenceKeys.KEY_USER_PROFESSION, profile.getProfession());

        mEditor.commit();
    }

    public ArrayList<CoWork> getNearbyCoworkList(Location location){
        ArrayList<CoWork> coworkList = new ArrayList<>();

        return coworkList;
    }
}