package com.algonquincollege.desa0068.doorsopenottawa.crug;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.algonquincollege.desa0068.doorsopenottawa.MainActivity;
import com.algonquincollege.desa0068.doorsopenottawa.R;
import com.algonquincollege.desa0068.doorsopenottawa.model.Building;
import com.algonquincollege.desa0068.doorsopenottawa.parsers.BuildingJSONParser;
import com.algonquincollege.desa0068.doorsopenottawa.utils.CustomTask;
import com.algonquincollege.desa0068.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.desa0068.doorsopenottawa.utils.RequestPackage;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhavidesai on 2016-12-10.
 */
public class NewBuildingActivity extends Fragment implements View.OnClickListener, View.OnTouchListener, CustomTask.AsyncResponse {

    private Button btnAddData;
    private EditText mName, mAddress, mDescription;
    private ImageView mImage;
    private Uri mImageUri;
    private String name, address, imageUrl, description, data;
    private static final int TAKE_PICTURE = 1;
    private File photo;
    private List<Building> mBuildingList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_building, container, false);
        mName = (EditText) view.findViewById(R.id.building_name);
        mAddress = (EditText) view.findViewById(R.id.building_address);
        mDescription = (EditText) view.findViewById(R.id.building_description);
        mImage = (ImageView) view.findViewById(R.id.building_image);
        btnAddData = (Button) view.findViewById(R.id.btnadd);
        mImage.setOnTouchListener(this);
        btnAddData.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void addData() {
        Building building = new Building();
        building.setName(mName.getText().toString());
        building.setDescription(mDescription.getText().toString());
        building.setImage(imageUrl);
        building.setAddress(mAddress.getText().toString());
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod(HttpMethod.POST);
        pkg.setUri(MainActivity.REST_URI);
        pkg.setParam("name", building.getName());
        pkg.setParam("address", building.getAddress());
        pkg.setParam("description", building.getDescription());
        pkg.setParam("image",imageUrl);
        CustomTask postTask = new CustomTask(this);
        postTask.execute(pkg);

    }

    @Override
    public void onClick(View v) {
        addData();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        mImageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = mImageUri;
                    getActivity().getContentResolver().notifyChange(selectedImage, null);

                    ContentResolver cr = getActivity().getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        mImage.setImageBitmap(bitmap);
                        imageUrl=photo.getName();
                        Toast.makeText(this.getActivity(), selectedImage.toString() + imageUrl, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this.getActivity(), "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }

    @Override
    public void processFinish(String output,String method) {
        if(output!=null) {
            Toast.makeText(this.getActivity(), mName.getText().toString()  + " is added successfully",Toast.LENGTH_SHORT).show();
        }
    }
}



