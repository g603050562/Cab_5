package com.NewElectric.app11.service.logic.logic_update;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description: 数据接口回调
 */
public interface OnRwAction{
    //写
    void write(byte[] bytes);
    //度
    byte[] read();
}
