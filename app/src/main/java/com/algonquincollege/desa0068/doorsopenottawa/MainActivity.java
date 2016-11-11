
/*This class is used to create a asynctask which helps in getting the json data and placing it in the list adapter.
It marks the starting point of the app
@author Vaibhavi Desai (desa0068@algonquinlive.com)
*/
package com.algonquincollege.desa0068.doorsopenottawa;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.desa0068.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";

    private ProgressBar pb;
    private List<MyTask> tasks;
    private List buildingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_get_data) {
            if (isOnline()) {
                requestData(REST_URI);
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    public void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }


    public void updateDisplay() {
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
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
