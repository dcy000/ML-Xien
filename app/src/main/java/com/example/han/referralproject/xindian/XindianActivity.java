package com.example.han.referralproject.xindian;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.han.referralproject.PlayVideoActivity;
import com.example.han.referralproject.R;
import com.example.han.referralproject.bean.NDialog;
import com.example.han.referralproject.bluetooth.BluetoothLeService;
import com.example.han.referralproject.bluetooth.SampleGattAttributes;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class XindianActivity extends AppCompatActivity {

    public ImageView mImageView;

    private BluetoothAdapter mBluetoothAdapter;
    int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    private String mDeviceAddress;
    private final static String TAG = XindianActivity.class.getSimpleName();
    private BluetoothLeService mBluetoothLeService;
    public boolean threadDisable = true;
    public String str;

    NDialog dialog;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dialog.create(NDialog.CONFIRM).dismiss();
                    Toast.makeText(getApplicationContext(), "连接完成，请点击测试", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    mTextView.setText(msg.obj + "");
                    break;

            }
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private boolean mConnected = false;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;


            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                str = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                Log.e("=============", str);
              /*  Message msg = mHandler.obtainMessage();
                msg.what = 1;
                msg.obj = str;
                mHandler.sendMessage(msg);*/

                str = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                msg.obj = str;
                mHandler.sendMessage(msg);
            }


        }
    };

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));


            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // get characteristic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }

    }


    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public ImageView mImageView1;

    public TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xindian);

        mImageView1 = (ImageView) findViewById(R.id.test_2);
        mTextView = (TextView) findViewById(R.id.text_temperature);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {


            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);


            }
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (threadDisable) {
                    if (mBluetoothLeService == null && mConnected == false) {
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                    } else {
                        mHandler.sendEmptyMessage(0);
                        threadDisable = false;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }

                }
            }

        }).start();

        mImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    lastClickTime = currentTime;

                    if (mBluetoothLeService != null && mConnected == true) {
                        sendDataToBLE(DEVICE1_ON);
                        Toast.makeText(getApplicationContext(), "已经开始测试！", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "请先开启设备再进行测试", Toast.LENGTH_SHORT).show();
                    }
                }


            }

        });


        mImageView = (ImageView) findViewById(R.id.temperature_video);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlayVideoActivity.class);
                intent.putExtra("url", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getPackageName() + "/电子温度计.mp4");
                startActivity(intent);

            }
        });

        dialog = new NDialog(this);
        showNormal("设备连接中，请稍后...");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
            }
            return;
        }


    }

    public String DEVICE1_ON = "DDDDD\n";


    @Override
    protected void onResume() {
        super.onResume();


        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


    }


    void sendDataToBLE(String str) {
        Log.d(TAG, "Sending result=" + str);
        final byte[] tx = str.getBytes();
        if (mConnected) {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);

        if (mBluetoothLeService != null) {

            unbindService(mServiceConnection);

        }
        threadDisable = false;
        if (mBluetoothAdapter != null){
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        mBluetoothLeService = null;
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null) {
                        //   Log.e("===============", device.getName());

                        if ("Med_link".equals(device.getName())) {
                            mDeviceAddress = device.getAddress();
                            Intent gattServiceIntent = new Intent(getApplicationContext(), BluetoothLeService.class);
                            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        }
                    }
                }
            });
        }
    };


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    public void showNormal(String message) {
        dialog.setMessageCenter(true)
                .setMessage(message)
                .setMessageSize(25)
                .setCancleable(false)
                .setNegativeTextColor(Color.parseColor("#0000FF"))
                .setButtonCenter(false)
                .setButtonSize(25)
                .setOnConfirmListener(new NDialog.OnConfirmListener() {
                    @Override
                    public void onClick(int which) {
                        finish();
                    }
                }).create(NDialog.CONFIRM).show();
    }
}
