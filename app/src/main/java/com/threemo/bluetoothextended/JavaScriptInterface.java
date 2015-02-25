package com.threemo.bluetoothextended;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by andre on 25.02.2015.
 */
public class JavaScriptInterface extends Activity {
    private MainActivity parentActivity;
    private Context mContext;

    /**
     * Instantiate the interface and set the context
     */
    public JavaScriptInterface(Context c) {
        mContext = c;
    }

    public JavaScriptInterface() {
    }

    @JavascriptInterface
    public void switchBluetoothOn() {
        BluetoothHandler bth = new BluetoothHandler();
        bth.on(mContext);

        //Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //startActivityForResult(turnOn, 0);
    }

    @JavascriptInterface
    public void switchBluetoothOff() {
        BluetoothHandler bth = new BluetoothHandler();
        bth.off(mContext);
    }

    @JavascriptInterface
    public void showDeviceList() {
        BluetoothHandler bth = new BluetoothHandler();
        bth.showDeviceList(mContext);
    }

    @JavascriptInterface
    public void setVisible() {
        BluetoothHandler bth = new BluetoothHandler();
        bth.setVisible(mContext);
    }

    @JavascriptInterface
    public void showToast(String toast) {
        //Toast.makeText(mContext,  mContext.getClass().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

}