package com.example.han.referralproject.measure;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v4.app.SupportActivity;

import com.example.han.referralproject.R;
import com.example.han.referralproject.util.LocalShared;
import com.gzq.lib_core.base.Box;

import java.math.BigDecimal;

import senssun.blelib.device.scale.cloudblelib.BleCloudProtocolUtils;
import senssun.blelib.scan.BleScan;

public class WeightXiangshanPresenter implements LifecycleObserver {
    private SupportActivity activity;
    private WeightMeasureActivity baseView;
    private String name;
    private String address;
    private final BleScan bleScan;
    private final BleCloudProtocolUtils bleCloudProtocolUtils;

    @SuppressLint("RestrictedApi")
    public WeightXiangshanPresenter(SupportActivity activity, WeightMeasureActivity baseView, String name, String address) {
        this.activity = activity;
        this.baseView = baseView;
        this.name = name;
        this.address = address;
        this.activity.getLifecycle().addObserver(this);
        bleCloudProtocolUtils = BleCloudProtocolUtils.getInstance(activity);
        bleScan = new BleScan();
        bleScan.Create(activity);
        connect();
    }

    private void connect() {
        bleCloudProtocolUtils.setOnConnectState(new BleCloudProtocolUtils.OnConnectState() {
            @Override
            public void OnState(boolean b) {
                if (b) {
                    baseView.updateState(Box.getString(R.string.bluetooth_device_connected));
                    baseView.updateData("initialization", "0.00");
                    LocalShared.getInstance(activity).setTizhongMac(address);
                } else {
                    baseView.updateState(Box.getString(R.string.bluetooth_device_disconnected));
                }
            }
        });


        bleCloudProtocolUtils.setOnDisplayDATA(new BleCloudProtocolUtils.OnDisplayDATA() {
            @Override
            public void OnDATA(String data) {
                String[] strdata = data.split("-");
                switch (strdata[5]) {
                    case "03":
                        switch (strdata[6]) {
                            case "80":
                                if (baseView == null) {
                                    return;
                                }
                                baseView.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (strdata[12].equals("A0")) {
                                            String tmpNum = strdata[7] + strdata[8];
                                            baseView.updateData(String.format("%.2f", Integer.valueOf(tmpNum, 16) / 10f));
                                        } else {
                                            String tmpNum = strdata[7] + strdata[8];
                                            baseView.updateData("result", "result", String.format("%.2f", Integer.valueOf(tmpNum, 16) / 10f));
                                        }
                                    }
                                });
                                break;
                            case "82":
                                switch (strdata[7]) {
                                    case "00": {
                                        float eigenvalue = new BigDecimal(Integer.valueOf(strdata[14] + strdata[15], 16)).floatValue();
                                        float weight = new BigDecimal(Integer.valueOf(strdata[10] + strdata[11], 16) / 10f).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                                        //TODO:请求香山的接口 获取更加详细的健康分析数据
                                    }
                                    break;
                                }

                        }
                        break;
                }
            }
        });
        bleCloudProtocolUtils.Connect(address);
    }

    @SuppressLint("RestrictedApi")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (activity != null) {
            activity.getLifecycle().removeObserver(this);
        }
        activity = null;
    }

}
