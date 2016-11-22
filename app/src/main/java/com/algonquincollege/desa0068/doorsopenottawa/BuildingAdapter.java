package com.algonquincollege.desa0068.doorsopenottawa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquincollege.desa0068.doorsopenottawa.model.Building;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * This class is used to create a custom adapter for the list view in the application that contains displays
 * custom fields rather than just displaying a a simple textview
 * Created by vaibhavidesai on 2016-11-04.
 */

public class BuildingAdapter extends ArrayAdapter<Building> {

    private Context context;
    private List<Building> buildingList;
    private TextView buildingId, buildingName, buildingDescription;
    private LruCache<Integer, Bitmap> imageCache;


    public BuildingAdapter(Context context, int resource, List<Building> building) {
        super(context, resource, building);
        this.context = context;
        this.buildingList = building;

        //Specifying the cache memory size
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);


        Building building = buildingList.get(position);

        buildingName = (TextView) view.findViewById(R.id.itemname);
        buildingName.setText(building.getName());
        buildingDescription = (TextView)view.findViewById(R.id.itemdescription);
        buildingDescription.setText(building.getDescription());
        Bitmap bitmap = imageCache.get(building.getBuildingId());

        //Loads the image from cache
        if (bitmap != null) {

            ImageView image = (ImageView) view.findViewById(R.id.itemImage);
            image.setImageBitmap(building.getBitmapimg());
        } else {

            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return view;

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

            try {

                String imageUrl = MainActivity.REST_URI + "/" + building.getBuildingId() + "/image";
                InputStream in = (InputStream) new URL(imageUrl).getContent();
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
            ImageView image = (ImageView) result.view.findViewById(R.id.itemImage);
            image.setImageBitmap(result.bitmap);
            imageCache.put(result.building.getBuildingId(), result.bitmap);
        }

    }

}


