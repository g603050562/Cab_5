package com.NewElectric.app11.controller.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.R;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateBaseBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateWarningBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduChargingInfoBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduEquipmentBean;
import com.NewElectric.app11.model.adapter.AdminControlPlateBottomAdapter;
import com.NewElectric.app11.model.adapter.AdminControlPlateTopAdapter;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateServiceInfoBeanFormat;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateServicePduInfoBeanFormat;
import com.NewElectric.app11.service.logic.logic_charging.ChargingGetHardWare;
import com.NewElectric.app11.service.logic.logic_httpConnection.fileDownLoad.UpdateHardWare;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttp;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttpParameterFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Created by guo on 2017/12/2.
 * 电柜后台界面
 */

public class A_Admin extends BaseShowActivity {

    //程序退出参数
    private static Boolean isExit = false;

    //参数显示
    @BindView(R.id.item_7_text)
    public TextView item_7_text;
    @BindView(R.id.item_8_text)
    public TextView item_8_text;
    @BindView(R.id.item_9_text)
    public TextView item_9_text;
    //线程保护按钮
    @BindView(R.id.thread_protection)
    public ImageView thread_protection;
    //电池显示列表
    @BindView(R.id.bar_info_girdview)
    public GridView bar_info_girdview;
    //充电器显示列表
    @BindView(R.id.cha_info_girdview)
    public GridView cha_info_girdview;
    //加热逻辑切换按钮
    @BindView(R.id.charge_status_1)
    public CheckBox charge_status_1;
    @BindView(R.id.charge_status_2)
    public CheckBox charge_status_2;
    //加热逻辑面板
    @BindView(R.id.charge_status_panel)
    public LinearLayout charge_status_panel;
    //改装柜充电功率设置
    @BindView(R.id.set_power_1)
    public CheckBox set_power_1;
    @BindView(R.id.set_power_2)
    public CheckBox set_power_2;
    //功率设置面板
    @BindView(R.id.set_power_panel)
    public LinearLayout set_power_panel;

    //控制板适配器
    private AdminControlPlateTopAdapter adminControlPlateTopAdapter;
    private List<ControlPlateBaseBean> controlPlateBaseBeans = new ArrayList<>();
    private List<ControlPlateWarningBean> controlPlateWarningBeans = new ArrayList<>();
    private List<PduChargingInfoBean> pduChargingInfoBeans = new ArrayList<>();
    private ArrayList<PduEquipmentBean> pduEquipmentTopBeans = new ArrayList<>();
    private AdminControlPlateBottomAdapter adminControlPlateBottomAdapter;
    private ArrayList<PduEquipmentBean> pduEquipmentBottomBeans = new ArrayList<>();

    //rxjava
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //配置分辨率
        if (cabInfoSp.getAndroidDeviceModel().equals("rk3288_box")) {
            setContentView(R.layout.activity_admin_1080p);
        } else if (cabInfoSp.getAndroidDeviceModel().equals("SABRESD-MX6DQ")) {
            setContentView(R.layout.activity_admin_720p);
        }
        ButterKnife.bind(this);
        initDataService();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogAdmin.dismiss();
        disposable.dispose();
    }

    protected void init() {

        //电柜参数
        TelephonyManager mTelephonyMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imsi = mTelephonyMgr.getSubscriberId();
        @SuppressLint("MissingPermission") String phone_code = mTelephonyMgr.getLine1Number();
        //线程保护
        String threadProtectionType = cabInfoSp.getTPTNumber();
        if (threadProtectionType.equals("1")) {
            thread_protection.setImageResource(R.drawable.image_6);
        } else if (threadProtectionType.equals("0")) {
            thread_protection.setImageResource(R.drawable.image_7);
        }
        //初始化控制页面
        String hardWareType = ChargingGetHardWare.getInstance().getHardWareType();
        if(hardWareType.equals("pdu_1_to_10")){
            set_power_panel.setVisibility(View.VISIBLE);
        }else if(hardWareType.equals("pdu_1_to_3")){
            charge_status_panel.setVisibility(View.VISIBLE);
        }
        //初始化充电模式
        String chargeType = cabInfoSp.getChargeMode();
        if (chargeType.equals("0")) {
            charge_status_1.setChecked(true);
            charge_status_2.setChecked(false);
        } else if (chargeType.equals("1")) {
            charge_status_2.setChecked(true);
            charge_status_1.setChecked(false);
        }
        //初始化充电功率
        String power = cabInfoSp.getMaxPower();
        if(power.equals("6000")){
            set_power_1.setChecked(true);
            set_power_2.setChecked(true);
        }else if(power.equals("9000")){
            set_power_1.setChecked(false);
            set_power_2.setChecked(true);
        }

        //每1秒更新数据
        disposable =  Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshData();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                                Date date = new Date(System.currentTimeMillis());
                                item_9_text.setText(simpleDateFormat.format(date));
                                item_7_text.setText(imsi);
                                item_8_text.setText(phone_code);
                            }
                        });
                    }
                });
    }

    /**
     * 控制板电池基础信息返回
     * @param canControlPlateServiceInfoBeanFormat
     */
    @Override
    public void batteryBaseBeanReturn(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat) {
        super.batteryBaseBeanReturn(canControlPlateServiceInfoBeanFormat);
        //获取信息
        int index = canControlPlateServiceInfoBeanFormat.getIndex();
        //更新内存数据
        controlPlateInfo.getControlPlateBaseBeans()[index] = (ControlPlateBaseBean)canControlPlateServiceInfoBeanFormat.getData();
    }
    /**
     * 控制板电池预警信息返回
     * @param canControlPlateServiceInfoBeanFormat
     */
    @Override
    public void batteryWarningBeanReturn(CanControlPlateServiceInfoBeanFormat canControlPlateServiceInfoBeanFormat) {
        super.batteryWarningBeanReturn(canControlPlateServiceInfoBeanFormat);
        controlPlateInfo.getControlPlateWarningBeans()[canControlPlateServiceInfoBeanFormat.getIndex()] = (ControlPlateWarningBean)canControlPlateServiceInfoBeanFormat.getData();
    }

    /**
     * PDU信息返回
     * @param canControlPlateServicePduInfoBeanFormat
     */
    @Override
    public void pduBeanReturn(CanControlPlateServicePduInfoBeanFormat canControlPlateServicePduInfoBeanFormat) {
        super.pduBeanReturn(canControlPlateServicePduInfoBeanFormat);
        pduInfo.setPduChargingInfoBean(canControlPlateServicePduInfoBeanFormat.getPduChargingInfoBean());
        pduInfo.setPduEquipmentBeans(canControlPlateServicePduInfoBeanFormat.getPduEquipmentBean());
    }

    /**
     * 超越控制板数据更新
     */
    protected void refreshData() {
        //初始化
        controlPlateBaseBeans.clear();
        controlPlateWarningBeans.clear();
        pduChargingInfoBeans.clear();
        pduEquipmentTopBeans.clear();
        pduEquipmentBottomBeans.clear();
        //电池数据刷新
        for(int i = 0 ; i < MAX_CABINET_COUNT ; i++){
            controlPlateBaseBeans.add(controlPlateInfo.getControlPlateBaseBeans()[i]);
            controlPlateWarningBeans.add(controlPlateInfo.getControlPlateWarningBeans()[i]);
            pduChargingInfoBeans.add(pduInfo.getPduChargingInfoBean()[i]);
            pduEquipmentTopBeans.add(pduInfo.getPduEquipmentBeans()[i+3]);
        }
        if (adminControlPlateTopAdapter == null) {
            adminControlPlateTopAdapter = new AdminControlPlateTopAdapter(activity, controlPlateBaseBeans,controlPlateWarningBeans,pduChargingInfoBeans,pduEquipmentTopBeans, cabInfoSp.getAndroidDeviceModel(), new AdminControlPlateTopAdapter.AdminTopAdapterListener() {
                @Override
                public void openDoor(int door) {
                    pushAndPull(door , "电柜后台开门");
                }
                @Override
                public void showDialog() {
                    dialogAdmin.showByTime(2);
                }
            });
            bar_info_girdview.setAdapter(adminControlPlateTopAdapter);
        } else {
            adminControlPlateTopAdapter.notifyDataSetChanged();
        }
        //充电机数据刷新
        for(int i = 0 ; i < MAX_CHARGE_COUNT ; i++){
            pduEquipmentBottomBeans.add(pduInfo.getPduEquipmentBeans()[i]);
        }
        if (adminControlPlateBottomAdapter == null) {
            adminControlPlateBottomAdapter = new AdminControlPlateBottomAdapter(activity, pduEquipmentBottomBeans,cabInfoSp.getAndroidDeviceModel());
            cha_info_girdview.setColumnWidth(3);
            cha_info_girdview.setAdapter(adminControlPlateBottomAdapter);
        } else {
            adminControlPlateBottomAdapter.notifyDataSetChanged();
        }
    }



    @OnClick(R.id.finish)
    public void onViewClick_finish() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(activity, "再按一次退出应用！", Toast.LENGTH_LONG).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;// 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            MyApplication.getInstance().exit();
        }
    }

    @OnClick(R.id.retuen_page)
    public void onViewClick_return() {
        this.finish();
    }

    @OnClick(R.id.thread_protection)
    public void onViewClickThreadProtection() {
        String threadProtectionType = cabInfoSp.getTPTNumber();
        if (threadProtectionType.equals("1")) {
            thread_protection.setImageResource(R.drawable.image_7);
            cabInfoSp.setTPTNumber("0");
        } else if (threadProtectionType.equals("0")) {
            thread_protection.setImageResource(R.drawable.image_6);
            cabInfoSp.setTPTNumber("1");
        }
    }

    @OnClick(R.id.item_4_text_buttom)
    public void onViewClick_download() {

        List<BaseHttpParameterFormat> baseHttpParameterFormats =  new ArrayList<>();
        BaseHttp baseHttp = new BaseHttp(HttpUrlMap.GetControlPlateVersionBin, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
            @Override
            public void dataReturn(int code, String message, String data) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == -1) {
                            System.out.println("网络 ：" + message);
                        } else if (code == 1) {
                            try {
                                JSONArray jsonArray = new JSONArray(data);
                                LayoutInflater inflater = LayoutInflater.from(activity);
                                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                final AlertDialog mAlertDialog = builder.create();
                                View view = inflater.inflate(R.layout.activity_admin_download_dialog, null);
                                ListView listView = (ListView) view.findViewById(R.id.listview);

                                final List<Map<String, String>> datalist = new ArrayList<>();
                                for (int i = jsonArray.length() - 1; i > -1; i--) {
                                    JSONObject jsonObject_item = jsonArray.getJSONObject(i);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("id", jsonObject_item.getString("id"));
                                    map.put("update_time", jsonObject_item.getString("update_time"));
                                    map.put("status", jsonObject_item.getString("status"));
                                    map.put("bata", jsonObject_item.getString("bata"));
                                    map.put("create_uid", jsonObject_item.getString("create_uid"));
                                    map.put("create_time", jsonObject_item.getString("create_time"));
                                    map.put("name", jsonObject_item.getString("name"));
                                    map.put("is_del", jsonObject_item.getString("is_del"));
                                    map.put("update_uid", jsonObject_item.getString("update_uid"));
                                    map.put("url", jsonObject_item.getString("url"));
                                    map.put("version", jsonObject_item.getString("version"));
                                    map.put("used", jsonObject_item.getString("used"));
                                    datalist.add(map);
                                }

                                SimpleAdapter simpleAdapter = new SimpleAdapter(activity, datalist, R.layout.activity_admin_download_dialog_item, new String[]{"name", "version", "create_time", "update_time"}, new int[]{R.id.t_1, R.id.t_2, R.id.t_3, R.id.t_4});
                                listView.setAdapter(simpleAdapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        String url = datalist.get(i).get("url");
                                        new UpdateHardWare(activity, "app.bin", url, new UpdateHardWare.DownloadUpdateFileListener() {
                                            @Override
                                            public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                                                Intent intent = new Intent(activity, A_UpDateControlPlate.class);
                                                intent.putExtra("door", 1 + "");
                                                intent.putExtra("type", 1 + "");
                                                intent.putExtra("path", dataPath);
                                                intent.putExtra("tel", cabInfoSp.getTelNumber());
                                                intent.putExtra("cab_id", cabInfoSp.getCabinetNumber_XXXXX());
                                                activity.startActivity(intent);
                                            }
                                        });
                                    }
                                });
                                mAlertDialog.show();
                                mAlertDialog.getWindow().setContentView(view);
                                mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                                dialogAdmin.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (code == 0) {

                        }
                    }
                });
            }
        });
        baseHttp.onStart();
        dialogAdmin.show();
    }

    @OnClick({R.id.charge_status_1 , R.id.charge_status_2 })
    public void onViewClickSetCharging(TextView view) {
        if (view.getId() == charge_status_1.getId()) {
            charge_status_1.setChecked(true);
            charge_status_2.setChecked(false);
            new CabInfoSp(activity).setChargeMode("0");
        } else if (view.getId() == charge_status_2.getId()) {
            charge_status_2.setChecked(true);
            charge_status_1.setChecked(false);
            new CabInfoSp(activity).setChargeMode("1");
        }
    }

    @OnClick({R.id.set_power_1 , R.id.set_power_2 })
    public void onViewClickSetPower(TextView view) {
        if (view.getId() == charge_status_1.getId()) {
            set_power_1.setChecked(true);
            set_power_2.setChecked(false);
            cabInfoSp.setMaxPower("60000");
        } else if (view.getId() == charge_status_2.getId()) {
            set_power_1.setChecked(true);
            set_power_2.setChecked(false);
            cabInfoSp.setMaxPower("90000");
        }
    }


}

