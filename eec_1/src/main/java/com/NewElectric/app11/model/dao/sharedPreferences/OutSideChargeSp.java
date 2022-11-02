package com.NewElectric.app11.model.dao.sharedPreferences;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 *  外接充电 时间储存 （功能废弃）
 */

public class OutSideChargeSp {

    private Activity activity;
    private SharedPreferences sharedPreferences;

    public OutSideChargeSp(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("OutSideCharge", Activity.MODE_PRIVATE);
    }

    /**
     * 设置外接电源充电时间
     * @param index 舱门下标（从0开始）
     * @param time  充电时间（秒为单位）
     */
    public void setOutSideCharge(int index, int time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("OutSideCharge_" + index, time);
        editor.commit();
    }

    /**
     * 获取外接电源充电时间
     * @param index 舱门下标（从0开始）
     */
    public int getOutSideCharge(int index) {
        int forbiddenType = sharedPreferences.getInt("OutSideCharge_"+index, 0);
        return forbiddenType;
    }
}
