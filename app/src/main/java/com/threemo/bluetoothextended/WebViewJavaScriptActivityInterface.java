package com.threemo.bluetoothextended;

import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by andre
 */
public class WebViewJavaScriptActivityInterface {
    private MainActivity parentActivity = null;

    /**
     * Instantiate the interface and set the context
     */
    public WebViewJavaScriptActivityInterface(MainActivity activity) {
        parentActivity = activity;
    }

    public WebViewJavaScriptActivityInterface() {
    }

    @JavascriptInterface
    public void switchBluetoothOn() {
        BluetoothHandler bth = new BluetoothHandler();
        bth.on(parentActivity);
    }

    @JavascriptInterface
    public void switchBluetoothOff() {
        BluetoothHandler bth = new BluetoothHandler();
        bth.off(parentActivity);
    }

    @JavascriptInterface
    public String showDeviceList() {
        BluetoothHandler bth = new BluetoothHandler();
        return bth.showDeviceList(parentActivity);
    }

    @JavascriptInterface
    public String setField() {
        //String javascript = "javascript: document.getElementById('BluetoothStatus').innerHTML='Status inactive Julian!';";
        BluetoothHandler bth = new BluetoothHandler();
        return bth.getStatus();
    }

    @JavascriptInterface
    public void showToast(String toast) {
        //Toast.makeText(mContext,  mContext.getClass().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(parentActivity, toast, Toast.LENGTH_SHORT).show();
    }

}