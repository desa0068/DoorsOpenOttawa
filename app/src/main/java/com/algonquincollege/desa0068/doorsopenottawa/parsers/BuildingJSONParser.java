package com.algonquincollege.desa0068.doorsopenottawa.parsers;

import com.algonquincollege.desa0068.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores the retrieved data from JSON using JSONObject into the
 * building fields using setters and returns the list of buildings
 * Created by vaibhavidesai on 2016-11-04.
 */


public class BuildingJSONParser {

    public static List<Building> parseFeed(String content) {
        try {

            //Retrieving JSON response and storing in it model class
            JSONObject jsonResponse = new JSONObject(content);
            JSONArray buildingArray = jsonResponse.getJSONArray("buildings");
            List<Building> listbuilding = new ArrayList<>();

            List<String> date = new ArrayList<>();
            for (int i = 0; i < buildingArray.length(); i++) {
                JSONObject obj = buildingArray.getJSONObject(i);
                Building building = new Building();
                building.setName(obj.getString("name"));
                building.setAddress(obj.getString("address"));
                building.setBuildingId(obj.getInt("buildingId"));
                building.setDescription(obj.getString("description"));
                building.setImage(obj.getString("image"));
                JSONArray open_hours = obj.getJSONArray("open_hours");
                for (int j = 0; j < open_hours.length(); j++) {
                    building.setOpen_hours((open_hours.getJSONObject(j).getString("date")));
                }

                listbuilding.add(building);
            }
            return listbuilding;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
