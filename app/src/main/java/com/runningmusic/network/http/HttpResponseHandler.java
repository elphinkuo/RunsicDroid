package com.runningmusic.network.http;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

public abstract class HttpResponseHandler extends AsyncHttpResponseHandler {
    public static String TAG = HttpResponseHandler.class.getName();

    @Override
    public void onFailure(int stateCode, Header[] headers, byte[] responseBytes, Throwable e) {
        onFailure(stateCode);
        if (stateCode == 403) {
            //未授权
        } else if (stateCode == 400) {
            //服务器已处理的错误
        } else if (stateCode == 500) {
            //服务器未处理错误
        }
    }

    @Override
    public void onSuccess(int stateCode, Header[] headers, byte[] responseBytes) {
        // 200属于正常返回，其他成功标识表示还未处理结束
        if (stateCode == 200)
            onSuccess(responseBytes);
    }

    public abstract void onSuccess(byte[] responseBytes);

    public abstract void onFailure(int errorCode);

}
