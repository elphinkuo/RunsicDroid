package com.runningmusic.network.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import com.runningmusic.utils.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetStatus {

	public static int NET_CNNT_BAIDU_OK = 1; // 正常访问因特网状态
	public static int NET_CNNT_BAIDU_TIMEOUT = 2; // 无法访问因特网状态
	public static int NET_NOT_PREPARE = 3; // 网络未准备好
	public static int NET_ERROR = 4;
	private static int TIMEOUT = 3000;

	/**
	 * 返回当前网络状态
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetState() {
		try {
			Context context = Util.context();
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
				if (networkinfo != null) {
					if (networkinfo.isAvailable() && networkinfo.isConnected()) {
						if (!connectionNetwork())
							return NET_CNNT_BAIDU_TIMEOUT;
						else
							return NET_CNNT_BAIDU_OK;
					} else {
						return NET_NOT_PREPARE;
					}
				}
			}
		} catch (Exception e) {
		}
		return NET_ERROR;
	}
	
	public static boolean isNetworkEnable()
	{
		return (NetStatus.isWifi() && (NetStatus.isWifiEnabled()) || NetStatus.is2G() || NetStatus.is3G());
	}

	/**
	 * 拼百度地址
	 * 
	 * @return
	 */
	static private boolean connectionNetwork() {
		boolean result = false;
		HttpURLConnection httpUrl = null;
		try {
			httpUrl = (HttpURLConnection) new URL("http://www.baidu.com")
					.openConnection();
			httpUrl.setConnectTimeout(TIMEOUT);
			httpUrl.connect();
			result = true;
		} catch (IOException e) {
		} finally {
			if (null != httpUrl) {
				httpUrl.disconnect();
			}
			httpUrl = null;
		}
		return result;
	}

	/**
	 * 判断当前网络是否是3G网络
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean is3G() {
		Context context = Util.context();
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifi() {
		Context context = Util.context();
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络是否是2G网络
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean is2G() {
		Context context = Util.context();
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& (activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
						|| activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || activeNetInfo
						.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA)) {
			return true;
		}
		return false;
	}

	/**
	 * wifi是否打开
	 */
	public static boolean isWifiEnabled() {
		Context context = Util.context();
		ConnectivityManager mgrConn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * 获得本机ip地址
	 * 
	 * @return
	 */
	public static String GetHostIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
						.hasMoreElements();) {
					InetAddress inetAddress = ipAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取本机串号imei
	 * @param context
	 * @return
	 */
	public static String getIMEI() {
		Context context = Util.context();
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
}
