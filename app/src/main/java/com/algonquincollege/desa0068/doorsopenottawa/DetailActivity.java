package com.algonquincollege.desa0068.doorsopenottawa;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView buildingName,buildingDescription,buildingAddress,buildingOpenHours;
    Bundle b;
    private GoogleMap mMap;
    private Geocoder mGeoCoder;
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
            Address address = mGeoCoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng( address.getLatitude(), address.getLongitude() );
            mMap.addMarker( new MarkerOptions().position(ll).title(locationName) );
            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(ll,10.0f));

            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
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
        buildingOpenHours.setText("Open Hours:"+"\n"+date);
        pin(b.getString("building_address"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
