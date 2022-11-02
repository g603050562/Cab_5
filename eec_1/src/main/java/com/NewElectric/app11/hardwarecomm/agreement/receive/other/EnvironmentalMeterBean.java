package com.NewElectric.app11.hardwarecomm.agreement.receive.other;


/**
 *  温湿传感器 返回信息
 */

public class EnvironmentalMeterBean {

    private String theMeter = "";
    private String temMeter = "";

    public EnvironmentalMeterBean(String theMeter, String temMeter) {
        this.theMeter = theMeter;
        this.temMeter = temMeter;
    }

    public String getTheMeter() {
        return theMeter;
    }


    public String getTemMeter() {
        return temMeter;
    }

    @Override
    public String toString() {
        return "ElectricityMeterFomate{" +
                "theMeter=" + theMeter +
                ", temMeter=" + temMeter +
                '}';
    }
}
