package com.NewElectric.app11.service.canAndSerial.serial;

public class SerialServiceReturnFormat {

    private String theMeter = "";
    private String temMeter = "";
    private String eleMeter = "";

    public SerialServiceReturnFormat() {
    }

    public String getTheMeter() {
        return theMeter;
    }

    public void setTheMeter(String theMeter) {
        this.theMeter = theMeter;
    }

    public String getTemMeter() {
        return temMeter;
    }

    public void setTemMeter(String temMeter) {
        this.temMeter = temMeter;
    }

    public String getEleMeter() {
        return eleMeter;
    }

    public void setEleMeter(String eleMeter) {
        this.eleMeter = eleMeter;
    }

    @Override
    public String toString() {
        return "SerialServiceReturnFormat{" +
                "theMeter='" + theMeter + '\'' +
                ", temMeter='" + temMeter + '\'' +
                ", eleMeter='" + eleMeter + '\'' +
                '}';
    }
}
