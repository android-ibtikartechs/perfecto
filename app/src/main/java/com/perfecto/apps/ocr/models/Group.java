package com.perfecto.apps.ocr.models;

/**
 * Created by hosam azzam on 06/10/2017.
 */

public class Group {
    String id, name, photo = "", user_id;
    int memberscount = 0;

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getMemberscount() {
        return memberscount;
    }

    public void setMemberscount(int memberscount) {
        this.memberscount = memberscount;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
