package com.example.linos.testapp;


import java.io.Serializable;

public class Contact implements Serializable{
    String userName;
    String name;

    public Contact(String userName, String name) {
        this.userName = userName;
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
