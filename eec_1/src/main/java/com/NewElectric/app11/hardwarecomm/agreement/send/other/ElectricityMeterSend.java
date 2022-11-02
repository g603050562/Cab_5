package com.NewElectric.app11.hardwarecomm.agreement.send.other;


public class ElectricityMeterSend {

    //获得电表的485地址
    public static byte[] getMeterAddress(){
       byte[] data =  new byte[]{(byte) 0xfe, (byte) 0xfe, (byte) 0xfe, (byte) 0xfe, (byte) 0x68, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0x68, (byte) 0x13, (byte) 0x00, (byte) 0xdf, (byte) 0x16};
       return data;
    }

    //获得电表读数
    public static byte[] getMeterEle(String address){

        int count = address.length() / 2;
        int[] RX = new int[count];
        for (int i = 0; i < count; i++) {
            RX[i] = Integer.parseInt(address.substring(i * 2, (i + 1) * 2), 16);
        }
        int[] small_rx = new int[]{0x68, RX[0], RX[1], RX[2], RX[3], RX[4], RX[5], 0x68, 0x11, 0x04, 0x33, 0x33, 0x33, 0x33};
        int a = 0;
        for (int i = 0; i < small_rx.length; i++) {
            a = a + small_rx[i];
        }
        String strHex = Integer.toHexString(a);
        if (strHex.length() == 3) {
            strHex = "0" + strHex;
        } else if (strHex.length() == 2) {
            strHex = "00" + strHex;
        } else if (strHex.length() == 1) {
            strHex = "000" + strHex;
        }
        String crc_str = strHex.substring(2, 4);
        int crc_int = Integer.parseInt(crc_str, 16);
        byte[] data = new byte[]{(byte) 0xfe, (byte) 0xfe, (byte) 0xfe, (byte) 0xfe, (byte) 0x68, (byte) RX[0], (byte) RX[1], (byte) RX[2], (byte) RX[3], (byte) RX[4], (byte) RX[5], (byte) 0x68, (byte) 0x11, (byte) 0x04, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) crc_int, (byte) 0x16};
        return data;
    }



}
