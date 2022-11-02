package com.NewElectric.app11.service.logic.logic_httpConnection.http.rentBattery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateBaseBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateInfo;
import com.NewElectric.app11.units.Units;

public class RentBatteryItemDataFormat {


    public JSONArray getJson(ControlPlateInfo controlPlateInfo) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(int i = 0 ; i < 9 ; i++){
            JSONObject jsonObject = new JSONObject();
            ControlPlateBaseBean controlPlateBaseBean = controlPlateInfo.getControlPlateBaseBeans()[i];
            jsonObject.put("door",i+1);
            jsonObject.put("bid",controlPlateBaseBean.getBID());
            jsonObject.put("per",controlPlateBaseBean.getBatteryRelativeSurplus());
            jsonObject.put("uid32",controlPlateBaseBean.getUID());
            jsonObject.put("volt", Units.bar_60_or_48(controlPlateBaseBean.getBID()));
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

}
