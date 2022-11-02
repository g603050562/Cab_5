package com.NewElectric.app11.service.logic.logic_httpConnection.http;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.NewElectric.app11.units.Md5;

public class BaseHttp {

    public interface BaseHttpListener{
        void dataReturn(int code , String message , String data);
    }

    private String url;
    private List<BaseHttpParameterFormat> baseHttpParameterFormats;
    private BaseHttpListener baseHttpListener;
    private boolean isSend = false;
    private int outTime = 10;

    public BaseHttp(String url , List<BaseHttpParameterFormat> baseHttpParameterFormats , BaseHttpListener baseHttpListener) {
        this.url = url;
        this.baseHttpParameterFormats = baseHttpParameterFormats;
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("data",System.currentTimeMillis()+""));
        this.baseHttpListener = baseHttpListener;
    }

    public BaseHttp(String url , List<BaseHttpParameterFormat> baseHttpParameterFormats , int outTime , BaseHttpListener baseHttpListener) {
        this.url = url;
        this.baseHttpParameterFormats = baseHttpParameterFormats;
        this.outTime = outTime;
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("data",System.currentTimeMillis()+""));
        this.baseHttpListener = baseHttpListener;
    }

    public BaseHttp(String url , List<BaseHttpParameterFormat> baseHttpParameterFormats) {
        this.url = url;
        this.baseHttpParameterFormats = baseHttpParameterFormats;
    }

    public BaseHttp(String url , List<BaseHttpParameterFormat> baseHttpParameterFormats , int outTime) {
        this.url = url;
        this.outTime = outTime;
        this.baseHttpParameterFormats = baseHttpParameterFormats;
    }

    public void onStart(){

        timeOut();

        String path = url;
        HttpPost httpPost = new HttpPost(path);
        httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, outTime * 1000);
        httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, outTime * 1000);

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
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        };
        httpPost.addHeader(header1);

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for(int i = 0 ; i < baseHttpParameterFormats.size(); i++){
            list.add(new BasicNameValuePair(baseHttpParameterFormats.get(i).getName() , baseHttpParameterFormats.get(i).getData()));
        }

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    System.out.println("okHttpSuccess：   " + jsonObject);
                    String messageString = jsonObject.getString("msg");
                    String status = jsonObject.getString("status");
                    String data = "";
                    if(status.equals("0")){
                        data = jsonObject.toString();
                    }else{
                        if(jsonObject.has("data")){
                            data = jsonObject.getString("data");
                        }else{
                            data = jsonObject.toString();
                        }
                    }
                    returnMessage( Integer.parseInt(status), messageString,data);
                } catch (JSONException e) {
                    System.out.println("okHttpError：   "+ url + "   " + e.toString());
                    returnMessage( -1, "okHttpError：   "+ url + "   " + e.toString(),"");
                }
            } else {
                System.out.println("okHttpError：   "+ url + "   " + httpResponse.getStatusLine().getStatusCode());
                returnMessage(-1,"okHttpError - "+ url + " - " + httpResponse.getStatusLine().getStatusCode() , "");
            }
        } catch (Exception e) {
            returnMessage(-1,"okHttpError - "+ url + " - " + e.toString() , "");
            System.out.println("okHttpError - " + url + " - " + e.toString());
        }
    }

    private void timeOut(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                returnMessage( -1, "okHttpError：   "+ url + "   " + "http请求超时","");
            }
        };
    }

    private void returnMessage(int code , String message , String data){
        if(isSend == false){
            isSend = true;
            if(baseHttpListener!=null){
                baseHttpListener.dataReturn(code,message,data);
            }
        }
    }

}
