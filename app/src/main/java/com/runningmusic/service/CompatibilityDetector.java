package com.runningmusic.service;

import java.util.Timer;
import java.util.TimerTask;

import com.runningmusic.utils.Util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CompatibilityDetector implements SensorEventListener {
	public interface DetectCompletedCallBack {
		public void onCompleted(int status);
	}

	public static final int STATUS_NORMAL = 3, STATUS_OPTIMIZED = 2, STATUS_LOW = 1, STATUS_FROZEN = 0;
	private PartialWakeLock pwlForAccFrequency_;
	private Timer timer_;
	private FrequencyDetector frequencyDetector_;
	private SensorManager sensorManager_;
	private Sensor sensor_;

	public boolean isPWLHeld() {
		return pwlForAccFrequency_.isHeld();
	}

	private CompatibilityDetector() {
		pwlForAccFrequency_ = new PartialWakeLock("CompatibilityDetector");
		timer_ = new Timer();
		frequencyDetector_ = new FrequencyDetector();
		sensorManager_ = (SensorManager) Util.context().getSystemService(Context.SENSOR_SERVICE);
		sensor_ = sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}

	private static CompatibilityDetector instance_;

	public synchronized static CompatibilityDetector getInstance() {
		if (instance_ == null) {
			instance_ = new CompatibilityDetector();
		}
		return instance_;
	}

	public void start(final DetectCompletedCallBack detectCompletedCallBack) {
		pwlForAccFrequency_.acquireWakeLock(Util.context());
		sensorManager_.registerListener(this, sensor_, 50000);
		frequencyDetector_.reinit();
		timer_.schedule(new TimerTask() {

			@Override
			public void run() {
				double frequency = frequencyDetector_.currentFrequency();
				frequencyDetector_.reinit();
				int status = 0;
				if (frequency > 7.9) {
					status = STATUS_NORMAL;
				} else if (frequency > 1.9) {
					status = STATUS_LOW;
				} else {
					status = STATUS_FROZEN;
				}

				detectCompletedCallBack.onCompleted(status);
			}
		}, 2 * 1000);
	}

	public void stop() {
		timer_.cancel();
		pwlForAccFrequency_.releaseWakeLock();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		frequencyDetector_.pushData(event);
	}
}
