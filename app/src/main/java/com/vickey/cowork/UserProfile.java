package com.vickey.cowork;

import android.graphics.Bitmap;

import com.vickey.cowork.utilities.Constants;

import java.io.Serializable;

/**
 * Created by vikram on 11/19/2015.
 */
public class UserProfile implements Serializable{

    String userId;
    String password;
    String name;
    String gender;
    String profession;
    String email;
    String birthday;
    Bitmap photo;
    Integer loginType;

    public UserProfile(){
        userId = "";
        password = "";
        name = "";
        gender = "";
        profession = "";
        email = "";
        birthday = "";
        loginType = Constants.LoginType.LOGIN_TYPE_NEW_REGISTER;
        photo = null;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}