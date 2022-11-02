package com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_3;

import android.content.Context;

import java.util.Arrays;
import java.util.Calendar;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.agreement.send.pdu.PduSend;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.service.logic.logic_charging.ChargingUtils;
import com.NewElectric.app11.units.Units;

public class Charging_1_to_3 extends ChargingUtils {

    //单例
    private static Charging_1_to_3 instance = new Charging_1_to_3();
    private Charging_1_to_3() {}
    public static Charging_1_to_3 getInstance() {
        return instance;
    }

    private Context context;
    //发送自增的生命帧
    int sendLiveCount = 0;
    //充电参数
    int[] chargingState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1}; // -1 - 初始化    0 - 充电   1 - 关闭      2 - 加热
    int[] chargingDianliu = new int[]{0, 0, 0};
    int[] chargingTarget = new int[]{-1, -1, -1};
    //线程参数
    private int threadCode = 0; // 0 - 初始化  1 - 充电   2 - 充电且加热
    private int changeCode = 1;


    //type: 0-不加热 1-加热
    @Override
    public void logicInit(Context context) {
        this.context = context;
        BaseLogicInit(context);

        String type = cabInfoSp.getChargeMode();
        if (type.equals("1")) {
            setMode(1);
        } else if (type.equals("-1")) {
            setMode(0);
        } else {
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            if (month == 10 || month == 11 || month == 12 || month == 1 || month == 2) {
                setMode(1);
            } else {
                setMode(0);
            }
        }
    }

    //设置充电模式
    @Override
    public void setMode(int type) {
        chargingState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1};
        chargingDianliu = new int[]{0, 0, 0};
        chargingTarget = new int[]{-1, -1, -1};
        if (type == 0 && threadCode != 1) {
            charging();
        } else if (type == 1 && threadCode != 2) {
            chargingAndHeating();
        }
    }

    //重置舱门状态
    @Override
    public void startChange(int door) {
        chargingState[door - 1] = -1;
        changeCode = 1;
    }

    //获取每个仓门的充电状态
    @Override
    public int[] getChargeStatus() {
        return chargingState;
    }

    @Override
    public void onDestroy() {
        threadCode = 0;
    }

    //只进行充电
    @Override
    protected void charging() {
        threadCode = 1;
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                //关闭pdu
                try {
                    PduSend.closeAllPdu();
                    sleep(10 * 1000);
                    System.out.println("CAN - 充电机 - 模式 - 充电：" + "    1拖3 ");
                    System.out.println("CAN - 充电机 - 模式 - 充电：" + "    正在启用充电免加热模式 ");
                    while (threadCode == 1) {

                        String saveType = new CabInfoSp(context).getChargeMode();
                        if (saveType.equals("1")) {
                            setMode(1);
                        }
                        Calendar calendar = Calendar.getInstance();
                        int minute = calendar.get(Calendar.MINUTE);
                        int second = calendar.get(Calendar.SECOND);
                        int hour = calendar.get(Calendar.HOUR);

                        //下发生命帧
                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.sendLive(sendLiveCount));
                        if (sendLiveCount > 254) {
                            sendLiveCount = 0;
                        } else {
                            sendLiveCount = sendLiveCount + 1;
                        }
                        System.out.println("CAN - 充电机 - 状态 - 充电：" + "    充电状态 - " + Arrays.toString(chargingState) + "   选择状态 - " + Arrays.toString(chargingTarget));

                        int[] DIANLIU = new int[controlPlateInfo.getControlPlateBaseBeans().length];
                        int[] PERCENtAGES = new int[controlPlateInfo.getControlPlateBaseBeans().length];
                        int[] DIANYA = new int[controlPlateInfo.getControlPlateBaseBeans().length];
                        double[] WENDU = new double[controlPlateInfo.getControlPlateBaseBeans().length];
                        String[] BIDS = new String[controlPlateInfo.getControlPlateBaseBeans().length];
                        String[] REQELE = new String[controlPlateInfo.getControlPlateBaseBeans().length];
                        for (int i = 0; i < controlPlateInfo.getControlPlateBaseBeans().length; i++) {
                            DIANLIU[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryElectric();
                            PERCENtAGES[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryRelativeSurplus();
                            DIANYA[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryVoltage();
                            BIDS[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBID();
                            WENDU[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryTemperature();
                            REQELE[i] = controlPlateInfo.getControlPlateWarningBeans()[i].getRequireELe();
                        }

                        //每分钟选择电池
                        if (second == 0 || changeCode == 1) {

                            writeLog("CAN - 充电机 - 状态：" + "    充电状态 - " + Arrays.toString(chargingState) + "   选择状态 - " + Arrays.toString(chargingTarget));
                            changeCode = 0;
                            int[] chargingTemp = new int[]{100, 100, 100}; //电量参数
                            for (int i = 0; i < 3; i++) {

                                //如果一横仓的电池都是关闭状态了，全部设置成默认值
                                if (chargingState[i * 3] == 1 && chargingState[i * 3 + 1] == 1 && chargingState[i * 3 + 2] == 1) {
                                    chargingState[i * 3] = -1;
                                    chargingState[i * 3 + 1] = -1;
                                    chargingState[i * 3 + 2] = -1;
                                    chargingTarget[i] = -1;
                                    System.out.println("CAN - 充电机 - 初始：   第" + (i + 1) + "个充电器轮冲完毕，正在初始化");
                                }

                                //循环一横排的每个仓
                                for (int j = 0; j < 3; j++) {
                                    //如果碰到有电池电量是0或者100的电池 不用充电 先排除掉不满足条件的电池
                                    if (DIANLIU[i * 3 + j] != 0) {
                                        chargingState[i * 3 + j] = 0;
                                    } else {
                                        if (PERCENtAGES[i * 3 + j] == 0 || PERCENtAGES[i * 3 + j] == 100) {
                                            chargingState[i * 3 + j] = 1;
                                        }
                                    }
                                }

                                //先判断一横仓的电流，如果电流为0，就把当前充电的电池状态设置为不需要充电 - 1
                                chargingDianliu[i] = DIANLIU[i * 3] + DIANLIU[i * 3 + 1] + DIANLIU[i * 3 + 2];
                                //电流等于0，存在目标电池(比如电池充满了或者一直充不进去)
                                if (chargingDianliu[i] <= 0 && chargingTarget[i] != -1) {
                                    chargingState[chargingTarget[i]] = 1;
                                    chargingTarget[i] = -1;
                                }

                                //电流等于0，不存在目标电池（没有选择要充电的电池，比如刚开机或者上面流程的电池充不进去）
                                if (chargingDianliu[i] <= 0 && chargingTarget[i] == -1) {
                                    //循环一横排的每个仓
                                    for (int j = 0; j < 3; j++) {
                                        //选择电池每排都选择一个 没有轮冲过 电量最小 电量不为0
                                        if (chargingState[i * 3 + j] != 1 && PERCENtAGES[i * 3 + j] < chargingTemp[i] && PERCENtAGES[i * 3 + j] != 0 && !BIDS[i * 3 + j].equals("FFFFFFFFFFFFFFFF")) {
                                            chargingTemp[i] = PERCENtAGES[i * 3 + j];
                                            chargingTarget[i] = i * 3 + j;
                                        }
                                    }
                                    //设置充电状态
                                    for (int j = 0; j < 3; j++) {
                                        if (chargingTarget[i] != -1) {
                                            chargingState[chargingTarget[i]] = 0;
                                        }
                                    }
                                }

                                //存在电流，存在目标电池（正在充电）
                                else if (chargingDianliu[i] > 0 && chargingTarget[i] != -1) {
                                    chargingState[chargingTarget[i]] = 0;

                                }
                                //存在电流，不存在目标电池（电池异常，以后再做处理）
                                else if (chargingDianliu[i] > 0 && chargingTarget[i] == -1) {

                                }
                            }

                            //打开需要充电的舱门
                            for (int i = 0; i < 9; i++) {
                                //获取电池编号
                                String BidStr = BIDS[i];
                                String TopBidStr = BidStr.substring(0, 1);
                                String MidBidStr = BidStr.substring(9, 10);
                                String EndBidStr = BidStr.substring(10, 12);
                                //最高电压
                                int maxVal = 0;
                                if (TopBidStr.equals("M") && EndBidStr.equals("MM")) {
                                    maxVal = 68400;
                                } else if (TopBidStr.equals("N") && EndBidStr.equals("NN")) {
                                    maxVal = 57500;
                                } else if (TopBidStr.equals("R") && EndBidStr.equals("RR")) {
                                    maxVal = 57500;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("C") && EndBidStr.equals("AA")) {
                                    maxVal = 54600;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("E") && EndBidStr.equals("AA")) {
                                    maxVal = 54600;
                                } else if (TopBidStr.equals("M") && EndBidStr.equals("ZZ")) {
                                    maxVal = 68400;
                                } else {
                                    maxVal = 54600;
                                }
                                //目标电压
                                int barVal = DIANYA[i];
                                if (i == chargingTarget[0] || i == chargingTarget[1] || i == chargingTarget[2]) {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.openChargingSwitch(i + 1, maxVal, barVal));
                                    sleep(50);
                                } else {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, maxVal, barVal));
                                    sleep(50);
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(i + 1, maxVal, barVal));
                                    sleep(50);
                                }
                            }
                        }

                        if (second % 5 == 0) {
                            //设置电压电路的舱门
                            for (int i = 0; i < 9; i++) {
                                //根据电池id判断60V电池
                                String BidStr = BIDS[i];
                                String TopBidStr = BidStr.substring(0, 1);
                                String MidBidStr = BidStr.substring(9, 10);
                                String EndBidStr = BidStr.substring(10, 12);
                                //乐嘉电池只能充电到10A需要做判定
                                String TopLeJiaBIDS = BidStr.substring(0, 7);
                                String endLeJiaBIDS = BidStr.substring(9, 12);
                                //国轩
                                String TopGuoXuanBIDS = BidStr.substring(0, 5);
                                //设置电压电流
                                int val = 0;
                                int ele = 0;
                                if (TopBidStr.equals("M") && EndBidStr.equals("MM")) {
                                    val = 68400;
                                    ele = 3850;
                                } else if (TopLeJiaBIDS.equals("GBEBCGG") && endLeJiaBIDS.equals("AGG")) {
                                    val = 54600;
                                    ele = 3900;
                                } else if (TopGuoXuanBIDS.equals("PDLLC")) {
                                    val = 57600;
                                    ele = 3900;
                                } else if (TopBidStr.equals("N") && EndBidStr.equals("NN")) {
                                    val = 57600;
                                    ele = 3850;
                                } else if (TopBidStr.equals("R") && EndBidStr.equals("RR")) {
                                    val = 57600;
                                    ele = 3810;
                                } else if (TopBidStr.equals("P") && EndBidStr.equals("PP")) {
                                    val = 57600;
                                    ele = 3850;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("C") && EndBidStr.equals("AA")) {
                                    val = 54600;
                                    ele = 3850;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("E") && EndBidStr.equals("AA")) {
                                    val = 54600;
                                    ele = 3850;
                                } else if (TopBidStr.equals("M") && EndBidStr.equals("ZZ")) {
                                    int dianliu = 0;
                                    if(REQELE[i].equals("")){
                                        //设置电压电流
                                        dianliu = (guoXuanTiDu((int)WENDU[i], DIANYA[i])) / 100;
                                        System.out.println("CAN - 充电机 - 梯度电流 - "  +  dianliu);
                                    }else{
                                        dianliu = Integer.parseInt(REQELE[i]) / 100;
                                        System.out.println("CAN - 充电机 - 需求电流 - "  +  dianliu);
                                    }
                                    val = 68400;
                                    ele = 4000 - dianliu;
                                } else {
                                    val = 54600;
                                    ele = 3850;
                                }
                                if (i == chargingTarget[0] || i == chargingTarget[1] || i == chargingTarget[2]) {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, val, ele));
                                    sleep(50);
                                    System.out.println("CAN - 充电机 - 下发设置充电参数：    第" + (i + 1) + "个模块");
                                }
                            }
                        }

                        if (second == 0 && minute == 0 && hour % 3 == 0) {
                            chargingState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1};
                            chargingTarget = new int[]{-1, -1, -1};
                            chargingDianliu = new int[]{0, 0, 0};
                        }


                        sleep(1000);

                    }
                    System.out.println("CAN - 充电机 - 模式：" + "    正在关闭充电免加热模式 ");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }


    //充电和加热一起
    @Override
    protected void chargingAndHeating() {
        threadCode = 2;
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //关闭pdu
                    PduSend.closeAllPdu();
                    sleep(10 * 1000);
                    System.out.println("CAN - 充电机 - 模式 - 充电：" + "    1拖3 ");
                    System.out.println("CAN - 充电机 - 模式：" + "    正在启用充电且加热模式 ");
                    while (threadCode == 2) {

                        String saveType = new CabInfoSp(context).getChargeMode();
                        if (saveType.equals("0")) {
                            setMode(0);
                        }

                        Calendar calendar = Calendar.getInstance();
                        int minute = calendar.get(Calendar.MINUTE);
                        int second = calendar.get(Calendar.SECOND);
                        int hour = calendar.get(Calendar.HOUR);

                        //下发生命帧
                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.sendLive(sendLiveCount));
                        if (sendLiveCount > 254) {
                            sendLiveCount = 0;
                        } else {
                            sendLiveCount = sendLiveCount + 1;
                        }

                        int[] DIANLIU = new int[controlPlateInfo.getControlPlateBaseBeans().length];
                        int[] PERCENtAGES = new int[controlPlateInfo.getControlPlateBaseBeans().length];
                        int[] DIANYA = new int[controlPlateInfo.getControlPlateBaseBeans().length];
                        int[] WENDU = new int[controlPlateInfo.getControlPlateBaseBeans().length];
                        String[] BIDS = new String[controlPlateInfo.getControlPlateBaseBeans().length];
                        int[] TEM_2 = new int[controlPlateInfo.getControlPlateBaseBeans().length];
                        String[] CHARGESTATUS_str = new String[controlPlateInfo.getControlPlateBaseBeans().length];
                        String[] REQELE = new String[controlPlateInfo.getControlPlateBaseBeans().length];
                        for (int i = 0; i < controlPlateInfo.getControlPlateBaseBeans().length; i++) {
                            DIANLIU[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryElectric();
                            PERCENtAGES[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryRelativeSurplus();
                            DIANYA[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryVoltage();
                            BIDS[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBID();
                            WENDU[i] = (int) controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryTemperature();
                            TEM_2[i] = (int) controlPlateInfo.getControlPlateBaseBeans()[i].getTemperatureSensor_2();
                            CHARGESTATUS_str[i] = Units.ByteArrToHex(pduInfo.getPduChargingInfoBean()[i].getData());
                            REQELE[i] = controlPlateInfo.getControlPlateWarningBeans()[i].getRequireELe();
                        }

                        //每分钟选择电池
                        if (second == 0 || changeCode == 1) {

                            writeLog("CAN - 充电机 - 状态 - 充电且加热：" + "    充电状态 - " + Arrays.toString(chargingState) + "   选择状态 - " + Arrays.toString(chargingTarget));

                            changeCode = 0;
                            int[] chargingTemp = new int[]{-60, -60, -60}; //温度参数
                            for (int i = 0; i < 3; i++) {

                                //如果一横仓的电池都是关闭状态了，全部设置成默认值
                                if (chargingState[i * 3] == 1 && chargingState[i * 3 + 1] == 1 && chargingState[i * 3 + 2] == 1) {
                                    chargingState[i * 3] = -1;
                                    chargingState[i * 3 + 1] = -1;
                                    chargingState[i * 3 + 2] = -1;
                                    chargingTarget[i] = -1;
                                    System.out.println("CAN - 充电机 - 初始：    第" + (i + 1) + "个充电器轮冲完毕，正在初始化");
                                }

                                //循环一横排的每个仓
                                for (int j = 0; j < 3; j++) {
                                    //如果碰到有电池电量是0或者100的电池 不用充电 先排除掉不满足条件的电池
                                    if (DIANLIU[i * 3 + j] != 0) {
                                        chargingState[i * 3 + j] = 0;
                                    } else {
                                        if (PERCENtAGES[i * 3 + j] == 0 || PERCENtAGES[i * 3 + j] == 100) {
                                            chargingState[i * 3 + j] = 1;
                                        }
                                    }
                                }

                                //先判断一横仓的电流，如果电流为0，就把当前充电的电池状态设置为不需要充电 - 1
                                chargingDianliu[i] = DIANLIU[i * 3] + DIANLIU[i * 3 + 1] + DIANLIU[i * 3 + 2];
                                System.out.println("CAN - 充电机 - 电流：    第" + (i + 1) + "排充电机电流 - " + chargingDianliu[i] + "   选择状态 - " + Arrays.toString(chargingTarget));
                                //电流等于0，存在目标电池，且充电冲不进去

                                if (chargingTarget[i] == -1) {
                                    //循环一横排的每个仓
                                    for (int j = 0; j < 3; j++) {
                                        //选择电池每排都选择一个 没有轮冲过 温度最高 电量不为0
                                        //设置选择目标仓
                                        if (chargingState[i * 3 + j] != 1 && WENDU[i * 3 + j] > chargingTemp[i] && PERCENtAGES[i * 3 + j] != 0 && !BIDS[i * 3 + j].equals("FFFFFFFFFFFFFFFF") && !BIDS[i * 3 + j].equals("0000000000000000")) {
                                            chargingTemp[i] = WENDU[i * 3 + j];
                                            chargingTarget[i] = i * 3 + j;
                                        }
                                    }
                                    //设置充电状态
                                    //设置选择目标仓的充电状态，温度大于0则充电，温度小于0则加热
                                    for (int j = 0; j < 3; j++) {
                                        if (chargingTarget[i] != -1) {
                                            if (WENDU[chargingTarget[i]] < 6 && WENDU[chargingTarget[i]] != -40 && WENDU[chargingTarget[i]] != -60) {
                                                chargingState[chargingTarget[i]] = 2;
                                            } else {
                                                chargingState[chargingTarget[i]] = 0;
                                            }
                                        }
                                    }
                                    System.out.println("CAN - 充电机 - 选择：    第" + (i + 1) + "仓状态为-1初始化");
                                } else {
                                    //电流等于0，存在目标电池，刚充完电
                                    if (chargingDianliu[i] <= 0 && chargingState[chargingTarget[i]] == 0) {
                                        chargingState[chargingTarget[i]] = 1;
                                        chargingTarget[i] = -1;
                                        System.out.println("CAN - 充电机 - 选择：    第" + (i + 1) + "仓状态为0结束");
                                    }
                                    //电流等于0，存在目标电池，被强制变成1了
                                    else if (chargingDianliu[i] <= 0 && chargingState[chargingTarget[i]] == 1) {
                                        chargingState[chargingTarget[i]] = 1;
                                        chargingTarget[i] = -1;
                                        System.out.println("CAN - 充电机 - 选择：    第" + (i + 1) + "仓状态为1结束");
                                    }
                                    //电流等于0，存在目标电池，且电池正在加热
                                    else if (chargingDianliu[i] <= 0 && chargingState[chargingTarget[i]] == 2) {
                                        if (WENDU[chargingTarget[i]] > 5) {
                                            //等于6度的时候就可以给它充电了
                                            chargingState[chargingTarget[i]] = 0;
                                        } else {
                                            //小于5度的时候不做任何操作
                                        }
                                        System.out.println("CAN - 充电机 - 选择：    第" + (i + 1) + "仓状态为2结束");
                                    }
                                    //电流等于0，不存在目标电池（没有选择要充电的电池，比如刚开机或者上面流程的电池充不进去）
                                    else if (chargingDianliu[i] <= 0 && chargingState[chargingTarget[i]] == -1) {
                                        //循环一横排的每个仓
                                        for (int j = 0; j < 3; j++) {
                                            //选择电池每排都选择一个 没有轮冲过 温度最高 电量不为0
                                            //设置选择目标仓
                                            if (chargingState[i * 3 + j] != 1 && WENDU[i * 3 + j] > chargingTemp[i] && PERCENtAGES[i * 3 + j] != 0) {
                                                chargingTemp[i] = WENDU[i * 3 + j];
                                                chargingTarget[i] = i * 3 + j;
                                            }
                                        }
                                        //设置充电状态
                                        //设置选择目标仓的充电状态，温度大于0则充电，温度小于0则加热
                                        for (int j = 0; j < 3; j++) {
                                            if (chargingTarget[i] != -1) {
                                                if (WENDU[chargingTarget[i]] < 6) {
                                                    chargingState[chargingTarget[i]] = 2;
                                                } else {
                                                    chargingState[chargingTarget[i]] = 0;
                                                }
                                            }
                                        }
                                        System.out.println("CAN - 充电机 - 选择：    第" + (i + 1) + "仓状态为-1结束");
                                    }

                                    //存在电流，存在目标电池（正在充电）
                                    else if (chargingDianliu[i] > 0 && chargingState[chargingTarget[i]] == 0) {
                                        chargingState[chargingTarget[i]] = 0;
                                        System.out.println("CAN - 充电机 - 选择：    第" + (i + 1) + "仓状态为0继续");
                                    }
                                    //存在电流，存在目标电池（正在充电）
                                    else if (chargingDianliu[i] > 0 && chargingState[chargingTarget[i]] == 1) {
                                        chargingState[chargingTarget[i]] = 1;
                                        System.out.println("CAN - 充电机 - 选择：    第" + (i + 1) + "仓状态为1继续");
                                    }
                                    //存在电流，存在目标电池（正在充电）
                                    else if (chargingDianliu[i] > 0 && chargingState[chargingTarget[i]] == 2) {
                                        chargingState[chargingTarget[i]] = 2;
                                        System.out.println("CAN - 充电机 - 选择：    第" + (i + 1) + "仓状态为2继续");
                                    }
                                    //存在电流，不存在目标电池（电池异常，以后再做处理）
                                    else if (chargingDianliu[i] > 0 && chargingState[chargingTarget[i]] == -1) {
                                        System.out.println("CAN - 充电机 - 选择：    第" + (i + 1) + "仓状态为-1继续");
                                    }
                                }
                            }

                            System.out.println("CAN - 充电机 - 状态 - 充电且加热：" + "    充电状态 - " + Arrays.toString(chargingState) + "   选择状态 - " + Arrays.toString(chargingTarget));

                            //打开需要充电的舱门
                            for (int i = 0; i < 9; i++) {

                                String BidStr = BIDS[i];
                                String TopBidStr = BidStr.substring(0, 1);
                                String MidBidStr = BidStr.substring(9, 10);
                                String EndBidStr = BidStr.substring(10, 12);
                                //最高电压
                                int maxVal = 0;
                                if (TopBidStr.equals("M") && EndBidStr.equals("MM")) {
                                    maxVal = 68400;
                                } else if (TopBidStr.equals("N") && EndBidStr.equals("NN")) {
                                    maxVal = 57500;
                                } else if (TopBidStr.equals("R") && EndBidStr.equals("RR")) {
                                    maxVal = 57500;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("C") && EndBidStr.equals("AA")) {
                                    maxVal = 54600;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("E") && EndBidStr.equals("AA")) {
                                    maxVal = 54600;
                                } else if (TopBidStr.equals("M") && EndBidStr.equals("ZZ")) {
                                    maxVal = 68400;
                                } else {
                                    maxVal = 54600;
                                }
                                //目标电压
                                int barVal = DIANYA[i];

                                if (i == chargingTarget[0] || i == chargingTarget[1] || i == chargingTarget[2]) {
                                    if (chargingState[i] == 0) {
                                        // ---------- 充电逻辑
                                        String COAStatiu = CHARGESTATUS_str[i];
                                        if (COAStatiu.equals("")) {
                                            System.out.println("CAN - 充电机 - 下发：   error ");
                                        } else {
                                            String top_COASatiu = COAStatiu.substring(0, 2);
                                            if (top_COASatiu.equals("05") || top_COASatiu.equals("00")) {     // 05 - 加热   00 - 无作为
                                                MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(i + 1, maxVal, barVal));
                                                sleep(50);
                                                sleep(5000);
                                            }
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.openChargingSwitch(i + 1, maxVal, barVal));
                                            sleep(50);
                                        }

                                    } else if (chargingState[i] == 2) {
                                        // ---------- 加热逻辑
                                        String COAStatiu = CHARGESTATUS_str[i];
                                        if (COAStatiu.equals("")) {
                                            System.out.println("CAN - 充电机 - 下发：   error ");
                                        } else {
                                            System.out.println("CAN - 充电机 - 下发：   sussess - " + COAStatiu);
                                            String top_COASatiu = COAStatiu.substring(0, 2);
                                            if (top_COASatiu.equals("01") || top_COASatiu.equals("00")) {     // 01 - 充电   00 - 无作为
                                                MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, maxVal, barVal));
                                                sleep(50);
                                                sleep(5000);
                                            }
                                        }

                                        if (TEM_2[i] > 70) {
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, maxVal, barVal));
                                            sleep(50);
                                        } else {
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.openHeatingSwitch(i + 1, maxVal, barVal));  //41V
                                            sleep(50);
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, 41000, 3850));
                                            sleep(50);
                                            System.out.println("CAN - 充电机 - 下发加热：    第" + (i + 1) + "个模块   温度：" + WENDU[i] + "   电流：" + DIANLIU[i] + "   加热板温度D：" + TEM_2[i]);
                                        }

                                        //应该是 打开加热了 但是模块儿没有电压输出 所以重新关闭打开了
                                        if (COAStatiu.equals("")) {
                                            System.out.println("CAN - 充电机 - 下发：   error ");
                                        } else {
                                            String top_COASatiu = COAStatiu.substring(0, 2);
                                            String mid_CAOSatui = COAStatiu.substring(2, 6);
                                            if (top_COASatiu.equals("05") && mid_CAOSatui.equals("0000")) {
                                                MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, maxVal, barVal));
                                                sleep(50);
                                                MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(i + 1, maxVal, barVal));
                                                sleep(5000);
                                                MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.openHeatingSwitch(i + 1, maxVal, barVal));  //41V
                                                sleep(50);
                                            }
                                        }

                                    } else {
                                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, maxVal, barVal));
                                        sleep(50);
                                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(i + 1, maxVal, barVal));
                                        sleep(50);
                                    }
                                } else {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, maxVal, barVal));
                                    sleep(50);
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(i + 1, maxVal, barVal));
                                    sleep(50);
                                }
                            }
                        }

                        if (second % 5 == 0) {
                            //设置电压电路的舱门
                            for (int i = 0; i < 9; i++) {
                                //根据电池id判断60V电池
                                String BidStr = BIDS[i];
                                String TopBidStr = BidStr.substring(0, 1);
                                String MidBidStr = BidStr.substring(9, 10);
                                String EndBidStr = BidStr.substring(10, 12);
                                //乐嘉电池只能充电到10A需要做判定
                                String TopLeJiaBIDS = BidStr.substring(0, 7);
                                String endLeJiaBIDS = BidStr.substring(9, 12);
                                //国轩
                                String TopGuoXuanBIDS = BidStr.substring(0, 5);
                                //设置电压电流
                                int val = 0;
                                int ele = 0;
                                if (TopBidStr.equals("M") && EndBidStr.equals("MM")) {
                                    val = 68400;
                                    ele = 3850;
                                } else if (TopLeJiaBIDS.equals("GBEBCGG") && endLeJiaBIDS.equals("AGG")) {
                                    val = 54600;
                                    ele = 3900;
                                } else if (TopGuoXuanBIDS.equals("PDLLC")) {
                                    val = 57600;
                                    ele = 3900;
                                } else if (TopBidStr.equals("N") && EndBidStr.equals("NN")) {
                                    val = 57600;
                                    ele = 3850;
                                } else if (TopBidStr.equals("R") && EndBidStr.equals("RR")) {
                                    val = 57600;
                                    ele = 3810;
                                } else if (TopBidStr.equals("P") && EndBidStr.equals("PP")) {
                                    val = 57600;
                                    ele = 3850;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("C") && EndBidStr.equals("AA")) {
                                    val = 54600;
                                    ele = 3850;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("E") && EndBidStr.equals("AA")) {
                                    val = 54600;
                                    ele = 3850;
                                } else if (TopBidStr.equals("M") && EndBidStr.equals("ZZ")) {
                                    int dianliu = 0;
                                    if(REQELE[i].equals("")){
                                        //设置电压电流
                                        dianliu = (guoXuanTiDu((int)WENDU[i], DIANYA[i])) / 100;
                                        System.out.println("CAN - 充电机 - 梯度电流 - "  +  dianliu);
                                    }else{
                                        dianliu = Integer.parseInt(REQELE[i]) / 100;
                                        System.out.println("CAN - 充电机 - 需求电流 - "  +  dianliu);
                                    }
                                    val = 68400;
                                    ele = 4000 - dianliu;
                                }else {
                                    val = 54600;
                                    ele = 3850;
                                }
                                if (i == chargingTarget[0] || i == chargingTarget[1] || i == chargingTarget[2]) {

                                    int tarBarTem = WENDU[i];
                                    String COAStatiu = CHARGESTATUS_str[i];
                                    if (COAStatiu.equals("")) {
                                    } else {
                                        String top_COASatiu = COAStatiu.substring(0, 2);
                                        if (top_COASatiu.equals("05") && tarBarTem == 6) {     // 05 - 加热   00 - 无作为
                                            changeCode = 1;
                                        }
                                    }
                                    if (chargingState[i] == 0) {
                                        // ---------- 充电逻辑
                                        if (COAStatiu.equals("")) {
                                            System.out.println("CAN - 充电机 - 下发：    第" + (i + 1) + "个模块   温度：" + tarBarTem + "   电流：" + DIANLIU[i] + "   加热板温度：" + TEM_2[i] +"   pdu状态：为空" );
                                        } else {
                                            String top_COASatiu = COAStatiu.substring(0, 2);
                                            if (top_COASatiu.equals("01") || top_COASatiu.equals("00")) {     // 01 - 充电   00 - 无作为
                                                if (tarBarTem > 5) {
                                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, val, ele));
                                                    sleep(50);
                                                    System.out.println("CAN - 充电机 - 下发：    第" + (i + 1) + "个模块   温度：" + tarBarTem + "   电流：" + DIANLIU[i] + "   加热板温度：" + TEM_2[i] +"   pdu状态："+top_COASatiu );
                                                }
                                            }else{
                                                System.out.println("CAN - 充电机 - 下发：    第" + (i + 1) + "个模块   温度：" + tarBarTem + "   电流：" + DIANLIU[i] + "   加热板温度：" + TEM_2[i] +"   pdu状态：为空" );
                                            }
                                        }
                                        if (TEM_2[i] > 70) {
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, 64800, 3850));
                                            sleep(50);
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(i + 1, 64800, 3850));
                                            sleep(50);
                                        }
                                        if (TEM_2[i] >= 100) {
                                            chargingState[i] = 1;
                                            changeCode = 1;
                                        }

                                    } else if (chargingState[i] == 2) {
                                        // ---------- 加热逻辑
                                        if (TEM_2[i] > 70 && TEM_2[i] < 100) {
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, 64800, 3850));
                                            sleep(50);
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(i + 1, 64800, 3850));
                                            sleep(50);
                                        } else if (TEM_2[i] >= 100) {
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, 64800, 3850));
                                            sleep(50);
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(i + 1, 64800, 3850));
                                            sleep(50);
                                            chargingState[i] = 1;
                                            changeCode = 1;
                                        } else if (TEM_2[i] == -49 && second == 0 && minute % 3 == 1) {
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, 64800, 3850));
                                            sleep(50);
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(i + 1, 64800, 3850));
                                            sleep(50);
                                        } else if (WENDU[i] >= 6) {
                                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch(i + 1, 64800, 3850));
                                            sleep(50);
                                            chargingState[i] = 1;
                                            changeCode = 1;
                                        } else {
                                            if (COAStatiu.equals("")) {

                                            } else {
                                                String top_COASatiu = COAStatiu.substring(0, 2);
                                                if (top_COASatiu.equals("05") || top_COASatiu.equals("00")) {     // 05 - 加热   00 - 无作为
                                                    double bar_tem_per = Double.parseDouble(new CabInfoSp(context).getTemMeter());
                                                    if (bar_tem_per > 20) {
                                                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, 41000, 3977));
                                                    } else if (bar_tem_per <= 20 && bar_tem_per >= 6) {
                                                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, 41000, 3977));
                                                    } else if (bar_tem_per <= 5 && bar_tem_per >= 0) {
                                                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, 41000, 3974));
                                                    } else {
                                                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, 41000, 3850));
                                                    }
                                                    sleep(50);
                                                    System.out.println("CAN - 充电机 - 下发：    第" + (i + 1) + "个模块   温度：" + tarBarTem + "   电流：" + DIANLIU[i] + "   加热板温度B：" + TEM_2[i]);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    //没选中的电池不做任何操作
                                }
                            }
                        }

                        if (second == 0 && minute == 0 && hour % 3 == 0) {
                            chargingState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1};
                            chargingTarget = new int[]{-1, -1, -1};
                            chargingDianliu = new int[]{0, 0, 0};
                        }
                        sleep(1000);
                    }
                    System.out.println("CAN - 充电机 - 模式：" + "    正在关闭充电且加热模式 ");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private int guoXuanTiDu(int wendu, int dianya) {

        float canshu = 0;
        float item_dianya = dianya / 19 / 1000;

        if (wendu < 0) {
            canshu = 0;
        } else if (wendu >= 0 && wendu < 5) {
            if (item_dianya < 3.5) {
                canshu = 0.1f;
            } else if (item_dianya >= 3.5 && item_dianya < 3.65) {
                canshu = 0.05f;
            } else if (item_dianya >= 3.65) {
                canshu = 0f;
            }
        } else if (wendu >= 5 && wendu < 15) {
            if (item_dianya < 3.5) {
                canshu = 0.33f;
            } else if (item_dianya >= 3.5 && item_dianya < 3.55) {
                canshu = 0.2f;
            } else if (item_dianya >= 3.55 && item_dianya < 3.65) {
                canshu = 0.05f;
            } else if (item_dianya >= 3.65) {
                canshu = 0f;
            }
        } else if (wendu >= 15 && wendu < 25) {
            if (item_dianya < 3.5) {
                canshu = 0.5f;
            } else if (item_dianya >= 3.5 && item_dianya < 3.52) {
                canshu = 0.33f;
            } else if (item_dianya >= 3.52 && item_dianya < 3.65) {
                canshu = 0.2f;
            } else if (item_dianya >= 3.65) {
                canshu = 0f;
            }
        } else if (wendu >= 25 && wendu < 45) {
            if (item_dianya < 3.5) {
                canshu = 0.68f;
            } else if (item_dianya >= 3.5 && item_dianya < 3.5) {
                canshu = 0.5f;
            } else if (item_dianya >= 3.5 && item_dianya < 3.52) {
                canshu = 0.3f;
            } else if (item_dianya >= 3.52 && item_dianya < 3.65) {
                canshu = 0.2f;
            } else if (item_dianya >= 3.65) {
                canshu = 0f;
            }
        } else if (wendu >= 45 && wendu < 50) {
            if (item_dianya < 3.5) {
                canshu = 0.5f;
            } else if (item_dianya >= 3.50 && item_dianya < 3.52) {
                canshu = 0.3f;
            } else if (item_dianya >= 3.52 && item_dianya < 3.65) {
                canshu = 0.2f;
            } else if (item_dianya >= 3.65) {
                canshu = 0f;
            }
        } else if (wendu >= 50) {
            if (item_dianya < 3.5) {
                canshu = 0.33f;
            } else if (item_dianya >= 3.50 && item_dianya < 3.65) {
                canshu = 0.2f;
            } else if (item_dianya >= 3.65) {
                canshu = 0f;
            }
        }

        float returnDianliu = 15000 * canshu;
        int returnDianliu_int = (int) returnDianliu;

        System.out.println("电池充电测试 - 电池电压 - "+ item_dianya +" - 电池温度 - "+ wendu + " 参数 - " + canshu);

        return returnDianliu_int;
    }

}
