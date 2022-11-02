package com.NewElectric.app11.service.logic.logic_update.battery;

import android.text.TextUtils;
import java.math.BigInteger;

import com.NewElectric.app11.service.logic.logic_update.OnRwAction;
import com.NewElectric.app11.service.logic.logic_update.OnUpgradeProgress;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description:
 */
public abstract class BatteryUpgradeProgram
{
    protected final byte mapAddress;
    protected final String upgradeFile;
    private OnUpgradeProgress onUpgradeProgress;
    private StringBuilder stringBuilder = new StringBuilder();

    protected String idCode;
    protected String bmsHardwareVersion;
    protected String crcValue;

    public BatteryUpgradeProgram(byte mapAddress, String upgradeFile)
    {
        this.mapAddress = mapAddress;
        this.upgradeFile = upgradeFile;
    }

    /**
     * @param idCode             ID码，要求写入前两位
     * @param bmsHardwareVersion 十六进制数据
     */
    public void setIdCodeAndBmsHardwareVersion(String idCode, String bmsHardwareVersion,String crcValue)
    {
        this.idCode = idCode;
        this.bmsHardwareVersion = bmsHardwareVersion;
        this.crcValue = crcValue;
    }


    protected final void onUpgrade(byte statusFlag, String statusInfo, long currentPregress, long totalPregress)
    {
        if (onUpgradeProgress != null)
        {
            onUpgradeProgress.onUpgrade(mapAddress, statusFlag, statusInfo, currentPregress, totalPregress);
        }
    }

    public final void upgrade(OnRwAction onRwAction)
    {
        upgrade(onRwAction, null);
    }

    public final void upgrade(OnRwAction onRwAction, OnUpgradeProgress onUpgradeProgress)
    {
        this.onUpgradeProgress = onUpgradeProgress;
        onRun(onRwAction);
    }

    protected abstract void onRun(OnRwAction onRwAction);

    /**
     * 计算校验和
     *
     * @param data
     * @param start
     * @param end
     *
     * @return
     */
    protected final int calculateSum(byte[] data, int start, int end)
    {
        int sum = 0;
        if (data != null && start >= 0 && end >= start && end < data.length)
        {
            for (int s = start; s <= end; sum += data[s] & 0xFF, s++) ;
        }
        return sum;
    }

    protected final void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * crc校验码计算
     *
     * @param data
     *
     * @return
     */
    //    protected final int crc32(byte[] data)
    //    {
    //        int crcVal = 0xFFFFFFFF;
    //        if (data != null && data.length > 0)
    //        {
    //            for (byte item : data)
    //            {
    //                crcVal ^= item << 24;
    //                for (int k = 0; k < 8; k++)
    //                {
    //                    if ((crcVal & 0x80000000) != 0)
    //                        crcVal = (crcVal << 1) ^ 0x04C11DB7;
    //                    else
    //                        crcVal <<= 1;
    //                }
    //            }
    //        }
    //        return crcVal;
    //    }

    /**
     * crc校验码计算
     *
     * @param data
     *
     * @return
     */
    protected final int crc32(byte[] data)
    {
        int crcVal = 0xFFFFFFFF;
        int k;
        for (byte item : data)
        {
            crcVal ^= item << 24;
            for (k = 0; k < 8; k++, crcVal = ((crcVal & 0x80000000) != 0) ? ((crcVal << 1) ^ 0x04C11DB7) : (crcVal << 1))
                ;
        }
        return crcVal;
    }



    protected final byte[] crc16(byte[] arr_buff, int offset, int len)
    {
        // 预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = offset; i < len; i++)
        {
            // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));
            for (j = 0; j < 8; j++)
            {
                // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 0x0001) > 0)
                {
                    // 如果移出位为 1, CRC寄存器与多项式A001进行异或
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else
                    // 如果移出位为 0,再次右移一位
                    crc = crc >> 1;
            }
        }
        return intToBytes(crc);
    }

    /**
     * 将int转换成byte数组，低位在前，高位在后
     * 改变高低位顺序只需调换数组序号
     */
    private byte[] intToBytes(int value)
    {
        byte[] src = new byte[2];
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    protected String toHexString(byte[] data, boolean isAppendSeparator)
    {
        if (stringBuilder != null)
        {
            stringBuilder.setLength(0);

            final int len = data.length;
            if (data != null && len > 0)
            {
                for (int i = 0; i < len; i++)
                {
                    String hex = Integer.toHexString(data[i] & 0xFF);
                    if (hex.length() == 1)
                    {
                        hex = '0' + hex;
                    }
                    if (isAppendSeparator)
                    {
                        stringBuilder.append("[").append(hex.toUpperCase()).append("]");
                    } else
                    {
                        stringBuilder.append(hex.toUpperCase());
                    }
                }
            }

            return stringBuilder.toString();
        }

        return null;
    }

    protected final int hexToInt(String hexString) throws NumberFormatException
    {
        if (!TextUtils.isEmpty(hexString))
        {
            hexString = hexString.trim();
            if (hexString.startsWith("0x") || hexString.startsWith("0X"))
            {
                hexString = hexString.substring(2);
            }
            if (!TextUtils.isEmpty(hexString))
            {
                return new BigInteger(hexString, 16).intValue();
            }
        }
        return 0;
    }
}
