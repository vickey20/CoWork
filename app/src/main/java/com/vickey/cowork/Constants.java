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
        static final String KEY_USER_ID = "USER_ID";
        static final String KEY_USER_NAME = "USER_NAME";
        static final String KEY_USER_GENDER = "USER_GENDER";
        static final String KEY_USER_PROFESSION = "USER_PROFESSION";
        static final String KEY_USER_EMAIL = "USER_EMAIL";
        static final String KEY_USER_BIRTHDAY = "USER_BIRTHDAY";
        static final String KEY_USER_LOGIN_TYPE = "USER_LOGIN_TYPE";
        static final String KEY_PERMISSION_ACCESS_FINE_LOCATION = "PERMISSION_ACCESS_FINE_LOCATION";
        static final String KEY_CHANGE_LOCATION_SETTINGS = "CHANGE_LOCATION_SETTINGS";
    }

    class Permissions{
        static final int PERMISSION_UNGRANTED = 0;
        static final int PERMISSION_GRANTED = 1;
        static final int NEVER = 2;
    }

    class Urls{
        static final String API_GET_PLACE_FROM_LOCATION = "https://www.maps.googleapis.com/maps/api/place/nearbysearch/json?";
    }
}
