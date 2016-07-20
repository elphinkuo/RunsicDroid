package com.runningmusic.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.runningmusic.application.WearelfApplication;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;


public class HeartBeatReceiver extends BroadcastReceiver implements
SensorEventListener {
    SensorManager sensorManager_;
    Sensor sensor_;
    private long startTime_ = 0;

    public HeartBeatReceiver() {
        super();
        sensorManager_ = (SensorManager) Util.context()
                .getSystemService(Context.SENSOR_SERVICE);
        sensor_ = sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    	if(EnvironmentDetector.getInstance().isPartialWakeLockAcc())
    	{
    		PartialWakeLock.getInstance().acquireWakeLock(context);
    	}
    	Log.i("RunsicService", "Received heartbeat Alarm!");
        HeartBeatSetting.getInstance().checkWakeupOnTime();
        startTime_ = System.currentTimeMillis();
        sensorManager_.registerListener(this, sensor_, 10000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if ((EnvironmentDetector.isBigMotion(event.values) ||
                !WearelfApplication.isBackground())
                && RunsicService.getInstance() != null)
        {
            sensorManager_.unregisterListener(this);
            RunsicService.getInstance().setActive();
            return;
        }

        if (System.currentTimeMillis() - startTime_ > 300)
        {
            sensorManager_.unregisterListener(this);
            if (RunsicService.getInstance() != null) {
                HeartBeatSetting.getInstance().setAlarm();
            }
            PartialWakeLock.getInstance().releaseWakeLock();
            return;
        }
    }
}
