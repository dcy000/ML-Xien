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
import com.inuker.bluetooth.library.utils.BluetoothUtils;

import java.util.List;
import java.util.UUID;

public class WeightPresenter extends BaseBluetooth {
    private static final String SELF_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";//主服务
    private static final String SELF_NOTIFY = "0000fff1-0000-1000-8000-00805f9b34fb";
    private final WeightMeasureActivity context;

    public WeightPresenter(LifecycleOwner owner) {
        super(owner);
        context = ((WeightMeasureActivity) owner);
        startDiscover();
    }

    public void startDiscover() {
        String tizhongMac = LocalShared.getInstance(context).getTizhongMac();
        start(BluetoothType.BLUETOOTH_TYPE_BLE, tizhongMac, "000FatScale01", "SHHC-60F1", "SENSSUN", "IF_B2A");
    }

    @Override
    protected boolean isSelfConnect(String name, String address) {
        if (name.startsWith("SHHC-60F1")) {
            new WeightYikePresenter(context, context, name, address);
            return true;
        }
        if (name.startsWith("SENSSUN") || name.startsWith("IF")) {
            new WeightXiangshanPresenter(context, context, name, address);
            return true;
        }
        return super.isSelfConnect(name, address);
    }

    @Override
    protected void noneFind() {

    }

    @Override
    protected void connectSuccessed(String address) {
        LocalShared.getInstance(context).setTizhongMac(address);
        MLVoiceSynthetize.startSynthesize("设备已连接", false);
        List<BluetoothDevice> connectedBluetoothLeDevices = BluetoothUtils.getConnectedBluetoothLeDevices();
        if (connectedBluetoothLeDevices != null && connectedBluetoothLeDevices.size() > 0) {
            BluetoothDevice bluetoothDevice = connectedBluetoothLeDevices.get(0);
            if (bluetoothDevice != null) {
                String name = bluetoothDevice.getName();
                if (!TextUtils.isEmpty(name)) {
                    if (name.startsWith("000FatScale01")) {
                        handleSelf(address);
                        return;
                    }
                    isSelfConnect(name, address);
                }
            }
        }

    }

    private void handleSelf(String address) {
        BluetoothStore.getClient().notify(address, UUID.fromString(SELF_SERVICE),
                UUID.fromString(SELF_NOTIFY), new BleNotifyResponse() {
                    @Override
                    public void onNotify(UUID service, UUID character, byte[] bytes) {
                        if (bytes.length == 14 && (bytes[1] & 0xff) == 221) {
                            float weight = ((float) (bytes[2] << 8) + (float) (bytes[3] & 0xff)) / 10;
                            context.updateData("result", "result", String.format("%.2f", weight));

                        }
                    }

                    @Override
                    public void onResponse(int code) {

                    }
                });
    }

    @Override
    protected void connectFailed() {

    }

    @Override
    protected void disConnected() {

    }
}
