package com.runningmusic.oauth;

/**
 * Created by guofuming on 19/7/16.
 */
public interface SucceedAndFailedHandler {
    void onSuccess(Object obj);
    void onFailure(int errorCode);
}
