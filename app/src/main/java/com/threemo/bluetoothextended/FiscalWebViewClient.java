package com.threemo.bluetoothextended;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * Created by andre on 27.02.2015.
 */
public class FiscalWebViewClient extends WebViewClient {
    private TextView titlebar;
    private WebView webView;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        titlebar.setText("Web Page Loading...");
        webView.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        titlebar.setText(url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
//page could not be loaded
    }
}
