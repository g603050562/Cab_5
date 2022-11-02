package com.NewElectric.app11.hardwarecomm.agreement.send.battery;

public class BatterySend {

    //获取电池 软硬件 版本
    public static byte[] getBatteryVersion() {
        byte[] data = new byte[]{(byte) 0x3A, (byte) 0x16, (byte) 0x7F, (byte) 0x01, (byte) 0x00, (byte) 0x96, (byte) 0x00, (byte) 0x0D, (byte) 0x0A};
        return data;
    }

    //读取电池历史信息 index - 0000~FFFF 0~65535
    public static byte[] getBatteryHistoryLog(long index) {
        long hIndex = index / 256;
        long lIndex = index % 256;
        byte[] data = new byte[]{(byte) 0x3A, (byte) 0x16, (byte) 0xA1, (byte) 0x02, (byte) lIndex, (byte) hIndex, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x0A};
        long addCrc = data[1] + data[2] + data[3] + data[4] + data[5];
        int hAddCrc = (int) (addCrc / 256);
        int lAddCrc = (int) (addCrc % 256);
        data[6] = (byte) lAddCrc;
        data[7] = (byte) hAddCrc;
        return data;
    }
}
