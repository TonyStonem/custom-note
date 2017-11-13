package klx.app.sdleader_app.debug.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import klx.app.sdleader_app.R;
import klx.app.sdleader_app.utils.UIUtils;

/**
 * Created by xjw on 2017/10/9.
 */

public class XSmartRefreshLayout extends ViewGroup {

    public interface OnRefreshListener {
        void onRefresh();

        void onLoadMore();
    }

    private OnRefreshListener mRefreshListener;//回调

    private View mHeadView;
    private View mFootView;

    private int mLastChildIndex;//最后一个child的index
    private int mLayoutContentHeight;//ViewGroup内容(不包括head和foot)高度

    private int mCanLoadHeight;// 最小有效滑动距离(滑动超过该距离才视作一次有效的滑动刷新/加载操作)

    private TextView tvHead;
    private TextView tvFoot;
    private Scroller mScroller;
    private ProgressBar pgOne;
    private ProgressBar pgTwo;

    private boolean mEnablePullUp;
    private boolean mEnablePullDwon;
    private Drawable mPullBGDrawable = null;

    private LayoutInflater mLayoutInflter;
    private int mReachBottomScroll;//当滚动到内容最底部时,Y轴所需要滑动的距离

    private int SCROLL_SPEED = 650;

    public XSmartRefreshLayout(Context context) {
        super(context);
        start(context, null);
    }

    public XSmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        start(context, attrs);
    }

    public XSmartRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        start(context, attrs);
    }

    private void start(Context context, AttributeSet attrs) {
        mCanLoadHeight = UIUtils.dip2px(context, 46);
        mScroller = new Scroller(context);
        mLayoutInflter = LayoutInflater.from(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XSmartRefreshLayout);
        try {
            mEnablePullDwon = typedArray.getBoolean(R.styleable.XSmartRefreshLayout_enablePullDown, true);
            mEnablePullUp = typedArray.getBoolean(R.styleable.XSmartRefreshLayout_enablePullUp, true);
            mPullBGDrawable = typedArray.getDrawable(R.styleable.XSmartRefreshLayout_pullBackground);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLastChildIndex = getChildCount() - 1;
        if (mEnablePullDwon) {
            addLayoutHeader();
        }
        if (mEnablePullUp) {
            addLayoutFooter();
        }
    }

    private void addLayoutFooter() {
        mFootView = mLayoutInflter.inflate(R.layout.debug_refresh_foot, null);
        if (mPullBGDrawable != null) {
            mHeadView.setBackgroundDrawable(mPullBGDrawable);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        );
        tvFoot = (TextView) mFootView.findViewById(R.id.tv_foot);
        pgTwo = (ProgressBar) mFootView.findViewById(R.id.pg_two);
        addView(mFootView, layoutParams);
    }

    private void addLayoutHeader() {
        mHeadView = mLayoutInflter.inflate(R.layout.debug_refresh_head, null);
        if (mPullBGDrawable != null) {
            mHeadView.setBackgroundDrawable(mPullBGDrawable);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        );
        tvHead = (TextView) mHeadView.findViewById(R.id.tv_head);
        pgOne = (ProgressBar) mHeadView.findViewById(R.id.pg_one);
        addView(mHeadView, layoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        mLayoutContentHeight = 0;
        for (int j = 0; j < getChildCount(); j++) {
            View child = getChildAt(j);
            if (child == mHeadView) {
                child.layout(0, 0 - child.getMeasuredHeight(), child.getMeasuredWidth(), 0);
            } else if (child == mFootView) {
                System.out.println("mLayoutContentHeight:" + mLayoutContentHeight);
                child.layout(0, mLayoutContentHeight, child.getMeasuredWidth(), mLayoutContentHeight + child.getMeasuredHeight());
            } else {
                child.layout(0, mLayoutContentHeight, child.getMeasuredWidth(), mLayoutContentHeight + child.getMeasuredHeight());
                if (j <= mLastChildIndex) {
                    System.out.println("getMeasuredHeight():" + getMeasuredHeight());
                    mLayoutContentHeight += getMeasuredHeight();
                    continue;
                }
                System.out.println("child.getMeasuredHeight():" + child.getMeasuredHeight());
                mLayoutContentHeight += child.getMeasuredHeight();
            }
        }
        // 计算到达内容最底部时ViewGroup的滑动距离
        mReachBottomScroll = mLayoutContentHeight - getMeasuredHeight();
        System.out.println("mReachBottomScroll:" + mReachBottomScroll);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
        }
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int dy = mLastYMoved - y;
                System.out.println("dy:" + dy);
                System.out.println("getScrollY():" + getScrollY());
                if (dy < 0) {//下拉
                    if (mEnablePullDwon) {//用户设置下拉可用
                        if (getScrollY() > 0 || Math.abs(getScrollY()) <= mHeadView.getMeasuredHeight() / 2) {//允许下拉
                            scrollBy(0, dy);//实行下拉操作
                            if (status != REFRESH) {//当前不是下拉刷新状态
                                if (getScrollY() <= 0) {//已经下拉了 或 准备下拉
                                    if (status != TRY_REFRESH)//当前不是意图下拉刷新状态
                                        updateStatus(TRY_REFRESH);//切换状态 意图下拉刷新
                                    if (Math.abs(getScrollY()) >= mCanLoadHeight)//满足下拉刷新条件
                                        updateStatus(REFRESH);//切换状态 下拉刷新
                                }
                            }
                        }
                    } else {//用户设置下拉不可用
                        if (getScrollY() > 0) {//下拉
                            dy = dy > 30 ? 30 : dy;//位移不可大于30
                            scrollBy(0, dy);//实行下拉操作
                            if (getScrollY() < mReachBottomScroll + mCanLoadHeight) {//当前偏移量小于 满足上拉加载值
                                updateStatus(TRY_LOAD_MORE);//切换状态 意图上拉加载
                            }
                        }
                    }
                } else if (dy > 0) {//上拉
                    if (mEnablePullUp) {
                        if (getScrollY() <= mReachBottomScroll + mFootView.getMeasuredHeight() / 2) {//允许
                            if (status != TRY_REFRESH && status != REFRESH) {//此时上拉刷新结束
                                scrollBy(0, dy);//实行上拉操作
                                if (status != LOAD_MORE) {
                                    if (getScrollY() >= mReachBottomScroll) {
                                        if (status != TRY_LOAD_MORE)
                                            updateStatus(TRY_LOAD_MORE);
                                        if (getScrollY() >= mReachBottomScroll + mCanLoadHeight)
                                            updateStatus(LOAD_MORE);
                                    }
                                }
                            }
                        }
                    } else {
                        if (getScrollY() <= 0) {//上拉
                            dy = dy > 30 ? 30 : dy;
                            scrollBy(0, dy);
                            if (Math.abs(getScrollY()) < mCanLoadHeight)
                                updateStatus(TRY_REFRESH);
                        }
                    }
                }
                mLastYMoved = y;
                break;
            case MotionEvent.ACTION_UP: {
                // 判断本次触摸系列事件结束时,Layout的状态
                switch (status) {
                    case NORMAL: {
                        upWithStatusNormal();
                        break;
                    }
                    case TRY_REFRESH: {
                        upWithStatusTryRefresh();
                        break;
                    }
                    case REFRESH: {
                        upWithStatusRefresh();
                        break;
                    }
                    case TRY_LOAD_MORE: {
                        upWithStatusTryLoadMore();
                        break;
                    }
                    case LOAD_MORE: {
                        upWithStatusLoadMore();
                        break;
                    }
                }
            }
        }
        mLastYIntercept = 0;
        postInvalidate();
        return true;
    }

    private void upWithStatusLoadMore() {
        mScroller.startScroll(0, getScrollY(), 0, -((getScrollY() - mCanLoadHeight) - mReachBottomScroll), SCROLL_SPEED);
        tvFoot.setVisibility(View.GONE);
        pgTwo.setVisibility(View.VISIBLE);
        // 通过Listener接口执行加载时的监听事件
        if (mRefreshListener != null)
            mRefreshListener.onLoadMore();
    }

    private void upWithStatusTryLoadMore() {
        mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() - mReachBottomScroll), SCROLL_SPEED);
        tvFoot.setText("上拉加载");
        status = NORMAL;
    }

    private void upWithStatusRefresh() {
        mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() - (-mCanLoadHeight)), SCROLL_SPEED);
        tvHead.setVisibility(View.GONE);
        pgOne.setVisibility(View.VISIBLE);
        // 通过Listener接口执行刷新时的监听事件
        if (mRefreshListener != null)
            mRefreshListener.onRefresh();
    }

    private void upWithStatusTryRefresh() {
        // 取消本次的滑动
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
        tvHead.setText("下拉刷新");
        status = NORMAL;
    }

    private void upWithStatusNormal() {

    }

    private void updateStatus(int s) {
        switch (s) {
            case NORMAL:
                break;
            case TRY_REFRESH: {
                this.status = TRY_REFRESH;
                break;
            }
            case REFRESH: {
                this.status = REFRESH;
                tvHead.setText("释放刷新");
                break;
            }
            case TRY_LOAD_MORE: {
                this.status = TRY_LOAD_MORE;
                break;
            }
            case LOAD_MORE:
                this.status = LOAD_MORE;
                tvFoot.setText("释放加载");
                break;
        }
    }

    // Layout状态
    private int status = NORMAL;
    // 普通状态
    private static final int NORMAL = 0;
    // 意图刷新
    private static final int TRY_REFRESH = 1;
    // 刷新状态
    private static final int REFRESH = 2;
    // 意图加载
    private static final int TRY_LOAD_MORE = 3;
    // 加载状态
    private static final int LOAD_MORE = 4;

    private int mLastYMoved;//用于计算滑动距离的Y坐标中介
    private int mLastYIntercept;//用于判断时候拦截触摸事件的Y坐标中介

    @Override
    /**
     * 处理滑动冲突
     * 参考《Android开发艺术探索》
     */
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastYMoved = y;
                //不拦截,因为:当ACTION_DOWN被拦截,后续所有的触摸事件都会被拦截
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (y > mLastYIntercept) {//下拉
                    //获取最顶部的子视图
                    View view = getChildAt(0);
                    if (view instanceof AdapterView) {
                        intercept = avPullDownIntercept(view);
                    } else if (view instanceof ScrollView) {
                        intercept = svPullDownIntercept(view);
                    } else if (view instanceof RecyclerView) {
                        intercept = rvPullDownIntercept(view);
                    }
                } else if (y < mLastYIntercept) {//上拉
                    //获取最底部的子视图
                    View view = getChildAt(mLastChildIndex);
                    if (view instanceof AdapterView) {
                        intercept = avPullUpIntercept(view);
                    } else if (view instanceof ScrollView) {
                        intercept = svPullUpIntercept(view);
                    } else if (view instanceof RecyclerView) {
                        intercept = rvPullUpIntercept(view);
                    }
                } else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        mLastYIntercept = y;
        return intercept;
    }

    private boolean avPullDownIntercept(View child) {
        AdapterView view = (AdapterView) child;
        //判断AbsListView是否已经到达内容最顶端
        return view.getFirstVisiblePosition() != 0 || view.getChildAt(0).getTop() != 0;
    }

    private boolean avPullUpIntercept(View child) {
        AdapterView view = (AdapterView) child;
        //判断AbsListView是否已经到达内容最底部
        return view.getLastVisiblePosition() == view.getCount() - 1 &&
                view.getChildAt(view.getChildCount() - 1).getBottom() == getMeasuredHeight();
    }

    private boolean svPullDownIntercept(View child) {
        return child.getScrollY() <= 0;
    }

    private boolean svPullUpIntercept(View child) {
        ScrollView view = (ScrollView) child;
        return view.getScrollY() >= (view.getChildAt(0).getHeight() - view.getHeight());
    }

    private boolean rvPullDownIntercept(View child) {
        RecyclerView view = (RecyclerView) child;
        return view.computeVerticalScrollOffset() <= 0;
    }

    private boolean rvPullUpIntercept(View child) {
        RecyclerView view = (RecyclerView) child;
        return view.computeVerticalScrollExtent() + view.computeVerticalScrollOffset()
                >= view.computeVerticalScrollRange();
    }

    public void setRefreshListener(OnRefreshListener onRefreshListener) {
        this.mRefreshListener = onRefreshListener;
    }

    public void resetLayoutLocation() {
        status = NORMAL;
        scrollTo(0, 0);
    }

    private static final int STOP_REFRESH = 1;
    private static final int STOP_LOAD_MORE = 2;

    private Handler mUIHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_REFRESH: {
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
                    tvHead.setText("下拉刷新");
                    tvHead.setVisibility(View.VISIBLE);
                    pgOne.setVisibility(View.GONE);
                    status = NORMAL;
                    break;
                }

                case STOP_LOAD_MORE: {
                    mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() - mReachBottomScroll), SCROLL_SPEED);
                    tvFoot.setText("上拉加载");
                    tvFoot.setVisibility(View.VISIBLE);
                    pgTwo.setVisibility(View.GONE);
                    status = NORMAL;
                    break;
                }
            }
        }
    };

    public void stopRefresh() {
        Message msg = mUIHandler.obtainMessage(STOP_REFRESH);
        mUIHandler.sendMessage(msg);
    }

    public void stopLoadMore() {
        Message msg = mUIHandler.obtainMessage(STOP_LOAD_MORE);
        mUIHandler.sendMessage(msg);
    }

}
