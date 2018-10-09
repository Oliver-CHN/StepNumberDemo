package com.oliver.stepnumber.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.oliver.stepnumber.R;

/**
 * Created by Administrator on 2018/5/25.
 */
public class QQStepNumber extends View {
    /**
     * 背景画笔
     */
    private Paint bgPaint;
    private int bgColor;

    /**
     * 覆盖层画笔
     *
     * @param context
     */
    private Paint coverPaint;

    /**
     * 步数
     */
    private int numberStep;

    /**
     * “步数”画笔
     */
    private Paint numberText;
    private int numberTextSize;
    private int numberTextColor;

    /**
     * “步”字画笔
     */
    private Paint stepText;

    /**
     * 提示字画笔
     */
    private Paint hintText;
    private int hintTextSize;
    private int hintTextColor;

    /**
     * 画笔宽度（默认20）
     */
    private int strokeWidth;

    /**
     * 渐变颜色数组
     */
    private int gradualColor;

    /**
     * 上限步数
     */
    private int limitNumber;

    /**
     * 提示文字
     */
    private String hintTextStr;

    /**
     * 步数右侧的文字
     */
    private String numberRightStr;
    private int numberRightTextSize;
    private int numberRightTextColor;

    /**
     * 宽高、圆弧的中心点、圆弧半径、当前进度、扫过的弧度、矩形、动画
     */
    private int mWidth, mHeight;
    private float centerX, centerY;
    private float radius;
    private float currentProgress = 0f;
    private float sweepAngle = 0;
    private RectF rectF;
    private ValueAnimator animator;

    public QQStepNumber(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.QQStepNumber);
        bgColor = ta.getResourceId(R.styleable.QQStepNumber_bgColor, R.color.bg_color);
        gradualColor = ta.getResourceId(R.styleable.QQStepNumber_gradualColor, R.array.qq_step);
        strokeWidth = ta.getDimensionPixelOffset(R.styleable.QQStepNumber_strokeWidth, 20);
        numberStep = ta.getInt(R.styleable.QQStepNumber_qqNumber, 2000);
        numberTextSize = ta.getDimensionPixelSize(R.styleable.QQStepNumber_numberTextSize, 32);
        numberTextColor = ta.getResourceId(R.styleable.QQStepNumber_numberTextColor, Color.BLACK);
        numberRightTextColor = ta.getResourceId(R.styleable.QQStepNumber_numberRightTextColor, Color.GRAY);
        numberRightTextSize = ta.getDimensionPixelSize(R.styleable.QQStepNumber_numberRightTextSize, 16);
        numberRightStr = ta.getString(R.styleable.QQStepNumber_numberRightStr);
        hintTextSize = ta.getDimensionPixelSize(R.styleable.QQStepNumber_hintTextSize, 16);
        hintTextColor = ta.getResourceId(R.styleable.QQStepNumber_hintTextColor, Color.GRAY);
        hintTextStr = ta.getString(R.styleable.QQStepNumber_hintTextStr);
        limitNumber = ta.getInt(R.styleable.QQStepNumber_limitNumber, 5000);
        ta.recycle();
        initPint();
    }

    private void initPint() {
        // 背景画笔
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(strokeWidth);
        bgPaint.setColor(ContextCompat.getColor(getContext(), bgColor));
        // 覆盖层画笔
        coverPaint = new Paint();
        coverPaint.setAntiAlias(true);
        coverPaint.setStyle(Paint.Style.STROKE);
        coverPaint.setStrokeWidth(strokeWidth);
        // 为覆盖层画笔设置渐变渲染器
        SweepGradient mSweepGradient = new SweepGradient(this.getWidth() / 2, this.getHeight() / 2,
                getResources().getIntArray(gradualColor), null);
        coverPaint.setShader(mSweepGradient);
        //步数
        numberText = new Paint();
        numberText.setAntiAlias(true);
        numberText.setColor(ContextCompat.getColor(getContext(), numberTextColor));
        numberText.setTextSize(numberTextSize);
        numberText.setTypeface(Typeface.DEFAULT_BOLD);
        numberText.setTextAlign(Paint.Align.CENTER);
        //步字
        stepText = new Paint();
        stepText.setAntiAlias(true);
        stepText.setColor(ContextCompat.getColor(getContext(), numberRightTextColor));
        stepText.setTextSize(numberRightTextSize);
        stepText.setTextAlign(Paint.Align.CENTER);
        // 提示字
        hintText = new Paint();
        hintText.setAntiAlias(true);
        hintText.setColor(ContextCompat.getColor(getContext(), hintTextColor));
        hintText.setTextSize(hintTextSize);
        hintText.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取控件宽高，并且保存
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
        measure();
    }

    private void measure() {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int width = mWidth - paddingLeft - paddingRight;
        int height = mHeight - paddingBottom - paddingTop;
        centerX = paddingLeft + width / 2;
        centerY = paddingTop + height / 2;
        // 半径 = 长、宽最小值 / 2 - 边界线的宽度（StrokeWidth）
        radius = Math.min(width, height) / 2 - 20;
        // 根据圆弧的周长来设置虚线的长度
        double dashedWidth = Math.PI * radius * 2 * 240 / 360 / 240;
        PathEffect effects = new DashPathEffect(new float[]{(float) dashedWidth, (float) dashedWidth}, 0);
        // 为圆弧设置虚线
        bgPaint.setPathEffect(effects);
        coverPaint.setPathEffect(effects);
        rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 5000步为一圈， 5000步也只显示一圈
        if (numberStep / limitNumber < 1) {
            sweepAngle = (float) (240 * numberStep / limitNumber * (currentProgress / 100.00));
        } else {
            sweepAngle = (float) (240 * (currentProgress / 100.00));
        }
        // 画圆弧
        canvas.drawArc(rectF, 150, 240, false, bgPaint);
        canvas.drawArc(rectF, 150, sweepAngle, false, coverPaint);
        // 文字
        Paint.FontMetricsInt fontMetrics = numberText.getFontMetricsInt();
        // 基线
        int baseline = (int) (centerY - fontMetrics.top / 2 - fontMetrics.bottom / 2);
        // 字体宽度
        Rect numberRect = new Rect();
        String numberStr = String.valueOf((int) (numberStep * currentProgress / 100));
        numberText.getTextBounds(numberStr, 0, numberStr.length(), numberRect);

        Rect stepRect = new Rect();
        if (!TextUtils.isEmpty(numberRightStr)) {
            stepText.getTextBounds(numberRightStr, 0, 1, stepRect);
        }

        canvas.drawText(numberStr, centerX - stepRect.width() * 2 / 3, baseline, numberText);
        if (!TextUtils.isEmpty(numberRightStr)) {
            canvas.drawText(numberRightStr, centerX + numberRect.width() / 2, baseline, stepText);
        }
        if (!TextUtils.isEmpty(hintTextStr)) {
            canvas.drawText(hintTextStr, centerX, centerX + radius / 2, hintText);
        }
    }

    public void start() {
        animationToCircle().start();
    }

    private ValueAnimator animationToCircle() {
        animator = ValueAnimator.ofFloat(0, 100);
        animator.setDuration(2000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        return animator;
    }

    public void cancelAnimator() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }
}
