package com.NewElectric.app11.service.logic.logic_firePrevention;

import android.app.Activity;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;

public class FirePrevention {

    public interface FirePreventionListener {
        void dialogReturn(String info, String time, String type);

        void open();

        void close();

        void closePdu();
    }

    //喷水参数
    int outfire_code = 0;
    private Activity activity;
    private FirePreventionListener firePreventionListener;

    public FirePrevention(Activity activity , FirePreventionListener firePreventionListener) {
        this.activity = activity;
        this.firePreventionListener = firePreventionListener;
    }

    public FirePrevention() {
    }

    public void onStart() {

        try {

            if (Float.parseFloat(new CabInfoSp(activity).getTemMeter()) > 68) {
                firePreventionListener.dialogReturn("柜体温度过高，停止充电", "5", "0");
                firePreventionListener.closePdu();
            }

//            if (A_Main.yangan_1 > 10000 || A_Main.wengan_1 > 68 || A_Main.wengan_2 > 68 || A_Main.wengan_3 > 68) {
//
//                if (outfire_code == 0) {
//
//                    Thread thread = new Thread() {
//                        @Override
//                        public void run() {
//                            super.run();
//
//                            outfire_code = 1;
//                            for (int i = 0; i < 60; i++) {
//                                try {
//
//                                    environmentBoard_1("1");
//                                    firePreventionListener.closePdu();
//                                    firePreventionListener.close();
//                                    firePreventionListener.dialogReturn("柜体温度过高，停止充电", "5", "0");
//
//                                    sleep(5000);
//
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            firePreventionListener.open();
//                            environmentBoard_1("2");
//                            outfire_code = 0;
//                        }
//                    };
//                    thread.start();
//                }
//            }

        } catch (Exception e) {
            System.out.println("温度：   " + e);
        }
    }

    public void environmentBoard_1(String type){

        if (type.equals("1")) {
            String b = "98b06665";
            byte[] a_1 = new byte[]{(byte) 0x10, (byte) 0xb0, (byte) 0x05, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x03, (byte) 0x00};
            byte[] a_2 = new byte[]{(byte) 0x20, (byte) 0x00, (byte) 0x76, (byte) 0x7e};
            MyApplication.serialAndCanPortUtils.canSendOrder(b, a_1);
            MyApplication.serialAndCanPortUtils.canSendOrder(b, a_2);
        } else {
            String b = "98b06665";
            byte[] a_1 = new byte[]{(byte) 0x10, (byte) 0xb0, (byte) 0x05, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x03, (byte) 0x00};
            byte[] a_2 = new byte[]{(byte) 0x20, (byte) 0x01, (byte) 0xb7, (byte) 0xbe};
            MyApplication.serialAndCanPortUtils.canSendOrder(b, a_1);
            MyApplication.serialAndCanPortUtils.canSendOrder(b, a_2);
        }

    }

}
