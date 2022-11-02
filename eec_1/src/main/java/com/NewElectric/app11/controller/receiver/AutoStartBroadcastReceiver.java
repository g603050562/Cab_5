package com.NewElectric.app11.controller.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.NewElectric.app11.controller.activity.A_Index;


//开机自启动广播接受
public class AutoStartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {     // boot
            Intent intent2 = new Intent(context, A_Index.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
    }
}