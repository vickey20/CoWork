package com.vickey.cowork;

/**
 * Created by ajay on 11/20/2015.
 */
public class CoWork {

    int coworkID;
    int creatorID;
    String attendeesID;
    String location;
    int numAttendees;
    String time;
    String date;
    int activityType;
    String description;
    String isFinished;
    String canceled;

    CoWork(){
        coworkID = 0;
        creatorID = 0;
        attendeesID = "";
        location = "";
        numAttendees = 0;
        time = "";
        date = "";
        activityType = 0;
        description = "";
        isFinished = "";
        canceled = "";
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

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {
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

    public void setCanceled(String canceled) {
        this.canceled = canceled;
    }

    public String getCanceled() {
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

    public String getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(String isFinished) {
        this.isFinished = isFinished;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
