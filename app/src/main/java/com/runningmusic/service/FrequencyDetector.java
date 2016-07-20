package com.runningmusic.service;

import android.hardware.SensorEvent;

public class FrequencyDetector implements AccLooper
{
	private long currentTime_ = 0;
	private long lastTime_ = 0;
	private long frameCountForFrequency_ = 0;
	public static final int UNINITED = -1;
	private double duration_ = 0;
	
	@Override
	public void pushData(SensorEvent event) 
	{
		frameCountForFrequency_++;
	}

	@Override
	public void reinit() 
	{
		currentTime_ = 0;
		frameCountForFrequency_ = 0;
		lastTime_ = System.currentTimeMillis();
	}
	
	public int currentFrames() {
		return (int)frameCountForFrequency_;
	}
	
	public double currentFrequency()
	{
		currentTime_ = System.currentTimeMillis();
		duration_ = currentTime_ - lastTime_;
		double frequency = UNINITED;
		if(currentTime_ > 0 && duration_ > 0)
		{
			frequency = (double)1000 / duration_ * frameCountForFrequency_;
		}
		return frequency;
	}
	
	public double duration()
	{
//		double duration = duration_;
//		duration_ = 0;
//		return duration;
		return System.currentTimeMillis() - lastTime_;
	}
	
}
