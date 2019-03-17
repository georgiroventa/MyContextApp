package com.example.georgi.myapplication;

public class DateAboutContextUser {
    public String activityU, location, headphone, weather;

    public DateAboutContextUser(){

    }

    public DateAboutContextUser(String activityU, String location, String headphone, String weather) {
        this.activityU = activityU;
        this.location = location;
        this.headphone = headphone;
        this.weather = weather;
    }

    public String getActivityU() {
        return activityU;
    }
    public String getLocation() {
        return location;
    }
    public String getHeadphone(){return  headphone;}
    public String getWeather() {
        return weather;
    }
}
