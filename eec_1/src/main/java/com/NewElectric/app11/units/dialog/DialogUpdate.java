package com.NewElectric.app11.units.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.NewElectric.app11.R;
import com.NewElectric.app11.units.Units;


public class DialogUpdate {

    private Activity activity;
    private int mtime = -1;
    private AlertDialog dialog;
    private TextView t_1, t_2;
    private Handler handler;
    private View view;
    private int threadStatus = 0;

    private Thread thread;

    public DialogUpdate(Activity mActivity) {


        this.activity = mActivity;

        if (activity == null) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        dialog = builder.create();
        view = inflater.inflate(R.layout.alertdialog_update_1080p, null);

        dialog.getWindow().setDimAmount(0);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.y = Units.dip2px(activity, -250); // 新位置Y坐标
        lp.width = Units.dip2px(activity, 500); // 宽度
        lp.height = Units.dip2px(activity, 180); // 高度

        dialogWindow.setAttributes(lp);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                t_1.setText(mtime + "");
            }
        };

        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (threadStatus == 0) {

                    if (activity == null) {
                        break;
                    }

                    try {
                        if (mtime > 0) {
                            handler.sendMessage(new Message());
                            mtime = mtime - 1;
                        } else if (mtime <= 0) {
                            dialog.dismiss();
                            mtime = -1;
                        }
                    } catch (Exception e) {

                        System.out.println("dialog：" + e.toString());

                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        thread.start();

    }

    public DialogUpdate(Activity mActivity, int small) {


        this.activity = mActivity;

        if (activity == null) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        dialog = builder.create();
        view = inflater.inflate(R.layout.alertdialog_update_720p, null);

        dialog.getWindow().setDimAmount(0);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.y = Units.dip2px(activity, -250); // 新位置Y坐标
        lp.width = Units.dip2px(activity, 500); // 宽度
        lp.height = Units.dip2px(activity, 180); // 高度

        dialogWindow.setAttributes(lp);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                t_1.setText(mtime + "");
            }
        };

        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (threadStatus == 0) {

                    if (activity == null) {
                        break;
                    }

                    try {
                        if (mtime > 0) {
                            handler.sendMessage(new Message());
                            mtime = mtime - 1;
                        } else if (mtime <= 0) {
                            dialog.dismiss();
                            mtime = -1;
                        }
                    } catch (Exception e) {

                        System.out.println("dialog：" + e.toString());

                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        thread.start();

    }


    public void show(String msg, int time) {

        if (activity == null) {
            return;
        }

        try {
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();

            lp.y = Units.dip2px(activity, 0); // 新位置Y坐标
            lp.width = Units.dip2px(activity, 700); // 宽度
            lp.height = Units.dip2px(activity, 180); // 高度

            dialogWindow.setAttributes(lp);


            mtime = time;

            t_1 = (TextView) view.findViewById(R.id.t_1);
            t_2 = (TextView) view.findViewById(R.id.t_2);
            t_2.setText(msg + "");

            dialog.show();
            dialog.getWindow().setContentView(view);
        } catch (Exception e) {

        }
    }

    public void show(String msg, int time, int i) {

        if (activity == null) {
            return;
        }

        try {
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();

            if (i == 1) {
                lp.y = Units.dip2px(activity, -250); // 新位置Y坐标
                lp.x = Units.dip2px(activity, 0); // 新位置Y坐标
            } else if (i == 2) {
                lp.x = Units.dip2px(activity, 50); // 新位置Y坐标
                lp.y = Units.dip2px(activity, 0); // 新位置Y坐标
            } else if (i == 3) {
                lp.y = Units.dip2px(activity, -400); // 新位置Y坐标
                lp.x = Units.dip2px(activity, 50); // 新位置Y坐标
            }else if(i == 4){
                lp.x = Units.dip2px(activity, 25); // 新位置Y坐标
                lp.y = Units.dip2px(activity, 0); // 新位置Y坐标
            }else if(i == 5){
                lp.y = Units.dip2px(activity, -400); // 新位置Y坐标
                lp.x = Units.dip2px(activity, 25); // 新位置Y坐标
            } else {
                lp.x = Units.dip2px(activity, 0); // 新位置Y坐标
                lp.y = Units.dip2px(activity, 0); // 新位置Y坐标
            }

            lp.width = Units.dip2px(activity, 700); // 宽度
            lp.height = Units.dip2px(activity, 180); // 高度

            dialogWindow.setAttributes(lp);


            mtime = time;

            t_1 = (TextView) view.findViewById(R.id.t_1);
            t_2 = (TextView) view.findViewById(R.id.t_2);
            t_2.setText(msg + "");

            dialog.show();
            dialog.getWindow().setContentView(view);
        } catch (Exception e) {

        }
    }

    public void onDestory() {
        threadStatus = 1;
        dialog.dismiss();
    }
}
