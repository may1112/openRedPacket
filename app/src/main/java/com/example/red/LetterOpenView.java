package com.example.red;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.example.red.inter.OnLetterClickListener;
import com.example.red.tool.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.xuexiang.xutil.XUtil.runOnUiThread;

public class LetterOpenView extends DialogFragment implements View.OnClickListener {

    private ImageView ivCover;
    private ImageView ivTriangle;
    private ConstraintLayout layoutLetter;
    private ImageView btnSeal;
    /**
     * 信纸
     */
    private RelativeLayout layoutLetterPaper;
    private ImageView ivUserPhoto;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvContent;
    private TextView btnNext;
    private TextView btnReply;
    /**
     * 信封
     */
    private ConstraintLayout.LayoutParams layoutLetterPaperParams;
    private ImageView ivCancel;
    /**
     * 信封
     */
    private LinearLayout envelope;
    private ImageView ivBag;
    /**
     *
     */
    private ImageButton imageButton;

    private long animationTime = 1000;
    /**
     * 判断只打开一次
     */
    private boolean openLetter = false;
    /**
     * 信纸内容
     */
    private LinearLayout layoutLetterContent;
    /**
     * 整块的父布局
     */
    private RelativeLayout container;
    /**
     * 缩放动画监听
     * 显示 取消按钮和增加背景透明色
     */
    private ZoomFromThumbAnimation zoomFromThumbAnimation;

    /**
     * 假数据
     */
    private List<String> strings;
    /**
     * 多封信  当前第几封
     */
    private int pos = 0;

    private Dialog dialog;
    private View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = new Dialog(getContext());
        view = LayoutInflater.from(getContext()).inflate(R.layout.activity_reverse_animation, null);
        dialog.setContentView(view);
        setStyle(dialog.getWindow());
        return dialog;
    }

    private void setStyle(Window window) {
        //背景透明度
        //WindowManager.LayoutParams layoutParams = window.getAttributes();
        //layoutParams.dimAmount = 0.0f;

        window.setWindowAnimations(R.style.MyDialogAnimation);
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        CommonUtil.setStatusBarTransparent(window);
    }

    private OnLetterClickListener onLetterClickListener;

    public void setOnLetterClickListener(OnLetterClickListener onLetterClickListener) {
        this.onLetterClickListener = onLetterClickListener;
    }

    private void initView() {



        strings = new ArrayList<>();
        ivTriangle = view.findViewById(R.id.ivTriangle);
        ivBag = view.findViewById(R.id.ivBagBG);
        ivCancel = view.findViewById(R.id.iv_cancel);
        ivCover = view.findViewById(R.id.ivCover);
        container = view.findViewById(R.id.container);
        layoutLetter = view.findViewById(R.id.layout_letter);
        layoutLetterContent = view.findViewById(R.id.layout_content);
        btnSeal = view.findViewById(R.id.btn_seal);
        layoutLetterPaper = view.findViewById(R.id.layout_letter_paper);
        envelope = view.findViewById(R.id.layout_letter_dialog);
//
        ivUserPhoto = view.findViewById(R.id.iv_user_photo);
        tvName = view.findViewById(R.id.tv_name);
        tvTime = view.findViewById(R.id.tv_time);
        tvContent = view.findViewById(R.id.tv_content);
        btnNext = view.findViewById(R.id.btn_next);
        btnReply = view.findViewById(R.id.btn_reply);
        btnNext.setOnClickListener(this);
        btnReply.setOnClickListener(this);
        btnSeal.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        layoutLetterPaperParams = (ConstraintLayout.LayoutParams) layoutLetterPaper.getLayoutParams();
        zoomFromThumbAnimation = ZoomFromThumbAnimation.getIntents();
        zoomFromThumbAnimation.setOnAnimationClick(new ZoomFromThumbAnimation.OnAnimationClick() {
            @Override
            public void onOneAnimationEnd() {

                showView();

            }
        });
//        imageButton = view.findViewById(R.id.ib_small);



    }

    private void showView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivCancel.setVisibility(VISIBLE);//此时已在主线程中，更新UI
            }
        });
    }


    public void openLetterAnimation(float x, float y) {
        if (zoomFromThumbAnimation != null) {
            imageButton.setVisibility(INVISIBLE);
            imageButton.setX(x);
            imageButton.setY(y);
            imageButton.post(new Runnable() {
                @Override
                public void run() {
                    zoomFromThumbAnimation.zoomImageFromThumb(imageButton, container, envelope);
                }
            });
        }
    }

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





    private void refreshView() {

        if (strings.size() <= 0) {
            return;
        }
        tvContent.setText(strings.get(pos));
        if (pos == strings.size() - 1) {
            btnNext.setVisibility(GONE);
        } else {
            btnNext.setVisibility(VISIBLE);
        }
        pos++;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_seal:
                //打开动画
                if (!openLetter) {
                    if (onLetterClickListener != null) onLetterClickListener.readFruits();
                    openLetter = true;
                    ivBag.post(new Runnable() {
                        @Override
                        public void run() {

                            layoutLetterPaperParams.height = ivBag.getHeight();
                            layoutLetterPaperParams.width = ivBag.getWidth() - 20;
                            rotate();

                        }
                    });
                }
                break;
            case R.id.iv_cancel:
                if (onLetterClickListener != null) {
                    onLetterClickListener.dismiss();
                }
                envelope.setVisibility(GONE);
                break;
            case R.id.btn_reply:
                if (onLetterClickListener != null) {
                    onLetterClickListener.writeBack(pos);
                }
                break;
            case R.id.btn_next:
                if (onLetterClickListener != null) {
                    pos++;
                    onLetterClickListener.next(pos);

                }
//                refreshView();
                break;
        }
    }
}
