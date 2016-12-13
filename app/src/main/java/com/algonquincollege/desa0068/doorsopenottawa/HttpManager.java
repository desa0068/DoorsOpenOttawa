package com.algonquincollege.desa0068.doorsopenottawa;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import com.algonquincollege.desa0068.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.desa0068.doorsopenottawa.utils.RequestPackage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is used to create a Http request to a server to get the JSON data
 * Created by vaibhavidesai on 2016-11-04.
 */

public class HttpManager {

    private static Integer serverResponseCode;

    public static String crugOperation(RequestPackage p, String userName, String password) {
        BufferedReader reader = null;
        String uri = p.getUri();
        if (p.getMethod() == HttpMethod.GET) {
            uri += "?" + p.getEncodedParams();
        }
        byte[] loginBytes = (userName + ":" + password).getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("Authorization", loginBuilder.toString());
            con.setRequestMethod(p.getMethod().toString());
            JSONObject json = new JSONObject(p.getParams());
            String params = json.toString();

            if (p.getMethod() == HttpMethod.POST || p.getMethod() == HttpMethod.PUT) {
                con.addRequestProperty("Accept", "application/json");
                con.addRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(params);
                writer.flush();
            }
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

//    @Deprecated
//    public static String uploadFile(RequestPackage pkg) {
//        String url = pkg.getUri();
//        File fileName = pkg.getImage();
//        String result = "";
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpPut httpPost = new HttpPut(url);
//        File file = fileName;
//        byte[] loginBytes = ("desa0068" + ":" + "password").getBytes();
//        StringBuilder loginBuilder = new StringBuilder().append("Basic ").append(Base64.encodeToString(loginBytes, Base64.DEFAULT));
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        FileBody fileBody = new FileBody(new File(file.getName()));
//        builder.addPart("photoFile", fileBody);
//        try {
//            HttpEntity entity = builder.build();
//            httpPost.setEntity(entity);
//            httpPost.setHeader("Authorization", loginBuilder.toString());
//            httpPost.setHeader("Content-Type", "multipart/form-data");
//            Log.d("executing request ", httpPost.getRequestLine().toString());
//            HttpResponse response = httpClient.execute(httpPost);
//            HttpEntity resEntity = response.getEntity();
//            result = resEntity.toString();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

//    @Deprecated
//    public static String uploadFile(RequestPackage pkg) {
//
//        DataOutputStream dos = null;
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1 * 1024 * 1024;
//        byte[] loginBytes = ("desa0068" + ":" + "password").getBytes();
//        StringBuilder loginBuilder = new StringBuilder()
//                .append("Basic ")
//                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));
//        try {
//            FileInputStream fileInputStream = new FileInputStream(pkg.getImage());
//            URL url = new URL(pkg.getUri());
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.addRequestProperty("Authorization", loginBuilder.toString());
//            con.setRequestMethod(HttpMethod.PUT.toString());
//            con.setDoInput(true);
//            con.setDoOutput(true);
//            con.setRequestProperty("ENCTYPE", "multipart/form-data");
//            con.setRequestProperty("Connection", "Keep-Alive");
//            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            con.setRequestProperty("photoImage", pkg.getImage().getAbsolutePath());
//            dos = new DataOutputStream(con.getOutputStream());
//            dos.writeBytes(twoHyphens + boundary + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name=\"photoImage\";filename=\"" + pkg.getImage().getName() + "\"" + lineEnd);
//
//                    dos.writeBytes(lineEnd);
//            bytesAvailable = fileInputStream.available();
//
//            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            buffer = new byte[bufferSize];
//
//            // read file and write it into form...
//            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//            while (bytesRead > 0) {
//
//                dos.write(buffer, 0, bufferSize);
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//            }
//
//            // send multipart form data necesssary after file data...
//            dos.writeBytes(lineEnd);
//            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//            // Responses from the server (code and message)
//            serverResponseCode = con.getResponseCode();
//            String serverResponseMessage = con.getResponseMessage();
//
//            Log.i("uploadFile", "HTTP Response is : "
//                    + serverResponseMessage + ": " + serverResponseCode);
//
//
//        }
//        catch(IOException e)
//        {
//
//        }
//        return serverResponseCode.toString();
//    }

    @Deprecated
    public static String uploadFile(RequestPackage pkg) {
        final String boundary;
        final String LINE_FEED = "\r\n";
        HttpURLConnection httpConn;
        String charset;
        OutputStream outputStream;
        PrintWriter writer;
        byte[] loginBytes = ("desa0068" + ":" + "password").getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        try {
            boundary = "===" + System.currentTimeMillis() + "===";
            URL url = new URL(pkg.getUri());
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.addRequestProperty("Authorization", loginBuilder.toString());
            httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"),
                    true);
            String fileName = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + pkg.getImage().getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + "photoImage"
                            + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append(
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);

            writer.append(LINE_FEED);
            writer.flush();


            FileInputStream inputStream = new FileInputStream(pkg.getImage());
            byte[] buffer = new byte[6000];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();

            List<String> response = new ArrayList<String>();

            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    response.add(line);
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }

            return response.toString();


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }



}













