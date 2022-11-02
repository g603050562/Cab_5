package com.NewElectric.app11.service.logic.logic_httpConnection.http;

import com.NewElectric.app11.model.dao.fileSave.LocalLog;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpUrlMap {

    private static String apc = "http://apc.halouhuandian.com/";
    private static String api = "http://api.halouhuandian.com/";
    private static String app = "https://app.halouhuandian.com/";
    private static String log = "http://logs.halouhuandian.com:52100/";
    //长链接
    public static String longlink = "http://long.halouhuandian.com:5858";
    //长链接绑定
    public static String BindLongLink = "http://apc.halouhuandian.com:1081/Connection/bind.html";

    //电柜日志
    public static String UploadCabinetInfo = log + "Log/logs.html";
    //写入UID上传服务器接口
    public static String UploadWriteUID = apc + "Cabinet/hisWriteUid32.html";
    //租电池接口
    public static String RentBattery =  api + "Rent/receiveBindv3.html";
    //租电池二次上传服务器接口
    public static String RentBatteryFinish = api + "Rent/confirmBindv3.html";
    //检测本地舱门电池是否存在绑定电池
    public static String BatteriesIsBinding = apc + "Check/startCheck.html";
    //获取服务器和下载器版本
    public static String GetDownloaderAndLauncher = "http://47.110.240.148/Log/getVersion";
    //上传服务器本地弹出电池
    public static String UploadOpenDoorLog = apc + "CabLog/addCabEjectLog";
    //上传服务器本地日志
    public static String UploadExchangeLog = apc + "Exchange/exchangev8.html";
    //下载二维码请求
    public static String GetQrCodeUrl = apc + "Cabinet/qrcode.html";
    //电池换电UID转换接口
    public static String GetUID = apc + "Check/checkOldBind.html";
    //电池换电获取用户信息
    public static String GetUserInfo = apc + "Check/checkUserBalance.html";
    //电池写入UID失败上传日志
    public static String UploadWriteUidFail = apc + "Errors/cabinet.html";
    //获取控制板版本
    public static String GetControlPlateVersionBin =  apc + "ApsHard/soft.html";
    //http心跳
    public static String HttpBeat = apc + "heartbeat.php";
    //QrCode 二维码转挑
    public static String DownLoadApkJump = app+"App/jump";
    //H5界面连接
    public static String h5AdvUrl = apc+"Cabinet/message.html?id=";

    public static void setServer(String type){

        if(type.equals("")){
            LocalLog.getInstance().writeLog("longLink - 当前服务器 - " + type.toString());
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(type);
            apc = jsonObject.getString("apc");
            api = jsonObject.getString("api");
            app = jsonObject.getString("app");
            log = jsonObject.getString("log");
            longlink = jsonObject.getString("longlink");
            BindLongLink = jsonObject.getString("BindLongLink") + "Connection/bind.html";
            init();
            LocalLog.getInstance().writeLog("longLink - 当前服务器 - " + type.toString());
        } catch (Exception e) {
            LocalLog.getInstance().writeLog("longLink - 当前服务器 - error - " + e.toString());
        }
    }

    private static void init(){
        UploadCabinetInfo = log + "Log/logs.html";
        UploadWriteUID = apc + "Cabinet/hisWriteUid32.html";
        RentBattery =  api + "Rent/receiveBindv3.html";
        RentBatteryFinish = api + "Rent/confirmBindv3.html";
        BatteriesIsBinding = apc + "Check/startCheck.html";
        GetDownloaderAndLauncher = "http://47.110.240.148/Log/getVersion";
        UploadOpenDoorLog = apc + "CabLog/addCabEjectLog";
        UploadExchangeLog = apc + "Exchange/exchangev8.html";
        GetQrCodeUrl = apc + "Cabinet/qrcode.html";
        GetUID = apc + "Check/checkOldBind.html";
        GetUserInfo = apc + "Check/checkUserBalance.html";
        UploadWriteUidFail = apc + "Errors/cabinet.html";
        GetControlPlateVersionBin =  apc + "ApsHard/soft.html";
        HttpBeat = apc + "heartbeat.php";
        DownLoadApkJump = app+"App/jump";
        h5AdvUrl = apc+"Cabinet/message.html?id=";
    }
}
