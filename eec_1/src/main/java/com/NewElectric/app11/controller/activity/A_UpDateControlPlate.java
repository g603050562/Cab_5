package com.NewElectric.app11.controller.activity;

import android.os.Bundle;
import android.view.View;


import java.util.concurrent.TimeUnit;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.service.logic.logic_update.SerialPortRwAction;
import com.NewElectric.app11.service.logic.logic_update.UpgradeCallBack;
import com.NewElectric.app11.service.logic.logic_update.controlplate.ControlPanelUpgradeDispatcher;
import com.NewElectric.app11.service.logic.logic_update.controlplate.ControlPanelUpgradeMessage;
import com.NewElectric.app11.units.Units;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by guo on 2017/12/2.
 * 控制板升级页面
 * <p>
 * activity基础类 - 硬件升级基础类 - 控制板升级页面
 * BaseActivity - BaseUpdateActivity - A_UpDateControlPlate
 */

public class A_UpDateControlPlate extends BaseUpdateActivity {

    private byte[] serReturnByte = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置电池升级最大时间
        timeCount = 120;
        init();
        p_bar_2.setVisibility(View.VISIBLE);
        title.setText("正在升级第" + door + "号舱门控制板，请稍候！");

    }

    private void init() {

        ControlPanelUpgradeDispatcher.getInstance().init(new SerialPortRwAction() {
            @Override
            public void write(byte[] bytes) {
                MyApplication.serialAndCanPortUtils.serSendOrder(bytes);
                System.out.println("UpDataControlPanel：   下发 - " + Units.ByteArrToHex(bytes));
            }
            @Override
            public byte[] read() {
                byte[] AAA = serReturnByte;
                serReturnByte = null;
                if (AAA != null) {
                    System.out.println("UpDataControlPanel：   返回 - " + Units.ByteArrToHex(AAA));
                }
                return AAA;
            }
        });
        updataControlPanel();
    }

    @Override
    public void update(java.util.Observable observable, Object object) {
        super.update(observable, object);
        DataFormat dataFormat = (DataFormat)object;
        if(dataFormat.getType().equals("serial")){
            serReturnByte = (byte[])dataFormat.getData();
        }
    }

    // type = 1 是整柜升级   type = 2 是单个仓升级
    private void updataControlPanel() {

        updateBar_2(door, MAX_CABINET_COUNT);
        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {

                System.out.println("UpDataBattery：   开始");
                updateLog("开始升级 - " + door);

                ControlPanelUpgradeMessage controlPanelUpgradeMessage = new ControlPanelUpgradeMessage((byte) door);
                controlPanelUpgradeMessage.setFilePath(path);
                controlPanelUpgradeMessage.setUpgradeCallBack(new UpgradeCallBack() {
                    @Override
                    public void onUpgradeBefore(byte address) {
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
                        System.out.println("UpDataControlPanel：   错误 - " + errorInfo);
                        updateLog("升级失败 - " + errorInfo);
                        showDialog("控制板升级失败，" + errorInfo + "，程序将在10秒后返回！", 10);
                        finishUpdate();
                    }

                    @Override
                    public void onUpgradeAfter(byte address) {
                        timeCount = 120;
                        if (type.equals("2")) {
                            updateLog("控制板" + address + " - 升级成功");
                            showDialog("控制板升级成功，程序将在10秒后返回！", 10);
                            finishUpdate();
                        } else if (type.equals("1")) {
                            Observable.timer(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    if (door > MAX_CABINET_COUNT - 1) {
                                        showDialog("控制板升级成功，程序将在10秒后返回！", 10);
                                        finishUpdate();
                                    } else {
                                        updateLog(door + "号仓升级成功");
                                        door = door + 1;
                                        updataControlPanel();
                                    }
                                }
                            });
                        }
                    }
                });
                ControlPanelUpgradeDispatcher.getInstance().dispatch(controlPanelUpgradeMessage);
            }
        });
    }
}

