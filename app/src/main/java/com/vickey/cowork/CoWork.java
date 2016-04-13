package com.vickey.cowork;

import java.io.Serializable;

/**
 * Created by vikram on 11/20/2015.
 */
public class CoWork implements Serializable{

    int coworkID;
    String creatorID;
    String attendeesID;
    int numAttendees;
    String locationName;
    String locationLat;
    String locationLng;
    String time;
    String date;
    int activityType;
    String description;
    int finished;
    int canceled;

    public CoWork(){
        coworkID = 0;
        creatorID = "";
        attendeesID = "";
        locationName = "";
        locationLat = "";
        locationLng = "";
        numAttendees = 0;
        time = "";
        date = "";
        activityType = 0;
        description = "";
        finished = 0;
        canceled = 0;
    }

    public int getActivityType() {
        return activityType;
    }
    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public int getCoworkID() {
        return coworkID;
    }

    public void setCoworkID(int coworkID) {
        this.coworkID = coworkID;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public int getNumAttendees() {
        return numAttendees;
    }

    public void setNumAttendees(int numAttendees) {
        this.numAttendees = numAttendees;
    }

    public void setAttendeesID(String attendeesID) {
        this.attendeesID = attendeesID;
    }

    public String getAttendeesID() {
        return attendeesID;
    }

    public void setCanceled(int canceled) {
        this.canceled = canceled;
    }

    public int getCanceled() {
        return canceled;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(String locationLng) {
        this.locationLng = locationLng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
