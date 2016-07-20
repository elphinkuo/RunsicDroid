package com.runningmusic.service;

import android.hardware.SensorEvent;

public class CPUSleepyDetector implements AccLooper {
	private long lastEventTime_ = 0;
	private boolean isCPUOFF_ = false;

	public CPUSleepyDetector()
	{
		reinit();
	}
	@Override
	public void pushData(SensorEvent event) {
		if(lastEventTime_ == 0)
		{
			lastEventTime_ = System.currentTimeMillis();
			isCPUOFF_ = false;
		}
		
		if(System.currentTimeMillis() - lastEventTime_ > 1000)
		{
			isCPUOFF_ = true;
		}
		else
		{
			isCPUOFF_ = false;
		}
		
		lastEventTime_ = System.currentTimeMillis();
	}
	
	public boolean isCPUOff()
	{
		return isCPUOFF_;
	}

	@Override
	public void reinit() {
		lastEventTime_ = 0;
		isCPUOFF_ = false;
	}

}
