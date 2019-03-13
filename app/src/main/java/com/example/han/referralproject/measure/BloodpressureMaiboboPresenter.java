package com.example.han.referralproject.measure;

import android.arch.lifecycle.LifecycleObserver;
import android.bluetooth.BluetoothDevice;

import com.example.han.referralproject.R;
import com.example.han.referralproject.util.LocalShared;
import com.gcml.sdk_maibobo.BluetoothManager;
import com.gcml.sdk_maibobo.MeasurementResult;
import com.gzq.lib_core.base.Box;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

import java.util.List;

public class BloodpressureMaiboboPresenter implements LifecycleObserver {
    private final BluetoothManager bluetoothManager;
    private String name;
    private String address;
    private BloodpressureMeasureActivity activity;

    public BloodpressureMaiboboPresenter(String name, String address, BloodpressureMeasureActivity activity) {
        this.name = name;
        this.address = address;
        this.activity = activity;

        bluetoothManager = com.gcml.sdk_maibobo.BluetoothManager.getInstance(activity);
        bluetoothManager.initSDK();
        bluetoothManager.startConnect(BluetoothUtils.getRemoteDevice(address), onBTMeasureListener);
    }

    private com.gcml.sdk_maibobo.BluetoothManager.OnBTMeasureListener onBTMeasureListener = new com.gcml.sdk_maibobo.BluetoothManager.OnBTMeasureListener() {

        @Override
        public void onRunning(String running) {
            //测量过程中的压力值
            activity.updateData(running);
        }

        @Override
        public void onPower(String power) {
            //测量前获取的电量值
        }

        @Override
        public void onMeasureResult(MeasurementResult result) {
            //测量结果
            int checkShrink = result.getCheckShrink();
            int checkDiastole = result.getCheckDiastole();
            int checkHeartRate = result.getCheckHeartRate();

            activity.updateData(checkShrink + "", checkDiastole + "", checkHeartRate + "");
        }

        @Override
        public void onMeasureError() {
            //测量错误
        }

        @Override
        public void onFoundFinish(List<BluetoothDevice> deviceList) {
            //搜索结束，deviceList.size()如果为0，则没有搜索到设备
            if (deviceList.size() == 0) {
//                baseView.updateState("未搜索到设备，请重新测量");
            }
        }

        @Override
        public void onDisconnected(BluetoothDevice device) {
            activity.updateState(Box.getString(R.string.bluetooth_device_disconnected));
        }

        @Override
        public void onConnected(boolean isConnected, BluetoothDevice device) {
            //是否连接成功
            if (isConnected) {
                LocalShared.getInstance(activity).setXueyaMac(device.getAddress());
                activity.updateState(Box.getString(R.string.bluetooth_device_connected));

            } else {

            }
        }
    };
}
