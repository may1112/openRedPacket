package com.example.red;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class ZoomFromThumbAnimation {
    //当前正在进行的动画
    private AnimatorSet mCurrentAnimation;
    private long animationTime = 1500;

    private static ZoomFromThumbAnimation zoomFromThumbAnimation;

    public static ZoomFromThumbAnimation getIntents() {
        if (zoomFromThumbAnimation == null) {
            zoomFromThumbAnimation = new ZoomFromThumbAnimation();
        }
        return zoomFromThumbAnimation;
    }

    /**
     * 缩放动画
     *
     * @param thumbView  缩略图
     * @param parentView 背景
     * @param childView  移动对象
     */
    public void zoomImageFromThumb(final View thumbView, View parentView, final View childView) {
        //如果当前有动画就取消，并执行当前的动画
        if (mCurrentAnimation != null) {
            mCurrentAnimation.cancel();
        }


        //计算放大图像的起始边界和结束边界
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        //起始边界是缩略图的全局可见矩形，最后的边界是容器container的全局可见矩形
        //将容器视图container的偏移量设置为区域的起源，因为定位动画的起源属性是（X，Y）
        thumbView.getGlobalVisibleRect(startBounds);

        parentView
                .getGlobalVisibleRect(finalBounds, globalOffset);

        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        //使用center_crop将起始边界调整为与最终边界相同的纵横比边界
        //这可以防止在动画期间的不良拉伸。还计算开始缩放比例
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            //横向放大
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            //纵向放大
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        //隐藏缩略图并显示放大视图
        //当动画开始时，它会将放大的视图定位在原来缩略图的位置
        thumbView.setAlpha(0f);
        childView.setVisibility(View.VISIBLE);


        //设置SCALE_X和SCALE_Y转换的轴心点为到放大后视图的左上角（默认值是视图的中心）
        childView.setPivotX(0f);
        childView.setPivotY(0f);


        //四种动画同时播放
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(childView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(childView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(childView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(childView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(animationTime);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimation = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimation = null;
            }
        });

        childView.setVisibility(View.VISIBLE);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                childView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (onAnimationClick != null) {
                    onAnimationClick.onOneAnimationEnd();
                }
//                ivCancel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();

        mCurrentAnimation = set;

        //从这往下就是给大图设置点击监听，完成缩小回去的过程
//        final float startScaleFinal = startScale;
//        dialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mCurrentAnimation != null) {
//                    mCurrentAnimation.cancel();
//                }
//
//                AnimatorSet set = new AnimatorSet();
//                set.play(ObjectAnimator
//                        .ofFloat(dialog, View.X, startBounds.left))
//                        .with(ObjectAnimator
//                                .ofFloat(dialog,
//                                        View.Y, startBounds.top))
//                        .with(ObjectAnimator
//                                .ofFloat(dialog,
//                                        View.SCALE_X, startScaleFinal))
//                        .with(ObjectAnimator
//                                .ofFloat(dialog,
//                                        View.SCALE_Y, startScaleFinal));
//                set.setDuration(mShortAnimationDuration);
//                set.setInterpolator(new DecelerateInterpolator());
//                set.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        thumbView.setAlpha(1f);
//                        dialog.setVisibility(View.GONE);
//                        mCurrentAnimation = null;
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//                        thumbView.setAlpha(1f);
//                        dialog.setVisibility(View.GONE);
//                        mCurrentAnimation = null;
//                    }
//                });
//                set.start();
//                mCurrentAnimation = set;
//            }
//        });
    }

    private OnAnimationClick onAnimationClick;

    public void setOnAnimationClick(OnAnimationClick onAnimationClick) {
        this.onAnimationClick = onAnimationClick;
    }

    public interface OnAnimationClick {
        void onOneAnimationEnd();
    }

}
