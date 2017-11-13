package klx.app.sdleader_app.debug.arcview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xjw on 2017/11/13 10:53
 * Email 1521975316@qq.com
 * <p>
 * 弧形头布局
 */

public class XArcHeaderView extends View {

    private Paint mPaint;

    private int mStartColor;
    private int mEndColor;
    private int mWidth;
    private int mHeight;
    private Path mPath = new Path();
    private int mArcHeight = 100;
    private LinearGradient mShader;

    public XArcHeaderView(Context context) {
        this(context, null);
    }

    public XArcHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XArcHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        start();
    }

    private void start() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mStartColor = Color.parseColor("#337E83");
        mEndColor = Color.parseColor("#6AA9A1");
    }

    public void setColor(int start,int end) {
        mStartColor = start;
        mEndColor = end;
        mShader = new LinearGradient(mWidth / 2, 0, mWidth / 2, mHeight, mStartColor, mEndColor, Shader.TileMode.MIRROR);        invalidate();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.addRect(0, 0, mWidth, mHeight - mArcHeight, Path.Direction.CCW);
        mShader = new LinearGradient(mWidth / 2, 0, mWidth / 2, mHeight, mStartColor, mEndColor, Shader.TileMode.MIRROR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setShader(mShader);
        mPath.moveTo(0, mHeight - mArcHeight);
        mPath.quadTo(mWidth / 2, mHeight, mWidth, mHeight - mArcHeight);
        canvas.drawPath(mPath, mPaint);
    }


}
