package com.algonquincollege.desa0068.doorsopenottawa;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.algonquincollege.desa0068.doorsopenottawa.crug.EditBuilding;
import com.algonquincollege.desa0068.doorsopenottawa.crug.NewBuildingActivity;
import com.algonquincollege.desa0068.doorsopenottawa.utils.SharedPreference;

/**
 * This activity checks for the presence of internet connection, if connection is available makes an http request to the specified url to retrieve
 * the list of buildings, also adds the building, deletes it and edit it and its details via HttpManager and thereafter executing the BuildingJSONParser class by creating an AsyncTask.Finally,
 * sets the list of buildings and displays it. It also generates an intent on item click event so that details of the event are displayed in
 * another screen.
 *
 * @author Vaibhavi Desai (desa0068@algonquinlive.com)
 */
public class MainActivity extends AppCompatActivity implements DataPassListener {

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String POST_IMAGE_REST_URI = "http://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String IMAGE_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static SharedPreference sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp= new SharedPreference();
        if (savedInstanceState == null) {
            ListBuilding listBuilding = new ListBuilding();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, listBuilding).commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            NewBuildingActivity addBuildingFragment = new NewBuildingActivity();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, addBuildingFragment).addToBackStack(null).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void passData(Bundle b, String message) {
        if (message.equals(getResources().getString(R.string.details))) {
            DetailActivity detailFragment = new DetailActivity();
            detailFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, detailFragment).addToBackStack(null).commit();
        } else if (message.equals(getResources().getString(R.string.edit))) {
            EditBuilding editFragment = new EditBuilding();
            editFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, editFragment).addToBackStack(null).commit();
        }
        else if(message.equals(getResources().getString(R.string.list)))
        {
            ListBuilding listBuilding = new ListBuilding();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, listBuilding).commit();

        }
    }
}
