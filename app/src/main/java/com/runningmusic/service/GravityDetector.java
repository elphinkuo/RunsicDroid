package com.runningmusic.service;

import android.hardware.SensorEvent;

public class GravityDetector implements AccLooper 
{

	static final int POOL_SIZE = 200;
	double dataPool_[] = new double[POOL_SIZE];
	int currentSize_ = 0;
	private double gravity_;
	
	public GravityDetector()
	{
		reinit();
	}
	
	public void reinit()
	{
		currentSize_ = 0;
		gravity_ = EnvironmentDetector.DEFAULT_GRAVITY;
	}
	
	public void pushData(SensorEvent data)
	{		
		double acc = Math.sqrt(data.values[0] * data.values[0] + data.values[1] * data.values[1] + data.values[2] * data.values[2]);
		
		dataPool_[currentSize_] = acc;
		currentSize_ ++;
		
		if(currentSize_ == POOL_SIZE)
		{
			currentSize_ = 0;
			
			double sum = 0;
			for(int i = 0 ; i < POOL_SIZE ; i++)
			{
				sum += dataPool_[i]; 
			}
			double mean = sum / POOL_SIZE;
			
			double sumMinus = 0;
			for(int i = 0 ; i < POOL_SIZE ; i++)
			{
				sumMinus += Math.abs(dataPool_[i] - mean); 
			}
			
			if(sumMinus / POOL_SIZE < mean / 20)
			{
				gravity_ = (float)mean;
			}
		}
	}
	
	public double gravity()
	{
		return gravity_;
	}

}
