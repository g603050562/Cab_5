package com.NewElectric.app11.service.logic.logic_update.controlplate;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-05
 * Description: 升级状态
 */
public final class ControlPanelUpgradeStatus {

    public static final byte WAITTING = 2;//等待

    public static final byte BATTERY_INFO = 3;//电池信息

    public static final byte MODE_1 = 4;//进入BootLoader模式

    public static final byte MODE_2 = 5;//初始化固件数据

    public static final byte MODE_3 = 6;//立即激活新BMS程序

    public static final byte WRITE_DATA = 7;//写入升级数据

    public static final byte FAILED = 8;//升级失败

    public static final byte SUCCESSED = 9;//升级成功
}