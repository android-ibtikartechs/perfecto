package com.perfecto.apps.ocr.models;

import org.json.simple.JSONObject;

/**
 * Created by Hosam Azzam on 13/08/2017.
 */

public class User {

    public Long id;
    public String name = "", email = "", phone = "", username = "", photo = "";

    public User() {
    }

    public User(JSONObject jsonObject) {
        try {
            this.id = (Long) jsonObject.get("id");
            this.name = (String) jsonObject.get("name");
            this.phone = (String) jsonObject.get("phone");
            this.photo = (String) jsonObject.get("photo");
            this.email = (String) jsonObject.get("email");
            this.username = (String) jsonObject.get("username");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String GetUserJson() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", this.id);
            jsonObject.put("name", this.name);
            jsonObject.put("phone", this.phone);
            jsonObject.put("photo", this.photo);
            jsonObject.put("email", this.email);
            jsonObject.put("username", this.username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
