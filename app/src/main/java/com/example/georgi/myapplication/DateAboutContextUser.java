package com.example.georgi.myapplication;

import java.text.SimpleDateFormat;

public class DateAboutContextUser {
    private String activityU, headphone;
    private long timestamp, time;
    private int humidity, temperature;
    private float longitude, latitude;
    private double timeSlot;

    public DateAboutContextUser(){

    }

    /*public DateAboutContextUser(String activityU, String headphone,int humidity, float latitude, float longitude, int temperature, SimpleDateFormat time) {
        this.activityU = activityU;
        this.headphone = headphone;
        this.humidity = humidity;
        this.latitide = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.time = time;
    }*/

    public String getActivityU() {
        return activityU;
    }

    public double getTimeSlot() {
        return timeSlot;
    }

    public String getHeadphone() {
        return headphone;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getTime() {
        return time;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setActivityU(String activityU) {
        this.activityU = activityU;
    }

    public void setHeadphone(String headphone) {
        this.headphone = headphone;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimeFormat(long time) {
        this.time = time;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setTimeSlot(double timeSlot) {
        this.timeSlot = timeSlot;
    }
}
