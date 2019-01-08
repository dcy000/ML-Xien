package com.example.han.referralproject.measure.ecg;

import com.example.han.referralproject.bean.DataInfoBean;
import com.example.han.referralproject.bean.MeasureResult;
import com.example.han.referralproject.network.NetworkApi;
import com.example.han.referralproject.network.NetworkManager;
import com.gcml.lib_ecg.SelfECGDetectionFragment;

public class SelfFragment extends SelfECGDetectionFragment {
    @Override
    protected void uploadEcg(int ecg, int heartRate) {
        super.uploadEcg(ecg, heartRate);
//        ArrayList<DetectionData> datas = new ArrayList<>();
//        DetectionData ecgData = new DetectionData();
//        //detectionType (string, optional): 检测数据类型 0血压 1血糖 2心电 3体重 4体温 6血氧 7胆固醇 8血尿酸 9脉搏 ,
//        ecgData.setDetectionType("2");
//        ecgData.setEcg(ecg + "");
//        ecgData.setResult(Box.getStrings(R.array.ecg_measureres)[ecg]);
//        ecgData.setHeartRate(heartRate);
//        datas.add(ecgData);
//        String userId = ((UserInfoBean) Box.getSessionManager().getUser()).bid;
//        Box.getRetrofit(API.class)
//                .postMeasureData(userId, datas)
//                .compose(RxUtils.httpResponseTransformer())
//                .as(RxUtils.autoDisposeConverter(this))
//                .subscribe(new CommonObserver<List<DetectionResult>>() {
//                    @Override
//                    public void onNext(List<DetectionResult> detectionResults) {
//                        ToastUtils.showShort("上传数据成功");
//                    }
//
//                    @Override
//                    protected void onError(ApiException ex) {
//                        super.onError(ex);
//                        ToastUtils.showShort(ex.message);
//                    }
//                });

        DataInfoBean ecgInfo = new DataInfoBean();
        ecgInfo.ecg = ecg;
        ecgInfo.heart_rate =heartRate;
        NetworkApi.postData(ecgInfo, new NetworkManager.SuccessCallback<MeasureResult>() {
            @Override
            public void onSuccess(MeasureResult response) {
                //Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
            }
        }, new NetworkManager.FailedCallback() {
            @Override
            public void onFailed(String message) {

            }
        });
    }
}
