package com.NewElectric.app11.service.logic.logic_exchange.cabinetOfPdu;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateBaseBean;
import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.model.dao.fileSave.LocalLog;
import com.NewElectric.app11.model.dao.sharedPreferences.ForbiddenSp;
import com.NewElectric.app11.model.dao.sqlLite.ExchangeInfoDB;
import com.NewElectric.app11.model.dao.sqlLite.OutLineExchangeSaveInfo;
import com.NewElectric.app11.service.BaseLogic;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateService;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateServiceInfoBeanFormat;
import com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_3.BatteryActivation;
import com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_3.Charging_1_to_3;
import com.NewElectric.app11.service.logic.logic_exchange.UidDictionart;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttp;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttpParameterFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnectDialogFormat;
import com.NewElectric.app11.service.logic.logic_writeUid.WriteUid;
import com.NewElectric.app11.units.Units;

/**
 * 离线换电
 */

public class ExchangeBarOutLine extends BaseLogic {

    //单例
    private static ExchangeBarOutLine instance = new ExchangeBarOutLine();

    private ExchangeBarOutLine() {
    }

    public static ExchangeBarOutLine getInstance() {
        return instance;
    }

    //上下文
    private Context context;
    //信号值（信号值不正常 直接离线换电）
    private int dbm;
    //上次换电UID
    private static String lastExchangeUID = "FFFFFFFF";
    //上次换电舱门号
    private static int lastExchangeDoor = -1;
    //上次换电的时间
    private static long lastExchangeTime = 0;
    //电池激活参数
    private int isSleepBar[] = new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1};   // -1 - 初始化   0 - 需要唤醒   1 - 不需要唤醒
    //电池微动1按压时间记录
    private long inching_1_pressTimes[] = new long[]{0,0,0,0,0,0,0,0,0};
    //电池BID触发变化时间记录
    private long bidChangeTimes[] = new long[]{0,0,0,0,0,0,0,0,0};
    //电池BID触发变化电池ID记录
    private String bidChangeBids[] = new String[]{"","","","","","","","",""};

    //数据监听
    @Override
    public void batteryBaseBeanReturn(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat) {
        //获取信息
        int index = canControlPlateServiceInfoBeanFormat.getIndex();
        //新控制板数据
        ControlPlateBaseBean controlPlateBaseBeanNew = (ControlPlateBaseBean) canControlPlateServiceInfoBeanFormat.getData();
        //信息对比
        onStart(index, controlPlateBaseBeanNew);
        //更新内存数据
        controlPlateInfo.getControlPlateBaseBeans()[index] = controlPlateBaseBeanNew;
    }

    //事件分发
    private void sendData(LongLinkConnectDialogFormat longLinkConnectDialogFormat) {
        setChanged();
        notifyObservers(new DataFormat<>("showDialog", longLinkConnectDialogFormat));
    }

    private void sendDataUpdateBatteryUI(int door) {
        setChanged();
        notifyObservers(new DataFormat<>("updateBatteryUI", door));
    }

    private void sendInchingAnimation(int door) {
        setChanged();
        notifyObservers(new DataFormat<>("inchingAnimation", door));
    }

    private void sendDataAnimation(ExchangeBarOutLineStartAnimation exchangeBarOutLineStartAnimation) {
        setChanged();
        notifyObservers(new DataFormat<>("exchangeAnimation", exchangeBarOutLineStartAnimation));
    }


    @Override
    public void dbmReturn(int dbm) {
        super.dbmReturn(dbm);
        this.dbm = dbm;
    }

    //结束
    private void errorReturn() {
        CanControlPlateService.getInstance().cancelHangUpAllDoor();
    }

    //初始化
    public void ExchangeBarInit(Context context) {
        this.context = context;
        BaseLogicInit(context);
    }

    //开始进行对比触发
    public void onStart(int index, ControlPlateBaseBean controlPlateBaseBeanNew) {
        //换电参数的参数
        String newBID = controlPlateBaseBeanNew.getBID();
        int newInching_1 = controlPlateBaseBeanNew.getInching_1();
        int newInching_3 = controlPlateBaseBeanNew.getInching_3();
        int newSOC = controlPlateBaseBeanNew.getBatteryRelativeSurplus();
        ControlPlateBaseBean controlPlateBaseBeanOld = controlPlateInfo.getControlPlateBaseBeans()[index];
        String oldBID = controlPlateBaseBeanOld.getBID();
        int oldInching_1 = controlPlateBaseBeanOld.getInching_1();
        int oldInching_3 = controlPlateBaseBeanOld.getInching_3();
        int oldSOC = controlPlateBaseBeanOld.getBatteryRelativeSurplus();

        //如果不符合地址 取消
        if (index >= MAX_CABINET_COUNT) {
            return;
        }
        //设置电池激活参数 如果不为空 就设置这个电池已经唤醒    1 - 不用唤醒   0 - 需要唤醒的先决条件             并且如果读到电池BID之后 再进行读取电池UID
        //UID参数比较特殊 是参与换电的必要参数 所以读到电池BID之后再继续获取
        if (!newBID.equals("FFFFFFFFFFFFFFFF") && !newBID.equals("0000000000000000")) {
            isSleepBar[index] = 1;
        } else {
            isSleepBar[index] = 0;
        }
        // 检测到电池的提示
        if (oldInching_1 == 0 && newInching_1 == 1) {
            inching_1_pressTimes[index]= System.currentTimeMillis();
            isSleepBar[index] = 0;
            sendInchingAnimation(index+1);
            sendData(new LongLinkConnectDialogFormat("正在检测" + (index + 1) + "号仓电池，请稍候!", 20, 1));
            //检测到两个微动状态 说明是我们的电池 先给他充电一分钟
            //TODO::激活问题
            Thread thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    for (int i = 0; i < 20; i++) {
                        System.out.println("电池：检测中-倒计时" + (20 - i));
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (isSleepBar[index] == 1) {
                            System.out.println("电池：检测到电池");
                            break;
                        }
                    }

                    if (isSleepBar[index] == 0) {
                        if (newInching_1 == 1 && newInching_3 == 1) {

                            System.out.println("电池：电池休眠");
                            sendData(new LongLinkConnectDialogFormat("正在尝试重新连接电池，请稍候！", 15, 1));
                            new BatteryActivation(index + 1).onStart();
                            inching_1_pressTimes[index]= System.currentTimeMillis();

                            for (int i = 0; i < 30; i++) {
                                System.out.println("电池：已经打开充电激活 检测中-倒计时" + (30 - i));
                                try {
                                    sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (isSleepBar[index] == 1) {
                                    break;
                                }
                            }
                            if (isSleepBar[index] == 0 && newBID.equals("0000000000000000")) {
                                if (newInching_1 == 1 && newInching_3 == 1) {
                                    if (forbiddenSp.getTargetForbidden(index) != 1) {
                                        sendData(new LongLinkConnectDialogFormat("未能检测到电池，舱门已被禁用，电池将被回收！", 10, 1));
                                    } else {
                                        sendData(new LongLinkConnectDialogFormat("未能检测到电池，请您重新插拔电池尝试再次换电！", 10, 1));
                                        pushAndPull(index + 1, "未能检测到电池，请您重新插拔电池尝试再次换电");
                                    }
                                }
                            } else {
                                System.out.println("电池：不需要检测是否弹出不识别电池 已检测到电池");
                            }
                        }
                        System.out.println("电池：正在尝试重新连接电池 - newInching_1" + newInching_1 + " - newInching_3 - " + newInching_3);
                    } else {
                        System.out.println("电池：不需打开激活指令 已检测到电池");
                    }
                }
            };
            thread.start();
        }

        //如果新老BID不相同 && 老BID不等于16个F && 新BID不等于16个0 && 新BID不等于16个F
        if (!newBID.equals(oldBID) && !oldBID.equals("FFFFFFFFFFFFFFFF") && !newBID.equals("0000000000000000") && !newBID.equals("FFFFFFFFFFFFFFFF")) {
            //检测单仓重复换电(电池相同 且在15秒内触发)
            if(System.currentTimeMillis() -  bidChangeTimes[index] < 15 * 1000 && newBID.equals(bidChangeBids[index])){
                writeLog("检测到单仓重复换电 换电被驳回 - 老电池ID - " + oldBID + " - 新电池ID - " + newBID);
            }else{
                //挂起除了这个仓的所有舱门
                for (int i = 0; i < MAX_CABINET_COUNT; i++) {
                    if (i != index) {
                        CanControlPlateService.getInstance().hangUpDoor(i + 1, 60);
                    }
                }
                //触发换电
                hasTrigger(index + 1);
                //记录触发事件
                bidChangeTimes[index] = System.currentTimeMillis();
                bidChangeBids[index] = newBID;
                //写日志
                writeLog("进入换电流程 - 老电池ID - " + oldBID + " - 新电池ID - " + newBID);
            }
        }
    }


    private void hasTrigger(int inputBarDoor) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    int is_stop = 70;
                    sleep(2000);
                    while (is_stop > 0) {   // is_stop = 0 为自然 --1 的超时结束进程      is_stop = -1 为强制赋值的强制结束进程
                        sleep(100);
                        //换电流程 符合逻辑开始换电
                        //获取舱门信息
                        ControlPlateBaseBean controlPlateBaseBean = controlPlateInfo.getControlPlateBaseBeans()[inputBarDoor - 1];
                        String BID = controlPlateBaseBean.getBID();
                        String UID = controlPlateBaseBean.getUID();
                        int SOC = controlPlateBaseBean.getBatteryRelativeSurplus();
                        int inching_1 = controlPlateBaseBean.getInching_1();
                        long inching_1_pressTime = inching_1_pressTimes[inputBarDoor - 1];
                        long diffTime = System.currentTimeMillis() - inching_1_pressTime;
                        if(is_stop % 10 == 0){
                            writeLog("换电流程 - 开始换电   舱门 - " + inputBarDoor + "   电池ID - " + BID + "   电池UID - " + UID + "   电量 - " + SOC + "   微动状态 - " + inching_1 + "   间隔时间 - " + diffTime);
                        }
                        //电池BID不为16个0 && 电池电量不为0 && 电池微动压死 && 电池UID不为6个0 && 微动1压死时间在半分钟内
                        if (!BID.equals("0000000000000000") && SOC != 0 && inching_1 == 1 && !UID.equals("00000000") && (diffTime < 30 * 1000)) {
                            //提示正在检测电池
                            sendData(new LongLinkConnectDialogFormat("正在校验" + inputBarDoor + "号仓电池信息，请稍候！！", 60, 1));
                            //电池UID为8个A 说明电池未绑定 需要走转换接口
                            if (UID.equals("AAAAAAAA")) {
                                List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                                baseHttpParameterFormats.add(new BaseHttpParameterFormat("battery", BID));
                                baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", cabInfoSp.getCabinetNumber_4600XXXX()));
                                baseHttpParameterFormats.add(new BaseHttpParameterFormat("door", inputBarDoor + ""));
                                BaseHttp baseHttp = new BaseHttp(HttpUrlMap.GetUID, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                                    @Override
                                    public void dataReturn(int code, String message, String data) {

                                        if (code == -1 || code == 0) {
                                            sendData(new LongLinkConnectDialogFormat("该电池未绑定，将被回收，如有问题请拨打电话客服！", 10, 1));
                                            errorReturn();
                                        } else if (code == 1 || code == 2) {
                                            try {
                                                final JSONObject jsonObject = new JSONObject(data);
                                                String uid32 = jsonObject.getString("uid32");
                                                WriteUid.getInstance().write(inputBarDoor, uid32, "UID转化", new WriteUid.WriteUidListener() {
                                                    @Override
                                                    public void showDialog(String message, int time, int type) {
                                                        sendData(new LongLinkConnectDialogFormat(message, 10, 1));
                                                    }

                                                    @Override
                                                    public void writeUidResult(boolean result) {
                                                        if(result){
                                                            checkUserBalance(inputBarDoor);
                                                        }else {
                                                            sendData(new LongLinkConnectDialogFormat("电池写入失败，如有问题请拨打电话客服！", 10, 1));
                                                            errorReturn();
                                                        }
                                                    }
                                                });
                                            } catch (Exception e) {
                                                sendData(new LongLinkConnectDialogFormat("网络请求失败，如有问题请拨打电话客服！", 10, 1));
                                                errorReturn();
                                            }
                                        } else {
                                            sendData(new LongLinkConnectDialogFormat("网络请求失败，如有问题请拨打电话客服！", 10, 1));
                                            errorReturn();
                                        }
                                    }
                                });
                                baseHttp.onStart();
                            }
                            //电池UID为正产UID 说明电池已经绑定 直接交换ID
                            else {
                                //判断上一块儿电池的UID和这次换电的UID是否相等 如果相等的话并且换电间隔小于60S的话 不让换电
                                long diff = (System.currentTimeMillis() - lastExchangeTime) / 1000;
                                writeLog("网络：   上次UID - " + lastExchangeUID + "   现在UID - " + UID + "   相差时间 - " + diff + "秒");
                                String phone = UidDictionart.getI10PhoneNumber(UID);
                                if (UID.equals(lastExchangeUID) && diff < 60 && !phone.equals("18611992352") && inputBarDoor == lastExchangeDoor) {
                                    pushAndPull(inputBarDoor, "检测到重复换电，弹出电池");
                                    sendData(new LongLinkConnectDialogFormat("操作太快，请取出电池1分钟后再次换电！", 50, 1));
                                } else {
                                    checkUserBalance(inputBarDoor);
                                }
                            }
                            //换电时间初始化
                            is_stop = -1;
                        }
                        is_stop = is_stop - 1;
                    }
                    if (is_stop == 0) {
                        //等待超时 做后续操作
                        ControlPlateBaseBean controlPlateBaseBean = controlPlateInfo.getControlPlateBaseBeans()[inputBarDoor - 1];
                        String BID = controlPlateBaseBean.getBID();
                        String UID = controlPlateBaseBean.getUID();
                        int SOC = controlPlateBaseBean.getBatteryRelativeSurplus();
                        int inching_1 = controlPlateBaseBean.getInching_1();
                        long diffTime = System.currentTimeMillis() - inching_1_pressTimes[inputBarDoor - 1];
                        writeLog("换电流程 - 换电失败   舱门 - " + inputBarDoor + "   电池ID - " + BID + "   电池UID - " + UID + "   电量 - " + SOC + "   微动信息 - " + inching_1 + "   微动时间间隔 - " + diffTime);
                        //指标都是正常的 就是底部微动没有压死 就弹出电池
                        if (!BID.equals("0000000000000000") && !UID.equals("AAAAAAAA") && !UID.equals("00000000") && SOC != 0 && inching_1 == 0) {
                            pushAndPull(inputBarDoor, "等待超时，电池未锁上，检测到有测微动，弹出电池！");
                            sendData(new LongLinkConnectDialogFormat("电池未锁上，请取出 " + inputBarDoor + " 号舱门电池，请重试！", 20, 1));
                        }
                        //指标都是正常的 底部微动也压死了 但是没有检测到30秒的一个状态
                        else if (!BID.equals("0000000000000000") && !UID.equals("AAAAAAAA") && !UID.equals("00000000") && SOC != 0 && inching_1 == 1 && diffTime > 30 * 1000) {
                            writeLog("换电流程 - 换电失败   此次换电微动未在30秒内触发，为不安全换电，电池锁定");
                            sendData(new LongLinkConnectDialogFormat("电池校验异常，将被回收，如有问题请联系电话客服！！", 20, 1));
                            errorReturn();
                        } else if (UID.equals("00000000")) {
                            writeLog("换电流程 - 换电失败  未读到电池UID，电池锁定");
                            sendData(new LongLinkConnectDialogFormat("未读到" + inputBarDoor + "号仓电池绑定信息，电池将被回收，如有问题请联系电话客服！", 20, 1));
                            errorReturn();
                        } else {
                            writeLog("换电流程 - 换电失败  未知原因（多原因导致）");
                            sendData(new LongLinkConnectDialogFormat("电池校验异常，将被回收，如有问题请联系电话客服！！", 20, 1));
                            errorReturn();
                        }

                    }
                } catch (Exception e) {
                    System.out.println("换电流程 - 换电时间 - " + e.toString());
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取用户换电信息 查看是否满足换电
     *
     * @param inputBarDoor
     */
    private void checkUserBalance(int inputBarDoor) {
        ControlPlateBaseBean controlPlateBaseBean = controlPlateInfo.getControlPlateBaseBeans()[inputBarDoor - 1];
        String BID = controlPlateBaseBean.getBID();
        String UID = controlPlateBaseBean.getUID();
        int SOC = controlPlateBaseBean.getBatteryRelativeSurplus();
        lastExchangeTime = System.currentTimeMillis();
        lastExchangeUID = UID;
        lastExchangeDoor = inputBarDoor;
        if (dbm > 50 || dbm < -125) {
            exchangeUid(inputBarDoor, UID, "离线换电");
        } else {
            List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
            baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", cabInfoSp.getCabinetNumber_4600XXXX()));
            baseHttpParameterFormats.add(new BaseHttpParameterFormat("uid32", UID));
            baseHttpParameterFormats.add(new BaseHttpParameterFormat("in_battery", BID));
            baseHttpParameterFormats.add(new BaseHttpParameterFormat("in_electric", SOC + ""));
            baseHttpParameterFormats.add(new BaseHttpParameterFormat("in_door", inputBarDoor + ""));
            BaseHttp baseHttp = new BaseHttp(HttpUrlMap.GetUserInfo, baseHttpParameterFormats , 3 , new BaseHttp.BaseHttpListener() {
                @Override
                public void dataReturn(int code, String message, String data) {

                    LocalLog.getInstance().writeLog("网络返回 - code - " + code + " - message - " + message + " - data - " + data);

                    try {
                        if (code == -1) {  //开启离线换电
                            exchangeUid(inputBarDoor, UID, "离线换电");
                        } else if (code == 0) {
                            JSONObject jsonObject = new JSONObject(data);
                            String show = jsonObject.getString("show");
                            String errno = jsonObject.getString("errno");
                            //吞电池
                            if (errno.equals("E2001")) {
                                //未绑定的电池 吞电池
                                if (show.equals("1")) {
                                    sendData(new LongLinkConnectDialogFormat(message, 20, 1));
                                }
                                Charging_1_to_3.getInstance().startChange(inputBarDoor);
                                errorReturn();
                                System.out.println("网络：   后台禁止换电 - 吞电池");
                            }
                            //弹出插入的电池
                            else if (errno.equals("E1001")) {
                                if (show.equals("1")) {
                                    sendData(new LongLinkConnectDialogFormat(message, 20, 1));
                                }
                                //根据返回的错误码 未知原因 吐出原电池
                                pushAndPull(inputBarDoor, "错误码弹出电池：E1001 - " + message);
                            }
                        } else if (code == 1) {
                            JSONObject jsonObject = new JSONObject(data);
                            String uid32 = jsonObject.getString("uid32");
                            String utype = jsonObject.getString("utype");
                            exchangeUid(inputBarDoor, uid32, utype);
                        }
                    } catch (Exception e) {
                        writeLog("json解析失败! - " + e.toString());
                        errorReturn();
                        exchangeUid(inputBarDoor, UID, "离线换电");
                    }
                }
            });
            baseHttp.onStart();
        }
    }


    /**
     * 换电是 交换UID
     *
     * @param door  插入电池
     * @param uid32 写入的UID
     */
    private void exchangeUid(int door, String uid32, final String type) {

        ControlPlateBaseBean inControlPlateBaseBean = controlPlateInfo.getControlPlateBaseBeans()[door - 1];
        String inBID = inControlPlateBaseBean.getBID();
        String inUID = inControlPlateBaseBean.getUID();
        int inSOC = inControlPlateBaseBean.getBatteryRelativeSurplus();

        if (door >= 0) {
            //判断插入电池的类型
            String in_volt = Units.bar_60_or_48(inBID);
            //找出电量最高的电池 并且还得是符合标准的 比如 48v和60v的
            int outDoorBarPerMax = -1;
            int outDoorBarIndex = -1;
            for (int i = 0; i < MAX_CABINET_COUNT; i++) {
                ControlPlateBaseBean itemControlPlateBaseBean = controlPlateInfo.getControlPlateBaseBeans()[i];
                String itemUID = itemControlPlateBaseBean.getUID();
                int itemSOC = itemControlPlateBaseBean.getBatteryRelativeSurplus();
                String volt = Units.bar_60_or_48(itemControlPlateBaseBean.getBID());
                int is_stop = new ForbiddenSp(context).getTargetForbidden(i);
                //电量最高 && 电池类型相同 && UID必须是8个A && 不是禁用状态 && 不能是插入仓
                if (itemSOC > outDoorBarPerMax && in_volt.equals(volt) && itemUID.equals("AAAAAAAA") && is_stop == 1 && i != door - 1) {
                    outDoorBarPerMax = itemSOC;
                    outDoorBarIndex = i;
                }
            }
            //特殊账号
            String phone = UidDictionart.getI10PhoneNumber(uid32);
            if (!phone.equals("18611992352")) {
                if (outDoorBarIndex != -1 && controlPlateInfo.getControlPlateBaseBeans()[outDoorBarIndex].getBatteryRelativeSurplus() < controlPlateInfo.getControlPlateBaseBeans()[door - 1].getBatteryRelativeSurplus()) {
                    outDoorBarIndex = -2;
                }
            }

            //没有找到合适的电池
            if (outDoorBarIndex == -1) {
                sendData(new LongLinkConnectDialogFormat("没有符合标准的电池，换电结束！", 20, 1));
                pushAndPull(door, "没有可以选择的电池，换电结束");
                return;
            } else if (outDoorBarIndex == -2) {
                sendData(new LongLinkConnectDialogFormat("您的电池电量高于当前电柜最大值，无需换电！", 20, 1));
                pushAndPull(door, "没有可以选择的电池，换电结束");
                return;
            } else if (outDoorBarIndex == -3) {
                sendData(new LongLinkConnectDialogFormat(door + "号仓电池未绑定，将被回收，如有问题请拨打电话咨询！", 20, 1));
                errorReturn();
                return;

            } else {
                //获取要弹出舱门的信息
                ControlPlateBaseBean outControlPlateBaseBean = controlPlateInfo.getControlPlateBaseBeans()[outDoorBarIndex];
                String outBID = outControlPlateBaseBean.getBID();
                int outSOC = outControlPlateBaseBean.getBatteryRelativeSurplus();
                final int fOutDoorBarIndex = outDoorBarIndex;

                WriteUid.getInstance().write(outDoorBarIndex + 1, uid32, "写入UID到新电池", new WriteUid.WriteUidListener() {
                    @Override
                    public void showDialog(String message, int time, int type) {
                        sendData(new LongLinkConnectDialogFormat(message, 20, 1));
                    }

                    @Override
                    public void writeUidResult(boolean result) {
                        if(result){
                            pushOutBattery(fOutDoorBarIndex + 1, uid32, inSOC, outSOC, type);
                            WriteUid.getInstance().write(door, "AAAAAAAA", "清空插入电池的UID", new WriteUid.WriteUidListener() {
                                @Override
                                public void showDialog(String message, int time, int type) {
                                    sendData(new LongLinkConnectDialogFormat(message, time, type));
                                }

                                @Override
                                public void writeUidResult(boolean result) {
                                    if(result){
                                        OutLineExchangeSaveInfo outLineExchangeSaveInfo = new OutLineExchangeSaveInfo(cabInfoSp.getCabinetNumber_4600XXXX(), uid32, System.currentTimeMillis() + "", inBID, door + "", inSOC + "", outBID + "", (fOutDoorBarIndex + 1) + "", outSOC + "");
                                        ExchangeInfoDB.getInstance(context).insertData(outLineExchangeSaveInfo);
                                    }else {
                                        new ForbiddenSp(context).setTargetForbidden(door - 1, -3);
                                        sendDataUpdateBatteryUI(door);
                                        pushOutBattery(fOutDoorBarIndex + 1, uid32, inSOC, outSOC, type);
                                        uploadWriteFail(fOutDoorBarIndex + 1, outBID, "AAAAAAAA");
                                        OutLineExchangeSaveInfo outLineExchangeSaveInfo = new OutLineExchangeSaveInfo(cabInfoSp.getCabinetNumber_4600XXXX(), uid32, System.currentTimeMillis() + "", inBID, door + "", inSOC + "", outBID + "", (fOutDoorBarIndex + 1) + "", outSOC + "");
                                        ExchangeInfoDB.getInstance(context).insertData(outLineExchangeSaveInfo);
                                    }
                                }
                            });
                        }else {
                            sendData(new LongLinkConnectDialogFormat("换电失败，正在弹出电池，请重试！，如有问题请联系电话客服！！", 20, 1));
                            pushAndPull(door, "写入要弹出的电池ID失败，弹出原本插入的电池！");
                            uploadWriteFail(door, inBID, uid32);
                        }
                    }
                });
            }
        } else {
            sendData(new LongLinkConnectDialogFormat("非法操作，换电结束！", 20, 1));
            errorReturn();
            return;
        }
    }

    //弹出电池
    private void pushOutBattery(int outDoor, String uid, int inElectric, int outElectric, String type) {
        pushAndPull(outDoor, "正常换电");
        String tel = UidDictionart.getI10EndPhoneNumber(uid);
        sendData(new LongLinkConnectDialogFormat("请手机尾号" + tel + "的用户拿走第" + (outDoor) + "号舱门电池", 20, 1));
        //开启动画
        sendDataAnimation(new ExchangeBarOutLineStartAnimation(inElectric, outElectric, type));
    }

    //电池写入失败上传网络
    private void uploadWriteFail(int door, String BID, String UID) {
        JSONObject errorJson = new JSONObject();
        try {
            errorJson.put("door", door);
            errorJson.put("uid32", UID);
            errorJson.put("battery", BID);
            errorJson.put("extime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", cabInfoSp.getCabinetNumber_4600XXXX()));
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("type", "10"));
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("error", errorJson.toString()));
        BaseHttp baseHttp = new BaseHttp(HttpUrlMap.UploadWriteUidFail, baseHttpParameterFormats);
        baseHttp.onStart();
    }


}
