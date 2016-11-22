
package com.algonquincollege.desa0068.doorsopenottawa;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.desa0068.doorsopenottawa.model.Building;
import com.algonquincollege.desa0068.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.List;
/**
 *  This activity checks for the presence of internet connection, if connection is available makes an http request to the specified url to retrieve
 *  the list of buildings and its details via HttpManager and thereafter executing the BuildingJSONParser class by creating an AsyncTask.Finally,
 *  sets the list of buildings and displays it. It also generates an intent on item click event so that details of the event are displayed in
 *  another screen.
 *  @author Vaibhavi Desai (desa0068@algonquinlive.com)
 */
public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener{

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";

    private ProgressBar pb;
    private List<MyTask> tasks;
    private List<Building> buildingList;
    private Bundle b;
    private BuildingAdapter adapter;
    private ListView lv;
    private String ABOUT_DIALOG_TAG;
    private AboutDialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
        if (isOnline()) {
            requestData(REST_URI);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
        lv=getListView();
        lv.setOnItemClickListener(this);
        ABOUT_DIALOG_TAG="About Dialog";
        dialog=new AboutDialogFragment();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_info)
        {
            dialog.show(getFragmentManager(),ABOUT_DIALOG_TAG);
        }
        return super.onOptionsItemSelected(item);

    }

    public void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }


    public void updateDisplay() {
        adapter= new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);


    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Building building=buildingList.get(position);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        b=new Bundle();
        b.putInt("building_id",building.getBuildingId());
        b.putString("building_name",building.getName());
        b.putString("building_address",building.getAddress());
        b.putString("building_description",building.getDescription());
        ArrayList<String> open_hours= (ArrayList<String>) building.getOpen_hours();
        b.putStringArrayList("open_hours",open_hours);
        intent.putExtras(b);
        startActivity(intent);

    }

    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(String... params) {
            String content = HttpManager.getData(params[0]);
            buildingList = BuildingJSONParser.parseFeed(content);
            return content;
        }

        @Override
        protected void onPostExecute(String s) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }
            if (s == null) {
                Toast.makeText(MainActivity.this, "Could not retrieve data", Toast.LENGTH_LONG).show();
            }
            buildingList = BuildingJSONParser.parseFeed(s);
            updateDisplay();

        }
    }
}
