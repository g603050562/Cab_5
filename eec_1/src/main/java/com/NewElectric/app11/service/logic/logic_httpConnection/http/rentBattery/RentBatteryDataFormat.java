package com.NewElectric.app11.service.logic.logic_httpConnection.http.rentBattery;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateInfo;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;

public class RentBatteryDataFormat {

    public JSONObject getJson(Context context , String did , String uid , String order_num , ControlPlateInfo controlPlateInfo) throws JSONException {
        CabInfoSp cabInfoSp = new CabInfoSp(context);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("did",did);
        jsonObject.put("uid",uid);
        jsonObject.put("cid",cabInfoSp.getCabinetNumber_4600XXXX());
        jsonObject.put("order_num",order_num);
        jsonObject.put("cabid",cabInfoSp.getCabinetNumber_XXXXX());
        jsonObject.put("data",new RentBatteryItemDataFormat().getJson(controlPlateInfo));
        return jsonObject;
    }

}
