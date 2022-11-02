package com.NewElectric.app11.service.logic.logic_update;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-08
 * Description: 升级的过程回调
 */
public interface UpgradeCallBack {
    /**
     * 升级之前
     */
    void onUpgradeBefore(byte address);
    /**
     * 正在升级
     *
     * @param address
     * @param process
     * @param total
     */
    void onUpgrade(byte address, long process, long total);

    /**
     * 当升级失败的时候调用
     *
     * @param address
     * @param errorInfo
     */
    void onError(byte address, String errorInfo);

    /**
     * 升级之后调用和onError方法是互斥的
     */
    void onUpgradeAfter(byte address);
}