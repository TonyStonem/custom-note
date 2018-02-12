package com.aixingfu.coachapp.view.smartcalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aixingfu.coachapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by xjw on 2018/2/5 0005.
 *
 * 1.数据生成
 * 2.measure
 * 3.layout
 * 4.数据填充
 * 5.分割线
 * 6.点击事件逻辑
 */

public class SmartCalendarView
        extends ViewGroup
{

    public String getCurrentYearAndMonth() {
        return formatYearAndMonth(mYear, mMonth);
    }

    public interface OnItemClickListener {
        void onItemClick(SmartCalendarBean bean);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private SmartCalendarBean generateCalendarBean(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;//月份从0开始
        day = c.get(Calendar.DAY_OF_MONTH);// == c.get(Calendar.DAY)
        return new SmartCalendarBean(year, month, day);
    }

    public static int getDays4MonthOfMaxCount(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, 1);
        return c.getActualMaximum(Calendar.DATE);
    }

    private int getWeekDayOfDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        return c.get(Calendar.DAY_OF_WEEK);//1-7(周日-周六)
    }

    private String formatYearAndMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, 1);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        return year + "年" + month + "月";
    }

    public static int[] getNowDayFromSystem() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return new int[]{cal.get(Calendar.YEAR),
                         cal.get(Calendar.MONTH) + 1,
                         cal.get(Calendar.DATE)};
    }

    private static boolean isToday(SmartCalendarBean bean) {
        int[] nowDay = getNowDayFromSystem();
        return bean.getYear() == nowDay[0] && bean.getMonth() == nowDay[1] && bean.getDay() == nowDay[2];
    }

    private List<SmartCalendarBean> getDays4Month(int year, int month) {
        int                     maxCount = getDays4MonthOfMaxCount(year, month);
        List<SmartCalendarBean> list     = new ArrayList<>();
        System.out.println("year " + year + "; month " + month);
        int ofDate = getWeekDayOfDate(year, month, 1);//当月第一天星期几
        //上月
        int count = ofDate - 1;
        System.out.println("top " + count);
        for (int i = count; i > 0; i--) {
            SmartCalendarBean bean = generateCalendarBean(year, month, 1 - i);
            bean.setCurreanMonth(false);
            list.add(bean);
        }
        //本月
        System.out.println("center " + maxCount);
        for (int i = 0; i < maxCount; i++) {
            SmartCalendarBean bean = generateCalendarBean(year, month, i + 1);
            bean.setCurreanMonth(true);
            list.add(bean);
        }
        //下月
        if ((maxCount + count) % 7 != 0) {
            int counts = (maxCount + count) / 7 + 1;
            counts = counts * column - (maxCount + count);
            System.out.println("bottom " + counts);
            for (int i = 0; i < counts; i++) {
                //            SmartCalendarBean bean = generateCalendarBean(year, month, (maxCount + (i + 1)));
                SmartCalendarBean bean = generateCalendarBean(year, month, maxCount + i + 1);
                bean.setCurreanMonth(false);
                list.add(bean);
            }
        }

        return list;
    }

    private int topIndex = -1;
    private int column   = 7;
    private int mYear;
    private int mMonth;
    private int mItemWidth;
    private int mViewHeight;
    private int mViewWidth;
    private List<SmartCalendarBean> beanList = new ArrayList<>();
    private Paint mPaint;

    public SmartCalendarView(Context context) {
        this(context, null);
    }

    public SmartCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#E9E9E9"));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(MeasureSpec.makeMeasureSpec(widthMeasureSpec,
                                                                          //widthMeasureSpec -> 尺寸大小+模式
                                                                          MeasureSpec.EXACTLY));
        int itemWidth    = parentWidth / column;
        int itemHeight   = itemWidth;
        int parentHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childView.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                              MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
            if (i % column == 0) {//判断有几行
                parentHeight += childView.getMeasuredHeight();
            }
        }
        System.out.println("SmartCalendarView : width -> " + parentWidth + "; height -> " + parentHeight);
        setMeasuredDimension(parentWidth, parentHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mItemWidth = w / column;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View itemView    = getChildAt(i);
            int  columnIndex = i % column;//第几个
            int  rowIndex    = i / column;//第几行
            int  mWidth      = itemView.getMeasuredWidth();
            int  mHeight     = itemView.getMeasuredHeight();
            l = columnIndex * mWidth;
            t = rowIndex * mHeight;
            r = l + mWidth;
            b = t + mWidth;
            itemView.layout(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawColumnLine(canvas);
        drawRowLine(canvas);
    }

    private void drawRowLine(Canvas canvas) {
        for (int i = 0; i < column + 1; i++) {
            canvas.drawLine(0, i * mItemWidth, mViewWidth, i * mItemWidth, mPaint);
        }
    }

    private void drawColumnLine(Canvas canvas) {
        for (int i = 0; i < column - 1; i++) {
            canvas.drawLine((i + 1) * mItemWidth, 0, (i + 1) * mItemWidth, mViewHeight, mPaint);
        }
    }

    public void setMonth(int year, int month) {
        this.mYear = year;
        this.mMonth = month;
        invalidateLayout();
    }

    private void invalidateLayout() {
        beanList = getDays4Month(mYear, mMonth);
        topIndex = -1;
        removeAllViews();
        addAllItem();
        requestLayout();
    }

    public void move2Top() {
        mMonth -= 1;
        invalidateLayout();
    }

    public void move2Bottom() {
        mMonth += 1;
        invalidateLayout();
    }

    private void addAllItem() {
        for (int i = 0; i < beanList.size(); i++) {
            final SmartCalendarBean bean     = beanList.get(i);
            final View              itemView = generateItemView(bean);
            addViewInLayout(itemView, i, itemView.getLayoutParams(), true);
            final int currentIndex = i;
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bean.isCurreanMonth()) {
                        return;
                    }
                    if (topIndex != currentIndex) {
                        if (topIndex != -1) {
                            getChildAt(topIndex).setSelected(false);
                        }
                        itemView.setSelected(true);
                    } else {
                        itemView.setSelected(false);
                    }
                    if (null != onItemClickListener) {
                        onItemClickListener.onItemClick(bean);
                    }
                    topIndex = currentIndex;
                }
            });
        }
    }

    private View generateItemView(SmartCalendarBean bean) {
        View itemView = LayoutInflater.from(getContext())
                                      .inflate(R.layout.smart_calendar_item_view, null);
        TextView tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        if (bean.isCurreanMonth()) {
            tvTime.setTextColor(Color.parseColor("#333333"));
        } else {
            tvTime.setTextColor(Color.parseColor("#FFFFFF"));
        }
        if (isToday(bean)) {
            itemView.setBackgroundResource(R.drawable.smart_calendar_current);
            tvTime.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            if (bean.isCurreanMonth()) {
                itemView.setBackgroundResource(R.drawable.smart_calendar_other);
            } else {
                itemView.setBackgroundColor(Color.parseColor("#DADADA"));
            }
        }
        tvTime.setText(String.valueOf(bean.getDay()));
        return itemView;
    }

}
