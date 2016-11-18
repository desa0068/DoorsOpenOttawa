package com.algonquincollege.desa0068.doorsopenottawa.model;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * This is a model class used to store and retrieve properties of the building
 * Created by vaibhavidesai on 2016-11-04.
 */

public class Building {

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("EEE,MMM,d,yyyy - hh:mm");
    private int buildingId;
    private String name;
    private String address;
    private String image;
    private List<String> open_hours;
    private Bitmap bitmapimg;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;


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

    public List<String> getOpen_hours() {
        return open_hours;
    }

    public void setOpen_hours(List<String> open_hours) {
        this.open_hours = open_hours;
    }

    public void setBitmap(Bitmap bitmapimg) {
        this.bitmapimg = bitmapimg;
    }

    public Bitmap getBitmapimg() {
        return bitmapimg;
    }
}
