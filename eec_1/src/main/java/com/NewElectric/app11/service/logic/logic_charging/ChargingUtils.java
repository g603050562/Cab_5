package com.NewElectric.app11.service.logic.logic_charging;


import android.content.Context;

import com.NewElectric.app11.service.BaseLogic;
import com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_10.Charging_1_to_10;
import com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_3.Charging_1_to_3;

public abstract class ChargingUtils extends BaseLogic {

    //单例
    private static ChargingUtils instance;
    public static ChargingUtils getInstance(String type) {
        if(type.equals("pdu_1_to_3")){
            instance = Charging_1_to_3.getInstance();
        }else if(type.equals("pdu_1_to_10")){
            instance = Charging_1_to_10.getInstance();
        }
        return instance;
    }

    public abstract void logicInit(Context context);

    public abstract void setMode(int type);

    public abstract int[] getChargeStatus();

    public abstract void startChange(int door);

    protected  abstract void charging();

    protected  abstract void chargingAndHeating();

    public abstract void onDestroy();
}
