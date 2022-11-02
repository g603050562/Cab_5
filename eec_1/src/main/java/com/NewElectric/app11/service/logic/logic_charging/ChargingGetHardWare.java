package com.NewElectric.app11.service.logic.logic_charging;

import android.content.Context;

import com.NewElectric.app11.service.BaseLogic;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateService;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateServicePduInfoBeanFormat;

/**
 * 获取硬件信息
 */
public class ChargingGetHardWare extends BaseLogic {

    public interface GetHardWareListener{
        void returnData(String type);
    }

    //单例
    private static ChargingGetHardWare instance = new ChargingGetHardWare();
    private ChargingGetHardWare() {}
    public static ChargingGetHardWare getInstance() {
        return instance;
    }

    private String type = "pdu_1_to_3";

    private GetHardWareListener getHardWareListener;


    public void logicInit(Context context , GetHardWareListener getHardWareListener) {
        this.getHardWareListener = getHardWareListener;
        BaseLogicInit(context);
        CanControlPlateService.getInstance().addObserver(this);
    }

    @Override
    public void pduBeanReturn(CanControlPlateServicePduInfoBeanFormat canControlPlateServicePduInfoBeanFormat) {
        super.pduBeanReturn(canControlPlateServicePduInfoBeanFormat);

        byte[] bytes = canControlPlateServicePduInfoBeanFormat.getPduEquipmentBean()[3].getData();
        int v = bytes[1] & 0xff;
        System.out.println("CAN - 充电机 - 模式 - 充电：    PDU版本 - " + v);
        if(v > 200){
            getHardWareListener.returnData("pdu_1_to_10");
            type = "pdu_1_to_10";
            CanControlPlateService.getInstance().deleteObserver(this);
        }else if(v < 200 && v > 100){
            getHardWareListener.returnData("pdu_1_to_3");
            type = "pdu_1_to_3";
            CanControlPlateService.getInstance().deleteObserver(this);
        }
    }

    public String getHardWareType(){
        return type;
    }

}
