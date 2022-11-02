package com.NewElectric.app11.service.logic.logic_update.controlplate;

import android.util.SparseArray;

import com.NewElectric.app11.service.logic.logic_update.OnRwAction;
import com.NewElectric.app11.service.logic.logic_update.OnUpgradeProgress;
import com.NewElectric.app11.service.logic.logic_update.SerialPortRwAction;
import com.NewElectric.app11.service.logic.logic_update.UpgradeCallBack;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-08
 * Description:
 */
public class ControlPanelExecuter extends SparseArray<UpgradeCallBack> implements OnRwAction, OnUpgradeProgress {

    //从外部接收数据的接口
    private SerialPortRwAction serialPortRwAction;
    private final byte[] EMPTY = new byte[0];
    private int type = 0;

    public void init(SerialPortRwAction serialPortRwAction) {
        this.serialPortRwAction = serialPortRwAction;
    }

    public void init_12(SerialPortRwAction serialPortRwAction) {
        this.serialPortRwAction = serialPortRwAction;
        type = 1;
    }

    public void upgrade(ControlPanelUpgradeMessage controlPanelUpgradeMessage) {
        if (controlPanelUpgradeMessage != null) {

            if(type == 0){
                ControlPanel_485_9_UpgradeProgram controlPanel_485_9_UpgradeProgram = new ControlPanel_485_9_UpgradeProgram(controlPanelUpgradeMessage.getAddress(), controlPanelUpgradeMessage.getFilePath());
                if (controlPanel_485_9_UpgradeProgram != null) {
                    put(controlPanelUpgradeMessage.getAddress(), controlPanelUpgradeMessage.getUpgradeCallBack());
                    controlPanel_485_9_UpgradeProgram.upgrade(this, this);
                }
            }else{
                ControlPanel_485_12_UpgradeProgram controlPanel_485_12_UpgradeProgram = new ControlPanel_485_12_UpgradeProgram(controlPanelUpgradeMessage.getAddress(), controlPanelUpgradeMessage.getFilePath());
                if (controlPanel_485_12_UpgradeProgram != null) {
                    put(controlPanelUpgradeMessage.getAddress(), controlPanelUpgradeMessage.getUpgradeCallBack());
                    controlPanel_485_12_UpgradeProgram.upgrade(this, this);
                }
            }
        }
    }

    @Override
    public void write(byte[] bytes) {
        if (serialPortRwAction != null) {
            serialPortRwAction.write(bytes);
        }
    }

    @Override
    public byte[] read() {
        return serialPortRwAction != null ? serialPortRwAction.read() : EMPTY;
    }

    @Override
    public void onUpgrade(byte mapAddress, byte statusFlag, String statusInfo, long currentPregress, long totalPregress) {
        UpgradeCallBack upgradeCallBack = get(mapAddress);
        if (upgradeCallBack != null) {
            switch (statusFlag) {
                case ControlPanelUpgradeStatus.WAITTING:
                    upgradeCallBack.onUpgradeBefore(mapAddress);
                    break;
                case ControlPanelUpgradeStatus.MODE_1:
                    break;
                case ControlPanelUpgradeStatus.MODE_2:
                    break;
                case ControlPanelUpgradeStatus.MODE_3:
                    break;
                case ControlPanelUpgradeStatus.WRITE_DATA:
                    upgradeCallBack.onUpgrade(mapAddress, currentPregress, totalPregress);
                    break;
                case ControlPanelUpgradeStatus.BATTERY_INFO:
                    break;
                case ControlPanelUpgradeStatus.FAILED:
                    upgradeCallBack.onError(mapAddress, statusInfo);
                    break;
                case ControlPanelUpgradeStatus.SUCCESSED:
                    upgradeCallBack.onUpgradeAfter(mapAddress);
                    remove(mapAddress);
                    break;
            }
        }
    }
}
