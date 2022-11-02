package com.NewElectric.app11.model.dao.sqlLite;

/**
 *  2021年3月15日
 *  存储在本地数据库的换电信息（需要上传给服务器）
 */

public class OutLineExchangeSaveInfo {

    //电柜编号 - 4600开头
    private String number = "";
    //电池UID
    private String uid = "";
    //电池换电时记录的时间
    private String extime = "";
    //插入电池的编号
    private String inBattery = "";
    //插入电池的舱门号
    private String inDoor = "";
    //插入电池的电量
    private String inElectric = "";
    //弹出电池的编号
    private String outBattery = "";
    //弹出电池的舱门号
    private String outDoor = "";
    //弹出电池的电量
    private String outElectric = "";

    public OutLineExchangeSaveInfo(String number, String uid, String extime, String inBattery, String inDoor, String inElectric, String outBattery, String outDoor, String outElectric) {
        this.number = number;
        this.uid = uid;
        this.extime = extime;
        this.inBattery = inBattery;
        this.inDoor = inDoor;
        this.inElectric = inElectric;
        this.outBattery = outBattery;
        this.outDoor = outDoor;
        this.outElectric = outElectric;
    }

    public OutLineExchangeSaveInfo() {
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getExtime() {
        return extime;
    }

    public void setExtime(String extime) {
        this.extime = extime;
    }

    public String getInBattery() {
        return inBattery;
    }

    public void setInBattery(String inBattery) {
        this.inBattery = inBattery;
    }

    public String getInDoor() {
        return inDoor;
    }

    public void setInDoor(String inDoor) {
        this.inDoor = inDoor;
    }

    public String getInElectric() {
        return inElectric;
    }

    public void setInElectric(String inElectric) {
        this.inElectric = inElectric;
    }

    public String getOutBattery() {
        return outBattery;
    }

    public void setOutBattery(String outBattery) {
        this.outBattery = outBattery;
    }

    public String getOutDoor() {
        return outDoor;
    }

    public void setOutDoor(String outDoor) {
        this.outDoor = outDoor;
    }

    public String getOutElectric() {
        return outElectric;
    }

    public void setOutElectric(String outElectric) {
        this.outElectric = outElectric;
    }

    @Override
    public String toString() {
        return "outLineExchangeInfo{" +
                "number='" + number + '\'' +
                ", uid='" + uid + '\'' +
                ", extime='" + extime + '\'' +
                ", inBattery='" + inBattery + '\'' +
                ", inDoor='" + inDoor + '\'' +
                ", inElectric='" + inElectric + '\'' +
                ", outBattery='" + outBattery + '\'' +
                ", outDoor='" + outDoor + '\'' +
                ", outElectric='" + outElectric + '\'' +
                '}';
    }
}
