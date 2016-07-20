package com.runningmusic.sns;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Looper;

import com.runningmusic.network.http.AndroidRequestParams;
import com.runningmusic.network.http.AndroidRestClient;
import com.runningmusic.network.http.HttpResponseHandler;
import com.runningmusic.utils.Constants;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusiclib.cppwrapper.LocationManagerWrapper;
import com.runningmusiclib.cppwrapper.PM2d5ManagerWrapper;
import com.runningmusiclib.cppwrapper.utils.Date;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

//import cn.ledongli.ldl.backup.ServerAccessTimeLimit;

//public class PM2d5Manager {
//    public static String TAG = PM2d5Manager.class.getName();
//
//    private static PM2d5Manager instance_;
//    private Timer timer_;
//    private AndroidRestClient client_ = null;
//    private long lastUpdateTime_ = 0;
//
//    public static PM2d5Manager getInstance() {
//        if (instance_ == null) {
//            instance_ = new PM2d5Manager();
//        }
//        return instance_;
//    }
//
//    private PM2d5Manager() {
//        client_ = new AndroidRestClient();
//        lastUpdateTime_ = Util.getUserPreferences().getLong("PM2D5_LAST_UPDATE_TIME", 0);
//    }
//
//    public long lastUpdateTime() {
//        return lastUpdateTime_;
//    }
//
//    public void setLastUpdateTime(long time) {
//        lastUpdateTime_ = time;
//        SharedPreferences sp = Util.getUserPreferences();
//        Editor editor = sp.edit();
//        editor.putLong("PM2D5_LAST_UPDATE_TIME", time);
//        editor.commit();
//    }
//
//    @SuppressWarnings("unused")
//    private void requestPM2d5() {
//        // if(!ServerAccessTimeLimit.getInstance().timeToAccess())
//        // {
//        // return;
//        // }
//        final Location location = LocationManagerWrapper.currentLocation();
//        JSONObject object = new JSONObject();
//        final long currentTime = System.currentTimeMillis();
//        try {
//            object.put("Time", 1395392400000L);
//            object.put("Lon", location.getLongitude());
//            object.put("Lat", location.getLatitude());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JSONArray array = new JSONArray();
//        array.put(object);
//
//        AndroidRequestParams params = new AndroidRequestParams();
//        params.put("q", array.toString());
//
//        client_.sendRequest(true, Constants.PM2D5_URL, params, new HttpResponseHandler() {
//
//            @Override
//            public void onSuccess(byte[] responseBytes) {
//                Log.i("zhouhan", "************requestPM2d5 succeed!");
//                try {
//                    String responseBody = new String(responseBytes, Constants.CHARSET);
//                    JSONArray array = new JSONArray(responseBody);
//                    JSONObject object = (JSONObject) array.get(0);
//                    int value = object.getInt("Pm25");
//                    double lat = location.getLatitude();
//                    double lon = location.getLongitude();
//                    double currentSecond = currentTime / 1000.0;
//                    PM2d5ManagerWrapper.storePM2d5(value, currentSecond, lat, lon);
//                    PM2d5ManagerWrapper.updatePM2d5Suction(Date.dateWithMilliSeconds(currentTime).startOfCurrentDay().seconds());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int errorCode) {
//                Log.i("zhouhan", "************requestPM2d5 failure!");
//            }
//        });
//    }
//
//    private void requestCurrentPM2d5() {
//        // 混淆PM2D5 URL
//        String serverURL = MobclickAgent.getConfigParams(Util.context(), "PM2D5_URL");
//        if (serverURL == null || serverURL.equals("")) {
//            serverURL = Constants.PM2D5_URL;
//        }
//        // 获取当前的位置
//        final Location location = LocationManagerWrapper.currentLocation();
//        if (location == null) {
//            return;
//        }
//
//        AndroidRequestParams params = new AndroidRequestParams();
//        params.put("lon", "" + location.getLongitude());
//        params.put("lat", "" + location.getLatitude());
//
//        if (client_ == null) {
//            client_ = new AndroidRestClient();
//        }
//        Log.i(TAG, params.toString());
//        client_.sendRequest(false, serverURL, params, new HttpResponseHandler() {
//
//            @Override
//            public void onSuccess(byte[] responseBytes) {
//                Log.i(TAG, "************requestCurrentPM2d5 succeed!");
//
//                try {
//                    String responseBody = new String(responseBytes, "UTF-8");
//                    JSONObject object = new JSONObject(responseBody);
//                    int value = object.getInt("Pm25");
//                    long currentTime = object.getLong("LastUpdate");
//                    Log.i("RunsicService", "pm2.5:" + value + " time:" + Date.dateWithMilliSeconds(currentTime));
//                    double lat = location.getLatitude();
//                    double lon = location.getLongitude();
//                    double currentSecond = currentTime / 1000.0;
//
//                    PM2d5ManagerWrapper.storePM2d5(value, currentSecond, lat, lon);
//                    PM2d5ManagerWrapper.updatePM2d5Suction(Date.dateWithMilliSeconds(currentTime).startOfCurrentDay().seconds());
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                    Log.e(TAG, "UnsupportedEncodingException: " + e.getMessage());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.e(TAG, "JSONException" + e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(int errorCode) {
//                Log.i(TAG, "************requestCurrentPM2d5 failure!");
//            }
//        });
//    }
//
//    /**
//     * 开始更新PM2d5的信息
//     */
//
//    public void stop() {
//        if (client_ != null) {
//            client_.cancelAllRequests(true);
//            client_ = null;
//        }
//
//        if (timer_ != null) {
//            timer_.cancel();
//            timer_ = null;
//        }
//    }
//
//    public void start() {
//        if (timer_ == null) {
//            timer_ = new Timer();
//            timer_.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    long currentTime = System.currentTimeMillis();
//                    if (Math.abs(currentTime - lastUpdateTime_) >= 3600 * 1000) {
//                        // Main线程下进行网络调用
//                        Looper.prepare();
//                        requestCurrentPM2d5();
//                        Looper.loop();
//
//                        setLastUpdateTime(currentTime);
//                    }
//                }
//            }, 0, 3600 * 1000);
//        }
//    }
//}
