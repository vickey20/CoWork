package com.vickey.cowork;

import java.io.Serializable;

/**
 * Created by vikramgupta on 4/20/16.
 */
public class UserProfileList implements Serializable {

    int coworkID;
    String userID;

    public UserProfileList() {
        coworkID = -1;
        userID = "";
    }

    public int getCoworkID() {
        return coworkID;
    }

    public void setCoworkID(int coworkID) {
        this.coworkID = coworkID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
