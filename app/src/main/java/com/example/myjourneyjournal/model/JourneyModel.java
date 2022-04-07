package com.example.myjourneyjournal.model;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.Arrays;

public class JourneyModel {

    private  int id;
    private  String title;
    private  String desc;
    private  String location;
    private  byte[] image;

    // for creating an empty model
    public JourneyModel(){}

    public JourneyModel(int id, String jTitle, String jDis, String jLocation, byte[] image){
        this.id=id;
        this.title =jTitle;
        this.desc =jDis;
        this.location =jLocation;
        this.image=image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @NonNull
    @Override
    public String toString() {
        return "Journey(" +
                "Id=" + id +
                " Title=" + title +
                " Description=" + desc +
                " Location=" + location +
                " Image Blob=" + Arrays.toString(image) +
                ")";
    }
}

