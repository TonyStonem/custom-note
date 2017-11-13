package klx.app.sdleader_app.debug.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by xjw on 2017/10/6.
 */

public class XViewPager extends ViewGroup {

    private Scroller mScroller;
    private int mCurrentIndex = 1, mPageCount;
    private int mScreenWidth;
    private int mLastX;
    private int mStartX;
    private int mEndX;

    public XViewPager(Context context) {
        super(context);
        start(context);
    }

    public XViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        start(context);
    }

    private void start(Context context) {
        mScroller = new Scroller(context);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int childCount = getChildCount();
        mPageCount = 0;
        MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
        layoutParams.width = mScreenWidth * childCount;
        setLayoutParams(layoutParams);
        for (int j = 0; j < childCount; j++) {
            View child = getChildAt(j);
            child.layout(j * mScreenWidth, 0, (j + 1) * mScreenWidth, i3);
            mPageCount++;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        event.getRawX();//相对于屏幕(0,0)
        int x = (int) event.getX();//相对于当前容器
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mStartX = getScrollX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                int dx = mLastX - x;
                System.out.println("dx:" + dx + ";getScrollX():" + getScrollX());
                if (dx < 0) {//左 -> 右 dx:负
                    //x轴的偏移量为0,则证明屏幕还未滑动过
//                    if (getScrollX() > 0) {//此时可以向右方进行滑动
//                        scrollBy(dx, 0);
//                    }
                    //新的判断方式
                    if (mCurrentIndex > 1) {
                        scrollBy(dx, 0);
                    }
                } else if (dx > 0) {//右 -> 左 dx:正
                    //保证一个match_parent的view可见
//                    if (getScrollX() < getWidth() - mScreenWidth) {
//                        scrollBy(dx, 0);
//                    }
                    //新的判断方式
                    if (mCurrentIndex < mPageCount) {
                        scrollBy(dx, 0);
                    }
                }
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
                mEndX = getScrollX();
                int movedX = mEndX - mStartX;
                if (movedX > 0) {//右 -> 左
                    if (movedX < mScreenWidth / 3) {//回到原处
//                        scrollBy(-movedX, 0);
                        mScroller.startScroll(getScrollX(), 0, -movedX, 0);
                        //设定时长
//                        mScroller.startScroll(getScrollX(), 0, -movedX, 0, 1500);
                    } else {//去下一个
//                        scrollBy(mScreenWidth - movedX, 0);
                        mScroller.startScroll(getScrollX(), 0, mScreenWidth - movedX, 0);
                        mCurrentIndex++;
                    }
                } else {//左 -> 右
                    if (-movedX < mScreenWidth / 3) {
//                        scrollBy(-movedX, 0);
                        mScroller.startScroll(getScrollX(), 0, -movedX, 0);
                    } else {//去上一个
//                        scrollBy(-mScreenWidth - movedX, 0);
                        mScroller.startScroll(getScrollX(), 0, -mScreenWidth - movedX, 0);
                        mCurrentIndex--;
                    }
                }
                break;
        }
        postInvalidate();
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {//是否完成了整个滑动
            scrollTo(mScroller.getCurrX(), 0);
        }
        postInvalidate();
    }
}
