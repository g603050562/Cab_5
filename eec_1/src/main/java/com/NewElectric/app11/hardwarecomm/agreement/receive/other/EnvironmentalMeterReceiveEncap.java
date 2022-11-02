package com.NewElectric.app11.hardwarecomm.agreement.receive.other;

public class EnvironmentalMeterReceiveEncap {

    //获取温湿传感器参数
    public static EnvironmentalMeterBean getEnvironmentalMeterInfo(String msg){

        String meter_count_1 = msg.substring(6, 10);
        Integer x_1 = Integer.parseInt(meter_count_1, 16);
        float f_x_1 = x_1;
        float meter_count_f_1 = f_x_1 / 10;
        String the_meter = meter_count_f_1 + "";
        String meter_count_2 = msg.substring(10, 14);
        Integer x_2 = Integer.parseInt(meter_count_2, 16);
        float f_x_2 = 0;

        if (x_2 > 1000) {
            f_x_2 = -(65535 - x_2);
        } else {
            f_x_2 = x_2;
        }

        float meter_count_f_2 = f_x_2 / 10;
        String tem_meter = meter_count_f_2 + "";

        EnvironmentalMeterBean environmentalMeterBean = new EnvironmentalMeterBean(the_meter , tem_meter);
        return environmentalMeterBean;
    }

}
