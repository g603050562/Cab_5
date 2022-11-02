package com.NewElectric.app11.service.logic.logic_update.battery.BoQiang;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.NewElectric.app11.service.logic.logic_update.OnRwAction;
import com.NewElectric.app11.service.logic.logic_update.battery.BatteryUpgradeProgram;
import com.NewElectric.app11.service.logic.logic_update.battery.BatteryUpgradeStatus;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-08-30
 * Description:
 */
public class BoQiangBatteryUpgradeProgram extends BatteryUpgradeProgram {

    private final byte[] _485 = {0x00, 0x05, 0x00, 0x0B, 0x00, 0x01, 0x00, 0x00};

    //电池ID码
    static final byte[] BATTERY_ID_CODE = {0x3A, 0x16, (byte) 0x7E, 0x01, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    //BMS版本信息
    static final byte[] VERSION = {0x3A, 0x16, 0x7F, 0x01, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    private final byte[] bootLoaderMode = new byte[]{0x3A, 0x16, (byte) 0xF0, 0x0D, (byte) 0xF1, 0x4A, 0x4D, 0x4B, 0x2D, 0x42, 0x4D, 0x53, 0x2D, 0x42, 0x4C, 0x30, 0x30, 0x00, 0x00, 0x0D, 0x0A};//长度信息是子命令和数据内容的长度

    //4.6. 发送新固件信息(0xF6)
    private final byte[] firmwareInfo = new byte[]{0x3A, 0x16, (byte) 0xF0, 0x11, (byte) 0xF6, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
            , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    //发送新固件数据(0xF7)
    private final byte[] firmwareData = new byte[]{0x3A, 0x16, (byte) 0xF0, (byte) 0x83, (byte) 0xF7, 0x00, 0x00,//1~2当前固件数据帧号。范围:0~(总帧数-1)。
            //固件数据每帧最长数据为 128 字节。
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    private final byte[] activationBMS = new byte[]{0x3A, 0x16, (byte) 0xF0, 0x02, (byte) 0xF4, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    private final byte[] BMS_Info = new byte[]{0x3A, 0x16, (byte) 0xF0, 0x02, (byte) 0xF2, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    public BoQiangBatteryUpgradeProgram(byte mapAddress, String upgradeFile) {
        super(mapAddress, upgradeFile);
        _485[0] = mapAddress;
    }

    @Override
    protected void onRun(OnRwAction onRwAction) {
        if (onRwAction == null) {
            onUpgrade(BatteryUpgradeStatus.FAILED, "准备阶段出错 升级失败！", 0, 0);
            return;
        }

        if (upgradeFile == null) {
            onUpgrade(BatteryUpgradeStatus.FAILED, "升级文件路径为空 升级失败！", 0, 0);
            return;
        }

        File file = new File(upgradeFile);
        if (file == null) {
            onUpgrade(BatteryUpgradeStatus.FAILED, "升级文件初始化失败 升级失败！", 0, 0);
            return;
        }
        if (!file.exists()) {
            onUpgrade(BatteryUpgradeStatus.FAILED, "文件不存在 升级失败！", 0, 0);
            return;
        }

        // TODO: 2019-09-08  转485模式
        onUpgrade(BatteryUpgradeStatus.WAITTING, "开始激活485转发!", 0, 0);
        sleep(10 * 1000);
        _485[0] = mapAddress;
        byte[] crcData = crc16(_485, 0, 6);
        if (crcData != null && crcData.length == 2) {
            _485[_485.length - 2] = crcData[0];
            _485[_485.length - 1] = crcData[1];
        }
        onRwAction.write(_485);
        sleep(600);
        byte[] result = null;
        result = onRwAction.read();
        if (result != null && result.length > 0) {

        } else {
            onUpgrade(BatteryUpgradeStatus.FAILED, "激活485转发失败!", 0, 0);
            return;
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(upgradeFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fileInputStream == null) {
            return;
        }
        int len = 0;
        try {
            len = fileInputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (len == 0) {
            return;
        }

        byte[] binData = new byte[len];
        try {
            fileInputStream.read(binData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int sum;
        sum = calculateSum(BATTERY_ID_CODE, 1, 4);
        BATTERY_ID_CODE[5] = (byte) (sum & 0xFF);
        BATTERY_ID_CODE[6] = (byte) (sum >> 8 & 0xFF);
        onRwAction.write(BATTERY_ID_CODE);
        sleep(500);
        result = onRwAction.read();
        if (result != null && result.length >= 20) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 4, lengh = result.length - 4; i < lengh; stringBuilder.append((char) (result[i] & 0xFF)), i++)
                ;
            final String resultIdCode = stringBuilder.toString();
            if (!resultIdCode.startsWith(idCode)) {
                onUpgrade(BatteryUpgradeStatus.BATTERY_INFO, "电池类型和升级包不匹配!", 0, 0);
                return;
            }
        } else {
            onUpgrade(BatteryUpgradeStatus.BATTERY_INFO, "电池ID码错误!", 0, 0);
            return;
        }

//        if (TextUtils.isEmpty(bmsHardwareVersion)) {
//            onUpgrade(BatteryUpgradeStatus.BATTERY_INFO, "BMS硬件版本为空!", 0, 0);
//            return;
//        } else {
//            sum = calculateSum(VERSION, 1, 4);
//            VERSION[5] = (byte) (sum & 0xFF);
//            VERSION[6] = (byte) (sum >> 8 & 0xFF);
//            onRwAction.write(VERSION);
//            sleep(500);
//            result = onRwAction.read();
//            if (result != null && result.length >= 6) {
//                final String hv = Byte.toString((byte) (result[6] & 0xFF));
//                if (!hv.equals(bmsHardwareVersion)) {
//                    onUpgrade(BatteryUpgradeStatus.BATTERY_INFO, "BMS硬件版本不匹配!", 0, 0);
//                    return;
//                }
//            } else {
//                onUpgrade(BatteryUpgradeStatus.BATTERY_INFO, "BMS硬件版本错误!", 0, 0);
//                return;
//            }
//        }

        // TODO: 2019-09-08 比较CRC校验码
        if (TextUtils.isEmpty(crcValue)) {
            onUpgrade(BatteryUpgradeStatus.BATTERY_INFO, "文件包CRC为空!", 0, 0);
            return;
        }
        final int binDataCrc = crc32(binData);
        System.out.println("crcValue:"+crcValue);
        if (hexToInt(crcValue) != binDataCrc) {
            onUpgrade(BatteryUpgradeStatus.BATTERY_INFO, "CRC不匹配!", 0, 0);
            return;
        }

        sleep(2000);

        // TODO: 2019-09-08 进入BootLoader模式
        sum = calculateSum(bootLoaderMode, 1, 16);
        bootLoaderMode[17] = (byte) (sum & 0xFF);
        bootLoaderMode[18] = (byte) (sum >> 8 & 0xFF);
        final long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 30 * 1000) {
            onRwAction.write(bootLoaderMode);
            sleep(500);
            result = onRwAction.read();
            if (result != null && result.length > 6 && result[4] == (byte) 0xF1 && result[5] == 0x00) {
                break;
            }
        }
        if (result != null && result.length > 6 && result[4] == (byte) 0xF1 && result[5] == 0x00) {
            onUpgrade(BatteryUpgradeStatus.BOOT_LOADER_MODE, "进入BootLoader模式成功!", 0, 0);
        } else {
            onUpgrade(BatteryUpgradeStatus.FAILED, "进入BootLoader模式失败!", 0, 0);
            return;
        }

        // TODO: 2019-09-08 总字节数,低字节在前，高字节在后。
        firmwareInfo[7] = (byte) (len & 0xFF);
        firmwareInfo[8] = (byte) (len >> 8 & 0xFF);
        firmwareInfo[9] = (byte) (len >> 16 & 0xFF);
        // TODO: 2019-09-08 总帧数,低字节在前，高字节在后。
        int totalFrameSize = len / 128;
        if (len % 128 > 0) {
            totalFrameSize += 1;
        }
        firmwareInfo[10] = (byte) (totalFrameSize & 0xFF);
        firmwareInfo[11] = (byte) (totalFrameSize >> 8 & 0xFF);
        // TODO:  2019-09-08 CRC32 校验码,低字节在前，高字节在后。
        firmwareInfo[12] = (byte) (binDataCrc & 0xFF);
        firmwareInfo[13] = (byte) (binDataCrc >> 8 & 0xFF);
        firmwareInfo[14] = (byte) (binDataCrc >> 16 & 0xFF);
        firmwareInfo[15] = (byte) (binDataCrc >> 24 & 0xFF);

        sum = calculateSum(firmwareInfo, 1, 20);
        firmwareInfo[21] = (byte) (sum & 0xFF);
        firmwareInfo[22] = (byte) (sum >> 8 & 0xFF);

        onRwAction.write(firmwareInfo);
        sleep(1000);
        result = onRwAction.read();

        if (result != null && result.length > 6 && result[4] == (byte) 0xF6 && result[5] == 0x00) {
            onUpgrade(BatteryUpgradeStatus.INIT_FIRMWARE_DATA, "新固件信息发送成功!", 0, totalFrameSize);
        } else {
            onUpgrade(BatteryUpgradeStatus.FAILED, "新固件信息发送失败!", 0, totalFrameSize);
            return;
        }


        // TODO: 2019-09-02 开始循环写入数据帧
        int loopCount = 1;
        short sn = 0;
        int offset = 0;
        for (; loopCount < totalFrameSize; loopCount++, sn++, offset += 127) {
            // TODO: 2019-09-02 帧号
            firmwareData[5] = (byte) (sn & 0xFF);
            firmwareData[6] = (byte) (sn >> 8 & 0xFF);
            // TODO: 2019-09-02 数据帧
            System.arraycopy(binData, offset, firmwareData, 7, 128);
            sum = calculateSum(firmwareData, 1, 134);
            firmwareData[135] = (byte) (sum & 0xFF);
            firmwareData[136] = (byte) (sum >> 8 & 0xFF);
            onRwAction.write(firmwareData);
            offset++;
            sleep(1000);
            result = onRwAction.read();

            if (result != null && result.length > 6 && result[4] == (byte) 0xF7 && result[5] == 0x00) {
                onUpgrade(BatteryUpgradeStatus.WRITE_DATA, "发送" + sn + "条成功", sn, totalFrameSize);
            } else {
                onUpgrade(BatteryUpgradeStatus.FAILED, "发送" + sn + "条失败", sn, totalFrameSize);
                return;
            }
        }

        if (loopCount == totalFrameSize) {
            // TODO: 2019-09-02 处理最后一帧数据
            // TODO: 2019-09-02 帧号
            firmwareData[5] = (byte) (sn & 0xFF);
            firmwareData[6] = (byte) (sn >> 8 & 0xFF);

            // TODO: 2019-09-03 最后一帧数据长度
            final int lastLen = 7 + len - offset + 4;
            byte[] lastData = new byte[lastLen];
            System.arraycopy(firmwareData, 0, lastData, 0, 7);
            System.arraycopy(binData, offset, lastData, 7, len - offset);
            // TODO: 2019-09-03 填充长度
            lastData[3] = (byte) ((3 + len - offset) & 0xFF);

            int end = lastLen - 4 - 1;
            sum = calculateSum(lastData, 1, end);
            lastData[++end] = (byte) (sum & 0xFF);
            lastData[++end] = (byte) (sum >> 8 & 0xFF);
            lastData[++end] = 0x0D;
            lastData[++end] = 0x0A;

            onUpgrade(BatteryUpgradeStatus.ACTION_BMS, "开始激活...", totalFrameSize, totalFrameSize);
            onRwAction.write(lastData);
            sleep(5000);// TODO: 2019-09-08 最后一针数据需要等待充分时间才行

            result = onRwAction.read();
            if (result != null && result.length > 6 && result[4] == (byte) 0xF7 && result[5] == 0x00) {
                onUpgrade(BatteryUpgradeStatus.WRITE_DATA, "发送" + sn + "条成功", sn, totalFrameSize);
            } else {
                onUpgrade(BatteryUpgradeStatus.FAILED, "发送" + sn + "条失败", sn, totalFrameSize);
                return;
            }
            onUpgrade(BatteryUpgradeStatus.WRITE_DATA, "发送" + sn + "条成功", totalFrameSize, totalFrameSize);
        }

        // TODO: 2019-09-08 激活指令，如果激活失败，10S后新固件程序自动生效
        sum = calculateSum(activationBMS, 1, 5);
        activationBMS[6] = (byte) (sum & 0xFF);
        activationBMS[7] = (byte) (sum >> 8 & 0xFF);
        onRwAction.write(activationBMS);
        sleep(600);
        result = onRwAction.read();
        if (result != null && result.length > 6 && result[4] == (byte) 0xF4 && result[5] == 0x00) { // TODO: 2019-09-08 激活失败
            sleep(10 * 1000);
            onUpgrade(BatteryUpgradeStatus.SUCCESSED, "激活成功", totalFrameSize, totalFrameSize);
            return;
        }

        // TODO: 2019-09-08 升级成功
        onUpgrade(BatteryUpgradeStatus.SUCCESSED, "激活成功", totalFrameSize, totalFrameSize);

    }
}
