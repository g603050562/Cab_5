package com.NewElectric.app11.service.canAndSerial.can;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import com.NewElectric.app11.MyApplication;

public class BaseCanService extends Observable implements Observer {

    //舱门挂起时间（用于指定目标舱门换电 其他舱门不接收上传数据 维持原来的数据）
    protected long[] hangUpTimes = new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
    //舱门清除时间（用于弹出目标舱门 是其上传的数据都为初始化数据 防止二次换电）
    protected long[] cleanUpTimes = new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0};

    //接收数据
    @Override
    public void update(Observable observable, Object o) {

    }

    public void canInit() {
        MyApplication.serialAndCanPortUtils.addMyObserver(this);
    }

    public void onDestroy() {
        MyApplication.serialAndCanPortUtils.deleteMyObserver(this);
    }


    public void hangUpDoor(int door, int hangOnTime) {
        hangUpTimes[door - 1] = System.currentTimeMillis() + hangOnTime * 1000;
    }

    public void hangUpAllDoor(int hangOnTime) {
        for (int i = 0; i < 9; i++) {
            hangUpDoor(i + 1, hangOnTime);
        }
    }

    public void cleanUpDoor(int door, int cleanOnTime) {
        cleanUpTimes[door - 1] = System.currentTimeMillis() + cleanOnTime * 1000;
    }

    public void cleanUpDoorUpAllDoor(int cleanOnTime) {
        for (int i = 0; i < 9; i++) {
            cleanUpDoor(i + 1, cleanOnTime);
        }
    }

    public void cancelHangUpDoor(int door) {
        hangUpTimes[door - 1] = System.currentTimeMillis() - 1000;
        cleanUpTimes[door - 1] = System.currentTimeMillis() - 1000;
    }

    public void cancelHangUpAllDoor() {
        for (int i = 0; i < 9; i++) {
            cancelHangUpDoor(i + 1);
        }
    }

    //初始化数组
    protected byte[][] initArrays(int len) {
        byte[] byte_8 = new byte[8];
        Arrays.fill(byte_8, (byte) 0);
        byte[][] byte_len_8 = new byte[len][8];
        Arrays.fill(byte_len_8, byte_8);
        return byte_len_8;
    }
}
