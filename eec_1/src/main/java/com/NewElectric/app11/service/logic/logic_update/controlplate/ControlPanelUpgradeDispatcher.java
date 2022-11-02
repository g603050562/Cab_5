package com.NewElectric.app11.service.logic.logic_update.controlplate;


import java.util.concurrent.ConcurrentLinkedQueue;

import com.NewElectric.app11.service.logic.logic_update.UpgradeMessage;
import com.NewElectric.app11.service.logic.logic_update.SerialPortRwAction;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description: 控制板升级cam和485数据信息 接口 配置
 * 三级配置
 * 单例                             状态配置（相当于工厂）    逻辑处理
 * ControlPanelUpgradeDispatcher - ControlPanelExecuter - ControlPanel_485_9_UpgradeProgram
 */
public final class ControlPanelUpgradeDispatcher extends ConcurrentLinkedQueue<UpgradeMessage> {

    //单例
    private static final ControlPanelUpgradeDispatcher UPGRADE_DISPATCHER = new ControlPanelUpgradeDispatcher();
    public static final ControlPanelUpgradeDispatcher getInstance() {
        return UPGRADE_DISPATCHER;
    }
    //控制板升级执行
    private final ControlPanelExecuter controlPanelExecuter = new ControlPanelExecuter();

    //9仓串口初始化（SerialPortRwAction：485和can数据接口）
    public void init(SerialPortRwAction serialPortRwAction) {
        controlPanelExecuter.init(serialPortRwAction);
    }

    //12仓串口初始化（SerialPortRwAction：485和can数据接口）
    public void init_12(SerialPortRwAction serialPortRwAction) {
        controlPanelExecuter.init_12(serialPortRwAction);
    }

    //开始进行
    public void dispatch(UpgradeMessage upgradeMessage) {
        //如果抽象类UpgradeMessage继承的是ControlPanelUpgradeMessage
        if (upgradeMessage instanceof ControlPanelUpgradeMessage) {
            controlPanelExecuter.upgrade((ControlPanelUpgradeMessage) upgradeMessage);
        }
    }
}