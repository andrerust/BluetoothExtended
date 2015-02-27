package com.threemo.bluetoothextended;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

import com.datecs.fiscalprinter.FiscalPrinterException;
import com.datecs.fiscalprinter.bgr.FMP10BG;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by andre
 */
public class BluetoothHandler extends Activity {
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String status = null;
    private FMP10BG mFMP;
    private BluetoothSocket mBtSocket;
    private BluetoothAdapter mBtAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DEVICE = 2;

    // @Constructor
    public BluetoothHandler() {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        status = ((!BA.isEnabled()) ? "Bluetooth OFF" : "Bluetooth ON");
    }

    public boolean bluetoothIsAccessible() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        return (mBtAdapter != null) ? true : false;
    }

    public boolean bluetoothIsActive() {
        return (mBtAdapter.isEnabled()) ? true : false;
    }


    private interface MethodInvoker {
        public void invoke() throws IOException;
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT: {
                if (resultCode == RESULT_OK) {
                    //selectDevice();
                } else {
                    finish();
                }
                break;
            }
            case REQUEST_DEVICE: {
                if (resultCode == RESULT_OK) {
                    String address = data.getStringExtra(DeviceActivity.EXTRA_ADDRESS);
                    connect(address);
                } else {
                    finish();
                }
                break;
            }
        }
    }
*/
    public void connect(final String address) {
        invokeHelper(new MethodInvoker() {
            @Override
            public void invoke() throws IOException {
                final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
                final BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                socket.connect();

                mBtSocket = socket;
                final InputStream in = socket.getInputStream();
                final OutputStream out = socket.getOutputStream();
                mFMP = new FMP10BG(in, out);
                postToast("Connected");

            }
        });
    }

    private void postToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void invokeHelper(final MethodInvoker invoker) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getString(R.string.msg_please_wait));
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        dialog.show();

        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    invoker.invoke();
                } catch (FiscalPrinterException e) { // Fiscal printer error
                    e.printStackTrace();
                    postToast("FiscalPrinterException: " + e.getMessage());
                } catch (IOException e) { //Communication error
                    e.printStackTrace();
                    postToast("IOException: " + e.getMessage());
                    disconnect();
                    //selectDevice(this);
                } catch (Exception e) { // Critical exception
                    e.printStackTrace();
                    postToast("Exception: " + e.getMessage());
                    disconnect();
                    //selectDevice(this);
                } finally {
                    dialog.dismiss();
                }
            }
        });
        t.start();
    }


    public synchronized void disconnect() {
        if (mFMP != null) {
            mFMP.close();
        }

        if (mBtSocket != null) {
            try {
                mBtSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // return bluetooth status
    public String getStatus() {
        return this.status;
    }

    public void enableBluetooth(MainActivity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    public void on(Activity activity) {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();

        if (!BA.isEnabled()) {
            BA.enable();
            Toast.makeText(activity, "Turned on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Already on", Toast.LENGTH_SHORT).show();
        }

    }

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

    public static boolean isBluetoothAvailable() {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        if (BA == null) {
            Toast.makeText(new Activity(), R.string.msg_bluetooth_is_not_supported, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
