package com.runningmusic.service;

import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;

import android.R.integer;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class HeartBeatSetting {
	String alarmName_ = Util.context().getPackageName() + "TIMER_ALARM";
	PendingIntent pendingIntent_ = null;
	long startTime_ = 0;
	private boolean isRTCWakeLock_;
	private static final long TIME_INTERVAL = 7000;
	private int rtcTimes_ = 0;
	private int rtcWakeupNotOnTimeTimes_ = 0;
	private long lastCheckDate_ = 0;
	
	private HeartBeatSetting()
	{
		Context context =  Util.context();
		Intent i = new Intent(context, HeartBeatReceiver.class);
		i.setAction(alarmName_);
		startTime_ = System.currentTimeMillis();
		pendingIntent_ = PendingIntent.getBroadcast(context, 0, i, 0);
		isRTCWakeLock_ = true;
		
		SharedPreferences sPreferences = Util.context()
				.getSharedPreferences("XIAOBAI_SP", 0);
		lastCheckDate_ = sPreferences.getLong("LAST_STYLE_CHECKE_DATA", 0);
	}
	
	private static HeartBeatSetting instance_;
	
	public static HeartBeatSetting getInstance()
	{
		if(instance_ == null)
		{
			instance_ = new HeartBeatSetting();
		}
		return instance_;
	}

	public void checkWakeupOnTime() 
	{
		long currentTime = System.currentTimeMillis();
		boolean isOutDay = currentTime - lastCheckDate_ > 24 * 60 * 60 * 1000;

		long timeInterval = Math.abs(System.currentTimeMillis()
				- getFirstAlarmComingTime());
		
		if (isRTCWakeLock_ && timeInterval > 1000) 
		{
			rtcWakeupNotOnTimeTimes_++;
		}

		if (isOutDay && isRTCWakeLock_) 
		{
			EnvironmentDetector.getInstance().setWakeupAligned(rtcWakeupNotOnTimeTimes_ > 0 ? 1 : 0);
			SharedPreferences sPreferences = Util.context()
					.getSharedPreferences("XIAOBAI_SP", 0);
			Editor editor = sPreferences.edit();
			lastCheckDate_ = currentTime;
			editor.putLong("LAST_STYLE_CHECKE_DATA", lastCheckDate_);
			editor.commit();
		}
	}
	
	public long getFirstAlarmComingTime()
	{
		return this.startTime_ + TIME_INTERVAL;
	}
	
	public void setAlarm() {

		boolean isRTCWakeupNow;
		if(isRTCWakeLock_)
		{
			if(rtcWakeupNotOnTimeTimes_ >= 1)
			{
				rtcWakeupNotOnTimeTimes_ = 0;
				isRTCWakeupNow = false;
			}
			else
			{
				isRTCWakeupNow = true;
			}
		}
		else
		{
			if(rtcTimes_ >= 16000)
			{
				rtcTimes_ = 0;
				isRTCWakeupNow = true;
			}
			else
			{
				isRTCWakeupNow = false;
			}
		}
		
		startTime_ = System.currentTimeMillis();
		Context context =  Util.context();
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		
		if(!isRTCWakeupNow)
		{
			rtcTimes_ ++;
			Log.i("RunsicService", "!isRTCWakeupNow rtcTimes:" + rtcTimes_ + " rtcWakeupNotOnTimeTimes:" + rtcWakeupNotOnTimeTimes_);
			isRTCWakeLock_ = false;
			am.set(AlarmManager.RTC, startTime_ + TIME_INTERVAL, pendingIntent_); 
		}
		else
		{
			Log.i("RunsicService", "isRTCWakeupNow rtcTimes:" + rtcTimes_ + " rtcWakeupNotOnTimeTimes:" + rtcWakeupNotOnTimeTimes_);
			isRTCWakeLock_ = true;
			am.set(AlarmManager.RTC_WAKEUP, startTime_ + TIME_INTERVAL, pendingIntent_); 
		}
	}

	public void cancelAlarm() {
		Context context =  Util.context();
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent_);
	}
}
