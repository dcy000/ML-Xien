package com.example.han.referralproject.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.han.referralproject.R;
import com.example.han.referralproject.activity.BaseActivity;
import com.example.han.referralproject.application.MyApplication;
import com.example.han.referralproject.bean.UserInfo;
import com.example.han.referralproject.network.NetworkApi;
import com.example.han.referralproject.network.NetworkManager;
import com.medlink.danbogh.utils.FastClickUtil;
import com.medlink.danbogh.utils.T;

public class DoctorAskGuideActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 预约医生
     */
    private ImageView mDoctorYuyue;
    /**
     * 在线医生
     */
    private ImageView mDoctorZaixian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_ask_guide);

        speak(getString(R.string.doctor_choice));

        setEnableListeningLoop(false);
        mToolbar.setVisibility(View.VISIBLE);
        mTitleText.setVisibility(View.VISIBLE);
        mTitleText.setText("医生咨询");
        speak("您好，请点击选择签约医生或在线医生");
        initView();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.doctor_yuyue:
                if (FastClickUtil.isFastClick()) {
                    NetworkApi.PersonInfo(MyApplication.getInstance().userId,
                            new NetworkManager.SuccessCallback<UserInfo>() {
                                @Override
                                public void onSuccess(UserInfo userInfo) {
                                    String state = userInfo.getState();
                                    if ((TextUtils.isEmpty(state) || "0".equals(state)) && TextUtils.isEmpty(userInfo.getDoctername())) {
                                        Intent intent = new Intent(DoctorAskGuideActivity.this, OnlineDoctorListActivity.class);
                                        intent.putExtra("flag", "contract");
                                        startActivity(intent);
                                    } else if (TextUtils.isEmpty(state) || ("0".equals(state)) && !TextUtils.isEmpty(userInfo.getDoctername())) {
                                        Intent intent = new Intent(DoctorAskGuideActivity.this, CheckContractActivity.class);
                                        startActivity(intent);
                                    } else {
                                        startActivity(new Intent(DoctorAskGuideActivity.this, DoctorappoActivity2.class));
                                    }
                                }
                            }, new NetworkManager.FailedCallback() {
                                @Override
                                public void onFailed(String message) {
//                                    T.show(message);
                                }
                            });
                }
                break;
            case R.id.doctor_zaixian:
                if (FastClickUtil.isFastClick()) {
                    Log.d("============", "click");
                    startActivity(new Intent(this, OnlineDoctorListActivity.class));
                }
                break;
        }
    }

    private void initView() {
        mDoctorYuyue = (ImageView) findViewById(R.id.doctor_yuyue);
        mDoctorYuyue.setOnClickListener(this);
        mDoctorZaixian = (ImageView) findViewById(R.id.doctor_zaixian);
        mDoctorZaixian.setOnClickListener(this);
    }
}
