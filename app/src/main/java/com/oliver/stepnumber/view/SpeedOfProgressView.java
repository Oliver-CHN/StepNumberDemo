package com.oliver.stepnumber.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.oliver.stepnumber.R;

public class SpeedOfProgressView extends View {
    /**
     * 背景画笔
     */
    private Paint bgPaint;

    /**
     * 覆盖层画笔
     */
    private Paint coverPaint;

    /**
     * 当前进度
     */
    private int current;

    /**
     * 百分比进度画笔
     */
    private Paint numberText;
    private int textSize;
    private int textColor;

    /**
     * 宽度
     */
    private int bgStrokeWidth;
    private int coverStrokeWidth;

    /**
     * 颜色
     */
    private int bgColor;
    private int coverColor;

    /**
     * 阈值
     */
    private int maximumLimit;

    /**
     * 宽高、圆弧的中心点、圆弧半径、当前进度、扫过的弧度、矩形、动画
     */
    private int mWidth, mHeight;
    private float centerX, centerY;
    private float radius;
    private RectF rectF;
    private ValueAnimator animator;

    /**
     * 首次是否需要绘制完成
     */
    private boolean isShow = false;

    public SpeedOfProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpeedOfProgressView);
        bgColor = ta.getResourceId(R.styleable.SpeedOfProgressView_pBgColor, R.color.lightGrey);
        bgStrokeWidth = ta.getDimensionPixelOffset(R.styleable.SpeedOfProgressView_pBgStrokeWidth, 16);
        coverColor = ta.getResourceId(R.styleable.SpeedOfProgressView_pCoverColor, R.color.redEF5050);
        coverStrokeWidth = ta.getDimensionPixelOffset(R.styleable.SpeedOfProgressView_pCoverStrokeWidth, 32);
        current = ta.getInt(R.styleable.SpeedOfProgressView_pCurrent, 0);
        textSize = ta.getDimensionPixelSize(R.styleable.SpeedOfProgressView_pTextSize, 32);
        textColor = ta.getResourceId(R.styleable.SpeedOfProgressView_pTextColor, Color.BLACK);
        maximumLimit = ta.getInt(R.styleable.SpeedOfProgressView_pMax, 100);
        ta.recycle();
        initPint();
    }

    private void initPint() {
        // 背景画笔
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(bgStrokeWidth);
        bgPaint.setColor(ContextCompat.getColor(getContext(), bgColor));
        // 覆盖层画笔
        coverPaint = new Paint();
        coverPaint.setAntiAlias(true);
        coverPaint.setStyle(Paint.Style.STROKE);
        coverPaint.setStrokeWidth(coverStrokeWidth);
        coverPaint.setColor(ContextCompat.getColor(getContext(), coverColor));
        //两端变圆弧
        coverPaint.setStrokeCap(Paint.Cap.ROUND);
        coverPaint.setStyle(Paint.Style.STROKE);
        //步数
        numberText = new Paint();
        numberText.setAntiAlias(true);
        numberText.setColor(ContextCompat.getColor(getContext(), textColor));
        numberText.setTextSize(textSize);
        numberText.setTypeface(Typeface.DEFAULT_BOLD);
        numberText.setTextAlign(Paint.Align.CENTER);
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
        centerX = paddingLeft + (width >> 1);
        centerY = paddingTop + (height >> 1);
        // 半径 = 长、宽最小值 / 2 - 边界线的宽度（StrokeWidth）
        radius = (Math.min(width, height) >> 1) - 20;
        rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, radius, bgPaint);
        if (!isShow) {
            drawText(canvas, 0);
            return;
        }
        float sweepAngle;
        if (current / maximumLimit < 1) {
            sweepAngle = (float) (360 * current / maximumLimit);
        } else {
            sweepAngle = (float) (360 * (current / 100.00));
        }
        canvas.drawArc(rectF, 270, sweepAngle, false, coverPaint);
        drawText(canvas, current);
    }

    private void drawText(Canvas canvas, int numText) {
        // 文字
        Paint.FontMetricsInt fontMetrics = numberText.getFontMetricsInt();
        // 基线
        int baseline = (int) (centerY - (fontMetrics.top >> 1) - (fontMetrics.bottom >> 1));
        // 字体宽度
        String numberStr = numText + "%";
        canvas.drawText(numberStr, centerX, baseline, numberText);
    }

    public void start() {
        isShow = true;
        animationToCircle().start();
    }

    private ValueAnimator animationToCircle() {
        animator = ValueAnimator.ofInt(0, current);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                current = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        return animator;
    }

    public void setCurrent(int current) {
        isShow = true;
        this.current = current;
        animationToCircle().start();
    }

    public void cancelAnimator() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }
}
