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

import com.NewElectric.app11.config.SystemConfig;
import com.NewElectric.app11.controller.activity.A_Admin;
import com.NewElectric.app11.controller.activity.A_UpDateBattery;
import com.NewElectric.app11.controller.activity.A_UpDateControlPlate;
import com.NewElectric.app11.controller.activity.A_UpDatePdu;
import com.NewElectric.app11.controller.activity.BaseShowActivity;
import com.NewElectric.app11.service.logic.logic_exchange.UidDictionart;
import com.squareup.picasso.Picasso;
import com.wonderkiln.camerakit.CameraView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateBaseBean;
import com.NewElectric.app11.R;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateWarningBean;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateServiceInfoBeanFormat;
import com.NewElectric.app11.service.canAndSerial.serial.SerialServiceReturnFormat;
import com.NewElectric.app11.service.logic.logic_exchange.ExchangeBarAnimation;
import com.NewElectric.app11.service.logic.logic_exchange.cabinetOfPdu.ExchangeBarOutLineStartAnimation;
import com.NewElectric.app11.service.logic.logic_find4gCard.Find4gCardReturnDataFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnectDialogFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.longLink.LongLinkConnectUpdateHardWare;
import com.NewElectric.app11.service.logic.logic_movies.MoviesController;
import com.NewElectric.app11.units.RootCommand;
import com.NewElectric.app11.units.Units;
import com.NewElectric.app11.units.dialog.DialogMain;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;


public class A_Main_Hello extends BaseShowActivity {

    //舱门view
    @BindViews({R.id.bar_r_image_1, R.id.bar_r_image_2, R.id.bar_r_image_3, R.id.bar_r_image_4, R.id.bar_r_image_5, R.id.bar_r_image_6, R.id.bar_r_image_7, R.id.bar_r_image_8, R.id.bar_r_image_9})
    public List<ImageView> bar_r_images;
    @BindViews({R.id.bar_r_text_1, R.id.bar_r_text_2, R.id.bar_r_text_3, R.id.bar_r_text_4, R.id.bar_r_text_5, R.id.bar_r_text_6, R.id.bar_r_text_7, R.id.bar_r_text_8, R.id.bar_r_text_9})
    public List<TextView> bar_r_texts;
    @BindViews({R.id.bar_not_image_1, R.id.bar_not_image_2, R.id.bar_not_image_3, R.id.bar_not_image_4, R.id.bar_not_image_5, R.id.bar_not_image_6, R.id.bar_not_image_7, R.id.bar_not_image_8, R.id.bar_not_image_9,})
    public List<ImageView> bar_not_images;
    //动画相关view
    @BindView(R.id.black_1)
    public ImageView black_1;
    @BindView(R.id.black_2)
    public ImageView black_2;
    @BindView(R.id.up_bar_1)
    public ImageView up_bar_1;
    @BindView(R.id.up_bar_2)
    public ImageView up_bar_2;
    @BindView(R.id.up_bar_1_text)
    public TextView up_bar_1_text;
    @BindView(R.id.up_bar_2_text)
    public TextView up_bar_2_text;
    @BindView(R.id.up_bar_1_panel)
    public FrameLayout up_bar_1_panel;
    @BindView(R.id.up_bar_2_panel)
    public FrameLayout up_bar_2_panel;
    @BindView(R.id.cost_bi) //扣币类型显示
    public TextView coinDeductionView;
    @BindView(R.id.cost) //扣币类型显示
    public LinearLayout costView;
    @BindView(R.id.info_panel) // 提示框
    public LinearLayout infoPanelView;
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
    //当前时间
    @BindView(R.id.now_time)
    public TextView nowTimeView;
    //400电话
    @BindView(R.id.tel_text_1)
    public TextView phoneView;
    //程序版本
    @BindView(R.id.version)
    public TextView versionView;
    //右下角图标显示
    @BindView(R.id.b_3) //信号强弱图标
    public ImageView signalView;
    @BindView(R.id.t_5)
    public TextView eleMeterView; //电表走字显示
    @BindView(R.id.t_6)
    public TextView theMeterView; //湿度显示
    @BindView(R.id.t_7)
    public TextView temMeterView; //温度显示
    @BindView(R.id.t_8)
    public TextView powerView; //温度显示
    @BindView(R.id.t_9)
    public TextView isOnlineView; //温度显示
    //上传文件显示
    @BindView(R.id.uploadmoviespanel)
    public LinearLayout uploadMoviesPanelView;
    @BindView(R.id.p_bar)
    public ProgressBar uploadProgressBarView;
    @BindView(R.id.uploadPbarTitle)
    public TextView uploadProgressBarTitleView;
    //提示框
    @BindView(R.id.dialog_panel)
    public LinearLayout dialogPanelView;
    @BindView(R.id.dialog_time)
    public TextView dialogTimeView;
    @BindView(R.id.dialog_info)
    public TextView dialogInfoView;
    //摄像头显示
    @BindView(R.id.camera_preview_bg)
    public LinearLayout cameraPreBackgroundView;
    @BindView(R.id.camera_preview_panel)
    public LinearLayout cameraPrePanelView;
    @BindView(R.id.camera_preview_panel_text)
    public TextView cameraPrePanelTitleView;
    //广告显示
    @BindView(R.id.adv_webview)
    public WebView advertisementWebView;
    //移动提示框
    private DialogMain AMainSpeakDialog;
    //电表和温度数据 485数据的返回
    public String eleMeter = "0";
    public String theMeter = "0";
    public String temMeter = "0";
    //录制视频
    private MoviesController moviesController = null;
    private CameraView camera_preview_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (cabInfoSp.getAndroidDeviceModel().equals("rk3288_box")) {
            setContentView(R.layout.activity_main_1080p_orange);
        } else if (cabInfoSp.getAndroidDeviceModel().equals("SABRESD-MX6DQ")) {
            setContentView(R.layout.activity_main_720p_orange);
        } else {
            setContentView(R.layout.activity_main_1080p_orange);
        }

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
        this.theMeter = serialServiceReturnFormat.getTheMeter();
        this.temMeter = serialServiceReturnFormat.getTemMeter();
        final String fCount = eleMeter + "KWH";
        final String fTemMeter = temMeter + "C";
        final String fTheMeter = theMeter + "%";
        //更新屏幕数据
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                eleMeterView.setText(fCount);
                temMeterView.setText(fTemMeter);
                theMeterView.setText(fTheMeter);
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
                if (AMainSpeakDialog == null) {
                    AMainSpeakDialog = new DialogMain(activity, dialogPanelView, dialogTimeView, dialogInfoView, infoPanelView, new DialogMain.SpeakDialogListener() {
                        @Override
                        public void onSpeakDialogReturn(String msg) {
                            final String fMsg = msg;
                            textToSpeech.speak(fMsg, TextToSpeech.QUEUE_ADD, null);
                        }
                    });
                }
                AMainSpeakDialog.show(fMsg, fTime, fType);
            }
        });
        writeLog("电柜提示 - " + msg);
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
                if (forbiddenSp.getTargetForbidden(fIndex) != 1 && !bar_r_texts.get(fIndex).getText().equals("禁用")) {
                    bar_r_texts.get(fIndex).setText("禁用");
                    bar_not_images.get(fIndex).setVisibility(View.VISIBLE);
                } else {
                    bar_not_images.get(fIndex).setVisibility(View.GONE);
                    if (SOC == -1 && !bar_r_texts.get(fIndex).getText().equals("0%")) {
                        Picasso.with(activity).load(R.drawable.left_bar_null).into(bar_r_images.get(fIndex));
                        bar_r_texts.get(fIndex).setText("0%");
                    } else if (SOC == 0 && !bar_r_texts.get(fIndex).getText().equals("空")) {
                        Picasso.with(activity).load(R.drawable.left_bar_null).into(bar_r_images.get(fIndex));
                        bar_r_texts.get(fIndex).setText("空");
                    } else if (SOC > 0 && SOC <= 100 && !bar_r_texts.get(fIndex).getText().equals(SOC + "%")) {
                        bar_r_texts.get(fIndex).setText(SOC + "%");
                        if (SOC > 0 && SOC <= 100) {
                            String TopBidStr = BID.substring(0, 1);
                            String EndBidStr = BID.substring(10, 12);
                            if (TopBidStr.equals("N") && EndBidStr.equals("NN")) {
                                Picasso.with(activity).load(R.drawable.left_bar_out).into(bar_r_images.get(fIndex));
                            } else {
                                Picasso.with(activity).load(R.drawable.left_bar_has).into(bar_r_images.get(fIndex));
                            }
                        }
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
        rentBarQRcodeView.setImageBitmap(Units.generateBitmap(cabInfoSp.getCabinetNumber_4600XXXX() + "/" + "0000000000000000" + "/" + "0", 400, 400));
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
                downloadQRcodeView.setImageBitmap(Units.generateBitmap(qrCode, 400, 400 , 0xff000000 , 0x00ffffff));
            }
        });
    }

    /**
     * 在线状态返回
     *
     * @param onLineType
     */
    @Override
    public void onLineType(String onLineType) {
        super.onLineType(onLineType);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isOnlineView.setText(onLineType);
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
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        sleep(1000 * 120);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            advertisementWebView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            };
                            thread.start();
                        }
                    });
                }
            }
        });
    }

    /**
     * 弹出电池舱门闪烁
     *
     * @param door
     */
    @Override
    public void pushAndPullUpdateUi(int door) {
        super.pushAndPullUpdateUi(door);
        final int fIndex = door - 1;
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 21; i++) {
                    if (i % 2 == 0) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (fIndex > 8) {
                                    return;
                                }
                                bar_r_images.get(fIndex).setImageResource(R.drawable.left_bar_has);
                            }
                        });
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (fIndex > 8) {
                                    return;
                                }
                                bar_r_images.get(fIndex).setImageResource(R.drawable.left_bar_out);
                            }
                        });
                    }
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    /**
     * 重写弹出电池
     *
     * @param door
     * @param reason
     */
    @Override
    protected void pushAndPull(int door, String reason) {
        super.pushAndPull(door, reason);
        pushAndPullUpdateUi(door);
    }

    /**
     * 时间UI显示
     *
     * @param timeStr
     */
    @Override
    public void setTime(String timeStr) {
        super.setTime(timeStr);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nowTimeView.setText(timeStr);
                Calendar calendar = Calendar.getInstance();
                int second = calendar.get(Calendar.SECOND);
                if(second == 0){
                    setRentBatteryQCode();
                }
            }
        });
    }

    /**
     * 功率UI显示
     *
     * @param power
     */
    @Override
    public void setPower(int power) {
        super.setPower(power);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                powerView.setText(power + "W");
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
        String type = exchangeBarOutLineStartAnimation.getType();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int j = in_electric;
                int g = out_electric;

                up_bar_1_text.setText(in_electric + "%");
                up_bar_2_text.setText(out_electric + "%");
                coinDeductionView.setText(type);

                if (j > 0 && j <= 10) {
                    Picasso.with(activity).load(R.drawable.image_b_0).into(up_bar_1);
                } else if (j > 10 && j <= 20) {
                    Picasso.with(activity).load(R.drawable.image_b_10).into(up_bar_1);
                } else if (j > 20 && j <= 30) {
                    Picasso.with(activity).load(R.drawable.image_b_20).into(up_bar_1);
                } else if (j > 30 && j <= 40) {
                    Picasso.with(activity).load(R.drawable.image_b_30).into(up_bar_1);
                } else if (j > 40 && j <= 50) {
                    Picasso.with(activity).load(R.drawable.image_b_40).into(up_bar_1);
                } else if (j > 50 && j <= 60) {
                    Picasso.with(activity).load(R.drawable.image_b_50).into(up_bar_1);
                } else if (j > 60 && j <= 70) {
                    Picasso.with(activity).load(R.drawable.image_b_60).into(up_bar_1);
                } else if (j > 70 && j <= 80) {
                    Picasso.with(activity).load(R.drawable.image_b_70).into(up_bar_1);
                } else if (j > 80 && j <= 90) {
                    Picasso.with(activity).load(R.drawable.image_b_80).into(up_bar_1);
                } else if (j > 90 && j <= 99) {
                    Picasso.with(activity).load(R.drawable.image_b_90).into(up_bar_1);
                } else if (j == 100) {
                    Picasso.with(activity).load(R.drawable.image_b_100).into(up_bar_1);
                }

                if (g > 0 && g <= 10) {
                    Picasso.with(activity).load(R.drawable.image_b_0).into(up_bar_2);
                } else if (g > 10 && g <= 20) {
                    Picasso.with(activity).load(R.drawable.image_b_10).into(up_bar_2);
                } else if (g > 20 && g <= 30) {
                    Picasso.with(activity).load(R.drawable.image_b_20).into(up_bar_2);
                } else if (g > 30 && g <= 40) {
                    Picasso.with(activity).load(R.drawable.image_b_30).into(up_bar_2);
                } else if (g > 40 && g <= 50) {
                    Picasso.with(activity).load(R.drawable.image_b_40).into(up_bar_2);
                } else if (g > 50 && g <= 60) {
                    Picasso.with(activity).load(R.drawable.image_b_50).into(up_bar_2);
                } else if (g > 60 && g <= 70) {
                    Picasso.with(activity).load(R.drawable.image_b_60).into(up_bar_2);
                } else if (g > 70 && g <= 80) {
                    Picasso.with(activity).load(R.drawable.image_b_70).into(up_bar_2);
                } else if (g > 80 && g <= 90) {
                    Picasso.with(activity).load(R.drawable.image_b_80).into(up_bar_2);
                } else if (g > 90 && g <= 99) {
                    Picasso.with(activity).load(R.drawable.image_b_90).into(up_bar_2);
                } else if (g == 100) {
                    Picasso.with(activity).load(R.drawable.image_b_100).into(up_bar_2);
                }

                ExchangeBarAnimation exchangeAnimation = new ExchangeBarAnimation(cabInfoSp.getAndroidDeviceModel(), infoPanelView, costView, black_1, black_2, up_bar_2_panel, up_bar_1_panel, dialogPanelView, new ExchangeBarAnimation.IFExchangeAnimationListener() {
                    @Override
                    public void onExchangeAnimationStart() {
                    }
                    @Override
                    public void onExchangeAnimationEnd() {
                    }
                });
                exchangeAnimation.startAnimation();
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
                    cameraPrePanelView.setVisibility(View.GONE);
                    LayoutInflater inflater = LayoutInflater.from(activity);
                    View view = (LinearLayout) inflater.inflate(R.layout.activity_main_camera, null);
                    cameraPreBackgroundView.addView(view);
                    camera_preview_2 = view.findViewById(R.id.camera_preview);
                    moviesController = new MoviesController(activity, camera_preview_2);
                } else {
                    cameraPrePanelView.setVisibility(View.VISIBLE);
                    cameraPrePanelTitleView.setText("视频监控中");
                    if (Units.hasCamera() == false) {
                        cameraPrePanelTitleView.setText(cameraPrePanelTitleView.getText() + "#1");
                    }
                    if (Units.getIsExistExCard() == 0) {
                        cameraPrePanelTitleView.setText(cameraPrePanelTitleView.getText() + "#2");
                    }
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