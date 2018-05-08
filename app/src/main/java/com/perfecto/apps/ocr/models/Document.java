package com.perfecto.apps.ocr.models;

/**
 * Created by Hosam Azzam on 16/08/2017.
 */

public class Document {
    String id, name, userid, date;
    int numoffiles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumoffiles() {
        return numoffiles;
    }

    public void setNumoffiles(int numoffiles) {
        this.numoffiles = numoffiles;
    }
}
