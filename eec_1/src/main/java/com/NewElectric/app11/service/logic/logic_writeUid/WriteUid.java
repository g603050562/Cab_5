package com.NewElectric.app11.service.logic.logic_writeUid;

import android.content.Context;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.agreement.send.controlPlate.ControlPlateSend;
import com.NewElectric.app11.hardwarecomm.androidHard.CanDataFormat;
import com.NewElectric.app11.model.dao.fileSave.LocalLog;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.service.BaseLogic;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateService;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttp;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttpParameterFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;

import java.util.ArrayList;
import java.util.List;

/**
 * 写入UID给目标电池
 */
public class WriteUid extends BaseLogic{

    public interface WriteUidListener{
        void showDialog(String message , int time , int type);
        void writeUidResult(boolean result);
    }

    private static volatile  WriteUid writeUid;
    private WriteUid(){};
    public static WriteUid getInstance(){
        if(writeUid == null){
            synchronized (WriteUid.class){
                if(writeUid == null){
                    writeUid = new WriteUid();
                }
            }
        }
        return writeUid;
    }

    private int door = 0;
    private int index = 0;
    private String uid = "";
    private int exchangeFailCount = 0;
    private WriteUidListener writeUidListener = null;

    public void init(Context context){
        BaseLogicInit(context);
    }

    public void write(int door , String uid , String reason , WriteUidListener writeUidListener){
        this.door = door;
        this.index = door - 1;
        this.uid = uid;
        this.writeUidListener = writeUidListener;
        onStart();
    }

    public void write(int door , String uid , String reason){
        write(door , uid , reason , null);
    }

    private void onStart(){

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    int result_out = 0;
                    for (int i = 0; i < 140; i++) {
                        sleep(100);
                        if (i == 0 || i == 70) {
                            //解除挂起舱门
                            CanControlPlateService.getInstance().cancelHangUpDoor(door);
                            //下发写入弹出电池ID
                            writeBatteryCheckCode(door,uid);
                            //写入日志
                            LocalLog.getInstance().writeLog("电池写入 - 舱门 - " + door +" - UID - " + uid);
                        }
                        String sUID = controlPlateInfo.getControlPlateBaseBeans()[index].getUID();
                        if (sUID.equals(uid)) {
                            result_out = 1;
                            break;
                        }
                    }

                    if (result_out == 0) {
                        //没有写入成功
                        if (exchangeFailCount == 0) {
                            if(writeUidListener!=null){
                                writeUidListener.showDialog("电池写入失败，正在尝试二次写入，请稍候！！", 10, 1);
                            }
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        sleep(10000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    onStart();
                                    exchangeFailCount = 1;
                                }
                            }.start();
                        } else {
                            exchangeFailCount = 0;
                            if(writeUidListener!=null) {
                                writeUidListener.writeUidResult(false);
                            }
                            LocalLog.getInstance().writeLog("电池写入失败");
                        }
                    } else {
                        exchangeFailCount = 0;
                        if(writeUidListener!=null) {
                            writeUidListener.writeUidResult(true);
                        }
                        LocalLog.getInstance().writeLog("电池写入成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //写入UID
    private void writeBatteryCheckCode(int door, String uid) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    CanDataFormat canDataFormat = ControlPlateSend.canWriteUid(door, uid);
                    String address = canDataFormat.getAddressByStr();
                    byte[] writeOrder = canDataFormat.getRawData();
                    byte[] a_1 = new byte[]{(byte)0x10, (byte)writeOrder[0], (byte)writeOrder[1], (byte)writeOrder[2], (byte)writeOrder[3], (byte)writeOrder[4], (byte)writeOrder[5], (byte)writeOrder[6]};
                    sleep(30);
                    byte[] a_2 = new byte[]{(byte)0x20, (byte)writeOrder[7], (byte)writeOrder[8], (byte)writeOrder[9], (byte)writeOrder[10], (byte)writeOrder[11], (byte)writeOrder[12], (byte)writeOrder[13]};
                    sleep(30);
                    byte[] a_3 = new byte[]{(byte)0x21, (byte)writeOrder[14], (byte)writeOrder[15]};
                    MyApplication.serialAndCanPortUtils.canSendOrder(address, a_1);
                    MyApplication.serialAndCanPortUtils.canSendOrder(address, a_2);
                    MyApplication.serialAndCanPortUtils.canSendOrder(address, a_3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
