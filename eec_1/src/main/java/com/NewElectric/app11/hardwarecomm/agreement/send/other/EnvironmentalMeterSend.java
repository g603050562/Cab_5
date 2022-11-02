package com.NewElectric.app11.hardwarecomm.agreement.send.other;

public class EnvironmentalMeterSend {

    //获取温湿传感器参数
    public static byte[] getEnvMeterInfo(){
        byte[] data = new byte[]{(byte) 0x41, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0xCA, (byte) 0xCB};
        return data;
    }

}
