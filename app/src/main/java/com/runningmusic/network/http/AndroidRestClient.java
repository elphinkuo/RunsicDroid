package com.runningmusic.network.http;


import com.loopj.android.http.AsyncHttpClient;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;
import com.runningmusic.utils.RMCrashHandler;

public class AndroidRestClient extends AsyncHttpClient {
    public static String TAG = AndroidRestClient.class.getName();

    /**
     * 向服务器发送Request, action.cmd.act 提出到 url 用于服务器日志的记录
     * 
     * @param isPost
     * @param url
     * @param params
     * @param responseHandler
     */
    public void sendRequest(boolean isPost, String url, AndroidRequestParams params, HttpResponseHandler responseHandler) {
        if (isPost) {
            String action = params.getString("action");
            String cmd = params.getString("cmd");
            String act = params.getString("act");
            if (!url.endsWith("?")) {
                url += "?";
            }

            if (action != null) {
                url += "&action=" + action;
                params.remove("action");
            }

            if (cmd != null) {
                url += "&cmd=" + cmd;
                params.remove("cmd");
            }

            if (act != null) {
                url += "&act=" + act;
                params.remove("act");
                String mod = params.getString("mod");
                url += "&mod=" + mod;
                params.remove("mod");
            }
            // 添加Android版本
            url += "&v=" + Util.getVersion() + "%20android";
            url += "&vc=" + RMCrashHandler.getVersionCode() + "%20android";
            post(url, params, responseHandler);
        } else {
            if (Util.DEBUG) {
                Log.e(TAG, url + "?" + params);
            }

            get(url, params, responseHandler);
        }
    }

    public void sendGZipRequest(boolean isPost, String url, AndroidRequestParams params, HttpResponseHandler responseHandler) {
        if (isPost) {
            String action = params.getString("action");
            String cmd = params.getString("cmd");
            String act = params.getString("act");
            if (!url.endsWith("?")) {
                url += "?";
            }

            if (action != null) {
                url += "&action=" + action;
                params.remove("action");
            }

            if (cmd != null) {
                url += "&cmd=" + cmd;
                params.remove("cmd");
            }

            if (act != null) {
                url += "&act=" + act;
                params.remove("act");
                String mod = params.getString("mod");
                url += "&mod=" + mod;
                params.remove("mod");
            }
            // 添加Android版本
            url += "&v=" + Util.getVersion() + "%20android";
            url += "&vc=" + RMCrashHandler.getVersionCode() + "%20android";
            post(url, params, responseHandler);
        } else {
            if (Util.DEBUG) {
                Log.e(TAG, url + "?" + params);
            }

            get(url, params, responseHandler);
        }
        this.addHeader("Accept-Encoding", "gzip");
    }


    public void sendRequestWithoutParamas(boolean isPost, String url, HttpResponseHandler responseHandler) {

        get(url, responseHandler);

    }
    // private String urlEncode(String urlStr) throws MalformedURLException, URISyntaxException {
    // URL url = new URL(urlStr);
    // URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
    // url = uri.toURL();
    // return url.toString();
    // }

    // @Override
    // public RequestHandle get(String url, RequestParams params,
    // ResponseHandlerInterface responseHandler) {
    // // TODO Auto-generated method stub
    // return super.get(url, params, responseHandler);
    // }
    //
    // @Override
    // public RequestHandle post(String url, RequestParams params,
    // ResponseHandlerInterface responseHandler) {
    // // TODO Auto-generated method stub
    // return super.post(url, params, responseHandler);
    // }
}
