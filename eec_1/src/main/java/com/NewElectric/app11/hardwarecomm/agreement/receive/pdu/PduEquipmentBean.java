package com.NewElectric.app11.hardwarecomm.agreement.receive.pdu;

import java.util.Arrays;

import com.NewElectric.app11.units.Units;

public class PduEquipmentBean {

    private byte[] data = new byte[]{0,0,0,0,0,0,0,0};
    private String dataStr = "";
    private String version = "V0B0D0";
    private String error = "无";

    public PduEquipmentBean(byte[] data, String version, String error) {
        this.data = data;
        this.version = version;
        this.error = error;
        dataStr = Units.ByteArrToHex(data);
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    public PduEquipmentBean() {
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "PduEquipmentBean{" +
                "data=" + Arrays.toString(data) +
                ", version='" + version + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
