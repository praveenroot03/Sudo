package com.project.sudo;

import java.util.ArrayList;

public class UserDetails {

    private String username = null;
    private String email = null;
    private String uid = null;
    private ArrayList<String> transList = null;
    private ArrayList<String> bookmarkIds = null;

    public UserDetails(){

    }

    public UserDetails(String username, String email, String uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
    }

    public UserDetails(String username, String email, String uid, ArrayList<String> transList) {
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

    public ArrayList<String> getTransList() {
        return transList;
    }

    public void setTransList(ArrayList<String> transList) {
        this.transList = transList;
    }

    public ArrayList<String> getBookmarkIds() {
        return bookmarkIds;
    }

    public void setBookmarkIds(ArrayList<String> bookmarkIds) {
        this.bookmarkIds = bookmarkIds;
    }
}
