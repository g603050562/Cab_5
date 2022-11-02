package com.NewElectric.app11.service.logic.logic_httpConnection.fileUpload.logs;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.NewElectric.app11.units.FilesDirectoryUnits;
import com.NewElectric.app11.units.Md5;

public class HttpUploadLogsPath extends Thread {

    private String cabid, admid , upUrl , date;

    public HttpUploadLogsPath(String cabid, String admid , String upUrl , String date) {
        this.cabid = cabid;
        this.admid = admid;
        this.upUrl = upUrl;
        this.date = date;
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

        JSONArray jsonArray = new JSONArray();
        File file = new File(FilesDirectoryUnits.INTERNAL_LOG_DIR,date);
        if(file.exists()){
            File[] subFile = file.listFiles();
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                // 判断是否为文件夹
                String fileName = subFile[iFileLength].getName();
                String fileSize = subFile[iFileLength].length() + "";
                String ctime = "0";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("fname", fileName);
                    jsonObject.put("fsize", fileSize);
                    jsonObject.put("ctime", ctime);
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("logs - "+e.toString());
                }
                jsonArray.put(jsonObject);
            }
        }
        list.add(new BasicNameValuePair("files", jsonArray.toString()));
        System.out.println("logs：   cabid - "+cabid + "   admid - "+ admid + "   files - "+ jsonArray.toString());


        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                System.out.println("logs：" + result);
            } else {
                System.out.println("logs：" + httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("logs：" + e.toString());
        }
    }
}
