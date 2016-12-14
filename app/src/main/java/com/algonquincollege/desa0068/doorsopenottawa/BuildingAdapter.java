package com.algonquincollege.desa0068.doorsopenottawa;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.LruCache;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.desa0068.doorsopenottawa.model.Building;
import com.algonquincollege.desa0068.doorsopenottawa.utils.CustomTask;
import com.algonquincollege.desa0068.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.desa0068.doorsopenottawa.utils.RequestPackage;
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

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder> implements CustomTask.AsyncResponse,Filterable, View.OnClickListener, View.OnCreateContextMenuListener {

    private final DataPassListener mCallback;
    List<Boolean> newArray;
    ArrayList<Boolean> positionArray;
    SharedPreference sp=new SharedPreference();

    View row;
    private ArrayList<Building> arraylist;
    private Context context;
    private List<Building> buildingList;
    private LruCache<Integer, Bitmap> imageCache;
    private boolean isFavorite = false;
    private View view;
     Building building;
    private Building selectedBuilding;


    public BuildingAdapter(List<Building> buildingList, Context context) {
        this.buildingList = buildingList;
        newArray = new ArrayList<>();
        arraylist=new ArrayList<>();
        for(Building b:buildingList)
        {
            arraylist.add(b);
        }
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);

        this.context = context;
        mCallback = (DataPassListener) context;

    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public void onBindViewHolder(final BuildingViewHolder holder, final int position) {
        building= buildingList.get(position);
        holder.bitmap = imageCache.get(building.getBuildingId());
        holder.buildingName.setText(building.getName());
        holder.buildingDescription.setText(building.getDescription());

        if (holder.bitmap != null) {
            holder.image.setImageBitmap(building.getBitmapimg());
        } else {

            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = holder.itemView;
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

            selectedBuilding = buildingList.get(position);

        holder.itemView.setOnCreateContextMenuListener(this);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.b = new Bundle();
                holder.b.putInt("building_id", building.getBuildingId());
                holder.b.putString("building_name", building.getName());
                holder.b.putString("building_address", building.getAddress());
                holder.b.putString("building_description", building.getDescription());
                ArrayList<String> open_hours = (ArrayList<String>) building.getOpen_hours();
                holder.b.putStringArrayList("open_hours", open_hours);
                mCallback.passData(holder.b, "Invoke Detail Fragment");
            }
        });


        holder.addToFavourites.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          String tag = holder.addToFavourites.getTag().toString();
                                                          if (tag.equalsIgnoreCase("no")) {

                                                              MainActivity.sp.addFavorite(context, buildingList.get(position).getBuildingId().toString());
                                                              holder.addToFavourites.setTag("yes");
                                                              Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();


                                                          } else if (tag.equalsIgnoreCase("yes")) {

                                                              MainActivity.sp.removeFavorite(context, buildingList.get(position).getBuildingId().toString());
                                                              holder.addToFavourites.setTag("no");
                                                              holder.addToFavourites.setButtonDrawable(R.drawable.addtofavourites);
                                                              Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                                                          }

                                                      }
                                                  });


//        holder.addToFavourites.setChecked(building.isAddedToFavourites());
        if (checkFavoriteItem(building.getBuildingId().toString())) {
            holder.addToFavourites.setTag("yes");
            holder.addToFavourites.invalidate();
            holder.addToFavourites.setChecked(true);
        } else {
            holder.addToFavourites.invalidate();
            holder.addToFavourites.setTag("no");
            holder.addToFavourites.setChecked(false);

        }

    }

    @Override
    public int getItemCount() {
        return buildingList.size();
    }

    @Override
    public BuildingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_building, parent, false);

        return new BuildingViewHolder(itemView);
    }

    public boolean checkFavoriteItem(String buildingId) {
        boolean check = false;
        if (sp != null) {
            List<String> favorites = sp.getFavorites(context);
            if (favorites != null) {
                for (String code : favorites) {
                    if (code.equals(buildingId)) {
                        check = true;
                        break;
                    }
                }
            }
        }

        return check;
    }

    // Filter Class

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        buildingList.clear();
       if(arraylist!=null) {
           if (charText.length() == 0) {
               buildingList.addAll(arraylist);
           } else {
               for (Building b : arraylist) {
                   if (b.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                       buildingList.add(b);
                   }
               }
           }
       }

        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        MenuItem editAction=menu.add(0, R.id.edit, 0, "Edit");
        MenuItem delete=menu.add(0, R.id.delete, 1, "Delete");
        editAction.setOnMenuItemClickListener(mOnMyActionClickListener);
        delete.setOnMenuItemClickListener(mOnMyActionClickListener);

    }

    private final MenuItem.OnMenuItemClickListener mOnMyActionClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int index = item.getOrder();


            switch (index) {
                case 0:
                    Bundle b;
                    b = new Bundle();
                    b.putInt("building_id", selectedBuilding.getBuildingId());
                    b.putString("building_name", selectedBuilding.getName());
                    b.putString("building_address", selectedBuilding.getAddress());
                    b.putString("building_description", selectedBuilding.getDescription());
                    mCallback.passData(b, context.getResources().getString(R.string.edit));
                    return true;
                case 1:

                    openDialog();
                    return true;


                default:
                    return false;


            }
        }


    };
        public void openDialog()
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.deletedata));
            alertDialogBuilder.setMessage(context.getResources().getString(R.string.confirmdelete));
            alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            RequestPackage pkg = new RequestPackage();
                            pkg.setMethod(HttpMethod.DELETE);
                            pkg.setUri(MainActivity.REST_URI + "/" + selectedBuilding.getBuildingId());
                            CustomTask deleteTask = new CustomTask(BuildingAdapter.this);
                            deleteTask.execute(pkg);


                        }
                    });
            alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


    @Override
    public void processFinish(String output, String method) {

    }


    public class BuildingViewHolder extends RecyclerView.ViewHolder {
        public TextView buildingName, buildingDescription;
        public ImageView image;
        public CheckBox addToFavourites;
        public Bitmap bitmap;
        public Bundle b;


        public BuildingViewHolder(View view) {
            super(view);
            buildingName = (TextView) view.findViewById(R.id.itemname);
            buildingDescription = (TextView) view.findViewById(R.id.itemdescription);
            addToFavourites = (CheckBox) view.findViewById(R.id.favourites);
            image = (ImageView) view.findViewById(R.id.itemImage);

        }
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
            if (result != null) {

                ImageView image = (ImageView) result.view.findViewById(R.id.itemImage);
                image.setImageBitmap(result.bitmap);
                if (result.building.getBuildingId() != null && result.bitmap != null) {
                    imageCache.put(result.building.getBuildingId(), result.bitmap);
                }
            }
        }

    }


}


