package com.NewElectric.app11.controller.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.agreement.send.pdu.PduSend;
import com.NewElectric.app11.hardwarecomm.androidHard.CanDataFormat;
import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by guo on 2017/12/2.
 * pdu升级页面
 * <p>
 * activity基础类 - 硬件升级基础类 - pdu升级页面
 * BaseActivity - BaseUpdateActivity - A_UpDatePdu
 */

public class A_UpDatePdu extends BaseUpdateActivity {

    //pdu升级程序内存
    private ArrayList<byte[]> pduDataList = new ArrayList();
    //pdu升级文件
    private File pduFile = null;
    //pdu升级到的包数
    int pduUpdatePageCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置电池升级最大时间
        timeCount = 300;
        title.setText("正在升级PDU，请稍候！");

        Observable.timer(3 , TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                updateLog("正在准备升级PDU");
                MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.updatePduStart());
            }
        });
    }

    @Override
    public void update(java.util.Observable observable, Object object) {
        super.update(observable, object);
        DataFormat dataFormat = (DataFormat)object;
        if(dataFormat.getType().equals("can")){
            CanDataFormat canDataFormat = new CanDataFormat((byte[]) dataFormat.getData());
            String address = canDataFormat.getAddressByStr();
            String dataStr = canDataFormat.getDataByStr();
            //pdu升级 - 收到确认升级指令 并且 下发发送下一包数据的头帧
            if (address.equals("880f3dab")) {
                pduDataList.clear();
                PduSend.closeAllPdu();
                Observable.timer(3 , TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        //文件分包 - 阵列化
                        pduFile = new File(path);
                        FileInputStream is = new FileInputStream(pduFile);
                        byte buffer[] = new byte[2048];
                        int length = 0;
                        while ((length = is.read(buffer)) > 0) {
                            byte hexData[] = new byte[length];
                            for (int i = 0; i < length; i++) {
                                hexData[i] = buffer[i];
                            }
                            pduDataList.add(hexData);
                            buffer = new byte[2048];
                        }
                        updateLog("正在解析升级包文件");
                        System.out.println("CAN - 充电机 - log：    " + "数据包数：" + pduDataList.size());

                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.updatePduItemHand(pduDataList.get(pduUpdatePageCount)));
                        updateLog("准备传输 第" + (pduUpdatePageCount + 1) + "包数据");
                        System.out.println("CAN - 充电机 - 下发：    " + "升级包：" + (pduUpdatePageCount + 1));
                    }
                });
            }

            //收到包数据的回复 准备下发报数据内容
            if (address.equals("881f3dab") && dataStr.length() == 16) {
                System.out.println("CAN - 充电机 - 下发：    正在传输文件");

                int count_h = Integer.parseInt(dataStr.substring(4, 6), 16);
                int count_l = Integer.parseInt(dataStr.substring(2, 4), 16);
                final int dataCount = count_h * 256 + count_l;

                updateBar( (int) pduUpdatePageCount + 1 , (int) pduDataList.size());

                Observable.intervalRange(0,dataCount,0,20,TimeUnit.MILLISECONDS).subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        int i = Integer.parseInt(aLong+"");
                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.updatePduItemBody(i,dataCount,pduDataList.get(pduUpdatePageCount)));
                        updateLog("正在传输 第" + (pduUpdatePageCount + 1) + "包数据   第" + (i + 1) + "帧数据");
                        System.out.println("CAN - 充电机 - 下发：    " + "升级包：" + (pduUpdatePageCount + 1) + "   目前帧数：" + (i + 1));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.updatePduItemFoot(pduDataList.get(pduUpdatePageCount)));
                        System.out.println("CAN - 充电机 - 下发：    " + "升级包：" + (pduUpdatePageCount + 1) + "   crc校验：");
                        updateLog("传输完 第" + (pduUpdatePageCount + 1) + "包数据");
                    }
                });
            }
            //数据尾帧回应
            if (address.equals("883f3dab")) {
                //升级下一包
                if (dataStr.equals("AA00000000000000")) {
                    if (pduUpdatePageCount < pduDataList.size() - 1) {
                        pduUpdatePageCount = pduUpdatePageCount + 1;
                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.updatePduItemHand(pduDataList.get(pduUpdatePageCount)));
                        updateLog("准备传输 第" + (pduUpdatePageCount + 1) + "包数据");
                        System.out.println("CAN - 充电机 - 下发：    " + "升级包：" + (pduUpdatePageCount + 1));
                    }
                    //结束升级
                    else {
                        MyApplication.serialAndCanPortUtils.canSendOrder(PduSend.updataPduEnd(pduFile));
                        System.out.println("CAN - 充电机 - 下发：    " + "整个升级包校验");
                        updateLog("升级完成");
                        finishUpdate();
                    }
                }
                //返回数据出错
                else {
                    updateLog("PDU升级失败!");
                    pduUpdatePageCount = 0;
                    finishUpdate();
                }
            }
        }

    }
}
