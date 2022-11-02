package com.NewElectric.app11.hardwarecomm.agreement.receive.pdu;

import java.util.Arrays;

public class PduInfo {

    private PduChargingInfoBean[] pduChargingInfoBeans = new PduChargingInfoBean[9];
    private PduEquipmentBean[] pduEquipmentBeans = new PduEquipmentBean[12];

    public PduInfo() {
        Arrays.fill(pduChargingInfoBeans,new PduChargingInfoBean());
        Arrays.fill(pduEquipmentBeans,new PduEquipmentBean());
    }

    public PduChargingInfoBean[] getPduChargingInfoBean() {
        return pduChargingInfoBeans;
    }

    public void setPduChargingInfoBean(PduChargingInfoBean[] pduChargingInfoBean) {
        this.pduChargingInfoBeans = pduChargingInfoBean;
    }

    public void setPduChargingInfoBean(int index , PduChargingInfoBean pduChargingInfoBean) {
        this.pduChargingInfoBeans[index] = pduChargingInfoBean;
    }

    public PduEquipmentBean[] getPduEquipmentBeans() {
        return pduEquipmentBeans;
    }

    public void setPduEquipmentBeans(PduEquipmentBean[] pduEquipmentBeans) {
        this.pduEquipmentBeans = pduEquipmentBeans;
    }

    public void setPduEquipmentBeans(int index , PduEquipmentBean pduEquipmentBeans) {
        this.pduEquipmentBeans[index] = pduEquipmentBeans;
    }

    @Override
    public String toString() {
        return "PduBean{" +
                "pduChargingInfoBean=" + Arrays.toString(pduChargingInfoBeans) +
                ", pduEquipmentBeans=" + Arrays.toString(pduEquipmentBeans) +
                '}';
    }
}
