package klx.app.sd_app.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xjw on 2017/10/30 15:13
 * Email 1521975316@qq.com
 * <p>
 * 1.画背景
 * 2.画分割线
 * 3.画圆点
 */

public class XPasswordView extends View {

    private int viewWidth;
    private int viewHeight;
    private Paint bgPaint;
    private int count = 4;
    private int gap = 13;
    private int paintWidth = 2;
    private int circleRadius = 8;
    private int style = 0;
    private int mIndex = -1;
    private int MAX_COUNT = 8;

    public XPasswordView(Context context) {
        this(context, null);
    }

    public XPasswordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XPasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        start(context);
    }

    private void start(Context context) {
        viewWidth = getResources().getDisplayMetrics().widthPixels;
        viewHeight = UIUtils.dip2px(context, 46);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.GRAY);
        bgPaint.setStrokeWidth(paintWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            if (heightMode == MeasureSpec.AT_MOST) {
                viewHeight = Math.min(viewHeight, MeasureSpec.getSize(heightMeasureSpec));
            } else {
                viewHeight = MeasureSpec.getSize(heightMeasureSpec);
            }
        }
        viewWidth = viewWidth + getPaddingLeft() + getPaddingRight();
        viewHeight = viewHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (style == 0) {
            bgPaint.setStyle(Paint.Style.STROKE);
            drawBG(canvas);
        } else {
            bgPaint.setStyle(Paint.Style.STROKE);
            drawBGS(canvas);
            drawDecoration(canvas);
        }
        bgPaint.setStyle(Paint.Style.FILL);
        drawCircle(canvas);
    }

    private void drawBGS(Canvas canvas) {
        canvas.drawRect(gap, paintWidth, getWidth() - gap, getHeight() - paintWidth, bgPaint);
    }

    private void drawCircle(Canvas canvas) {
        if (mIndex == -1) {
            return;
        }
        int size = (getWidth() - (count + 1) * gap) / count;
        canvas.save();
        canvas.translate(gap + size / 2, 0);
        for (int i = 0; i < mIndex; i++) {
            canvas.drawCircle(0, getHeight() / 2, circleRadius, bgPaint);
            canvas.translate(size + gap, 0);
        }
        canvas.restore();
    }

    private void drawDecoration(Canvas canvas) {
        int size = getWidth() / count;
        canvas.save();
        for (int i = 0; i < count - 1; i++) {
            canvas.drawLine(size, paintWidth, size, getHeight() - paintWidth, bgPaint);
            canvas.translate(size, 0);
        }
        canvas.restore();
    }

    private void drawBG(Canvas canvas) {
        int size = (getWidth() - (count + 1) * gap) / count;
        canvas.save();
        canvas.translate(gap + paintWidth, 0);
        for (int i = 0; i < count; i++) {
            canvas.drawRect(0, paintWidth, size, getHeight() - paintWidth, bgPaint);
            canvas.translate(size + gap, 0);
        }
        canvas.restore();
    }

    public void setStyle(int i) {
        if (i == 0 || i == 1) {
            this.style = i;
        }else {
            this.style = 0;
        }
        invalidate();
    }

    public void setCount(int i) {
        if (i > MAX_COUNT) {
            this.count = MAX_COUNT;
        } else if (i < 4) {
            this.count = 4;
        }else {
            this.count = i;
        }
        invalidate();
    }

    public void setShowCount(int i) {
        if (i > this.count) {
            this.mIndex = this.count;
        }else if (i <= 0) {
            this.mIndex = -1;
        }else {
            this.mIndex = i;
        }
        invalidate();
    }

}
