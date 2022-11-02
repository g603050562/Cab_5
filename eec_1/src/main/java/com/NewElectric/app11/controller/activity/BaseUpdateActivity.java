package com.NewElectric.app11.controller.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.R;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateService;
import com.NewElectric.app11.service.canAndSerial.serial.SerialService;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnect;
import com.NewElectric.app11.units.dialog.DialogUpdate;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by guo on 2017/12/2.
 * 升级基础类
 *
 * activity基础类 - 电池升级基础类
 * BaseActivity - BaseUpdateActivity
 *
 */

public class BaseUpdateActivity extends BaseActivity  implements Observer {

    //提示框
    protected DialogUpdate dialogUpdate;
    //接收数据 - 舱门
    protected int door = -1;
    //接收数据 - 文件地址
    protected String path = "";
    //接收数据 - 升级类型
    protected String type = "";
    //最大线程保护时间（超过退出）
    protected int timeCount = 600;
    //rxjava
    private Disposable disposable;

    //日志显示
    @BindView(R.id.log)
    public EditText logView;
    //进度条
    @BindView(R.id.p_bar)
    public ProgressBar p_bar;
    @BindView(R.id.p_bar_2)
    public ProgressBar p_bar_2;
    //左上角数据
    @BindView(R.id.cabid)
    public TextView cabid;
    @BindView(R.id.tel)
    public TextView tel;
    @BindView(R.id.time)
    public TextView time;
    //中间title
    @BindView(R.id.title)
    public TextView title;

    //升级电池初始化
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(cabInfoSp.getAndroidDeviceModel().equals("rk3288_box")){
            setContentView(R.layout.activity_updata_1080p);
        }else if(cabInfoSp.getAndroidDeviceModel().equals("SABRESD-MX6DQ")){
            setContentView(R.layout.activity_updata_720p);
        }

        //注册can和485原始数据监听
        MyApplication.serialAndCanPortUtils.addMyObserver(this);
        //绑定ButterKnife
        ButterKnife.bind(this);

        //挂起can数据解析
        CanControlPlateService.getInstance().hangUpAllDoor(1200);
        //挂起485数据解析
        SerialService.getInstance().threadHangOnCode(1);
        //挂起长链接
        LongLinkConnect.getInstance().setThreadHangUpCode(1);

        door = getIntent().getIntExtra("door",0);
        path = getIntent().getStringExtra("path");
        type = getIntent().getStringExtra("type");

        cabid.setText(getIntent().getStringExtra("cabid"));
        tel.setText(getIntent().getStringExtra("tel"));

        dialogUpdate = new DialogUpdate(activity);

        //时间保护线程 超时退出
        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        if(timeCount > 0){
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    time.setText(timeCount+" S");
                                }
                            });
                            timeCount = timeCount - 1;
                        }else{
                            finishUpdate();
                        }
                    }
                });
    }

    //dialog提示
    protected void showDialog(final String msg , final int time){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(activity != null){
                    dialogUpdate.show(msg, time);
                }
            }
        });
    }

    //更新日志
    protected void updateLog(final String log){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sub_log = logView.getText().toString();
                if (sub_log.length() > 2000) {
                    int a = sub_log.length() - 2000;
                    sub_log = sub_log.substring(a, sub_log.length());
                }
                logView.setText(sub_log + "\n" + df.format(new Date()) + "    " + log);
                logView.setSelection(logView.getText().length());
            }
        });
    }

    //更新进度条
    protected void updateBar(final long process , final long total){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float b = (float) process / total * 100;
                p_bar.setProgress((int) b);
            }
        });
    }
    //更新进度条
    protected void updateBar_2(final long process , final long total){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float b = (float) process / total * 100;
                p_bar_2.setProgress((int) b);
            }
        });
    }

    //结束升级后 舱门推杆归位
    protected void finishUpdate(){

        CanControlPlateService.getInstance().cancelHangUpAllDoor();

        Observable.timer(15,TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                activity.finish();
                Observable.intervalRange(0, 9, 0, 3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        int index = Integer.parseInt(aLong + "");
                        if (forbiddenSp.getTargetForbidden(index) == -2) {
                            push(index + 1);
                        }else{
                            pull(index + 1);
                        }
                    }
                });
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogUpdate.onDestory();
        disposable.dispose();
        //结束挂起can数据
        CanControlPlateService.getInstance().cancelHangUpAllDoor();
        //结束挂起485数据
        SerialService.getInstance().threadHangOnCode(0);
        //解除挂起长连接
        LongLinkConnect.getInstance().setThreadHangUpCode(0);
        //结束原始数据监听
        MyApplication.serialAndCanPortUtils.deleteMyObserver(this);
    }

    //观察者数据
    @Override
    public void update(java.util.Observable observable, Object o) {

    }
}
