package com.vickey.cowork;

import com.vickey.cowork.utilities.Constants;

/**
 * Created by vikram on 11/19/2015.
 */
public class UserProfile {

    int userId;
    String name;
    String gender;
    String profession;
    String email;
    String birthday;
    Integer loginType;

    UserProfile(){
        userId = 0;
        name = "";
        gender = "";
        profession = "";
        email = "";
        birthday = "";
        loginType = Constants.LoginType.LOGIN_TYPE_NEW_REGISTER;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Integer getLoginType() {
        return loginType;
    }
    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }
}