package com.NewElectric.app11.service.logic.logic_exchange.cabinetOfPdu;

public class ExchangeBarOutLineStartAnimation {

   private int inDoor = -1;
   private int outDoor = -1;
   private String type = "";

    public ExchangeBarOutLineStartAnimation(int inDoor, int outDoor, String type) {
        this.inDoor = inDoor;
        this.outDoor = outDoor;
        this.type = type;
    }

    public int getInDoor() {
        return inDoor;
    }

    public void setInDoor(int inDoor) {
        this.inDoor = inDoor;
    }

    public int getOutDoor() {
        return outDoor;
    }

    public void setOutDoor(int outDoor) {
        this.outDoor = outDoor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
