package com.runningmusic.oauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.runningmusic.network.http.AndroidRequestParams;
import com.runningmusic.network.http.HttpResponseHandler;
import com.runningmusic.network.http.RunsicRestClient;
import com.runningmusic.network.http.RunsicRestClientUsage;
import com.runningmusic.utils.Constants;
import com.runningmusic.utils.Util;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMediaObject;
import com.umeng.socialize.sso.CustomHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.sso.UMTencentSsoHandler;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guofuming on 19/7/16.
 */
public class ThirdLoginOAuth {

    private static ThirdLoginOAuth singleton;

    private UMSocialService controller_;

    private ThirdLoginOAuth() {
        controller_ = UMServiceFactory.getUMSocialService("com.umeng.login");
    }

    public synchronized static ThirdLoginOAuth getInstance() {
        if (singleton == null)
            singleton = new ThirdLoginOAuth();
        return singleton;
    }

    public UMSocialService getController() {
        return controller_;
    }

    public void checkTokenExpired(Context context, final SucceedAndFailedHandler sh) {
        SHARE_MEDIA[] sms = { SHARE_MEDIA.QZONE };
        controller_.checkTokenExpired(context, sms, new SocializeListeners.UMDataListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, Map<String, Object> mapData) {
                if (status == 200) {
                    sh.onSuccess(mapData);
                } else {
                    sh.onFailure(0);
                }
            }
        });
    }

    private void doOauthVerify(Context context, final SHARE_MEDIA media, final SucceedAndFailedHandler sh) {

        Log.e("doOauthVerify", "context is " + context + " media is " + media + " sh is " + sh);
        controller_.doOauthVerify(context, media, new SocializeListeners.UMAuthListener() {

            @Override
            public void onCancel(SHARE_MEDIA arg0) {
                sh.onFailure(-10000); // 用户取消
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA media) {
                sh.onSuccess(value);
            }

            @Override
            public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
                sh.onFailure(0);
            }

            @Override
            public void onStart(SHARE_MEDIA arg0) {

            }
        });
    }

    /**
     * 通过微信登录
     */
    public void loginWechat(final Context context, final SucceedAndFailedHandler sh) {
        Log.e("LoginWechat", "conext is " + context);
//        final IWXAPI wxAPI= WXAPIFactory.createWXAPI(context, null);
//        wxAPI.registerApp("wx1360512d25eaad2e");
//        final SendAuth.Req req = new SendAuth.Req();
//        wxAPI.sendReq(req);
        controller_.getConfig().setSsoHandler(new UMWXHandler(context, "wx1360512d25eaad2e", "00a1cb7f140aafc2fd4933b765247ec0"));
        doOauthVerify(context, SHARE_MEDIA.WEIXIN, sh);
    }

    /**
     * 通过新浪登录
     *
     * @param context
     * @param sh
     *            登录成功后，返回onSuccess(obj) (obj中包含"uid", "access_token" )
     */
    public void loginSina(final Context context, final SucceedAndFailedHandler sh) {
        controller_.getConfig().setSinaCallbackUrl("http://oauth-client.ledongli.cn/sina_callback");
        controller_.getConfig().setSsoHandler(new SinaSsoHandler());
        doOauthVerify(context, SHARE_MEDIA.SINA, sh);
    }

    /**
     * 通过QQ登录
     *
     * @param context
     * @param sh
     *            登录成功后，返回onSuccess(obj) (obj中包含"openid", "access_token" )
     */
    public void loginTencent(final Context context, final SucceedAndFailedHandler sh) {
        controller_.getConfig().setSsoHandler(new QZoneSsoHandler((Activity) context, Constants.QQ_APPID, Constants.QQ_APPSECRET));// "100481185",
        // "f586d6abab308f3ae79bf001ccdf92cc"));
        doOauthVerify(context, SHARE_MEDIA.QZONE, sh);
    }

    /**
     * 对QQ的数据（ACCESS_TOKEN, UID 在服务器上进行验证）
     *
     * @param value
     * @param sh
     */
    public void oauthByTencent(Bundle value, final SucceedAndFailedHandler sh) {

        String accessToken = value.getString("access_token"); // 获取授权token
        String openId = value.getString("openid"); // 获取用户在此平台的ID
        // String nickname = db.get("nickname"); // 获取用户昵称

        SharedPreferences sPreferences = Util.getUserPreferences();
        int userId = sPreferences.getInt(Constants.USER_INFO_USERID, 0);
        String pCode = sPreferences.getString(Constants.DEVICE_ID, "");

        AndroidRequestParams params = new AndroidRequestParams();
        params.put("action", "authbyqq");
        params.put("uid", "" + userId);
        params.put("pc", "" + pCode);
        params.put("qq_id", "" + openId);
        params.put("token", "" + accessToken);

        HashMap<String, String> userMap = new HashMap<String, String>();
        userMap.put("pc", pCode);
        // userMap.put("IOS_APN_token", "");//TODO 上传ios token
        String userString = JSON.toJSONString(userMap);
        params.put("user", userString);

        RunsicRestClient.post(Constants.SERVER_IP, params, new HttpResponseHandler() {

            @Override
            public void onSuccess(byte[] responseBytes) {
                String statusString = "";
                int uid = 0;
                int errorCode = 0;
                try {
                    String responseBody = new String(responseBytes, Constants.CHARSET);
                    JSONObject obj = new JSONObject(responseBody);
                    statusString = obj.getString("status");
                    errorCode = Integer.parseInt(obj.getString("errorCode").toString());
                    uid = Integer.parseInt(obj.getString("uid"));
                } catch (Exception e) {
                    e.printStackTrace();
                    sh.onFailure(errorCode);
                    return;
                }
                if (!statusString.equals("OK")) {
                    sh.onFailure(errorCode);
                    return;
                }

                // 设置uid是否更改
                RunsicRestClientUsage.getInstance().setIsUidChanged(uid);
                // 成功返回uid
                sh.onSuccess(uid);
            }

            @Override
            public void onFailure(int errorCode) {
                sh.onFailure(errorCode);
            }
        });
    }

    /**
     * 对Sina的数据（ACCESS_TOKEN, UID 在服务器上进行验证）
     *
     * @param value
     * @param sh
     */
    public void oauthBySina(Bundle value, final SucceedAndFailedHandler sh) {
        String accessToken = value.getString("access_token");
        if (accessToken == null)
            accessToken = value.getString("access_secret"); // 获取授权token
        String openId = value.getString("uid"); // 获取用户在此平台的ID
        // String nickname = db.get("nickname"); // 获取用户昵称

        SharedPreferences sPreferences = Util.getUserPreferences();
        int userId = sPreferences.getInt(Constants.USER_INFO_USERID, 0);
        String pCode = sPreferences.getString(Constants.DEVICE_ID, "");

        AndroidRequestParams params = new AndroidRequestParams();
        params.put("action", "authbysina");
        params.put("uid", "" + userId);
        params.put("pc", "" + pCode);
        params.put("sina_id", "" + openId);
        params.put("token", "" + accessToken);

        HashMap<String, String> userMap = new HashMap<String, String>();
        userMap.put("pc", pCode);
        // userMap.put("IOS_APN_token", "");//TODO 上传ios token
        String userString = JSON.toJSONString(userMap);
        params.put("user", userString);

        RunsicRestClient.post(Constants.SERVER_IP, params, new HttpResponseHandler() {

            @Override
            public void onSuccess(byte[] responseBytes) {
                String statusString = "";
                int uid = 0;
                int errorCode = 0;
                try {
                    String responseBody = new String(responseBytes, Constants.CHARSET);
                    JSONObject obj = new JSONObject(responseBody);
                    statusString = obj.getString("status");
                    errorCode = Integer.parseInt(obj.getString("errorCode").toString());
                    uid = Integer.parseInt(obj.getString("uid"));

                } catch (Exception e) {
                    e.printStackTrace();
                    sh.onFailure(errorCode);
                    return;
                }

                if (!statusString.equals("OK")) {
                    sh.onFailure(errorCode);
                    return;
                }
                // 设置uid是否更改
                RunsicRestClientUsage.getInstance().setIsUidChanged(uid);

                sh.onSuccess(uid);
            }

            @Override
            public void onFailure(int errorCode) {
                sh.onFailure(errorCode);
            }

        });

    }

    public boolean isQQAuthenticated(Context context) {
        return OauthHelper.isAuthenticated(context, SHARE_MEDIA.QZONE);
    }

    public String getQQToken(Context context) {
        String[] qqTokens = OauthHelper.getAccessToken(context, SHARE_MEDIA.QZONE);
        if (qqTokens == null || qqTokens.length == 0)
            return null;

        if (!OauthHelper.isAuthenticatedAndTokenNotExpired(context, SHARE_MEDIA.QZONE))
            return null;

        return qqTokens[0];
    }

    public String getSinaToken(Context context) {
        String[] sinaTokens = OauthHelper.getAccessToken(context, SHARE_MEDIA.SINA);
        if (sinaTokens == null || sinaTokens.length == 0)
            return null;

        if (!OauthHelper.isAuthenticatedAndTokenNotExpired(context, SHARE_MEDIA.SINA))
            return null;

        return sinaTokens[0];
    }

    public long getQQAccessTokenExpireTime(Context context) {
        return OauthHelper.getTokenExpiresIn(context, SHARE_MEDIA.QZONE);
    }

    public void getWechatUserInfo(Context context, final SucceedAndFailedHandler sh) {
        getUserInfoFromShareMedia(context, SHARE_MEDIA.WEIXIN, sh);
    }

    public void getSinaWeiboUserInfo(Context context, final SucceedAndFailedHandler sh) {
        getUserInfoFromShareMedia(context, SHARE_MEDIA.SINA, sh);
    }

    public void getTencentUserInfo(Context context, final SucceedAndFailedHandler sh) {
        getUserInfoFromShareMedia(context, SHARE_MEDIA.QZONE, sh);

    }

//	public void getWandoujiaUserInfo(Context context, final SucceedAndFailedHandler sh) {
//
//		WandouGamesApi wandouGamesApi = XiaoBaiApplication.getWandouGamesApi();
//		String wdjId = wandouGamesApi.getCurrentPlayerInfo().getId();
//		String wdjNickName = wandouGamesApi.getCurrentPlayerInfo().getNick();
//		String wdjAvatarUrl = wandouGamesApi.getCurrentPlayerInfo().getAvatar();
//		String token = "";
//		Map<String, Object> map = new HashMap<String, Object>();
//		if (!wdjNickName.equals("")) {
//			map.put("screen_name", wdjNickName);
//		} else {
//			map.put("screen_name", wdjId);
//		}
//		map.put("profile_image_url", wdjAvatarUrl);
//		map.put("iswdj", "1");
//		map.put("wdj_id", wdjId);
//		map.put("token", token);
//		sh.onSuccess(map);
//	}

    private void getUserInfoFromShareMedia(Context context, SHARE_MEDIA sm, final SucceedAndFailedHandler sh) {
        controller_.getPlatformInfo(context, sm, new SocializeListeners.UMDataListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, Map<String, Object> map) {
                if (status == 200) {
                    sh.onSuccess(map);
                } else {
                    sh.onFailure(status);
                }
            }
        });
    }

    public void logout(final Context context) {
        controller_.loginout(context, new SocializeListeners.SocializeClientListener() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onComplete(int arg0, SocializeEntity arg1) {
                // TODO Auto-generated method stub

            }
        });

        controller_.deleteOauth(context, SHARE_MEDIA.QZONE, new SocializeListeners.SocializeClientListener() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onComplete(int arg0, SocializeEntity arg1) {
                // TODO Auto-generated method stub

            }
        });

        controller_.deleteOauth(context, SHARE_MEDIA.SINA, new SocializeListeners.SocializeClientListener() {

            @Override
            public void onStart() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onComplete(int arg0, SocializeEntity arg1) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void ssoActivityResult(int requestCode, int resultCode, Intent data) {
        /** 使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = controller_.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
