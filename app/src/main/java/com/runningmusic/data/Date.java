package com.runningmusic.data;

import java.util.Calendar;
import java.util.TimeZone;

import android.text.format.Time;
import android.util.Log;

public class Date extends java.util.Date {
    private double seconds_ = 0;
    public static final double DISTANT_FUTURE = 64092211200.0;

    private Date() {
        super(System.currentTimeMillis());
        seconds_ = System.currentTimeMillis() / 1000.0;
    }

    public static Date dateWithSeconds(double seconds) {
        return new Date(seconds);
    }

    public static Date dateWithMilliSeconds(long milliSeconds) {
        return new Date(milliSeconds);
    }

    public static Date now() {
        return new Date();
    }

    public static Date getDate(int year, int month, int day, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        // calendar.set(Calendar.MILLISECOND, 0);

        Long dateTime = calendar.getTime().getTime();
        Date date = new Date(dateTime);

        return date;
    }

    public static long offsetBetweenGMT8() {
        TimeZone tz = TimeZone.getDefault();
        TimeZone tz8 = TimeZone.getTimeZone("GMT+8:00");
        return tz.getRawOffset() - tz8.getRawOffset();
    }

    public Date startOfCurrentDayInGMT8() {
        Date startOfCurrentDay = startOfCurrentDay();
        Date result = Date.dateWithSeconds(startOfCurrentDay.seconds() + offsetBetweenGMT8());
        return result;
    }

    private Date(double seconds) {
        super((long) (seconds * 1000));
        seconds_ = seconds;
    }

    private Date(long milliseconds) {
        super(milliseconds);
        seconds_ = milliseconds / 1000.0;
    }

    private Date(int year, int month, int day, int hour, int minute, int second) {
        super(year, month, day, hour, minute, second);
        Log.e("errordata", "" + year + ":" + month + ":" + day + ":" + hour + ":" + minute);
    }

    public boolean isInOneYear(Date date) {
        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(this);
        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(date);

        return calendarA.get(Calendar.YEAR) == calendarB.get(Calendar.YEAR);
    }

    public Date startOfCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Long dateTime = calendar.getTime().getTime();
        Date date = new Date(dateTime);

        return date;
    }

    public boolean isInOneDay(Date date) {
        return this.startOfCurrentDay().isEqualTo(date.startOfCurrentDay());
    }

    // mark days
    public Date startOfPreviousDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Long dateTime = calendar.getTime().getTime();
        Date date = new Date(dateTime);

        return date;
    }

    public Date startOfCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Long dateTime = calendar.getTime().getTime();
        Date date = new Date(dateTime);

        return date;

    }

    public Date endOfCurrentDay() {
        return startOfNextDay();
    }

    public Date startOfNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Long dateTime = calendar.getTime().getTime();
        Date date = new Date(dateTime);

        return date;
    }

    public Date twentyFourHoursPrevious() {
        return Date.dateWithSeconds(seconds_ - 86400);
    }

    public Date twentyFourHoursNext() {
        return Date.dateWithSeconds(seconds_ + 86400);
    }

    public Date oneDayPrevious() {
        return this.dateByAddingDate(0, 0, -1, 0, 0, 0);
    }

    public boolean isEqualTo(Date date) {
        return this.getTime() == date.getTime();
    }

    public Date oneDayNext() {
        return this.dateByAddingDate(0, 0, +1, 0, 0, 0);
    }

    public double timeIntervalSince(Date date) {
        return (this.getTime() - date.getTime()) / 1000.0;
    }

    public double seconds() {
        return seconds_;
    }

    private Date dateByAddingDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        calendar.add(Calendar.MINUTE, minute);
        calendar.add(Calendar.SECOND, second);

        Long dateTime = calendar.getTime().getTime();
        Date date = new Date(dateTime);

        return date;
    }

    @Override
    public String toString() {
        Time time = new Time("GMT+8");
        time.set(this.getTime());
        // int year = time.year;
        int month = time.month + 1;
        int day = time.monthDay;
        int minute = time.minute;
        int hour = time.hour;
        int sec = time.second;
        return "" + month + "/" + day + " " + hour + ":" + minute + ":" + sec;
    }

	@Override
	public boolean equals(Object object) {
		if(object instanceof Date)
		{
			return ((Date) object).seconds() == seconds_;
		}
		return false;
	}
    
}
