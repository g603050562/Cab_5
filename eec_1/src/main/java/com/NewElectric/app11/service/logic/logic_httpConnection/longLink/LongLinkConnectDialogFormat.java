package com.NewElectric.app11.service.logic.logic_httpConnection.longLink;

public class LongLinkConnectDialogFormat {

    private String message = "";
    private int time = 0;
    private int type = 1;

    public LongLinkConnectDialogFormat(String message, int time, int type) {
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
