package com.NewElectric.app11.hardwarecomm.agreement.receive.battery;

import com.NewElectric.app11.units.Units;

public class BatteryReceiveEncap {

    private static String[] logTypes = {"按键开机", "接入充电器开机", "关机", "清空历史数据", "SOC=100", "SOC=0", "过压保护", "过压保护恢复", "欠压保护", "欠压保护恢复",
            "放电短路保护", "放电短路保护恢复", "充电过流保护", "充电过流保护恢复", "放电过流保护1", "放电过流保护1恢复", "放电过流保护2", "放电过流保护2恢复", "充电过温保护", "充电过温保护恢复",
            "放电过温保护", "放电过温保护恢复", "充电低温保护", "充电低温保护恢复", "放电低温保护", "放电低温保护恢复", "充电异常高压保护", "功率温度过温保护", "功率温度过温保护恢复", "环境温度过温保护",
            "环境温度过温保护恢复", "环境温度低温保护", "环境温度低温保护恢复", "加热膜异常", "RTC读取时间异常", "修改EEPROM参数", "修改MCU参数", "收到关机指令0x05，进入关机状态", "收到重启指令0x41，重启系统", "收到指令0x42，以开路电压重新标定SOC",
            "校准电压", "校准零点电流值", "校准实时电流值", "校准温度", "校准RTC", "上电读出配置数据异常，恢复默认值", "收到恢复默认值指令0xA0，恢复系统默认配置值", "程序升级"};

    private static String[] barType = {"充电", "放电", "静置", "无"};

    public static BatteryHistoryLogsBean batteryHistory(byte[] mData) {

        BatteryHistoryLogsBean batteryHistoryLogsBean = new BatteryHistoryLogsBean();

        int[] data = new int[mData.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = mData[i] & 0xff;
        }

        int year = data[4];
        int mouth = data[5];
        int day = data[6];
        int hour = data[7];
        int min = data[8];
        int sec = data[9];
        if (year == 255 && mouth == 255 && day == 255 && hour == 255 && min == 255 && sec == 255) {
            year = mouth = day = hour = min = sec = 0;
        }
        batteryHistoryLogsBean.setTime(year + "年" + mouth + "月" + day + "日   " + hour + ":" + min + ":" + sec);

        int totalCount = 0;
        if (data[10] == 255 || data[11] == 255 || data[12] == 255 || data[13] == 255) {
            totalCount = 0;
        } else {
            totalCount = data[10] + data[11] * 256 + data[12] * 256 * 256 + data[13] * 256 * 256 * 256;
        }
        batteryHistoryLogsBean.setTotalCount(totalCount);

        int logTypeInt = data[14];
        String longTypeStr = "";
        if (logTypeInt != 255) {
            longTypeStr = logTypes[logTypeInt - 1];
        }else{
            longTypeStr = "无";
        }
        batteryHistoryLogsBean.setLogType(longTypeStr);

        int hardVersion = data[16];
        int softVersion = data[15];
        int funVersion = data[17];
        if (hardVersion == 255) {
            hardVersion = -1;
        }
        if (softVersion == 255) {
            softVersion = -1;
        }
        if (funVersion == 255) {
            funVersion = -1;
        }
        batteryHistoryLogsBean.setBatteryVersion("软件本版 - " + softVersion + " - 硬件版本 - " + hardVersion + " - 功能版本 - " + funVersion);

        int soc = data[22];
        if (soc == 255) {
            soc = -1;
        }
        batteryHistoryLogsBean.setSoc(soc);

        int barTypeInt = data[23];
        String barTypeStr = "";
        if (barTypeInt == 0x43) {
            barTypeStr = barType[0];
        } else if (barTypeInt == 0x44) {
            barTypeStr = barType[1];
        } else if (barTypeInt == 0x4E) {
            barTypeStr = barType[2];
        } else {
            barTypeStr = barType[3];
        }
        batteryHistoryLogsBean.setBatteryType(barTypeStr);

        String waringStateStrLow = Units.int2Binary(data[44]);
        String waringStateStrHight = Units.int2Binary(data[45]);
        String waringStateStr = waringStateStrHight + waringStateStrLow;
        String waringState = "";
        for (int i = 0; i < waringStateStr.length(); i++) {
            String warningItem = waringStateStr.substring(i, i + 1);
            if (warningItem.equals("1") && i == 5) {
                waringState = waringState + "SOC低/";
            }
            if (warningItem.equals("1") && i == 6) {
                waringState = waringState + "MOS温度高/";
            }
            if (warningItem.equals("1") && i == 7) {
                waringState = waringState + "短路/";
            }
            if (warningItem.equals("1") && i == 8) {
                waringState = waringState + "放电过流/";
            }
            if (warningItem.equals("1") && i == 9) {
                waringState = waringState + "充电过流/";
            }
            if (warningItem.equals("1") && i == 10) {
                waringState = waringState + "放电低温/";
            }
            if (warningItem.equals("1") && i == 11) {
                waringState = waringState + "放电高温/";
            }
            if (warningItem.equals("1") && i == 12) {
                waringState = waringState + "充电低温/";
            }
            if (warningItem.equals("1") && i == 13) {
                waringState = waringState + "充电高温/";
            }
            if (warningItem.equals("1") && i == 14) {
                waringState = waringState + "单体过低/";
            }
            if (warningItem.equals("1") && i == 15) {
                waringState = waringState + "单体过高/";
            }
        }
        if (waringState.equals("")) {
            waringState = "正常";
        }
        if (data[44] == 255 && data[45] == 255) {
            waringState = "无";
        }
        batteryHistoryLogsBean.setWarningInfo(waringState);

        int batteryLoops = 0;
        if (data[58] == 255 && data[59] == 255) {
            batteryLoops = 0;
        }else{
            batteryLoops = data[58] + data[59] * 256;
        }
        batteryHistoryLogsBean.setBatteryLoops(batteryLoops);

        return batteryHistoryLogsBean;
    }

}
