package com.NewElectric.app11.service.logic.logic_exchange;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 离线换电 动画
 */
public class ExchangeBarAnimation {

    private LinearLayout info_panel, cost, dialogPanel;
    private ImageView black_1, black_2;
    private FrameLayout up_bar_2_panel, up_bar_1_panel;
    private int time = 1000;
    private int height = 1000 + 100;
    private int height_2 = -(580 + 100);

    public interface IFExchangeAnimationListener {
        void onExchangeAnimationStart();

        void onExchangeAnimationEnd();
    }

    IFExchangeAnimationListener ifExchangeAnimationListener;

    public ExchangeBarAnimation(String hardwareType, LinearLayout info_panel, LinearLayout cost, ImageView black_1, ImageView black_2, FrameLayout up_bar_2_panel, FrameLayout up_bar_1_panel, LinearLayout mDialogPanel, IFExchangeAnimationListener ifExchangeAnimationListener) {
        this.info_panel = info_panel;
        this.cost = cost;
        this.black_1 = black_1;
        this.black_2 = black_2;
        this.up_bar_1_panel = up_bar_1_panel;
        this.up_bar_2_panel = up_bar_2_panel;
        this.dialogPanel = mDialogPanel;

        if (hardwareType.equals("rk3288_box")) {
            height_2 = -(580 + 100);
        } else if (hardwareType.equals("SABRESD-MX6DQ")) {
            height_2 = -(350 + 100);
        }


        this.ifExchangeAnimationListener = ifExchangeAnimationListener;

    }

    public ExchangeBarAnimation(LinearLayout info_panel, LinearLayout cost, ImageView black_1, ImageView black_2, FrameLayout up_bar_2_panel, FrameLayout up_bar_1_panel, LinearLayout mDialogPanel, IFExchangeAnimationListener ifExchangeAnimationListener) {
        this.info_panel = info_panel;
        this.cost = cost;
        this.black_1 = black_1;
        this.black_2 = black_2;
        this.up_bar_1_panel = up_bar_1_panel;
        this.up_bar_2_panel = up_bar_2_panel;
        this.dialogPanel = mDialogPanel;
        this.ifExchangeAnimationListener = ifExchangeAnimationListener;

    }

    public void startAnimation() {
        An_1();
    }

    //换电期间 动画
    private void An_1() {

        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(info_panel, "translationY", -100, height_2 - 60);
        ObjectAnimator objectAnimator_dialog = ObjectAnimator.ofFloat(dialogPanel, "translationY", 0, height_2 + 30);
        objectAnimator.setDuration(time);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                ifExchangeAnimationListener.onExchangeAnimationStart();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                An_2();
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
        animatorSet.play(objectAnimator).with(objectAnimator_dialog);//两个动画同时开始
        animatorSet.start();
    }

    private void An_2() {
        AnimatorSet animatorSet_1 = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(black_1, "scaleX", 0, 1);
        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(black_1, "scaleY", 0, 1);
        objectAnimator_1.setDuration(time);

        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                black_1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                An_3();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animatorSet_1.setDuration(1000);
        animatorSet_1.setInterpolator(new DecelerateInterpolator());
        animatorSet_1.play(objectAnimator_1).with(objectAnimator_2);//两个动画同时开始
        animatorSet_1.start();

        AnimatorSet animatorSet_2 = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator_3 = ObjectAnimator.ofFloat(black_2, "scaleX", 0, 1);
        ObjectAnimator objectAnimator_4 = ObjectAnimator.ofFloat(black_2, "scaleY", 0, 1);
        objectAnimator_3.setDuration(time);

        objectAnimator_3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                black_2.setVisibility(View.VISIBLE);
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

        animatorSet_2.setDuration(1000);
        animatorSet_2.setInterpolator(new DecelerateInterpolator());
        animatorSet_2.play(objectAnimator_3).with(objectAnimator_4);//两个动画同时开始
        animatorSet_2.start();
    }

    private void An_3() {

        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(up_bar_1_panel, "translationY", height, 0);
        objectAnimator_1.setDuration(time);
        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                up_bar_1_panel.setVisibility(View.VISIBLE);
                up_bar_2_panel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                An_4();

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        objectAnimator_1.start();

        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(up_bar_2_panel, "translationY", height, 0);
        objectAnimator_2.setDuration(time);
        objectAnimator_2.start();
    }

    private void An_4() {

        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(cost, "alpha", 0, 1);
        objectAnimator_1.setDuration(time);
        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                cost.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        An_5();
                    }
                }, 5000);//3秒后执行Runnable中的run方法
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        objectAnimator_1.start();
    }

    private void An_5() {

        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(cost, "alpha", 1, 0);
        objectAnimator_1.setDuration(time);
        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                cost.setVisibility(View.GONE);
                An_6();

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator_1.start();
    }


    private void An_6() {

        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(up_bar_1_panel, "translationY", 0, height);
        objectAnimator_1.setDuration(time);
        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                up_bar_1_panel.setVisibility(View.GONE);
                up_bar_2_panel.setVisibility(View.GONE);
                An_7();

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator_1.start();
        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(up_bar_2_panel, "translationY", 0, height);
        objectAnimator_2.setDuration(time);
        objectAnimator_2.start();
    }


    private void An_7() {
        AnimatorSet animatorSet_1 = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator_1 = ObjectAnimator.ofFloat(black_1, "scaleX", 1, 0);
        ObjectAnimator objectAnimator_2 = ObjectAnimator.ofFloat(black_1, "scaleY", 1, 0);
        objectAnimator_1.setDuration(time);

        objectAnimator_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                black_1.setVisibility(View.GONE);
                An_8();

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animatorSet_1.setDuration(1000);
        animatorSet_1.setInterpolator(new DecelerateInterpolator());
        animatorSet_1.play(objectAnimator_1).with(objectAnimator_2);//两个动画同时开始
        animatorSet_1.start();

        AnimatorSet animatorSet_2 = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator_3 = ObjectAnimator.ofFloat(black_2, "scaleX", 1, 0);
        ObjectAnimator objectAnimator_4 = ObjectAnimator.ofFloat(black_2, "scaleY", 1, 0);
        objectAnimator_3.setDuration(time);

        objectAnimator_3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                black_2.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animatorSet_2.setDuration(1000);
        animatorSet_2.setInterpolator(new DecelerateInterpolator());
        animatorSet_2.play(objectAnimator_3).with(objectAnimator_4);//两个动画同时开始
        animatorSet_2.start();

    }

    private void An_8() {
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(info_panel, "translationY", height_2 - 60, -100);
        ObjectAnimator objectAnimator_dialog = ObjectAnimator.ofFloat(dialogPanel, "translationY", height_2 + 30, 0);
        objectAnimator.setDuration(time);

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ifExchangeAnimationListener.onExchangeAnimationEnd();
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
        animatorSet.play(objectAnimator).with(objectAnimator_dialog);//两个动画同时开始
        animatorSet.start();
    }

}
