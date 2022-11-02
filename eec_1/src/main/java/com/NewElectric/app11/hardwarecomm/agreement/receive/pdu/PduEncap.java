package com.NewElectric.app11.hardwarecomm.agreement.receive.pdu;


import com.NewElectric.app11.units.Units;

public class PduEncap {

    private static String[] chargingStatus = new String[]{"待机","充电","故障","启动","排队","加热","告警"};
    private static String[] chargingStopStatus = new String[]{"充电模块故障", "充电模块通信异常告警", "充电模块开启失败告警", "充电继电器黏连告警", "加热继电器黏连告警", "充电继电器驱动失效告警", "加热继电器驱动失效告警", "整包电池过压告警", "电池端接触器开路告警", "电池电压异常告警", "安卓版离线故障", "系统无可用模块故障"};
    private static String[] chargingErrorStatus = new String[]{"充电模块故障终止", "充电模块通信异常终止", "充电模块开启失败终止", "充电继电器粘连终止", "充电继电器驱动失效终止", "加热继电器黏连终止", "加热继电器驱动失效终止", "整包电池过压防护终止", "电池端接触器开路防护终止", "电池电压异常防护终止", "安卓版离线防护终止", "安卓版下发关机终止"};
    private static String[] equipmentCharging = new String[]{"模块保护（2）","模块故障（1）","过温","输出过压","温度限功率状态","交流限功率状态","模块EEPROM故障","风扇故障"
            ,"模块WALK-In功能使能","风扇全速","模块开机","模块限功率","模块CAN错误状态","模块电流均流告警","模块识别","AC OFF输入继电器动作"
            ,"","","AC欠压保护","模块顺序起机功能使能","PFC过压故障","AC过压故障","ID重复","严重不均流大于3s"
            ,"","模块短路故障","","不均流时间大于3s","","","",""};
    private static String[] equipmentPdu = new String[]{"充电模块故障","充电模块通信异常告警","充电模块开启失败告警","充电继电器黏连告警","加热继电器黏连告警","充电继电器驱动失效告警","加热继电器驱动失效告警","整包电池过压告警",
            "电池端接触器开路告警","电池电压异常告警","安卓版离线故障","系统无可用模块故障","","","","",
            "","","","","","","","",
            "","","","","","","",""};

    //解析 980300??
    public static PduChargingInfoBean chargingInfoAnalysis(byte[] bytes){

        String status = chargingStatus[bytes[0]];
        double outputVoltage = (0xff & bytes[2] * 256 + 0xff & bytes[1]) / 10;
        double outputElectric = (4000 - (0xff & bytes[4] * 256 + 0xff & bytes[3])) / 10;
        String stopStatus = "无";
        if(bytes[6] != 0 ){
            stopStatus = chargingStopStatus[bytes[6]];
        }else if(bytes[6] == 0){
            stopStatus = "无";
        }else{
            stopStatus = "未知";
        }
        String errorStatus = "";
        if(bytes[5] == 0){
            errorStatus = "无";
        }else if(bytes[5] == 1){
            errorStatus = chargingErrorStatus[0];
        }else if(bytes[5] == 2){
            errorStatus = chargingErrorStatus[1];
        }else if(bytes[5] == 3){
            errorStatus = chargingErrorStatus[2];
        }else if(bytes[5] == 32){
            errorStatus = chargingErrorStatus[3];
        }else if(bytes[5] == 33){
            errorStatus = chargingErrorStatus[4];
        }else if(bytes[5] == 34){
            errorStatus = chargingErrorStatus[5];
        }else if(bytes[5] == 35){
            errorStatus = chargingErrorStatus[6];
        }else if(bytes[5] == 64){
            errorStatus = chargingErrorStatus[7];
        }else if(bytes[5] == 65){
            errorStatus = chargingErrorStatus[8];
        }else if(bytes[5] == 66){
            errorStatus = chargingErrorStatus[9];
        }else if(bytes[5] == 67){
            errorStatus = chargingErrorStatus[10];
        }else if(bytes[5] == 96){
            errorStatus = chargingErrorStatus[11];
        }
        return new PduChargingInfoBean(bytes,status,outputVoltage,outputElectric,stopStatus,errorStatus);
    }

    //解析9807？？
    public static PduEquipmentBean equipmentAnalysis(byte[] bytes){
        String dataStrHex = Units.ByteArrToHex(bytes).substring(8,16);
        String dataStrBinaryString = Units.hexString2binaryStringAnt(dataStrHex);
        String error = "";
        for(int i = 0 ; i < dataStrBinaryString.length() ; i++){
            String item = dataStrBinaryString.substring(i,i+1);
            if(item.equals("1")){
                if(bytes[0] > 3){
                    error = error + equipmentPdu[i];
                }else{  
                    error = error + equipmentCharging[i];
                }
            }
        }
        if(error.equals("")){
            error = "无";
        }
        String version = "V"+(0xff & bytes[1])+"B"+(0xff & bytes[2])+"D"+(0xff & bytes[3]);
        return new PduEquipmentBean(bytes , version , error);
    }
}
