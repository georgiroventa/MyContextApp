package com.example.georgi.myapplication;

import java.text.SimpleDateFormat;

public class DateAboutContextUser {
    public String activityU, headphone;
    public SimpleDateFormat time;
    public int humidity, temperature;
    public float longitude, latitide;

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

    public float getLatitide() {
        return latitide;
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

    public void setLatitide(float latitide) {
        this.latitide = latitide;
    }
}
