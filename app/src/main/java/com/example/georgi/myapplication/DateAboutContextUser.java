package com.example.georgi.myapplication;

public class DateAboutContextUser {
    public String activityU, headphone,time;
    Integer humidity, temperature;
    float longitude, latitide;

    public DateAboutContextUser(){

    }

    public DateAboutContextUser(String activityU, String headphone,Integer humidity, float latitude, float longitude, Integer temperature, String time) {
        this.activityU = activityU;
        this.headphone = headphone;
        this.humidity = humidity;
        this.latitide = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.time = time;
    }

   /* public String getActivityU() {
        return activityU;
    }
    public String getLocation() {
        return location;
    }
    public String getHeadphone(){return  headphone;}
    public String getWeather() {
        return weather;
    }*/
}
