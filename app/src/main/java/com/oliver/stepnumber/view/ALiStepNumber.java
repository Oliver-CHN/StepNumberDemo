package com.oliver.stepnumber.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
public class ALiStepNumber extends View {
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
     * 小圆的画笔
     */
    private Paint smallCirclePaint;

    /**
     * “步数”画笔
     */
    private Paint numberText;

    /**
     * 今日步数字画笔
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

    public ALiStepNumber(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ALiStepNumber);
        numberStep = ta.getInt(R.styleable.ALiStepNumber_aLiNumberStr, 2000);
        ta.recycle();
        initPint();
    }

    private void initPint() {
        // 背景画笔
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(16);
        bgPaint.setColor(Color.parseColor("#EFEFEE"));
        // 覆盖层画笔
        coverPaint = new Paint();
        coverPaint.setAntiAlias(true);
        coverPaint.setStyle(Paint.Style.STROKE);
        coverPaint.setStrokeWidth(32);
        //两端变圆弧
        coverPaint.setStrokeCap(Paint.Cap.ROUND);
        // 为覆盖层画笔设置渐变渲染器
        SweepGradient mSweepGradient = new SweepGradient(this.getWidth() / 2, this.getHeight() / 2,
                new int[]{0xFFFFB401, 0x7FFFB401, 0xFFFFB401, 0x7FFFB401}, null);
        coverPaint.setShader(mSweepGradient);
        //步数
        numberText = new Paint();
        numberText.setAntiAlias(true);
        numberText.setColor(Color.BLACK);
        numberText.setTextSize(100);
        numberText.setTypeface(Typeface.DEFAULT_BOLD);
        numberText.setTextAlign(Paint.Align.CENTER);
        // 提示字
        hintText = new Paint();
        hintText.setAntiAlias(true);
        hintText.setColor(Color.GRAY);
        hintText.setTextSize(45);
        hintText.setTextAlign(Paint.Align.CENTER);
        //小圆
        smallCirclePaint = new Paint();
        smallCirclePaint.setAntiAlias(true);
        coverPaint.setStyle(Paint.Style.STROKE);
        smallCirclePaint.setColor(Color.WHITE);
        smallCirclePaint.setStrokeWidth(16);
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
        rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 5000步为一圈， 5000步也只显示一圈
        if (numberStep / 5000 < 1) {
            sweepAngle = (float) (360 * numberStep / 5000 * (currentProgress / 100.00));
        } else {
            sweepAngle = (float) (360 * (currentProgress / 100.00));
        }
        canvas.drawCircle(centerX, centerY, radius, bgPaint);
        canvas.drawArc(rectF, 270, sweepAngle, false, coverPaint);
        //小圆的坐标
        float smallCircleX = (float) (centerX + radius * Math.cos((sweepAngle - 90) * Math.PI / 180));
        float smallCircleY = (float) (centerY + radius * Math.sin((sweepAngle - 90) * Math.PI / 180));
        canvas.drawCircle(smallCircleX, smallCircleY, 10, smallCirclePaint);
        // 文字
        Paint.FontMetricsInt fontMetrics = numberText.getFontMetricsInt();
        // 基线
        int baseline = (int) (centerY - fontMetrics.top / 2 - fontMetrics.bottom / 2);
        // 字体宽度
        String numberStr = String.valueOf((int) (numberStep * currentProgress / 100));
        canvas.drawText(numberStr, centerX, baseline, numberText);
        // fontMetrics.top 为负数，所以只需要 +
        canvas.drawText("今日步数", centerX, centerX + fontMetrics.ascent, hintText);
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
