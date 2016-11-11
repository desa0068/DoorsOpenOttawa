package com.algonquincollege.desa0068.doorsopenottawa.model;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

/**
 * This is a model class used to store and retrieve properties of the building
 * Created by vaibhavidesai on 2016-11-04.
 */

public class Building {

    private int buildingId;
    private String name;
    private String address;
    private String image;
    private List<String> date;
    private Bitmap bitmapimg;

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address + " Ottawa, Ontario";
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public void setBitmap(Bitmap bitmapimg)
    {
      this.bitmapimg=bitmapimg;
    }

    public Bitmap getBitmapimg()
    {
        return bitmapimg;
    }
}
