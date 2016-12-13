package com.algonquincollege.desa0068.doorsopenottawa.crug;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.algonquincollege.desa0068.doorsopenottawa.DataPassListener;
import com.algonquincollege.desa0068.doorsopenottawa.MainActivity;
import com.algonquincollege.desa0068.doorsopenottawa.R;
import com.algonquincollege.desa0068.doorsopenottawa.model.Building;
import com.algonquincollege.desa0068.doorsopenottawa.utils.CustomTask;
import com.algonquincollege.desa0068.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.desa0068.doorsopenottawa.utils.RequestPackage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditBuilding.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditBuilding#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditBuilding extends Fragment implements View.OnClickListener, CustomTask.AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;
    private Bundle b;
    private EditText mName, mAddress, mDescription;
    private String name, address, description;
    private Button btnEdit, btnCancel;
    private Building mBuilding;
    private int id;
    private CustomTask putTask;
    private DataPassListener mCallback;
    public EditBuilding() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment EditBuilding.
     */
    // TODO: Rename and change types and number of parameters
    public static EditBuilding newInstance(Bundle param1) {
        EditBuilding fragment = new EditBuilding();

        fragment.setArguments(param1);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        View view = inflater.inflate(R.layout.fragment_edit_building, container, false);
        b = getArguments();
        name = b.getString("building_name");
        address = b.getString("building_address");
        description = b.getString("building_description");
        id = b.getInt("building_id");
        mName = (EditText) view.findViewById(R.id.displayName);
        mAddress = (EditText) view.findViewById(R.id.editAddress);
        mDescription = (EditText) view.findViewById(R.id.editDescription);
        btnEdit = (Button) view.findViewById(R.id.editData);
        btnCancel = (Button) view.findViewById(R.id.cancelEdit);
        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        mName.setText(name);

        mBuilding = new Building();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        mAddress.setText("");
        mAddress.setText(address);
        mDescription.setText(description);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (DataPassListener) context;
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
    public void onClick(View v) {
        if(v.getId()==R.id.editData) {
            mBuilding.setAddress(mAddress.getText().toString());
            mBuilding.setDescription(mDescription.getText().toString());
            RequestPackage pkg = new RequestPackage();
            pkg.setMethod(HttpMethod.PUT);
            pkg.setUri(MainActivity.REST_URI + "/" + id);
            pkg.setParam("address", mBuilding.getAddress());
            pkg.setParam("description", mBuilding.getDescription());
            putTask = new CustomTask(this);
            putTask.execute(pkg);
        }
        else if(v.getId()==R.id.cancelEdit)
        {
            mCallback.passData(null, getResources().getString(R.string.list));
        }


    }


    @Override
    public void processFinish(String output, String method) {
        if (output != null) {
            Toast.makeText(this.getActivity(), mName.getText().toString() + " is updated successfully", Toast.LENGTH_SHORT).show();
        }
            // My AsyncTask is done and onPostExecute was called
            mCallback.passData(null, getResources().getString(R.string.list));

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
