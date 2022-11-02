package com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate;

import com.NewElectric.app11.units.Units;

/**
 * 控制板信息解析
 */

public class ControlPlateReceiveEncap {

    private static String TS = "6928.7832//6442.0112//5992.9141//5578.3193//5195.3418//4841.3589//4513.981//4211.0298//3930.5215//3670.644//3429.7449//3206.3125//2998.9675//2806.4453//2627.5916//2461.3464//2306.74//2162.8838//2028.9607//1904.2219//1787.9797//1679.6017//1578.5061//1484.1584//1396.0662//1313.7754//1236.8685//1164.9598//1097.6941//1034.7432//975.8038//920.5962//868.8615//820.3603//774.871//732.1889//692.1238//654.4999//619.154//585.9346//554.7016//525.3245//497.6821//471.6621//447.1599//424.0781//402.3264//381.8204//362.4818//344.2375//327.0195//310.764//295.4121//280.9084//267.2014//254.2428//241.9877//230.394//219.4224//209.0361//199.2007//189.8841//181.0559//172.6881//164.754//157.229//150.0898//143.3144//136.8825//130.7749//124.9734//119.4612//114.2223//109.2417//104.5053//100//95.7132//91.6333//87.7492//84.0505//80.5274//77.1707//73.9717//70.9222//68.0144//65.2411//62.5954//60.0707//57.661//55.3604//53.1635//51.0651//49.0602//47.1443//45.313//43.5621//41.8878//40.2862//38.7539//37.2876//35.8842//34.5405//33.2538//32.0214//30.8408//29.7096//28.6253//27.586//26.5895//25.6338//24.7171//23.8376//22.9937//22.1836//21.4061//20.6594//19.9424//19.2537//18.592//17.9562//17.3452//16.7578//16.193//15.6499//15.1276//14.6251//14.1417//13.6764//13.2286//12.7976//12.3825//11.9828//11.5978//11.227//10.8697//10.5254//10.1935//9.8736//9.5652//9.2678//8.9809//8.7042//8.4373//8.1797//7.9312//7.6912//7.4596//7.236//7.0201//6.8115//6.6101//6.4155//6.2274//6.0457//5.8701//5.7003//5.5362//5.3775//5.224//5.0755//4.9319//4.793//4.6586//4.5285//4.4026//4.2807//4.1627//4.0484//3.9378//3.8306//3.7268//3.6263//3.5289//3.4345//3.343//3.2543//3.1683//3.085//3.0042//2.9258//2.8498//2.7761//2.7045//2.6352//2.5678//2.5025//2.4391//2.3775//2.3178//2.2598//2.2034//2.1487//2.0956//2.044//1.9939//1.9452//1.8978//1.8518//1.8071//1.7637//1.7215//1.6804//1.6405//1.6017//1.564//1.5273//1.4915//1.4568//1.423//1.3901//1.3582//1.327//1.2967//1.2672//1.2385//1.2106//1.1833//1.1568//1.131//1.1059//1.0814//1.0576//1.0343//1.0117//0.9896//0.9681//0.9472//0.9268//0.9069//0.8875//0.8686//0.8501//0.8321//0.8146//0.7975//0.7808//0.7646//0.7487//0.7332//0.7181//0.7034//0.689//0.6749//0.6612//0.6479//0.6348//0.6221//0.6096//0.5975//0.5856//0.574//0.5627//0.5517//0.5409//0.5303//0.52//0.5099//0.5001//0.4905//0.4811//0.4719//0.463//0.4542//0.4456//0.4372//0.429//0.421//0.4132//0.4055//0.398//0.3907//0.3836//0.3765//0.3697//0.363//0.3564//0.35//0.3437//0.3376//0.3315//0.3257//0.3199//0.3142//0.3087//0.3033//0.298//0.2928//0.2878//0.2828//0.2779//0.2732//0.2685//0.2639//0.2594//0.255//0.2507//0.2465//0.2424//0.2384//0.2344//0.2305//0.2267//0.223//0.2193//0.2157//0.2122//0.2088//0.2054//0.2021//0.1988//0.1957//0.1925//0.1895//0.1865//0.1835//0.1806//0.1778//0.175//0.1723//0.1696//0.167//0.1644//0.1619//0.1594//0.157//0.1546//0.1522//0.1499//0.1477//0.1455//0.1433//0.1412//0.1391//0.137//0.135//0.133//0.131//0.1291//0.1272//0.1254//0.1236//0.1218//0.1201//0.1183//0.1166//0.115//0.1134//0.1118//0.1102//0.1086//0.1071";
    private static double[] tsArrays = null;

    public ControlPlateReceiveEncap() {
        if(tsArrays == null){
            String[] tempArrays = TS.split("//");
            tsArrays = new double[tempArrays.length];
            for(int i = 0 ; i < tempArrays.length ; i++){
                tsArrays[i] = Double.parseDouble(tempArrays[i]);
            }
        }
    }

    /**
     * 解析基础信息
     * @param bytes
     * @return
     */
    public ControlPlateBaseBean returnBaseByBytes(byte[] bytes){

        //负数处理
        int[] data = new int[bytes.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = bytes[i] & 0xff;
        }

        ControlPlateBaseBean controlPlateBaseBean = new ControlPlateBaseBean();
        //帧地址
        controlPlateBaseBean.setAddress(data[0]);
        //帧类型
        controlPlateBaseBean.setAddress(data[1]);
        //帧长度
        controlPlateBaseBean.setAddress(data[2]);

        //赋值舱门微动状态
        int inching = data[3] * 256 + data[4];
        String inchingBinarySystem = Units.int2Binary(inching);
        //设置inching_1   2021年4月8日 此微动为舱门底部微动
        controlPlateBaseBean.setInching_1(Integer.parseInt(inchingBinarySystem.substring(inchingBinarySystem.length() - 1, inchingBinarySystem.length())));
        //设置inching_2   2021年4月8日 此微动目前为空
        controlPlateBaseBean.setInching_2(Integer.parseInt(inchingBinarySystem.substring(inchingBinarySystem.length() - 2, inchingBinarySystem.length()-1)));
        //设置inching_3   2021年4月8日 此微动为舱门侧边微动 而且 这个值是个反值 0代表有电池 1代表没电池 我这里做了处理
        int inching_3 = Integer.parseInt(inchingBinarySystem.substring(inchingBinarySystem.length() - 3, inchingBinarySystem.length()-2));
        if(inching_3 == 1){
            inching_3 = 0;
        }else if(inching_3 == 0) {
            inching_3 = 1;
        }
        controlPlateBaseBean.setInching_3(inching_3);

        //赋值电池温度
        int batteryTemperature =  data[6] * 256 + data[5];
        batteryTemperature = (batteryTemperature - 2731) / 10;
        if (batteryTemperature == -273) {
            batteryTemperature = 0;
        }
        controlPlateBaseBean.setBatteryTemperature(batteryTemperature);

        //赋值电池电流
        int batteryElectric =  data[14] * 256 + data[13];
        controlPlateBaseBean.setBatteryElectric(batteryElectric);

        //赋值相对电池剩余容量 (SOC 98 99 置百)
        int batteryRelativeSurplus = data[17];
        if(batteryRelativeSurplus > 97){
            batteryRelativeSurplus = 100;
        }
        controlPlateBaseBean.setBatteryRelativeSurplus(batteryRelativeSurplus);

        //赋值绝对电池剩余容量
        int batteryAbsoluteSurplus = data[19];
        controlPlateBaseBean.setBatteryAbsoluteSurplus(batteryAbsoluteSurplus);

        //赋值电池剩余容量
        int batteryRemainingCapacity = data[22] * 256 + data[21];
        controlPlateBaseBean.setBatteryRemainingCapacity(batteryRemainingCapacity);

        //赋值电池满充容量
        int batteryFullCapacity = data[24]*256 + data[23];
        controlPlateBaseBean.setBatteryFullCapacity(batteryFullCapacity);

        //赋值电池循环次数
        int loops = data[26]*256 + data[25];
        controlPlateBaseBean.setLoops(loops);

        //赋值 单体最小电压 单体最大电压 压差
        int[] arrays = new int[14];
        for(int i = 0 ; i < arrays.length ; i++){
            arrays[i] = data[28 + 2 * i] * 256 + data[27 + 2 * i];
        }
        int min = 0, max = 0;
        int minIndex = 0,maxIndex = 0;
        min = max = arrays[0];
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i] != 0) {
                // 判断最大值
                if (arrays[i] > max){
                    max = arrays[i];
                    maxIndex = i;
                }
                // 判断最小值
                if (arrays[i] < min) {
                    min = arrays[i];
                    minIndex = i;
                }
            }
        }
        int pressureDifferential = max - min;
        controlPlateBaseBean.setItemMax((maxIndex+1)+"_"+max);
        controlPlateBaseBean.setItemMin((minIndex+1)+"_"+min);
        controlPlateBaseBean.setPressureDifferential(pressureDifferential);

        //赋值控制板上层版本
        int controlPlateTopVersion =  data[55];
        controlPlateBaseBean.setControlPlateTopVersion(controlPlateTopVersion);

        //赋值控制板底层版本
        int controlPlateBottomVersion = data[56];
        controlPlateBaseBean.setControlPlateBottomVersion(controlPlateBottomVersion);

        //赋值电池BID
        String BID = "";
        for(int i = 0 ; i < 16 ; i++){
            if(data[57 + i] == 0){
                BID = BID + "0";
            }else{
                BID = BID + (char)(data[57 + i]);
            }
        }
        controlPlateBaseBean.setBID(BID);

        //赋值电池电压
        int batteryVoltage = data[11] * 256 * 256 + data[10] * 256 + data[9];
        controlPlateBaseBean.setBatteryVoltage(batteryVoltage);

        //赋值电池版本
        String str_77 = Integer.toHexString(data[77]);
        if (data[77] < 16) {
            str_77 = "0" + str_77;
        }
        String str_78 = Integer.toHexString(data[78]);
        if (data[78] < 16) {
            str_78 = "0" + str_78;
        }
        String s_7877 = str_78 + str_77;
        controlPlateBaseBean.setBatteryVersion(s_7877);

        //赋值控制本温度
        int controlPlateTemperature = data[97];
        controlPlateBaseBean.setControlPlateTemperature(controlPlateTemperature);

        //赋值电池健康度
        int batteryHealthy = data[100] * 256 + data[99];
        controlPlateBaseBean.setBatteryHealthy(batteryHealthy);

        //赋值传感器温度一
        double setTemperatureSensor_1 = data[101] * 256 * 256 * 256 + data[102] * 256 * 256 + data[103] * 256 + data[104];
        setTemperatureSensor_1 = setTemperatureSensor_1 / 10000;
        controlPlateBaseBean.setTemperatureSensor_1(setTemperatureSensor_1);

        //赋值传感器温度二
        double mTemperatureSensor_2 = data[105] * 256 * 256 * 256 + data[106] * 256 * 256 + data[107] * 256 + data[108];
        mTemperatureSensor_2 = mTemperatureSensor_2 / 10000;
        double[] temperatureSensor_2 = Units.numSearch(tsArrays, mTemperatureSensor_2);
        if(temperatureSensor_2[0] > 350){
            temperatureSensor_2[0] = 50;
        }
        controlPlateBaseBean.setTemperatureSensor_2(temperatureSensor_2[0] - 50);

        //赋值电池UID
        String UID = "";
        for(int i = 0 ; i < 8 ; i++){
            if(data[109 + i] == 0){
                UID = UID + "0";
            }else{
                UID = UID + (char)(data[109 + i]);
            }
        }
        controlPlateBaseBean.setUID(UID);

        return controlPlateBaseBean;
    }


    /**
     * 解析预警信息
     * @param bytes
     * @return
     */
    public ControlPlateWarningBean returnWaringByBytes(byte[] bytes) {

        //负数处理
        int[] data = new int[bytes.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = bytes[i] & 0xff;
        }

        int warning = data[5];
        String warningStr = "";
        String warningBinary = Units.int2Binary(warning);
        for(int i = 0 ; i < warningBinary.length() ; i++){
            String warningItem = warningBinary.substring(i , i+1);
            if(warningItem.equals("1") && i == 0){
                warningStr = warningStr + "充电握手失败/";
            }
            if(warningItem.equals("1") && i == 1){
                warningStr = warningStr + "过低温/";
            }
            if(warningItem.equals("1") && i == 2){
                warningStr = warningStr + "过高温/";
            }
            if(warningItem.equals("1") && i == 3){
                warningStr = warningStr + "短路/";
            }
            if(warningItem.equals("1") && i == 4){
                warningStr = warningStr + "放电过流/";
            }
            if(warningItem.equals("1") && i == 5){
                warningStr = warningStr + "充电过流/";
            }
            if(warningItem.equals("1") && i == 6){
                warningStr = warningStr + "欠压/";
            }
            if(warningItem.equals("1") && i == 7){
                warningStr = warningStr + "过压/";
            }
        }
        if(warningStr.equals("")){
            warningStr = "正常";
        }

        int error = data[7];
        String errorStr = "";
        String errorBinary = Units.int2Binary(error);
        for(int i = 0 ; i < errorBinary.length() ; i++){
            String errorItem = errorBinary.substring(i , i+1);
            if(errorItem.equals("1") && i == 0){
                errorStr = errorStr + "电池严重虚电/";
            }
            if(errorItem.equals("1") && i == 3){
                errorStr = errorStr + "采样失败/";
            }
            if(errorItem.equals("1") && i == 4){
                errorStr = errorStr + "保险丝断开/";
            }
            if(errorItem.equals("1") && i == 5){
                errorStr = errorStr + "电池反冲/";
            }
            if(errorItem.equals("1") && i == 6){
                errorStr = errorStr + "电池温度异常/";
            }
            if(errorItem.equals("1") && i == 7){
                errorStr = errorStr + "MOS击穿/";
            }
        }
        if(errorStr.equals("")){
            errorStr = "正常";
        }


        int MOS = data[8];
        String MOSStr = "";
        String MOSBinary = Units.int2Binary(MOS);
        for(int i = 0 ; i < MOSBinary.length() ; i++){
            String MOSItem = MOSBinary.substring(i , i+1);

            if(MOSItem.equals("1") && i == 6){
                MOSStr = MOSStr + "充电打开/";
            }
            if(MOSItem.equals("1") && i == 7){
                MOSStr = MOSStr + "放电打开/";
            }
        }
        if(MOSStr.equals("")){
            MOSStr = "正常";
        }


        int requireVol = data[9] + data[10] * 256 + data[11] * 256 * 256 + data[12] * 256 * 256 * 256 ;
        int requireEle = data[13] + data[14] * 256 + data[15] * 256 * 256 + data[16] * 256 * 256 * 256 ;
        int supportVol = data[17] + data[18] * 256 + data[19] * 256 * 256 + data[20] * 256 * 256 * 256 ;
        int supportEle = data[21] + data[22] * 256 + data[23] * 256 * 256 + data[24] * 256 * 256 * 256 ;

        ControlPlateWarningBean controlPlateWarningBean = new ControlPlateWarningBean(warningStr , errorStr , MOSStr , requireVol+"" , requireEle+"" , supportVol+"" , supportEle + "");
        return controlPlateWarningBean;
    }

}
