package com.threemo.bluetoothextended;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView webView;
    private TextView titlebar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* enable webview */
        webView = (WebView) findViewById(R.id.webview);
        titlebar = (TextView) findViewById(R.id.titlebar);

        /* enable javascript */
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);

        //enable JavaScript and attach javascript interface
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebViewJavaScriptActivityInterface(this), "Android");
        webView.setWebViewClient(new FiscalWebViewClient());
        webView.setWebChromeClient(new FiscalWebChromeClient());
        webView.loadUrl("file:///android_asset/index.html");
    }

    /*
        @name FiscalWebViewClient
        @description allows listening for web page events. Events such as when the web page begins
        to load, page has finished loading, when there is an error related to page loading, on form
        submission, links clicked by the end user, and other events.
     */
    public class FiscalWebViewClient extends WebViewClient {
        /*
            ensures that the application gets control of the new web page one it finishes loading.
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            titlebar.setText("Web Page Loading...");
            webView.loadUrl(url);
            return true;
        }

        /*
            method is called once the web page loads. Here in this method the address of the web page
            is set to textview , once the web page finishes loading. Similarly, canGoBack(),goBack(),canGoForward()
            are navigation methods which allow the user to move back and forth within the WebView page history.
         */
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

    /*
        @name FiscalWebChromeClient
        @description allows listening to JavaScript calls, notification of the current page such as console
        messages, alerts, progress updates of page, and other JavaScript calls.
    */
    private class FiscalWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("JavaScript Alert !")
                    .setMessage(message + " ale")
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do your stuff here
                                    result.confirm();
                                }
                            }).setCancelable(false).create().show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("JavaScript Confirm Alert !")
                    .setMessage(message + " conf")
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do your stuff here
                                    result.confirm();
                                }
                            }).setCancelable(false).create().show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, final JsPromptResult result) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("JavaScript Prompt Alert !")
                    .setMessage(message + " prom")
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do your stuff here
                                    result.confirm();
                                }
                            }).setCancelable(false).create().show();
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        activate javascript usage
     */
    public class AppJavaScriptProxy {

        private Activity activity = null;
        private WebView webView = null;

        public AppJavaScriptProxy(Activity activity, WebView webView) {
            this.activity = activity;
            this.webView = webView;
        }

        @JavascriptInterface
        public void showMessage(final String message) {

            final Activity theActivity = this.activity;
            final WebView theWebView = this.webView;

            this.activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (!theWebView.getUrl().startsWith("http://google.com")) {
                        return;
                    }

                    Toast toast = Toast.makeText(theActivity.getApplicationContext(),
                            message,
                            Toast.LENGTH_SHORT);

                    toast.show();
                }

            });
        }
    }

    public void javascriptCallFinished(int val) {
        System.out.println(val);
    }
}