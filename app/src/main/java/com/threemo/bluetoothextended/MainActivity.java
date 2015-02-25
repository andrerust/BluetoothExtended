package com.threemo.bluetoothextended;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private WebView webView = null;
    private BluetoothAdapter BA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* enable webview */
        WebView webView = (WebView) findViewById(R.id.webview);

        /* enable javascript */
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        webView.loadUrl("file:///android_asset/index.html");
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
                    if (!theWebView.getUrl().startsWith("http://turorials.jenkov.vom")) {
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