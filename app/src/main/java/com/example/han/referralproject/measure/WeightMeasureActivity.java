package com.example.han.referralproject.measure;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.han.referralproject.R;
import com.example.han.referralproject.bean.DataInfoBean;
import com.example.han.referralproject.bean.MeasureResult;
import com.example.han.referralproject.network.NetworkApi;
import com.example.han.referralproject.network.NetworkManager;
import com.example.han.referralproject.util.LocalShared;
import com.example.han.referralproject.util.WeakHandler;
import com.gzq.lib_core.utils.ToastUtils;
import com.iflytek.synthetize.MLVoiceSynthetize;
import com.medlink.danbogh.healthdetection.HealthRecordActivity;

public class WeightMeasureActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mVideoTemperature;
    private Button mHistory;
    private Button mTemperatureVideo;
    private LinearLayout mDetectLlNav1;
    private TextView mTextTemperature1;
    private TextView mTvResult;
    private RelativeLayout mRelativeLayout1;
    private ImageView mTest2;
    private RelativeLayout mRlTemp;
    private VideoView mVvTips;
    private FrameLayout mViewOver;
    private RelativeLayout mDeviceRlTips;
    private ImageView mIvBack;
    private ImageView mIconHome;
    private boolean isMeasureFinishedOfThisTime;
    /**
     * 体重
     */
    private TextView mTestTizhong;
    /**
     * 0.0
     */
    private TextView mTvTizhong;
    private RelativeLayout mRlTi;
    /**
     * 体脂
     */
    private TextView mTestTizhi;
    /**
     * 0.0
     */
    private TextView mTvTizhi;
    private RelativeLayout mRlTi2;
    /**
     * 历史记录
     */
    private Button mHistory6;
    private RelativeLayout mRlTizhong;
    private WeightPresenter weightPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_measure);
        initView();
        weightPresenter = new WeightPresenter(this);
    }


    public void updateData(String... datas) {
        if (datas.length == 2) {
            if (mTvTizhong != null) {
                mTvTizhong.setText("0.00");
            }
            isMeasureFinishedOfThisTime = false;
        } else if (datas.length == 1) {
            if (mTvTizhong != null) {
                mTvTizhong.setText(datas[0]);
            }
        } else if (datas.length == 3) {
            float weight = Float.parseFloat(datas[2]);
            if (!isMeasureFinishedOfThisTime && weight != 0) {
                isMeasureFinishedOfThisTime = true;
                if (mTvTizhong != null) {
                    mTvTizhong.setText(datas[2]);
                }
                String height_s = LocalShared.getInstance(WeightMeasureActivity.this).getUserHeight();
                float height = TextUtils.isEmpty(height_s) ? 0 : Float.parseFloat(height_s) / 100;
                float tizhi = weight / (height * height);
                if (height != 0) {
                    mTvTizhi.setText(String.format("%1$.2f", tizhi));
                }
                MLVoiceSynthetize.startSynthesize("主人，您本次测量体重" + datas[2] + "公斤，体质" + tizhi);
                postData(weight);
            }
        }
    }

    private void postData(float weight) {
        //上传数据
        DataInfoBean info = new DataInfoBean();
        info.weight = weight;
        NetworkApi.postData(info, new NetworkManager.SuccessCallback<MeasureResult>() {
            @Override
            public void onSuccess(MeasureResult response) {
            }
        }, new NetworkManager.FailedCallback() {
            @Override
            public void onFailed(String message) {
            }
        });
    }

    public void updateState(String state) {
        ToastUtils.showShort(state);
    }

    private void initView() {
        mTestTizhong = (TextView) findViewById(R.id.test_tizhong);
        mTvTizhong = (TextView) findViewById(R.id.tv_tizhong);
        mRlTi = (RelativeLayout) findViewById(R.id.rl_ti);
        mTestTizhi = (TextView) findViewById(R.id.test_tizhi);
        mTvTizhi = (TextView) findViewById(R.id.tv_tizhi);
        mRlTi2 = (RelativeLayout) findViewById(R.id.rl_ti2);
        mHistory6 = (Button) findViewById(R.id.history6);
        mHistory6.setOnClickListener(this);
        mRlTizhong = (RelativeLayout) findViewById(R.id.rl_tizhong);
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
            case R.id.history6:
                Intent intent = new Intent(this, HealthRecordActivity.class);
                intent.putExtra("position", 8);
                startActivity(intent);
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
                                LocalShared.getInstance(WeightMeasureActivity.this).setTizhongMac("");
                                if (weightPresenter != null) {
                                    weightPresenter.onDestroy();
                                    new WeakHandler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            weightPresenter = new WeightPresenter(WeightMeasureActivity.this);
                                        }
                                    }, 1000);
                                }
                            }
                        }).show();
                break;
        }
    }
}
