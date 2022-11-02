package com.NewElectric.app11.hardwarecomm.agreement.send.controlPlate;


import java.util.Arrays;

import com.NewElectric.app11.hardwarecomm.androidHard.CanDataFormat;
import com.NewElectric.app11.units.Units;

/**
 * 控制板 编辑控制帧
 */

public class ControlPlateSend {


    //所有控制板地址都是从5开始计数
    private static int DOOR_INDEX = 4;

    /**
     * 控制板操作相关
     */

    //控制板进入转发模式
    public static CanDataFormat canTo485(int door) {

        int mDoor = door + 4;

        byte[] data = {(byte) mDoor, 0x05, 0x00, 0x0B, 0x00, 0x01};
        String str = Units.getCRC(data);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }
        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] returnData = {(byte) mDoor, 0x05, 0x00, 0x0B, 0x00, 0x01, (byte) Integer.parseInt(str_07, 16), (byte) Integer.parseInt(str_06, 16)};

        String address = "";
        if (mDoor < 16) {
            address = "0000000" + Integer.toHexString(mDoor);
        } else {
            address = "000000" + Integer.toHexString(mDoor);
        }

        return new CanDataFormat(address, returnData);
    }

    //can - 重启控制板
    public static CanDataFormat canPlateReboot(int door) {

        int mDoor = door + DOOR_INDEX;

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) mDoor;
        message_b[1] = (byte) 05;
        message_b[2] = (byte) 00;
        message_b[3] = (byte) 04;
        message_b[4] = (byte) 00;
        message_b[5] = (byte) 01;
        String str = Units.getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) mDoor;
        message_i[1] = (byte) 05;
        message_i[2] = (byte) 0;
        message_i[3] = (byte) 04;
        message_i[4] = (byte) 0;
        message_i[5] = (byte) 01;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        String address = "";
        if (mDoor < 16) {
            address = "0000000" + Integer.toHexString(mDoor);
        } else {
            address = "000000" + Integer.toHexString(mDoor);
        }

        return new CanDataFormat(address, message_i);
    }

    //485 - 伸长推杆
    public static byte[] _485PlateElongation(int door) {   //number:第几位机器

        int mDoor = door + DOOR_INDEX;

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) mDoor;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x09;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x01;

        String str = Units.getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) mDoor;
        message_i[1] = (byte) 0x05;
        message_i[2] = (byte) 0x00;
        message_i[3] = (byte) 0x09;
        message_i[4] = (byte) 0x00;
        message_i[5] = (byte) 0x01;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        return message_i;
    }

    //can - 伸长推杆 door
    public static CanDataFormat canPlateElongation(int door) {

        int mDoor = door + DOOR_INDEX;

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) mDoor;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x09;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x01;

        String str = Units.getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) mDoor;
        message_i[1] = 0x05;
        message_i[2] = 0x00;
        message_i[3] = 0x09;
        message_i[4] = 0x00;
        message_i[5] = 0x01;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        String address = "";
        if (mDoor < 16) {
            address = "0000000" + Integer.toHexString(mDoor);
        } else {
            address = "000000" + Integer.toHexString(mDoor);
        }

        return new CanDataFormat(address, message_i);
    }

    //can - 推杆收回
    public static CanDataFormat canPlateShrink(int door) {   //number:第几位机器

        int mDoor = door + DOOR_INDEX;

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) mDoor;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x09;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x00;

        String str = Units.getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) mDoor;
        message_i[1] = (byte) 0x05;
        message_i[2] = (byte) 0x00;
        message_i[3] = (byte) 0x09;
        message_i[4] = (byte) 0x00;
        message_i[5] = (byte) 0x00;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        String address = "";
        if (mDoor < 16) {
            address = "0000000" + Integer.toHexString(mDoor);
        } else {
            address = "000000" + Integer.toHexString(mDoor);
        }

        return new CanDataFormat(address, message_i);
    }

    //can - 组合动作 推和收
    public static CanDataFormat canPlateElongationAndShrink(int door) {

        int mDoor = door + DOOR_INDEX;

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) mDoor;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x09;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x03;


        String str = Units.getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) mDoor;
        message_i[1] = (byte) 0x05;
        message_i[2] = (byte) 0x00;
        message_i[3] = (byte) 0x09;
        message_i[4] = (byte) 0x00;
        message_i[5] = (byte) 0x03;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        String address = "";
        if (mDoor < 16) {
            address = "0000000" + Integer.toHexString(mDoor);
        } else {
            address = "000000" + Integer.toHexString(mDoor);
        }
        return new CanDataFormat(address, message_i);
    }

    //写入ID
    public static CanDataFormat canWriteUid(int door, String uid) { //number:第几位机器

        int door_number = door + DOOR_INDEX;

        StringBuffer sbu = new StringBuffer();
        char[] chars = uid.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        String[] uids = sbu.toString().split(",");

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) door_number;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x0D;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x01;
        message_b[6] = (byte) Integer.parseInt(uids[0]);
        message_b[7] = (byte) Integer.parseInt(uids[1]);
        message_b[8] = (byte) Integer.parseInt(uids[2]);
        message_b[9] = (byte) Integer.parseInt(uids[3]);
        message_b[10] = (byte) Integer.parseInt(uids[4]);
        message_b[11] = (byte) Integer.parseInt(uids[5]);
        message_b[12] = (byte) Integer.parseInt(uids[6]);
        message_b[13] = (byte) Integer.parseInt(uids[7]);

        String str = Units.getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) door_number;
        message_i[1] = (byte) 0x05;
        message_i[2] = (byte) 0x00;
        message_i[3] = (byte) 0x0D;
        message_i[4] = (byte) 0x00;
        message_i[5] = (byte) 0x01;
        message_i[6] = (byte) Integer.parseInt(uids[0]);
        message_i[7] = (byte) Integer.parseInt(uids[1]);
        message_i[8] = (byte) Integer.parseInt(uids[2]);
        message_i[9] = (byte) Integer.parseInt(uids[3]);
        message_i[10] = (byte) Integer.parseInt(uids[4]);
        message_i[11] = (byte) Integer.parseInt(uids[5]);
        message_i[12] = (byte) Integer.parseInt(uids[6]);
        message_i[13] = (byte) Integer.parseInt(uids[7]);
        message_i[14] = (byte) Integer.parseInt(str_07, 16);
        message_i[15] = (byte) Integer.parseInt(str_06, 16);

        String address = "";
        if (door < 16) {
            address = "0000000" + Integer.toHexString(door_number);
        } else {
            address = "000000" + Integer.toHexString(door_number);
        }

        return new CanDataFormat(address, message_i);
    }


    /**
     * 控制板升级相关
     */

    //485 - 读取电池信息
    public static byte[] _485ReadPlateInfo(int door) {
        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) door;
        message_b[1] = (byte) 0x02;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x00;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x00;
        String str = Units.getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }
        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);
        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) door;
        message_i[1] = (byte) 0x02;
        message_i[2] = (byte) 0x00;
        message_i[3] = (byte) 0x00;
        message_i[4] = (byte) 0x00;
        message_i[5] = (byte) 0x00;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        return message_i;
    }

    //485 - 升级命令一
    public static byte[] _485UpdatePlateStage(int door, int type) { // door - 舱门👌   type：升级步骤
        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) door;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x04;
        message_b[4] = (byte) 0x00;

        if (type == 1) {
            message_b[5] = (byte) 0x00;
        } else if (type == 2) {
            message_b[5] = (byte) 0x01;
        } else if (type == 3) {
            message_b[5] = (byte) 0x02;
        }

        String str = Units.getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) door;
        message_i[1] = (byte) 0x05;
        message_i[2] = (byte) 0x00;
        message_i[3] = (byte) 0x04;
        message_i[4] = (byte) 0x00;

        if (type == 1) {
            message_i[5] = (byte) 0x00;
        } else if (type == 2) {
            message_i[5] = (byte) 0x01;
        } else if (type == 3) {
            message_i[5] = (byte) 0x02;
        }

        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        return message_i;
    }

    //发送升级包数据 name - 文件名   size - 文件大小   data[] - 文件数据   m - 发送类型
    public static byte[] _485UpdatePlateSendData(String name, long size, byte data[], int m) {

        if (m == 0) {
            byte[] return_message = new byte[133];
            byte[] message = new byte[128];
            Arrays.fill(message, (byte) 0x00);
            int name_int_long = name.length();
            for (int i = 0; i < name_int_long; i++) {
                String a = name.substring(i, i + 1);
                byte[] b_a = a.getBytes();
                message[i] = (byte) b_a[0];
            }
            message[name_int_long] = 0;

            String size_str = size + "";
            int size_int_long = size_str.length();
            for (int i = 0; i < size_int_long; i++) {
                String b = size_str.substring(i, i + 1);
                byte[] b_b = b.getBytes();
                message[name_int_long + 1 + i] = (byte) b_b[0];
            }


            String str_0607 = Units.getCRC(message);
            while (true) {
                if (str_0607.length() < 4) {
                    str_0607 = "0" + str_0607;
                } else {
                    break;
                }
            }

            String str_06 = str_0607.substring(0, 2);
            String str_07 = str_0607.substring(2, 4);


            return_message[0] = (byte) 1;
            return_message[1] = (byte) m;
            return_message[2] = (byte) (255 - m);

            int m_size = message.length;
            for (int i = 0; i < message.length; i++) {
                return_message[i + 3] = (byte) message[i];
            }
            return_message[m_size + 3] = (byte) Integer.parseInt(str_07, 16);
            return_message[m_size + 4] = (byte) Integer.parseInt(str_06, 16);

            return return_message;

        } else if (m > 0) {

            byte[] return_message = new byte[1029];
            byte[] message = new byte[1024];
            Arrays.fill(message, (byte) 0x00);

            for (int i = 0; i < data.length; i++) {
                message[i] = data[i];
            }

            String str_0607 = Units.getCRC(message);
            while (true) {
                if (str_0607.length() < 4) {
                    str_0607 = "0" + str_0607;
                } else {
                    break;
                }
            }

            String str_06 = str_0607.substring(0, 2);
            String str_07 = str_0607.substring(2, 4);

            return_message[0] = (byte) 2;
            return_message[1] = (byte) m;
            return_message[2] = (byte) (255 - m);

            int m_size = message.length;
            for (int i = 0; i < message.length; i++) {
                return_message[i + 3] = (byte) message[i];
            }

            return_message[m_size + 3] = (byte) Integer.parseInt(str_07, 16);
            return_message[m_size + 4] = (byte) Integer.parseInt(str_06, 16);

            return return_message;

        } else if (m == -1) {

            byte[] message = new byte[1029];
            Arrays.fill(message, (byte) 0);
            message[0] = (byte) 02;
            message[1] = (byte) 0;
            message[2] = (byte) 255;
            return message;

        } else {
            return null;
        }
    }

}