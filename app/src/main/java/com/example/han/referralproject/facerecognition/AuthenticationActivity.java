package com.example.han.referralproject.facerecognition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.han.referralproject.MainActivity;
import com.example.han.referralproject.R;
import com.example.han.referralproject.Test_mainActivity;
import com.example.han.referralproject.activity.BaseActivity;
import com.example.han.referralproject.activity.DetectActivity;
import com.example.han.referralproject.application.MyApplication;
import com.example.han.referralproject.measure.ecg.ECGCompatActivity;
import com.example.han.referralproject.network.NetworkApi;
import com.example.han.referralproject.network.NetworkManager;
import com.example.han.referralproject.util.LocalShared;
import com.example.han.referralproject.util.ToastTool;
import com.example.han.referralproject.util.Utils;
import com.gzq.lib_core.bean.UserInfoBean;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.IdentityResult;
import com.iflytek.cloud.SpeechError;
import com.medlink.danbogh.signin.SignInActivity;
import com.medlink.danbogh.utils.Handlers;
import com.medlink.danbogh.utils.JpushAliasUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 头像身份验证
 */

public class AuthenticationActivity extends BaseActivity {
    private SurfaceView mPreviewSurface;
    private Camera mCamera;
    private int PREVIEW_WIDTH = 1280;
    private int PREVIEW_HEIGHT = 720;
    // 缩放矩阵
    private Matrix mScaleMatrix = new Matrix();
    private byte[] mImageData = null;
    private Bitmap b3;
    private String orderid;
    private String fromString;//标识从哪个页面过来的
    private String fromType;
    private Button mTiaoguo;
    private ByteArrayOutputStream baos;
    private String mAuthid;
    private MyHandler myHandler;
    private ArrayList<UserInfoBean> mDataList;
    private boolean isInclude_PassPerson = false;
    private int authenticationNum = 0;
    private TextView tvTips;
    private boolean isTest;
    private LottieAnimationView lottAnimation;
    private Animation rotateAnim;
    //    private byte[] cacheCamera;
    private static final int TO_FACE_AUTHENTICATION = 1;
    private static final int TO_CAMERA_PRE_RESOLVE = 2;
    private boolean openOrcloseAnimation = true;

    class MyHandler extends Handler {
        private WeakReference<AuthenticationActivity> weakReference;

        public MyHandler(AuthenticationActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_FACE_AUTHENTICATION://开始验证头像
                    findViewById(R.id.iv_circle).startAnimation(rotateAnim);
                    FaceAuthenticationUtils.getInstance(weakReference.get()).verificationFace(mImageData, LocalShared.getInstance(weakReference.get()).getGroupId());
                    FaceAuthenticationUtils.getInstance(weakReference.get()).setOnVertifyFaceListener(new VertifyFaceListener() {
                        @Override
                        public void onResult(IdentityResult result, boolean islast) {
                            if (null == result) {
                                myHandler.sendEmptyMessage(TO_FACE_AUTHENTICATION);
                                return;
                            }

                            try {
                                String resultStr = result.getResultString();
                                JSONObject resultJson = new JSONObject(resultStr);
                                if (ErrorCode.SUCCESS == resultJson.getInt("ret")) {//此处检验百分比
                                    JSONArray scoreList = resultJson.getJSONObject("ifv_result").getJSONArray("candidates");
                                    Logger.e(scoreList.toString());
                                    String scoreFirstXfid = scoreList.getJSONObject(0).optString("user");
                                    Logger.e("最高分数的讯飞id" + scoreFirstXfid);
                                    final double firstScore = scoreList.getJSONObject(0).optDouble("score");
                                    if (firstScore > 80) {
                                        closeAnimation();
                                        if ("Test".equals(fromString) || "Welcome".equals(fromString)) {
                                            authenticationSuccessForTest$Welcome(scoreFirstXfid, weakReference);
                                        } else if ("Pay".equals(fromString)) {
                                            if (mAuthid.equals(scoreFirstXfid)) {
                                                paySuccess();
                                            } else {
                                                payFail();
                                            }
                                        }

                                    } else {
                                        if (firstScore > 30) {
                                            authenticationNum = 0;
                                            ToastTool.showShort("请将您的面孔靠近摄像头，再试一次");
                                            myHandler.sendEmptyMessageDelayed(TO_CAMERA_PRE_RESOLVE, 1000);
                                        } else {
                                            ToastTool.showLong("匹配度" + String.format("%.2f", firstScore) + "%,验证不通过!");
                                            finishActivity();
                                        }
                                    }
                                } else {
                                    ToastTool.showShort("识别失败");
                                    finishActivity();
                                }
                            } catch (JSONException e) {
                                Logger.e(e, "验证失败");
                            }
                        }

                        @Override
                        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
                            tvTips.setText("努力验证中...");
                        }

                        @Override
                        public void onError(SpeechError error) {
                            Logger.e(error, "验证出错");
                            if (authenticationNum < 5) {
                                authenticationNum++;
                                ToastTool.showShort("第" + Utils.getChineseNumber(authenticationNum) + "次验证失败");
//                                    myHandler.sendEmptyMessage(2);
//                                    myHandler.sendEmptyMessageDelayed(1, 2000);
                                myHandler.sendEmptyMessageDelayed(TO_CAMERA_PRE_RESOLVE, 1000);
                            } else {
                                finishActivity();
                            }
                        }
                    });
                    break;
                case TO_CAMERA_PRE_RESOLVE://解析图像
                    if (mCamera != null&&!isCameraRelease) {

                        mCamera.setOneShotPreviewCallback(new PreviewCallback() {
                            @Override
                            public void onPreviewFrame(byte[] data, Camera camera) {
                                b3 = decodeToBitMap(data, mCamera);
                                if (b3 != null) {
                                    Bitmap bitmap = Utils.centerSquareScaleBitmap(b3, 300);
                                    if (bitmap != null) {
                                        if (baos != null)
                                            baos.reset();
                                        if (baos != null)
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        if (baos != null)
                                            mImageData = baos.toByteArray();
                                        myHandler.sendEmptyMessage(TO_FACE_AUTHENTICATION);
                                    }
                                }
                            }
                        });
                    }
                    break;
            }
        }
    }

    /**
     * 通过验证
     *
     * @param scoreFirstXfid
     * @param weakReference
     */
    private void authenticationSuccessForTest$Welcome(String scoreFirstXfid, WeakReference<AuthenticationActivity> weakReference) {

        ToastTool.showShort("通过验证，欢迎回来！");
        if (mDataList != null) {
            for (int i = 0; i < mDataList.size(); i++) {
                UserInfoBean user = mDataList.get(i);
                if (user.xfid.equals(scoreFirstXfid)) {
                    Logger.e("识别到的讯飞id" + user.xfid + "++++识别到的人" + user.bname);
                    new JpushAliasUtils(AuthenticationActivity.this).setAlias("user_" + user.bid);
                    LocalShared.getInstance(mContext).setUserInfo(user);
                    LocalShared.getInstance(mContext).setSex(user.sex);
                    LocalShared.getInstance(mContext).setUserPhoto(user.userPhoto);
                    LocalShared.getInstance(mContext).setUserAge(user.age);
                    LocalShared.getInstance(mContext).setUserHeight(user.height);
                    isInclude_PassPerson = true;
                    break;
                } else {
                    isInclude_PassPerson = false;
                }
            }
            if (isInclude_PassPerson) {
                if ("Welcome".equals(fromString)) {
                    startActivity(new Intent(weakReference.get(), MainActivity.class));
                } else if ("Test".equals(fromString)) {
                    Intent intent = new Intent();
                    if (TextUtils.isEmpty(fromType)) {
                        intent.setClass(weakReference.get(), Test_mainActivity.class);
                    } else if ("xindian".equals(fromType)) {
                        intent.setClass(weakReference.get(), ECGCompatActivity.class);
                    } else {
                        intent.setClass(weakReference.get(), DetectActivity.class);
                        intent.putExtra("type", fromType);
                    }
                    startActivity(intent);
                }
            } else {
                if ("Welcome".equals(fromString)) {
                    ToastTool.showLong("该机器人没有此账号的人脸认证信息，请手动登录");
                    startActivity(new Intent(weakReference.get(), SignInActivity.class));
                } else if ("Test".equals(fromString)) {
                    ToastTool.showLong("验证不通过!");
                }
            }
            finishActivity();
        }
    }

    /**
     * 支付成功
     */
    private void paySuccess() {
        NetworkApi.pay_status(MyApplication.getInstance().userId, Utils.getDeviceId(), orderid, new NetworkManager.SuccessCallback<String>() {
            @Override
            public void onSuccess(String response) {
                setResult(RESULT_OK);
                finishActivity();
            }

        }, new NetworkManager.FailedCallback() {
            @Override
            public void onFailed(String message) {

            }
        });
    }

    private void payFail() {
        NetworkApi.pay_cancel("3", "0", "1", orderid, new NetworkManager.SuccessCallback<String>() {
            @Override
            public void onSuccess(String response) {
                speak(getString(R.string.shop_yanzheng));
                ToastTool.showShort("验证不通过");
                finish();

            }

        }, new NetworkManager.FailedCallback() {
            @Override
            public void onFailed(String message) {


            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_demo);
        //工厂测试专用
        isTest = getIntent().getBooleanExtra("isTest", false);

        init();
        openCameraPreview();
        if (isTest) {
            openAnimation();
        } else {
            joinGroup();
        }
        setClick();
        getAllUsersInfo();
    }

    private void joinGroup() {
        String groupid = LocalShared.getInstance(this).getGroupId();
        String firstXfid = LocalShared.getInstance(this).getGroupFirstXfid();
        final String currentXfid = LocalShared.getInstance(this).getXunfeiId();
        FaceAuthenticationUtils.getInstance(this).joinGroup(groupid, currentXfid);
        FaceAuthenticationUtils.getInstance(AuthenticationActivity.this).setOnJoinGroupListener(new JoinGroupListener() {
            @Override
            public void onResult(IdentityResult result, boolean islast) {
                tvTips.setText("请将人脸对准识别框");
                openAnimation();
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

            }

            @Override
            public void onError(SpeechError error) {
                Logger.e(error, "添加成员出现异常");
                if (error.getErrorCode() == 10143 || error.getErrorCode() == 10106) {//该组不存在;无效的参数
                    createGroup(currentXfid);
                } else {
                    openAnimation();
                }

            }
        });
    }

    private void createGroup(final String xfid) {
        FaceAuthenticationUtils.getInstance(this).createGroup(xfid);
        FaceAuthenticationUtils.getInstance(this).setOnCreateGroupListener(new CreateGroupListener() {
            @Override
            public void onResult(IdentityResult result, boolean islast) {
                try {
                    JSONObject resObj = new JSONObject(result.getResultString());
                    String groupId = resObj.getString("group_id");
                    LocalShared.getInstance(AuthenticationActivity.this).setGroupId(groupId);
                    LocalShared.getInstance(AuthenticationActivity.this).setGroupFirstXfid(xfid);
                    joinGroup();
                    FaceAuthenticationUtils.getInstance(AuthenticationActivity.this).updateGroupInformation(groupId, xfid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            }

            @Override
            public void onError(SpeechError error) {
                Logger.e(error, "创建组失败");
            }
        });
    }

    private void openAnimation() {
        lottAnimation.playAnimation();
    }

    private void closeAnimation() {
        lottAnimation.reverseAnimation();
        findViewById(R.id.iv_circle).clearAnimation();
    }

    private void getAllUsersInfo() {
        Handlers.bg().post(new Runnable() {
            @Override
            public void run() {
                String[] accounts = LocalShared.getInstance(AuthenticationActivity.this).getAccounts();
                if (accounts == null) {
                    return;
                }
                StringBuilder mAccountIdBuilder = new StringBuilder();
                for (String item : accounts) {
                    mAccountIdBuilder.append(item.split(",")[0]).append(",");
                }
                NetworkApi.getAllUsers(mAccountIdBuilder.substring(0, mAccountIdBuilder.length() - 1),
                        new NetworkManager.SuccessCallback<ArrayList<UserInfoBean>>() {
                            @Override
                            public void onSuccess(ArrayList<UserInfoBean> response) {
                                if (response == null) {
                                    return;
                                }
                                mDataList = response;
                            }
                        });
            }
        });
    }

    private void init() {
        Intent intent = getIntent();
        orderid = intent.getStringExtra("orderid");
        fromString = intent.getStringExtra("from");
        fromType = intent.getStringExtra("fromType");
        mAuthid = LocalShared.getInstance(this).getXunfeiId();
        mTiaoguo = (Button) findViewById(R.id.tiao_guos);
        if ("Pay".equals(fromString) || "Welcome".equals(fromString)) {//支付过来
            mTiaoguo.setVisibility(View.GONE);
        }
        if ("Test".equals(fromString)) {
            mTiaoguo.setVisibility(View.VISIBLE);
        }
        mPreviewSurface = findViewById(R.id.sfv_preview);
        rotateAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_face_check);
        baos = new ByteArrayOutputStream();
        myHandler = new MyHandler(this);
        tvTips = findViewById(R.id.tv_tip);
        lottAnimation = findViewById(R.id.lott_animation);
        lottAnimation.setImageAssetsFolder("lav_imgs/");
        lottAnimation.setAnimation("camera_pre.json");
    }

    private Callback callback;

    private void openCameraPreview() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = (int) (width * PREVIEW_WIDTH / (float) PREVIEW_HEIGHT);
        LayoutParams params = new LayoutParams(width, height);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mPreviewSurface.setLayoutParams(params);

        callback = new Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Logger.e("getHolder().addCallback所在线程");
                Handlers.bg().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
                            if (mCamera == null) {
                                runOnUiThreadWithOpenCameraFail();
                                return;
                            }
                            Parameters params = mCamera.getParameters();
                            params.setPreviewFormat(ImageFormat.NV21);
                            params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
                            mCamera.setParameters(params);
                            setCameraDisplayOrientation(AuthenticationActivity.this, CameraInfo.CAMERA_FACING_BACK, mCamera);
                            mCamera.setPreviewDisplay(mPreviewSurface.getHolder());
                            mCamera.startPreview();

                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThreadWithOpenCameraFail();
                        }
                    }
                });

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mScaleMatrix.setScale(width / (float) PREVIEW_HEIGHT, height / (float) PREVIEW_WIDTH);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                finish();
            }
        };
        mPreviewSurface.getHolder().addCallback(callback);


    }

    private void runOnUiThreadWithOpenCameraFail() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastTool.showShort("启动相机失败");
                finishActivity();
            }
        });
    }

    private void setClick() {
        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        mTiaoguo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isTest) {
                    startActivity(new Intent(AuthenticationActivity.this, Test_mainActivity.class)
                            .putExtra("isTest", isTest));
                    return;
                }

                if ("Test".equals(fromString)) {
                    Intent intent = new Intent();
                    if (TextUtils.isEmpty(fromType)) {
                        intent.setClass(AuthenticationActivity.this, Test_mainActivity.class);
                    } else if ("xindian".equals(fromType)) {
                        intent.setClass(AuthenticationActivity.this, ECGCompatActivity.class);
                    } else {
                        intent.setClass(AuthenticationActivity.this, DetectActivity.class);
                        intent.putExtra("type", fromType);
                    }
                    startActivity(intent);
                } else if ("Welcome".equals(fromString)) {
                    startActivity(new Intent(AuthenticationActivity.this, SignInActivity.class));
                }
                finishActivity();
            }
        });
        lottAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束
                if (openOrcloseAnimation) {
                    myHandler.sendEmptyMessage(TO_CAMERA_PRE_RESOLVE);
                    openOrcloseAnimation = false;
                }
                Logger.e("动画结束");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 设置预览图像的方向
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    private void setCameraDisplayOrientation(Activity activity,
                                             int cameraId, Camera camera) {
        CameraInfo info =
                new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    /**
     * NV21格式(所有相机都支持的格式)转换为bitmap
     */
    public Bitmap decodeToBitMap(byte[] data, Camera mCamera) {
        if (mCamera == null) {
            return null;
        }
        try {
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            if (image != null) {
                baos.reset();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, baos);
                Bitmap bmp = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
                Matrix matrix = new Matrix();
                matrix.postRotate(0);
                Bitmap nbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                return nbmp;
            }
        } catch (Exception ex) {
            Log.e("tag", "Error:" + ex.getMessage());
        }
        return null;
    }
    private boolean isCameraRelease=false;
    private void finishActivity() {
        isCameraRelease=true;
        mImageData = null;
        myHandler.removeCallbacksAndMessages(null);
        Handlers.bg().post(new Runnable() {
            @Override
            public void run() {
                if (null != mCamera) {
                    if (callback != null) {
                        if (mPreviewSurface != null) {
                            SurfaceHolder holder = mPreviewSurface.getHolder();
                            if (holder != null) {
                                holder.removeCallback(callback);
                            }
                        }
                    }
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
                if (baos != null) {
                    try {
                        baos.close();
                        baos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.e("onDestroy");
        Handlers.bg().removeCallbacksAndMessages(null);
        if (lottAnimation != null)
            lottAnimation.cancelAnimation();
    }
}
