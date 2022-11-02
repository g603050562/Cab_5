package com.NewElectric.app11.hardwarecomm.androidHard;

import java.util.Observer;

/**
 * 抽象模型
 */

public abstract class SerialAndCanPortUtils extends SerialAndCanPortUtilsObservable {

    public abstract void serSendOrder(byte[] data);

    public abstract void canSendOrder(String str, byte[] data);

    public abstract void canSendOrder(byte[] data);

    public abstract void canSendOrder(CanDataFormat canDataFormat);

    public abstract void openCanPortAndSerialPort();

    public abstract void addMyObserver(Observer observer);

    public abstract void deleteMyObserver(Observer observer);
}
