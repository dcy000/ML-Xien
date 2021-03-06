package com.example.han.referralproject.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.han.referralproject.R;
import com.example.han.referralproject.adapter.MessageShowAdapter;
import com.example.han.referralproject.bean.YzInfoBean;
import com.example.han.referralproject.network.NetworkApi;
import com.example.han.referralproject.network.NetworkManager;

import java.util.ArrayList;

public class MessageActivity extends BaseActivity implements View.OnClickListener {
    private ArrayList<YzInfoBean> mDataList = new ArrayList<>();
    private MessageShowAdapter messageShowAdapter;
    private TextView tvMsg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mToolbar.setVisibility(View.VISIBLE);
        mTitleText.setText("医 生 建 议");
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_message);
        tvMsg = (TextView) findViewById(R.id.lude_tv_msg);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        messageShowAdapter = new MessageShowAdapter(mContext, mDataList);
        mRecyclerView.setAdapter(messageShowAdapter);
        NetworkApi.getYzList(successCallback);
        tvMsg.setVisibility(messageShowAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private NetworkManager.SuccessCallback successCallback = new NetworkManager.SuccessCallback<ArrayList<YzInfoBean>>() {
        @Override
        public void onSuccess(ArrayList<YzInfoBean> response) {
            if (response == null || response.size() == 0) {
                speak(R.string.no_yz);
                tvMsg.setVisibility(View.VISIBLE);
                return;
            }
            tvMsg.setVisibility(View.GONE);
            mDataList.addAll(response);
            messageShowAdapter.notifyDataSetChanged();
            speak(response.get(0).yz);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
