package com.NewElectric.app11.service.logic.logic_find4gCard;

public class Find4gCardReturnDataFormat {

    private String type = "";
    private String imsi = "";

    public Find4gCardReturnDataFormat(String type, String imsi) {
        this.type = type;
        this.imsi = imsi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    @Override
    public String toString() {
        return "Find4gCardReturnDataFormat{" +
                "type='" + type + '\'' +
                ", imsi='" + imsi + '\'' +
                '}';
    }
}
