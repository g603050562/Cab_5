package com.NewElectric.app11.service.logic.logic_update.controlplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.NewElectric.app11.hardwarecomm.agreement.send.controlPlate.ControlPlateSend;
import com.NewElectric.app11.service.logic.logic_update.OnRwAction;

public class ControlPanel_485_12_UpgradeProgram extends ControlPanelUpgradeProgram {

    private int mapAddress = 0;
    private String upgradeFile = "";
    private int btype = -1;
    private String fileName = "app.bin";
    private long fileSize = 0;
    private ArrayList<byte[]> dataList = new ArrayList<>();

    public ControlPanel_485_12_UpgradeProgram(byte mapAddress, String upgradeFile) {
        super(mapAddress, upgradeFile);
        this.mapAddress = mapAddress;
        this.upgradeFile = upgradeFile;
    }

    @Override
    protected void onRun(OnRwAction onRwAction) {

        //升级初始化
        onUpgrade(ControlPanelUpgradeStatus.WAITTING, "等待下载文件初始化", 0, 0);
        if (onRwAction == null) {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "准备阶段出错 升级失败！", 0, 0);
            return;
        }
        if (upgradeFile == null) {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "升级文件路径为空 升级失败！", 0, 0);
            return;
        }
        File file = new File(upgradeFile);
        fileSize = file.length();
        if (file == null) {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "升级文件初始化失败 升级失败！", 0, 0);
            return;
        }
        if (!file.exists()) {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "文件不存在 升级失败！", 0, 0);
            return;
        }
        try {
            FileInputStream is = new FileInputStream(file);
            byte buffer[] = new byte[1024];
            int length = 0;
            while ((length = is.read(buffer)) > 0) {
                byte hexData[] = new byte[1024];
                for (int i = 0; i < buffer.length; i++) {
                    hexData[i] = buffer[i];
                }
                dataList.add(hexData);
                buffer = new byte[1024];
            }
        } catch (FileNotFoundException e) {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "格式化升级文件失败，升级失败！", 0, 0);
            return;
        } catch (IOException e) {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "格式化升级文件失败，升级失败！", 0, 0);
            return;
        }
        //读取电池信息 确保控制板底层和上层信息是否正确
        onUpgrade(ControlPanelUpgradeStatus.BATTERY_INFO, "读取控制板信息!", 0, 0);
        onRwAction.write(ControlPlateSend._485ReadPlateInfo(mapAddress));
        sleep(1000);
        byte[] result_1 = onRwAction.read();
        if (result_1 != null && result_1.length > 60) {
            btype = result_1[56];
            System.out.println("UpDataControlPanel：   类型 - " + btype);
        } else {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "读取控制板信息失败，升级失败!", 0, 0);
            return;
        }
        sleep(500);
        //下发升级命令一
        onUpgrade(ControlPanelUpgradeStatus.MODE_1, "下发升级命令一", 0, 0);
        onRwAction.write(ControlPlateSend._485UpdatePlateStage(mapAddress, 1));
        sleep(500);
        byte[] result_2 = onRwAction.read();
        if (result_2 != null && result_2.length > 0) {
        } else {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "读取控制板信息失败，升级失败!", 0, 0);
            return;
        }
        sleep(500);
        //下发升级命令二
        onUpgrade(ControlPanelUpgradeStatus.MODE_2, "下发升级命令二", 0, 0);
        onRwAction.write(ControlPlateSend._485UpdatePlateStage(mapAddress, 2));
        if (btype == 102) {
            sleep(1000);
        } else if (btype == 103) {
            sleep(2500);
        }else if (btype == 85) {
            sleep(2000);
        } else {
            sleep(1000);
        }
        //下发升级命令三
        byte[] order_1 = null;
        if (btype == 65) {
            order_1 = new byte[]{49};
        } else if (btype == 102) {
            order_1 = ControlPlateSend._485UpdatePlateStage(mapAddress, 3);
        } else if (btype == 103) {
            order_1 = ControlPlateSend._485UpdatePlateStage(mapAddress, 3);
        } else if (btype == 0) {
            order_1 = ControlPlateSend._485UpdatePlateStage(mapAddress, 3);
        } else if (btype == 85) {
            order_1 = ControlPlateSend._485UpdatePlateStage(mapAddress, 3);
        } else {
            order_1 = new byte[]{49};
        }
        onUpgrade(ControlPanelUpgradeStatus.MODE_3, "下发升级命令四", 0, 0);
        onRwAction.write(order_1);
        sleep(2500);
        byte[] result_3 = onRwAction.read();
        if (result_3 != null && result_3.length > 0) {
        } else {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "读取控制板信息失败，升级失败!", 0, 0);
            return;
        }
        //下发数据升级
        byte[] order_2 = null;
        order_2 = ControlPlateSend._485UpdatePlateSendData(fileName, fileSize, dataList.get(0), 0);
        onUpgrade(ControlPanelUpgradeStatus.WRITE_DATA, "发送" + 0 + "条成功", 0, dataList.size());
        onRwAction.write(order_2);
        sleep(1000);

        byte[] result_4 = onRwAction.read();
        if (result_4 != null && result_4.length > 0) {
        } else {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "未接到返回值，升级失败!", 0, 0);
            return;
        }
        sleep(1000);

        byte[] result_5 = onRwAction.read();
        if (result_5 != null && result_5.length > 0) {
        } else {
            onUpgrade(ControlPanelUpgradeStatus.FAILED, "未接到返回值，升级失败!", 0, 0);
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            byte[] order_3 = null;
            order_3 = ControlPlateSend._485UpdatePlateSendData(fileName, fileSize, dataList.get(i), i + 1);
            onRwAction.write(order_3);
            onUpgrade(ControlPanelUpgradeStatus.WRITE_DATA, "发送" + 1+i + "条成功", 1+i, dataList.size());
            sleep(1500);
            byte[] result_6 = onRwAction.read();
            if (result_6 != null && result_6.length > 0) {
            } else {
                onUpgrade(ControlPanelUpgradeStatus.FAILED, "未接到返回值，升级失败!", 0, 0);
                return;
            }
        }
        sleep(10000);
        //升级成功
        onUpgrade(ControlPanelUpgradeStatus.SUCCESSED, "升级成功！", 0, 0);
    }

}