package com.NewElectric.app11.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateBaseBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateInfo;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateWarningBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduInfo;
import com.NewElectric.app11.hardwarecomm.agreement.send.controlPlate.ControlPlateSend;
import com.NewElectric.app11.hardwarecomm.agreement.send.pdu.PduSend;
import com.NewElectric.app11.hardwarecomm.androidHard.CanDataFormat;
import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.model.dao.fileSave.LocalLog;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.model.dao.sharedPreferences.ForbiddenSp;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateService;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateServiceInfoBeanFormat;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateServicePduInfoBeanFormat;
import com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_3.Charging_1_to_3;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttp;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttpParameterFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;
import com.NewElectric.app11.service.logic.logic_netDBM.CurrentNetDBM;

public class BaseLogic extends BaseLogicObservable implements Observer {

    //控制板信息
    protected ControlPlateInfo controlPlateInfo;
    //pdu信息
    protected PduInfo pduInfo;
    //最大舱门数
    protected int MAX_CABINET_COUNT = 9;
    //最大充电机数量
    protected int MAX_CHARGE_COUNT = 3;
    //舱门弹出挂起时间
    protected int PUSH_DOOR_HANG_ON_TIME = 20;
    //电柜参数储存
    protected CabInfoSp cabInfoSp;
    //电柜禁用参数
    protected ForbiddenSp forbiddenSp;

    public void BaseLogicInit(Context context) {
        controlPlateInfo = new ControlPlateInfo();
        pduInfo = CanControlPlateService.getInstance().getPduInfo();
        cabInfoSp = new CabInfoSp(context);
        forbiddenSp = new ForbiddenSp(context);
        CanControlPlateService.getInstance().addObserver(this);
        CurrentNetDBM.getInstance().addObserver(this);
    }


    @Override
    public void update(Observable observable, Object object) {
        DataFormat dataFormat = (DataFormat) object;
        if (dataFormat.getType().equals("batteryBaseBean")) {
            batteryBaseBeanReturn((CanControlPlateServiceInfoBeanFormat) dataFormat.getData());
        } else if (dataFormat.getType().equals("batteryWarningBean")) {
            batteryWarningBeanReturn((CanControlPlateServiceInfoBeanFormat) dataFormat.getData());
        } else if (dataFormat.getType().equals("pduBean")) {
            pduBeanReturn((CanControlPlateServicePduInfoBeanFormat) dataFormat.getData());
        } else if(dataFormat.getType().equals("dbm")){
            dbmReturn((int) dataFormat.getData());
        }
    }

    private void pushAndPullSendData(int door) {
        setChanged();
        notifyObservers(new DataFormat<>("pushAndPull", door));
    }

    public void batteryBaseBeanReturn(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat) {
        //获取信息
        int index = canControlPlateServiceInfoBeanFormat.getIndex();
        //更新内存数据
        controlPlateInfo.getControlPlateBaseBeans()[index] = (ControlPlateBaseBean) canControlPlateServiceInfoBeanFormat.getData();
    }

    public void batteryWarningBeanReturn(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat) {
        controlPlateInfo.getControlPlateWarningBeans()[canControlPlateServiceInfoBeanFormat.getIndex()] = (ControlPlateWarningBean) canControlPlateServiceInfoBeanFormat.getData();
    }

    public void pduBeanReturn(CanControlPlateServicePduInfoBeanFormat canControlPlateServicePduInfoBeanFormat) {
        pduInfo.setPduChargingInfoBean(canControlPlateServicePduInfoBeanFormat.getPduChargingInfoBean());
        pduInfo.setPduEquipmentBeans(canControlPlateServicePduInfoBeanFormat.getPduEquipmentBean());
    }

    public void dbmReturn(int dbm){

    }


    public void onDestroy() {
        CanControlPlateService.getInstance().deleteObserver(this);
        CurrentNetDBM.getInstance().deleteObserver(this);
    }

    //收回推杆
    protected void pull(int door) {
        MyApplication.serialAndCanPortUtils.canSendOrder(ControlPlateSend.canPlateShrink(door));
    }

    //退出推杆
    protected void push(int door) {
        MyApplication.serialAndCanPortUtils.canSendOrder(ControlPlateSend.canPlateElongation(door));
    }

    //退出再收回
    protected void pushAndPull(int door, String reason) {
        CanControlPlateService.getInstance().cancelHangUpDoor(door);
        CanControlPlateService.getInstance().cleanUpDoor(door,PUSH_DOOR_HANG_ON_TIME);
        writeLog("电池弹出 - 舱门 - " + door + " - 原因 - " +reason);
        Charging_1_to_3.getInstance().startChange(door);
        pushAndPullSendData(door);
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //关闭pdu
                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(door , 64800 , 3850));
                    sleep(50);
                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(door , 64800 , 3850));
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
    protected void writeLog(String message) {
        LocalLog.getInstance().writeLog(message);
    }
}
