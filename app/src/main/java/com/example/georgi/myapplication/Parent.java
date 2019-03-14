package com.example.georgi.myapplication;

public class Parent {
    public String name, email, phone, password, type;

    public Parent(){

    }

    public Parent(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.type = "user";
    }

    public String getEmail() {
        return email;
    }
    public String getType() {
        return type;
    }
    public String getPassword() {
        return password;
    }
}
