package com.NewElectric.app11.service.logic.logic_update;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description: 升级状态回调接口
 */
public interface OnUpgradeProgress {
    void onUpgrade(byte mapAddress, byte statusFlag, String statusInfo, long currentPregress, long totalPregress);
}
