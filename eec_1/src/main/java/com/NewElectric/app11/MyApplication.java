package com.NewElectric.app11;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.NewElectric.app11.hardwarecomm.androidHard.HardWareCommFactoryProducer;
import com.NewElectric.app11.hardwarecomm.androidHard.SerialAndCanPortUtils;
import com.NewElectric.app11.model.dao.fileSave.LocalLog;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateService;
import com.NewElectric.app11.service.canAndSerial.serial.SerialService;
import com.NewElectric.app11.service.logic.logic_charging.ChargingGetHardWare;
import com.NewElectric.app11.service.logic.logic_charging.ChargingUtils;
import com.NewElectric.app11.service.logic.logic_exchange.cabinetOfPdu.ExchangeBarOutLine;
import com.NewElectric.app11.service.logic.logic_find4gCard.Find4gCard;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnect;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnectOutLineReboot;
import com.NewElectric.app11.service.logic.logic_netDBM.CurrentNetDBM;
import com.NewElectric.app11.service.logic.logic_timeThread.TimeThread;
import com.NewElectric.app11.service.logic.logic_writeUid.WriteUid;
import com.NewElectric.app11.units.FilesDirectoryUnits;
import com.tencent.bugly.crashreport.CrashReport;

import client.halouhuandian.app11.MyService;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by guo on 2017/3/23.
 * 全局变量初始化
 * 数据接收中心 所有信息在这里汇总
 */

public class MyApplication extends Application  {

    //单例
    private static MyApplication instance = new MyApplication();
    //电柜版本信息
    private static String CAB_VERSION = "3.0.006(bete)";
    //完全推出缓存队列
    private static List<Activity> activitys = new LinkedList<Activity>();
    //电柜信息储存 SharedPreferences储存
    private static CabInfoSp cabInfoSp;

    public MyApplication() {}

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //获取设备信息
        cabInfoSp = new CabInfoSp(getApplicationContext());
        cabInfoSp.setAndroidDeviceModel(Build.MODEL);
        cabInfoSp.setAndroidVersionRelease(Build.VERSION.RELEASE);
        cabInfoSp.setVersion(CAB_VERSION+"");
        //初始化文件夹
        FilesDirectoryUnits.getInstance().init(getApplicationContext());
        //下层数据通信
        initHandCommSerialAndCanPortUtils();
        //上层数据通信
        initSoftCommSerialAndCanPortUtils();
        //bugly初始化
        CrashReport.setAppPackage(getApplicationContext(), cabInfoSp.getCabinetNumber_4600XXXX());
        CrashReport.initCrashReport(getApplicationContext(), "5c5e29ee54", false);
//        CrashReport.testJavaCrash();
    }

    /**
     * 初始化activity容器
     */

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activitys != null && activitys.size() > 0) {
            if (!activitys.contains(activity)) {
                activitys.add(activity);
            }
        } else {
            activitys.add(activity);
        }
    }

    public List<Activity> getActivity() {
        return activitys;
    }

    // 遍历所有Activity并finish
    public void exit() {
        System.out.println("Activity：ALL onDestory");
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                activity.finish();
            }
        }
        System.exit(0);
    }


    /**
     * 底层通信
     * 单例 统一从这里下发数据
     * 485串口 和 canbus 初始化 （不同android板的抽象工厂实现）
     * 原始数据分发（主要用于数据解析 或者 升级）
     */

    public static SerialAndCanPortUtils serialAndCanPortUtils = null;
    // 添加Listener到容器中
    public void initHandCommSerialAndCanPortUtils() {
        serialAndCanPortUtils = HardWareCommFactoryProducer.getFactory(cabInfoSp.getAndroidDeviceModel());
        serialAndCanPortUtils.openCanPortAndSerialPort();
    }

    /**
     * 上层通信
     * 485串口 和 can 初始化
     * 经过解析数据触发条件分发（主要用于更新数据）
     */

    private void initSoftCommSerialAndCanPortUtils(){

        //485串口控制
        SerialService.getInstance()._485Init(getApplicationContext());
        //can口控制
        CanControlPlateService.getInstance().canInit();
        //换电监听进程开启
        ExchangeBarOutLine.getInstance().ExchangeBarInit(getApplicationContext());
        //充电服务
        ChargingGetHardWare.getInstance().logicInit(getApplicationContext(), new ChargingGetHardWare.GetHardWareListener() {
            @Override
            public void returnData(String type) {
                ChargingUtils.getInstance(type).logicInit(getApplicationContext());
            }
        });
        //其他服务
        initService(getApplicationContext());
    }

    /**
     * 其他服务
     */

    private void initService(Context context) {

        //守护进程的服务
        Intent intent = new Intent(context, MyService.class);
        PendingIntent sender = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, sender);
        //获取4g卡信息服务
        Find4gCard.getInstance().find4gInit(context, new Find4gCard.Find4gCardListener() {
            @Override
            public void dataReturn() {
                //有4g卡返回了 然后开启长链接服务
                Observable.timer(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LongLinkConnect.getInstance().longLinkInit(context);
                        //心跳重启
                        LongLinkConnectOutLineReboot.getInstance().OutLineRebootInit(context);
                        //开时间线程
                        TimeThread.getInstance().timeInit(context);
                    }
                });
            }
        });
        //写uid初始化
        WriteUid.getInstance().init(getApplicationContext());
        //本地日志初始化
        LocalLog.getInstance().init(getApplicationContext(),cabInfoSp.getCabinetNumber_4600XXXX());
        //开启dbm服务（PhoneStateListener方法特殊 必须时刻依存context）
        CurrentNetDBM.getInstance().DBMInit(getApplicationContext());
        //初始化服务器
        HttpUrlMap.setServer(cabInfoSp.getServer());
    }
}

