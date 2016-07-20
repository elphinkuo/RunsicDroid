package com.runningmusic.wxapi;

import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

/**
 * Created by guofuming on 1/3/16.
 */
public class WXEntryActivity extends WXCallbackActivity {

    @Override
    public void onReq(BaseReq arg0) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Bundle bundle = new Bundle();

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) resp).code;

                break;
            default:
                break;
        }
    }
}
