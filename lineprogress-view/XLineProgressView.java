package klx.app.sdleader_app.debug.linedialog;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xjw on 2017/10/12 13:42
 * Email 1521975316@qq.com
 */

public class XLineProgressView extends View {

    private int mStartX;
    private int mEndX;
    private int mStartY;
    private Paint mBgPaint;
    private Paint mTextPaint;
    private int mStyle = 0;
    private String mContent;
    private float mLenght;
    private float mWidth;
    private int mPaddingRight;
    private Paint mDrawPaint;
    private float oneL;
    private Rect mBounds = new Rect();
    private Rect mRect = new Rect();

    private int mRectPaddingleft = 4;
    private Path mPath = new Path();
    private int mSize = 15;

    public XLineProgressView(Context context) {
        this(context, null);
    }

    public XLineProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        start();
    }

    private void start() {
        mStartX = getPaddingLeft();
        mPaddingRight = getPaddingRight();

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mDrawPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgPaint.setColor(Color.GRAY);
        mTextPaint.setColor(Color.WHITE);
        mDrawPaint.setColor(Color.parseColor("#FF4081"));
        mBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDrawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBgPaint.setStrokeWidth(6);
        mTextPaint.setStrokeWidth(2);
        mDrawPaint.setStrokeWidth(8);
        mTextPaint.setTextSize(26);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mEndX += w - mPaddingRight;
        mWidth = w - mStartX - mPaddingRight;
        mStartY = h / 2;
        System.out.println("mStartX>" + mStartX + "; mEndX>" + mEndX + "; mStartY>" + mStartY + "; mWidth>" + mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(mStartX, mStartY, mEndX, mStartY, mBgPaint);
        canvas.drawLine(mStartX, mStartY, mLenght + mStartX, mStartY, mDrawPaint);
        if (mStyle == 0) {
            mTextPaint.setColor(Color.parseColor("#FF4081"));
            canvas.drawText(mContent, mLenght + mStartX, mStartY, mTextPaint);//文字
        } else if (mStyle == 1) {
            mTextPaint.setColor(Color.parseColor("#FF4081"));
            mTextPaint.getTextBounds(mContent, 0, mContent.length(), mBounds);
            int textWidth = mBounds.width();
            int textHeight = mBounds.height();
            mRect.set((int) (
                            mLenght + mStartX) - textWidth / 2 - mRectPaddingleft,
                    mStartY - mSize - textHeight - mRectPaddingleft * 2,
                    (int) (mLenght + mStartX + textWidth / 2),
                    mStartY - mSize);
            canvas.drawRect(mRect, mTextPaint);//气泡
            mPath.moveTo(mStartX + mLenght, mStartY);
            mPath.lineTo(mStartX + mLenght - mSize, mStartY - mSize);
            mPath.lineTo(mStartX + mLenght + mSize, mStartY - mSize);
            canvas.drawPath(mPath, mTextPaint);//三角
            mPath.reset();
            mTextPaint.setColor(Color.WHITE);
            canvas.drawText(mContent, mRect.centerX(),//文字
                    mStartY - mSize - mRectPaddingleft, mTextPaint);
        }
    }

    public void setStyle(int style) {
        this.mStyle = style;
    }

    public void setCurrentProgress(final int i) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, i);
        anim.setDuration(3000);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                oneL = mWidth / i;
                float progress = (float) valueAnimator.getAnimatedValue();
                mLenght = oneL * progress;
                mContent = (int) progress + "%";
                System.out.println("progress>" + progress + "; mLenght>" + mLenght + "; oneL>" + oneL);
                invalidate();
            }
        });
        anim.start();
    }
}
