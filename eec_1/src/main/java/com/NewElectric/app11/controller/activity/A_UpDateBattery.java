package com.NewElectric.app11.controller.activity;

import android.os.Bundle;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.service.logic.logic_update.SerialPortRwAction;
import com.NewElectric.app11.service.logic.logic_update.UpgradeCallBack;
import com.NewElectric.app11.service.logic.logic_update.battery.BatteryUpgradeDispatcher;
import com.NewElectric.app11.service.logic.logic_update.battery.BatteryUpgradeMessage;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;


/**
 * Created by guo on 2017/12/2.
 * 电池升级页面
 *
 * activity基础类 - 硬件升级基础类 - 电池升级页面
 * BaseActivity - BaseUpdateActivity - A_UpDateBattery
 */


public class A_UpDateBattery extends BaseUpdateActivity{

    //485接收数据缓存
    private byte[] serReturnByte = null;
    //电池升级最大时间 也是 电池can数据挂起时间
    private int MAX_TIME = 900;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置电池升级最大时间
        timeCount = MAX_TIME;
        init();
        title.setText("正在升级第" + door + "号舱门电池，请稍候！");
    }

    private void init() {

        BatteryUpgradeDispatcher.getInstance().init(new SerialPortRwAction() {
            @Override
            public void write(byte[] bytes) {
                MyApplication.serialAndCanPortUtils.serSendOrder(bytes);
                System.out.println("UpDataBattery：   下发 - " + Arrays.toString(bytes));
            }

            @Override
            public byte[] read() {
                byte[] AAA = serReturnByte;
                serReturnByte = null;
                System.out.println("UpDataBattery：   返回 - " + Arrays.toString(AAA));
                return AAA;
            }
        });

        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {

                System.out.println("UpDataBattery：   开始");
                updateLog("开始升级");

                BatteryUpgradeMessage batteryUpgradeMessage = new BatteryUpgradeMessage((byte) (door + 4));
                batteryUpgradeMessage.setManufacturer(type);
                batteryUpgradeMessage.setFilePath(path);
                batteryUpgradeMessage.setUpgradeCallBack(new UpgradeCallBack() {
                    @Override
                    public void onUpgradeBefore(byte address) {
                        System.out.println("UpDataBattery：   准备中 - " + address);
                        updateLog("准备中");
                    }

                    @Override
                    public void onUpgrade(byte address, long process, long total) {
                        System.out.println("UpDataBattery：   升级中 - " + address + "   " + process + "   " + "    " + total);
                        updateLog("升级中 ： 发送帧 - " + process + "   总帧 - " + total);
                        updateBar(process, total);
                    }

                    @Override
                    public void onError(byte address, String errorInfo) {
                        System.out.println("UpDataBattery：   错误 - " + errorInfo);
                        updateLog("升级失败 - " + errorInfo);
                        showDialog("电池升级出错，程序将在10秒后返回！", 10);
                        finishUpdate();
                    }

                    @Override
                    public void onUpgradeAfter(byte address) {
                        System.out.println("UpDataBattery：   成功");
                        updateLog("升级成功!!!!!");
                        showDialog("电池升级成功，程序将在10秒后返回！", 10);
                        finishUpdate();
                    }
                });
                BatteryUpgradeDispatcher.getInstance().dispatch(batteryUpgradeMessage);
            }
        });
    }

    @Override
    public void update(java.util.Observable observable, Object object) {
        super.update(observable, object);
        DataFormat dataFormat = (DataFormat)object;
        if(dataFormat.getType().equals("serial")){
            serReturnByte = (byte[])dataFormat.getData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeCount = 0;
    }
}