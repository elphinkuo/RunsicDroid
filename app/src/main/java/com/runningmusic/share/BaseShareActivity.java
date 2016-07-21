package com.runningmusic.share;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.runningmusic.application.BaseActivity;
import com.runningmusic.oauth.ThirdShareParams;
import com.runningmusic.oauth.ThirdShareService;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.ImageUtil;
import com.runningmusic.utils.Util;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Date;
import java.util.Random;

@SuppressLint("HandlerLeak")
public class BaseShareActivity extends BaseActivity{

    protected static final int MSG_TOAST = 1;
    protected static final int MSG_ACTION_CCALLBACK = 2;
    protected static final int MSG_CANCEL_NOTIFY = 3;
    protected static final int MSG_SSO = 4;

    protected Activity thisActivity_ = this;
    protected String imagePath_ = null;
    private DisplayMetrics metrics_;

    private final boolean onSoftKeyboardShown_ = false;

    protected AQuery query_;

    // 分享文案
    String shareTitle_ = "奔跑吧音乐";
    String shareText_ = "当音乐遇到你，让脚步听到它的声音";
    String shareURL_ = "http://a.app.qq.com/o/simple.jsp?pkgname=com.runningmusic.runninspire";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        query_ = new AQuery(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public Bitmap shareCreateImage() {
        View view = findViewById(R.id.share_content);
        Bitmap shareBitmap = ImageUtil.createBitmapByView(view);

        return shareBitmap;
    }

    protected void init() {
        // 添加分享到微信
//        query_.id(R.id.share_wechat).clicked(thisActivity_, "tapShareWechat");
        query_.id(R.id.share_wechatmoments).clicked(thisActivity_, "tapShareWechatmoments");
        // 添加qq分享
        query_.id(R.id.share_qq).clicked(thisActivity_, "tapShareQQ");
        // 添加 新浪微博分享
//        query_.id(R.id.share_wechat).clicked(thisActivity_, "tapShareWechat");
        // // 添加人人分享
        // query_.id(R.id.share_renren).clicked(thisActivity_,
        // "tapShareRenren");
        // // 添加QZONE分享
         query_.id(R.id.share_qzone).clicked(thisActivity_, "tapShareQZone");
        // // 添加腾讯微博分享
        // query_.id(R.id.share_tencentweibo).clicked(thisActivity_,
        // "tapShareTencentweibo");
        // 什么分享都可以添加
        // 移动bottom到合适的位置
        metrics_ = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics_);

        // WindowManager wm = thisActivity_.getWindowManager();
        // screenHeight_ = wm.getDefaultDisplay().getHeight();
//        query_.id(R.id.share_content_bottom).getView().setVisibility(View.GONE);
        // 添加点击图片的响应时间，实现点击时 返回的功能
        query_.id(R.id.share_content).clicked(thisActivity_, "tapBackPress");
//        query_.id(R.id.share_touch_view).clicked(thisActivity_, "tapBackPress");
    }

    public void tapBackPress() {
        // 判断当显示键盘的时候
        if (onSoftKeyboardShown_) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    private final Handler handler_ = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 100:
//                    query_.id(R.id.share_wechat).clickable(true);
                    query_.id(R.id.share_wechatmoments).clickable(true);
                    query_.id(R.id.share_qq).clickable(true);
                    query_.id(R.id.share_qzone).clickable(true);
                    break;
                default:
                    break;
            }

        }

    };

    private void disableShareBtn() {
        query_.id(R.id.share_qq).clickable(false);
//        query_.id(R.id.share_wechat).clickable(false);
        query_.id(R.id.share_wechatmoments).clickable(false);
        query_.id(R.id.share_qzone).clickable(false);
        handler_.sendEmptyMessageDelayed(100, 500);
    }

    // ----------------------------------------------------------------------------

    public void tapShareQQ() {
        if (imagePath_ == null) {
            currentShareType_ = ShareType.shareQQ;
            createImage();
            return;
        }
        MobclickAgent.onEvent(this, "event_shareto_QQ");
        disableShareBtn();
        ThirdShareParams sp = new ThirdShareParams();
        sp.setImagePath(imagePath_);
        sp.setTitle(shareTitle_);
        sp.setText(shareText_);
        sp.setUrlString(shareURL_);
        ThirdShareService.getInstance().shareToQQ(thisActivity_, sp);
    }

    public void tapShareWechat() {
        if (imagePath_ == null) {
            currentShareType_ = ShareType.shareWechat;
            createImage();
            return;
        }
        MobclickAgent.onEvent(this, "event_shareto_wechat");
        disableShareBtn();

        ThirdShareParams sp = new ThirdShareParams();
        // sp.shareType = Platform.SHARE_IMAGE;
        sp.setImagePath(imagePath_);
        ThirdShareService.getInstance().shareToWechat(thisActivity_, sp);
        // String name = Wechat.NAME;
        // plat.setPlatformActionListener(this);
        // plat.share(sp);

    }

    public void tapShareWechatmoments() {
        if (imagePath_ == null) {
            currentShareType_ = ShareType.shareWechatMoments;
            createImage();
            return;
        }
        MobclickAgent.onEvent(this, "event_shareto_wechatMoments");
        disableShareBtn();
        ThirdShareParams sp = new ThirdShareParams();

        // sp.shareType = Platform.SHARE_IMAGE;
        sp.setImagePath(imagePath_);
        ThirdShareService.getInstance().shareToWechatCircle(thisActivity_, sp);
    }

    public void tapShareSinaweibo() {
        if (imagePath_ == null) {
            currentShareType_ = ShareType.shareSinaweibo;
            createImage();
            return;
        }
        MobclickAgent.onEvent(this, "event_shareto_sinaWeibo");
        disableShareBtn();
        ThirdShareParams sp = new ThirdShareParams();
        sp.setImagePath(imagePath_);
        sp.setTitle(shareTitle_);
        sp.setText(shareText_);
        sp.setUrlString(shareURL_);
        ThirdShareService.getInstance().shareToWeibo(thisActivity_, sp);

    }

    // 暂无用了
    public void tapShareTencentweibo() {
        if (imagePath_ == null) {
            currentShareType_ = ShareType.shareTencentweibo;
            createImage();
            return;
        }

        ThirdShareParams sp = new ThirdShareParams();
        sp.setImagePath(imagePath_);
        sp.setTitle(shareTitle_);
        sp.setText(shareText_);
        sp.setUrlString(shareURL_);
        ThirdShareService.getInstance().shareToTencentWeibo(thisActivity_, sp);
    }

    // 暂无用了
    public void tapShareRenren() {
        if (imagePath_ == null) {
            currentShareType_ = ShareType.shareRenren;
            createImage();
            return;
        }

        ThirdShareParams sp = new ThirdShareParams();
        sp.setImagePath(imagePath_);
        sp.setImagePath(imagePath_);
        sp.setTitle(shareTitle_);
        sp.setText(shareText_);
        sp.setUrlString(shareURL_);
        ThirdShareService.getInstance().shareToRenren(thisActivity_, sp);
    }

    // 暂无用了
    public void tapShareQZone() {
        if (imagePath_ == null) {
            currentShareType_ = ShareType.shareQZone;
            createImage();
            return;
        }

        ThirdShareParams sp = new ThirdShareParams();
        sp.setImagePath(imagePath_);
        sp.setImagePath(imagePath_);
        sp.setTitle(shareTitle_);
        sp.setText(shareText_);
        sp.setUrlString(shareURL_);
        ThirdShareService.getInstance().shareToQZone(thisActivity_, sp);
    }

    // 分享图片的路径
    public String getNewPath() {

        String path = thisActivity_.getFilesDir().getAbsolutePath();
        Random random = new Random();
        String fileName = random.nextDouble() + "share_image.jpg";

        File out = new File(path);
        if (!out.exists()) {
            out.mkdirs();
        }

        return path + fileName;
    }

    // 去生成一个分享的图片
    protected void createImage() {
        showDialog("正在截屏...");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    Bitmap bitmap = shareCreateImage();
                    String fileString = getNewPath();
                    AQUtility.store(new File(fileString), Util.Bitmap2Bytes(bitmap));
                    Log.i("ImageURL", fileString);

                    imagePath_ = fileString;
                } catch (Exception e) {
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // 回调原有的分享接口
                        tapShareType();
                    }
                });
            }
        }, 0);

    }

    private ShareType currentShareType_ = null;

    public enum ShareType {
        shareQQ, shareWechat, shareWechatMoments, shareSinaweibo, shareTencentweibo, shareQZone, shareRenren
    }

    protected void tapShareType() {
        hideDialog();
        if (imagePath_ == null) {
            showMsg("截屏失败");
            return;
        }

        switch (currentShareType_) {
            case shareQQ:
                tapShareQQ();
                break;
            case shareWechat:
                tapShareWechat();
                break;
            case shareWechatMoments:
                tapShareWechatmoments();
                break;
            case shareSinaweibo:
                tapShareSinaweibo();
                break;
            case shareTencentweibo:
                tapShareTencentweibo();
                break;
            case shareQZone:
                tapShareQZone();
                break;
            case shareRenren:
                tapShareRenren();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ONACTIVITY", "### ready to call onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        ThirdShareService.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }
}
