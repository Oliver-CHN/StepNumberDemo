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
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.oliver.stepnumber.R;

/**
 * Created by Administrator on 2018/5/25.
 */
public class QQStepNumber extends View {
    /**
     * 背景画笔
     */
    private Paint bgPaint;

    /**
     * 覆盖层画笔
     *
     * @param context
     */
    private Paint coverPaint;

    /**
     * “步数”画笔
     */
    private Paint numberText;

    /**
     * “步”字画笔
     */
    private Paint stepText;

    /**
     * 提示字画笔
     */
    private Paint hintText;

    /**
     * 步数
     */
    private int numberStep;

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
        numberStep = ta.getInt(R.styleable.QQStepNumber_qqNumberStr, 2000);
        initPint();
    }

    private void initPint() {
        // 背景画笔
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(20);
        bgPaint.setColor(Color.parseColor("#E1EDFF"));
        // 覆盖层画笔
        coverPaint = new Paint();
        coverPaint.setAntiAlias(true);
        coverPaint.setStyle(Paint.Style.STROKE);
        coverPaint.setStrokeWidth(20);
        // 为覆盖层画笔设置渐变渲染器
        SweepGradient mSweepGradient = new SweepGradient(this.getWidth() / 2, this.getHeight() / 2,
                new int[]{0xFF1571FA, 0x7F1571FA, 0xFF1571FA, 0x7F1571FA}, null);
        coverPaint.setShader(mSweepGradient);
        //步数
        numberText = new Paint();
        numberText.setAntiAlias(true);
        numberText.setColor(Color.BLACK);
        numberText.setTextSize(100);
        numberText.setTypeface(Typeface.DEFAULT_BOLD);
        numberText.setTextAlign(Paint.Align.CENTER);
        //步字
        stepText = new Paint();
        stepText.setAntiAlias(true);
        stepText.setColor(Color.BLACK);
        stepText.setTextSize(45);
        stepText.setTextAlign(Paint.Align.CENTER);
        // 提示字
        hintText = new Paint();
        hintText.setAntiAlias(true);
        hintText.setColor(Color.GRAY);
        hintText.setTextSize(45);
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
        if (numberStep / 5000 < 1) {
            sweepAngle = (float) (240 * numberStep / 5000 * (currentProgress / 100.00));
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
        String numberStr = String.valueOf((int) (numberStep * currentProgress/100));
        numberText.getTextBounds(numberStr, 0, numberStr.length(), numberRect);

        Rect stepRect = new Rect();
        stepText.getTextBounds("步", 0, 1, stepRect);

        canvas.drawText(numberStr, centerX - stepRect.width() * 2 / 3, baseline, numberText);
        canvas.drawText("步", centerX + numberRect.width() / 2, baseline, stepText);
        canvas.drawText("满5000步打卡", centerX, centerX + radius / 2, hintText);
    }

    public void start() {
        animationToCircle().start();
    }

    private ValueAnimator animationToCircle() {
        animator = ValueAnimator.ofFloat(0, 100);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
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
