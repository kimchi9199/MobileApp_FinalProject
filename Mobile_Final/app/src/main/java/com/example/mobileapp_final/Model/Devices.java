package com.example.mobileapp_final.Model;

import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.RecyclerView;

public class Devices {
    private String ID;
    private String deviceName;
    private String Description;
    private Drawable ImageURL;

//    public Devices() {
//    }

    public Devices(String id, String name, String description, Drawable imageURL ){
        this.ID = id;
        this.deviceName = name;
        this.Description = description;
        this.ImageURL = imageURL;

    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID()
    {
        return ID;
    }


    public void setName(String name) {
        deviceName = name;
    }
    public String getName() {
        return deviceName;
    }


    public void setDescription(String description) {
        Description = description;
    }
    public String getDescription() {
        return Description;
    }

    public Drawable getImageURL() {
        return ImageURL;
    }

    public void setImageURL(Drawable imageURL) {
        ImageURL = imageURL;
    }
}

