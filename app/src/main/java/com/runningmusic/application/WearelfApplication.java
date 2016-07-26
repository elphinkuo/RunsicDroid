package com.runningmusic.application;


import android.content.Context;
import android.content.Intent;

import com.runningmusic.network.RequestManager;
import com.runningmusic.service.RunsicService;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.RMCrashHandler;
import com.runningmusic.utils.Util;
import com.runningmusic.videocache.HttpProxyCacheServer;
import com.umeng.analytics.MobclickAgent;

/*
 * 
 */
public class WearelfApplication extends LifecycleApplication {
	public static String TAG = WearelfApplication.class.getName();
	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";


	private static boolean isBackground_ = false;

	public static boolean isBackground() {
		return isBackground_;
	}

	private HttpProxyCacheServer proxyCacheServer;

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "onCreate");
		isBackground_ = false;
		//初始化网络引擎
        RequestManager.init(this);

        //初始化工具类 引用
		Util.setContext(this.getApplicationContext());
		
		//初始化CrashHandler
		if (Log.LOG) {
			RMCrashHandler.getInstance().init(this);
		}

		//启动Runsic Service



		//暂时不存Log
//		if (Log.LOG) {
//			Intent logIntent = new Intent(this, LogService.class);
//			this.startService(logIntent);
//		}
		
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.e(TAG, "onLowMemory");
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		Log.e(TAG, "onTrimMemory");

	}

	@Override
	protected void onAppBackground() {
		Log.i(TAG, "______________onAppBackground");
		isBackground_ = true;
	}

	@Override
	protected void onAppForeground(LifecycleActivity activity) {
		Log.i(TAG, "______________onAppForeground");
		isBackground_ = false;
		RunsicService service = RunsicService.getInstance();
		if (service != null) {
			service.setActive();
		}
		MobclickAgent.onResume(this);

	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d(TAG, "onTerminate:");

	}


//	public static HttpProxyCacheServer getProxy(Context context) {
////		WearelfApplication app = (WearelfApplication) context.getApplicationContext();
//////		HttpProxyCacheServer.Builder builder = new HttpProxyCacheServer.Builder(context);
//////		builder.cacheDirectory(new File(MUSIC_CACHE_DIR));
//////		Log.e(TAG, "CACHE DIR IS ++++++++++++++++++++++" + MUSIC_CACHE_DIR);
////		return app.proxyCacheServer == null ? (app.proxyCacheServer = newProxy(context)) : app.proxyCacheServer;
//	}
//
//	private static HttpProxyCacheServer newProxy (Context context) {
//		WearelfApplication app = (WearelfApplication) context.getApplicationContext();
//
//		return new HttpProxyCacheServer.Builder(app).maxCacheSize(300 * 1024 * 1024).build();
//	}




}
