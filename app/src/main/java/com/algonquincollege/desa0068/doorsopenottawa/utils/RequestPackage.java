package com.algonquincollege.desa0068.doorsopenottawa.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaibhavidesai on 2016-12-10.
 */

public class RequestPackage {
    private String uri;
    private HttpMethod method = HttpMethod.GET;
    private Map<String, String> params = new HashMap<>();
    private Map<String, File> imageParams = new HashMap<>();
    private Map<String, File> paramsImage = new HashMap<>();
    private boolean isImage;

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public HttpMethod getMethod() {
        return method;
    }
    public void setMethod(HttpMethod method) {
        this.method = method;
    }
    public Map<String, String> getParams() {
        return params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Integer getBuildingId()
    {
        return Integer.parseInt(params.get("id"));
    }
    public void setParam(String key, String value) {
        params.put(key, value);
    }

    public void setImageParams(String key,File bm)
    {
        imageParams.put(key,bm);
    }
    public void setIsImage(boolean isImage)
    {
        this.isImage=isImage;
    }

    public boolean isImage()
    {
        return isImage;
    }

    public File getImage()
    {
        return imageParams.get("image");
    }
    public String getEncodedParams() {
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(params.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key + "=" + value);
        }
        return sb.toString();
    }
}
