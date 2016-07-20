package com.runningmusic.network.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSON;
import com.runningmusic.event.EventRequestEvent;
import com.runningmusic.music.Event;
import com.runningmusic.music.Music;
import com.runningmusic.music.MusicRequestCallback;
import com.runningmusic.network.service.NetStatus;
import com.runningmusic.service.RunsicService;
import com.runningmusic.utils.Constants;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.PackageInfoUtil;
import com.runningmusic.utils.Util;
import com.runningmusiclib.cppwrapper.ServiceLauncher;
import com.runningmusiclib.cppwrapper.utils.Date;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarOutputStream;


public class RunsicRestClientUsage {

    public static String TAG = RunsicRestClientUsage.class.getName();

    private static RunsicRestClientUsage singleton;

    private MusicRequestCallback musicRequestCallback;

    private RunsicRestClientUsage() {
        musicRequestCallback = RunsicService.getInstance();
    }

    public synchronized static RunsicRestClientUsage getInstance() {
        if (singleton == null)
            singleton = new RunsicRestClientUsage();
        return singleton;
    }


    public void getTempoMusic(int tempo) {
        AndroidRequestParams params = new AndroidRequestParams();
        params.put("tempo", tempo);

        RunsicRestClient.get(Constants.PLAY_ON_TEMPO, params, new HttpResponseHandler() {
            @Override
            public void onSuccess(byte[] responseBytes) {
                try {
                    String responseBody = new String (responseBytes, Constants.CHARSET);
                    JSONObject jsonObject = new JSONObject(responseBody);

                    if (Util.DEBUG) {
                        Log.e(TAG, "responseBody is " + jsonObject);
                        Log.e(TAG, "jsonArray is "+ jsonObject);
                    }


                    if (jsonObject != null) {

                            Music music = new Music();
                            music.initWithJSONObject(jsonObject);
                            RunsicService.getInstance().playOnTempoCallback(music);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int errorCode) {
                Log.e(TAG, "PLAYLISTONTEMPO ERROR CODE is " + errorCode);
            }
        });

    }


    public void getPGCList() {


        RunsicRestClient.get(Constants.PGC_MUSIC_LIST, null, new HttpResponseHandler() {
            @Override
            public void onSuccess(byte[] responseBytes) {
                try {
                    String responseBody = new String (responseBytes, Constants.CHARSET);
                    JSONArray jsonArray = new JSONArray(responseBody);
                    if (Util.DEBUG) {
                        Log.e(TAG, "responseBody is " + responseBody);
                        Log.e(TAG, "jsonArray is "+ jsonArray);
                    }



                    if (jsonArray!=null) {
                        for (int i=0; i < jsonArray.length(); i++) {
                            Music music = new Music();
                            music.initWithJSONObject(jsonArray.getJSONObject(i));
                            RunsicService.getInstance().addPGCList(music);
                        }
                    }

                    RunsicService.getInstance().setPGCListChange();

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int errorCode) {
                Log.e(TAG, "GET PGCLIST FAILURE");
                Log.e(TAG, "ERROR CODE " + errorCode);
            }
        });
    }


    public void getTempoList(int tempo) {
        AndroidRequestParams params = new AndroidRequestParams();
        params.put("tempo", tempo);

        RunsicRestClient.get(Constants.PLAY_LIST_ON_TEMPO, params, new HttpResponseHandler() {
            @Override
            public void onSuccess(byte[] responseBytes) {
                try {
                    String responseBody = new String (responseBytes, Constants.CHARSET);
                    JSONArray jsonArray = new JSONArray(responseBody);
                    if (Util.DEBUG) {
                        Log.e(TAG, "responseBody is " + responseBody);
                        Log.e(TAG, "jsonArray is "+ jsonArray);
                    }


                    if (jsonArray != null) {
                        for (int i=0; i < jsonArray.length(); i++) {
                            Music music = new Music();
                            music.initWithJSONObject(jsonArray.getJSONObject(i));
                            RunsicService.getInstance().addCurrentList(music);
                        }
                    }

                    musicRequestCallback.onPlayonTempoListCallback();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int errorCode) {
                Log.e(TAG, "PLAYLISTONTEMPO ERROR CODE is " + errorCode);
            }
        });

    }

    public void getMoreTempoList(int tempo) {
        AndroidRequestParams params = new AndroidRequestParams();
        params.put("tempo", tempo);

        RunsicRestClient.get(Constants.PLAY_LIST_ON_TEMPO, params, new HttpResponseHandler() {
            @Override
            public void onSuccess(byte[] responseBytes) {
                try {
                    String responseBody = new String (responseBytes, Constants.CHARSET);
                    JSONArray jsonArray = new JSONArray(responseBody);
                    if (Util.DEBUG) {
                        Log.e(TAG, "responseBody is " + responseBody);
                        Log.e(TAG, "jsonArray is "+ jsonArray);
                    }


                    if (jsonArray != null) {
                        for (int i=0; i < jsonArray.length(); i++) {
                            Music music = new Music();
                            music.initWithJSONObject(jsonArray.getJSONObject(i));
                            RunsicService.getInstance().addCurrentList(music);
                        }
                    }

                    musicRequestCallback.onPlayonTempoMoreListCallback();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int errorCode) {
                Log.e(TAG, "PLAYLISTONTEMPO ERROR CODE is " + errorCode);
            }
        });
    }

    public void getEventList() {
        RunsicRestClient.getWithoutParams(Constants.EVENT_LIST_URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(byte[] responseBytes) {
                try {
                    String responseBody = new String(responseBytes, Constants.CHARSET);
                    JSONArray jsonArray = new JSONArray(responseBody);

                    if (Util.DEBUG) {
                        Log.e(TAG, "getEventList " + "responseBody is " + responseBody);
                        Log.e(TAG, "getEventList " + "jsonArray is " + jsonArray);
                    }
                    ArrayList<Event> eventList = new ArrayList<Event>();

                    if (jsonArray != null) {
                        for (int i = 0; i< jsonArray.length(); i++) {
                            Event event = new Event();
                            event.initWithJSONObject(jsonArray.getJSONObject(i));
                            eventList.add(event);
                        }

                    }

                    EventBus.getDefault().post(new EventRequestEvent(eventList));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Log.e(TAG, "request url is " + Constants.EVENT_LIST_URL);
                Log.e(TAG, "getEventList ERROR CODE is " + errorCode);
            }
        });
    }



    public void addUser() {

        SharedPreferences sPreferences = Util.getUserPreferences();
        int userId = sPreferences.getInt(Constants.USER_INFO_USERID, 0);
        // userId = 0;
        if (userId == 0) {

            String pCode = sPreferences.getString(Constants.DEVICE_ID, "");
            // 去注册 用户
            AndroidRequestParams params = new AndroidRequestParams();
            params.put("action", "adduser");

            HashMap<String, String> userMap = new HashMap<String, String>();
            userMap.put("pc", pCode);
            String userString = JSON.toJSONString(userMap);

            params.put("user", userString);

            RunsicRestClient.post(Constants.SERVER_IP, params, new HttpResponseHandler() {

                @Override
                public void onSuccess(byte[] responseBytes) {
                    int uid = 0;
                    try {
                        String responseBody = new String(responseBytes, Constants.CHARSET);
                        JSONObject obj = new JSONObject(responseBody);
                        uid = Integer.parseInt(obj.getString("uid"));
                        ServiceLauncher.saveUidAndPCode(uid + "", Util.deviceId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // 保存uid
                    SharedPreferences sPreferences = Util.getUserPreferences();

                    Editor editor = sPreferences.edit();
                    editor.putInt(Constants.USER_INFO_USERID, uid);
                    editor.commit();
                }

                @Override
                public void onFailure(int errorCode) {
                }

            });

        }

    }

    /**
     * 获取用户信息
     * 
     * @param sh
     */

    /**
     * 本地获取用户数据
     * 
     * @return
     */
    public Map<String, String> getUserInfoFromLocal() {

        SharedPreferences sPreferences = Util.getUserPreferences();
        // 性别 体重 身高 生日 手机push_token
        String gender = sPreferences.getString(Constants.USER_INFO_GENDER, "m");
        String weight = "" + sPreferences.getFloat(Constants.USER_INFO_WEIGHT, 70);
        String height = "" + sPreferences.getFloat(Constants.USER_INFO_HEIGHT, 1.72f) * 100;
        String birthdate = "" + sPreferences.getFloat(Constants.USER_INFO_BIRTHDAY, 1980);

        String mobilPushToken = sPreferences.getString(Constants.MOBILE_PUSH_TOKEN, "");

        String nickname = sPreferences.getString(Constants.USER_INFO_NICKNAME, "");
        String avatarurl = sPreferences.getString(Constants.USER_INFO_AVATARURL, "");

        HashMap<String, String> userMap = new HashMap<String, String>();
        userMap.put("nick_name", nickname);
        userMap.put("avatar_url", "" + avatarurl);
        userMap.put("gender", "" + gender);
        userMap.put("birthdate", birthdate);
        userMap.put("weight", weight);
        userMap.put("height", height);
        userMap.put("mobile_type", "" + 1);
        userMap.put("mobile_version", "" + PackageInfoUtil.getVersionCode(Util.context()));
        userMap.put("mobile_push_token", mobilPushToken);

        return userMap;
    }

    private enum TypeInfo {
        Tencent, Sinaweibo, Wandoujia
    }





    public synchronized void setInstallSource() {
        int currentVersionNumber = 0;
        String channel = "friends";
        String deviceId = "";
        try {
            Context context = Util.context();

            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            channel = ai.metaData.getString("UMENG_CHANNEL");

            TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = TelephonyMgr.getDeviceId(); // Requires

            PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            currentVersionNumber = pinfo.versionCode;
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }

        if (currentVersionNumber == 0)
            return;

        SharedPreferences sPreferences = Util.getUserPreferences();

        String source = sPreferences.getString(Constants.INSTALL_SOURCE, "");

        if (Util.isEmpty(source)) { // 之前已经拼凑source成功,直接开始传输

            int guide = sPreferences.getInt(Constants.USER_INFO_GUIDE, 0);
            int versionNumber = sPreferences.getInt(Constants.LEDONGLI_VERSION_NUMBER, currentVersionNumber + 1);

            // 已经上传过版本信息,直接退出
            if (versionNumber == currentVersionNumber)
                return;

            // 升级用户
            if (currentVersionNumber > versionNumber || guide == 0) {
                source += "update@@@";
            } else { // 新用户
                source += "new@@@";
            }

            source += Date.now().getTime() + "@@@" + channel + "@@@" + currentVersionNumber;
            // 本地保存source和更新本地版本号信息
            Editor editor = sPreferences.edit();
            editor.putLong(Constants.INSTALL_TIME, Date.now().getTime());
            editor.putString(Constants.INSTALL_SOURCE, source);
            editor.putInt(Constants.LEDONGLI_VERSION_NUMBER, currentVersionNumber);
            editor.commit();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("source", "" + source);
            jsonObject.put("deviceId", "" + deviceId);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        // 网络判断
        if (!(NetStatus.isNetworkEnable())) {
            return;
        }

        AndroidRequestParams params = new AndroidRequestParams();
        params.put("action", "userstatistics");
        params.put("cmd", "setinstallsource");
        params.put("ctx", jsonObject.toString());

        RunsicRestClient.post(Constants.SERVER_IP, params, new HttpResponseHandler() {

            @Override
            public void onSuccess(byte[] responseBytes) {
                String succesString = "";
                try {
                    String responseBody = new String(responseBytes, Constants.CHARSET);
                    JSONObject object = new JSONObject(responseBody);
                    succesString = object.getString("status");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (succesString.equals("OK")) {
                    // 上传成功后,恢复source的本地
                    Util.getUserPreferences().edit().putString(Constants.INSTALL_SOURCE, "").commit();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                if (Util.DEBUG)
                    Log.d(TAG, "error");
            }
        });
    }

    public void uploadDeviceInfo(String info) {
        SharedPreferences sPreferences = Util.getUserPreferences();
        // boolean uploadDeviceStatus =
        // sPreferences.getBoolean(Constants.USER_DEVICE_INFO_STATUS, false);
        //
        // if (uploadDeviceStatus) {
        // return;
        // }

        int userId = sPreferences.getInt(Constants.USER_INFO_USERID, 0);
        // userId = 0;
        if (userId == 0) {
            addUser();
        }

        String pCode = sPreferences.getString(Constants.DEVICE_ID, "");
        // 去注册 用户
        AndroidRequestParams params = new AndroidRequestParams();
        params.put("action", "updatedeviceinfo");
        params.put("pc", pCode);
        params.put("uid", "" + userId);
        params.put("info", info);

        // 网络判断
        if (!(NetStatus.isNetworkEnable())) {
            return;
        }

        RunsicRestClient.post(Constants.SERVER_IP, params, new HttpResponseHandler() {

            @Override
            public void onSuccess(byte[] responseBytes) {
                String succesString = "";
                try {
                    String responseBody = new String(responseBytes, Constants.CHARSET);
                    JSONObject object = new JSONObject(responseBody);
                    succesString = object.getString("status");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (succesString.equals("OK")) {
                    // 保存上传状态
                    // SharedPreferences sPreferences =
                    // Util.getUserPreferences();
                    //
                    // Editor editor = sPreferences.edit();
                    // editor.putBoolean(Constants.USER_DEVICE_INFO_STATUS,
                    // true);
                    // editor.commit();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Log.d(TAG, "error");
            }
        });
    }

    public void updateinfo() {

        SharedPreferences sPreferences = Util.getUserPreferences();
        int userId = sPreferences.getInt(Constants.USER_INFO_USERID, 0);

        if (userId == 0) {
            return;
        }

        boolean needUpdateInfo = sPreferences.getBoolean(Constants.UPDATE_INFO_KEY, false);
        if (!needUpdateInfo) {
            return;
        }

        String pCode = sPreferences.getString(Constants.DEVICE_ID, "");
        String gender = sPreferences.getString(Constants.USER_INFO_GENDER, "f");
        String weight = "" + sPreferences.getFloat(Constants.USER_INFO_WEIGHT, 70);
        String height = "" + sPreferences.getFloat(Constants.USER_INFO_HEIGHT, 1.72f) * 100;
        String birthdate = "" + sPreferences.getFloat(Constants.USER_INFO_BIRTHDAY, 1980);

        String nickname = sPreferences.getString(Constants.USER_INFO_NICKNAME, "");
        String avatarurl = sPreferences.getString(Constants.USER_INFO_AVATARURL, "");

        String mobilPushToken = sPreferences.getString(Constants.MOBILE_PUSH_TOKEN, "");

        AndroidRequestParams params = new AndroidRequestParams();
        params.put("action", "updateinfo");
        params.put("uid", "" + userId);
        params.put("pc", "" + pCode);

        HashMap<String, String> userMap = new HashMap<String, String>();
        userMap.put("nick_name", nickname);
        userMap.put("avatar_url", "" + avatarurl);
        userMap.put("gender", "" + gender);
        userMap.put("birthdate", birthdate);
        userMap.put("weight", weight);
        userMap.put("height", height);
        // userMap.put("IOS_APN_token", "");//TODO 上传ios token
        userMap.put("mobile_type", "" + 1);
        userMap.put("mobile_version", "" + PackageInfoUtil.getVersionCode(Util.context()));
        userMap.put("mobile_push_token", mobilPushToken);
        String userString = JSON.toJSONString(userMap);
        params.put("user", userString);

        RunsicRestClient.post(Constants.SERVER_IP, params, new HttpResponseHandler() {

            @Override
            public void onSuccess(byte[] responseBytes) {
                String statusString = "";
                try {
                    String responseBody = new String(responseBytes, Constants.CHARSET);
                    JSONObject obj = new JSONObject(responseBody);
                    statusString = obj.getString("status");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (!statusString.equals("OK")) {
                    return;
                }

                SharedPreferences sPreferences = Util.getUserPreferences();
                Editor editor = sPreferences.edit();
                editor.putBoolean(Constants.UPDATE_INFO_KEY, false);
                editor.commit();
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });
    }







    public boolean isUidChanged = false;

    /**
     * 修改Uid的状态
     * 
     * @param uid
     */
    public void setIsUidChanged(int uid) {
        // 保存uid
        SharedPreferences sPreferences = Util.getUserPreferences();
        int oldUid = sPreferences.getInt(Constants.USER_INFO_USERID, 0);

        if (oldUid != uid)
            isUidChanged = true;

        Editor editor = sPreferences.edit();
        editor.putInt(Constants.USER_INFO_USERID, uid);
        editor.commit();
    }

}
