package com.vickey.cowork;

import java.io.Serializable;

/**
 * Created by vikram on 11/20/2015.
 */
public class CoWork implements Serializable {

    int coworkID;
    String creatorID;
    String attendeesID;
    int numAttendees;
    String locationName;
    Double locationLat;
    Double locationLng;
    String time;
    String date;
    long duration;
    int activityType;
    String description;
    int finished;
    int canceled;
    int autoCheckin;

    public CoWork(){
        coworkID = 0;
        creatorID = "";
        attendeesID = "";
        locationName = "";
        locationLat = 0.0;
        locationLng = 0.0;
        numAttendees = 0;
        time = "";
        date = "";
        duration = 1 * 60 * 60 * 1000;
        activityType = 0;
        description = "";
        finished = 0;
        canceled = 0;
        autoCheckin = 0;
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

    public Double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    public Double getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(Double locationLng) {
        this.locationLng = locationLng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAutoCheckin() {
        return autoCheckin;
    }

    public void setAutoCheckin(int autoCheckin) {
        this.autoCheckin = autoCheckin;
    }
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
