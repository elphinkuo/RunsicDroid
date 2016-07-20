package com.runningmusic.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author guofuming on 2015/08/15
 *
 */

public class JsonArrayRequestWithCookie extends JsonArrayRequest {
    private Map<String, String> mHeaders = new HashMap<String, String>();

    public JsonArrayRequestWithCookie(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    //发送请求时，往Header中添加cookie，可以一并发送
    public void setCookie(String cookie) throws AuthFailureError {
        mHeaders.put("Cookie", cookie);
    }
}
