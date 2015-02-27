package com.threemo.bluetoothextended;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by andre
 */
public class BluetoothHandler {

    private String status = null;

    // @Constructor
    public BluetoothHandler() {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        status = ((!BA.isEnabled()) ? "Bluetooth OFF" : "Bluetooth ON");
    }

    // return bluetooth status
    public String getStatus() {
        return this.status;
    }

    @JavascriptInterface
    public void on(Activity activity) {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();

        if (!BA.isEnabled()) {
            BA.enable();
            Toast.makeText(activity, "Turned on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Already on", Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void off(Activity activity) {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();

        if (BA.isEnabled()) {
            BA.disable();
            Toast.makeText(activity, "Turned off", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Already off", Toast.LENGTH_SHORT).show();
        }
    }

    public String showDeviceList(Activity activity) {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();
        StringBuilder deviceLine = new StringBuilder();
        int count = 1;

        //System.out.println(toast.getDuration());
        Toast.makeText(activity, "Showing Paired Devices", Toast.LENGTH_SHORT).show();

        if (BA.isEnabled()) {
            for (BluetoothDevice device : pairedDevices) {
                list.add(device.getName());
                deviceLine.append(device.getName());
                Toast.makeText(activity, "Device no " + count + " " + device.getName(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
        }
        return deviceLine.toString();
    }

}
