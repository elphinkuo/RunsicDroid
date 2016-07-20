package com.runningmusic.oauth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.umeng.socialize.bean.HandlerRequestCode;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ThirdShareService {
	private static ThirdShareService instance_;
	private ThirdShareParams sp_;
	// 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
	private final static UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

	public static synchronized ThirdShareService getInstance() {
		if (instance_ == null) {
			instance_ = new ThirdShareService();
		}
		return instance_;
	}

	public void shareToQQ(final Activity activity, ThirdShareParams params) {
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, "1104829333", "KEYEF2H3EtBtAVvfJJu");
		qqSsoHandler.addToSocialSDK();
		// mController.getConfig().getSsoHandler(HandlerRequestCode.QQ_REQUEST_CODE).isClientInstalled();
		sp_ = params;
		QQShareContent qqShareContent = new QQShareContent();
		// qqShareContent.setShareContent(sp_.getText());
		// qqShareContent.setTitle(sp_.getTitle());
		qqShareContent.setShareImage(new UMImage(activity, sp_.getImagePath()));
		// qqShareContent.setTargetUrl(sp_.getUrlString());
		mController.setShareMedia(qqShareContent);
		if (mController.getConfig().getSsoHandler(HandlerRequestCode.QQ_REQUEST_CODE).isClientInstalled()) {
			// mController.getConfig().closeToast();
			mController.directShare(activity, SHARE_MEDIA.QQ, new SnsPostListener() {

				@Override
				public void onStart() {
					// Toast.makeText(activity, "分享中...",
					// Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					mController.getConfig().closeToast();
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						// Toast.makeText(activity, "分享失败",
						// Toast.LENGTH_SHORT).show();
					}
					mController.getConfig().cleanListeners();

				}
			});
		} else {
			Toast.makeText(activity, "你还没有安装QQ，请先去下载", Toast.LENGTH_SHORT).show();
		}
	}

	public void inviteQQ(final Activity activity, ThirdShareParams params) {
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, "1104829333", "KEYEF2H3EtBtAVvfJJu");
		qqSsoHandler.addToSocialSDK();
		sp_ = params;
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setTitle(sp_.getTitle());
		qqShareContent.setShareContent(sp_.getText());
		qqShareContent.setShareImage(new UMImage(activity, sp_.getImagePath()));
		qqShareContent.setTargetUrl(sp_.getUrlString());
		// qqShareContent.setTargetUrl("http://wise-adapt.ledongli.cn");
		mController.setShareMedia(qqShareContent);
		if (mController.getConfig().getSsoHandler(HandlerRequestCode.QQ_REQUEST_CODE).isClientInstalled()) {
			mController.getConfig().closeToast();
			mController.directShare(activity, SHARE_MEDIA.QQ, new SnsPostListener() {

				@Override
				public void onStart() {
					// Toast.makeText(activity, "邀请中...",
					// Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(activity, "邀请成功", Toast.LENGTH_SHORT).show();
					} else {
						mController.getConfig().closeToast();
					}
					mController.getConfig().cleanListeners();
				}
			});
		} else {
			Toast.makeText(activity, "你还没有安装QQ，请先去下载", Toast.LENGTH_SHORT).show();
		}
	}

	public void shareToWechat(final Activity activity, ThirdShareParams params) {
		String appId = "wx1360512d25eaad2e"; // 正式版appid
		sp_ = params;
		String contentUrl = sp_.getUrlString();
		// mController.getConfig().supportWXPlatform(activity, appId,
		// contentUrl);
		// 添加微信
		UMWXHandler wxHandler = new UMWXHandler(activity, appId);
		wxHandler.addToSocialSDK();
		UMImage mUMImgBitmap = new UMImage(activity, sp_.getImagePath());
		mController.setShareMedia(mUMImgBitmap);
		if (mController.getConfig().getSsoHandler(HandlerRequestCode.WX_REQUEST_CODE).isClientInstalled()) {
			mController.getConfig().closeToast();
			mController.directShare(activity, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
				@Override
				public void onStart() {
					// Toast.makeText(activity, "分享中...",
					// Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						// Toast.makeText(activity, "分享成功",
						// Toast.LENGTH_SHORT).show();
					} else {
						// Toast.makeText(activity, "分享失败",
						// Toast.LENGTH_SHORT).show();
					}
					mController.getConfig().cleanListeners();
				}

			});
		} else {
			Toast.makeText(activity, "你还没有安装微信，请先去下载", Toast.LENGTH_SHORT).show();
		}
	}

	public void inviteWechat(final Activity activity, ThirdShareParams params) {
		String appId = "wx1360512d25eaad2e"; // 正式版appid
		sp_ = params;
		String contentUrl = sp_.getUrlString();
		// ((Object) mController.getConfig()).supportWXPlatform(activity, appId,
		// contentUrl);
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(activity, appId);
		wxHandler.addToSocialSDK();
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(sp_.getText());
		weixinContent.setTitle(sp_.getTitle());
		weixinContent.setTargetUrl(contentUrl);
		// mController.setShareContent(sp_.getText());
		weixinContent.setShareImage(new UMImage(activity, sp_.getImagePath()));
		mController.setShareMedia(weixinContent);
		// UMImage mUMImgBitmap = new UMImage(activity, sp_.getImagePath());
		// mController.setShareMedia(mUMImgBitmap);
		if (mController.getConfig().getSsoHandler(HandlerRequestCode.WX_REQUEST_CODE).isClientInstalled()) {
			mController.getConfig().closeToast();
			mController.directShare(activity, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
				@Override
				public void onStart() {
					// Toast.makeText(activity, "邀请中...",
					// Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						// Toast.makeText(activity, "邀请成功",
						// Toast.LENGTH_SHORT).show();
					} else {
						// Toast.makeText(activity, "邀请失败",
						// Toast.LENGTH_SHORT).show();
					}
					mController.getConfig().cleanListeners();
				}

			});
		} else {
			Toast.makeText(activity, "你还没有安装微信，请先去下载", Toast.LENGTH_SHORT).show();
		}
	}

	public void shareToWeibo(final Activity activity, ThirdShareParams params) {
		// mController.getConfig().setSinaCallbackUrl("http://fenziphysical.yizhedian.com.cn/sina_callback");
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		sp_ = params;
		// 设置新浪分享内容
		SinaShareContent sinaShareContent = new SinaShareContent();
		sinaShareContent.setTitle(sp_.getTitle());
		sinaShareContent.setShareContent(sp_.getText());
		sinaShareContent.setShareImage(new UMImage(activity, sp_.getImagePath()));
		mController.setShareMedia(sinaShareContent);
		mController.postShare(activity, SHARE_MEDIA.SINA, new SnsPostListener() {

			@Override
			public void onStart() {
				// Toast.makeText(activity, "分享开始", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
				if (eCode == StatusCode.ST_CODE_SUCCESSED) {
					Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
				} else {
					// Toast.makeText(activity, "分享失败",
					// Toast.LENGTH_SHORT).show();
				}
				mController.getConfig().cleanListeners();
			}
		});
	}

	public void inviteContact(final Activity activity, ThirdShareParams params) {
		// 设置短信分享内容
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
		sp_ = params;
		SmsShareContent sms = new SmsShareContent();
		sms.setShareContent(sp_.getText());
		mController.setShareMedia(sms);
		mController.directShare(activity, SHARE_MEDIA.SMS, new SnsPostListener() {
			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
				if (eCode == StatusCode.ST_CODE_SUCCESSED) {
					Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
				} else {
					// Toast.makeText(activity, "分享失败",
					// Toast.LENGTH_SHORT).show();
				}
				mController.getConfig().cleanListeners();
			}

		});
	}

	public void shareToWechatCircle(final Activity activity, ThirdShareParams params) {
		String appId = "wx1360512d25eaad2e"; // 正式版appid
		sp_ = params;
		String contentUrl = sp_.getUrlString();
		// mController.getConfig().supportWXPlatform(activity, appId,
		// contentUrl);
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(activity, appId);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		// mController.getConfig().getSsoHandler(HandlerRequestCode.WX_CIRCLE_REQUEST_CODE).isClientInstalled();
		// wxHandler.setWXTitle(sp_.getTitle());
		// mController.setShareContent(sp_.getText());
		UMImage mUMImgBitmap = new UMImage(activity, sp_.getImagePath());
		mController.setShareMedia(mUMImgBitmap);
		if (mController.getConfig().getSsoHandler(HandlerRequestCode.WX_CIRCLE_REQUEST_CODE).isClientInstalled()) {
			mController.directShare(activity, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
				@Override
				public void onStart() {
					// Toast.makeText(activity, "邀请中...",
					// Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						// Toast.makeText(activity, "分享失败",
						// Toast.LENGTH_SHORT).show();
					}
					mController.getConfig().cleanListeners();
				}

			});
		} else {
			Toast.makeText(activity, "你还没有安装微信，请先去下载", Toast.LENGTH_SHORT).show();
		}
	}

	// 暂不用
	public void shareToQZone(final Activity activity, ThirdShareParams params) {
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, "1104829333", "KEYEF2H3EtBtAVvfJJu");
		qZoneSsoHandler.addToSocialSDK();

		sp_ = params;
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent(sp_.getText());
		qzone.setTargetUrl(sp_.getUrlString());
		qzone.setTitle(sp_.getTitle());
		qzone.setShareImage(new UMImage(activity, sp_.getImagePath()));
		mController.setShareMedia(qzone);
		mController.directShare(activity, SHARE_MEDIA.QZONE, new SnsPostListener() {
			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
				if (eCode == StatusCode.ST_CODE_SUCCESSED) {
					Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(activity, "分享失败", Toast.LENGTH_SHORT).show();
				}
				mController.getConfig().cleanListeners();
			}
		});
	}

	// 暂不用
	public void shareToTencentWeibo(final Activity activity, ThirdShareParams params) {
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		sp_ = params;
		TencentWbShareContent tencentWB = new TencentWbShareContent();
		tencentWB.setShareContent(sp_.getText());
		// tencentWB.setTargetUrl("http://sns.whalecloud.com/app/7BUCJN ");
		tencentWB.setTitle(sp_.getTitle());
		tencentWB.setShareImage(new UMImage(activity, sp_.getImagePath()));
		mController.setShareMedia(tencentWB);
		mController.directShare(activity, SHARE_MEDIA.TENCENT, new SnsPostListener() {
			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
				Toast.makeText(activity, "分享完成", Toast.LENGTH_SHORT).show();
				mController.getConfig().cleanListeners();
			}
		});
	}

	// 暂不用
	public void shareToRenren(final Activity activity, ThirdShareParams params) {
		// 设置renren分享内容
		RenrenShareContent renrenShareContent = new RenrenShareContent();
		sp_ = params;
		renrenShareContent.setShareContent("测试人人分享内容");
		UMImage image = new UMImage(activity, sp_.getImagePath());
		renrenShareContent.setShareImage(image);
		renrenShareContent.setAppWebSite("wise-adapter.ledongli.cn");
		mController.setShareMedia(renrenShareContent);
		mController.directShare(activity, SHARE_MEDIA.RENREN, new SnsPostListener() {
			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
				Toast.makeText(activity, "分享完成", Toast.LENGTH_SHORT).show();
				mController.getConfig().cleanListeners();
			}
		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		/** 使用SSO授权必须添加如下代码 */
		Log.d("ONACTIVITY", "### onActivityResult");
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

}
