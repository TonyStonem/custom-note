package com.aixingfu.coachapp.view.smartcalendar;

/**
 * Created by xjw on 2018/2/5 0005.
 */

public class SmartCalendarBean {

    private int year;
    private int month;//1-12 --- 0-11
    private int day;//1-31
    private boolean isCurreanMonth = true;//是否当月

    public boolean isCurreanMonth() {
        return isCurreanMonth;
    }

    public void setCurreanMonth(boolean curreanMonth) {
        isCurreanMonth = curreanMonth;
    }

    public SmartCalendarBean(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return year + "-" + month + "-" + day;
    }
}
