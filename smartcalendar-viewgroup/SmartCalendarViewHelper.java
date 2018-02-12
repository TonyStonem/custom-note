package com.aixingfu.coachapp.view.smartcalendar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixingfu.coachapp.R;
import com.aixingfu.coachapp.utils.UIUtils;

/**
 * Created by xjw on 2018/2/5 0005.
 *
 * 1.基于线性布局进行基本的设置
 * 2.添加头布局,星期布局,日历布局
 * 3.点击事件逻辑
 * 4.圆角
 */

public class SmartCalendarViewHelper
        extends LinearLayout
        implements SmartCalendarView.OnItemClickListener, View.OnClickListener
{

    private SmartCalendarView                     mSmartCalendarView;
    private TextView                              mTitle;
    private SmartCalendarView.OnItemClickListener onDatePickListener;

    public SmartCalendarViewHelper(Context context) {
        this(context, null);
    }

    public SmartCalendarViewHelper(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.color_content));

        //头视图
        LayoutParams headParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                                                   UIUtils.dip2px(context, 60));
        View headView = LayoutInflater.from(getContext())
                                      .inflate(R.layout.smart_calender_head_view, null);
        mTitle = (TextView) headView.findViewById(R.id.tv_title);
        headView.findViewById(R.id.tv_left)
                .setOnClickListener(this);
        headView.findViewById(R.id.tv_right)
                .setOnClickListener(this);
        addView(headView, headParams);
        //线
        View view = new View(context);
        view.setBackgroundColor(getResources().getColor(R.color.color_view));
        LayoutParams viewParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                                                   UIUtils.dip2px(context, 1));
        addView(view, viewParams);

        //星期视图
        LayoutParams weekParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                                                   UIUtils.dip2px(context, 33));
        weekParams.setMargins(UIUtils.dip2px(context, 16),
                              UIUtils.dip2px(context, 16),
                              UIUtils.dip2px(context, 16),
                              0);
        View weekView = LayoutInflater.from(getContext())
                                      .inflate(R.layout.smart_calender_week_view, null);
        addView(weekView, weekParams);
        //日历视图
        LayoutParams smartParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT);
        smartParams.setMargins(UIUtils.dip2px(context, 16),
                               0,
                               UIUtils.dip2px(context, 16),
                               UIUtils.dip2px(context, 16));
        mSmartCalendarView = new SmartCalendarView(context);
        mSmartCalendarView.setBackgroundColor(Color.WHITE);
        initSmartCalendarData();
        mSmartCalendarView.setOnItemClickListener(this);
        addView(mSmartCalendarView, smartParams);
    }

    private void initSmartCalendarData() {
        int[] nowDay = mSmartCalendarView.getNowDayFromSystem();
        mSmartCalendarView.setMonth(nowDay[0], nowDay[1]);
        updateTitle();
    }

    private void updateTitle() {
        if (null != mTitle && null != mSmartCalendarView) {
            mTitle.setText(mSmartCalendarView.getCurrentYearAndMonth());
        }
    }

    @Override
    public void onItemClick(SmartCalendarBean bean) {
        if (null != onDatePickListener) {
            onDatePickListener.onItemClick(bean);
        }
    }

    public void setOnDatePickListener(SmartCalendarView.OnItemClickListener onDatePickListener) {
        this.onDatePickListener = onDatePickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                if (null != mSmartCalendarView) {
                    mSmartCalendarView.move2Top();
                }
                updateTitle();
                break;
            case R.id.tv_right:
                if (null != mSmartCalendarView) {
                    mSmartCalendarView.move2Bottom();
                }
                updateTitle();
                break;
        }
    }
}
