package com.project.sudo;

public class Organisation {

    private String name = null;
    private String tagline = null;
    private String desc = null;
    private String website = null;
    private String phnum = null;
    private String email = null;
    private String type = null;

    public Organisation(){

    }

    public Organisation(String name, String tagline, String desc, String website, String phnum, String email, String type) {
        this.name = name;
        this.tagline = tagline;
        this.desc = desc;
        this.website = website;
        this.phnum = phnum;
        this.email = email;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getTagline() {
        return tagline;
    }

    public String getDesc() {
        return desc;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhnum() {
        return phnum;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }
}
