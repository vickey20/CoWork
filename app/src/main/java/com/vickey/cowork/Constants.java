package com.vickey.cowork;

/**
 * Created by vikram on 11/19/2015.
 */
public class Constants {

    public static final String API_KEY = "AIzaSyBzu2n3k9Nj3utGpBMcz-kBmFYEqBn_egM";

    class Login{
        static final int LOGIN_FALSE = 0;
        static final int LOGIN_TRUE = 1;
    }

    class LoginType{
        static final int LOGIN_TYPE_NEW_REGISTER = 1;
        static final int LOGIN_TYPE_FACEBOOK = 2;
        static final int LOGIN_TYPE_TWITTER = 3;
    }

    class PreferenceKeys{
        static final String KEY_LOGIN_FLAG = "LOGIN_FLAG";
        static final String KEY_LOGIN_USERNAME = "LOGIN_USERNAME";
        static final String KEY_LOGIN_PASSWORD = "LOGIN_PASSWORD";

        static final String DATABASE_CREATION_FLAG = "DATABASE_CREATION_FLAG";
        static final String TABLE_COWORK_POPULATED_FLAG = "TABLE_COWORK_POPULATED_FLAG";

        static final String KEY_USER_LOGIN_TYPE = "USER_LOGIN_TYPE";
        static final String KEY_USER_ID = "USER_ID";
        static final String KEY_USER_NAME = "USER_NAME";
        static final String KEY_USER_GENDER = "USER_GENDER";
        static final String KEY_USER_PROFESSION = "USER_PROFESSION";
        static final String KEY_USER_EMAIL = "USER_EMAIL";
        static final String KEY_USER_AGE = "USER_AGE";
        static final String KEY_USER_BIRTHDAY = "USER_BIRTHDAY";

        static final String KEY_PERMISSION_ACCESS_FINE_LOCATION = "PERMISSION_ACCESS_FINE_LOCATION";
        static final String KEY_CHANGE_LOCATION_SETTINGS = "CHANGE_LOCATION_SETTINGS";
    }

    class Permissions{
        static final int PERMISSION_UNGRANTED = 0;
        static final int PERMISSION_GRANTED = 1;
        static final int NEVER = 2;
    }

    class Urls{
        static final String API_GET_PLACE_FROM_LOCATION = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    }

    class MyDatabase{

        static final int DATABASE_NOT_CREATED = 0;
        static final int DATABASE_CREATED = 1;
        static final int COWORK_TABLE_NOT_POPULATED = 0;
        static final int COWORK_TABLE_POPULATED = 1;

        static final String DATABASE_NAME = "COWORK_DATABASE";
        static final String TABLE_NAME_COWORK = "USER_COWORK_LIST";

        static final String FIELD_COWORK_ID = "coworkId";
        static final String FIELD_CREATOR_ID = "creatorId";
        static final String FIELD_ATTENDEES_ID = "attendeesId";
        static final String FIELD_NUM_ATTENDEES = "numAttendees";
        static final String FIELD_lOCATION_NAME = "locationName";
        static final String FIELD_LOCATION_LATITUDE = "locationLat";
        static final String FIELD_LOCATION_LONGITUDE = "locationLng";
        static final String FIELD_TIME = "time";
        static final String FIELD_DATE = "date";
        static final String FIELD_ACTIVITY_TYPE = "activityType";
        static final String FIELD_DESCRIPTION = "description";
        static final String FIELD_FINISHED = "finished";
        static final String FIELD_CANCELED = "canceled";

        static final String QUERY_CREATE_TABLE_COWORK = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_COWORK+"(" +
                                                        FIELD_COWORK_ID + " INTEGER, " +
                                                        FIELD_CREATOR_ID + " VARCHAR, " +
                                                        FIELD_ATTENDEES_ID + " VARCHAR, " +
                                                        FIELD_NUM_ATTENDEES + " INTEGER, " +
                                                        FIELD_lOCATION_NAME + " VARCHAR, " +
                                                        FIELD_LOCATION_LATITUDE + " VARCHAR, " +
                                                        FIELD_LOCATION_LONGITUDE + " VARCHAR, " +
                                                        FIELD_TIME + " VARCHAR, " +
                                                        FIELD_DATE + " VARCHAR, " +
                                                        FIELD_ACTIVITY_TYPE + " INTEGER, " +
                                                        FIELD_DESCRIPTION + " VARCHAR, " +
                                                        FIELD_FINISHED + " INTEGER, " +
                                                        FIELD_CANCELED + " INTEGER);";
    }
}
