package com.runningmusic.service;

//import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.PowerManager;

import com.alibaba.fastjson.JSONObject;
//import javax.xml.datatype.Duration;
//import android.R.string;
//import android.os.Build;
//import cn.ledongli.ldl.application.XiaoBaiApplication;
import com.runningmusic.network.http.RunsicRestClientUsage;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;

public class EnvironmentDetector {
	public static final int STATUS_UNINITED = -1, STATUS_TRUE = 1,
			STATUS_FALSE = 0;
	public static final int /*STATUS_UNINITED = -1,*/ STATUS_NORMAL = 3, STATUS_OPTIMIZED = 2, STATUS_LOW = 1, STATUS_FROZEN = 0;
	public static final String DEFAULT_SPECIFICATION = "";
	public static final float DEFAULT_GRAVITY = (float) 9.812345;
	private static EnvironmentDetector instance_;
	private static boolean isScreenOn = true;
	
	//private ArrayList<Double> resultRatio_;
	//private ArrayList<Integer> resultStatus_;
	//private ArrayList<Double> resultFrequency_;
	
	private ArrayList<FrequencyResult> results_;
	
	public class FrequencyResult {
		public double frequency = 0;
		public double duration = 0;
		public boolean isScreenOn = false;
	}

	//private int isAccelerometerFrozen_;
	private int isAccFrequencyChange_;
	private int lockScreenAccStatus_;
	private int isWakeupAligned_;
	private float gravity_;

	private String accName_;
	private String accVendor_;
	private double accMaxRange_;
	private double accMinDelay_;
	private double accPower_;
	private double accResolution_;

	private String deviceBoard_;
	private String deviceBrand_;
	private String deviceModel_;

	private String accelerometerSpecification_;
	private String deviceSpecification_;
	private String deviceOSVersion_;
	private String deviceDisplay_;
	
	private GravityDetectorListener gravityDetectorListener_;
	private SensorManager sensorManager_;
	
	private int isPartialWakeLockAcc_;
	private Sensor sensor_;
	
	public static SharedPreferences getUserPreferences() {
	        SharedPreferences sPreferences = Util.context().getSharedPreferences("XIAOBAI_EV", 0);

	        return sPreferences;
	}
	
	private EnvironmentDetector() {
		sensorManager_ = (SensorManager) Util.context()
				.getSystemService(Context.SENSOR_SERVICE);
		sensor_ = sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		if (sensor_ == null)
		    return;

		SharedPreferences sPreferences = getUserPreferences();

		lockScreenAccStatus_ = sPreferences.getInt("ACC_LOCKSCREEN_STATUS",
				STATUS_UNINITED);

		if (lockScreenAccStatus_ == STATUS_UNINITED) {
			startDetectAccFrequency();
		}

		gravity_ = sPreferences.getFloat("ACC_GRAVITY", DEFAULT_GRAVITY);
		if (gravity_ == DEFAULT_GRAVITY) {
			startDetectGravity();
		}
		
		isWakeupAligned_ = sPreferences.getInt("WAKEUP_ALIGNED",
				STATUS_UNINITED);

		accelerometerSpecification_ = sPreferences.getString(
				"ACC_SPECIFICATION", DEFAULT_SPECIFICATION);
		if (accelerometerSpecification_.equals(DEFAULT_SPECIFICATION)) {
			detectAccelerometerSpecification();
		}

		deviceSpecification_ = sPreferences.getString("DEVICE_SPECIFICATION",
				DEFAULT_SPECIFICATION);
		if (deviceSpecification_.equals(DEFAULT_SPECIFICATION)) {
			detectDeviceSpecification();
		}
		
//		isPartialWakeLockAcc_ = sPreferences.getInt("PARTIAL_WAKE_LOCK_ACC", STATUS_UNINITED);
//		if(isPartialWakeLockAcc_ == STATUS_UNINITED)
//		{
//			startDetectPWLAcc();
//		}
		Log.i("RunsicService", "isPartialWakeLockAcc:" + isPartialWakeLockAcc_);
		deviceBoard_ = android.os.Build.BOARD;
		deviceBrand_ = android.os.Build.BRAND;
		deviceModel_ = android.os.Build.MODEL;
		deviceOSVersion_ = android.os.Build.VERSION.RELEASE;
		deviceDisplay_ = android.os.Build.DISPLAY;
		
		accName_ = sensor_.getName();
		accVendor_ = sensor_.getVendor();
		accMaxRange_ = sensor_.getMaximumRange();
		accMinDelay_ = sensor_.getMaximumRange();
		accPower_ = sensor_.getPower();
		accResolution_ = sensor_.getResolution();
		String miuiVersion = getSystemProperty("ro.miui.ui.version.name");
		if(miuiVersion != null)
		{
			if(miuiVersion.equals("V5"))
			{
				isPartialWakeLockAcc_ = STATUS_TRUE;
			}
			else
			{
				isPartialWakeLockAcc_ = STATUS_FALSE;
			}
		}
		else
		{
			isPartialWakeLockAcc_ = STATUS_FALSE;
		}

	}
	
	public boolean isPartialWakeLockAcc()
	{
		return isPartialWakeLockAcc_ != STATUS_FALSE;
	}
	
	public void setPartialWakeLockAcc(int status) {
		SharedPreferences sPreferences = getUserPreferences();
		Editor editor = sPreferences.edit();
		editor.putInt("PARTIAL_WAKE_LOCK_ACC", status);
		editor.commit();
		isPartialWakeLockAcc_ = status;
	}
	
	public void startDetectPWLAcc()
	{
		sensorManager_.registerListener(new SensorEventListener() {
			
			int countOfNoPWLEvents_ = 0;
			
			@Override
			public void onSensorChanged(SensorEvent event) 
			{
				if(
						isPartialWakeLockAcc_ == STATUS_UNINITED &&
						!PartialWakeLock.getInstance().isHeld() &&
						!pwlForAccFrequency_.isHeld() &&
						!pwlForGravity_.isHeld() &&
						!CompatibilityDetector.getInstance().isPWLHeld() &&
						!isScreenOn())
				{
					countOfNoPWLEvents_ ++;
					if(countOfNoPWLEvents_ >= 40)
					{
						Log.i("RunsicService", "setPartialWakeLockAcc:STATUS_FALSE");
						setPartialWakeLockAcc(STATUS_FALSE);
						sensorManager_.unregisterListener(this);
					}
				}
				else
				{
					countOfNoPWLEvents_ = 0;
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				
			}
		}, sensor_, SensorManager.SENSOR_DELAY_UI);
	}
	
	public int isWakeupAligned()
	{
		return isWakeupAligned_;
	}

	public String getDeviceOSVersion()
	{
		return deviceOSVersion_;
	}
	
	public static synchronized EnvironmentDetector getInstance() {
		if (instance_ == null) {
			instance_ = new EnvironmentDetector();
		}
		return instance_;
	}
		
	public static String getSystemProperty(String propName){
        String line;
        BufferedReader input = null;
        try
        {
        	Process p = Runtime.getRuntime().exec("getprop " + propName);
        	input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
        	line = input.readLine();
        	input.close();
        }
        catch (IOException ex)
        {
        	Log.e("RunsicService", "Unable to read sysprop " + propName, ex);
        	return null;
        }
        finally
        {
        	if(input != null)
        	{
                        try
                        {
                                input.close();
                        }
                        catch (IOException e)
                        {
                                Log.e("RunsicService", "Exception while closing InputStream", e);
                        }
        	}
        }
        return line;
	}
	
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		//String normalRatios_ = new String();
//		for (int i = 0; i < resultRatio_.size(); i++) {
//			normalRatios_ += "(" + resultRatio_.get(i) + "," + resultStatus_.get(i) + "," + resultFrequency_.get(i) + ")";
//		}
		
		double fsum = 0;
		double tsum = 0;
		for (int i = 0; i < results_.size(); i++) {
			fsum += results_.get(i).frequency;
			tsum += results_.get(i).duration;
		}
		double faverage = fsum / tsum; 
		
		Log.i("lock_screen_acc_status", lockScreenAccStatus_ + " " + faverage);
				
		json.put("accFrequencyChange", "" + lockScreenAccStatus_);
		json.put("wakeupAligned", "" + isWakeupAligned_);
		json.put("gravity", "" + gravity_);

		json.put("accName", "" + accName_);
		json.put("accVendor", "" + accVendor_);
		json.put("accMaxRange", "" + accMaxRange_);
		json.put("accMinDelay", "" + accMinDelay_);
		json.put("accPower", "" + accPower_);
		json.put("accResolution", "" + accResolution_);

		json.put("deviceBoard", "" + deviceBoard_);
		json.put("deviceBrand", "" + deviceBrand_);
		json.put("deviceModel", "" + deviceModel_);
		PackageManager pm = Util.context().getPackageManager();  
        PackageInfo pi;
		try {
			pi = pm.getPackageInfo(Util.context().getPackageName(), 0);
			json.put("deviceOsVersion", "" + deviceOSVersion_ + " averageFrequency:" + faverage);
			json.put("accFrozenOnStandby", "" + pi.versionCode);

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		json.put("deviceDisplay", "" + deviceDisplay_);
		
		// json.put("accSpec", accelerometerSpecification_);
		// json.put("devSpec", deviceSpecification_);
//		json.put("other", "");
		return json;
	}

	private void detectAccelerometerSpecification() {
		String accSpec = new String();
		String seperator = new String(" ");
		accSpec += "name:";
		accSpec += sensor_.getName();
		accSpec += seperator;
		accSpec += "vendor:";
		accSpec += sensor_.getVendor();
		accSpec += seperator;
		accSpec += "maxRange:";
		accSpec += sensor_.getMaximumRange();
		accSpec += seperator;
		accSpec += "minDelay:";
		accSpec += sensor_.getMinDelay();
		accSpec += seperator;
		accSpec += "power:";
		accSpec += sensor_.getPower();
		accSpec += seperator;
		accSpec += "resolution:";
		accSpec += sensor_.getResolution();
		accSpec += seperator;
		accSpec += "type:";
		accSpec += sensor_.getType();
		accSpec += seperator;
		accSpec += "version:";
		accSpec += sensor_.getVersion();

		setAccelerometerSpecification(accSpec);
	}

	public double getAccResolution() {
		return accResolution_;
	}

	public double getAccPower() {
		return accPower_;
	}

	public double getAccMinDelay() {
		return accMinDelay_;
	}

	public double getAccMaxRange() {
		return accMaxRange_;
	}

	public String getAccVendor() {
		return accVendor_;
	}

	public String getAccName() {
		return accName_;
	}
	
	public boolean isXiaomi()
	{
		return deviceBrand_.equals("Xiaomi");
	}

	private void detectDeviceSpecification() {
		String deviceSpec = new String();
		String seperator = new String(" ");
		deviceSpec += "board:";
		deviceSpec += android.os.Build.BOARD;
		deviceSpec += seperator;
		deviceSpec += "bootLoader:";
		deviceSpec += android.os.Build.BOOTLOADER;
		deviceSpec += seperator;
		deviceSpec += "brand:";
		deviceSpec += android.os.Build.BRAND;
		deviceSpec += seperator;
		deviceSpec += "cpuABI:";
		deviceSpec += android.os.Build.CPU_ABI;
		deviceSpec += seperator;
		deviceSpec += "cpuABI2:";
		deviceSpec += android.os.Build.CPU_ABI2;
		deviceSpec += seperator;
		deviceSpec += "device:";
		deviceSpec += android.os.Build.DEVICE;
		deviceSpec += seperator;
		deviceSpec += "display:";
		deviceSpec += android.os.Build.DISPLAY;
		deviceSpec += seperator;
		deviceSpec += "fingerPrint:";
		deviceSpec += android.os.Build.FINGERPRINT;
		deviceSpec += seperator;
		deviceSpec += "hardware:";
		deviceSpec += android.os.Build.HARDWARE;
		deviceSpec += seperator;
		deviceSpec += "host:";
		deviceSpec += android.os.Build.HOST;
		deviceSpec += seperator;
		deviceSpec += "id:";
		deviceSpec += android.os.Build.ID;
		deviceSpec += seperator;
		deviceSpec += "manufacturer:";
		deviceSpec += android.os.Build.MANUFACTURER;
		deviceSpec += seperator;
		deviceSpec += "model:";
		deviceSpec += android.os.Build.MODEL;
		deviceSpec += seperator;
		deviceSpec += "product:";
		deviceSpec += android.os.Build.PRODUCT;
		deviceSpec += seperator;
		deviceSpec += "serial:";
		deviceSpec += android.os.Build.SERIAL;
		deviceSpec += seperator;
		deviceSpec += "tags:";
		deviceSpec += android.os.Build.TAGS;
		deviceSpec += seperator;
		deviceSpec += "type:";
		deviceSpec += android.os.Build.TYPE;
		deviceSpec += seperator;
		deviceSpec += "unknown:";
		deviceSpec += android.os.Build.UNKNOWN;
		deviceSpec += seperator;
		deviceSpec += "user:";
		deviceSpec += android.os.Build.USER;
		deviceSpec += seperator;
		deviceSpec += "time:";
		deviceSpec += android.os.Build.TIME;

		setDeviceSpecification(deviceSpec);
	}

	public String getDeviceModel() {
		return deviceModel_;
	}

	public String getDeviceBrand() {
		return deviceBrand_;
	}

	public String getDeviceBoard() {
		return deviceBoard_;
	}

	public boolean isDetectionCompleted() {
		return /*isAccelerometerFrozen_ != STATUS_UNINITED &&
				isAccFrequencyChange_ != STATUS_UNINITED &&*/
				lockScreenAccStatus_ != STATUS_UNINITED &&
//				isWakeupAligned_ != -STATUS_UNINITED &&
				gravity_ != DEFAULT_GRAVITY;
//				!accelerometerSpecification_.equals(DEFAULT_SPECIFICATION) &&
//				!deviceSpecification_.equals(DEFAULT_SPECIFICATION);
	}

	PartialWakeLock pwlForGravity_ = new PartialWakeLock("startDetectGravity");
	private void startDetectGravity() {
		pwlForGravity_.acquireWakeLock(Util.context());
		gravityDetectorListener_ = new GravityDetectorListener();
		sensorManager_.registerListener(gravityDetectorListener_, sensor_,
				50000);
	}

	private void stopDetectGravity() {
		sensorManager_.unregisterListener(gravityDetectorListener_);
		pwlForGravity_.releaseWakeLock();
	}

	public class GravityDetectorListener implements SensorEventListener {
		GravityDetector gravityDetector_;

		public GravityDetectorListener() {
			gravityDetector_ = new GravityDetector();
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {

		}

		@Override
		public void onSensorChanged(SensorEvent data) {
			if (gravity_ == DEFAULT_GRAVITY) {
				gravityDetector_.pushData(data);
				if (gravityDetector_.gravity() != DEFAULT_GRAVITY) {
					setGravity((float) gravityDetector_.gravity());
					stopDetectGravity();
				}
			}
		}
	}

	public static boolean isGPSLocationEnable() {
		LocationManager locationManager = (LocationManager) Util.context().getSystemService(Context.LOCATION_SERVICE);
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		return isGPSEnabled;
	}

	public static boolean isNetworkLocationEnable() {
		LocationManager locationManager = (LocationManager) Util.context().getSystemService(Context.LOCATION_SERVICE);
		boolean isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		return isNetworkEnabled;
	}

	public void setAccFrequencyChange(int frozen) {
		SharedPreferences sPreferences = getUserPreferences();
		Editor editor = sPreferences.edit();
		editor.putInt("ACC_FREQUENCY_CHANGE", frozen);
		editor.commit();
		isAccFrequencyChange_ = frozen;
		checkDetectionCompleted();
	}
	
	public void setLockScreenAccStatus(int status) {
		SharedPreferences sPreferences = getUserPreferences();
		Editor editor = sPreferences.edit();
		editor.putInt("ACC_LOCKSCREEN_STATUS", status);
		editor.commit();
		lockScreenAccStatus_ = status;
		checkDetectionCompleted();
	}
	
	public void checkDetectionCompleted()
	{
		if(isDetectionCompleted())
		{
			RunsicRestClientUsage.getInstance().uploadDeviceInfo(toJSONObject().toJSONString());
		}
	}

	public int isAccFrequencyChange() {
		return isAccFrequencyChange_;
	}

	public void setGravity(float gravity) {
		SharedPreferences sPreferences = getUserPreferences();
		Editor editor = sPreferences.edit();
		editor.putFloat("ACC_GRAVITY", gravity);
		editor.commit();
		gravity_ = gravity;
		checkDetectionCompleted();
	}

	public float gravity() {
		return gravity_;
	}

	public void setAccelerometerFrozen(int frozen) {
		SharedPreferences sPreferences = getUserPreferences();
		Editor editor = sPreferences.edit();
		editor.putInt("ACC_FROZON_ON_STANDBY", frozen);
		editor.commit();
		//isAccelerometerFrozen_ = frozen;
		checkDetectionCompleted();
	}

	public boolean isAccelerometerFrozen() {
		return false;
//		return lockScreenAccStatus_ == STATUS_LOW || lockScreenAccStatus_ == STATUS_FROZEN;
	}

	public void setWakeupAligned(int aligned) {
		SharedPreferences sPreferences = getUserPreferences();
		Editor editor = sPreferences.edit();
		editor.putInt("WAKEUP_ALIGNED", aligned);
		editor.commit();
		isWakeupAligned_ = aligned;
//		checkDetectionCompleted();
	}

	public void setAccelerometerSpecification(String spec) {
		SharedPreferences sPreferences = getUserPreferences();
		Editor editor = sPreferences.edit();
		editor.putString("ACC_SPECIFICATION", spec);
		editor.commit();
		accelerometerSpecification_ = spec;
	}

	public String accelerometerSpecification() {
		return accelerometerSpecification_;
	}

	public void setDeviceSpecification(String spec) {
		SharedPreferences sPreferences = getUserPreferences();
		Editor editor = sPreferences.edit();
		editor.putString("DEVICE_SPECIFICATION", spec);
		editor.commit();
		deviceSpecification_ = spec;
	}

	public String deviceSpecification() {
		return deviceSpecification_;
	}

	public static boolean isBigMotion(ArrayList<float[]> pool) {
		int bigMotionCount = 0;
		for (float[] data : pool) {
			double accValue = Math.sqrt(data[0] * data[0] + data[1] * data[1]
					+ data[2] * data[2]);
			if (Math.abs(accValue - EnvironmentDetector.getInstance().gravity()) > 0.45) {
				bigMotionCount++;
			}
		}
		return bigMotionCount > 1;
	}
	
	public static boolean isBigMotion(float[] data) {
		return !isNoMovement(data);
	}

	public static boolean isNoMovement(float[] data) {
		boolean motion = false;
		double accValue = Math.sqrt(data[0] * data[0] + data[1] * data[1]
				+ data[2] * data[2]);
		if (Math.abs(accValue - EnvironmentDetector.getInstance().gravity()) > 0.45) {
			motion = true;
		}
		return !motion;
	}

	private static PowerManager powerManager_ =  (PowerManager) Util.context()
			.getSystemService(Context.POWER_SERVICE);
	public static boolean isScreenOn() {
		return powerManager_.isScreenOn();
	}

	private class AccFrequencyListener implements SensorEventListener {
		FrequencyDetector smallDetector_;

		public AccFrequencyListener() {
			smallDetector_ = new FrequencyDetector();
			results_ = new ArrayList<FrequencyResult>();
		}

		public void reinit() {
			smallDetector_.reinit();
			results_.clear();
		}
		
		public void judgeLockScreenStatus() {
			int countOfNormal = 0;
			int countOfLow = 0;
			for(int i = 0 ; i < results_.size() ; i++)
			{
				FrequencyResult result = results_.get(i);
				if (result.frequency > 17.9) {
					countOfNormal++;
				}
				else if (result.frequency > 11.9) {
					countOfNormal++;
				}
				else if (result.frequency > 1.9) {
					countOfLow++;
				}
				else {
				}
			}
			String status = null;
			if ((double)countOfNormal / results_.size() >= 0.8) {
				status = "NORMAL";
				setLockScreenAccStatus(STATUS_NORMAL);
			}
			else if ((double)countOfNormal >= 2) {
				status = "OPTIMIZED";
				setLockScreenAccStatus(STATUS_OPTIMIZED);
			}
			else if ((double)countOfLow / results_.size() >= 0.8) {
				Log.i("lock_screen_acc_status", "test4");
				status = "LOW";
				setLockScreenAccStatus(STATUS_LOW);
			}
			else {
				status = "FROZEN";
				setLockScreenAccStatus(STATUS_FROZEN);
			}
			
			Log.i("lock_screen_acc_status", status + " ");
		}

		public void screenActionNotify(boolean isOn) {
			FrequencyResult fr = new FrequencyResult();
			fr.frequency = smallDetector_.currentFrequency();
			fr.duration = smallDetector_.duration();
			fr.isScreenOn = !isOn;
			isScreenOn = isOn;
			
			if(isOn && fr.duration >= 2000)
			{
				results_.add(fr);
				Log.i("lock_screen_acc_status", "IN " + fr.frequency + "fps");
			}
			
			if(isOn && results_.size() > 0)
			{
				if (results_.size() == 1 && results_.get(0).frequency < 0.1) {
					setLockScreenAccStatus(STATUS_FROZEN);
					Log.i("lock_screen_acc_status", "FROZEN");
					stopDetectAccFrequency();
				}
				else if (results_.size() >= 30) {
					judgeLockScreenStatus();
					stopDetectAccFrequency();
				}
			}
			smallDetector_.reinit();
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			smallDetector_.pushData(event);
			//bigDetector_.pushData(event);
			if (!isScreenOn && (/*smallDetector_.currentFrames() >= 40 || */smallDetector_.duration() >= 2000)) {
				FrequencyResult fr = new FrequencyResult();
				fr.frequency = smallDetector_.currentFrequency();
				fr.duration = smallDetector_.duration();
				fr.isScreenOn = isScreenOn;
				
				results_.add(fr);
				Log.i("lock_screen_acc_status", "OUT " + fr.frequency + "fps");
				
				if (results_.size() >= 30) {
					judgeLockScreenStatus();
					stopDetectAccFrequency();
				}
				
				smallDetector_.reinit();
			}
		}
	}

	PartialWakeLock pwlForAccFrequency_ = new PartialWakeLock("startDetectAccFrequency");
	
	private void startDetectAccFrequency() {
		pwlForAccFrequency_.acquireWakeLock(Util.context());
		registerBroadcastReceiver(sensor_);
		accFrequencyListener_.reinit();
		
		sensorManager_.registerListener(accFrequencyListener_, sensor_, 50000);
	}

	private void stopDetectAccFrequency() {
		unregisterBroadcastReceiver();
		sensorManager_.unregisterListener(accFrequencyListener_);
		pwlForAccFrequency_.releaseWakeLock();
	}

	AccFrequencyListener accFrequencyListener_ = new AccFrequencyListener();
	
	private BroadcastReceiver powerKeyReceiver_ = null;

	private void registerBroadcastReceiver(final Sensor sensor) {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

		powerKeyReceiver_ = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String strAction = intent.getAction();

				if (strAction.equals(Intent.ACTION_SCREEN_OFF)) {
					sensorManager_.unregisterListener(accFrequencyListener_);
					sensorManager_.registerListener(accFrequencyListener_, sensor, 50000);
					accFrequencyListener_.screenActionNotify(false);
				}

				if (strAction.equals(Intent.ACTION_SCREEN_ON)) {
					accFrequencyListener_.screenActionNotify(true);
				}
			}
		};

		Util.context().registerReceiver(powerKeyReceiver_, intentFilter);
	}

	private void unregisterBroadcastReceiver() {
		try {
			Util.context().unregisterReceiver(
					powerKeyReceiver_);
		} catch (IllegalArgumentException e) {
			powerKeyReceiver_ = null;
		}
	}
}
