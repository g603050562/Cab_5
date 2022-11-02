package com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_3;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.agreement.send.pdu.PduSend;

/**
 * 电池激活
 *
 */
//TODO::存在问题就是 这个逻辑貌似与充电逻辑有点儿冲突 之后再做改善
public class BatteryActivation {

    private int door = 0;

    public BatteryActivation(int door) {
        this.door = door;
    }

    public void onStart() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    int top = 0;
                    int end = 3;
                    if (door > 0 && door <= 3) {
                        top = 1;
                        end = 4;
                    } else if (door > 3 && door <= 6) {
                        top = 4;
                        end = 7;
                    } else if (door > 6 && door <= 9) {
                        top = 7;
                        end = 10;
                    }
                    for (int i = top; i < end; i++) {
                        System.out.println("电池：正在下发关闭横向舱门充电 - "+ (i+1)+"号仓");
                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(door, 64800, 3850));
                        sleep(20);
                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(door, 64800, 3850));
                        sleep(20);
                    }
                    sleep(500);
                    System.out.println("电池：下发打开充电继电器 - "+ door);
                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.openChargingSwitch(door, 64800, 64800));
                    sleep(500);
                    System.out.println("电池：下发设置充电继电器 - "+ door);
                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(door,64800,3950));
                    sleep(60 * 1000);
                    Charging_1_to_3.getInstance().startChange(door);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
