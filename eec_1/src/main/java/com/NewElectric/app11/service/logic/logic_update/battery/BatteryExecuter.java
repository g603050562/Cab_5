package com.NewElectric.app11.service.logic.logic_update.battery;

import android.text.TextUtils;
import android.util.SparseArray;



import com.NewElectric.app11.service.logic.logic_update.OnRwAction;
import com.NewElectric.app11.service.logic.logic_update.OnUpgradeProgress;
import com.NewElectric.app11.service.logic.logic_update.SerialPortRwAction;
import com.NewElectric.app11.service.logic.logic_update.UpgradeCallBack;
import com.NewElectric.app11.service.logic.logic_update.battery.BoQiang.BoQiangBatteryUpgradeProgram;
import com.NewElectric.app11.service.logic.logic_update.battery.GuanTong.GuanTongBatteryUpgradeProgram;
import com.NewElectric.app11.service.logic.logic_update.battery.JieMinKe.JieMinKeBatteryUpgradeProgram;
import com.NewElectric.app11.service.logic.logic_update.battery.NuoWan.NuoWanBatteryUpgradeProgram;


/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-08
 * Description:
 */
public class BatteryExecuter extends SparseArray<UpgradeCallBack> implements OnRwAction, OnUpgradeProgress {

    private SerialPortRwAction serialPortRwAction;
    private final byte[] EMPTY = new byte[0];

    public void init(SerialPortRwAction serialPortRwAction) {
        this.serialPortRwAction = serialPortRwAction;
    }

    public void upgrade(BatteryUpgradeMessage batteryUpgradeMessage) {
        if (batteryUpgradeMessage != null) {

            BatteryUpgradeProgram batteryUpgradeProgram = null;

            final String manufactuer = batteryUpgradeMessage.getManufacturer();
            if (!TextUtils.isEmpty(manufactuer) && !TextUtils.isEmpty(manufactuer.trim()) && manufactuer.trim().length() >= 2) {
                switch (manufactuer.trim().charAt(1)) {
                    case UpgradeNameTable.NuoWan:
                        batteryUpgradeProgram = new NuoWanBatteryUpgradeProgram(batteryUpgradeMessage.getAddress(),
                                batteryUpgradeMessage.getFilePath());
                        break;
                    case UpgradeNameTable.BoQiang:
                        batteryUpgradeProgram = new BoQiangBatteryUpgradeProgram(batteryUpgradeMessage.getAddress(),
                                batteryUpgradeMessage.getFilePath());
                        break;
                    case UpgradeNameTable.GuanTong:
                        batteryUpgradeProgram = new GuanTongBatteryUpgradeProgram(batteryUpgradeMessage.getAddress(),
                                batteryUpgradeMessage.getFilePath());
                        break;
                    default:
                        batteryUpgradeProgram = new JieMinKeBatteryUpgradeProgram(batteryUpgradeMessage.getAddress(),
                                batteryUpgradeMessage.getFilePath());
                        break;
                }
            }

            boolean isUpgrade = false;
            if (batteryUpgradeProgram != null) {
                put(batteryUpgradeMessage.getAddress(), batteryUpgradeMessage.getUpgradeCallBack());

                // TODO: 2019-11-15 解析文件名称
                String path = batteryUpgradeMessage.getFilePath();
                if (!TextUtils.isEmpty(path)) {
                    int startPos = path.lastIndexOf("/");
                    int lastPos = path.lastIndexOf(".");
                    if (startPos != -1 && lastPos != -1) {
                        path = path.substring(startPos + 1, lastPos);
                    }
                    String[] paths = path.split("_");
                    if (paths != null && paths.length == 4) {
                        batteryUpgradeProgram.setIdCodeAndBmsHardwareVersion(paths[0], paths[1], paths[3]);
                        isUpgrade = true;
                    }
                }

                if (isUpgrade) {
                    batteryUpgradeProgram.upgrade(this, this);
                } else {
                    onUpgrade(batteryUpgradeMessage.getAddress(), BatteryUpgradeStatus.FAILED,
                            "升级包不匹配：" + batteryUpgradeMessage.getManufacturer(),
                            0, 0);
                }
            } else {
                onUpgrade(batteryUpgradeMessage.getAddress(), BatteryUpgradeStatus.FAILED,
                        "升级程序不匹配：" + batteryUpgradeMessage.getManufacturer(),
                        0, 0);
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
                case BatteryUpgradeStatus.WAITTING:
                    upgradeCallBack.onUpgradeBefore(mapAddress);
                    break;
                case BatteryUpgradeStatus.BOOT_LOADER_MODE:
                case BatteryUpgradeStatus.INIT_FIRMWARE_DATA:
                    break;
                case BatteryUpgradeStatus.WRITE_DATA:
                    upgradeCallBack.onUpgrade(mapAddress, currentPregress, totalPregress);
                    break;
                case BatteryUpgradeStatus.ACTION_BMS:
                    break;
                case BatteryUpgradeStatus.BATTERY_INFO:
                case BatteryUpgradeStatus.FAILED:
                    upgradeCallBack.onError(mapAddress, statusInfo);
                    break;
                case BatteryUpgradeStatus.SUCCESSED:
                    upgradeCallBack.onUpgradeAfter(mapAddress);
                    remove(mapAddress);
                    break;
            }
        }
    }
}
