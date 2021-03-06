package com.example.han.referralproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.han.referralproject.R;
import com.example.han.referralproject.WelcomeActivity;
import com.example.han.referralproject.bean.HealthInfo;
import com.example.han.referralproject.imageview.CircleImageView;
import com.example.han.referralproject.network.NetworkApi;
import com.example.han.referralproject.network.NetworkManager;
import com.example.han.referralproject.util.LocalShared;
import com.gzq.lib_core.bean.UserInfoBean;
import com.medlink.danbogh.register.AuthChangeBloodTypeActivity;
import com.medlink.danbogh.utils.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by gzq on 2017/11/24.
 */

public class MyBaseDataActivity extends BaseActivity implements View.OnClickListener {
    private CircleImageView mHead;
    /**
     * 曹建平
     */
    private TextView mName;
    /**
     * 67
     */
    private TextView mAge;
    /**
     * 男
     */
    private TextView mSex;
    /**
     * 175cm
     */
    private TextView mHeight;
    /**
     * 60Kg
     */
    private TextView mWeight;
    /**
     * O
     */
    private TextView mBlood;
    /**
     * 15181438908
     */
    private TextView mPhone;
    /**
     * 222222*********222222
     */
    private TextView mIdcard;
    /**
     * 100002
     */
    private TextView mNumber;
    /**
     * 每周一次
     */
    private TextView mMotion;
    /**
     * 每天吸烟
     */
    private TextView mSmoke;
    /**
     * 荤素搭配
     */
    private TextView mEating;
    private TextView mDrinking;
    private LinearLayout llHeight, llWeight, llExercise, llSmoke, llEating, llDrinking;
    private UserInfoBean response;
    private static String TAG = "MyBaseDataActivity";
    private LinearLayout mLlHeight;
    private LinearLayout mLlWeight;
    private LinearLayout mLlBlood;
    private TextView mAddress;
    private TextView mHistory;
    private LinearLayout mLlHistory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybasedata);
        mToolbar.setVisibility(View.VISIBLE);
        mTitleText.setText("健康档案");
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        NetworkApi.getMyBaseData(new NetworkManager.SuccessCallback<UserInfoBean>() {
            @Override
            public void onSuccess(UserInfoBean response) {
                Log.e(TAG, response.toString());
                MyBaseDataActivity.this.response = response;
                Picasso.with(MyBaseDataActivity.this)
                        .load(response.userPhoto)
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.avatar_placeholder)
                        .tag(this)
                        .fit()
                        .into(mHead);
                mName.setText(response.bname);

                mAge.setText(Utils.age(response.sfz)-1 + "岁");
                mSex.setText(response.sex);
                mHeight.setText(TextUtils.isEmpty(response.height) ? "暂未填写" : response.height + "cm");
                mWeight.setText(TextUtils.isEmpty(response.weight) ? "暂未填写" : response.weight + "Kg");
                mBlood.setText(TextUtils.isEmpty(response.bloodType) ? "暂未填写" : response.bloodType + "型");
                mPhone.setText(response.tel);
                mNumber.setText(response.eqid);

                String sports = HealthInfo.SPORTS_MAP.get(response.exerciseHabits);
                mMotion.setText(TextUtils.isEmpty(sports) ? "暂未填写" : sports);
                String smoke = HealthInfo.SMOKE_MAP.get(response.smoke);
                mSmoke.setText(TextUtils.isEmpty(smoke) ? "暂未填写" : smoke);
                String eat = HealthInfo.EAT_MAP.get(response.eatingHabits);
                mEating.setText(TextUtils.isEmpty(eat) ? "暂未填写" : eat);
                String drink = HealthInfo.DRINK_MAP.get(response.drink);
                mDrinking.setText(TextUtils.isEmpty(drink) ? "暂未填写" : drink);
                String deseaseHistory = HealthInfo.getDeseaseHistory(response.mh);
                mHistory.setText(TextUtils.isEmpty(deseaseHistory)
                        ? "无" : deseaseHistory.replaceAll(",", "/"));

                mAddress.setText(response.dz);
                String shenfen = response.sfz.substring(0, 5) + "********" + response.sfz.substring(response.sfz.length() - 5, response.sfz.length());
                mIdcard.setText(shenfen);
            }
        }, new NetworkManager.FailedCallback() {
            @Override
            public void onFailed(String message) {
//                ToastTool.showShort(message);
            }
        });
    }

    private void initView() {
        mHead = (CircleImageView) findViewById(R.id.head);
        mName = (TextView) findViewById(R.id.name);
        mAge = (TextView) findViewById(R.id.age);
        mSex = (TextView) findViewById(R.id.sex);
        mHeight = (TextView) findViewById(R.id.height);
        mWeight = (TextView) findViewById(R.id.weight);
        mBlood = (TextView) findViewById(R.id.blood);
        mPhone = (TextView) findViewById(R.id.phone);
        mIdcard = (TextView) findViewById(R.id.idcard);
        mNumber = (TextView) findViewById(R.id.number);
        mMotion = (TextView) findViewById(R.id.motion);
        mSmoke = (TextView) findViewById(R.id.smoke);
        mEating = (TextView) findViewById(R.id.eating);
        mDrinking = (TextView) findViewById(R.id.drinking);
        llHeight = (LinearLayout) findViewById(R.id.ll_height);
        llHeight.setOnClickListener(this);
        llWeight = (LinearLayout) findViewById(R.id.ll_weight);
        llWeight.setOnClickListener(this);
        llExercise = (LinearLayout) findViewById(R.id.ll_exercise);
        llExercise.setOnClickListener(this);
        llSmoke = (LinearLayout) findViewById(R.id.ll_smoke);
        llEating = (LinearLayout) findViewById(R.id.ll_eating);
        llDrinking = (LinearLayout) findViewById(R.id.ll_drinking);

        llSmoke.setOnClickListener(this);
        llEating.setOnClickListener(this);
        llDrinking.setOnClickListener(this);

        mHead.setOnClickListener(this);
        mName.setOnClickListener(this);
        mAge.setOnClickListener(this);
        mSex.setOnClickListener(this);
        mBlood.setOnClickListener(this);
        mHeight.setOnClickListener(this);
        mLlHeight = (LinearLayout) findViewById(R.id.ll_height);
        mWeight.setOnClickListener(this);
        mLlWeight = (LinearLayout) findViewById(R.id.ll_weight);
        mLlBlood = (LinearLayout) findViewById(R.id.ll_blood);
        mLlBlood.setOnClickListener(this);
        mPhone.setOnClickListener(this);
        mIdcard.setOnClickListener(this);
        mNumber.setOnClickListener(this);
        mAddress = (TextView) findViewById(R.id.address);
        mMotion.setOnClickListener(this);
        mSmoke.setOnClickListener(this);
        mEating.setOnClickListener(this);
        mDrinking = (TextView) findViewById(R.id.drinking);
        mDrinking.setOnClickListener(this);
        mHistory = (TextView) findViewById(R.id.history);
        mLlHistory = (LinearLayout) findViewById(R.id.ll_history);
        mLlHistory.setOnClickListener(this);
        mAddress.setOnClickListener(this);
        findViewById(R.id.tv_reset).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.icon_back:
                finish();
                break;
            case R.id.ll_height:
                startActivity(new Intent(this, AlertHeightActivity.class).putExtra("data", response));
                break;
            case R.id.ll_weight:
                startActivity(new Intent(this, AlertWeightActivity.class).putExtra("data", response));
                break;
            case R.id.ll_exercise:
                startActivity(new Intent(this, AlertSportActivity.class).putExtra("data", response));
                break;
            case R.id.ll_smoke:
                startActivity(new Intent(this, AlertSmokeActivity.class).putExtra("data", response));
                break;
            case R.id.ll_eating:
                startActivity(new Intent(this, AlertEatingActivity.class).putExtra("data", response));
                break;
            case R.id.ll_drinking:
                startActivity(new Intent(this, AlertDrinkingActivity.class).putExtra("data", response));
                break;
            case R.id.ll_history:
                startActivity(new Intent(this, AlertMHActivity.class).putExtra("data", response));
                break;
            case R.id.address:
                startActivity(new Intent(this, AlertAddressActivity.class).putExtra("data", response));
                break;
            case R.id.ll_blood:
                startActivity(new Intent(this, AuthChangeBloodTypeActivity.class).putExtra("data", response));
                break;
            case R.id.tv_reset:
                LocalShared.getInstance(mContext).reset();
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }
}
