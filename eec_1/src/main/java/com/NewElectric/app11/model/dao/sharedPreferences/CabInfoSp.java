package com.NewElectric.app11.model.dao.sharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class CabInfoSp {

    private Context context;
    private SharedPreferences sharedPreferences;

    public CabInfoSp(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("CabInfo", Activity.MODE_WORLD_READABLE);
    }

    /**
     * 换电程序版本
     * @return
             */
    public String getVersion(){
        String version = sharedPreferences.getString("version", "");
        return version;
    }
    public void setVersion(String version){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("version", version);
        editor.commit();
    }

    /**
     * android板硬件型号
     * @return
     */
    public String getAndroidDeviceModel(){
        String androidDeviceModel = sharedPreferences.getString("androidDeviceModel", "");
        return androidDeviceModel;
    }
    public void setAndroidDeviceModel(String androidDeviceModel){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("androidDeviceModel", androidDeviceModel);
        editor.commit();
    }

    /**
     * android版本
     * @return
     */
    public String getAndroidVersionRelease(){
        String androidVersionRelease = sharedPreferences.getString("androidVersionRelease", "");
        return androidVersionRelease;
    }
    public void setAndroidVersionRelease(String setAndroidVersionRelease){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("androidVersionRelease", setAndroidVersionRelease);
        editor.commit();
    }

    /**
     * 温湿传感器 - 温度
     * @return
     */
    public String getTemMeter(){
        String temMeter = sharedPreferences.getString("temMeter", "");
        return temMeter;
    }
    public void setTemMeter(String temMeter){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("temMeter", temMeter);
        editor.commit();
    }

    /**
     * 温湿传感器 - 湿度
     * @return
     */
    public String getTheMeter(){
        String theMeter = sharedPreferences.getString("theMeter", "");
        return theMeter;
    }
    public void setTheMeter(String theMeter){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theMeter", theMeter);
        editor.commit();
    }

    /**
     * 电表走字
     * @return
     */
    public String getEleMeter(){
        String eleMeter = sharedPreferences.getString("eleMeter", "");
        return eleMeter;
    }
    public void setEleMeter(String eleMeter){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eleMeter", eleMeter);
        editor.commit();
    }


    /**
     * 获取存在sp里面的电柜ID - 电柜左上角5位ID 如：04531
     * @return
     */
    public String getCabinetNumber_XXXXX(){
        String cabNumber = sharedPreferences.getString("cabinetNumber_XXXXX","00000");
        return cabNumber;
    }
    public void setCabinetNumber_XXXXX(String cabinetNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cabinetNumber_XXXXX", cabinetNumber);
        editor.commit();
    }

    /**
     * 获取存在sp里面的电柜ID - 4g卡4600开头号码
     * @return
     */
    public String getCabinetNumber_4600XXXX(){
        String cabNumber = sharedPreferences.getString("getCabinetNumber_4600XXXX","");
        return cabNumber;
    }
    public void setCabinetNumber_4600XXXX(String cabinetNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("getCabinetNumber_4600XXXX", cabinetNumber);
        editor.commit();
    }

    /**
     * 获取存在sp里面的400电话
     * @return
     */
    public String getTelNumber(){
        String cabNumber = sharedPreferences.getString("tel","");
        return cabNumber;
    }
    public void setTelNumber(String telNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tel", telNumber);
        editor.commit();
    }

    /**
     * 获取存在sp里面的线程保护参数
     * TPT：thread_protection_type 线程保护参数
     * @return
     */
    public String getTPTNumber(){
        String cabNumber = sharedPreferences.getString("thread_protection_type","1");
        return cabNumber;
    }
    public void setTPTNumber(String telNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("thread_protection_type", telNumber);
        editor.commit();
    }

    /**
     * 获取存在sp里面的dbm
     * @return
     */
    public String getDBM(){
        String dbm = sharedPreferences.getString("dbm","0");
        return dbm;
    }
    public void setDBM(String telNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dbm", telNumber);
        editor.commit();
    }
    /**
     * 设置sp里面 电柜位置
     */
    public void setAddress(String chargeMode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("setAddress", chargeMode);
        editor.commit();
    }
    public String getAddress(){
        String longLinkCabNumber = sharedPreferences.getString("setAddress","0");
        return longLinkCabNumber;
    }

    /**
     * 获取存在sp里面的最后的电柜数据
     * @return
     */
    public String getLastCabInfo(){
        String lastCabInfo = sharedPreferences.getString("lastCabInfo","");
        return lastCabInfo;
    }
    public void setLastCabInfo(String lastCabInfo){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastCabInfo", lastCabInfo);
        editor.commit();
    }


    /**
     * 获取存在sp里面的线程保护参数
     * TPT：thread_protection_type 线程保护参数
     * @return
     */
    public String getMaxPower(){
        String maxPower = sharedPreferences.getString("maxPower","60000");
        return maxPower;
    }
    public void setMaxPower(String maxPower){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("maxPower", maxPower);
        editor.commit();
    }


    /**
     * 设置sp里面 电柜的充电模式
     */
    public void setChargeMode(String chargeMode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("setChargeMode", chargeMode);
        editor.commit();
    }
    public String getChargeMode(){
        String longLinkCabNumber = sharedPreferences.getString("setChargeMode","0");
        return longLinkCabNumber;
    }

    /**
     * 设置sp里面 服务器选择
     */

    public void setServer(String type){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("server", type);
        editor.commit();
    }
    public String getServer(){
        String server = sharedPreferences.getString("server","");
        return server;
    }

}
