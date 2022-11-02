package com.NewElectric.app11.service.logic.logic_httpConnection.fileUpload.movies;

import android.content.Context;
import android.media.MediaMetadataRetriever;


import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.NewElectric.app11.units.FilesDirectoryUnits;
import com.NewElectric.app11.units.Md5;

public class HttpUploadMoviesPath extends Thread {

    private String cabid, admid, upUrl, logintk, data, hour, level;

    public HttpUploadMoviesPath(Context context, String cabid, String admid, String upUrl, String logintk, String data, String hour, String level) {
        this.cabid = cabid;
        this.admid = admid;
        this.upUrl = upUrl;
        this.logintk = logintk;
        this.data = data;
        this.hour = hour;
        this.level = level;
    }

    @Override
    public void run() {
        super.run();
        String path = upUrl;
        HttpPost httpPost = new HttpPost(path);
        httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

        Header header1 = new Header() {
            @Override
            public String getName() {
                return "aptk";
            }

            @Override
            public String getValue() {
                return new Md5().getDateToken();
            }

            @Override
            public HeaderElement[] getElements() {
                return new HeaderElement[0];
            }
        };
        httpPost.addHeader(header1);

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("cabid", cabid));
        list.add(new BasicNameValuePair("admid", admid));
        list.add(new BasicNameValuePair("_logintk_", logintk));
        list.add(new BasicNameValuePair("level", level));

        if (level.equals("3")) {
            JSONArray jsonArray = new JSONArray();
            File file = new File(FilesDirectoryUnits.EXTERNAL_MOVIES_DIR + File.separator + data + File.separator + hour);
            System.out.println("movies：" + "上传路径 - " + FilesDirectoryUnits.EXTERNAL_MOVIES_DIR  + File.separator + data + File.separator + hour);
            if (file.exists()) {
                File[] subFile = file.listFiles();
                for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                    // 判断是否为文件夹
                    String filename = subFile[iFileLength].getName();
                    long fsize = subFile[iFileLength].length();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("fname", filename);
                        jsonObject.put("fsize", fsize);
                        try {
                            jsonObject.put("flong", getTimeFromMillisecond(Long.parseLong(getVideoDuration(subFile[iFileLength].getAbsolutePath()))));
                        } catch (Exception e) {
                            jsonObject.put("flong", "00:00");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonArray.put(jsonObject);
                }
                list.add(new BasicNameValuePair("videos", jsonArray.toString()));
                System.out.println("movies：" + cabid + "   " + admid + "   " + logintk + "   " + jsonArray.toString());
            } else {

            }

        } else if (level.equals("1")) {
            JSONArray jsonArray = new JSONArray();
            File file = new File(FilesDirectoryUnits.EXTERNAL_MOVIES_DIR );
            System.out.println("movies：" + "上传路径 - " + FilesDirectoryUnits.EXTERNAL_MOVIES_DIR );
            if (file.exists()) {
                File[] subFile = file.listFiles();
                for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                    // 判断是否为文件夹
                    String filename = subFile[iFileLength].getName();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("date", filename);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(jsonObject);
                }
                list.add(new BasicNameValuePair("videos", jsonArray.toString()));
                System.out.println("movies：" + cabid + "   " + admid + "   " + logintk + "   " + jsonArray.toString());
            } else {
                System.out.println("movies：   文件不存在");
            }
        } else if (level.equals("2")) {
            JSONArray jsonArray = new JSONArray();
            File file = new File(FilesDirectoryUnits.EXTERNAL_MOVIES_DIR  + File.separator + data);
            System.out.println("movies：" + "上传路径 - " +FilesDirectoryUnits.EXTERNAL_MOVIES_DIR  + File.separator + data);
            if (file.exists()) {
                File[] subFile = file.listFiles();
                for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                    // 判断是否为文件夹
                    String filename = subFile[iFileLength].getName();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("date", data);
                        jsonObject.put("hour", filename);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonArray.put(jsonObject);
                }
                list.add(new BasicNameValuePair("videos", jsonArray.toString()));
                System.out.println("movies：" + cabid + "   " + admid + "   " + logintk + "   " + jsonArray.toString());
            } else {

            }
        }

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());

                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

                System.out.println("movies：" + jsonObject);

            } else {
                System.out.println("movies：" + httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("movies：" + e.toString());
        }
    }

    public static String getTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Date date = new Date(millisecond);
        String timeStr = simpleDateFormat.format(date);
        return timeStr;
    }

    //获取视频总时长
    public static String getVideoDuration(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); //
        return duration;
    }


}
