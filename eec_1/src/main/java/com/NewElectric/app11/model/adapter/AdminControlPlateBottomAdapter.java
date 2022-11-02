package com.NewElectric.app11.model.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.NewElectric.app11.R;
import com.NewElectric.app11.hardwarecomm.agreement.receive.pdu.PduEquipmentBean;
import com.NewElectric.app11.units.Units;

/**
 * A_Admin页面 电池信息 适配器
 */

public class AdminControlPlateBottomAdapter extends BaseAdapter {

    private Context context;
    private int resources;
    private List<PduEquipmentBean> pduEquipmentList = new ArrayList<>();

    public AdminControlPlateBottomAdapter(Context context , List<PduEquipmentBean> pduEquipmentList , String hardWareType) {
        this.context = context;
        this.pduEquipmentList = pduEquipmentList;
        if(hardWareType.equals("rk3288_box")){
            resources = R.layout.activity_admin_1080p_controlplate_grid_item_2;
        }else if(hardWareType.equals("SABRESD-MX6DQ")){
            resources = R.layout.activity_admin_720p_grid_item_2;
        }
    }

    @Override
    public int getCount() { return pduEquipmentList.size(); }

    @Override
    public Object getItem(int i) {
        return pduEquipmentList.get(i);
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

            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.t_1 = (TextView) convertView.findViewById(R.id.t_1);
            viewHolder.t_2 = (TextView) convertView.findViewById(R.id.t_2);
            viewHolder.t_3 = (TextView) convertView.findViewById(R.id.t_3);
            viewHolder.t_4 = (TextView) convertView.findViewById(R.id.t_4);
            viewHolder.t_5 = (TextView) convertView.findViewById(R.id.t_5);
            viewHolder.t_6 = (TextView) convertView.findViewById(R.id.t_6);
            viewHolder.t_7 = (TextView) convertView.findViewById(R.id.t_7);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText("充电机：0" + (i + 1));
        viewHolder.t_1.setText("开");
        viewHolder.t_2.setText("正常");
        viewHolder.t_3.setText("54.6/68.4");
        viewHolder.t_4.setText("16A");

        byte[] bytes = pduEquipmentList.get(i).getData();
        if (bytes[0]!=0) {
            viewHolder.t_5.setText(pduEquipmentList.get(i).getVersion());
            viewHolder.t_6.setText(Units.ByteArrToHex(bytes).substring(8,16));
            viewHolder.t_7.setText(pduEquipmentList.get(i).getError());
        } else {
            viewHolder.t_5.setText("无");
            viewHolder.t_6.setText("无");
            viewHolder.t_7.setText("无");
        }
        return convertView;

    }

    private class ViewHolder {
        TextView title;
        TextView t_1;
        TextView t_2;
        TextView t_3;
        TextView t_4;
        TextView t_5;
        TextView t_6;
        TextView t_7;
    }
}