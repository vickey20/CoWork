package com.vickey.cowork;

/**
 * Created by vikramgupta on 4/20/16.
 */
public class CoworkBundle {

    CoWork coWork;
    UserProfile userProfile;

    public CoworkBundle() {
        coWork = new CoWork();
        userProfile = new UserProfile();
    }

    public CoWork getCoWork() {
        return coWork;
    }

    public void setCoWork(CoWork coWork) {
        this.coWork = coWork;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
