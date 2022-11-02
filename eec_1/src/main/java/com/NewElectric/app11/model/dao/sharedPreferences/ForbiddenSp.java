package com.NewElectric.app11.model.dao.sharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 舱门禁用状态 sp储存
 */

public class ForbiddenSp {

    private Context context;
    private SharedPreferences sharedPreferences;

    public ForbiddenSp(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("Forbidden", Activity.MODE_PRIVATE);
    }
    /**
     * 设置目标舱门禁用状态
     *
     * @param index 舱门下标（从0开始）
     * @param type  设置舱门状态     1 - 正常     -1 - 停用不推出推杆      -2 - 停用推出推杆     -3 - 电池写入出错禁用
     */
    public void setTargetForbidden(int index, int type) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("forbiddenType_" + index, type);
        editor.commit();
    }

    /**
     * 获取目标舱门禁用状态
     *
     * @param index 舱门下标（从0开始）
     */
    public int getTargetForbidden(int index) {
        int forbiddenType = sharedPreferences.getInt("forbiddenType_"+index, 1);
        return forbiddenType;
    }
}
