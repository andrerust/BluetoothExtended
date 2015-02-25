package com.datecs.fdsamplebgr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.datecs.fdsamplebgr.R;
import com.datecs.fiscalprinter.FiscalPrinterException;
import com.datecs.fiscalprinter.bgr.FMP10BG;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;

public class MainActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_DEVICE = 2;
	
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private interface MethodInvoker {
        public void invoke() throws IOException;
    }
    
	private BluetoothAdapter mBtAdapter;
	private BluetoothSocket mBtSocket;
	private FMP10BG mFMP;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter != null) {
        	if (mBtAdapter.isEnabled()) {
        		selectDevice();
        	} else {
        		enableBluetooth();
        	}
        } else {
        	Toast.makeText(this, R.string.msg_bluetooth_is_not_supported, Toast.LENGTH_SHORT).show();
        	finish();
        	return;
        }        
        
        findViewById(R.id.btn_panic_operation).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				performPanicOperation();
			}
		});
        
        findViewById(R.id.btn_non_fiscal_receipt).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				printNonFiscalReceipt();
			}
		});
        
        findViewById(R.id.btn_fiscal_receipt).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				printFiscalReceipt();
			}
		});
        
        findViewById(R.id.btn_z_report).setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                performZReport();
            }
        });
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();    	    	
    	disconnect();    	
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_ENABLE_BT: {
				if (resultCode == RESULT_OK) {
					selectDevice();
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

	private void enableBluetooth() {
    	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);	    
    }
    
    private void selectDevice() {
    	Intent selectDevice = new Intent(this, DeviceActivity.class);
	    startActivityForResult(selectDevice, REQUEST_DEVICE);	    
    }
    
    private void postToast(final String text) {
        runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			}
		});
    }
    
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
    
    private void invokeHelper(final MethodInvoker invoker) {
    	final ProgressDialog dialog = new ProgressDialog(this);
    	dialog.setCancelable(false);
    	dialog.setCanceledOnTouchOutside(false);
    	dialog.setMessage(getString(R.string.msg_please_wait));
		dialog.setOnKeyListener(new OnKeyListener() {					
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
		    		selectDevice();
		    	} catch (Exception e) { // Critical exception
                    e.printStackTrace();
                    postToast("Exception: " + e.getMessage());
                    disconnect();           
                    selectDevice(); 
                } finally {
		    		dialog.dismiss();
		    	}
			}
		});    	
    	t.start();
    }
    
    private void performPanicOperation() {
    	invokeHelper(new MethodInvoker() {			
			@Override
			public void invoke() throws IOException {
				mFMP.checkAndResolve();								
			}
		});
    }
    
    private void printNonFiscalReceipt() {
    	invokeHelper(new MethodInvoker() {			
			@Override
			public void invoke() throws IOException {
				mFMP.command38Variant0Version0();
				mFMP.command42Variant0Version0(" ");
				mFMP.command42Variant0Version0(" SIMPLE NON FISCAL TEXT 1");
				mFMP.command42Variant0Version0(" SIMPLE NON FISCAL TEXT 2");
				mFMP.command42Variant0Version0(" SIMPLE NON FISCAL TEXT 3");
				mFMP.command42Variant0Version0(" ");
				mFMP.command39Variant0Version0();				
			}
		});
    }
    
    private void printFiscalReceipt() {
    	invokeHelper(new MethodInvoker() {		
			@Override 
			public void invoke() throws IOException {
				mFMP.command48Variant0Version0("1", "0000", "1");
                mFMP.command54Variant0Version0("    * SIMPLE FISCAL TEXT *");               
                mFMP.command49Variant2Version2("ITEM 1", "B", "0.05", "1.000");
                mFMP.command49Variant1Version2("ITEM 2", "B", "0.90", "1.000", "-0.80");
                mFMP.command49Variant0Version2("ITEM 3", "B", "0.90", "1.000", "-50.00");                
                mFMP.command54Variant0Version0("    * SIMPLE FISCAL TEXT *"); 
                mFMP.command51Variant0Version1("-10.00");              
                mFMP.command49Variant1Version2("ITEM 4", "B", "0.90", "1.000", "0.50");
                mFMP.command51Variant0Version0();
                mFMP.totalInCash();
                mFMP.closeFiscalCheck();                
			}
		});
    }
    
    private void performZReport() {
        invokeHelper(new MethodInvoker() {      
            @Override
            public void invoke() throws IOException {
                mFMP.command69Variant0Version0("0","");                              
            }
        });
    }
    
}
