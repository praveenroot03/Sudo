package com.project.sudo;

import java.util.ArrayList;

public class UserDetails {

    private String username = null;
    private String email = null;
    private String uid = null;
    private ArrayList  transList = new ArrayList();

    public UserDetails(){

    }

    public UserDetails(String username, String email, String uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
    }

    public UserDetails(String username, String email, String uid, ArrayList transList) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.transList = transList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList getTransList() {
        return transList;
    }

    public void setTransList(ArrayList transList) {
        this.transList = transList;
    }
}
