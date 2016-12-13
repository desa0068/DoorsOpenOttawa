package com.algonquincollege.desa0068.doorsopenottawa.utils;

import android.os.AsyncTask;


import com.algonquincollege.desa0068.doorsopenottawa.HttpManager;


/**
 * Created by vaibhavidesai on 2016-12-11.
 */

public class CustomTask extends AsyncTask<RequestPackage, String,String> {
    String method="";
    public interface AsyncResponse {
        void processFinish(String output,String method);
    }
    public AsyncResponse delegate = null;

    public CustomTask(AsyncResponse delegate){
        this.delegate = delegate;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(RequestPackage... params) {
        String content;
            if(params[0].isImage())
            {
              content=HttpManager.uploadFile(params[0]);
              return content;
            }
        else {
                method = params[0].getMethod().toString();
                content = HttpManager.crugOperation(params[0], "desa0068", "password");
                return content;
            }
    }

    @Override
    protected void onPostExecute(String s) {
        delegate.processFinish(s, method);

    }
}
