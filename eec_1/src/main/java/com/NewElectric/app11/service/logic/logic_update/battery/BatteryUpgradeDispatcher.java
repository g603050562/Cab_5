package com.NewElectric.app11.service.logic.logic_update.battery;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.NewElectric.app11.service.logic.logic_update.SerialPortRwAction;
import com.NewElectric.app11.service.logic.logic_update.UpgradeMessage;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description: 电池升级cam和485数据信息 接口 配置
 */
public final class BatteryUpgradeDispatcher extends ConcurrentLinkedQueue<UpgradeMessage> {

    //单例
    public static final BatteryUpgradeDispatcher getInstance() {
        return UPGRADE_DISPATCHER;
    }
    private static final BatteryUpgradeDispatcher UPGRADE_DISPATCHER = new BatteryUpgradeDispatcher();

    //电池升级执行
    private final BatteryExecuter batteryExecuter = new BatteryExecuter();

    //初始化配置
    public void init(SerialPortRwAction serialPortRwAction) {
        batteryExecuter.init(serialPortRwAction);
    }

    //开始
    public void dispatch(UpgradeMessage upgradeMessage) {
        if (upgradeMessage instanceof BatteryUpgradeMessage) {
            batteryExecuter.upgrade((BatteryUpgradeMessage) upgradeMessage);
        }
    }
}