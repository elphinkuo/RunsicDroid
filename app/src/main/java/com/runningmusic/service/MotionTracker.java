package com.runningmusic.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.runningmusic.application.WearelfApplication;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusiclib.cppwrapper.MotionManagerWrapper;

public class MotionTracker implements SensorEventListener {
	private String TAG = MotionTracker.class.getName();
	private static MotionTracker instance_;
	private long firstTimeStamp = 0;
	private long firstAbsoluteTimeStamp = 0;
	private long currentFrame_;
	private CPUSleepyDetector cpuSleepyDetector_;
	public static MotionTracker getInstance()
	{
		if(instance_ == null)
		{
			instance_ = new MotionTracker();
		}
		return instance_;
	}

	public void reinitAll() {
	
	}


	private MotionTracker() {
		firstTimeStamp = 0;
		currentFrame_ = 0;
		cpuSleepyDetector_ = new CPUSleepyDetector();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void onSensorChanged(SensorEvent event) {
		//Log.i("MotionAcc", "X:" + event.values[0] + "Y:" + event.values[1] + "Z:" + event.values[2]);
		motionUpdate(event);
	}
	
//	FrequencyDetector frequencyDetector_ = new FrequencyDetector();
//	int currentFrameCount_ = 0;

	
	private void motionUpdate(SensorEvent event)
	{	
		if(currentFrame_ % 200 == 0)
		{
			firstAbsoluteTimeStamp = System.currentTimeMillis();
			firstTimeStamp = event.timestamp;		
		}
		
		if(!EnvironmentDetector.getInstance().isPartialWakeLockAcc())
		{
			cpuSleepyDetector_.pushData(event);
			if(cpuSleepyDetector_.isCPUOff())
			{
				if (Util.DEBUG)
					Log.i("RunsicService", "MotionTracker cpu off request Partial WakeLock");
				PartialWakeLock.getInstance().acquireWakeLock(Util.context());
				frameCountForNoMovement_ = 0;
				firstNoMovementTime_ = 0;
				return;
			}
		}

		pushData(event);
//		Log.i("RunsicService","X:" + event.values[0] + " Y:" + event.values[1] + " Z:" + event.values[2] + "acc:" + Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]) + " gravity" + EnvironmentDetector.getInstance().gravity());
		if (EnvironmentDetector.isNoMovement(event.values)) 
		{
			long currentTime = System.currentTimeMillis();
			if (frameCountForNoMovement_ == 0) 
			{
				firstNoMovementTime_ = currentTime;
			}
			
			frameCountForNoMovement_++;
			
			long timeInterval = 60000 * 5;
			if(EnvironmentDetector.getInstance().isWakeupAligned() != EnvironmentDetector.STATUS_UNINITED)
			{
				timeInterval = 60000;
			}
			
			if (currentTime - firstNoMovementTime_ > timeInterval) 
			{
				if(WearelfApplication.isBackground())
				{
					frameCountForNoMovement_ = 0;
					cpuSleepyDetector_.reinit();
					RunsicService.getInstance().setSleepy();
				}
				else 
				{
					frameCountForNoMovement_ = 0;
				}
			}
		}
		else 
		{
			frameCountForNoMovement_ = 0;
		}
		
		currentFrame_ ++;
	}
	
	private void pushData(SensorEvent event){


		double x = -event.values[0] / EnvironmentDetector.getInstance().gravity();
		double y = -event.values[1] / EnvironmentDetector.getInstance().gravity();
		double z = -event.values[2] / EnvironmentDetector.getInstance().gravity();
		double timeStamp = (event.timestamp - firstTimeStamp) / 1000000000.0 + firstAbsoluteTimeStamp / 1000.0;
//		Log.i("zhouhan", "timeStamp:" + timeStamp + " date:" + new Date((long)(timeStamp * 1000.0)).toString());
//		ActivityManagerWrapper.getActivitiesForDay(new Date(System.currentTimeMillis()));
		MotionManagerWrapper.addData(Util.context(), x, y, z, timeStamp);
//		Log.i("zhouhan", "running! " + x + " " + y + " " + z);
		ReportHandler.handleReport();
	}
	

	
	private int frameCountForNoMovement_ = 0;
	private long firstNoMovementTime_ = 0;
}
