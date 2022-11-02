package com.NewElectric.app11.hardwarecomm.agreement.send.pdu;


import java.io.File;
import java.util.concurrent.TimeUnit;

import com.NewElectric.app11.hardwarecomm.androidHard.CanDataFormat;
import com.NewElectric.app11.units.Units;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class PduSend {

    //最大舱门数
    public static int MAX_CABINET_COUNT = 9;
    //地址偏移
    public static int ADDRESS_DRIFT = 20;

    /**
     * pdu操作相关
     */

    //关闭整体充电器的加热和充电
    public static void closeAllPdu() {
        System.out.println("CAN - 充电机 - 状态：    关闭所有充电器");
        Observable.intervalRange(0, MAX_CABINET_COUNT, 0, 200, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                int index = Integer.parseInt(aLong + "");
                closeChargingSwitch(index + 1, 64800, 60000);
                closeHeatingSwitch(index + 1, 64800, 60000);
            }
        });
    }

    //下发生命帧
    public static CanDataFormat sendLive(int count) {
        String address = "98051500";
        byte[] data = new byte[]{(byte) count};
        return new CanDataFormat(address, data);
    }

    //打开充电继电器 第一位：55-打开 AA-关闭  第二位：00-充电 01-加热
    public static CanDataFormat openChargingSwitch(int door, int maxVal, int barVal) {
        int count = door + ADDRESS_DRIFT;
        String count_str = Integer.toHexString(count);
        String address = "9800" + count_str + "00";
        //最高电压
        int tarTarDianya = maxVal / 100;
        int topTarDianya = tarTarDianya / 256;
        int endTarDianya = tarTarDianya % 256;
        //目标电池电量下发
        int BarBarDianya = barVal / 100;
        int topBarDianya = BarBarDianya / 256;
        int endBarDianya = BarBarDianya % 256;
        byte[] data = new byte[]{(byte) 0x55, 0x00, (byte) endTarDianya, (byte) topTarDianya, (byte) endBarDianya, (byte) topBarDianya, 0x00, 0x00}; //02ac  648  64.8V电压
        System.out.println("CAN - 充电机 - 下发打开充电继电器：      第" + (door) + "个模块" + "   address - " + address + "   data - " + Units.ByteArrToHex(data));
        return new CanDataFormat(address, data);
    }

    //关闭充电继电器 第一位：55-打开 AA-关闭  第二位：00-充电 01-加热
    public static CanDataFormat closeChargingSwitch(int door, int maxVal, int barVal) {
        int count = door + ADDRESS_DRIFT;
        String count_str = Integer.toHexString(count);
        String address = "9800" + count_str + "00";
        //最高电压
        int tarTarDianya = maxVal / 100;
        int topTarDianya = tarTarDianya / 256;
        int endTarDianya = tarTarDianya % 256;
        //目标电池电量下发
        int BarBarDianya = barVal / 100;
        int topBarDianya = BarBarDianya / 256;
        int endBarDianya = BarBarDianya % 256;
        byte[] data = new byte[]{(byte) 0xAA, 0x00, (byte) endTarDianya, (byte) topTarDianya, (byte) endBarDianya, (byte) topBarDianya, 0x00, 0x00}; //02ac  648  64.8V电压
        System.out.println("CAN - 充电机 - 下发关闭充电继电器：      第" + (door) + "个模块" + "   address - " + address + "   data - " + Units.ByteArrToHex(data));
        return new CanDataFormat(address, data);
    }

    //打开加热电器 第一位：55-打开 AA-关闭  第二位：00-充电 01-加热
    public static CanDataFormat openHeatingSwitch(int door, int maxVal, int barVal) {
        int count = door + ADDRESS_DRIFT;
        String count_str = Integer.toHexString(count);
        String address = "9800" + count_str + "00";
        //最高电压
        int tarTarDianya = maxVal / 100;
        int topTarDianya = tarTarDianya / 256;
        int endTarDianya = tarTarDianya % 256;
        //目标电池电量下发
        int BarBarDianya = barVal / 100;
        int topBarDianya = BarBarDianya / 256;
        int endBarDianya = BarBarDianya % 256;
        byte[] data = new byte[]{(byte) 0x55, 0x01, (byte) endTarDianya, (byte) topTarDianya, (byte) endBarDianya, (byte) topBarDianya, 0x00, 0x00}; //02ac  648  64.8V电压
        System.out.println("CAN - 充电机 - 下发打开加热继电器：      第" + (door) + "个模块" + "   address - " + address + "   data - " + Units.ByteArrToHex(data));
        return new CanDataFormat(address, data);
    }

    //关闭充电继电器 第一位：55-打开 AA-关闭
    public static CanDataFormat closeHeatingSwitch(int door, int maxVal, int barVal) {
        int count = door + ADDRESS_DRIFT;
        String count_str = Integer.toHexString(count);
        String address = "9800" + count_str + "00";
        //最高电压
        int tarTarDianya = maxVal / 100;
        int topTarDianya = tarTarDianya / 256;
        int endTarDianya = tarTarDianya % 256;
        //目标电池电量下发
        int BarBarDianya = barVal / 100;
        int topBarDianya = BarBarDianya / 256;
        int endBarDianya = BarBarDianya % 256;
        byte[] data = new byte[]{(byte) 0xAA, 0x01, (byte) endTarDianya, (byte) topTarDianya, (byte) endBarDianya, (byte) topBarDianya, 0x00, 0x00}; //02ac  648  64.8V电压
        System.out.println("CAN - 充电机 - 下发关闭加热继电器：      第" + (door) + "个模块" + "   address - " + address + "   data - " + Units.ByteArrToHex(data));
        return new CanDataFormat(address, data);
    }

    //设置电压电流
    public static CanDataFormat setParameter(int door, int val, int ele) {
        int count = door + ADDRESS_DRIFT;
        String count_str = Integer.toHexString(count);
        String address = "9801" + count_str + "00";
        //设置电压
        int pVal = val / 100;
        int topVal = pVal / 256;
        int endVal = pVal % 256;
        //设置电流
        int pEle = ele;
        int topEle = pEle / 256;
        int endEle = pEle % 256;
        byte[] data = new byte[]{(byte) endVal, (byte) topVal, (byte) endEle, (byte) topEle, (byte) 0x01, 0x00, 0x00, 0x00}; //02ac  648  64.8V电压
        return new CanDataFormat(address, data);
    }


    //下面两条指令只在改装柜适用 改装电柜 多一个加热充电器
    //关闭所有加热继电器
    public static CanDataFormat closeHeatingSwitch_1_to_10( ) {
        String address = "98001e00";
        byte[] data = new byte[]{(byte) 0xAA, 0x01, (byte) 0x90, (byte) 0x01, (byte) 0x90, (byte) 0x01, (byte) 0x00, (byte) 0x00}; //019a  410  41V电压
        return new CanDataFormat(address, data);
    }
    //打开需要加热的加热继电器
    public static CanDataFormat openHeatingSwitch_1_to_10(int heatingCount) {
        int headingCount_h = heatingCount / 256;
        int headingCount_l = heatingCount % 256;
        String address = "98001e00";
        byte[] data = new byte[]{(byte) 0x55, 0x01, (byte) 0xf4, (byte) 0x01, (byte) 0xf4, (byte) 0x01, (byte) headingCount_l, (byte) headingCount_h};
        return new CanDataFormat(address, data);
    }
    //设置充电器的电压电流
    //设置电压电流
    public static CanDataFormat setParameter_1_to_10(int val, int ele) {
        String address = "98011e00";
        //设置电压
        int pVal = val / 100;
        int topVal = pVal / 256;
        int endVal = pVal % 256;
        //设置电流
        int pEle = ele;
        int topEle = pEle / 256;
        int endEle = pEle % 256;
        byte[] data = new byte[]{(byte) endVal, (byte) topVal, (byte) endEle, (byte) topEle, (byte) 0x01, 0x00, 0x00, 0x00}; //02ac  648  64.8V电压
        return new CanDataFormat(address, data);
    }


    /**
     * pdu升级相关
     */

    //PDU升级指令
    public static CanDataFormat updatePduStart() {
        byte data[] = new byte[]{(byte) 0x01, (byte) 0xf0, (byte) 0x00, (byte) 0x30, (byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        String address = "880daf3f";
        CanDataFormat canDataFormat = new CanDataFormat(address, data);
        return canDataFormat;
    }

    //pdu升级 - 发送第N包数据的头帧
    public static CanDataFormat updatePduItemHand(byte[] itemData) {

        int packageCount = itemData.length;
        int p_a = packageCount / 256;
        int p_b = packageCount % 256;

        int c_a = packageCount / 7;
        int c_b = packageCount % 7;
        int c_c = 0;
        if (c_b == 0) {
            c_c = c_a;
        } else {
            c_c = c_a + 1;
        }
        int c_d = c_c / 256;
        int c_e = c_c % 256;

        byte data[] = new byte[]{(byte) 0x10, (byte) p_b, (byte) p_a, (byte) 0x00, (byte) 0x00, (byte) c_e, (byte) c_d, (byte) 0xff};
        String address = "881daf3f";

        CanDataFormat canDataFormat = new CanDataFormat(address, data);
        return canDataFormat;
    }

    //pdu升级 - 发送第N包数据的内容帧
    public static CanDataFormat updatePduItemBody(int i, int count, byte[] tempPduData) {

        int byteCount = tempPduData.length;

        int d_1 = 0;
        if (i < 256) {
            d_1 = i + 1;
        } else {
            d_1 = i - 255;
        }

        int d_2 = 0;
        if (7 * i < byteCount) {
            d_2 = tempPduData[7 * i];
        }

        int d_3 = 0;
        if (7 * i + 1 < byteCount) {
            d_3 = tempPduData[7 * i + 1];
        }

        int d_4 = 0;
        if (7 * i + 2 < byteCount) {
            d_4 = tempPduData[7 * i + 2];
        }

        int d_5 = 0;
        if (7 * i + 3 < byteCount) {
            d_5 = tempPduData[7 * i + 3];
        }

        int d_6 = 0;
        if (7 * i + 4 < byteCount) {
            d_6 = tempPduData[7 * i + 4];
        }

        int d_7 = 0;
        if (7 * i + 5 < byteCount) {
            d_7 = tempPduData[7 * i + 5];
        }

        int d_8 = 0;
        if (7 * i + 6 < byteCount) {
            d_8 = tempPduData[7 * i + 6];
        }
        byte data[] = null;
        if (i == count - 1) {
            int y = byteCount % 7;
            if (y == 0) {
                data = new byte[]{(byte) d_1, (byte) d_2, (byte) d_3, (byte) d_4, (byte) d_5, (byte) d_6, (byte) d_7, (byte) d_8};
            } else {
                data = new byte[y + 1];
                data[0] = (byte) d_1;
                for (int j = 0; j < y; j++) {
                    data[j + 1] = tempPduData[byteCount - y + j];
                }
            }
        } else {
            data = new byte[]{(byte) d_1, (byte) d_2, (byte) d_3, (byte) d_4, (byte) d_5, (byte) d_6, (byte) d_7, (byte) d_8};
        }

        String address = "882daf3f";
        CanDataFormat canDataFormat = new CanDataFormat(address, data);
        return canDataFormat;
    }


    //pdu升级 - 发送第N包数据的尾帧
    public static CanDataFormat updatePduItemFoot(byte[] itemData) {

        int packageCount_t = itemData.length;
        int p_a = packageCount_t / 256;
        int p_b = packageCount_t % 256;

        String crc_str = Units.getCRC(itemData);
        while (true) {
            if (crc_str.length() < 4) {
                crc_str = "0" + crc_str;
            } else {
                break;
            }
        }
        int crc_h = Integer.parseInt(crc_str.substring(0, 2), 16);
        int crc_l = Integer.parseInt(crc_str.substring(2, 4), 16);
        byte data[] = new byte[]{(byte) 0x13, (byte) p_b, (byte) p_a, (byte) 0x00, (byte) 0x00, (byte) crc_l, (byte) crc_h, (byte) 0x00};
        String address = "881daf3f";

        CanDataFormat canDataFormat = new CanDataFormat(address, data);
        return canDataFormat;
    }

    //pdu升级 - 整体结束
    public static CanDataFormat updataPduEnd(File pduFile) {
        long fileSize = pduFile.length();
        long fileSize_h = fileSize / 256;
        long fileSize_l = fileSize % 256;

        byte[] fileByte = Units.getBytes(pduFile);
        String crc_str = Units.getCRC(fileByte);
        while (true) {
            if (crc_str.length() < 4) {
                crc_str = "0" + crc_str;
            } else {
                break;
            }
        }
        int crc_h = Integer.parseInt(crc_str.substring(0, 2), 16);
        int crc_l = Integer.parseInt(crc_str.substring(2, 4), 16);

        byte data[] = new byte[]{(byte) fileSize_l, (byte) fileSize_h, (byte) 0x00, (byte) 0x00, (byte) crc_l, (byte) crc_h, (byte) 0xff, (byte) 0xff};
        String address = "884daf3f";
        CanDataFormat canDataFormat = new CanDataFormat(address, data);
        return canDataFormat;
    }

}
