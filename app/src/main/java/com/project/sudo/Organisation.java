package com.project.sudo;

import java.io.Serializable;

public class Organisation implements Serializable {

    private String name = null;
    private String tagline = null;
    private String desc = null;
    private String website = null;
    private String phnum = null;
    private String email = null;
    private String type = null;
    private String photourl = null;

    public Organisation(){

    }

    public Organisation(String name, String tagline, String desc, String website, String phnum, String email,String photourl, String type) {
        this.name = name;
        this.tagline = tagline;
        this.desc = desc;
        this.website = website;
        this.phnum = phnum;
        this.email = email;
        this.type = type;
        this.photourl = photourl;
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

    public String getPhotourl() { return photourl; }

    @Override
    public String toString() {
        return "Organisation{" +
                "name='" + name + '\'' +
                ", tagline='" + tagline + '\'' +
                ", desc='" + desc + '\'' +
                ", website='" + website + '\'' +
                ", phnum='" + phnum + '\'' +
                ", email='" + email + '\'' +
                ", type='" + type + '\'' +
                ", photourl='" + photourl + '\'' +
                '}';
    }
}
