package com.NewElectric.app11.hardwarecomm.agreement.receive.pdu;


import java.util.Arrays;

import com.NewElectric.app11.units.Units;

public class PduChargingInfoBean {

    private byte data[] = new byte[]{0,0,0,0,0,0,0,0};
    private String dataStr = "";
    private String status = "";
    private double outputVoltage = 0;
    private double outputElectric = 0;
    private String stopStatus = "";
    private String errorStatus = "";

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getOutputVoltage() {
        return outputVoltage;
    }

    public void setOutputVoltage(double outputVoltage) {
        this.outputVoltage = outputVoltage;
    }

    public double getOutputElectric() {
        return outputElectric;
    }

    public void setOutputElectric(double outputElectric) {
        this.outputElectric = outputElectric;
    }

    public String getStopStatus() {
        return stopStatus;
    }

    public void setStopStatus(String stopStatus) {
        this.stopStatus = stopStatus;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    public PduChargingInfoBean(byte[] data, String status, double outputVoltage, double outputElectric, String stopStatus, String errorStatus) {

        this.data = data;
        this.status = status;
        this.outputVoltage = outputVoltage;
        this.outputElectric = outputElectric;
        this.stopStatus = stopStatus;
        this.errorStatus = errorStatus;
        dataStr = Units.ByteArrToHex(data);
    }

    public PduChargingInfoBean() {
    }


    @Override
    public String toString() {
        return "PduChargingInfoBean{" +
                "data=" + Arrays.toString(data) +
                ", status='" + status + '\'' +
                ", outputVoltage=" + outputVoltage +
                ", outputElectric=" + outputElectric +
                ", stopStatus='" + stopStatus + '\'' +
                ", errorStatus='" + errorStatus + '\'' +
                '}';
    }
}
