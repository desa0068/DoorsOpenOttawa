package com.algonquincollege.desa0068.doorsopenottawa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquincollege.desa0068.doorsopenottawa.model.Building;

import java.util.List;

/**
 * This class is used to create a custom adapter for the list view in the application that contains displays custom fields rather than just displaying a a simple textview
 * Created by vaibhavidesai on 2016-11-04.
 */

public class BuildingAdapter extends ArrayAdapter<Building> {

    private Context context;
    private List<Building> buildingList;
    private TextView buildingId, buildingName, buildingAddress,buildingDate;
    private ImageView buildingImage;

    public BuildingAdapter(Context context, int resource, List<Building> building) {
        super(context, resource, building);
        this.context = context;
        this.buildingList = building;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);

        //Display planet name in the TextView widget
        Building building = buildingList.get(position);

        buildingName = (TextView) view.findViewById(R.id.itemname);
        buildingName.setText(building.getName());

        buildingDate = (TextView)view.findViewById(R.id.itemdate);
        String date="";
        for(int i = 0; i<building.getOpen_hours().size(); i++) {
            date+=building.getOpen_hours().get(i)+"\n";
        }

        buildingDate.setText("Date and Time is:"+"\n"+date);
//        if(building.getBitmapimg()!=null)
//        {
//            buildingImage=(ImageView)view.findViewById(R.id.itemImage);
//            buildingImage.setImageBitmap(building.getBitmapimg());
//        }
//        else
//        {
//            BuildingAndView container=new BuildingAndView();
//            container.building=building;
//            container.view=view;
//
//            ImageLoader loader=new ImageLoader();
//            loader.execute(container);
//        }
        return view;

    }

    private class BuildingAndView
    {
        public Building building;
        public View view;
        public Bitmap bitmap;
    }

//    private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView>
//    {
//        @Override
//        protected BuildingAndView doInBackground(BuildingAndView... params) {
//            BuildingAndView container=params[0];
//            Building building=container.building;
//
//            try
//            {
//                String imageUrl=building.getImage();
//                InputStream in=(InputStream) new URL(imageUrl).getContent();
//                Bitmap bitmap= BitmapFactory.decodeStream(in);
//                building.setBitmap(bitmap);
//                in.close();
//                container.bitmap=bitmap;
//                return container;
//
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(BuildingAndView buildingAndView) {
////            ImageView imageView=(ImageView)buildingAndView.view.findViewById(R.id.itemImage);
////            imageView.setImageBitmap(buildingAndView.bitmap);
//            buildingAndView.building.setBitmap(buildingAndView.bitmap);
//        }
//    }

}
