package com.NewElectric.app11.service.logic.logic_httpConnection.longLink;

public class LongLinkConnectUpdateHardWare {

    private int door = 0;
    private String dataPath = "";
    private String type = "";
    private String tel = "";
    private String cabID = "";
    private String name = "";

    public LongLinkConnectUpdateHardWare(int door, String dataPath, String type, String tel, String cabID, String name) {
        this.door = door;
        this.dataPath = dataPath;
        this.type = type;
        this.tel = tel;
        this.cabID = cabID;
        this.name = name;
    }

    public int getDoor() {
        return door;
    }

    public void setDoor(int door) {
        this.door = door;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCabID() {
        return cabID;
    }

    public void setCabID(String cabID) {
        this.cabID = cabID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
