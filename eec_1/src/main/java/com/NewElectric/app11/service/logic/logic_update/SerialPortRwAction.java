package com.NewElectric.app11.service.logic.logic_update;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-08
 * Description: can和485接口定义 此接口调用一般用作从外部接收数据给executer
 */
public interface SerialPortRwAction {
    //写
    void write(byte[] bytes);
    //读
    byte[] read();
}
