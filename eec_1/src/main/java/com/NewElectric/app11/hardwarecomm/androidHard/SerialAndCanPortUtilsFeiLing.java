package com.NewElectric.app11.hardwarecomm.androidHard;

import android.util.Log;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Observer;

import com.NewElectric.app11.CanAndSer;
import com.NewElectric.app11.units.Units;
import forlinx.hardware.canFrame;
import forlinx.hardware.hardwareControl;


/**
 * 飞凌 大安卓版 can和485收发实现类
 */

public class SerialAndCanPortUtilsFeiLing extends SerialAndCanPortUtils {


    private FileInputStream cmFileInputStream;
    private FileOutputStream cmFileOutputStream;
    private OutputStream coutputStream;
    private FileInputStream smFileInputStream;
    private FileOutputStream smFileOutputStream;
    private OutputStream soutputStream;

    private int canReadState = 0;
    private int serReadState = 0;

    private canFrame scanFrame, mcanFrame;

    private static SerialAndCanPortUtilsFeiLing instance;
    private SerialAndCanPortUtilsFeiLing(){};
    public static SerialAndCanPortUtils getInstance(){
        if(instance == null){
            instance = new SerialAndCanPortUtilsFeiLing();
        }
        return instance;
    }


    public void sendData(DataFormat dataFormat){
        setChanged();
        notifyObservers(dataFormat);
    }

    @Override
    public void addMyObserver(Observer observer) {
        this.addObserver(observer);
    }

    @Override
    public void deleteMyObserver(Observer observer) {
        this.deleteObserver(observer);
    }



    /**
     * 打开485和can服务
     *
     * @return serialPort串口对象
     */
    @Override
    public void openCanPortAndSerialPort() {
        //485初始化
        FileDescriptor sFd = CanAndSer.openSer(new File("/dev/ttymxc2").getAbsolutePath(), 9600);
        smFileInputStream = new FileInputStream(sFd);
        smFileOutputStream = new FileOutputStream(sFd);
        soutputStream = getSerOutputStream();
        new ReadSerThread().start(); //开始线程监控是否有数据要接收
        //can初始化
        try {
            scanFrame = new canFrame();
            hardwareControl.openCan();
            hardwareControl.initCan(125000);
            if (!readCANThread.isAlive()) {
                readCANThread.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    /**
     * 串口写数据
     */
    public void serSendOrder(byte[] data) {
        System.out.println("SER 下发：" + Units.ByteArrToHex(data));
        try {
            if (data.length > 0) {
                soutputStream.write(data);
                soutputStream.flush();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 单开一线程，来读串口数据
     */

    private int send_time = 160;
    private byte[] str = new byte[]{};

    private class ReadSerThread extends Thread {
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            sendMessage.start();
            while (serReadState == 0) {
                //64   1024
                send_time = 160;
                byte[] buffer = new byte[64];
                try {
                    int size = smFileInputStream.read(buffer);
                    if (size > 0) {
                        byte[] temp = new byte[size];
                        System.arraycopy(buffer, 0, temp, 0, size);
                        str = arrayJoin(str, temp);
                    }
                } catch (IOException e) {
                    Log.e("TAR", "run: 数据读取异常：" + e.toString());
                }
            }
        }
    }

    private Thread sendMessage = new Thread() {

        @Override
        public void run() {
            while (serReadState == 0) {
                try {
                    sleep(20);
                    if (send_time <= 0) {
                        if (str.length > 0) {
                            sendData(new DataFormat("serial",str));
                            send_time = -1;
                            str = new byte[]{};
                        }
                    } else if (send_time > 0) {
                        send_time = send_time - 20;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    /**
     * 单开一线程，来读CAN数据
     */
    private Thread readCANThread = new Thread() {
        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                mcanFrame = hardwareControl.canRead(scanFrame, 1);
                long canid = mcanFrame.can_id;
                byte[] candata = mcanFrame.recdata;
                if(canid < 0){
                    canid = canid + 4294967296l;
                }
                byte returnByte[] = new byte[16];
                Arrays.fill(returnByte, (byte) 0);
                long a = (long) canid / 256 / 256 / 256;
                long b = (long) canid / 256 / 256;
                long c = (long) canid / 256;
                long d = (long) canid % 256;

                if (a < 0) {
                    a = a - 1;
                }

                returnByte[0] = (byte) d;
                returnByte[1] = (byte) c;
                returnByte[2] = (byte) b;
                returnByte[3] = (byte) a;
                for (int i = 0; i < mcanFrame.recdata.length; i++) {
                    returnByte[i + 8] = mcanFrame.recdata[i];
                }

                if (mcanFrame.can_dlc != 0) {
                    sendData(new DataFormat("can",returnByte));
                }
            }
        }
    };

    /**
     * 写入CAN数据
     */
    public void canSendOrder(String str, byte[] data) {
        long c = Long.parseLong(str, 16);
        hardwareControl.canWrite((int) c, data);
    }

    /**
     * 写入CAN数据
     */
    public void canSendOrder(byte[] data) {
        try {

            if (data != null && data.length > 0) {
                coutputStream.write(data);
                coutputStream.flush();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 写入CAN数据
     */
    public void canSendOrder(CanDataFormat canDataFormat){
        canSendOrder(canDataFormat.getAddressByStr(), canDataFormat.getData());
    }

    public void onDestroy() {
        canReadState = 1;
        serReadState = 1;
        cmFileInputStream = null;
        cmFileOutputStream = null;
        coutputStream = null;
        smFileInputStream = null;
        smFileOutputStream = null;
        soutputStream = null;
    }

    public byte[] arrayJoin(byte[] a, byte[] b) {
        byte[] arr = new byte[a.length + b.length];//开辟新数组长度为两数组之和
        for (int i = 0; i < a.length; i++) {//拷贝a数组到目标数组arr
            arr[i] = a[i];
        }
        for (int j = 0; j < b.length; j++) {//拷贝b数组到目标数组arr
            arr[a.length + j] = b[j];
        }
        return arr;
    }

    public OutputStream getCanOutputStream() {
        return cmFileOutputStream;
    }

    public OutputStream getSerOutputStream() {
        return smFileOutputStream;
    }
}
