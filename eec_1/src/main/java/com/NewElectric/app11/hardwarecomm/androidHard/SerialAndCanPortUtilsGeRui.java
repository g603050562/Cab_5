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
import com.NewElectric.app11.units.RootCommand;


/**
 * 格瑞斯特 小安卓版 can和485收发实现类
 */

public class SerialAndCanPortUtilsGeRui extends SerialAndCanPortUtils{


    private FileInputStream cmFileInputStream;
    private FileOutputStream cmFileOutputStream;
    private OutputStream coutputStream;
    private FileInputStream smFileInputStream;
    private FileOutputStream smFileOutputStream;
    private OutputStream soutputStream;

    private int canReadState = 0;
    private int serReadState = 0;

    private static SerialAndCanPortUtilsGeRui instance;
    private SerialAndCanPortUtilsGeRui(){};
    public static SerialAndCanPortUtilsGeRui getInstance(){
        if(instance == null){
            instance = new SerialAndCanPortUtilsGeRui();
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
     * 打开串口
     *
     * @return serialPort串口对象
     */

    @Override
    public void openCanPortAndSerialPort() {
        //485初始化
        FileDescriptor sFd = CanAndSer.openSer(new File("/dev/ttyS4").getAbsolutePath(), 9600);
        smFileInputStream = new FileInputStream(sFd);
        smFileOutputStream = new FileOutputStream(sFd);
        soutputStream = getSerOutputStream();
        new ReadSerThread().start(); //开始线程监控是否有数据要接收
        //can初始化
        RootCommand.execRootCmd("ip link set can0 down");
        RootCommand.execRootCmd("ip link set can0 type can loopback off triple-sampling on");
        RootCommand.execRootCmd("ip link set can0 type can bitrate 125000 loopback off triple-sampling on");
        RootCommand.execRootCmd("ip link set can0 up");
        FileDescriptor cFd = CanAndSer.openCan();
        cmFileInputStream = new FileInputStream(cFd);
        cmFileOutputStream = new FileOutputStream(cFd);
        coutputStream = getCanOutputStream();
        new ReadCanThread().start(); //开始线程监控是否有数据要接收
    }


    /**
     * 单开一线程，来读数据
     */

    private int send_time = 200;
    private byte[] str = new byte[]{};

    private class ReadSerThread extends Thread {
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            sendMessage.start();
            while (serReadState == 0) {
                //64   1024
                send_time = 200;
                byte[] buffer = new byte[64];
                try {
                    int size = smFileInputStream.read(buffer);
                    if (size > 0) {
                        byte[] temp = new byte[size];
                        System.arraycopy(buffer,0,temp,0,size);
                        str = arrayJoin(str, temp);
                    }
                } catch (IOException e) {
                    Log.e("TAR", "run: 数据读取异常：" + e.toString());
                }
            }
        }
    };

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
     * 单开一线程，来读数据
     */
    private class ReadCanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (canReadState == 0) {
                byte[] buffer = new byte[16];
                try {
                    int size = cmFileInputStream.read(buffer);
                    if (size > 0) {
                        sendData(new DataFormat("can",buffer));
                    }
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }

        }
    }


    public void serSendOrder(byte[] data) {
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
     * 写入CAN数据
     */
    public void canSendOrder(String str, byte[] data) {

        try {
            int a_int = Integer.parseInt(str.substring(6, 8), 16);
            int b_int = Integer.parseInt(str.substring(4, 6), 16);
            int c_int = Integer.parseInt(str.substring(2, 4), 16);
            int d_int = Integer.parseInt(str.substring(0, 2), 16);

            byte[] sendData = new byte[16];
            Arrays.fill(sendData, (byte) 0);
            sendData[0] = (byte) a_int;
            sendData[1] = (byte) b_int;
            sendData[2] = (byte) c_int;
            sendData[3] = (byte) d_int;
            sendData[4] = (byte) data.length;
            sendData[5] = (byte) 0x00;
            sendData[6] = (byte) 0x00;
            sendData[7] = (byte) 0x00;

            for (int i = 0; i < data.length; i++) {
                sendData[8 + i] = data[i];
            }

            if (sendData.length > 0) {
                coutputStream.write(sendData);
                coutputStream.flush();
            }

        } catch (IOException e) {
            System.out.println(e.toString());
        }
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
