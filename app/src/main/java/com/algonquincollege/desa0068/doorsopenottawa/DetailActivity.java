package com.algonquincollege.desa0068.doorsopenottawa;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/**
 * * This activity is responsible for displaying the details of the building and displaying the map and pin on it
* Created by vaibhavidesai on 2016-11-04.*/


public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView buildingName,buildingDescription,buildingAddress,buildingOpenHours;
    Bundle b;
    private GoogleMap mMap;
    private Geocoder mGeoCoder;
    private String url="https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyDljwyAhRkGAPhYd-IB_rFGsurxHNojjQU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        buildingName=(TextView)findViewById(R.id.buildingname);
        buildingDescription=(TextView)findViewById(R.id.buildingdescription);
        buildingAddress=(TextView)findViewById(R.id.buildingaddress);
        buildingOpenHours=(TextView)findViewById(R.id.openhours);
        b=getIntent().getExtras();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGeoCoder=new Geocoder(this, Locale.CANADA);
        loadData();
    }
    private void pin(String locationName)
    {

        try {
            List<Address> address = mGeoCoder.getFromLocationName(locationName, 10);
            if(address.size()==0)
            {
                String requesturl = String.format(url, URLEncoder.encode(String.valueOf(locationName), "UTF-8"));
                Log.e("TAG",requesturl);
                new LocalGeoCoder().execute(requesturl);
            }
            else {
                 LatLng ll=new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
                 mMap.addMarker(new MarkerOptions().position(ll).title(locationName));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 12.0f));

            }
            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }


    private void loadData()
    {
        buildingName.setText(b.getString("building_name"));
        buildingDescription.setText(b.getString("building_description"));
        buildingAddress.setText(b.getString("building_address"));
        ArrayList<String> open_hours=b.getStringArrayList("open_hours");
        String date = "";
        for (int i = 0; i < open_hours.size(); i++) {
            date += open_hours.get(i) + "\n";
        }
        buildingOpenHours.setText(date);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        pin(b.getString("building_address"));
    }

    private class LocalGeoCoder extends AsyncTask<String, Void, LatLng> {

        @Override
        protected LatLng doInBackground(String... params) {
           String content = HttpManager.getData(params[0]);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(content);
                double longitute = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                double latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                return new LatLng(latitude, longitute);
                } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                }
            return null;
            }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
            if (latLng == null) {
                Toast.makeText(getApplicationContext(), "LatLong not found", Toast.LENGTH_SHORT).show();
                } else {
                putPinonMap(latLng);
                }
            }
    }

    private void putPinonMap(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(b.getString("building_address")));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
    }


}
