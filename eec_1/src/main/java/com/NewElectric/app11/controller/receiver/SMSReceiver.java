package com.NewElectric.app11.controller.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

    private IBinderSMSReciver iBinderSMSReciver;

    public SMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        System.out.println("来短信了...");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] objs = (Object[]) bundle.get("pdus");
            SmsMessage[] smsMessages = new SmsMessage[objs.length];

            StringBuffer sb = new StringBuffer();
            String content = null;
            for (int i = 0; i < objs.length; i++) {

                smsMessages[i] = SmsMessage.createFromPdu((byte[])objs[i]);
                //获取发送的号码
                String number = smsMessages[i].getDisplayOriginatingAddress();
                //获取发送内容
                content = smsMessages[i].getDisplayMessageBody();
                sb.append(content);
            }
            System.out.println(sb.toString());
            iBinderSMSReciver.setUI(sb.toString());
        }

    }

    //观察者设计模式
    public interface IBinderSMSReciver{
        public void setUI(String content);
    }
    //做个监听器
    public void setIBinderSMSReciverListene(IBinderSMSReciver iBinderSMSReciver){
        this.iBinderSMSReciver = iBinderSMSReciver;
    }

}