package com.example.han.referralproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.example.han.referralproject.activity.BaseActivity;
import com.example.han.referralproject.activity.DetectActivity;
import com.example.han.referralproject.activity.SelectXuetangTimeActivity;
import com.example.han.referralproject.measure.BloodpressureMeasureActivity;
import com.example.han.referralproject.measure.BreathHomeActivity;
import com.example.han.referralproject.measure.TemperatureMeasureActivity;
import com.example.han.referralproject.measure.WeightMeasureActivity;
import com.example.han.referralproject.measure.ecg.ECGCompatActivity;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Test_mainActivity extends BaseActivity implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    @BindView(R.id.ll_xueya)
    LinearLayout llXueya;
    @BindView(R.id.ll_xueyang)
    LinearLayout llXueyang;
    @BindView(R.id.ll_tiwen)
    LinearLayout llTiwen;
    @BindView(R.id.ll_xuetang)
    LinearLayout llXuetang;
    @BindView(R.id.cl_1)
    ConstraintLayout cl1;
    @BindView(R.id.ll_xindian)
    LinearLayout llXindian;
    @BindView(R.id.ll_tizhong)
    LinearLayout llTizhong;
    @BindView(R.id.ll_san)
    LinearLayout llSan;
    @BindView(R.id.ll_more)
    LinearLayout llMore;
    @BindView(R.id.cl_2)
    ConstraintLayout cl2;
    private long lastClickTime = 0;

    private boolean isTest;

    /**
     * 返回上一页
     */
    protected void backLastActivity() {
        if (isTest) {
            backMainActivity();
        }
        finish();
    }

    /**
     * 返回到主页面
     */
    protected void backMainActivity() {
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main2);
        ButterKnife.bind(this);
        mTitleText.setText("健 康 检 测");
        mToolbar.setVisibility(View.VISIBLE);
        isTest = getIntent().getBooleanExtra("isTest", false);

        llXueya.setOnClickListener(this);
        llXueyang.setOnClickListener(this);
        llXuetang.setOnClickListener(this);
        llXindian.setOnClickListener(this);
        llTizhong.setOnClickListener(this);
        llTiwen.setOnClickListener(this);
        llSan.setOnClickListener(this);
        llMore.setOnClickListener(this);
        setEnableListeningLoop(false);

        speak(R.string.tips_test);

    }

    @Override
    public void onClick(View v) {

        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;


            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.ll_xueya:
                    startActivity(new Intent(this, BloodpressureMeasureActivity.class));
                    break;
                case R.id.ll_xueyang:
                    intent.setClass(getApplicationContext(), DetectActivity.class);
//                    intent.setClass(mContext, InstructionsActivity.class);
                    intent.putExtra("type", "xueyang");
                    startActivity(intent);
                    break;
                case R.id.ll_tiwen:
                    startActivity(new Intent(this, TemperatureMeasureActivity.class));
                    break;
                case R.id.ll_xuetang:
                    intent.setClass(getApplicationContext(), SelectXuetangTimeActivity.class);
                    intent.putExtra("type", "xuetang");
                    startActivity(intent);
                    break;
                case R.id.ll_xindian:
//                    intent.setClass(mContext, XinDianDetectActivity.class);
//                    startActivity(intent);
                    ECGCompatActivity.startActivity(this);
                    break;
                case R.id.ll_san:
                    intent.setClass(mContext, SelectXuetangTimeActivity.class);
                    intent.putExtra("type", "sanheyi");
                    startActivity(intent);
                    break;
                case R.id.ll_tizhong://体重
//                    intent.setClass(mContext, DetectActivity.class);
////                    intent.setClass(mContext, OnMeasureActivity.class);
//                    intent.putExtra("type", "tizhong");
//                    startActivity(intent);
//                    ToastUtil.showShort(this,"暂未开通");
                    startActivity(new Intent(this, WeightMeasureActivity.class));
                    break;
                case R.id.ll_more://敬请期待
//                    ToastTool.showShort("敬请期待");
                    startActivity(new Intent(this, BreathHomeActivity.class));
                    break;
            }
        }
    }
}
