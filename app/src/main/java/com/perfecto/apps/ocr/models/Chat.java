package com.perfecto.apps.ocr.models;

/**
 * Created by hosam azzam on 14/10/2017.
 */

public class Chat {
    String id, group_id, group_photo, group_name, user_id, user_name, user_photo, msg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_photo() {
        return group_photo;
    }

    public void setGroup_photo(String group_photo) {
        this.group_photo = group_photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String chat_photo) {
        this.user_photo = chat_photo;
    }
}
