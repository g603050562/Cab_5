package com.NewElectric.app11.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateInfo;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduInfo;
import com.NewElectric.app11.hardwarecomm.agreement.send.controlPlate.ControlPlateSend;
import com.NewElectric.app11.hardwarecomm.agreement.send.pdu.PduSend;
import com.NewElectric.app11.hardwarecomm.androidHard.CanDataFormat;
import com.NewElectric.app11.model.dao.fileSave.LocalLog;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.model.dao.sharedPreferences.ForbiddenSp;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateService;
import com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_3.Charging_1_to_3;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttp;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttpParameterFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;
import com.NewElectric.app11.units.RootCommand;
import com.NewElectric.app11.units.dialog.DialogAdmin;

/**
 * Created by guo on 2017/12/2.
 * activity基础类
 */

public class BaseActivity extends Activity {

    protected Activity activity;
    //电柜参数储存
    protected CabInfoSp cabInfoSp;
    //电柜禁用储存
    protected ForbiddenSp forbiddenSp;
    //最大舱门数
    protected int MAX_CABINET_COUNT = 9;
    //最大充电机数量(控制板未改装)
    protected int MAX_CHARGE_COUNT = 3;
    //最大acdc数量
    protected int MAX_ACDC_COUNT = 2;
    //舱门弹出挂起时间
    protected int PUSH_DOOR_HANG_ON_TIME = 20;
    //黑色dialog框
    protected DialogAdmin dialogAdmin;
    //ttl语音播报
    protected TextToSpeech textToSpeech;
    //控制板信息
    protected ControlPlateInfo controlPlateInfo;
    //pdu信息
    protected PduInfo pduInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        //控制板内存数据初始化
        controlPlateInfo = CanControlPlateService.getInstance().getControlPlateInfo();
        pduInfo = CanControlPlateService.getInstance().getPduInfo();
        //其他功能初始化
        activity = this;
        cabInfoSp = new CabInfoSp(activity);
        forbiddenSp = new ForbiddenSp(activity);
        dialogAdmin = new DialogAdmin(activity);
        //获取root权限
        RootCommand rootCommand = new RootCommand();
        rootCommand.RootCommandStart("chmod 777 " + getPackageCodePath());
        //activity注册
        MyApplication.getInstance().addActivity(this);
        //语音初始化
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE) {
                        System.out.println("TTS：TTS暂时不支持这种语音的朗读！");
                    }
                }
            }
        });
    }

    //收回推杆
    protected void pull(int door){
        MyApplication.serialAndCanPortUtils.canSendOrder(ControlPlateSend.canPlateShrink(door));
    }
    //退出推杆
    protected void push(int door){
        MyApplication.serialAndCanPortUtils.canSendOrder(ControlPlateSend.canPlateElongation(door));
    }

    //推出再收回
    protected void pushAndPull(int door , String reason){
        CanControlPlateService.getInstance().cancelHangUpDoor(door);
        CanControlPlateService.getInstance().cleanUpDoor(door ,PUSH_DOOR_HANG_ON_TIME);
        writeLog("电池弹出 - 舱门 - " + door + " - 原因 - " +reason);
        Charging_1_to_3.getInstance().startChange(door);
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    //关闭pdu
                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(door , 64800 , 64800));
                    sleep(50);
                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(door , 64800 , 64800));
                    sleep(50);
                    //待机两秒 防止拉弧
                    sleep(2000);
                    //推出推杆
                    MyApplication.serialAndCanPortUtils.canSendOrder(ControlPlateSend.canPlateElongationAndShrink(door));
                    sleep(9000);
                    //重启控制板
                    MyApplication.serialAndCanPortUtils.canSendOrder(ControlPlateSend.canPlateReboot(door));
                    sleep(7000);
                    //解除挂起其他所有舱门 这个舱门的挂起还是由时间决定
                    for(int i = 0 ; i < MAX_CABINET_COUNT ; i++){
                        if(i+1 != door){
                            CanControlPlateService.getInstance().cancelHangUpDoor(i+1);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        List<BaseHttpParameterFormat> baseHttpParameterFormats =  new ArrayList<>();
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("number",cabInfoSp.getCabinetNumber_4600XXXX()));
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("door",door+""));
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("battery",controlPlateInfo.getControlPlateBaseBeans()[door-1].getBID()));
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("remark",reason));
        BaseHttp baseHttp = new BaseHttp(HttpUrlMap.UploadOpenDoorLog,baseHttpParameterFormats);
        baseHttp.onStart();
    }

    //写入本地日志
    protected void writeLog(String message){
        LocalLog.getInstance().writeLog(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogAdmin.destory();
        textToSpeech.shutdown();
        textToSpeech.stop();
    }
}
