package com.example.han.referralproject.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.han.referralproject.MainActivity;
import com.example.han.referralproject.R;
import com.example.han.referralproject.adapter.WifiConnectRecyclerAdapter;
import com.example.han.referralproject.application.MyApplication;
import com.example.han.referralproject.util.LocalShared;
import com.example.han.referralproject.util.WiFiUtil;
import com.medlink.danbogh.register.lude.LudeAuthActivity;
import com.medlink.danbogh.signin.SignInActivity;
import com.medlink.danbogh.utils.T;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WifiConnectActivity extends BaseActivity implements View.OnClickListener {
    private WifiConnectRecyclerAdapter mConnectAdapter;
    private List<ScanResult> mDataList = new ArrayList<>();
    private WiFiUtil mWiFiUtil;
    private Switch mSwitch;
    private View mConnectedLayout;
    private TextView mConnectedWifiName;
    private WifiManager mWifiManager;
    private boolean isFirstWifi = false;
    private MediaPlayer mediaPlayer;
    private ImageView ivConnectedWifiIndicator;
    private CheckBox cbWifiConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connect_layout);
        mToolbar.setVisibility(View.VISIBLE);
        mRightView.setImageResource(R.drawable.icon_refresh);
        mTitleText.setText("WiFi连接");
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean iswifiConnected = cm != null
                && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
        if (iswifiConnected) {
//            mediaPlayer=MediaPlayer.create(this,R.raw.wifi_connected);
            speak("您好，您的wifi已连接,如果需要更换,请点击对应wifi名称");
        } else {
//            mediaPlayer = MediaPlayer.create(this, R.raw.wifi_connect);
            speak("您好，请连接wifi,如果未找到,请点击右上角的刷新按钮");
        }

//        mediaPlayer.start();//播放音乐

        mRightView.setOnClickListener(this);
        isFirstWifi = getIntent().getBooleanExtra("is_first_wifi", false);

        findViewById(R.id.tv_netless).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalShared instance = LocalShared.getInstance(v.getContext());
                instance.setString("netless", "1");
                Intent intent = new Intent(getApplicationContext(), LudeAuthActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mWiFiUtil = WiFiUtil.getInstance(this);
        mWiFiUtil.openWifi();
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mConnectedLayout = findViewById(R.id.rl_connected);
        mConnectedWifiName = (TextView) findViewById(R.id.tv_connect_name);
        ivConnectedWifiIndicator = (ImageView) findViewById(R.id.xien_iv_connected_wifi_indicator);
        cbWifiConnected = (CheckBox) findViewById(R.id.cb_wifi_connected);
        cbWifiConnected.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mSwitch = (Switch) findViewById(R.id.switch_wifi);
        mSwitch.setChecked(mWiFiUtil.isWifiOpened());
        mSwitch.setOnCheckedChangeListener(mCheckedChangeListener);
        RecyclerView mWifiRv = (RecyclerView) findViewById(R.id.rv_wifi);
        mWifiRv.setLayoutManager(new LinearLayoutManager(mContext));
        mConnectAdapter = new WifiConnectRecyclerAdapter(mContext, mDataList);
        mWifiRv.setAdapter(mConnectAdapter);
        scanWifi();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkReceiver, filter);
    }

    public void scanWifi() {
        mDataList.clear();
        mWiFiUtil.startScan();
        WifiInfo mInfo = mWifiManager.getConnectionInfo();
        if (mInfo != null) {
            mConnectedLayout.setVisibility(View.VISIBLE);
            ivConnectedWifiIndicator.setImageLevel(WifiManager.calculateSignalLevel(mInfo.getRssi(), 4));
            for (ScanResult itemResult : mWiFiUtil.getWifiList()) {
                if (!itemResult.BSSID.equals(mInfo.getBSSID())) {
                    mDataList.add(itemResult);
                }
            }
            if (WiFiUtil.isWiFiConnected(this)) {
                mConnectedWifiName.setText(mInfo.getSSID());
                cbWifiConnected.setChecked(true);
            } else {
                mConnectedWifiName.setText("正在连接...");
                cbWifiConnected.setChecked(false);
            }
        } else {
            mConnectedLayout.setVisibility(View.GONE);
            mDataList.addAll(mWiFiUtil.getWifiList());
        }
        Collections.sort(mDataList, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult o1, ScanResult o2) {
                if (o1.level > o2.level) {
                    return -1;
                } else if (o1.level < o2.level) {
                    return 1;
                }
                return 0;
            }
        });
        mConnectAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top_right:
                scanWifi();
                break;
            case R.id.tv_system:
                startActivity(new Intent("android.net.wifi.PICK_WIFI_NETWORK"));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkReceiver);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mWiFiUtil.openWifi();
            } else {
                mRightView.performClick();
                mWiFiUtil.closeWifi();
            }
        }
    };

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("mylog", intent.getAction());
            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    Log.e("TAG", "wifiState:" + wifiState);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            break;
                    }
                    break;
                // 监听wifi的连接状态即是否连上了一个有效无线路由
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    Parcelable parcelableExtra = intent
                            .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (null != parcelableExtra) {
                        // 获取联网状态的NetWorkInfo对象
                        NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                        //获取的State对象则代表着连接成功与否等状态
                        NetworkInfo.State state = networkInfo.getState();
                        //判断网络是否已经连接
                        boolean isConnected = state == NetworkInfo.State.CONNECTED;
                        Log.e("TAG", "isConnected:" + isConnected);
                        if (isConnected) {
                            T.show("连接成功");
                        } else {
                            T.show("连接断开");
                        }
                    }
                    break;
                // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        //Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                        if (isFirstWifi) {
                            if (TextUtils.isEmpty(MyApplication.getInstance().userId)) {
                                startActivity(new Intent(mContext, SignInActivity.class));
                            } else {
                                startActivity(new Intent(mContext, MainActivity.class));
                            }
                            finish();
                        } else {
                            scanWifi();
                        }
                    }
//                    if (info != null) {
//                        //如果当前的网络连接成功并且网络连接可用
//                        if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
//                            if (info.getType() == ConnectivityManager.TYPE_WIFI
//                                    || info.getType() == ConnectivityManager.TYPE_MOBILE) {
//                                //Log.i("TAG", getConnectionType(info.getType()) + "连上");
//                            }
//                        } else {
//                            //Log.i("TAG", getConnectionType(info.getType()) + "断开");
//                        }
//                    }
                    break;

            }
        }
    };
}
