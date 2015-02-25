package com.threemo.bluetoothextended;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by andre on 25.02.2015.
 */
public class BluetoothHandler {

    public BluetoothHandler() {
    }

    @JavascriptInterface
    public void on(Context mContext) {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();

        if (!BA.isEnabled()) {
            BA.enable();
            Toast.makeText(mContext, "Turned on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Already on", Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void off(Context mContext) {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();

        if (BA.isEnabled()) {
            BA.disable();
            Toast.makeText(mContext, "Turned off", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Already off", Toast.LENGTH_SHORT).show();
        }


    }


    public void showDeviceList(Context mContext) {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();
        int count = 1;

        //System.out.println(toast.getDuration());
        Toast.makeText(mContext, "Showing Paired Devices", Toast.LENGTH_SHORT).show();

        if (BA.isEnabled()) {
            for (BluetoothDevice device : pairedDevices) {
                list.add(device.getName());
                Toast.makeText(mContext, "Device no " + count + " " + device.getName(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mContext, "Bluetooth not enabled", Toast.LENGTH_LONG).show();

        }


    }

    public void setVisible(Context mContext) {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        //Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        Toast.makeText(mContext, "Not implemented", Toast.LENGTH_LONG).show();
    }
}
