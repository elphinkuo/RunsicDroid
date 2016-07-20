package com.runningmusic.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author shilin
 * 
 */
public class RMCrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "LedongliCrashHandler";

	// 系统默认的UncaughtException处理类
	private UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static RMCrashHandler INSTANCE = new RMCrashHandler();
	// 程序的Context对象
	private Context mContext;

	/** 保证只有一个CrashHandler实例 */
	private RMCrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static RMCrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Log.e(TAG, "error : ", ex);
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private String msg = "";

	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		String file = saveCrashInfo2File("crash", ex);
		msg = "很抱歉，程序出现异常，即将退出。";
		msg += "异常日志文件保存到\"" + file + "\"路径中。";
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		return true;
	}

	// 收集设备信息
	public static Map<String, String> collectDeviceInfo() {
		// 用来存储设备信息和异常信息
		Map<String, String> infos = new HashMap<String, String>();
		try {
			String versionName = RMCrashHandler.getVersionName();
			String versionCode = RMCrashHandler.getVersionCode();
			infos.put("versionName", versionName);
			infos.put("versionCode", versionCode);
		} catch (Exception e) {
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
			}
		}
		return infos;
	}

	// 崩溃日志文件存放路径
	public static final String CrashFileDir = "/sdcard/crash/";

	// 保存错误信息到文件中
	@SuppressLint("SimpleDateFormat")
	public static String saveCrashInfo2File(String type, Throwable ex) {
		Map<String, String> infos = collectDeviceInfo();
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			// 用于格式化日期,作为日志文件名的一部分
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = type + "-" + time + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File dir = new File(CrashFileDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(CrashFileDir + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return CrashFileDir + fileName;
		} catch (Exception e) {
		}
		return null;
	}

	public static String getVersionCode() {
		try {
			return Util.context().getPackageManager().getPackageInfo(Util.context().getPackageName(), 0).versionCode + "";
		} catch (Exception e) {
			return "0";
		}
	}

	public static String getVersionName() {
		try {
			return Util.context().getPackageManager().getPackageInfo(Util.context().getPackageName(), 0).versionName;
		} catch (Exception e) {
			return "";
		}
	}

}
