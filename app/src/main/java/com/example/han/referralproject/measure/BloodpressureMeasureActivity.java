package com.example.han.referralproject.measure;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.han.referralproject.R;
import com.example.han.referralproject.util.LocalShared;
import com.example.han.referralproject.util.WeakHandler;
import com.gzq.lib_core.utils.ToastUtils;
import com.gzq.lib_core.utils.UiUtils;
import com.iflytek.synthetize.MLVoiceSynthetize;
import com.medlink.danbogh.healthdetection.HealthRecordActivity;
import com.medlink.danbogh.widget.BloodPressureSurfaceView;

public class BloodpressureMeasureActivity extends AppCompatActivity implements View.OnClickListener {
    private BloodPressureSurfaceView mPressureSvValue;
    private View mPressureBgValue1;
    private TextView mHighPressure1;
    private TextView mHighPressureUnit1;
    private TextView mHighPressure;
    private TextView mLowPressure1;
    private TextView mLowPressureUnit1;
    private TextView mLowPressure;
    private TextView mPulse1;
    private TextView mPulseUnit1;
    private TextView mPulse;
    private ImageView mPressureIvHighLow;
    private ImageView mPressureIvHighToLow;
    private ImageView mPressureIvHighNormal;
    private ImageView mPressureIvHighToHigh;
    private ImageView mPressureIvHighHigh;
    private ImageView mPressureIvLowLow;
    private ImageView mPressureIvLowToLow;
    private ImageView mPressureIvLowNormal;
    private ImageView mPressureIvLowToHigh;
    private ImageView mPressureIvLowHigh;
    private TextView mPressureTvHighLow;
    private TextView mPressureTvHighToLow;
    private TextView mPressureTvHighNormal;
    private TextView mPressureTvHighToHigh;
    private TextView mPressureTvHighHigh;
    private TextView mPressureTvLowLow;
    private TextView mPressureTvLowToLow;
    private TextView mPressureTvLowNormal;
    private TextView mPressureTvLowToHigh;
    private TextView mPressureTvLowHigh;
    private ImageView mPressureIvHighIndicator;
    private ImageView mPressureIvLowIndicator;
    private Button mHistory1;
    private Button mXueyaVideo;
    private TextView mTvTitle;
    private ConstraintLayout mDeviceClPressure;
    private VideoView mVvTips;
    private FrameLayout mViewOver;
    private RelativeLayout mDeviceRlTips;
    private ImageView mIvBack;
    private ImageView mIconHome;
    private boolean isMeasureFinishedOfThisTime;
    private int highId;
    private int lowId;
    private ConstraintSet highSet;
    private ConstraintSet lowSet;
    private ConstraintLayout clPressure;
    private BloodpressurePresenter bloodpressurePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodpressure_measure);
        initView();
        playVideo();
        bloodpressurePresenter = new BloodpressurePresenter(this);
    }

    private void playVideo() {
        mVvTips.setVisibility(View.VISIBLE);
        mViewOver.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tips_xueya);
        mVvTips.setZOrderOnTop(true);
        mVvTips.setZOrderMediaOverlay(true);
        mVvTips.setVideoURI(uri);
        mVvTips.setOnCompletionListener(mCompletionListener);
        mVvTips.start();
    }

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mVvTips.setVisibility(View.GONE);
            mViewOver.setVisibility(View.GONE);
        }
    };

    private void initView() {
        mPressureSvValue = (BloodPressureSurfaceView) findViewById(R.id.pressure_sv_value);
        mPressureBgValue1 = (View) findViewById(R.id.pressure_bg_value_1);
        mHighPressure1 = (TextView) findViewById(R.id.high_pressure1);
        mHighPressureUnit1 = (TextView) findViewById(R.id.high_pressure_unit_1);
        mHighPressure = (TextView) findViewById(R.id.high_pressure);
        mLowPressure1 = (TextView) findViewById(R.id.low_pressure1);
        mLowPressureUnit1 = (TextView) findViewById(R.id.low_pressure_unit_1);
        mLowPressure = (TextView) findViewById(R.id.low_pressure);
        mPulse1 = (TextView) findViewById(R.id.pulse1);
        mPulseUnit1 = (TextView) findViewById(R.id.pulse_unit_1);
        mPulse = (TextView) findViewById(R.id.pulse);
        mPressureIvHighLow = (ImageView) findViewById(R.id.pressure_iv_high_low);
        mPressureIvHighToLow = (ImageView) findViewById(R.id.pressure_iv_high_to_low);
        mPressureIvHighNormal = (ImageView) findViewById(R.id.pressure_iv_high_normal);
        mPressureIvHighToHigh = (ImageView) findViewById(R.id.pressure_iv_high_to_high);
        mPressureIvHighHigh = (ImageView) findViewById(R.id.pressure_iv_high_high);
        mPressureIvLowLow = (ImageView) findViewById(R.id.pressure_iv_low_low);
        mPressureIvLowToLow = (ImageView) findViewById(R.id.pressure_iv_low_to_low);
        mPressureIvLowNormal = (ImageView) findViewById(R.id.pressure_iv_low_normal);
        mPressureIvLowToHigh = (ImageView) findViewById(R.id.pressure_iv_low_to_high);
        mPressureIvLowHigh = (ImageView) findViewById(R.id.pressure_iv_low_high);
        mPressureTvHighLow = (TextView) findViewById(R.id.pressure_tv_high_low);
        mPressureTvHighToLow = (TextView) findViewById(R.id.pressure_tv_high_to_low);
        mPressureTvHighNormal = (TextView) findViewById(R.id.pressure_tv_high_normal);
        mPressureTvHighToHigh = (TextView) findViewById(R.id.pressure_tv_high_to_high);
        mPressureTvHighHigh = (TextView) findViewById(R.id.pressure_tv_high_high);
        mPressureTvLowLow = (TextView) findViewById(R.id.pressure_tv_low_low);
        mPressureTvLowToLow = (TextView) findViewById(R.id.pressure_tv_low_to_low);
        mPressureTvLowNormal = (TextView) findViewById(R.id.pressure_tv_low_normal);
        mPressureTvLowToHigh = (TextView) findViewById(R.id.pressure_tv_low_to_high);
        mPressureTvLowHigh = (TextView) findViewById(R.id.pressure_tv_low_high);
        mPressureIvHighIndicator = (ImageView) findViewById(R.id.pressure_iv_high_indicator);
        mPressureIvLowIndicator = (ImageView) findViewById(R.id.pressure_iv_low_indicator);
        mHistory1 = (Button) findViewById(R.id.history1);
        mHistory1.setOnClickListener(this);
        mXueyaVideo = (Button) findViewById(R.id.xueya_video);
        mXueyaVideo.setOnClickListener(this);
        mTvTitle = (TextView) findViewById(R.id.tvTitle);
        mDeviceClPressure = (ConstraintLayout) findViewById(R.id.device_cl_pressure);
        mVvTips = (VideoView) findViewById(R.id.vv_tips);
        mViewOver = (FrameLayout) findViewById(R.id.view_over);
        mViewOver.setOnClickListener(this);
        mDeviceRlTips = (RelativeLayout) findViewById(R.id.device_rl_tips);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mIconHome = (ImageView) findViewById(R.id.icon_home);
        mIconHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.view_over:
                mVvTips.setVisibility(View.GONE);
                mViewOver.setVisibility(View.GONE);
                if (mVvTips.isPlaying()) {
                    mVvTips.pause();
                }
                break;
            case R.id.history1:
                Intent intent = new Intent(this, HealthRecordActivity.class);
                intent.putExtra("position", 1);
                startActivity(intent);
                break;
            case R.id.xueya_video:
                bloodpressurePresenter.startXienMeasure();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.icon_home:
                new AlertDialog.Builder(this)
                        .setMessage("是否匹配新设备")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LocalShared.getInstance(BloodpressureMeasureActivity.this).setXueyaMac("");
                                if (bloodpressurePresenter != null) {
                                    bloodpressurePresenter.onDestroy();
                                    new WeakHandler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bloodpressurePresenter = new BloodpressurePresenter(BloodpressureMeasureActivity.this);
                                        }
                                    }, 1000);
                                }
                            }
                        }).show();
                break;
        }
    }

    public void updateData(String... datas) {
        if (datas.length == 1) {
            float value = Float.parseFloat(datas[0]);
            applyIndicatorAnimation(true, (int) value);
            mPressureSvValue.setTargetValue(value);
            mHighPressure.setText(datas[0]);
            mLowPressure.setText("0");
            mPulse.setText("0");
            isMeasureFinishedOfThisTime = false;
        } else if (datas.length == 3) {
            float h = Float.parseFloat(datas[0]);
            float l = Float.parseFloat(datas[1]);
            applyIndicatorAnimation(true, (int) h);
            applyIndicatorAnimation(false, (int) l);
            mPressureSvValue.setTargetValue(h);
            mHighPressure.setText(datas[0]);
            mLowPressure.setText(datas[1]);
            mPulse.setText(datas[2]);
            if (!isMeasureFinishedOfThisTime && Float.parseFloat(datas[0]) != 0) {
                isMeasureFinishedOfThisTime = true;
            }
            MLVoiceSynthetize.startSynthesize("主人，您本次测量收缩压" + datas[0] + ",舒张压" + datas[1] + ",脉搏" + datas[2]);
        } else {
        }

    }

    private void applyIndicatorAnimation(boolean high, int value) {
        int targetId;
        int descId;
        ConstraintSet constraintSet;
        if (high) {
            targetId = R.id.pressure_iv_high_indicator;
            if (value < 90) {
                descId = R.id.pressure_iv_high_low;
            } else if (value < 95) {
                descId = R.id.pressure_iv_high_to_low;
            } else if (value < 135) {
                descId = R.id.pressure_iv_high_normal;
            } else if (value < 140) {
                descId = R.id.pressure_iv_high_to_high;
            } else {
                descId = R.id.pressure_iv_high_high;
            }
            if (descId == highId) {
                return;
            }
            highId = descId;
            if (highSet == null) {
                highSet = new ConstraintSet();
            }
            constraintSet = highSet;
        } else {
            targetId = R.id.pressure_iv_low_indicator;
            if (value < 60) {
                descId = R.id.pressure_iv_low_low;
            } else if (value < 65) {
                descId = R.id.pressure_iv_low_to_low;
            } else if (value < 85) {
                descId = R.id.pressure_iv_low_normal;
            } else if (value < 90) {
                descId = R.id.pressure_iv_low_to_high;
            } else {
                descId = R.id.pressure_iv_low_high;
            }
            if (descId == lowId) {
                return;
            }
            lowId = descId;
            if (lowSet == null) {
                lowSet = new ConstraintSet();
            }
            constraintSet = lowSet;
        }
        if (clPressure == null) {
            return;
        }
        constraintSet.clone(clPressure);
        constraintSet.connect(targetId, ConstraintSet.START, descId, ConstraintSet.START);
        constraintSet.connect(targetId, ConstraintSet.END, descId, ConstraintSet.END);
        constraintSet.connect(targetId, ConstraintSet.BOTTOM, descId, ConstraintSet.TOP, UiUtils.pt(16));
        constraintSet.applyTo(clPressure);
    }

    public void updateState(String state) {
        ToastUtils.showShort(state);
    }
}
