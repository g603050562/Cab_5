package com.NewElectric.app11.service.logic.logic_httpConnection.http.uploadCabinet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateInfo;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduInfo;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.model.dao.sqlLite.ExchangeInfoDB;
import com.NewElectric.app11.units.Units;

public class UploadCabinetInfoDataFormat {

    public String getJson (Context context, ControlPlateInfo controlPlateInfo , PduInfo pduInfo) throws JSONException, PackageManager.NameNotFoundException {

        CabInfoSp cabInfoSp = new CabInfoSp(context);
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("number", cabInfoSp.getCabinetNumber_4600XXXX());  //柜子id
        jsonObject.put("version", cabInfoSp.getVersion());  //柜子当前版本
        jsonObject.put("doors", new UploadCabinetDoorInfoDataFormat().getJson(context,controlPlateInfo,pduInfo));
        jsonObject.put("cab_tem", cabInfoSp.getTemMeter()); //温湿传感器的温度上传数据
        jsonObject.put("cab_the", cabInfoSp.getTheMeter()); //温湿传感器的湿度上传数据
        jsonObject.put("cab_ele", cabInfoSp.getEleMeter()); //电表的用电量上传数据
        jsonObject.put("pdu_charge_1", pduInfo.getPduEquipmentBeans()[0].toString()); //pdu 三个充电机的 充电情况
        jsonObject.put("pdu_charge_2", pduInfo.getPduEquipmentBeans()[1].toString()); //pdu 三个充电机的 充电情况
        jsonObject.put("pdu_charge_3", pduInfo.getPduEquipmentBeans()[2].toString()); //pdu 三个充电机的 充电情况
        jsonObject.put("isline", "-1"); //判断是什么样的柜子   1(默认) - 网络换电    -1 - 离线换电
        jsonObject.put("androidSoft",cabInfoSp.getAndroidDeviceModel()); //上传android设备名称，以后好区分android板    rk3288_box - 小安卓板     SABRESD-MX6DQ - 大安卓板
        jsonObject.put("threadProtectionType", cabInfoSp.getTPTNumber()); //本地保护线程参数
        jsonObject.put("extime", System.currentTimeMillis() + ""); //这条记录的时间
        jsonObject.put("dbm", cabInfoSp.getDBM() + ""); //信号值
        jsonObject.put("isExCard", Units.getIsExistExCard());     //jsonObject
        jsonObject.put("localExchanges", ExchangeInfoDB.getInstance(context).getCount());   //本地换电日志条数
        //上传本地下载器版本
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo("com.NewElectric.app4", 0);
        jsonObject.put("downloaderVersion", packageInfo.versionCode);
        //上传launcher界面版本
        PackageInfo packageInfoLauncher = packageManager.getPackageInfo("com.NewElectric.app5", 0);
        jsonObject.put("launcherVersion", packageInfoLauncher.versionCode);
        //本地保存最后上传的记录
        cabInfoSp.setLastCabInfo(jsonObject.toString());

        return jsonObject.toString();
    }

}
