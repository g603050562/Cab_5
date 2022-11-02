package com.NewElectric.app11.model.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.NewElectric.app11.R;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateBaseBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.controlPlate.ControlPlateWarningBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduChargingInfoBean;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduEquipmentBean;
import com.NewElectric.app11.service.canAndSerial.can.controlPlate.CanControlPlateService;

/**
 * A_Admin页面 充电器信息 适配器
 */

public class AdminControlPlateTopAdapter extends BaseAdapter {

    public interface AdminTopAdapterListener{
        void openDoor(int door);
        void showDialog();
    }

    private Context context;
    private List<ControlPlateBaseBean> controlPlateBaseBeans = new ArrayList<>();
    private List<ControlPlateWarningBean> controlPlateWarningBeans = new ArrayList<>();
    private List<PduChargingInfoBean> pduChargingInfoBeans = new ArrayList<>();
    private List<PduEquipmentBean> pduEquipmentBeans = new ArrayList<>();
    private int resources;
    private AdminTopAdapterListener adminTopAdapterListener;


    public AdminControlPlateTopAdapter(Context context, List<ControlPlateBaseBean> controlPlateBaseBeans, List<ControlPlateWarningBean> controlPlateWarningBeans, List<PduChargingInfoBean> pduChargingInfoBeans, List<PduEquipmentBean> pduEquipmentBeans, String hardWareType, AdminTopAdapterListener adminTopAdapterListener) {
        this.context = context;
        this.controlPlateBaseBeans = controlPlateBaseBeans;
        this.controlPlateWarningBeans = controlPlateWarningBeans;
        this.pduChargingInfoBeans = pduChargingInfoBeans;
        this.pduEquipmentBeans = pduEquipmentBeans;
        if(hardWareType.equals("rk3288_box")){
            resources = R.layout.activity_admin_1080p_controlplate_grid_item_1;
        }else if(hardWareType.equals("SABRESD-MX6DQ")){
            resources = R.layout.activity_admin_720p_grid_item_1;
        }
        this.adminTopAdapterListener = adminTopAdapterListener;
    }


    @Override
    public int getCount() {
        return controlPlateBaseBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return controlPlateBaseBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (null == convertView) {

            convertView = View.inflate(context, resources, null);
            viewHolder = new ViewHolder();

            viewHolder.a_1 = (TextView) convertView.findViewById(R.id.a_1);
            viewHolder.t_1 = (TextView) convertView.findViewById(R.id.bid);
            viewHolder.t_2 = (TextView) convertView.findViewById(R.id.dianya);
            viewHolder.t_3 = (TextView) convertView.findViewById(R.id.dianliang);
            viewHolder.t_4 = (TextView) convertView.findViewById(R.id.dianliu);
            viewHolder.t_5 = (TextView) convertView.findViewById(R.id.wendu);
            viewHolder.t_6 = (TextView) convertView.findViewById(R.id.inner_lock);
            viewHolder.t_7 = (TextView) convertView.findViewById(R.id.frame_loss_rate);
            viewHolder.t_8 = (TextView) convertView.findViewById(R.id.t_8);
            viewHolder.t_9 = (TextView) convertView.findViewById(R.id.t_9);
            viewHolder.t_10 = (TextView) convertView.findViewById(R.id.t_10);
            viewHolder.t_11 = (TextView) convertView.findViewById(R.id.t_11);
            viewHolder.t_12 = (TextView) convertView.findViewById(R.id.side_lock);
            viewHolder.t_13 = (TextView) convertView.findViewById(R.id.t_13);
            viewHolder.t_14 = (TextView) convertView.findViewById(R.id.t_14);
            viewHolder.t_16 = (TextView) convertView.findViewById(R.id.t_16);
            viewHolder.t_17 = (TextView) convertView.findViewById(R.id.uid);
            viewHolder.t_18 = (TextView) convertView.findViewById(R.id.t_18);
            viewHolder.t_19 = (TextView) convertView.findViewById(R.id.t_19);
            viewHolder.t_20 = (TextView) convertView.findViewById(R.id.t_20);
            viewHolder.open_door = (TextView) convertView.findViewById(R.id.open_door);
            viewHolder.charge = (TextView) convertView.findViewById(R.id.charge);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.open_door.setTag(i);
        viewHolder.open_door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminTopAdapterListener.openDoor(Integer.parseInt(view.getTag().toString()) + 1);
                adminTopAdapterListener.showDialog();
            }
        });

        if (i + 1 < 10) {
            viewHolder.a_1.setText("0" + (i + 1));
        } else {
            viewHolder.a_1.setText((i + 1) + "");
        }

        if (controlPlateBaseBeans == null || controlPlateBaseBeans.get(i) == null) {
            return convertView;
        }

        viewHolder.t_1.setText(controlPlateBaseBeans.get(i).getBID());
        //uid从canService里面拿实时的
        viewHolder.t_17.setText(CanControlPlateService.getInstance().getControlPlateInfo().getControlPlateBaseBeans()[i].getUID());
        viewHolder.t_2.setText(controlPlateBaseBeans.get(i).getBatteryVoltage()+"V");
        viewHolder.t_3.setText(controlPlateBaseBeans.get(i).getBatteryRelativeSurplus()+"%");

        int dianliu_item_int = controlPlateBaseBeans.get(i).getBatteryElectric();
        if (dianliu_item_int > 20000) {
            int max = 256 * 256;
            dianliu_item_int = max - dianliu_item_int;
            String dianliu_return = "-" + dianliu_item_int;
            viewHolder.t_4.setText(dianliu_return + "mA");
        } else {
            viewHolder.t_4.setText(dianliu_item_int + "mA");
        }

        viewHolder.t_5.setText(controlPlateBaseBeans.get(i).getBatteryTemperature()+"C");
        int inching_1 = controlPlateBaseBeans.get(i).getInching_1(); //底部微动
        int inching_3 = controlPlateBaseBeans.get(i).getInching_3(); //侧边微动
        if (inching_1 == 1) {
            viewHolder.t_6.setText("有电池");
            viewHolder.t_6.setTextColor(0xff008000);
        } else if (viewHolder.t_6.getText().equals("-1")) {
            viewHolder.t_6.setText("操作中");
            viewHolder.t_6.setTextColor(0xffcccccc);
        } else {
            viewHolder.t_6.setText("没电池");
            viewHolder.t_6.setTextColor(0xfff06b00);
        }
        if (inching_3 == 1) {
            viewHolder.t_12.setText("有电池");
            viewHolder.t_12.setTextColor(0xff008000);
        } else if (viewHolder.t_12.getText().equals("-1")) {
            viewHolder.t_12.setText("操作中");
            viewHolder.t_12.setTextColor(0xffcccccc);
        } else {
            viewHolder.t_12.setText("没电池");
            viewHolder.t_12.setTextColor(0xfff06b00);
        }

        String chargeStatus = pduChargingInfoBeans.get(i).getStatus();
        viewHolder.charge.setText(chargeStatus);

        String chargingStopStatus = pduChargingInfoBeans.get(i).getStopStatus();
        viewHolder.t_8.setText(chargingStopStatus);
        String chargingErrorStatus = pduChargingInfoBeans.get(i).getErrorStatus();
        viewHolder.t_9.setText(chargingErrorStatus);
        String equipmentError = pduEquipmentBeans.get(i).getError();
        viewHolder.t_11.setText(equipmentError);
        String equipmentVersion = pduEquipmentBeans.get(i).getVersion();
        viewHolder.t_10.setText(equipmentVersion);

        String barVersion = controlPlateBaseBeans.get(i).getBatteryVersion();
        viewHolder.t_13.setText("BH:" + Integer.parseInt(barVersion.substring(0, 2), 16) + "  " + "BS:" + Integer.parseInt(barVersion.substring(2, 4), 16) + "");
        viewHolder.t_14.setText("V" + controlPlateBaseBeans.get(i).getControlPlateTopVersion());
        viewHolder.t_16.setText(controlPlateBaseBeans.get(i).getTemperatureSensor_2() + "C");

        viewHolder.t_18.setText(controlPlateWarningBeans.get(i).getWarningInfo());
        viewHolder.t_19.setText(controlPlateWarningBeans.get(i).getErrorInfo());
        viewHolder.t_20.setText(controlPlateWarningBeans.get(i).getMosInfo());

        return convertView;

    }

    private class ViewHolder {
        TextView a_1;
        TextView t_1;
        TextView t_2;
        TextView t_3;
        TextView t_4;
        TextView t_5;
        TextView t_6;
        TextView t_7;
        TextView t_8;
        TextView t_9;
        TextView t_10;
        TextView t_11;
        TextView t_12;
        TextView t_13;
        TextView t_14;
        TextView t_16;
        TextView t_17;
        TextView t_18;
        TextView t_19;
        TextView t_20;
        TextView open_door;
        TextView charge;
    }

}