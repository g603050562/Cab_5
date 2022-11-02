package com.NewElectric.app11.units.dialog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

public class DialogMainAnimation {

    private Activity activity;
    private LinearLayout info_panel;
    private LinearLayout dialog_panel;

    private int time = 200;


    public interface IFDialogAnimationListener {
        void onStartDialogAnimationStart();

        void onStartDialogAnimationEnd();

        void onCloseDialogAnimationStart();

        void onCloseDialogAnimationEnd();
    }

    IFDialogAnimationListener iFDialogAnimationListener;

    public DialogMainAnimation(Activity activity, LinearLayout info_panel, LinearLayout dialog_panel, IFDialogAnimationListener iFDialogAnimationListener) {
        this.info_panel = info_panel;
        this.dialog_panel = dialog_panel;
        this.iFDialogAnimationListener = iFDialogAnimationListener;
    }

    public void startAnimation() {
        An_1();
    }

    public void closeAnimation() {
        An_3();
    }


    private void An_1() {

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(info_panel, "translationY", 0, -100);
        objectAnimator.setDuration(time);

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                iFDialogAnimationListener.onStartDialogAnimationStart();
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
        objectAnimator.start();
    }

    private void An_2() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(dialog_panel, "alpha", 0, 1);
        objectAnimator.setDuration(time);

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                dialog_panel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                iFDialogAnimationListener.onStartDialogAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        objectAnimator.start();
    }

    private void An_3() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(dialog_panel, "alpha", 1, 0);
        objectAnimator.setDuration(time);

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                iFDialogAnimationListener.onCloseDialogAnimationStart();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                dialog_panel.setVisibility(View.GONE);
                An_4();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        objectAnimator.start();
    }

    private void An_4() {

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(info_panel, "translationY", -100, 0);
        objectAnimator.setDuration(time);

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                iFDialogAnimationListener.onCloseDialogAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        objectAnimator.start();
    }

}