package com.algonquincollege.desa0068.doorsopenottawa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.desa0068.doorsopenottawa.model.Building;
import com.algonquincollege.desa0068.doorsopenottawa.utils.SharedPreference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class is used to create a custom adapter for the list view in the application that contains displays
 * custom fields rather than just displaying a a simple textview
 * Created by vaibhavidesai on 2016-11-04.
 */

public class BuildingAdapter extends ArrayAdapter<Building> implements Filterable,View.OnClickListener {

    private final ArrayList<Building> arraylist;
    private Context context;
    private List<Building> buildingList;
    private TextView buildingId, buildingName, buildingDescription;
    private LruCache<Integer, Bitmap> imageCache;
    private ArrayList<Building> mOriginalValues; // Original Values
    private ArrayList<Building> mDisplayedValues;    // Values to be displayed
    LayoutInflater inflater;
    List<Boolean> newArray;
    private ImageView addToFavourites;
    private boolean isFavorite=false;
    private SharedPreference sp;


    public BuildingAdapter(Context context, int resource, List<Building> building) {
        super(context, resource, building);
        this.context = context;
        this.buildingList = building;

        //Specifying the cache memory size
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        newArray=new ArrayList<>();
        for(Building b:buildingList)
        {
            b.setAddedToFavourites(false);
        }
        imageCache = new LruCache<>(cacheSize);
        arraylist = new ArrayList<Building>();
        arraylist.addAll(buildingList);
        sp=new SharedPreference();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);
        Building building = buildingList.get(position);
        buildingName = (TextView) view.findViewById(R.id.itemname);
        buildingName.setText(building.getName());
        buildingDescription = (TextView)view.findViewById(R.id.itemdescription);
        buildingDescription.setText(building.getDescription());
        addToFavourites=(ImageView)view.findViewById(R.id.favourites);
        Bitmap bitmap = imageCache.get(building.getBuildingId());
        addToFavourites.bringToFront();

        if(checkFavoriteItem(building.getBuildingId().toString())){
            addToFavourites.setBackgroundResource(R.drawable.starfilled);
            notifyDataSetChanged();
            isFavorite=true;
        }
        else
        {
            addToFavourites.setBackgroundResource(R.drawable.addtofavourites);
            notifyDataSetChanged();
            isFavorite=false;
        }

        addToFavourites.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isFavorite==false)
                {
                    isFavorite=true;
                    sp.addFavorite(context, buildingList.get(position).getBuildingId().toString());
                    addToFavourites.setBackgroundResource(R.drawable.starfilled);
                    Toast.makeText(getContext(),"Added To Favourites",Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
                else
                {
                    isFavorite=false;
                    sp.removeFavorite(context, buildingList.get(position).getBuildingId().toString());
                    addToFavourites.setBackgroundResource(R.drawable.addtofavourites);
                    Toast.makeText(getContext(),"Removed from Favourites",Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();

                }
            }





        });

        //Loads the image from cache
        if (bitmap != null) {

            ImageView image = (ImageView) view.findViewById(R.id.itemImage);
            image.setImageBitmap(building.getBitmapimg());
        } else {

            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = view;
//
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }




        return view;

    }
    public boolean checkFavoriteItem(String buildingId) {
        boolean check = false;

            List<String> favorites = sp.getFavorites(context);
            if (favorites != null) {
                for (String code : favorites) {
                    if (code.equals(buildingId)) {
                        check = true;
                        break;
                    }
                }
            }

        return check;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        buildingList.clear();
        if (charText.length() == 0) {
            buildingList.addAll(arraylist);
        }
        else
        {
            for (Building b : arraylist)
            {
                if (b.getName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    buildingList.add(b);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {



    }

    private class BuildingAndView {
        public Building building;
        public View view;
        public Bitmap bitmap;
    }

    //Asyntask specified in order to execute the function on different thread rather than waiting for the images to completely load and
    // then display other details

    private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView> {

        @Override
        protected BuildingAndView doInBackground(BuildingAndView... params) {

            BuildingAndView container = params[0];
            Building building = container.building;
            byte[] loginBytes = ("desa0068" + ":" + "password").getBytes();
            StringBuilder loginBuilder = new StringBuilder()
                    .append("Basic ")
                    .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));
            try {

                String imageUrl = MainActivity.REST_URI + "/" + building.getBuildingId() + "/image";
                URL url = new URL(imageUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.addRequestProperty("Authorization", loginBuilder.toString());
                InputStream in = con.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                building.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(BuildingAndView result) {
            if(result !=null) {
                ImageView image = (ImageView) result.view.findViewById(R.id.itemImage);
                image.setImageBitmap(result.bitmap);
                if(result.building.getBuildingId()!=null && result.bitmap!=null) {
                    imageCache.put(result.building.getBuildingId(), result.bitmap);
                }
            }
        }

    }



}


