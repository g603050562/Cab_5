package com.NewElectric.app11.hardwarecomm.androidHard;

/**
 * 抽象工厂类
 * 生产对应android板的接口类
 */

public class HardWareCommFactoryProducer {

    public static SerialAndCanPortUtils getFactory(String choice){

        if(choice.equalsIgnoreCase("rk3288_box")){
            return SerialAndCanPortUtilsGeRui.getInstance();
        } else if(choice.equalsIgnoreCase("SABRESD")){
            return SerialAndCanPortUtilsFeiLing.getInstance();
        }
        return null;
    }
}
