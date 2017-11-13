package cn.xjw.mvp_kotlin.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

/**
 * Created by xjw on 2017/10/27 10:44
 * Email : 1521975316@qq.com
 */

public class XRulerView extends View {



    public interface OnChooseResulterListener {

        void onEndResult(String result);

        void onScrollResult(String result);
    }

    private OnChooseResulterListener onChooseResulterListener;
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private ValueAnimator valueAnimator;
    private boolean showResultText = true;
    private Rect scaleNumRect = new Rect();
    private Rect resultNumRect = new Rect();
    private Rect kgRect = new Rect();
    private float midScaleHeight = 20;
    private Paint midScalePaint;
    private Paint smallScalePaint;
    private Paint lagScalePaint;
    private Paint scaleNumPaint;
    private Paint kgPaint;
    private int height;
    private int width;
    private Paint bgPaint;
    private int rulerHeight = 50;
    private float lagScaleHeight = 30;
    private int rulerToResultTop = rulerHeight / 4;
    private float smallScaleHeight = 10;
    private int resultTextSize = 20;
    private float firstScale = 0f;
    private float moveX = 0;
    private float lastMoveX = 0;
    private float scaleGap = 10;
    private float scaleCount = 10;
    private float minScale = 0;
    private float maxScale = 100f;
    private int rulerRight;
    private boolean isUp;
    private String resultText;
    private String unit = "KG";
    private int resultNumRight;

    private float downX;
    private float currentX;
    private int xVelocity;
    private int leftScroll;
    private int rightScroll;


    public XRulerView(Context context) {
        this(context, null);
    }

    public XRulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        start();
    }

    private void start() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        midScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lagScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        kgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        bgPaint.setStyle(Paint.Style.FILL);
        midScalePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        smallScalePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        lagScalePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        scaleNumPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        kgPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        bgPaint.setColor(Color.WHITE);
        midScalePaint.setColor(Color.GREEN);
        smallScalePaint.setColor(Color.BLUE);
        lagScalePaint.setColor(Color.RED);
        scaleNumPaint.setColor(Color.BLUE);
        kgPaint.setColor(Color.RED);

        midScalePaint.setStrokeWidth(2);
        lagScalePaint.setStrokeWidth(2);
        smallScalePaint.setStrokeWidth(1);
        scaleNumPaint.setStrokeWidth(1);
        scaleNumPaint.setTextSize(20);
        kgPaint.setTextSize(20);

        valueAnimator = new ValueAnimator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = rulerHeight + (showResultText ? resultTextSize : 0) +
                        rulerToResultTop * 2 +
                        getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSize + getPaddingTop() + getPaddingBottom();
                break;
        }

        width = widthSize + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBG(canvas);
        drawRuler(canvas);
        drawResultText(canvas);
    }

    private void drawBG(Canvas canvas) {
        canvas.drawRect(0, 0, width, height, bgPaint);
    }

    private void drawRuler(Canvas canvas) {
        //移动画布到结果值下面
        canvas.translate(0, (showResultText ? resultTextSize : 0) + rulerToResultTop);
        int num1;//确定刻度位置
        float num2;
        if (firstScale != -1) {
            //moveX 假设尺子应该向左边滑动的距离
            //如果当前的刻度是最小值,movex = width/2 从view宽度的一半来绘制尺子
            moveX = getWhichScalMoveX(firstScale);
            lastMoveX = moveX;
            firstScale = -1;
            System.out.println("moveX >> " + moveX + "; width >> " + width);
        }
        num1 = -(int) (moveX / scaleGap);//有几个刻度
        num2 = (moveX % scaleGap);//余下来的值

        System.out.println("num1 >> " + num1 + " ;num2 >> " + num2);

        canvas.save();

        rulerRight = 0;//尺子距离右边的距离,保证尺子滑不出屏幕

        if (isUp) {
            num2 = ((moveX - width / 2 % scaleGap) % scaleGap);
            if (num2 <= 0) {
                num2 = scaleGap - Math.abs(num2);
            }
            leftScroll = (int) Math.abs(num2);
            rightScroll = (int) (scaleGap - Math.abs(num2));

            float moveX2 = num2 <= scaleGap / 2 ? moveX - leftScroll : moveX + rightScroll;

            if (valueAnimator != null && !valueAnimator.isRunning()) {
                valueAnimator = ValueAnimator.ofFloat(moveX, moveX2);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        moveX = (float) animation.getAnimatedValue();
                        lastMoveX = moveX;
                        invalidate();
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //这里是滑动结束时候回调给使用者的结果值
                        if (onChooseResulterListener != null) {
                            onChooseResulterListener.onEndResult(resultText);
                        }
                    }
                });
                valueAnimator.setDuration(300);
                valueAnimator.start();
                isUp = false;
            }
        }

        canvas.translate(num2, 0);

        resultText = String.valueOf(new WeakReference<>(
                new BigDecimal(
                        (width / 2 - moveX) / (scaleGap * scaleCount)))
                .get()
                .setScale(1, BigDecimal.ROUND_HALF_UP)
                .floatValue() + minScale);
        if (onChooseResulterListener != null) {
            onChooseResulterListener.onScrollResult(resultText);
        }

        System.out.println("resultText >> " + resultText);

        //绘制当前屏幕可见刻度
        while (rulerRight < width) {//屏幕可见
            if (num1 % scaleCount == 0) {//大刻度
                //去除左右边界
                //moveX>=0 && rulerRight < moveX - scaleGap 说明距离左边还有距离,暂时不画刻度
                // width / 2 - rulerRight <= getWhichScalMoveX(maxScale + 1) - moveX 说明
                //      开始距离右边有距离
                if ((moveX >= 0 && rulerRight < moveX - scaleGap) ||
                        width / 2 - rulerRight <= getWhichScalMoveX(maxScale + 1)
                                - moveX) {
                    System.out.println("not draw -> mid");
                } else {
                    System.out.println("draw -> mid");
                    canvas.drawLine(0, 0, 0, midScaleHeight, midScalePaint);
                    scaleNumPaint.getTextBounds(num1 / scaleGap + minScale + "",
                            0, (num1 / scaleGap + minScale + "").length(),
                            scaleNumRect);
                    canvas.drawText(num1 / scaleCount + minScale + "",
                            -scaleNumRect.width() / 2,
                            lagScaleHeight + (rulerHeight - lagScaleHeight) / 2
                                    + scaleNumRect.height(),
                            scaleNumPaint);
                }
            } else {//小刻度
                ////去除左右边界
                if ((moveX >= 0 && rulerRight < moveX) || width / 2 - rulerRight <
                        getWhichScalMoveX(maxScale) - moveX) {
                    System.out.println("not draw -> small");
                } else {
                    System.out.println("draw -> small");
                    canvas.drawLine(0, 0, 0, smallScaleHeight, smallScalePaint);
                }
            }

            ++num1;//已画刻度数+1
            rulerRight += scaleGap;//距离右边的距离+一个刻度之间的距离
            canvas.translate(scaleGap, 0);//画布右边移动一个刻度之间的距离
        }

        canvas.restore();
        //绘制屏幕中间用来选中刻度的大刻度
        canvas.drawLine(width / 2, 0, width / 2, lagScaleHeight, lagScalePaint);
    }

    private float getWhichScalMoveX(float scale) {
        //计算刻度应该向左边滑动的距离是多少 刻度越大值越小
        //从尺子的中部开始
        return width / 2 - scaleGap * scaleCount * (scale - minScale);
    }

    private void drawResultText(Canvas canvas) {
//        if (!showResultText) {
//            return;
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX = event.getX();
        isUp = false;
        velocityTracker.computeCurrentVelocity(300);
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (valueAnimator != null && valueAnimator.isRunning()) {
                    valueAnimator.end();
                    valueAnimator.cancel();
                }
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = currentX - downX + lastMoveX;
                if (moveX >= width / 2) {
                    moveX = width / 2;
                } else if (moveX <= getWhichScalMoveX(maxScale)) {
                    moveX = getWhichScalMoveX(maxScale);
                }
                break;
            case MotionEvent.ACTION_UP:
                lastMoveX = moveX;
                xVelocity = (int) velocityTracker.getXVelocity();
                autoVelocityScroll(xVelocity);
                velocityTracker.clear();
                break;
        }
        invalidate();
        return true;
    }

    private void autoVelocityScroll(int xVelocity) {
        //惯性滑动
        if (Math.abs(xVelocity) < 50) {
            isUp = true;
            return;
        }
        if (valueAnimator.isRunning()) {
            return;
        }
        valueAnimator = ValueAnimator.ofInt(0, xVelocity / 20).setDuration(Math.abs(xVelocity / 10));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveX += (int) animation.getAnimatedValue();
                if (moveX >= width / 2) {
                    moveX = width / 2;
                } else if (moveX <= getWhichScalMoveX(maxScale)) {
                    moveX = getWhichScalMoveX(maxScale);
                }
                lastMoveX = moveX;
                invalidate();
            }

        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isUp = true;
                invalidate();
            }
        });

        valueAnimator.start();
    }
}
