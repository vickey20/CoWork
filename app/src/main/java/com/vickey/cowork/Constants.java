package com.vickey.cowork;

/**
 * Created by vikram on 11/19/2015.
 */
public class Constants {

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
    }
}
