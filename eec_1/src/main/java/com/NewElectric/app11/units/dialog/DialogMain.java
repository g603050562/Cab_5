package com.NewElectric.app11.units.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogMain {

    public interface SpeakDialogListener{
        void onSpeakDialogReturn(String msg);
    }

    private Activity activity;
    private LinearLayout panel,mInfoPanel;
    private TextView time, info;
    //线程相关
    private int threadStatus = 0;
    private Thread thread;
    private int mtime = -100;
    int open_an = 0;
    private DialogMainAnimation dialogMainAnimation;

    private SpeakDialogListener speakDialogListener;

    public DialogMain(Activity mActivity, LinearLayout mPanel, TextView mTime, TextView mInfo , LinearLayout mInfoPanel , SpeakDialogListener speakDialogListener) {
        this.activity = mActivity;
        this.panel = mPanel;
        this.time = mTime;
        this.info = mInfo;
        this.mInfoPanel = mInfoPanel;
        this.speakDialogListener = speakDialogListener;
        init();
    }

    private void init(){
        dialogMainAnimation = new DialogMainAnimation(activity,mInfoPanel, panel, new DialogMainAnimation.IFDialogAnimationListener() {
            @Override
            public void onStartDialogAnimationStart() {

            }

            @Override
            public void onStartDialogAnimationEnd() {

            }

            @Override
            public void onCloseDialogAnimationStart() {

            }

            @Override
            public void onCloseDialogAnimationEnd() {

            }
        });

        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (threadStatus == 0) {

                    if (activity == null) {
                        break;
                    }

                    if (mtime > 0) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time.setText(mtime + "");
                            }
                        });
                        mtime = mtime - 1;
                    } else if (mtime <= 0 && mtime!=-100) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogMainAnimation.closeAnimation();
                                open_an = 0;
                            }
                        });
                        mtime = -100;
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


    public void show(String msg_str, int time_int, int speakType) {
        try {
            if (activity == null) {
                return;
            }
            time.setText(time_int + "");
            info.setText(msg_str + "");
            mtime = time_int;
            if(open_an == 0){
                dialogMainAnimation.startAnimation();
                open_an = 1;
            }
            if(speakType == 1){
                speakDialogListener.onSpeakDialogReturn(msg_str);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void onDestory() {
        panel.setVisibility(View.GONE);
        threadStatus = 1;
    }
}
