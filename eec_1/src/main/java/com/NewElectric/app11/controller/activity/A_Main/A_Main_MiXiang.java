package com.NewElectric.app11.controller.activity.A_Main;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.NewElectric.app11.R;
import com.NewElectric.app11.config.SystemConfig;
import com.NewElectric.app11.controller.activity.A_Admin;
import com.NewElectric.app11.controller.activity.A_UpDateBattery;
import com.NewElectric.app11.controller.activity.A_UpDateControlPlate;
import com.NewElectric.app11.controller.activity.A_UpDatePdu;
import com.NewElectric.app11.controller.activity.BaseShowActivity;
import com.NewElectric.app11.controller.custom.BlackExchangeAnimation;
import com.NewElectric.app11.controller.custom.BlackExchangeDialog;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateBaseBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateWarningBean;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateServiceInfoBeanFormat;
import com.NewElectric.app11.service.canAndSerial.serial.SerialServiceReturnFormat;
import com.NewElectric.app11.service.logic.logic_exchange.ExchangeBarAnimation;
import com.NewElectric.app11.service.logic.logic_exchange.UidDictionart;
import com.NewElectric.app11.service.logic.logic_exchange.cabinetOfPdu.ExchangeBarOutLineStartAnimation;
import com.NewElectric.app11.service.logic.logic_find4gCard.Find4gCardReturnDataFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnectDialogFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnectUpdateHardWare;
import com.NewElectric.app11.service.logic.logic_movies.MoviesController;
import com.NewElectric.app11.units.RootCommand;
import com.NewElectric.app11.units.Units;
import com.NewElectric.app11.units.dialog.DialogMain;
import com.squareup.picasso.Picasso;
import com.wonderkiln.camerakit.CameraView;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;


public class A_Main_MiXiang extends BaseShowActivity {

    //舱门view
    @BindViews({R.id.bar_r_text_1, R.id.bar_r_text_2, R.id.bar_r_text_3, R.id.bar_r_text_4, R.id.bar_r_text_5, R.id.bar_r_text_6, R.id.bar_r_text_7, R.id.bar_r_text_8, R.id.bar_r_text_9})
    public List<TextView> bar_r_texts;
    @BindViews({R.id.bar_not_image_1, R.id.bar_not_image_2, R.id.bar_not_image_3, R.id.bar_not_image_4, R.id.bar_not_image_5, R.id.bar_not_image_6, R.id.bar_not_image_7, R.id.bar_not_image_8, R.id.bar_not_image_9,})
    public List<ImageView> bar_not_images;

    //动画相关view
    @BindView(R.id.animation_dialog)
    public BlackExchangeDialog animationDialog;
    //租电池二维码
    @BindView(R.id.rent_bar_qcode)
    public ImageView rentBarQRcodeView;
    //扫码下载app二维码
    @BindView(R.id.download_qcode)
    public ImageView downloadQRcodeView;
    //站点编号
    @BindView(R.id.cab_id)
    public TextView cabIdView;
    //站点名称
    @BindView(R.id.now_address)
    public TextView addressView;
    //400电话
    @BindView(R.id.tel_text_1)
    public TextView phoneView;
    //程序版本
    @BindView(R.id.version)
    public TextView versionView;
    //广告显示
    @BindView(R.id.adv_webview)
    public WebView advertisementWebView;

    //右下角图标显示
    @BindView(R.id.signal) //信号强弱图标
    public ImageView signalView;
    @BindView(R.id.t_5)
    public TextView eleMeterView; //电表走字显示

    //摄像头显示
    @BindView(R.id.camera_preview_bg)
    public LinearLayout cameraPreBackgroundView;

    //电表和温度数据 485数据的返回
    public String eleMeter = "0";
    //录制视频
    private MoviesController moviesController = null;
    private CameraView camera_preview_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_1080p_black);

        ButterKnife.bind(this);

        //删除本地webView缓存
        deleteDatabase("WebView.db");
        advertisementWebView.clearHistory();
        advertisementWebView.clearFormData();
        getCacheDir().delete();

        //获取android板内核版本
        String androidCoreString = new RootCommand().execRootCmd("getprop ro.grst.version", 1);
        writeLog("初始化 - 内核版本 - " + androidCoreString);
        System.out.println("初始化 - 内核版本 - " + androidCoreString);

        //初始化信息
        writeLog("电柜初始化");
        animationDialog.setActivity(activity);
        showDialogInfo("电柜正在初始化，请稍候！", 30, 0);
        cabIdView.setText("D" + cabInfoSp.getCabinetNumber_XXXXX());
        setTel(cabInfoSp.getTelNumber());
        versionView.setText("Ver：" + cabInfoSp.getVersion());
        setRentBatteryQCode();
        setQrCode(HttpUrlMap.DownLoadApkJump);

        //摄像头初始化
        setMovies();

        initDataService();
        initOtherService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Units.hasCamera() == true) {
            moviesController.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Units.hasCamera() == true) {
            moviesController.onPause();
        }
    }


    @OnClick(R.id.into_admin)
    public void intoAdmin() {
        activity.startActivity(new Intent(activity, A_Admin.class));
    }

    /**
     * 电池基础信息返回
     *
     * @param canControlPlateServiceInfoBeanFormat
     */
    @Override
    public void batteryBaseBeanReturn(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat) {
        super.batteryBaseBeanReturn(canControlPlateServiceInfoBeanFormat);
        //获取信息
        int index = canControlPlateServiceInfoBeanFormat.getIndex();
        //更新内存数据
        controlPlateInfo.getControlPlateBaseBeans()[index] = (ControlPlateBaseBean)canControlPlateServiceInfoBeanFormat.getData();
        //更新界面电池
        updateBattery(index + 1);
    }

    /**
     * 电池预警信息返回
     *
     * @param canControlPlateServiceInfoBeanFormat
     */
    @Override
    public void batteryWarningBeanReturn(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat) {
        super.batteryWarningBeanReturn(canControlPlateServiceInfoBeanFormat);
        controlPlateInfo.getControlPlateWarningBeans()[canControlPlateServiceInfoBeanFormat.getIndex()] = (ControlPlateWarningBean)canControlPlateServiceInfoBeanFormat.getData();
    }

    /**
     * 4g卡初始化返回
     *
     * @param canServicePduBeanFormat
     */
    @Override
    public void find4gCardReturn(Find4gCardReturnDataFormat canServicePduBeanFormat) {
        super.find4gCardReturn(canServicePduBeanFormat);
        if (canServicePduBeanFormat.getType().equals("success")) {
            showDialogInfo("初始化成功！", 5, 1);
        } else {
            showDialogInfo("初始化失败，未检测到4G卡，正在重启系统", 10, 1);
            Observable.timer(10, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    new RootCommand().RootCommandStart("reboot");
                }
            });
        }
    }

    /**
     * 传感器返回的电表数据和温室传感器数据
     *
     * @param serialServiceReturnFormat
     */
    @Override
    public void _485Return(SerialServiceReturnFormat serialServiceReturnFormat) {
        this.eleMeter = serialServiceReturnFormat.getEleMeter();
        final String fCount = eleMeter + "KWH";
        //更新屏幕数据
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                eleMeterView.setText(fCount);
            }
        });
    }

    /**
     * dbm返回信号数据更新
     */
    @Override
    public void dbmReturn(int dbm) {
        if (cabInfoSp.getAndroidDeviceModel().equals("rk3288_box")) {
            if (dbm >= -95 && dbm < 0) {
                signalView.setImageResource(R.drawable.b3);
            } else if (dbm < -95 && dbm > -115) {
                signalView.setImageResource(R.drawable.b10);
            } else if (dbm <= -115) {
                signalView.setImageResource(R.drawable.b9);
            } else {
                signalView.setImageResource(R.drawable.b9);
            }
        } else {
            if (dbm >= -115 && dbm < 0) {
                signalView.setImageResource(R.drawable.b3);
            } else if (dbm < -115 && dbm > -130) {
                signalView.setImageResource(R.drawable.b10);
            } else if (dbm <= -130) {
                signalView.setImageResource(R.drawable.b9);
            } else {
                signalView.setImageResource(R.drawable.b9);
            }
        }
    }

    /**
     * 显示对话框提示
     *
     * @param msg
     * @param time
     */
    private void showDialogInfo(String msg, int time, int type) {
        final String fMsg = msg;
        final int fTime = time;
        final int fType = type;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animationDialog.showDialog(msg,time,type);
            }
        });
    }

    /**
     * 更新界面信息
     *
     * @param door
     */
    private void updateBattery(int door) {
        final int fIndex = door - 1;
        ControlPlateBaseBean controlPlateBaseBean = controlPlateInfo.getControlPlateBaseBeans()[fIndex];
        int SOC = controlPlateBaseBean.getBatteryRelativeSurplus();
        String BID = controlPlateBaseBean.getBID();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(forbiddenSp.getTargetForbidden(fIndex) != 1){
                    if(!bar_r_texts.get(fIndex).getText().equals("禁用")){
                        bar_r_texts.get(fIndex).setText("禁用");
                        bar_not_images.get(fIndex).setVisibility(View.GONE);
                    }
                }else{
                    if (SOC == -1 && !bar_r_texts.get(fIndex).getText().equals("0%")) {
                        bar_r_texts.get(fIndex).setText("0%");
                        bar_not_images.get(fIndex).setVisibility(View.GONE);
                    } else if (SOC == 0 && !bar_r_texts.get(fIndex).getText().equals("")) {
                        bar_r_texts.get(fIndex).setText("");
                        bar_not_images.get(fIndex).setVisibility(View.VISIBLE);
                    } else if (SOC > 0 && SOC <= 100 && !bar_r_texts.get(fIndex).getText().equals(SOC + "%")) {
                        bar_r_texts.get(fIndex).setText(SOC + "%");
                        bar_not_images.get(fIndex).setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    /**
     * 设置400电话
     *
     * @param phones_str
     */
    private void setTel(String phones_str) {
        if (phones_str.equals("")) {
            phoneView.setVisibility(View.GONE);
        } else {
            phoneView.setText(phones_str);
            phoneView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置租电池二维码
     */
    public void setRentBatteryQCode(){
        String timeString = System.currentTimeMillis()+"";
        int length = timeString.length();
        timeString = timeString.substring(0 , length - 3);
        String dataString = cabInfoSp.getCabinetNumber_4600XXXX() + timeString;
        String dataString_62 = UidDictionart.get_T10_To_S62(dataString);
        rentBarQRcodeView.setImageBitmap(Units.generateBitmap(dataString_62, 400, 400 , 0xff000000 , 0xffffffff));
    }

    /**
     * 设置二维码
     *
     * @param qrCode
     */
    @Override
    public void setQrCode(String qrCode) {
        super.setQrCode(qrCode);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                downloadQRcodeView.setImageBitmap(Units.generateBitmap(qrCode, 400, 400 , 0xff000000 , 0xffffffff));
            }
        });
    }


    /**
     * 打开电柜后台
     *
     * @param openAdmin
     */
    @Override
    public void openAdmin(int openAdmin) {
        super.openAdmin(openAdmin);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.startActivity(new Intent(activity, A_Admin.class));
            }
        });
    }

    /**
     * 显示提示框
     * @param longLinkConnectDialogFormat
     */
    @Override
    public void showDialog(LongLinkConnectDialogFormat longLinkConnectDialogFormat) {
        super.showDialog(longLinkConnectDialogFormat);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialogInfo(longLinkConnectDialogFormat.getMessage(), longLinkConnectDialogFormat.getTime(), longLinkConnectDialogFormat.getType());
            }
        });
    }

    /**
     * 长链接返回更新电池UI
     *
     * @param door
     */
    @Override
    public void updateBatteryUI(int door) {
        super.updateBatteryUI(door);
        updateBattery(door);
    }

    /**
     * 长链接更新电柜显示信息
     */
    @Override
    public void updateCabinetUI() {
        super.updateCabinetUI();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cabIdView.setText(cabInfoSp.getCabinetNumber_XXXXX());
                addressView.setText(cabInfoSp.getAddress());
                setTel(cabInfoSp.getTelNumber());
                for (int i = 0; i < MAX_CABINET_COUNT; i++) {
                    updateBatteryUI(i + 1);
                }
                if (cabInfoSp.getAndroidDeviceModel().equals("rk3288_box")) {
                    WebSettings webSettings = advertisementWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
                    webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
                    webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
                    webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
                    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
                    webSettings.setAllowFileAccess(true); //设置可以访问文件
                    webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
                    webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
                    webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
                    advertisementWebView.loadUrl(HttpUrlMap.h5AdvUrl + cabInfoSp.getCabinetNumber_4600XXXX());
                    advertisementWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            advertisementWebView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }.start();
                        }
                    });
                }
            }
        });
    }

    /**
     * 硬件更新
     *
     * @param longLinkConnectUpdateHardWare
     */
    @Override
    public void updateHardWare(LongLinkConnectUpdateHardWare longLinkConnectUpdateHardWare) {
        super.updateHardWare(longLinkConnectUpdateHardWare);
        String name = longLinkConnectUpdateHardWare.getName();
        int door = longLinkConnectUpdateHardWare.getDoor();
        String dataPath = longLinkConnectUpdateHardWare.getDataPath();
        String type = longLinkConnectUpdateHardWare.getType();
        Intent intent = null;
        if (name.equals("Battery")) {
            intent = new Intent(activity, A_UpDateBattery.class);
        } else if (name.equals("PDU")) {
            intent = new Intent(activity, A_UpDatePdu.class);
        } else if (name.equals("ControlPlate")) {
            intent = new Intent(activity, A_UpDateControlPlate.class);
        }
        intent.putExtra("door", door);
        intent.putExtra("path", dataPath);
        intent.putExtra("type", type);
        intent.putExtra("tel", cabInfoSp.getTelNumber());
        intent.putExtra("cabid", cabInfoSp.getCabinetNumber_XXXXX());
        activity.startActivity(intent);
    }

    /**
     * 换电动画
     */
    @Override
    public void startAnimation(ExchangeBarOutLineStartAnimation exchangeBarOutLineStartAnimation) {
        super.startAnimation(exchangeBarOutLineStartAnimation);
        int in_electric = exchangeBarOutLineStartAnimation.getInDoor();
        int out_electric = exchangeBarOutLineStartAnimation.getOutDoor();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animationDialog.showExchangeInfo(in_electric , out_electric);
            }
        });
    }

    /**
     * 初始化摄像头UI
     */
    private void setMovies(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Units.getIsExistExCard() == 1 &&  Units.hasCamera() == true) {
                    LayoutInflater inflater = LayoutInflater.from(activity);
                    View view = (LinearLayout) inflater.inflate(R.layout.activity_main_camera, null);
                    cameraPreBackgroundView.addView(view);
                    camera_preview_2 = view.findViewById(R.id.camera_preview);
                    moviesController = new MoviesController(activity, camera_preview_2);
                }
                System.out.println("movies：   是否存在摄像头 - " + Units.hasCamera() + "   SD卡数是否存在 - " + Units.getIsExistExCard());
            }
        });
    }

    /**
     * 返回键不再退出程序
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            return true;
        }
        //继续执行父类其他点击事件
        return super.onKeyUp(keyCode, event);
    }
}