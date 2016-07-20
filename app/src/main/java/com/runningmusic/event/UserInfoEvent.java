package com.runningmusic.event;

import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by guofuming on 21/7/16.
 */
public class UserInfoEvent {
    public String headImageURL;
    public String nickName;
    public String city;

    public UserInfoEvent(Object object) {
        HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
        this.headImageURL = hashMap.get("headimgurl").toString();
        this.nickName = hashMap.get("nickname").toString();
        this.city = hashMap.get("province").toString() + hashMap.get("city").toString();
    }
}
