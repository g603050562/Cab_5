package com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate;

/**
 *  2021年3月15日
 *  电池国标 故障信息
 */
public class ControlPlateWarningBean {

    //电池告警状态
    private String warningInfo = "";
    //电池故障信息
    private String errorInfo = "";
    //mos信息
    private String mosInfo = "";
    //所需电压
    private String requireVol = "";
    //所需电流
    private String requireELe = "";
    //可承受电压
    private String supportVol = "";
    //可承受电流
    private String supportEle = "";

    public ControlPlateWarningBean(String warningInfo, String errorInfo, String mosInfo, String requireVol, String requireELe, String supportVol, String supportEle) {
        this.warningInfo = warningInfo;
        this.errorInfo = errorInfo;
        this.mosInfo = mosInfo;
        this.requireVol = requireVol;
        this.requireELe = requireELe;
        this.supportVol = supportVol;
        this.supportEle = supportEle;
    }

    public ControlPlateWarningBean() {
    }

    public String getWarningInfo() {
        return warningInfo;
    }

    public void setWarningInfo(String warningInfo) {
        this.warningInfo = warningInfo;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getMosInfo() {
        return mosInfo;
    }

    public void setMosInfo(String mosInfo) {
        this.mosInfo = mosInfo;
    }

    public String getRequireVol() {
        return requireVol;
    }

    public void setRequireVol(String requireVol) {
        this.requireVol = requireVol;
    }

    public String getRequireELe() {
        return requireELe;
    }

    public void setRequireELe(String requireELe) {
        this.requireELe = requireELe;
    }

    public String getSupportVol() {
        return supportVol;
    }

    public void setSupportVol(String supportVol) {
        this.supportVol = supportVol;
    }

    public String getSupportEle() {
        return supportEle;
    }

    public void setSupportEle(String supportEle) {
        this.supportEle = supportEle;
    }

    @Override
    public String toString() {
        return "CabinInfoWarning{" +
                "warningInfo=" + warningInfo +
                ", errorInfo=" + errorInfo +
                ", mosInfo=" + mosInfo +
                ", requireVol=" + requireVol +
                ", requireELe=" + requireELe +
                ", supportVol=" + supportVol +
                ", supportEle=" + supportEle +
                '}';
    }
}
