package com.NewElectric.app11.controller.custom;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.NewElectric.app11.R;
import com.NewElectric.app11.units.Units;
import com.squareup.picasso.Picasso;

import butterknife.internal.Utils;
import okhttp3.internal.Util;

public class BlackExchangeAnimation extends FrameLayout {

    private Context context;
    private Activity activity;

    private RelativeLayout panel_1;
    private TextView t_1;
    private Thread panel_1_thread;
    private boolean panel_1_threadCode = true;
    private int panel_1_threadCount = -1;


    private RelativeLayout panel_2;
    private TextView t_2;

    private RelativeLayout panel_3;
    private TextView t_3;

    public BlackExchangeAnimation(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BlackExchangeAnimation(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public BlackExchangeAnimation(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }


    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_main_1080p_black_animation, null);
        panel_1 = view.findViewById(R.id.panel_1);
        panel_2 = view.findViewById(R.id.panel_2);
        panel_3 = view.findViewById(R.id.panel_3);
        t_1 = view.findViewById(R.id.t_1);
        t_2 = view.findViewById(R.id.t_2);
        t_3 = view.findViewById(R.id.t_3);
        ImageView imageView_1 = view.findViewById(R.id.image_1);
        ImageView imageView_2 = view.findViewById(R.id.image_2);
        ImageView imageView_3 = view.findViewById(R.id.image_3);
        Picasso.with(context).load(R.drawable.black_image_exhange_bar_bg_1).resize(400, 400).into(imageView_1);
        Picasso.with(context).load(R.drawable.black_image_exhange_bar_bg_2).resize(400, 400).into(imageView_2);
        Picasso.with(context).load(R.drawable.black_image_exhange_bar_bg_3).resize(400, 400).into(imageView_3);
        addView(view);
    }


    /**
     * 检测微动 弹出大提示框
     * @param door
     */
    public void inchingStart(int door) {
        panel_1_threadCount = 30;
        t_1.setText("正在检测"+door+"号仓电池,请稍候！");
        final int time = 1000;
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_1, "scaleX", 0, 1.2f);
        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(panel_1, "scaleY", 0, 1.2f);
        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                panel_1.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.setDuration(time);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(objectAnimator_1).with(objectAnimator_2);//两个动画同时开始
        animatorSet.start();

        if(panel_1_thread == null){
            panel_1_thread = new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (panel_1_threadCode == true){
                        try {
                            sleep(1000);

                            if(panel_1_threadCount > 0){
                                panel_1_threadCount = panel_1_threadCount - 1;
                            }

                            if(panel_1_threadCount == 0){
                                panel_1_threadCount = -1;
                                finishPanel_1();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            panel_1_thread.start();
        }
    }

    /**
     * 换电动画
     */
    public void exchangeStart(int type , int inPer , int outPer , String info){

        panel_1_threadCount = -1;
        //写入参数
        t_2.setText(inPer +"%");
        t_3.setText(outPer+"%");

        //第一大提示框缩回
        final int time = 300;
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_1, "scaleX", 1.2f, 0.4f);
        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(panel_1, "scaleY", 1.2f, 0.4f);
        ObjectAnimator objectAnimator_3 = ObjectAnimator.ofFloat(panel_1, "translationX", -Units.dip2px(context, 665));
        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                batteryInfoStartStep_1();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.setDuration(time);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(objectAnimator_1).with(objectAnimator_2).with(objectAnimator_3);//两个动画同时开始
        animatorSet.start();
    }


    //第二大提示框放大
    private void batteryInfoStartStep_1() {
        final int time = 1000;
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_2, "scaleX", 0, 1.2f);
        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(panel_2, "scaleY", 0, 1.2f);
        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                panel_2.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                batteryInfoStartStep_2();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.setDuration(time);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(objectAnimator_1).with(objectAnimator_2);//两个动画同时开始
        animatorSet.start();
    }

    //第二大提示框缩回
    private void batteryInfoStartStep_2() {
        final int time = 300;
        AnimatorSet animatorSet_1 = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_2, "scaleX", 1.2f, 0.6f);
        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(panel_2, "scaleY", 1.2f, 0.6f);
        ObjectAnimator objectAnimator_3 = ObjectAnimator.ofFloat(panel_2, "translationX", -Units.dip2px(context, 520));
        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                panel_2.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                batteryInfoStartStep_3();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet_1.setDuration(time);
        animatorSet_1.setInterpolator(new DecelerateInterpolator());
        animatorSet_1.play(objectAnimator_1).with(objectAnimator_2).with(objectAnimator_3);//两个动画同时开始
        animatorSet_1.start();
    }

    //第三大提示框放大
    private void batteryInfoStartStep_3() {
        final int time = 1000;
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_3, "scaleX", 0, 1.2f);
        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(panel_3, "scaleY", 0, 1.2f);
        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                panel_3.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                batteryInfoStartStep_4();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.setDuration(time);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(objectAnimator_1).with(objectAnimator_2);//两个动画同时开始
        animatorSet.start();
    }

    //第三大提示框缩回
    private void batteryInfoStartStep_4() {
        final int time = 1000;
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_3, "scaleX", 1.2f, 1);
        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(panel_3, "scaleY", 1.2f, 1);
        ObjectAnimator objectAnimator_3 = ObjectAnimator.ofFloat(panel_3,"translationX", -Units.dip2px(context, 270));
        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                panel_3.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                batteryInfoStartStep_5();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.setDuration(time);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(objectAnimator_1).with(objectAnimator_2).with(objectAnimator_3);//两个动画同时开始
        animatorSet.start();
    }

    //全体回归
    private void batteryInfoStartStep_5() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(10 * 1000);
                    finishPanel_1();
                    sleep(300);
                    finishPanel_2();
                    sleep(300);
                    finishPanel_3();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void finishPanel_1(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int time = 1000;
                AnimatorSet animatorSet_1 = new AnimatorSet();//组合动画
                ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_1, "translationY", Units.dip2px(context, 1500));
                objectAnimator_1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        panel_1.setVisibility(GONE);
                        AnimatorSet animatorSet = new AnimatorSet();//组合动画
                        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_1, "scaleX", 1, 1);
                        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(panel_1, "scaleY", 1, 1);
                        ObjectAnimator objectAnimator_3 = ObjectAnimator.ofFloat(panel_1,"translationX",0);
                        ObjectAnimator objectAnimator_4 = ObjectAnimator.ofFloat(panel_1,"translationY",0);
                        animatorSet.setDuration(10);
                        animatorSet.setInterpolator(new DecelerateInterpolator());
                        animatorSet.play(objectAnimator_1).with(objectAnimator_2).with(objectAnimator_3).with(objectAnimator_4);//两个动画同时开始
                        animatorSet.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                animatorSet_1.setDuration(time);
                animatorSet_1.setInterpolator(new DecelerateInterpolator());
                animatorSet_1.play(objectAnimator_1);//两个动画同时开始
                animatorSet_1.start();
            }
        });
    }
    private void finishPanel_2(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int time = 1000;
                AnimatorSet animatorSet_1 = new AnimatorSet();//组合动画
                ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_2, "translationY", Units.dip2px(context, 1500));
                objectAnimator_1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        panel_2.setVisibility(GONE);
                        AnimatorSet animatorSet = new AnimatorSet();//组合动画
                        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_2, "scaleX", 1, 1);
                        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(panel_2, "scaleY", 1, 1);
                        ObjectAnimator objectAnimator_3 = ObjectAnimator.ofFloat(panel_2,"translationX",0);
                        ObjectAnimator objectAnimator_4 = ObjectAnimator.ofFloat(panel_2,"translationY",0);
                        animatorSet.setDuration(10);
                        animatorSet.setInterpolator(new DecelerateInterpolator());
                        animatorSet.play(objectAnimator_1).with(objectAnimator_2).with(objectAnimator_3).with(objectAnimator_4);//两个动画同时开始
                        animatorSet.start();
                    }
                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                animatorSet_1.setDuration(time);
                animatorSet_1.setInterpolator(new DecelerateInterpolator());
                animatorSet_1.play(objectAnimator_1);//两个动画同时开始
                animatorSet_1.start();
            }
        });
    }
    private void finishPanel_3(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int time = 1000;
                AnimatorSet animatorSet_1 = new AnimatorSet();//组合动画
                ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_3, "translationY", Units.dip2px(context, 1500));
                objectAnimator_1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        panel_3.setVisibility(GONE);
                        AnimatorSet animatorSet = new AnimatorSet();//组合动画
                        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(panel_3, "scaleX", 1, 1);
                        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(panel_3, "scaleY", 1, 1);
                        ObjectAnimator objectAnimator_3 = ObjectAnimator.ofFloat(panel_3,"translationX",0);
                        ObjectAnimator objectAnimator_4 = ObjectAnimator.ofFloat(panel_3,"translationY",0);
                        animatorSet.setDuration(10);
                        animatorSet.setInterpolator(new DecelerateInterpolator());
                        animatorSet.play(objectAnimator_1).with(objectAnimator_2).with(objectAnimator_3).with(objectAnimator_4);//两个动画同时开始
                        animatorSet.start();
                    }
                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }
                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                animatorSet_1.setDuration(time);
                animatorSet_1.setInterpolator(new DecelerateInterpolator());
                animatorSet_1.play(objectAnimator_1);//两个动画同时开始
                animatorSet_1.start();
            }
        });
    }

    public void onDestroy(){

    }

}

