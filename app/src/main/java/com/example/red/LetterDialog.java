package com.example.red;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.red.tool.LogUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.xuexiang.xutil.XUtil.runOnUiThread;

public class LetterDialog extends AlertDialog {
    private ImageView initialView;
    private LinearLayout envelope;
    /**
     * 缩放动画
     */
    private ZoomFromThumbAnimation zoomFromThumbAnimation;
    private RelativeLayout container;
    private ImageView btnCancel;
    private ImageView btnSeal;
    private ImageView ivCover;
    private ImageView ivTriangle;

    /**
     * 打开信封动画
     */
    private boolean isOpenLetter = false;
    private ImageView ivBag;

    /**
     * 信纸  动态计算高宽
     */
    private RelativeLayout layoutLetterPaper;
    private ConstraintLayout.LayoutParams layoutLetterPaperParams;
    private LinearLayout layoutLetterContent;


    protected LetterDialog(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.activity_reverse_animation, null);

        setView(view);
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                zoomFromThumbAnimation.zoomImageFromThumb(initialView, container, envelope);
            }
        });
    }

    @Override
    public void setView(View view) {
        super.setView(view);
        initialView = view.findViewById(R.id.btn_bubble);
        envelope = view.findViewById(R.id.layout_letter_dialog);
        btnCancel = view.findViewById(R.id.iv_cancel);
        container = view.findViewById(R.id.container);
        btnSeal = view.findViewById(R.id.btn_seal);
        ivBag = view.findViewById(R.id.ivBagBG);
        ivCover = view.findViewById(R.id.ivCover);
        ivTriangle = view.findViewById(R.id.ivTriangle);
        layoutLetterContent = view.findViewById(R.id.layout_content);

        layoutLetterPaper = view.findViewById(R.id.layout_letter_paper);
        layoutLetterPaperParams = (ConstraintLayout.LayoutParams) layoutLetterPaper.getLayoutParams();

        zoomFromThumbAnimation = ZoomFromThumbAnimation.getIntents();
        zoomFromThumbAnimation.setOnAnimationClick(new ZoomFromThumbAnimation.OnAnimationClick() {
            @Override
            public void onOneAnimationEnd() {

                showView();

            }
        });
        initialView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomFromThumbAnimation.zoomImageFromThumb(initialView, container, envelope);

            }
        });

        btnSeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpenLetter) return;
                isOpenLetter = true;
                openLetterAnim();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetView();
                dismiss();
            }
        });
    }

    /**
     * 重置布局
     */
    private void resetView() {
        ivCover.setScaleY(1);
        ivCover.setVisibility(VISIBLE);
        ivTriangle.setVisibility(View.INVISIBLE);
        isOpenLetter = false;
        layoutLetterPaper.setVisibility(View.INVISIBLE);
        layoutLetterPaperParams.height = ivBag.getHeight();
        layoutLetterPaper.setLayoutParams(layoutLetterPaperParams);

    }

    /*
    打开信封动画
    * */
    private void openLetterAnim() {
        ivBag.post(new Runnable() {
            @Override
            public void run() {

                layoutLetterPaperParams.height = ivBag.getHeight();
                layoutLetterPaperParams.width = ivBag.getWidth() - 20;
                rotate();

            }
        });
    }

    private void showView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnCancel.setVisibility(VISIBLE);//此时已在主线程中，更新UI
            }
        });
    }

    /**
     * 添加起始点
     */
    public void addInitialPoint(final ImageView view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                initialView.setX(view.getX());
                initialView.setY(view.getY());

            }
        });
    }

    private long animationTime = 1000;

    public void rotate() {

        //信纸
        layoutLetterPaper.setLayoutParams(layoutLetterPaperParams);

        ivCover.setPivotY(0);
        final ObjectAnimator animator1 = ObjectAnimator.ofFloat(ivCover, "scaleY", 1f, 0f);
        animator1.setDuration(animationTime / 2);
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                btnSeal.setVisibility(View.GONE);
                showView();
                layoutLetterPaper.setVisibility(VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ivCover.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        ivTriangle.setPivotY(ivTriangle.getHeight());
//        钱包打开背面
        final ObjectAnimator animator2 = ObjectAnimator.ofFloat(ivTriangle, "scaleY", 0, 1f);
        animator2.setDuration(animationTime / 2);
        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ivTriangle.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //信纸  信纸顶部高度根据内容变化出现
        ValueAnimator va = ValueAnimator.ofInt(layoutLetterPaperParams.height, (layoutLetterPaperParams.height * 2) + layoutLetterContent.getHeight() + 30);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int h = (int) animation.getAnimatedValue();
                layoutLetterPaperParams.height = h;
                layoutLetterPaper.setLayoutParams(layoutLetterPaperParams);

            }
        });


        va.setDuration(animationTime * 2);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(animator2).after(animator1);
        animatorSet.play(va).after(animator2);
        animatorSet.start();

    }


}
