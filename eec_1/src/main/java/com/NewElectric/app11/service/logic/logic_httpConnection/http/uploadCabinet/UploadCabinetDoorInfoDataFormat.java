package com.NewElectric.app11.service.logic.logic_httpConnection.http.uploadCabinet;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateBaseBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateInfo;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduInfo;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.model.dao.sharedPreferences.ForbiddenSp;
import com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_3.Charging_1_to_3;
import com.NewElectric.app11.units.Units;

public class UploadCabinetDoorInfoDataFormat {

    public JSONArray getJson(Context context, ControlPlateInfo controlPlateInfo , PduInfo pduInfo) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        int[] chargeStatus = Charging_1_to_3.getInstance().getChargeStatus();
        CabInfoSp cabInfoSp = new CabInfoSp(context);
        ForbiddenSp forbiddenSp = new ForbiddenSp(context);
        for (int i = 0; i < 9; i++) {
            JSONObject jsonObject_item = new JSONObject();
            ControlPlateBaseBean controlPlateBaseBean = controlPlateInfo.getControlPlateBaseBeans()[i];
            jsonObject_item.put("door", i + 1 + ""); //舱门id 从1开始
            jsonObject_item.put("battery", controlPlateBaseBean.getBID());   //电池BID
            jsonObject_item.put("bty_rate", controlPlateBaseBean.getBatteryRelativeSurplus());  //电池相对容量
            jsonObject_item.put("soh", controlPlateBaseBean.getControlPlateTopVersion());  //当时是电池健康比 现在是控制板的上层版本（以后得改）
            jsonObject_item.put("soc", controlPlateBaseBean.getBatteryRelativeSurplus());  //电池相对容量（跟bty_rate一样 ， 以后需要废弃一个）
            jsonObject_item.put("vdif", controlPlateBaseBean.getPressureDifferential());  //电池里面各个串数电池 最高和最低的差值    压差
            jsonObject_item.put("uses", controlPlateBaseBean.getLoops());  //循环次数
            jsonObject_item.put("wendu",controlPlateBaseBean.getBatteryTemperature()); //电池温度
            jsonObject_item.put("dianya", controlPlateBaseBean.getBatteryVoltage()); //电池 电压
            jsonObject_item.put("dianliu",controlPlateBaseBean.getBatteryElectric()); //电池 电流
            jsonObject_item.put("inching", controlPlateBaseBean.getInching_1()); // 微动数据上传
            jsonObject_item.put("side_inching",controlPlateBaseBean.getInching_2());//边侧微动数据上传
            jsonObject_item.put("cabid", cabInfoSp.getCabinetNumber_XXXXX()); // 长链接下发的id
            jsonObject_item.put("charge_info",  pduInfo.getPduChargingInfoBean()[i].getDataStr());//每个仓的充电情况
            jsonObject_item.put("pdu_info",pduInfo.getPduEquipmentBeans()[i+3].getDataStr()); //设备情况
            jsonObject_item.put("pdu_status",pduInfo.getPduChargingInfoBean()[i].getStatus()); //舱门状态
            jsonObject_item.put("full_cap", controlPlateBaseBean.getBatteryFullCapacity()); // 充满电池容量
            jsonObject_item.put("left_cap", controlPlateBaseBean.getBatteryRemainingCapacity()); // 剩余电池容量
            jsonObject_item.put("IS_CHARGE", chargeStatus[i]); // 剩余电池容量
            jsonObject_item.put("TEM_2", controlPlateBaseBean.getTemperatureSensor_2());//加热板温度
            jsonObject_item.put("outIn", forbiddenSp.getTargetForbidden(i)); // 剩余电池容量
            jsonObject_item.put("volt", Units.bar_60_or_48(controlPlateBaseBean.getBID())); //上传电池的类型是什么类型的 现在又 48V和60V的
            jsonObject_item.put("soh_2", controlPlateBaseBean.getBatteryHealthy()); // 这个才是真正的电池健康比
            jsonObject_item.put("uid32",controlPlateBaseBean.getUID()); // 剩余电池容量
            jsonObject_item.put("lastDataDate", controlPlateBaseBean.getDataTime());
            jsonObject_item.put("volt_max", controlPlateBaseBean.getItemMax());
            jsonObject_item.put("volt_min", controlPlateBaseBean.getItemMin());
            String barBer = controlPlateBaseBean.getBatteryVersion();
            String a = barBer.substring(0, 2);
            String b = barBer.substring(2, 4);
            int a_i = Integer.parseInt(a, 16);
            int b_i = Integer.parseInt(b, 16);
            jsonObject_item.put("bsv", b_i); // 电池健康比 soh
            jsonObject_item.put("bhv", a_i); // 电池健康比 soh
            jsonArray.put(jsonObject_item);
        }
        return jsonArray;
    }
}
