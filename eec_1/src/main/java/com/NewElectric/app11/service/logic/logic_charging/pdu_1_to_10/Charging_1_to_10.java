package com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_10;


import android.content.Context;

import java.util.Arrays;
import java.util.Calendar;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.agreement.send.pdu.PduSend;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.service.logic.logic_charging.ChargingUtils;

public class Charging_1_to_10 extends ChargingUtils {

    //单例
    private static Charging_1_to_10 instance = new Charging_1_to_10();
    private Charging_1_to_10() {}
    public static Charging_1_to_10 getInstance() {
        return instance;
    }

    private Context context;
    //发送自增的生命帧
    int sendLiveCount = 0;
    //充电参数
    int[] chargingState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1}; // -1 - 初始化    0 - 充电   1 - 关闭      2 - 加热
    int[] chargingDianliu = new int[]{0, 0, 0};
    int[] chargingTarget = new int[]{-1, -1, -1};
    int[] heatState = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1};
    //线程参数
    private int threadCode = 0; // 0 - 初始化  1 - 充电   2 - 充电且加热

    @Override
    public void logicInit(Context context) {
        BaseLogicInit(context);
        setMode(1);
    }

    //设置充电模式
    @Override
    public void setMode(int type) {
        chargingState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1};
        chargingDianliu = new int[]{0, 0, 0};
        chargingTarget = new int[]{-1, -1, -1};
        if (type == 1 && threadCode != 1) {
            threadCode = 1;
            charging();
        }
    }

    @Override
    public void onDestroy() {
        threadCode = 0;
    }

    @Override
    public int[] getChargeStatus() {
        return chargingState;
    }

    @Override
    public void startChange(int door) {
        chargingState[door - 1] = -1;
    }

    @Override
    protected void charging() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    PduSend.closeAllPdu();
                    sleep(10 * 1000);
                    System.out.println("CAN - 充电机 - 模式 - 充电：" + "    1拖10 ");
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
                        int[] isSleepBar = new int[controlPlateInfo.getControlPlateBaseBeans().length];
                        double[] TEM_2 = new double[controlPlateInfo.getControlPlateBaseBeans().length];
                        for (int i = 0; i < controlPlateInfo.getControlPlateBaseBeans().length; i++) {
                            DIANLIU[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryElectric();
                            PERCENtAGES[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryRelativeSurplus();
                            DIANYA[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryVoltage();
                            BIDS[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBID();
                            WENDU[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getBatteryTemperature();
                            TEM_2[i] = controlPlateInfo.getControlPlateBaseBeans()[i].getTemperatureSensor_2();
                        }

                        //0A电池先以2A进行充电
                        for (int i = 0; i < MAX_CABINET_COUNT; i++) {
                            //获取每个电池的电流
                            int dianliu = DIANLIU[i];
                            //如果电池电流小于1A 下发2A电流充电
                            if (dianliu < 1000) {
                                //计算电流
                                int item_dianliu = 2000;
                                item_dianliu = item_dianliu / 100;
                                item_dianliu = 4000 - item_dianliu;
                                int h_dianliu = item_dianliu / 256;
                                int l_dianliu = item_dianliu % 256;
                                //获取电池UID
                                String BidStr = BIDS[i];
                                String TopBidStr = BidStr.substring(0, 1);
                                String MidBidStr = BidStr.substring(9, 10);
                                String EndBidStr = BidStr.substring(10, 12);
                                //目标电池电量下发
                                int tarDianya = DIANYA[i] / 100;
                                int topTarDianya = tarDianya / 256;
                                int endTarDianya = tarDianya % 256;
                                //乐嘉电池只能充电到10A需要做判定
                                String TopLeJiaBIDS = BidStr.substring(0, 7);
                                String endLeJiaBIDS = BidStr.substring(9, 12);
                                //国轩
                                String TopGuoXuanBIDS = BidStr.substring(0, 5);

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
                                } else {
                                    maxVal = 54600;
                                }

                                //目标电压
                                int barVal = DIANYA[i];
                                //读不到电池BID 激活参数是0 关闭充电和加热继电器
                                if ((BIDS[i].equals("0000000000000000") || BIDS[i].equals("FFFFFFFFFFFFFFFF")) && isSleepBar[i] == 1) {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, maxVal, barVal));

                                }
                                //读不到电池BID 激活参数是2 关闭充电和加热继电器
                                else if ((BIDS[i].equals("0000000000000000") || BIDS[i].equals("FFFFFFFFFFFFFFFF")) && isSleepBar[i] == 0) {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.openChargingSwitch(i + 1, maxVal, barVal));
                                }
                                //能读到电池信息 不管是不是加热状态 都关闭加热 打开充电
                                else {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.openChargingSwitch(i + 1, maxVal, barVal));
                                }

                                sleep(50);

                                //设置电压电流
                                int val = 0;
                                if (TopBidStr.equals("M") && EndBidStr.equals("MM")) {
                                    val = 68400;
                                } else if (TopLeJiaBIDS.equals("GBEBCGG") && endLeJiaBIDS.equals("AGG")) {
                                    val = 54600;
                                } else if (TopGuoXuanBIDS.equals("PDLLC")) {
                                    val = 57600;
                                } else if (TopBidStr.equals("N") && EndBidStr.equals("NN")) {
                                    val = 57600;
                                } else if (TopBidStr.equals("R") && EndBidStr.equals("RR")) {
                                    val = 57600;
                                } else if (TopBidStr.equals("P") && EndBidStr.equals("PP")) {
                                    val = 57600;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("C") && EndBidStr.equals("AA")) {
                                    val = 54600;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("E") && EndBidStr.equals("AA")) {
                                    val = 54600;
                                } else {
                                    val = 54600;
                                }
                                if ((BIDS[i].equals("0000000000000000") || BIDS[i].equals("FFFFFFFFFFFFFFFF")) && isSleepBar[i] == 1) {

                                }
                                else if ((BIDS[i].equals("0000000000000000") || BIDS[i].equals("FFFFFFFFFFFFFFFF")) && isSleepBar[i] == 0) {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, 65000, 3900));
                                } else {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, val, item_dianliu));
                                }
                            }
                        }


                        //大于等于1A的电池 下发大电流分配充电
                        //总共有多少块有效电池
                        int bar_count = 0;
                        for (int i = 0; i < BIDS.length; i++) {
                            if (BIDS[i].equals("0000000000000000") || BIDS[i].equals("FFFFFFFFFFFFFFFF") || PERCENtAGES[i] == 100 || DIANLIU[i] < 1000) {
                            } else {
                                bar_count = bar_count + 1;
                            }
                        }

                        //柜子最大可以承受的电流 3000这个值没验证过
                        int max_dianliu = Integer.parseInt(new CabInfoSp(context).getMaxPower()) - 3000;
                        //单个舱门最大电流16A
                        int item_max_dianliu = 15000;
                        int item_dianliu = 0;
                        //求平均的一个值
                        if (bar_count == 0) {
                            item_dianliu = 2000;
                        } else {
                            item_dianliu = max_dianliu / bar_count;
                        }
                        //如果求出来的这个值大于舱门单体充电的最大值 把电流强制到16A
                        if (item_dianliu > item_max_dianliu) {
                            item_dianliu = item_max_dianliu;
                        }

                        item_dianliu = item_dianliu / 100;
                        item_dianliu = 4000 - item_dianliu;
                        int h_dianliu = item_dianliu / 256;
                        int l_dianliu = item_dianliu % 256;
                        //打开需要充电的舱门
                        for (int i = 0; i < 9; i++) {

                            if (DIANLIU[i] >= 1000) {

                                String BidStr = BIDS[i];
                                String TopBidStr = BidStr.substring(0, 1);
                                String MidBidStr = BidStr.substring(9, 10);
                                String EndBidStr = BidStr.substring(10, 12);
                                //目标电池电量下发
                                int tarDianya = DIANYA[i] / 100;
                                int topTarDianya = tarDianya / 256;
                                int endTarDianya = tarDianya % 256;
                                //乐嘉电池只能充电到10A需要做判定
                                String TopLeJiaBIDS = BidStr.substring(0, 7);
                                String endLeJiaBIDS = BidStr.substring(9, 12);
                                //国轩
                                String TopGuoXuanBIDS = BidStr.substring(0, 5);

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
                                } else {
                                    maxVal = 54600;
                                }

                                //目标电压
                                int barVal = DIANYA[i];
                                //读不到电池BID 激活参数是0 关闭充电和加热继电器
                                if ((BIDS[i].equals("0000000000000000") || BIDS[i].equals("FFFFFFFFFFFFFFFF")) && isSleepBar[i] == 1) {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeChargingSwitch(i + 1, maxVal, barVal));

                                }
                                //读不到电池BID 激活参数是2 关闭充电和加热继电器
                                else if ((BIDS[i].equals("0000000000000000") || BIDS[i].equals("FFFFFFFFFFFFFFFF")) && isSleepBar[i] == 0) {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.openChargingSwitch(i + 1, maxVal, barVal));
                                }
                                //能读到电池信息 不管是不是加热状态 都关闭加热 打开充电
                                else {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.openChargingSwitch(i + 1, maxVal, barVal));
                                }

                                sleep(50);

                                //设置电压电流
                                int val = 0;
                                if (TopBidStr.equals("M") && EndBidStr.equals("MM")) {
                                    val = 68400;
                                } else if (TopLeJiaBIDS.equals("GBEBCGG") && endLeJiaBIDS.equals("AGG")) {
                                    val = 54600;
                                } else if (TopGuoXuanBIDS.equals("PDLLC")) {
                                    val = 57600;
                                } else if (TopBidStr.equals("N") && EndBidStr.equals("NN")) {
                                    val = 57600;
                                } else if (TopBidStr.equals("R") && EndBidStr.equals("RR")) {
                                    val = 57600;
                                } else if (TopBidStr.equals("P") && EndBidStr.equals("PP")) {
                                    val = 57600;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("C") && EndBidStr.equals("AA")) {
                                    val = 54600;
                                } else if (TopBidStr.equals("N") && MidBidStr.equals("E") && EndBidStr.equals("AA")) {
                                    val = 54600;
                                } else {
                                    val = 54600;
                                }
                                if ((BIDS[i].equals("0000000000000000") || BIDS[i].equals("FFFFFFFFFFFFFFFF")) && isSleepBar[i] == 1) {

                                }
                                else if ((BIDS[i].equals("0000000000000000") || BIDS[i].equals("FFFFFFFFFFFFFFFF")) && isSleepBar[i] == 0) {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, 65000, 3900));
                                } else {
                                    MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter(i + 1, val, item_dianliu));
                                }
                                
                                sleep(50);
                            }
                          
                        }


                        //计算充电继电器的二进制参数 （1，2，4，8，16，32，64，128，256）
                        int headingCount = 0;
                        for (int i = 0; i < BIDS.length; i++) {
                            if (!BIDS[i].equals("0000000000000000") && !BIDS[i].equals("FFFFFFFFFFFFFFFF") && WENDU[i] > 7 && TEM_2[i] < 70 && WENDU[i] != -40 && WENDU[i] != -60) {
                                heatState[i] = 1;
                            } else if (!BIDS[i].equals("0000000000000000") && !BIDS[i].equals("FFFFFFFFFFFFFFFF") && WENDU[i] < 6 && TEM_2[i] < 70 && WENDU[i] != -40 && WENDU[i] != -60) {
                                headingCount = headingCount + (int) Math.pow(2, i);
                                heatState[i] = 2;
                            } else if (!BIDS[i].equals("0000000000000000") && !BIDS[i].equals("FFFFFFFFFFFFFFFF") && WENDU[i] >= 6 && WENDU[i] <= 7 && TEM_2[i] < 70 && WENDU[i] != -40 && WENDU[i] != -60) {
                                if (heatState[i] == 2) {
                                    headingCount = headingCount + (int) Math.pow(2, i);
                                } else if (heatState[i] == 1) {

                                }
                            }
                        }
                        System.out.println("CAN - 充电机 - 下发：" + "   帧ID：98001e00" + "   帧数据： " + headingCount);
                        if (headingCount == 0) {
                            //下发关闭加热继电器
                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.closeHeatingSwitch_1_to_10());
                            sleep(50);
                            //下发最后加热充电器的电压电流
                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter_1_to_10(41000 , 0));
                            sleep(50);
                        } else {
                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.openHeatingSwitch_1_to_10(headingCount));
                            sleep(50);
                            //下发最后加热充电器的电压电流
                            MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.setParameter_1_to_10(41000 , 3800));
                            sleep(50);
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

    @Override
    protected void chargingAndHeating() {
        charging();
    }
}
