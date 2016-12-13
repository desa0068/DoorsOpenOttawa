package com.algonquincollege.desa0068.doorsopenottawa.crug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.algonquincollege.desa0068.doorsopenottawa.DataPassListener;
import com.algonquincollege.desa0068.doorsopenottawa.MainActivity;
import com.algonquincollege.desa0068.doorsopenottawa.R;
import com.algonquincollege.desa0068.doorsopenottawa.model.Building;
import com.algonquincollege.desa0068.doorsopenottawa.parsers.BuildingJSONParser;
import com.algonquincollege.desa0068.doorsopenottawa.utils.CustomTask;
import com.algonquincollege.desa0068.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.desa0068.doorsopenottawa.utils.RequestPackage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by vaibhavidesai on 2016-12-10.
 */
public class NewBuildingActivity extends Fragment implements View.OnClickListener,  CustomTask.AsyncResponse {

    private static final int SELECT_IMAGE = 1;

    private Button btnAddData, btnSelectImage, btnCancel;
    private EditText mName, mAddress, mDescription;
    private ImageView mImage;
    private Uri mImageUri;
    private String name, address, imageUrl, description, data;
    private File photo;
    private List<Building> mBuildingList;
    private Bitmap bitmap;
    private Bitmap yourSelectedImage;
    private String imagepath;
    private DataPassListener mCallback;
    private CustomTask postTask;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_building, container, false);
        mName = (EditText) view.findViewById(R.id.building_name);
        mAddress = (EditText) view.findViewById(R.id.building_address);
        mDescription = (EditText) view.findViewById(R.id.building_description);
        mImage = (ImageView) view.findViewById(R.id.building_image);
        btnAddData = (Button) view.findViewById(R.id.btnadd);
        btnSelectImage = (Button) view.findViewById(R.id.pick_image);
        btnCancel=(Button)view.findViewById(R.id.btncancel);
        btnCancel.setOnClickListener(this);
        btnSelectImage.setOnClickListener(this);
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
        building.setImage("Pic.jpg");
        building.setAddress(mAddress.getText().toString());
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod(HttpMethod.POST);
        pkg.setUri(MainActivity.REST_URI);
        pkg.setParam("name", building.getName());
        pkg.setParam("address", building.getAddress());
        pkg.setParam("description", building.getDescription());
        pkg.setParam("image", imageUrl);
        postTask = new CustomTask(this);
        postTask.execute(pkg);

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btnadd) {
            addData();
        }
        else if(id==R.id.pick_image)
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);

        }
        else if(id==R.id.btncancel)
        {
            mCallback.passData(null, getResources().getString(R.string.list));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {

                    if (data != null) {
                        try {
                            mImageUri = data.getData();
                            imageUrl = mImageUri.getPath().toString();
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                            mImage.setImageBitmap(bitmap);
                            Uri tempUri = getImageUri(getContext(), bitmap);
                            photo = new File(getRealPathFromURI(tempUri));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver().query(uri, proj, null, null, null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(column_index);
    }

    @Override
    public void processFinish(String output, String method) {
        if (output != null) {

            Building building = BuildingJSONParser.parseBuilding(output);
            if(building!=null) {

                RequestPackage pkg = new RequestPackage();
                pkg.setMethod(HttpMethod.POST);
                pkg.setUri(MainActivity.POST_IMAGE_REST_URI + "/" + building.getBuildingId() + "/image");
                pkg.setParam("id", building.getBuildingId().toString());
                pkg.setImageParams("image", photo);
                pkg.setIsImage(true);
                CustomTask ct = new CustomTask(this);
                ct.execute(pkg);
            }
            Toast.makeText(this.getActivity(), mName.getText().toString() + " is added successfully", Toast.LENGTH_SHORT).show();

            if(postTask.getStatus() == AsyncTask.Status.FINISHED){
                // My AsyncTask is done and onPostExecute was called
                mCallback.passData(null, getResources().getString(R.string.list));
            }
        }

    }

}



