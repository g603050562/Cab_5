package com.NewElectric.app11.hardwarecomm.agreement.receive.other;

public class ElectricityMeterReceiveEncap {

    //解析电量 两种电表 - 其1
    public static double getMeterCount_1(String str){
        String d = str.substring(36, 38);
        String c = str.substring(38, 40);
        String b = str.substring(40, 42);
        String a = str.substring(42, 44);

        int int_d = Integer.parseInt(d, 16) - 51;
        int int_c = Integer.parseInt(c, 16) - 51;
        int int_b = Integer.parseInt(b, 16) - 51;
        int int_a = Integer.parseInt(a, 16) - 51;

        String f_d = Integer.toHexString(int_d);
        if (f_d.length() == 1) {
            f_d = "0" + f_d;
        }
        String f_c = Integer.toHexString(int_c);
        if (f_c.length() == 1) {
            f_c = "0" + f_c;
        }
        String f_b = Integer.toHexString(int_b);
        if (f_b.length() == 1) {
            f_b = "0" + f_b;
        }
        String f_a = Integer.toHexString(int_a);
        if (f_a.length() == 1) {
            f_a = "0" + f_a;
        }
        String ele_meter = f_a + f_b + f_c + "." + f_d;
        double ele_meter_int = Double.parseDouble(ele_meter);
        return ele_meter_int;
    }

    //解析电量 两种电表 - 其2
    public static double getMeterCount_2(String str){
        String d = str.substring(28, 30);
        String c = str.substring(30, 32);
        String b = str.substring(32, 34);
        String a = str.substring(34, 36);

        int int_d = Integer.parseInt(d, 16) - 51;
        int int_c = Integer.parseInt(c, 16) - 51;
        int int_b = Integer.parseInt(b, 16) - 51;
        int int_a = Integer.parseInt(a, 16) - 51;

        String f_d = Integer.toHexString(int_d);
        if (f_d.length() == 1) {
            f_d = "0" + f_d;
        }
        String f_c = Integer.toHexString(int_c);
        if (f_c.length() == 1) {
            f_c = "0" + f_c;
        }
        String f_b = Integer.toHexString(int_b);
        if (f_b.length() == 1) {
            f_b = "0" + f_b;
        }
        String f_a = Integer.toHexString(int_a);
        if (f_a.length() == 1) {
            f_a = "0" + f_a;
        }
        String ele_meter = f_a + f_b + f_c + "." + f_d;
        double ele_meter_int = Double.parseDouble(ele_meter);
        return ele_meter_int;
    }

}
