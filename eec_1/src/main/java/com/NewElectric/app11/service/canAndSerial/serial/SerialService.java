package com.NewElectric.app11.service.canAndSerial.serial;

import android.content.Context;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.agreement.receive.other.ElectricityMeterReceiveEncap;
import com.NewElectric.app11.hardwarecomm.agreement.receive.other.EnvironmentalMeterReceiveEncap;
import com.NewElectric.app11.hardwarecomm.agreement.send.other.ElectricityMeterSend;
import com.NewElectric.app11.hardwarecomm.agreement.send.other.EnvironmentalMeterSend;
import com.NewElectric.app11.hardwarecomm.agreement.receive.other.EnvironmentalMeterBean;
import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.units.Units;

public class SerialService extends Observable implements Observer {

    //单例
    private static SerialService instance = new SerialService();
    private SerialService(){}
    public static SerialService getInstance() {
        return instance;
    }


    private Thread thread = null;
    //线程运行参数 0 - 正常 1 - 关闭退出
    private int threadStopCode = 0;
    //线程挂起参数 0 - 正常 1 - 挂起
    private int threadHandCode = 0;
    //返回数据储存
    private SerialServiceReturnFormat serialServiceReturnFormat;
    //数据储存
    private CabInfoSp cabInfoSp;


    private void sendData(SerialServiceReturnFormat serialServiceReturnFormat){
        setChanged();
        notifyObservers(new DataFormat<>("_485",serialServiceReturnFormat));
    }


    public void _485Init(Context context) {

        MyApplication.serialAndCanPortUtils.addMyObserver(this);
        serialServiceReturnFormat = new SerialServiceReturnFormat();
        cabInfoSp = new CabInfoSp(context);

        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(10000);
                        //获取温湿传感器参数
                        MyApplication.serialAndCanPortUtils.serSendOrder(EnvironmentalMeterSend.getEnvMeterInfo());
                        sleep(2000);
                        //获取电表地址
                        MyApplication.serialAndCanPortUtils.serSendOrder(ElectricityMeterSend.getMeterAddress());
                        sleep(2000);
                        while (threadStopCode == 0) {

                            sleep(1000);

                            Calendar calendar = Calendar.getInstance();
                            int minute = calendar.get(Calendar.MINUTE);
                            int second = calendar.get(Calendar.SECOND);

                            if(threadHandCode == 0){
                                //每1分钟下发温湿或者电量的指令
                                if (minute % 2 == 0 && second == 2) {
                                    MyApplication.serialAndCanPortUtils.serSendOrder(EnvironmentalMeterSend.getEnvMeterInfo());
                                }
                                if (minute % 2 == 1 && second == 2) {
                                    MyApplication.serialAndCanPortUtils.serSendOrder(ElectricityMeterSend.getMeterAddress());
                                }
                            }else{
                                //挂起时不做任何操作
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    @Override
    public void update(Observable observable, Object object) {
        DataFormat dataFormat = (DataFormat)object;
        if(dataFormat.getType().equals("serial")){
            try {
            String str = Units.ByteArrToHex((byte[])dataFormat.getData());
//            System.out.println("SER 返回：" + str);
                //返回帧类型
                int type_count = str.length();
                String head = str.substring(0, 2);
                if (type_count == 18) { //温湿传感器处理
                    String address = str.substring(0, 2);
                    if (address.equals("41")) {
                        EnvironmentalMeterBean environmentalMeterBean = EnvironmentalMeterReceiveEncap.getEnvironmentalMeterInfo(str);
                        serialServiceReturnFormat.setTemMeter(environmentalMeterBean.getTemMeter());
                        serialServiceReturnFormat.setTheMeter(environmentalMeterBean.getTheMeter());
                        cabInfoSp.setTheMeter(environmentalMeterBean.getTheMeter());
                        cabInfoSp.setTemMeter(environmentalMeterBean.getTemMeter());
                        sendData(serialServiceReturnFormat);
                    }
                } else if (type_count == 44) {   //电表兼容一 ： 返回电表地址
                    String address = str.substring(10, 22);
                    byte[] data = ElectricityMeterSend.getMeterEle(address);
                    MyApplication.serialAndCanPortUtils.serSendOrder(data);
                } else if (type_count == 48) {   //电表兼容一 ： 返回电表信息
                    serialServiceReturnFormat.setEleMeter( ElectricityMeterReceiveEncap.getMeterCount_1(str)+"");
                    cabInfoSp.setEleMeter( ElectricityMeterReceiveEncap.getMeterCount_1(str)+"");
                    sendData(serialServiceReturnFormat);
                } else if (type_count == 36 && head.equals("68")) {  //电表兼容二 ： 返回电表地址
                    String address = str.substring(2, 14);
                    byte[] data = ElectricityMeterSend.getMeterEle(address);
                    MyApplication.serialAndCanPortUtils.serSendOrder(data);
                } else if (type_count == 40 && head.equals("68")) {   //电表兼容二 ： 返回电表信息
                    serialServiceReturnFormat.setEleMeter( ElectricityMeterReceiveEncap.getMeterCount_2(str)+"");
                    cabInfoSp.setEleMeter( ElectricityMeterReceiveEncap.getMeterCount_1(str)+"");
                    sendData(serialServiceReturnFormat);
                }
        } catch (Exception e) {
            System.out.println(e);
        }
        }
    }

    public int getThreadHangOnCode() {
        return threadHandCode;
    }

    /**
     * 设置ser服务挂起
     * @param threadHandCode   0-正常   1-挂起
     */
    public void threadHangOnCode(int threadHandCode) {
        this.threadHandCode = threadHandCode;
    }

    public void onDestroy() {
        threadStopCode = 1;
        MyApplication.serialAndCanPortUtils.deleteMyObserver(this);
    }

}
