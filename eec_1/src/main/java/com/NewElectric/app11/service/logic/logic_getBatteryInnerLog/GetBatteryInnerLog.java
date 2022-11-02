package com.NewElectric.app11.service.logic.logic_getBatteryInnerLog;

import android.app.Activity;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.agreement.send.battery.BatterySend;
import com.NewElectric.app11.hardwarecomm.agreement.send.controlPlate.ControlPlateSend;
import com.NewElectric.app11.hardwarecomm.agreement.receive.battery.BatteryHistoryLogsBean;

/**
 * 获取舱门电池的内置日志
 */
public class GetBatteryInnerLog{

    public interface GetBatteryInnerLogListener{
        void onGetBatteryInnerLogStart();
        void onGetBatteryInnerLogInfo(String info);
        void onGetBatteryInnerLogFinish();
        void onGetBatteryInnerLogRetun(BatteryHistoryLogsBean batteryHistoryLogsBean);
    }

    private Activity activity;
    private int door;
    private Thread thread = null;
    private int threadCode = 0;
    private int step = 0;
    private GetBatteryInnerLogListener getBatteryInnerLogListener;

    public GetBatteryInnerLog(Activity activity, int door ,  GetBatteryInnerLogListener getBatteryInnerLogListener) {
        this.activity = activity;
        this.door = door;
        this.getBatteryInnerLogListener = getBatteryInnerLogListener;
        System.out.println("GetBatteryInnerLog - 初始化");
    }

    public void onStart() {
        if (thread == null) {
            threadCode = 0;
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {

                        getBatteryInnerLogListener.onGetBatteryInnerLogStart();
                        System.out.println("GetBatteryInnerLog - 线程启动");

                        while (threadCode == 0) {

                            sleep(500);

                            if(step == 0){
                                //控制板进入转发模式
                                MyApplication.serialAndCanPortUtils.canSendOrder(ControlPlateSend.canTo485(door));
                                System.out.println("GetBatteryInnerLog - 下发485转发命令");
                                sleep(2000);
                                //获取电池信息 查看是否有信息返回 测试线路是否正确
                                byte[] getBatteryVersion = BatterySend.getBatteryVersion();
                                MyApplication.serialAndCanPortUtils.serSendOrder(getBatteryVersion);
                                System.out.println("GetBatteryInnerLog - 下发获取电池版本号");
                                step = 100;
                            }

                            if(step == 1){

                                System.out.println("GetBatteryInnerLog - 485转发成功");

                                for(int i = 0 ; i < 10 ; i++){
                                    byte[] getBatteryInfo = BatterySend.getBatteryHistoryLog(i);
                                    MyApplication.serialAndCanPortUtils.serSendOrder(getBatteryInfo);
                                    sleep(1000);
                                }
                                step = 200;
                            }

                            step = step + 1;
                            if(step > 110  && step < 200){
                                getBatteryInnerLogListener.onGetBatteryInnerLogInfo("信息返回超时，请检查通信线路是否正常！");
                                onStop();
                            }
                            if(step > 210){
                                getBatteryInnerLogListener.onGetBatteryInnerLogInfo("获取信息成功");
                                onStop();
                            }

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }else{
            System.out.println("GetBatteryInnerLog -  stop thread first!!");
        }
    }

    public void onStop() {
        System.out.println("GetBatteryInnerLog - 线程退出");
        threadCode = 1;
        thread.interrupt();
        thread = null;
        getBatteryInnerLogListener.onGetBatteryInnerLogFinish();
    }
//
//    public void onDestory(){
//        onStop();
//        MyApplication.getInstance().deletListener(this);
//    }
//
//    //不能覆盖
//    @Override
//    public void onCanResultApp(CanDataFormat canDataFormat) {
//
//    }
//
//    @Override
//    public void onSerialResultApp(byte[] serData) {
//        if(serData[0] == (byte)0x3A && serData[1] == (byte)0x16 && serData[2] == (byte)0x7F){
//            step = 1;
//        }
//        if(serData[0] == (byte)0x3A && serData[1] == (byte)0x16 && serData[2] == (byte)0xA1){
//            BatteryHistoryLogsBean batteryHistoryLogsBean = BatteryReceiveEncap.batteryHistory(serData);
//            getBatteryInnerLogListener.onGetBatteryInnerLogRetun(batteryHistoryLogsBean);
//        }
//    }
}
