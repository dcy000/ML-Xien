package com.example.han.referralproject.measure;

import android.arch.lifecycle.LifecycleOwner;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.example.han.referralproject.util.LocalShared;
import com.gzq.lib_bluetooth.BaseBluetooth;
import com.gzq.lib_bluetooth.BluetoothStore;
import com.gzq.lib_bluetooth.BluetoothType;
import com.iflytek.synthetize.MLVoiceSynthetize;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

import java.util.List;
import java.util.UUID;

public class BloodpressurePresenter extends BaseBluetooth {
    /**
     * 超思
     */
    private String CHAOSI_SERVICE;
    private static final String CHAOSI_NOTIFY = "0000cd04-0000-1000-8000-00805f9b34fb";
    private static final String CHAOSI_WRITE = "0000cd20-0000-1000-8000-00805f9b34fb";
    private static final String CHAOSI_NOTIFY1 = "0000cd01-0000-1000-8000-00805f9b34fb";
    private static final String CHAOSI_NOTIFY2 = "0000cd02-0000-1000-8000-00805f9b34fb";
    private static final String CHAOSI_NOTIFY3 = "0000cd03-0000-1000-8000-00805f9b34fb";
    private static final byte[] CHAOSI_PASSWORD = {(byte) 0xAA, 0x55, 0x04, (byte) 0xB1, 0x00, 0x00, (byte) 0xB5};//密码校验指令

    /**
     * 自家
     */
    private static final String SELF_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    private static final String SELF_NOTIFY = "0000fff4-0000-1000-8000-00805f9b34fb";
    private boolean isGetResult = false;

    /**
     * 西恩4.0
     */
    private static final String XIEN4_SERVICE = "01000000-0000-0000-0000-000000000080";
    private static final String XIEN4_WRITE = "05000000-0000-0000-0000-000000000080";
    private static final String XIEN4_NOTIFY = "02000000-0000-0000-0000-000000000080";
    private byte[] XIEN4_COMMOND = {(byte) 0xff, (byte) 0xff, 0x05, 0x01, (byte) 0xfa};
    /**
     * 鱼跃
     */
    private static final String YUYUE_SERVICE = "00001810-0000-1000-8000-00805f9b34fb";
    private static final String YUYUE_INDICATE = "00002a35-0000-1000-8000-00805f9b34fb";
    private static final String YUYUE_NOTIFY = "00002a36-0000-1000-8000-00805f9b34fb";

    private BloodpressureMeasureActivity context;
    private BloodpressureXien2Presenter xien2Presenter;
    private String targetAddress;

    public BloodpressurePresenter(LifecycleOwner owner) {
        super(owner);
        context = ((BloodpressureMeasureActivity) owner);
        startDiscover();
    }

    public void startDiscover() {
        String xueyaMac = LocalShared.getInstance(context).getXueyaMac();
        start(BluetoothType.BLUETOOTH_TYPE_BLE, xueyaMac, "eBlood-Pressure", "LD", "Dual-SPP", "Yuwell");
    }


    @Override
    protected void noneFind() {
        String xueyaMac = LocalShared.getInstance(context).getXueyaMac();
        start(BluetoothType.BLUETOOTH_TYPE_CLASSIC, xueyaMac, "Dual-SPP");
    }

    @Override
    protected boolean isSelfConnect(String name, String address) {
        if (name.startsWith("Dual")) {
            xien2Presenter = new BloodpressureXien2Presenter(context, context, "Dual-SPP", address);
            return true;
        }
        return super.isSelfConnect(name, address);
    }

    @Override
    protected void connectSuccessed(String address) {
        targetAddress = address;
        LocalShared.getInstance(context).setXueyaMac(address);
        MLVoiceSynthetize.startSynthesize("设备已连接", false);
        List<BluetoothDevice> connectedBluetoothLeDevices = BluetoothUtils.getConnectedBluetoothLeDevices();
        if (connectedBluetoothLeDevices != null && connectedBluetoothLeDevices.size() > 0) {
            BluetoothDevice bluetoothDevice = connectedBluetoothLeDevices.get(0);
            if (bluetoothDevice != null) {
                String name = bluetoothDevice.getName();
                if (!TextUtils.isEmpty(name)) {
                    if (name.startsWith("eBlood")) {
                        handleSelf(address);
                        return;
                    }
                    if (name.startsWith("LD")) {
                        handleXien4(address);
                        return;
                    }

                    if (name.startsWith("Yuwell")) {
                        handleYuyue(address);
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void connectFailed() {

    }

    @Override
    protected void disConnected() {

    }

    private void handleSelf(String address) {
        BluetoothStore.getClient().notify(address,
                UUID.fromString(SELF_SERVICE),
                UUID.fromString(SELF_NOTIFY), new BleNotifyResponse() {
                    @Override
                    public void onNotify(UUID uuid, UUID uuid1, byte[] bytes) {
                        int length = bytes.length;
                        switch (length) {
                            case 2:
                                isGetResult = false;
                                context.updateData((bytes[1] & 0xff) + "");
                                break;
                            case 12:
                                if (!isGetResult) {
                                    isGetResult = true;
                                    context.updateData((bytes[2] & 0xff) + "", (bytes[4] & 0xff) + "", (bytes[8] & 0xff) + "");
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onResponse(int i) {

                    }
                });
    }

    private void handleXien4(String address) {
        BluetoothStore.getClient().notify(address,
                UUID.fromString(XIEN4_SERVICE),
                UUID.fromString(XIEN4_NOTIFY), new BleNotifyResponse() {
                    @Override
                    public void onNotify(UUID uuid, UUID uuid1, byte[] bytes) {
                        if (bytes.length == 11 && (bytes[3] & 0xff) == 10 && (bytes[4] & 0xff) == 2) {
                            int hight = (bytes[6] & 0xff) | (bytes[7] << 8 & 0xff00);
                            context.updateData(String.valueOf(hight));
                        } else if (bytes.length == 9 && (bytes[3] & 0xff) == 73 && (bytes[4] & 0xff) == 3) {
                            int highPress = (bytes[6] & 0xff) + 30;
                            int lowPress = (bytes[7] & 0xff) + 30;
                            int pulse = bytes[5] & 0xff;

                            context.updateData(highPress + "", lowPress + "", pulse + "");
                        }
                    }

                    @Override
                    public void onResponse(int i) {
                    }
                });
    }

    private void handleYuyue(String address) {
        BluetoothStore.getClient().notify(address, UUID.fromString(YUYUE_SERVICE),
                UUID.fromString(YUYUE_NOTIFY), new BleNotifyResponse() {
                    @Override
                    public void onNotify(UUID service, UUID character, byte[] value) {
                        if (value.length == 19) {
                            context.updateData((value[1] & 0xff) + "");
                        }
                    }

                    @Override
                    public void onResponse(int code) {

                    }
                });
        BluetoothStore.getClient().indicate(address, UUID.fromString(YUYUE_SERVICE),
                UUID.fromString(YUYUE_INDICATE),
                new BleNotifyResponse() {
                    @Override
                    public void onNotify(UUID service, UUID character, byte[] value) {
                        if (value.length == 19) {
                            context.updateData((value[1] & 0xff) + "", (value[3] & 0xff) + "", (value[14] & 0xff) + "");
                        }
                    }

                    @Override
                    public void onResponse(int code) {

                    }
                });
    }

    /**
     * 西恩手动开始测量
     */
    public void startXienMeasure() {
        if (xien2Presenter != null) {
            xien2Presenter.start();
            return;
        }
        if (!isConnected) {
            context.updateState("请先连接设备");
            return;
        }
        BluetoothStore.getClient().write(targetAddress,
                UUID.fromString(XIEN4_SERVICE),
                UUID.fromString(XIEN4_WRITE), XIEN4_COMMOND, new BleWriteResponse() {
                    @Override
                    public void onResponse(int code) {
                    }
                });
    }
}
