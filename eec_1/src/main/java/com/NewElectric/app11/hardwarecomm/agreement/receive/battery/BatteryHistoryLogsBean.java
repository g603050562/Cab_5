package com.NewElectric.app11.hardwarecomm.agreement.receive.battery;

public class BatteryHistoryLogsBean {

    private String time = "";
    private long totalCount = 0;
    private String logType = "";
    private String batteryVersion = "";
    private int soc = 0;
    private String batteryType = "";
    private String warningInfo = "";
    private long batteryLoops;

    public BatteryHistoryLogsBean() {
    }

    public BatteryHistoryLogsBean(String time, long totalCount, String logType, String batteryVersion, int soc, String batteryType, String warningInfo, long batteryLoops) {
        this.time = time;
        this.totalCount = totalCount;
        this.logType = logType;
        this.batteryVersion = batteryVersion;
        this.soc = soc;
        this.batteryType = batteryType;
        this.warningInfo = warningInfo;
        this.batteryLoops = batteryLoops;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getBatteryVersion() {
        return batteryVersion;
    }

    public void setBatteryVersion(String batteryVersion) {
        this.batteryVersion = batteryVersion;
    }

    public int getSoc() {
        return soc;
    }

    public void setSoc(int soc) {
        this.soc = soc;
    }

    public String getBatteryType() {
        return batteryType;
    }

    public void setBatteryType(String batteryType) {
        this.batteryType = batteryType;
    }

    public String getWarningInfo() {
        return warningInfo;
    }

    public void setWarningInfo(String warningInfo) {
        this.warningInfo = warningInfo;
    }

    public long getBatteryLoops() {
        return batteryLoops;
    }

    public void setBatteryLoops(long batteryLoops) {
        this.batteryLoops = batteryLoops;
    }

    @Override
    public String toString() {
        return "BatteryHistoryLogs{" +
                "time='" + time + '\'' +
                ", totalCount=" + totalCount +
                ", logType='" + logType + '\'' +
                ", batteryVersion='" + batteryVersion + '\'' +
                ", soc=" + soc +
                ", batteryType='" + batteryType + '\'' +
                ", warningInfo='" + warningInfo + '\'' +
                ", batteryLoops=" + batteryLoops +
                '}';
    }
}
