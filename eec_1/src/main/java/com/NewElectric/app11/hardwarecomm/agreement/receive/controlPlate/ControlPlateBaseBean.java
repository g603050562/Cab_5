package com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate;

public class ControlPlateBaseBean {

    //舱门地址
    private int address = -1;
    //帧类型
    private int type = -1;
    //数据长度
    private int dataCount = -1;

    //三个微动或者其他硬件信息
    private int inching_1 = 0; //5代柜以及以前 - 这个代表舱门的底部微动
    private int inching_2 = 0; //5代柜以及以前 - 这个无意义
    private int inching_3 = 0; //5代柜以及以前 - 这个代表舱门的侧边微动

    //电池温度
    private double batteryTemperature = -1;
    //电池内部电流
    private int batteryElectric = -1;
    //相对剩余容量 - SOC
    private int batteryRelativeSurplus = 0;
    //绝对剩余容量
    private int batteryAbsoluteSurplus = -1;
    //剩余容量
    private int batteryRemainingCapacity = -1;
    //满充容量
    private int batteryFullCapacity = -1;

    //循环次数
    private int loops = -1;
    //单体电压最小 格式:  "串数下标"+_+"数值"
    private String itemMin = "0_0";
    //单体电压最大 格式:  "串数下标"+_+"数值"
    private String itemMax = "0_0";
    //压差
    private int pressureDifferential = -1;

    //控制板上层版本
    private int controlPlateTopVersion = -1;
    //控制板底层版本
    private int controlPlateBottomVersion = -1;

    //电池ID
    private String BID = "0000000000000000";
    //电池UID
    private String UID = "00000000";
    //电池电压
    private int batteryVoltage = -1;
    //电池版本 格式："软件版本"+"硬件版本"
    private String batteryVersion = "0000";
    //控制板温度
    private int controlPlateTemperature = -1;
    //电池健康百分比
    private int batteryHealthy = -1;

    //温度传感器1
    private double temperatureSensor_1 = -1;
    //温度传感器2
    private double temperatureSensor_2 = -1;

    //最后上传时间
    private long dataTime = 0;


    public ControlPlateBaseBean(int address, int type, int dataCount, int inching_1, int inching_2, int inching_3, int batteryTemperature, int batteryElectric, int batteryRelativeSurplus, int battrtyAbsoluteSurplus, int batteryRemainingCapacity, int batteryFullCapacity, int loops, String itemMin, String itemMax, int pressureDifferential, int controlPlateTopVersion, int controlPlateBottomVersion, String BID, int batteryVoltage, String batteryVersion, int controlPlateTemperature, int batteryHealthy, int temperatureSensor_1, int temperatureSensor_2, String UID) {
        this.address = address;
        this.type = type;
        this.dataCount = dataCount;
        this.inching_1 = inching_1;
        this.inching_2 = inching_2;
        this.inching_3 = inching_3;
        this.batteryTemperature = batteryTemperature;
        this.batteryElectric = batteryElectric;
        this.batteryRelativeSurplus = batteryRelativeSurplus;
        this.batteryAbsoluteSurplus = battrtyAbsoluteSurplus;
        this.batteryRemainingCapacity = batteryRemainingCapacity;
        this.batteryFullCapacity = batteryFullCapacity;
        this.loops = loops;
        this.itemMin = itemMin;
        this.itemMax = itemMax;
        this.pressureDifferential = pressureDifferential;
        this.controlPlateTopVersion = controlPlateTopVersion;
        this.controlPlateBottomVersion = controlPlateBottomVersion;
        this.BID = BID;
        this.batteryVoltage = batteryVoltage;
        this.batteryVersion = batteryVersion;
        this.controlPlateTemperature = controlPlateTemperature;
        this.batteryHealthy = batteryHealthy;
        this.temperatureSensor_1 = temperatureSensor_1;
        this.temperatureSensor_2 = temperatureSensor_2;
        this.UID = UID;
        dataTime = System.currentTimeMillis();
    }

    public void init(){
        inching_1 = -1;
        inching_2 = -1;
        inching_3 = -1;
        batteryRelativeSurplus = -1;
        BID = "FFFFFFFFFFFFFFFF";
        UID = "FFFFFFFF";
    }


    public ControlPlateBaseBean() {
        dataTime = System.currentTimeMillis();
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public int getInching_1() {
        return inching_1;
    }

    public void setInching_1(int inching_1) {
        this.inching_1 = inching_1;
    }

    public int getInching_2() {
        return inching_2;
    }

    public void setInching_2(int inching_2) {
        this.inching_2 = inching_2;
    }

    public int getInching_3() {
        return inching_3;
    }

    public void setInching_3(int inching_3) {
        this.inching_3 = inching_3;
    }

    public double getBatteryTemperature() {
        return batteryTemperature;
    }

    public void setBatteryTemperature(double batteryTemperature) {
        this.batteryTemperature = batteryTemperature;
    }

    public int getBatteryElectric() {
        return batteryElectric;
    }

    public void setBatteryElectric(int batteryElectric) {
        this.batteryElectric = batteryElectric;
    }

    public int getBatteryRelativeSurplus() {
        return batteryRelativeSurplus;
    }

    public void setBatteryRelativeSurplus(int batteryRelativeSurplus) {
        this.batteryRelativeSurplus = batteryRelativeSurplus;
    }

    public int getBatteryAbsoluteSurplus() {
        return batteryAbsoluteSurplus;
    }

    public void setBatteryAbsoluteSurplus(int battrtyAbsoluteSurplus) {
        this.batteryAbsoluteSurplus = battrtyAbsoluteSurplus;
    }

    public int getBatteryRemainingCapacity() {
        return batteryRemainingCapacity;
    }

    public void setBatteryRemainingCapacity(int batteryRemainingCapacity) {
        this.batteryRemainingCapacity = batteryRemainingCapacity;
    }

    public int getBatteryFullCapacity() {
        return batteryFullCapacity;
    }

    public void setBatteryFullCapacity(int batteryFullCapacity) {
        this.batteryFullCapacity = batteryFullCapacity;
    }

    public int getLoops() {
        return loops;
    }

    public void setLoops(int loops) {
        this.loops = loops;
    }

    public String getItemMin() {
        return itemMin;
    }

    public void setItemMin(String itemMin) {
        this.itemMin = itemMin;
    }

    public String getItemMax() {
        return itemMax;
    }

    public void setItemMax(String itemMax) {
        this.itemMax = itemMax;
    }

    public int getPressureDifferential() {
        return pressureDifferential;
    }

    public void setPressureDifferential(int pressureDifferential) {
        this.pressureDifferential = pressureDifferential;
    }

    public int getControlPlateTopVersion() {
        return controlPlateTopVersion;
    }

    public void setControlPlateTopVersion(int controlPlateTopVersion) {
        this.controlPlateTopVersion = controlPlateTopVersion;
    }

    public int getControlPlateBottomVersion() {
        return controlPlateBottomVersion;
    }

    public void setControlPlateBottomVersion(int controlPlateBottomVersion) {
        this.controlPlateBottomVersion = controlPlateBottomVersion;
    }

    public String getBID() {
        return BID;
    }

    public void setBID(String BID) {
        this.BID = BID;
    }

    public int getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(int batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public String getBatteryVersion() {
        return batteryVersion;
    }

    public void setBatteryVersion(String batteryVersion) {
        this.batteryVersion = batteryVersion;
    }

    public int getControlPlateTemperature() {
        return controlPlateTemperature;
    }

    public void setControlPlateTemperature(int controlPlateTemperature) {
        this.controlPlateTemperature = controlPlateTemperature;
    }

    public int getBatteryHealthy() {
        return batteryHealthy;
    }

    public void setBatteryHealthy(int batteryHealthy) {
        this.batteryHealthy = batteryHealthy;
    }

    public double getTemperatureSensor_1() {
        return temperatureSensor_1;
    }

    public void setTemperatureSensor_1(double temperatureSensor_1) {
        this.temperatureSensor_1 = temperatureSensor_1;
    }

    public double getTemperatureSensor_2() {
        return temperatureSensor_2;
    }

    public void setTemperatureSensor_2(double temperatureSensor_2) {
        this.temperatureSensor_2 = temperatureSensor_2;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }


    @Override
    public String toString() {
        return "ControlPlateBaseBean{" +
                "address=" + address +
                ", type=" + type +
                ", dataCount=" + dataCount +
                ", inching_1=" + inching_1 +
                ", inching_2=" + inching_2 +
                ", inching_3=" + inching_3 +
                ", batteryTemperature=" + batteryTemperature +
                ", batteryElectric=" + batteryElectric +
                ", batteryRelativeSurplus=" + batteryRelativeSurplus +
                ", batteryAbsoluteSurplus=" + batteryAbsoluteSurplus +
                ", batteryRemainingCapacity=" + batteryRemainingCapacity +
                ", batteryFullCapacity=" + batteryFullCapacity +
                ", loops=" + loops +
                ", itemMin='" + itemMin + '\'' +
                ", itemMax='" + itemMax + '\'' +
                ", pressureDifferential=" + pressureDifferential +
                ", controlPlateTopVersion=" + controlPlateTopVersion +
                ", controlPlateBottomVersion=" + controlPlateBottomVersion +
                ", BID='" + BID + '\'' +
                ", batteryVoltage=" + batteryVoltage +
                ", batteryVersion='" + batteryVersion + '\'' +
                ", controlPlateTemperature=" + controlPlateTemperature +
                ", batteryHealthy=" + batteryHealthy +
                ", temperatureSensor_1=" + temperatureSensor_1 +
                ", temperatureSensor_2=" + temperatureSensor_2 +
                ", UID='" + UID + '\'' +
                '}';
    }
}
