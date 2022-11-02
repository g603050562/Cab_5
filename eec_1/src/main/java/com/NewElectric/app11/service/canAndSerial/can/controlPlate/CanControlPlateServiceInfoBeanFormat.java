package com.NewElectric.app11.service.canAndSerial.can.controlPlate;

public class CanControlPlateServiceInfoBeanFormat<T> {

    private int index = 0;
    private T data = null;

    public CanControlPlateServiceInfoBeanFormat(int index, T data) {
        this.index = index;
        this.data = data;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
