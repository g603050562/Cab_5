package com.NewElectric.app11.service.logic.logic_update;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description: 配置升级硬件版本信息
 */
public abstract class UpgradeMessage {
    //舱门地址
    private byte address;
    //升级文件本地地址
    private String filePath;
    //升级时的状态回调
    private UpgradeCallBack upgradeCallBack;

    public UpgradeMessage(byte address) {
        this.address = address;
    }
    public byte getAddress(){
        return address;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public UpgradeCallBack getUpgradeCallBack() {
        return upgradeCallBack;
    }
    public void setUpgradeCallBack(UpgradeCallBack upgradeCallBack) {
        this.upgradeCallBack = upgradeCallBack;
    }

}
