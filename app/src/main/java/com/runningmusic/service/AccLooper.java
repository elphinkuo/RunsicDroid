package com.runningmusic.service;

import android.hardware.SensorEvent;

public interface AccLooper {
	public void pushData(SensorEvent event);
	public void reinit();
}
