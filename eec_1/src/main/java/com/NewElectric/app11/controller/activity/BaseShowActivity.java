package com.NewElectric.app11.controller.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Observable;
import java.util.Observer;

import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateService;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateServiceInfoBeanFormat;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateServicePduInfoBeanFormat;
import com.NewElectric.app11.service.canAndSerial.serial.SerialService;
import com.NewElectric.app11.service.canAndSerial.serial.SerialServiceReturnFormat;
import com.NewElectric.app11.service.logic.logic_exchange.cabinetOfPdu.ExchangeBarOutLine;
import com.NewElectric.app11.service.logic.logic_exchange.cabinetOfPdu.ExchangeBarOutLineStartAnimation;
import com.NewElectric.app11.service.logic.logic_find4gCard.Find4gCard;
import com.NewElectric.app11.service.logic.logic_find4gCard.Find4gCardReturnDataFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnect;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnectDialogFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnectOutLineReboot;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnectUpdateHardWare;
import com.NewElectric.app11.service.logic.logic_netDBM.CurrentNetDBM;
import com.NewElectric.app11.service.logic.logic_timeThread.TimeThread;

public class BaseShowActivity extends BaseActivity implements Observer {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void initDataService() {
        SerialService.getInstance().addObserver(this);
        CanControlPlateService.getInstance().addObserver(this);
    }
    protected void initOtherService() {
        Find4gCard.getInstance().addMyObserver(this);
        CurrentNetDBM.getInstance().addObserver(this);
        LongLinkConnect.getInstance().addObserver(this);
        LongLinkConnectOutLineReboot.getInstance().addObserver(this);
        TimeThread.getInstance().addObserver(this);
        ExchangeBarOutLine.getInstance().addObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SerialService.getInstance().deleteObserver(this);
        CanControlPlateService.getInstance().deleteObserver(this);
        Find4gCard.getInstance().deleteObserver(this);
        CurrentNetDBM.getInstance().deleteObserver(this);
        LongLinkConnect.getInstance().deleteObserver(this);
        LongLinkConnectOutLineReboot.getInstance().deleteObserver(this);
        TimeThread.getInstance().deleteObserver(this);
        ExchangeBarOutLine.getInstance().deleteObserver(this);
    }

    /**
     * 注册返回参数
     * SerialService：
     *      - _485 485数据返回刷新 主要是温湿传感器和电表数据刷新
     * CanControlPlateService：
     *      - batteryBaseBean 电池基础数据返回触发刷新
     *      - batteryWarningBean 电池预警数据返回触发刷新
     *      - pduBean PDU数据返回触发刷新
     * Find4gCard：
     *      - find4G 找到4g卡后完成初始化的触发
     * dbm：
     *      - dbm DBM信号刷新触发
     * LongLinkConnect：
     *      - onlineType 网络状态返回
     *      - openAdmin 打开电柜后台
     *      - showDialog 显示提示框
     *      - updateBatteryUI 更新电池UI
     *      - updateHardWare 更新硬件
     *      - updateCabinetUI 更新电柜信息
     * @param observable
     * @param object
     */
    @Override
    public void update(Observable observable, Object object) {

        DataFormat dataFormat = (DataFormat)object;
        if(dataFormat.getType().equals("_485")){
            _485Return((SerialServiceReturnFormat) dataFormat.getData());
        }else if(dataFormat.getType().equals("batteryBaseBean")){
            batteryBaseBeanReturn((CanControlPlateServiceInfoBeanFormat)dataFormat.getData());
        }else if(dataFormat.getType().equals("batteryWarningBean")){
            batteryWarningBeanReturn((CanControlPlateServiceInfoBeanFormat) dataFormat.getData());
        }else if(dataFormat.getType().equals("pduBean")){
            pduBeanReturn((CanControlPlateServicePduInfoBeanFormat) dataFormat.getData());
        }else if(dataFormat.getType().equals("find4G")){
            find4gCardReturn((Find4gCardReturnDataFormat) dataFormat.getData());
        }else if(dataFormat.getType().equals("dbm")){
            dbmReturn((int) dataFormat.getData());
        }else if(dataFormat.getType().equals("onlineType")){
            onLineType((String) dataFormat.getData());
        }else if(dataFormat.getType().equals("openAdmin")){
            openAdmin((int) dataFormat.getData());
        }else if(dataFormat.getType().equals("showDialog")){
            showDialog((LongLinkConnectDialogFormat) dataFormat.getData());
        }else if(dataFormat.getType().equals("updateBatteryUI")){
            updateBatteryUI((int) dataFormat.getData());
        } else if(dataFormat.getType().equals("updateCabinetUI")){
            updateCabinetUI();
        }else if(dataFormat.getType().equals("updateHardWare")){
            updateHardWare((LongLinkConnectUpdateHardWare) dataFormat.getData());
        }else if(dataFormat.getType().equals("pushAndPull")){
            pushAndPullUpdateUi((int) dataFormat.getData());
        }else if(dataFormat.getType().equals("time")){
            setTime((String) dataFormat.getData());
        }else if(dataFormat.getType().equals("power")){
            setPower((int) dataFormat.getData());
        }else if(dataFormat.getType().equals("qrCode")){
            setQrCode((String) dataFormat.getData());
        }else if(dataFormat.getType().equals("exchangeAnimation")){
            startAnimation((ExchangeBarOutLineStartAnimation) dataFormat.getData());
        }else if(dataFormat.getType().equals("inchingAnimation")){
            inchingAnimation((int) dataFormat.getData());
        }
    }

    /**
     *  以下方法为监听 分发
     *
     */

    public void _485Return(SerialServiceReturnFormat serialServiceReturnFormat){

    }

    public void batteryBaseBeanReturn(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat){
    }

    public void batteryWarningBeanReturn(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat){

    }

    public void pduBeanReturn(CanControlPlateServicePduInfoBeanFormat canControlPlateServicePduInfoBeanFormat){

    }

    public void find4gCardReturn(Find4gCardReturnDataFormat canServicePduBeanFormat){

    }

    public void dbmReturn(int dbm){

    }

    public void onLineType(String onLineType){

    }

    public void openAdmin(int openAdmin){

    }

    public void showDialog(LongLinkConnectDialogFormat longLinkConnectDialogFormat){
    }

    public void updateBatteryUI(int door){

    }

    public void updateCabinetUI(){

    }

    public void updateHardWare(LongLinkConnectUpdateHardWare longLinkConnectUpdateHardWare){

    }

    public void pushAndPullUpdateUi(int door){

    }

    public void setTime(String timeStr){

    }

    public void setPower(int power){

    }

    public void setQrCode(String qrCode){

    }

    public void inchingAnimation(int door){

    }


    public void startAnimation(ExchangeBarOutLineStartAnimation exchangeBarOutLineStartAnimation){

    }

}
