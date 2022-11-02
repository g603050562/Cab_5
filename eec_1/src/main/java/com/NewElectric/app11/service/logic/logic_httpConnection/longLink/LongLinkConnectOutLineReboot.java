package com.NewElectric.app11.service.logic.logic_httpConnection.longLink;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.service.BaseLogic;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttp;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttpParameterFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;
import com.NewElectric.app11.units.RootCommand;

/**
 * 长链接定时重启
 */
public class LongLinkConnectOutLineReboot extends BaseLogic {

    //单例
    private static LongLinkConnectOutLineReboot instance = new LongLinkConnectOutLineReboot();
    private LongLinkConnectOutLineReboot() {}
    public static LongLinkConnectOutLineReboot getInstance() {
        return instance;
    }

    private Thread outLineRebootThread = null;
    private int outLineRebootThreadCode = 1;
    private int timeParam;
    private int count = 0;

    public void OutLineRebootInit(Context context) {
        Calendar calendar = Calendar.getInstance();
        int second = calendar.get(Calendar.SECOND);
        timeParam = second;
        onStart();
    }

    private void sendData(LongLinkConnectDialogFormat longLinkConnectDialogFormat){
        setChanged();
        notifyObservers(new DataFormat<>("showDialog", longLinkConnectDialogFormat));
    }

    public void onStart() {
        if (outLineRebootThread == null) {
            outLineRebootThread = new Thread() {
                @Override
                public void run() {
                    super.run();

                    while (outLineRebootThreadCode == 1) {

                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Calendar calendar = Calendar.getInstance();
                        int second = calendar.get(Calendar.SECOND);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);

                        if (hour == 2 || hour == 3) {

                            if (timeParam == second) {
                                List<BaseHttpParameterFormat> baseHttpParameterFormats =  new ArrayList<>();
                                BaseHttp baseHttp = new BaseHttp(HttpUrlMap.HttpBeat, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                                    @Override
                                    public void dataReturn(int code, String message, String data) {
                                        if (code == 1) {
                                            count = 0;
                                        } else if (code == -1) {
                                            count = count + 1;
                                            writeLog(message);
                                        }
                                    }
                                });
                                baseHttp.onStart();
                            }

                            if (count >= 10) {
                                try {
                                    sendData(new LongLinkConnectDialogFormat("电柜将在30秒后重启，请勿进行操作！",30,1));
                                    sleep(10000);
                                    sendData(new LongLinkConnectDialogFormat("电柜将在20秒后重启，请勿进行操作！",20,1));
                                    sleep(10000);
                                    sendData(new LongLinkConnectDialogFormat("电柜将在10秒后重启，请勿进行操作！",10,1));
                                    sleep(10000);
                                    writeLog("无网络心跳，重新启动！");
                                    new RootCommand().RootCommandStart("reboot");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            };
            outLineRebootThread.start();
        }
    }

    public void onDestery() {
        outLineRebootThreadCode = 0;
    }

}
