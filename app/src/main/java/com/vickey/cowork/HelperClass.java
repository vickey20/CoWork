package com.vickey.cowork;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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
        /*mSp = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSp.edit();*/
    }

    public int initializeDatabase() {

        try {

            Log.d(TAG, "initializeDatabase() :: creating database: " + Constants.MyDatabase.QUERY_CREATE_TABLE_COWORK);
            mDatabase = mContext.openOrCreateDatabase(Constants.MyDatabase.DATABASE_NAME, mContext.MODE_PRIVATE, null);

            mDatabase.execSQL(Constants.MyDatabase.QUERY_CREATE_TABLE_COWORK);

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
}