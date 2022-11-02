package com.NewElectric.app11.service.canAndSerial.can.controlPlate;

import java.util.Arrays;
import java.util.Observable;

import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateBaseBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateInfo;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateReceiveEncap;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduChargingInfoBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduEncap;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduEquipmentBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduInfo;
import com.NewElectric.app11.hardwarecomm.androidHard.CanDataFormat;
import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.service.canAndSerial.can.BaseCanService;
import com.NewElectric.app11.units.Units;

public class CanControlPlateService extends BaseCanService {

    //单例
    private static CanControlPlateService instance = new CanControlPlateService();
    private CanControlPlateService() {}
    public static CanControlPlateService getInstance() {
        return instance;
    }

    //控制板拨码地址从5开始 计算从0开始 5就是偏移量
    private int CONTROL_PLATE_ADDRESS_DEVIATION = 5;
    //pdu充电模块儿地址偏移量
    private static int ADDRESS_DEVIATION = 18;
    //控制板基础信息数据阵列 处理缓存以及拼装数据
    private byte[][][] controlPlateBaseArrays = new byte[12][17][8];
    //控制板预警信息数据阵列 处理缓存以及拼装数据
    private byte[][][] controlPlateWaringArrays = new byte[12][4][8];
    //充电机当前状态
    private PduChargingInfoBean[] pduChargingInfoBeans = new PduChargingInfoBean[9];
    //设备实时信息
    private PduEquipmentBean[] pduEquipmentBeans = new PduEquipmentBean[32];
    //上帧数据信息缓存
    private ControlPlateBaseBean[] lastControlPlateBaseBeans = new ControlPlateBaseBean[32];
    //缓存数据（储存真实的数据 不存在因为挂起覆盖空数据）
    private ControlPlateInfo controlPlateInfo;
    private PduInfo pduInfo;

    private void sendData(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat) {
        setChanged();
        notifyObservers(new DataFormat<>("batteryBaseBean", canControlPlateServiceInfoBeanFormat));
    }

    private void sendData(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat, int i) {
        setChanged();
        notifyObservers(new DataFormat<>("batteryWarningBean", canControlPlateServiceInfoBeanFormat));
    }

    private void sendData(CanControlPlateServicePduInfoBeanFormat canControlPlateServicePduInfoBeanFormat) {
        setChanged();
        notifyObservers(new DataFormat<>("pduBean", canControlPlateServicePduInfoBeanFormat));
    }

    //初始化复写
    @Override
    public void canInit() {
        super.canInit();
        Arrays.fill(controlPlateBaseArrays, initArrays(17));
        Arrays.fill(controlPlateWaringArrays, initArrays(4));
        Arrays.fill(pduChargingInfoBeans, new PduChargingInfoBean());
        Arrays.fill(pduEquipmentBeans, new PduEquipmentBean());
        Arrays.fill(lastControlPlateBaseBeans, new ControlPlateBaseBean());
        controlPlateInfo = new ControlPlateInfo();
        pduInfo = new PduInfo();
    }

    @Override
    public void update(Observable observable, Object object) {
        DataFormat dataFormat = (DataFormat) object;
        if (dataFormat.getType().equals("can")) {
            CanDataFormat canDataFormat = new CanDataFormat((byte[]) dataFormat.getData());
            //can报文地址
            long canAddress = canDataFormat.getAddressByLong();
            //can报文数据
            byte[] canData = canDataFormat.getData();
            //控制板拨码地址从5开始
            if (canAddress < 17 && canAddress > 4) {
                //控制板相对地址
                int controlPlateIndex = (int) canAddress - CONTROL_PLATE_ADDRESS_DEVIATION;
                //控制板数据拼接地址 如 - 16，32，33，34 .....
                int controlPlateDataItemIndex = canData[0];
                //数据进行缓存
                if (controlPlateDataItemIndex == 16) {
                    controlPlateBaseArrays[controlPlateIndex][0] = canData;
                } else if (controlPlateDataItemIndex == 32) {
                    controlPlateBaseArrays[controlPlateIndex][1] = canData;
                } else if (controlPlateDataItemIndex == 33) {
                    controlPlateBaseArrays[controlPlateIndex][2] = canData;
                } else if (controlPlateDataItemIndex == 34) {
                    controlPlateBaseArrays[controlPlateIndex][3] = canData;
                } else if (controlPlateDataItemIndex == 35) {
                    controlPlateBaseArrays[controlPlateIndex][4] = canData;
                } else if (controlPlateDataItemIndex == 36) {
                    controlPlateBaseArrays[controlPlateIndex][5] = canData;
                } else if (controlPlateDataItemIndex == 37) {
                    controlPlateBaseArrays[controlPlateIndex][6] = canData;
                } else if (controlPlateDataItemIndex == 38) {
                    controlPlateBaseArrays[controlPlateIndex][7] = canData;
                } else if (controlPlateDataItemIndex == 39) {
                    controlPlateBaseArrays[controlPlateIndex][8] = canData;
                } else if (controlPlateDataItemIndex == 40) {
                    controlPlateBaseArrays[controlPlateIndex][9] = canData;
                } else if (controlPlateDataItemIndex == 41) {
                    controlPlateBaseArrays[controlPlateIndex][10] = canData;
                } else if (controlPlateDataItemIndex == 42) {
                    controlPlateBaseArrays[controlPlateIndex][11] = canData;
                } else if (controlPlateDataItemIndex == 43) {
                    controlPlateBaseArrays[controlPlateIndex][12] = canData;
                } else if (controlPlateDataItemIndex == 44) {
                    controlPlateBaseArrays[controlPlateIndex][13] = canData;
                } else if (controlPlateDataItemIndex == 45) {
                    controlPlateBaseArrays[controlPlateIndex][14] = canData;
                } else if (controlPlateDataItemIndex == 46) {
                    controlPlateBaseArrays[controlPlateIndex][15] = canData;
                } else if (controlPlateDataItemIndex == 47) {
                    //判断接收到2c 证明一个帧的报文发送完毕了  取出并拼装所有报文并清空  得先判断里面有没有空值 如果有空值的话 证明报文不完整 废弃掉
                    controlPlateBaseArrays[controlPlateIndex][16] = canData;
                    //建立临时数组
                    byte[][] tempArrays = controlPlateBaseArrays[controlPlateIndex];
                    //先判断有没有空值
                    int isHaveNull = 0;
                    for (int i = 0; i < tempArrays.length; i++) {
                        if (tempArrays[i][0] == 0) {
                            isHaveNull = 1;
                            break;
                        }
                    }
                    //没有空值
                    if (isHaveNull == 0) {
                        byte[] returnOrder = new byte[119];
                        for (int i = 0; i < tempArrays.length; i++) {
                            for (int j = 0; j < tempArrays[i].length - 1; j++) {
                                returnOrder[(i * 7) + j] = tempArrays[i][j + 1];
                            }
                        }
                        //数据帧地址相对地址
                        returnOrder[0] = (byte) (returnOrder[0] - 4);
                        //数据校验
                        if (controlPlateIndex == returnOrder[0] - 1) {
                            //解析数据
                            ControlPlateBaseBean controlPlateBaseBean = new ControlPlateReceiveEncap().returnBaseByBytes(returnOrder);
                            //如果现在的时间小于挂起的时候 返回什么值都不做分发任何更新 维持现在的数据    （只更新缓存 不分发给任务）
                            if (System.currentTimeMillis() < hangUpTimes[controlPlateIndex]) {
                                controlPlateInfo.setControlPlateBaseBean(controlPlateIndex, controlPlateBaseBean);
                            }
                            //如果现在的时间小于清除的时间 返回什么值都不管 只有返回初始值就行   （更新缓存 分发空仓数据给任务）
                            else if (System.currentTimeMillis() < cleanUpTimes[controlPlateIndex]) {
                                ControlPlateBaseBean controlPlateBaseBeanNull = new ControlPlateBaseBean();
                                sendData(new CanControlPlateServiceInfoBeanFormat(controlPlateIndex, controlPlateBaseBeanNull));
                                controlPlateInfo.setControlPlateBaseBean(controlPlateIndex, controlPlateBaseBeanNull);
                            }
                            //更新数据
                            else {
                                if(controlPlateBaseBean.getUID().equals("00000000") && controlPlateBaseBean.getBID().equals("0000000000000000") && !lastControlPlateBaseBeans[controlPlateIndex].getUID().equals("00000000") && !lastControlPlateBaseBeans[controlPlateIndex].getBID().equals("0000000000000000")){
                                    //如果老数据有效 新数据无效都是0 说明途中蹦出来了一个干扰 丢弃数据
                                }else{
                                    controlPlateInfo.setControlPlateBaseBean(controlPlateIndex, controlPlateBaseBean);
                                    sendData(new CanControlPlateServiceInfoBeanFormat(controlPlateIndex, controlPlateBaseBean));
                                }
                                //缓存老数据
                                lastControlPlateBaseBeans[controlPlateIndex] = controlPlateBaseBean;
                            }
                        }
                    }
                    //清空数据
                    controlPlateBaseArrays[controlPlateIndex] = initArrays(17);
                    //warning数据
                    controlPlateWaringArrays[controlPlateIndex][0] = canData;
                } else if (controlPlateDataItemIndex == 48) {
                    controlPlateWaringArrays[controlPlateIndex][1] = canData;
                } else if (controlPlateDataItemIndex == 49) {
                    controlPlateWaringArrays[controlPlateIndex][2] = canData;
                } else if (controlPlateDataItemIndex == 50) {
                    controlPlateWaringArrays[controlPlateIndex][3] = canData;
                    //判断接收到2c 证明一个帧的报文发送完毕了  取出并拼装所有报文并清空  得先判断里面有没有空值 如果有空值的话 证明报文不完整 废弃掉
                    //建立临时数组
                    byte[][] tempArrays = controlPlateWaringArrays[controlPlateIndex];
                    //先判断有没有空值
                    int isHaveNull = 0;
                    for (int i = 0; i < tempArrays.length; i++) {
                        if (tempArrays[i][0] == 0) {
                            isHaveNull = 1;
                        }
                    }
                    if (isHaveNull == 0) {
                        byte[] return_order = new byte[28];
                        byte[][] arr_t = tempArrays;
                        for (int i = 0; i < arr_t.length; i++) {
                            for (int j = 0; j < arr_t[i].length - 1; j++) {
                                return_order[(i * 7) + j] = arr_t[i][j + 1];
                            }
                        }
                        sendData(new CanControlPlateServiceInfoBeanFormat(controlPlateIndex, new ControlPlateReceiveEncap().returnWaringByBytes(return_order)),1);
                        controlPlateInfo.setControlPlateWarningBean(controlPlateIndex, new ControlPlateReceiveEncap().returnWaringByBytes(return_order));
                    } else {
//                        System.err.println("CanService - 控制板 - 返回：" + " 电池预警数据丢帧 - " + (controlPlateIndex + 1) + "仓");
                    }
                    controlPlateWaringArrays[controlPlateIndex] = initArrays(4);
                }
            } else if (canAddress > 100000) {

                String addressStr = Long.toHexString(canAddress);
                String dataStr = Units.ByteArrToHex(canData);
                String addressStrTop = addressStr.substring(0, 6);
                String addressStrEnd = addressStr.substring(6, 8);

                if (addressStrTop.equals("980300")) {
                    int index = Integer.parseInt(addressStrEnd, 16) - 21;
                    pduChargingInfoBeans[index] = PduEncap.chargingInfoAnalysis(canData);
                    pduInfo.setPduChargingInfoBean(index, PduEncap.chargingInfoAnalysis(canData));
                    sendData(new CanControlPlateServicePduInfoBeanFormat(pduChargingInfoBeans, pduEquipmentBeans));
                } else if (addressStrTop.equals("980700")) {   //pdu实时信息帧 20s    1到3是电源   模块   剩下的电池模块
                    int index = Integer.parseInt(dataStr.substring(0, 2), 16);
                    if (index > 3) {
                        index = index - ADDRESS_DEVIATION;
                    } else {
                        index = index - 1;
                    }
                    pduEquipmentBeans[index] = PduEncap.equipmentAnalysis(canData);
                    pduInfo.setPduEquipmentBeans(index, PduEncap.equipmentAnalysis(canData));
                    sendData(new CanControlPlateServicePduInfoBeanFormat(pduChargingInfoBeans, pduEquipmentBeans));
                }
            }
        }
    }

    public ControlPlateInfo getControlPlateInfo() {
        return controlPlateInfo;
    }

    public PduInfo getPduInfo() {
        return pduInfo;
    }
}
