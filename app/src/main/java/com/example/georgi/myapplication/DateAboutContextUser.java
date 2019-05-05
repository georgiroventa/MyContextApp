package com.example.georgi.myapplication;

import java.text.SimpleDateFormat;

public class DateAboutContextUser {
    private String activityU, headphone;
    private SimpleDateFormat time;
    private int humidity, temperature;
    private float longitude, latitude;

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

    public String getHeadphone() {
        return headphone;
    }

    public SimpleDateFormat getTime() {
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

    public void setTime(SimpleDateFormat time) {
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
}
