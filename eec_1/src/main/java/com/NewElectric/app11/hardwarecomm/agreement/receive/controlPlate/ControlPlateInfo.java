package com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate;

import java.util.Arrays;

public class ControlPlateInfo {

    private ControlPlateBaseBean[] controlPlateBaseBeans = new ControlPlateBaseBean[9];
    private ControlPlateWarningBean[] controlPlateWarningBeans = new ControlPlateWarningBean[9];

    public ControlPlateInfo(ControlPlateBaseBean[] controlPlateBaseBeans, ControlPlateWarningBean[] controlPlateWarningBeans) {
        this.controlPlateBaseBeans = controlPlateBaseBeans;
        this.controlPlateWarningBeans = controlPlateWarningBeans;
    }

    public ControlPlateInfo() {
        ControlPlateBaseBean controlPlateBaseBean = new ControlPlateBaseBean();
        controlPlateBaseBean.init();
        Arrays.fill(controlPlateBaseBeans,controlPlateBaseBean);
        Arrays.fill(controlPlateWarningBeans,new ControlPlateWarningBean());
    }

    public void setControlPlateBaseBeans(ControlPlateBaseBean[] controlPlateBaseBeans) {
        this.controlPlateBaseBeans = controlPlateBaseBeans;
    }

    public void setControlPlateWarningBeans(ControlPlateWarningBean[] controlPlateWarningBeans) {
        this.controlPlateWarningBeans = controlPlateWarningBeans;
    }

    public ControlPlateBaseBean[] getControlPlateBaseBeans() {
        return controlPlateBaseBeans;
    }

    public void setControlPlateBaseBean(int index ,ControlPlateBaseBean controlPlateBaseBean) {
        this.controlPlateBaseBeans[index] = controlPlateBaseBean;
    }

    public ControlPlateWarningBean[] getControlPlateWarningBeans() {
        return controlPlateWarningBeans;
    }

    public void setControlPlateWarningBean(int index , ControlPlateWarningBean controlPlateWarningBean) {
        this.controlPlateWarningBeans[index] = controlPlateWarningBean;
    }

    @Override
    public String toString() {
        return "ControlPlateBean{" +
                "controlPlateBaseBeans=" + Arrays.toString(controlPlateBaseBeans) +
                ", controlPlateWarningBeans=" + Arrays.toString(controlPlateWarningBeans) +
                '}';
    }
}
