package com.runningmusic.activity;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidquery.AQuery;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Log;

public class WebViewActivity extends Activity {
    public static String TAG = WebViewActivity.class.getName();
    private WebView webView;

    private AQuery aQuery;
    private Activity context;

    private String titleText;
    private String urlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        Log.e(TAG, "Bundle is " + savedInstanceState);
        titleText = getIntent().getStringExtra("title");
        urlString = getIntent().getStringExtra("url");
        setContentView(R.layout.activity_web_view);
//        webView = new WebView(context);
        webView = (WebView) findViewById(R.id.webview_content);

        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("gb2312");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webView.loadUrl(urlString);


        aQuery = new AQuery(this);
        aQuery.id(R.id.webview_title).text(titleText);
        aQuery.id(R.id.webview_back).clickable(true).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });





    }
}
