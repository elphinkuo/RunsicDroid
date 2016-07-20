package com.runningmusic.service;

import android.hardware.SensorEvent;

public interface AccLooper {
	void pushData(SensorEvent event);
	void reinit();
}
