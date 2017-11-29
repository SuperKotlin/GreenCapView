package com.zhuyong.greencapview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情包view
 * Created by zhuyong on 2017/11/14.
 */

public class GreenCapView extends RelativeLayout {
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 起点坐标
     */
    private float mStartX, mStartY;
    /**
     * 终点坐标（可动态改变）
     */
    private float endX, endY;

    /**
     * 罗尼玛的宽高、抛帽子的人的宽高
     */
    private int mLuoNiMaWidth = 250;
    /**
     * 帽子的宽度
     */
    private int mGreenCapWidth = 150;
    /**
     * 帽子的高度
     */
    private int mGreenCapHeight = 100;
    /**
     * 绿帽子叠起之后的间隔长度
     */
    private int mIntervalLength = 38;//40像素;
    /**
     * 绿帽子集合
     */
    List<View> listGreenView = new ArrayList<>();
    /**
     * 字“啪”的显示坐标
     */
    private float mPaX, mPaY;

    public GreenCapView(Context context) {
        this(context, null);
    }

    public GreenCapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GreenCapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    /**
     * 添加初始化view
     *
     * @param resource
     * @param params
     */
    private void addPeopleView(int resource, ViewGroup.LayoutParams params) {
        ImageView mViewMMP = new ImageView(mContext);
        mViewMMP.setImageResource(resource);
        mViewMMP.setLayoutParams(params);
        addView(mViewMMP);
    }

    /**
     * 初始化参数
     */
    private void initView() {
        setBackgroundColor(Color.WHITE);
        /**
         * 添加戴帽子的人，位于左下角
         */
        LayoutParams layoutParams = new LayoutParams(mLuoNiMaWidth, mLuoNiMaWidth);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        addPeopleView(R.mipmap.icon_biaoqingbao, layoutParams);
        /**
         * 添加抛帽子的人，位于右下角
         */
        LayoutParams layoutParams2 = new LayoutParams(mLuoNiMaWidth, mLuoNiMaWidth);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addPeopleView(R.mipmap.people_start, layoutParams2);
        /**
         * 初始化帽子的起点坐标
         */
        mStartX = ScreenUtils.getScreenWidth(mContext)
                - mLuoNiMaWidth
                + 15;
        mStartY = ScreenUtils.getScreenHeight(mContext)
                - ScreenUtils.getStatusHeight(mContext)//状态栏的高度
                - ScreenUtils.dip2px(mContext, 55) //title的高度
                - mGreenCapHeight//绿帽子的高度
                - mLuoNiMaWidth;//抛帽子小人的高度
        /**
         * 初始化帽子的终点坐标
         */
        endX = (mLuoNiMaWidth - mGreenCapWidth) / 2;
        endY = ScreenUtils.getScreenHeight(mContext)
                - ScreenUtils.getStatusHeight(mContext)//状态栏的高度
                - ScreenUtils.dip2px(mContext, 55) //title的高度
                - mGreenCapHeight//绿帽子的高度
                - mLuoNiMaWidth//抛帽子小人的高度
                + mIntervalLength//抵消第一个帽子减去的高度
                + 40;//戴帽子效果
        /**
         * 初始化“啪”的坐标
         */
        mPaX = ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dip2px(mContext, 30);
        mPaY = mStartY - 50;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                startAnimation();
            default:
                break;
        }
        return true;
    }

    /**
     * 开始动画
     */
    private void startAnimation() {
        endY = endY - mIntervalLength;
        /**
         * 判断如果已经达到边界，则不进行addView
         */
        if (endY <= 0) {
            return;
        }
        View mViewGood = getCap();
        listGreenView.add(mViewGood);
        addView(mViewGood);
        getGreenCapValueAnimator(mViewGood).start();
        /**
         * 显示啪字动画
         */
        if (mTextAnimationOver) {
            startAnimationText();
        }
    }

    /**
     * "啪"字动画是否结束标记，作用是防止字体重合
     */
    private boolean mTextAnimationOver = true;

    /**
     * "啪"字动画
     */
    private void startAnimationText() {
        mTextAnimationOver = false;
        final TextView textView = new TextView(mContext);
        textView.setText("啪");
        textView.setTextColor(Color.parseColor("#333333"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_18sp));
        textView.setX(mPaX);
        textView.setY(mPaY);
        addView(textView);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(textView, "ScaleX", 0f, 1.0f)
                , ObjectAnimator.ofFloat(textView, "ScaleY", 0f, 1.0f));
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView(textView);
                mTextAnimationOver = true;
            }
        });
    }

    /**
     * 获得一个绿帽子
     */
    private View getCap() {
        final View mViewGood = new View(mContext);
        mViewGood.setBackgroundResource(R.mipmap.icon_greencap);
        mViewGood.setLayoutParams(new ViewGroup.LayoutParams(mGreenCapWidth, mGreenCapHeight));
        return mViewGood;
    }

    /**
     * 获得一个绿帽子动画
     */
    private ValueAnimator getGreenCapValueAnimator(final View mViewGood) {
        ValueAnimator animator = ValueAnimator.ofObject(new BezierEvaluator(new PointF(300, 300))
                , new PointF(mStartX, mStartY), new PointF(endX, endY));
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PointF pointF = (PointF) valueAnimator.getAnimatedValue();
                mViewGood.setX(pointF.x);
                mViewGood.setY(pointF.y);
            }
        });

        return animator;
    }

    /**
     * 初始化部分参数和remove不要的view
     */
    public void removeAllGreenCaps() {
        for (int i = 0; i < listGreenView.size(); i++) {
            if (listGreenView.get(i) != null) {
                removeView(listGreenView.get(i));
            }
        }
        listGreenView.clear();
        /**
         * 重新计算终点坐标
         */
        endY = ScreenUtils.getScreenHeight(mContext)
                - ScreenUtils.getStatusHeight(mContext)//状态栏的高度
                - ScreenUtils.dip2px(mContext, 55) //title的高度
                - mGreenCapHeight//绿帽子的高度
                - mLuoNiMaWidth//抛帽子小人的高度
                + mIntervalLength//抵消第一个帽子减去的高度
                + 40;//戴帽子效果
    }

    /**
     * 帽子飞起动画
     */
    private ValueAnimator valueAnimator;
    /**
     * 自动模式默认关闭
     */
    private boolean mAutomaticPatternStatus = false;

    public boolean ismAutomaticPatternStatus() {
        return mAutomaticPatternStatus;
    }

    /**
     * 自动模式控制开关
     */
    public void automaticPatternSwitch() {
        if (mAutomaticPatternStatus) {
            mAutomaticPatternStatus = false;
            closeAutomaticPattern();
        } else {
            mAutomaticPatternStatus = true;
            openAutomaticPattern();
        }
    }

    /**
     * 开启自动模式
     */
    private void openAutomaticPattern() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(150);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                Log.i("TAG动画", "动画开始再次执行了");
                startAnimation();

            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Log.i("TAG动画", "动画开始执行了哦哦");
                startAnimation();

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                Log.i("TAG动画", "动画取消");
            }
        });
        valueAnimator.start();
    }

    /**
     * 关闭自动模式
     */
    private void closeAutomaticPattern() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }

    /**
     * 自定义贝塞尔插值器
     */
    class BezierEvaluator implements TypeEvaluator<PointF> {

        private PointF pointF;

        /**
         * 控制点坐标
         */
        public BezierEvaluator(PointF pointF) {
            this.pointF = pointF;
        }

        @Override
        public PointF evaluate(float time, PointF startValue, PointF endValue) {
            float timeOn = 1.0f - time;
            PointF point = new PointF();
            //二阶贝塞尔公式
            point.x = timeOn * timeOn * (startValue.x)
                    + 2 * timeOn * time * (pointF.x)
                    + time * time * (endValue.x);

            point.y = timeOn * timeOn * (startValue.y)
                    + 2 * timeOn * time * (pointF.y)
                    + time * time * (endValue.y);
            return point;
        }
    }
}
