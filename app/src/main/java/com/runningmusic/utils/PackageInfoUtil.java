package com.runningmusic.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageInfoUtil {

	public static int getVersionCode(Context context) {
		PackageInfo pinfo = null;
		try {
			pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			
		}
		if(pinfo == null){
			return 1;
		}
		
		int versionNumber = pinfo.versionCode;
		return versionNumber;
	}
}
