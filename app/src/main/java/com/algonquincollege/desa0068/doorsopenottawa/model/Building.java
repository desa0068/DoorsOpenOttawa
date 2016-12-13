package com.algonquincollege.desa0068.doorsopenottawa.model;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a model class used to store and retrieve properties of the building
 * Created by vaibhavidesai on 2016-11-04.
 */

public class Building {

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("EEE,MMM,d,yyyy - hh:mm");
    private Integer buildingId;
    private String name;
    private String address;
    private boolean isAddedToFavourites;
    private String image;
    private List<String> open_hours = new ArrayList<String>();
    private Bitmap bitmapimg;
    private String description;

    public Building() {

    }

    public Building(String name) {
        this.name = name;
    }

    public boolean isAddedToFavourites() {
        return isAddedToFavourites;
    }

    public void setAddedToFavourites(boolean addedToFavourites) {
        isAddedToFavourites = addedToFavourites;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBuildingId() {
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
        this.address = address;
    }

    public List<String> getOpen_hours() {
        return open_hours;
    }

    public void setOpen_hours(String date) {

        open_hours.add(date);
    }

    public void setBitmap(Bitmap bitmapimg) {
        this.bitmapimg = bitmapimg;
    }

    public Bitmap getBitmapimg() {
        return bitmapimg;
    }
}
