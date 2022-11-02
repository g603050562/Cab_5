package com.NewElectric.app11.service.logic.logic_netDBM;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class GSMCellLocation {

    private static final String TAG = "GSMCellLocationActivity";

    private Context context;

    public GSMCellLocation(Context context) {
        this.context = context;
    }

    public Map<String , String> getGSMCell() {

        try {

            Map<String , String> map = new HashMap<>();

            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // 中国移动和中国联通获取LAC、CID的方式
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "获取邻区基站信息: 没有权限");
                return null;
            }


            // 返回值MCC + MNC
            String operator = mTelephonyManager.getNetworkOperator();
            int mcc = Integer.parseInt(operator.substring(0, 3));
            int mnc = Integer.parseInt(operator.substring(3));

            map.put("mcc",mcc+"");
            map.put("mnc",mnc+"");

            GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
            int lac = location.getLac();
            int cellId = location.getCid();

            map.put("lac",lac+"");
            map.put("cellId",cellId+"");

            Log.i(TAG, " MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cellId);

            return map;


        } catch (Exception e) {
            return null;
        }
    }
}
