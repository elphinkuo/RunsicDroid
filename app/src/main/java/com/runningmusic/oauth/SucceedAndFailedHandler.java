package com.runningmusic.oauth;

/**
 * Created by guofuming on 19/7/16.
 */
public interface SucceedAndFailedHandler {
    public void onSuccess(Object obj);
    public void onFailure(int errorCode);
}
