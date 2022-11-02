package com.NewElectric.app11.hardwarecomm.androidHard;

import java.util.Arrays;

import com.NewElectric.app11.units.Units;


/**
 * can报文 格式 地址 + 数据
 */

public class CanDataFormat {

    private String addressStr;
    private long addressLong;
    private byte[] data;
    private String  dataStr;
    private byte[] rawData;

    public CanDataFormat(String addressStr, byte[] data) {
        this.addressStr = addressStr;
        this.data = data;
        this.rawData = data;
    }

    public CanDataFormat(byte[] rawData){
        String return_id = String.format("%02x", new Object[]{rawData[3]}).toUpperCase() + "" + String.format("%02x", new Object[]{rawData[2]}).toUpperCase() + "" + String.format("%02x", new Object[]{rawData[1]}).toUpperCase() + "" + String.format("%02x", new Object[]{rawData[0]}).toUpperCase();
        this.addressLong = Long.parseLong(return_id, 16);
        this.addressStr = Long.toHexString(addressLong);
        this.data = new byte[]{rawData[8], rawData[9], rawData[10], rawData[11], rawData[12], rawData[13], rawData[14], rawData[15]};
        this.rawData = rawData;
        this.dataStr = Units.ByteArrToHex(data);
    }

    public String getAddressByStr() { return addressStr; }

    public long getAddressByLong() { return addressLong; }

    public byte[] getData() {
        return data;
    }

    public String getDataByStr() {
        return dataStr;
    }

    public byte[] getRawData() {
        return rawData;
    }

    @Override
    public String toString() {
        return "CanReturnFormat{" +
                "addressStr='" + addressStr + '\'' +
                ", addressLong=" + addressLong +
                ", data=" + Arrays.toString(data) +
                ", dataStr='" + dataStr + '\'' +
                ", rawData=" + Arrays.toString(rawData) +
                '}';
    }
}
