package com.NewElectric.app11.service.logic.logic_update.battery;


import com.NewElectric.app11.service.logic.logic_update.UpgradeMessage;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description: 电池升级参数配置
 */
public class BatteryUpgradeMessage extends UpgradeMessage {

    //升级电池的类型（像是博强诺万这些的版本区分）
    private String manufacturer;

    //构造（继承）
    public BatteryUpgradeMessage(byte address) {
        super(address);
    }
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
