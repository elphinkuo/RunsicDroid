package com.runningmusic.service;

import android.content.Context;
import android.os.PowerManager;

import com.runningmusic.utils.Log;

public class PartialWakeLock {
	private static boolean ON = true;
	private PowerManager.WakeLock wakeLock_;
	private static int LOCK_ID = 0;
	private String name_;
	public PartialWakeLock(String name)
	{
		LOCK_ID ++;
		name_ = name;
		Log.i("RunsicService", name + " Create PartialWakeLock");
	}
	
	private static PartialWakeLock pwl_ = null;
	public synchronized static PartialWakeLock getInstance()
	{
		if(pwl_ == null)
		{
			pwl_ = new PartialWakeLock("Default");
		}
		return pwl_;
	}
	
	public boolean isHeld()
	{
		return wakeLock_ != null;
	}
	
	public synchronized void acquireWakeLock(Context context)
	{
		Log.i("RunsicService", name_ + " acquireWakeLock");

		if(wakeLock_ == null && ON)
		{
		//	Log.i("zhouhan", "acquireWakeLock ID = " + LOCK_ID);
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			wakeLock_ = pm.newWakeLock(
					PowerManager.PARTIAL_WAKE_LOCK, "");
			wakeLock_.acquire();
		}
	}
	
	public synchronized void releaseWakeLock()
	{
		Log.i("RunsicService", name_ + " releaseWakeLock");

		if(wakeLock_ != null && ON)
		{
		//	Log.i("zhouhan", "releaseWakeLock ID = " + LOCK_ID);
			wakeLock_.release();
			wakeLock_ = null;
		}
	}
}
