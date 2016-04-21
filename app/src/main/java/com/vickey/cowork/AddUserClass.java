package com.vickey.cowork;

import java.io.Serializable;

/**
 * Created by vikramgupta on 4/20/16.
 */
public class AddUserClass implements Serializable{

    int coworkID;
    String userID;

    public AddUserClass() {
        coworkID = -1;
        userID = "";
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getCoworkID() {
        return coworkID;
    }

    public void setCoworkID(int coworkID) {
        this.coworkID = coworkID;
    }
}
