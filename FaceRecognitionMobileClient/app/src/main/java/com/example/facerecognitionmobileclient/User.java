package com.example.facerecognitionmobileclient;

public class User {
    String  email, username, phone;
    private String dataImage;

    public String getDataImage() {
        return dataImage;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
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

    public User(String username, String phone, String dataImage) {
        this.phone = phone;
        this.username = username;
        this.dataImage=dataImage;
    }
    public User() {
    }

}
