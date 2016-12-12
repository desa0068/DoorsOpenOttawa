package com.algonquincollege.desa0068.doorsopenottawa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;

import com.algonquincollege.desa0068.doorsopenottawa.model.Building;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by vaibhavidesai on 2016-12-11.
 */

public class SharedPreference {
    public static final String PREFS_NAME = "BUILDING_APP";
    public static final String FAVORITES = "code_Favorite";
    android.content.SharedPreferences.Editor editor;
    SharedPreferences settings;
    Set<String> fav;
    Set<String> favorites;

    public SharedPreference()
    {
        super();
    }
    public void saveFavorites(Context context, Set<String> favorites){
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        if(fav==null) {
            fav = new HashSet<String>();
        }
        for(String s:favorites)
        {
            fav.add(s);
        }
        editor.putStringSet(FAVORITES,fav);
        editor.commit();
    }

    public void addFavorite(Context context, String buildingId){


        if(favorites==null) {
            favorites = new HashSet<String>();
        }
        else
        {
            favorites = settings.getStringSet(FAVORITES, null);
        }
        favorites.add(buildingId);
        saveFavorites(context,favorites);
    }

    public void removeFavorite(Context context, String buildingId) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Set<String> favorites = settings.getStringSet(FAVORITES,new ArraySet<String>());

        if (favorites != null) {
            favorites.remove(buildingId);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<String> getFavorites(Context context) {
        SharedPreferences settings;
        List<String> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            Set<String> newSet;
            newSet = settings.getStringSet(FAVORITES, new ArraySet<String>());
            String[] favoriteItems= newSet.toArray(new String[newSet.size()]);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else
            return null;

        return (ArrayList<String>) favorites;
    }


}
