package com.NewElectric.app11.service.logic.logic_timeThread;


import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.model.dao.sharedPreferences.ForbiddenSp;
import com.NewElectric.app11.model.dao.sqlLite.ExchangeInfoDB;
import com.NewElectric.app11.model.dao.sqlLite.OutLineExchangeSaveInfo;
import com.NewElectric.app11.service.BaseLogic;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttp;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttpParameterFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;
import com.NewElectric.app11.service.logic.logic_writeUid.WriteUid;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class TimeThread extends BaseLogic {

    //单例
    private static TimeThread instance = new TimeThread();

    private TimeThread() {
    }

    public static TimeThread getInstance() {
        return instance;
    }

    private Context context;
    private Thread thread;
    private int threadCode = 0;

    private void sendData(String time) { //更新时间
        setChanged();
        notifyObservers(new DataFormat<>("time", time));
    }

    private void sendData(int power) { //更新功率
        setChanged();
        notifyObservers(new DataFormat<>("power", power));
    }

    private void sendData(String qrCode, int a) { //更新时间
        setChanged();
        notifyObservers(new DataFormat<>("qrCode", qrCode));
    }

    public void timeInit(Context context) {
        this.context = context;
        BaseLogicInit(context);
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(10 * 1000);
                        //开机推出禁用舱门
                        Observable.intervalRange(0, 9, 0, 3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                int i = Integer.parseInt(aLong + "");
                                int outIn = new ForbiddenSp(context).getTargetForbidden(i);
                                if (outIn == -2 && controlPlateInfo.getControlPlateBaseBeans()[i].getBID().equals("0000000000000000") && controlPlateInfo.getControlPlateBaseBeans()[i].getInching_3() == 0) {
                                    push(i + 1);
                                    sleep(3000);
                                }
                            }
                        });
                        //更新二维码
                        getQrCode();
                        //时间循环进程
                        while (threadCode == 0) {

                            //设置时间UI
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
                            Date curDate = new Date(System.currentTimeMillis());
                            String str = formatter.format(curDate);
                            sendData(str);

                            Calendar calendar = Calendar.getInstance();
                            int minute = calendar.get(Calendar.MINUTE);
                            int second = calendar.get(Calendar.SECOND);
                            int hour = calendar.get(Calendar.HOUR);

                            if (second % 10 == 0) {

                                //返回功率
                                int gonglv_total = 0;
                                for (int i = 0; i < 9; i++) {
                                    int gonglv_item = (int) (controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryElectric() / 1000) * (int) (controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryVoltage() / 1000);
                                    gonglv_total = gonglv_total + gonglv_item;
                                }
                                sendData(gonglv_total);

                                //删除数据库操作
                                ExchangeInfoDB exchangeInfoDB = ExchangeInfoDB.getInstance(context);
                                OutLineExchangeSaveInfo outLineExchangeSaveInfo = exchangeInfoDB.getLastInfo();
                                if (outLineExchangeSaveInfo != null) {
                                    List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", outLineExchangeSaveInfo.getNumber()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("uid32", outLineExchangeSaveInfo.getUid()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("extime", outLineExchangeSaveInfo.getExtime()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("in_battery", outLineExchangeSaveInfo.getInBattery()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("in_door", outLineExchangeSaveInfo.getInDoor()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("in_electric", outLineExchangeSaveInfo.getInElectric()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("out_battery", outLineExchangeSaveInfo.getOutBattery()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("out_door", outLineExchangeSaveInfo.getOutDoor()));
                                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("out_electric", outLineExchangeSaveInfo.getOutElectric()));
                                    BaseHttp baseHttp = new BaseHttp(HttpUrlMap.UploadExchangeLog, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                                        @Override
                                        public void dataReturn(int code, String message, String data) {
                                            if (code == 1) {
                                                final String fExtime = outLineExchangeSaveInfo.getExtime();
                                                exchangeInfoDB.deleteData(fExtime);
                                            }
                                        }
                                    });
                                    baseHttp.onStart();
                                    System.out.println("网络：   正在上传数据库数据   " + outLineExchangeSaveInfo.getExtime());
                                }

                                if (minute % 10 == 0 && second == 2) {
                                    //更新二维码
                                    getQrCode();
                                    //todo::接口没有
//                                    //上传GMS
//                                    Map<String, String> map = new GSMCellLocation(context).getGSMCell();
//                                    if (map != null) {
//                                        HttpUploadGMS httpUploadGMS = new HttpUploadGMS(cabInfoSp.getCabinetNumber_4600XXXX(), map.get("mcc"), map.get("mnc"), map.get("lac"), map.get("cellId"));
//                                        httpUploadGMS.start();
//                                    }
                                    //如果十分钟内没有收到控制板数据的返回 下发一条50字节的485命令 重启该控制板
                                    for (int i = 0; i < MAX_CABINET_COUNT; i++) {
                                        long nowTimeData = System.currentTimeMillis();
                                        long lastTimeData = controlPlateInfo.getControlPlateBaseBeans()[i].getDataTime();
                                        System.out.println("CAN - 充电机 - 下发：   " + (nowTimeData - lastTimeData));
                                        if (nowTimeData - lastTimeData > 10 * 60 * 1000) {
                                            byte[] order = new byte[50];
                                            Arrays.fill(order, (byte) 0xff);
                                            MyApplication.serialAndCanPortUtils.serSendOrder(order);
                                            final int fi = i;
                                            Observable.timer(10,TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                                                @Override
                                                public void accept(Long aLong) throws Exception {
                                                    pull(fi + 1);
                                                }
                                            });
                                            break;
                                        }
                                    }
                                    //每三分钟判断一下 电柜里面 擦写失败的电池 重写写成AAAAAAAA
                                    if (minute % 3 == 0 && second == 0) {
                                        for (int i = 0; i < MAX_CABINET_COUNT; i++) {
                                            if (forbiddenSp.getTargetForbidden(i) == -3) {
                                                if (controlPlateInfo.getControlPlateBaseBeans()[i].getBID().equals("AAAAAAAA") || controlPlateInfo.getControlPlateBaseBeans()[i].getBID().equals("00000000")) {
                                                    forbiddenSp.setTargetForbidden(i, 1);
                                                } else {
                                                    WriteUid.getInstance().write(i+1,"AAAAAAAA", "换电当时插入电池UID没清除掉，每三分钟下发清除UID");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    private void getQrCode() {
        List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", cabInfoSp.getCabinetNumber_4600XXXX()));
        new BaseHttp(HttpUrlMap.GetQrCodeUrl, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
            @Override
            public void dataReturn(int code, String message, String data) {
                if (code == 1) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String url = jsonObject.getString("url");
                        sendData(url, 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void onDestroy() {
        threadCode = 1;
    }
}
