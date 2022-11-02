package com.NewElectric.app11.service.canAndSerial.can.controlPlate;

import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduChargingInfoBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduEquipmentBean;

public class CanControlPlateServicePduInfoBeanFormat {

   private PduChargingInfoBean[] pduChargingInfoBean;
   private PduEquipmentBean[] pduEquipmentBean;

    public CanControlPlateServicePduInfoBeanFormat(PduChargingInfoBean[] pduChargingInfoBean, PduEquipmentBean[] pduEquipmentBean) {
        this.pduChargingInfoBean = pduChargingInfoBean;
        this.pduEquipmentBean = pduEquipmentBean;
    }

    public PduChargingInfoBean[] getPduChargingInfoBean() {
        return pduChargingInfoBean;
    }

    public void setPduChargingInfoBean(PduChargingInfoBean[] pduChargingInfoBean) {
        this.pduChargingInfoBean = pduChargingInfoBean;
    }

    public PduEquipmentBean[] getPduEquipmentBean() {
        return pduEquipmentBean;
    }

    public void setPduEquipmentBean(PduEquipmentBean[] pduEquipmentBean) {
        this.pduEquipmentBean = pduEquipmentBean;
    }
}
