package com.threemo.bluetoothextended;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.provider.SyncStateContract;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by andre on 24.02.2015.
 */
public class MyWebViewClientImpl extends WebViewClient {

    private Activity activity = null;

    public MyWebViewClientImpl(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        if (url.indexOf("google.com") > -1) return false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        return false;
    }


}
