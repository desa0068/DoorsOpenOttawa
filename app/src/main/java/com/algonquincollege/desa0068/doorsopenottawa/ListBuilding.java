package com.algonquincollege.desa0068.doorsopenottawa;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.desa0068.doorsopenottawa.model.Building;
import com.algonquincollege.desa0068.doorsopenottawa.parsers.BuildingJSONParser;
import com.algonquincollege.desa0068.doorsopenottawa.utils.CustomTask;
import com.algonquincollege.desa0068.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.desa0068.doorsopenottawa.utils.RequestPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListBuilding.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListBuilding#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListBuilding extends ListFragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, CustomTask.AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private ListView mbuildingLV;
    private BuildingAdapter mAdapter;
    private List<Building> mBuildingList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Bundle b;
    private List<CustomTask> tasks;
    private int selectedBuildingId;
    private OnFragmentInteractionListener mListener;
    private DataPassListener mCallback;
    private ImageView deleteImage,addToFavouriteImage;
    private EditText inputSearch;


    public ListBuilding() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ListBuilding newInstance(String param1, String param2) {
        ListBuilding fragment = new ListBuilding();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_building, container, false);
        tasks = new ArrayList<>();
        deleteImage=(ImageView)view.findViewById(R.id.delete);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        inputSearch = (EditText)view.findViewById(R.id.inputSearch);


        if (isOnline()) {
            requestData(MainActivity.REST_URI);
        } else {
            Toast.makeText(this.getActivity(), "Network isn't available", Toast.LENGTH_LONG).show();
        }



        return view;
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void requestData(String uri) {
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.GET );
        pkg.setUri(MainActivity.REST_URI);
        CustomTask getData=new CustomTask(this);
        getData.execute( pkg );

    }
    public void updateDisplay() {
        if(mBuildingList!=null) {
            mAdapter = new BuildingAdapter(this.getActivity(), R.layout.item_building, mBuildingList);
            setListAdapter(mAdapter);
            inputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    mAdapter.filter(cs.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mbuildingLV=getListView();
        registerForContextMenu(mbuildingLV);
        mbuildingLV.setOnItemClickListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (DataPassListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DataPassListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Building building=mBuildingList.get(position);
        b=new Bundle();
        b.putInt("building_id",building.getBuildingId());
        b.putString("building_name",building.getName());
        b.putString("building_address",building.getAddress());
        b.putString("building_description",building.getDescription());
        ArrayList<String> open_hours= (ArrayList<String>) building.getOpen_hours();
        b.putStringArrayList("open_hours",open_hours);
        mCallback.passData(b, getResources().getString(R.string.details));


    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDisplay();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        },2000);
    }

    @Override
    public void processFinish(String output,String method) {
        if(method==HttpMethod.POST.toString() || method==HttpMethod.GET.toString()) {
            if (output != null) {
                mBuildingList = BuildingJSONParser.parseFeed(output);
                updateDisplay();

            }
        }
        else if(method==HttpMethod.DELETE.toString())
        {
            requestData(MainActivity.REST_URI);
            updateDisplay();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getActivity().getMenuInflater().inflate(R.menu.menu_list, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        selectedBuildingId=mBuildingList.get(index).getBuildingId();
        switch(item.getItemId()) {

            case R.id.edit:
                Building building=mBuildingList.get(info.position);
                b=new Bundle();
                b.putInt("building_id",building.getBuildingId());
                b.putString("building_name",building.getName());
                b.putString("building_address",building.getAddress());
                b.putString("building_description",building.getDescription());
                mCallback.passData(b, getResources().getString(R.string.edit));
                return true;
            case R.id.delete:
                Toast.makeText(this.getActivity(),""+selectedBuildingId,Toast.LENGTH_SHORT).show();
                RequestPackage pkg = new RequestPackage();
                pkg.setMethod( HttpMethod.DELETE );
                pkg.setUri( MainActivity.REST_URI +  "/" +selectedBuildingId);
                CustomTask deleteTask=new CustomTask(this);
                deleteTask.execute(pkg);
                return true;


            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortasc:
                Collections.sort(mBuildingList, new Comparator<Building>() {
                    @Override
                    public int compare(Building lhs, Building rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });

                break;

            case R.id.sortdesc:
                Collections.sort(mBuildingList, Collections.reverseOrder(new Comparator<Building>() {
                    @Override
                    public int compare(Building lhs, Building rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                }));
                break;
        }
        item.setChecked(true);

        ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
        return true;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}