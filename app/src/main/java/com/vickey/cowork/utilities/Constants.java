package com.vickey.cowork.utilities;

/**
 * Created by vikram on 11/19/2015.
 */
public class Constants {

    public static final String API_KEY = "AIzaSyBzu2n3k9Nj3utGpBMcz-kBmFYEqBn_egM";

    public class Login{
        public static final int LOGIN_FALSE = 0;
        public static final int LOGIN_TRUE = 1;
    }

    public class LoginType{
        public static final int LOGIN_TYPE_NEW_REGISTER = 1;
        public static final int LOGIN_TYPE_FACEBOOK = 2;
        static final int LOGIN_TYPE_TWITTER = 3;
    }

    public class PreferenceKeys{
        public static final String KEY_LOGIN_FLAG = "LOGIN_FLAG";
        public static final String KEY_LOGIN_USERNAME = "LOGIN_USERNAME";
        public static final String KEY_LOGIN_PASSWORD = "LOGIN_PASSWORD";

        public static final String DATABASE_CREATION_FLAG = "DATABASE_CREATION_FLAG";
        public static final String TABLE_COWORK_POPULATED_FLAG = "TABLE_COWORK_POPULATED_FLAG";

        public static final String KEY_USER_LOGIN_TYPE = "USER_LOGIN_TYPE";
        public static final String KEY_USER_ID = "USER_ID";
        public static final String KEY_USER_NAME = "USER_NAME";
        public static final String KEY_USER_GENDER = "USER_GENDER";
        public static final String KEY_USER_PROFESSION = "USER_PROFESSION";
        public static final String KEY_USER_EMAIL = "USER_EMAIL";
        public static final String KEY_USER_AGE = "USER_AGE";
        public static final String KEY_USER_BIRTHDAY = "USER_BIRTHDAY";

        public static final String KEY_PERMISSION_ACCESS_FINE_LOCATION = "PERMISSION_ACCESS_FINE_LOCATION";
        public static final String KEY_CHANGE_LOCATION_SETTINGS = "CHANGE_LOCATION_SETTINGS";
    }

    public class Permissions{
        public static final int PERMISSION_DENIED = 0;
        public static final int PERMISSION_GRANTED = 1;
        public static final int NEVER = 2;
    }

    public class Urls{
        public static final String API_GET_PLACE_FROM_LOCATION = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    }

    public class MyDatabase{

        public static final int DATABASE_NOT_CREATED = 0;
        public static final int DATABASE_CREATED = 1;
        public static final int COWORK_TABLE_NOT_POPULATED = 0;
        public static final int COWORK_TABLE_POPULATED = 1;

        public static final String DATABASE_NAME = "COWORK_DATABASE";
        public static final String TABLE_NAME_COWORK = "USER_COWORK_LIST";
        public static final String TABLE_NAME_USER_PROFILE = "USER_COWORK_LIST";

        //Table Cowork
        public static final String FIELD_COWORK_ID = "coworkId";
        public static final String FIELD_CREATOR_ID = "creatorId";
        public static final String FIELD_ATTENDEES_ID = "attendeesId";
        public static final String FIELD_NUM_ATTENDEES = "numAttendees";
        public static final String FIELD_lOCATION_NAME = "locationName";
        public static final String FIELD_LOCATION_LATITUDE = "locationLat";
        public static final String FIELD_LOCATION_LONGITUDE = "locationLng";
        public static final String FIELD_TIME = "time";
        public static final String FIELD_DATE = "date";
        public static final String FIELD_ACTIVITY_TYPE = "activityType";
        public static final String FIELD_DESCRIPTION = "description";
        public static final String FIELD_FINISHED = "finished";
        public static final String FIELD_CANCELED = "canceled";

        //Table User Profile
        public static final String FIELD_USER_ID = "userId";
        public static final String FIELD_PASSWORD = "password";
        public static final String FIELD_NAME = "name";
        public static final String FIELD_GENDER = "gender";
        public static final String FIELD_PROFESSION = "profession";
        public static final String FIELD_EMAIL = "email";
        public static final String FIELD_BIRTHDAY = "birthday";
        public static final String FIELD_PHOTO = "photo";
        public static final String FIELD_LOGINTYPE = "loginType";

        public static final String QUERY_CREATE_TABLE_COWORK = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_COWORK+"(" +
                                                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                FIELD_COWORK_ID + " INTEGER, " +
                                                                FIELD_CREATOR_ID + " VARCHAR, " +
                                                                FIELD_ATTENDEES_ID + " VARCHAR, " +
                                                                FIELD_NUM_ATTENDEES + " INTEGER, " +
                                                                FIELD_lOCATION_NAME + " VARCHAR, " +
                                                                FIELD_LOCATION_LATITUDE + " DOUBLE, " +
                                                                FIELD_LOCATION_LONGITUDE + " DOUBLE, " +
                                                                FIELD_TIME + " VARCHAR, " +
                                                                FIELD_DATE + " VARCHAR, " +
                                                                FIELD_ACTIVITY_TYPE + " INTEGER, " +
                                                                FIELD_DESCRIPTION + " VARCHAR, " +
                                                                FIELD_FINISHED + " INTEGER, " +
                                                                FIELD_CANCELED + " INTEGER);";

        public static final String QUERY_CREATE_TABLE_USER_PROFILE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_USER_PROFILE+"(" +
                                                                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    FIELD_USER_ID + " VARCHAR, " +
                                                                    FIELD_PASSWORD + " VARCHAR, " +
                                                                    FIELD_NAME + " VARCHAR, " +
                                                                    FIELD_GENDER + " VARCHAR, " +
                                                                    FIELD_PROFESSION + " VARCHAR, " +
                                                                    FIELD_EMAIL + " VARCHAR, " +
                                                                    FIELD_BIRTHDAY + " VARCHAR, " +
                                                                    FIELD_PHOTO + " BLOB NOT NULL, " +
                                                                    FIELD_LOGINTYPE + " INTEGER);";
    }

    public class ActivityConstants{
        public final static int REQUEST_CHECK_SETTINGS = 100;
        public final static int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 201;
        public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    }

    public class Request{
        public final static int COWORK_REQUEST = 1;
        public final static int USER_REQUEST = 2;
        public final static int GEOFENCE_REQUEST = 3;
        public final static int SEND_COWORK_TO_SERVER = 3;
        public final static int GET_COWORK_FROM_SERVER = 4;
        public final static int GET_ALL_COWORKS_FROM_SERVER = 5;
        public final static int GET_NEARBY_COWORKS_FROM_SERVER = 6;
        public final static int UPDATE_COWORK = 7;
        public final static int ADD_USER_AS_ATTENDEE = 8;
        public final static int SEND_USER_PROFILE_TO_SERVER = 9;
        public final static int UPDATE_USER_PROFILE = 10;
        public final static int GET_USER_PROFILE = 11;
        public final static int GET_CORRESPONDING_USER_PROFILE_LIST = 12;
    }

    public class TimeAndDate {
        public static final String TIME_FORMAT = "hh:mm a";
        public static final String DATE_FORMAT = "EEE, MMM dd";
    }
}
