package com.example.han.referralproject.floatingball;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;


import com.example.han.referralproject.R;
import com.ml.brightness.BrightnessHelper;

import java.util.Calendar;

public class AssistiveTouchService extends Service {

    private boolean isMoving;

    private float rawX;
    private float rawY;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mStatusBarHeight;

    private int lastAssistiveTouchViewX;
    private int lastAssistiveTouchViewY;

    private View mAssistiveTouchView;
    private View mInflateAssistiveTouchView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private PopupWindow mPopupWindow;
    private AlertDialog.Builder mBulider;
    private AlertDialog mAlertDialog;
    private View mScreenShotView;

    //    private Timer mTimer;
    private Handler mHandler;

    private LayoutInflater mInflater;

    ImageView mImageView;

    SeekBar mSeekBar;

    AudioManager mAudioManager;

    ImageView mIvVolumeIndicator;

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    private int maxVolume;
    private int currentVolume;
    private SeekBar sbBrightness;
    private float mScreenBrightness;
    private BrightnessHelper mBrightnessHelper;
    private ImageView mIvBrightnessIndicator;


    // private CheckDoubleClickListener checkDoubleClickListener;


    @Override

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        init();
        calculateForMyPhone();
        createAssistiveTouchView();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void init() {
//        mTimer = new Timer();
        mHandler = new MyHandler();
        mBulider = new AlertDialog.Builder(AssistiveTouchService.this);
        mAlertDialog = mBulider.create();
        mParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        mInflater = LayoutInflater.from(this);
        mAssistiveTouchView = mInflater.inflate(R.layout.assistive_touch_layout, null);

        mImageView = (ImageView) mAssistiveTouchView.findViewById(R.id.icons);

        //   mImageView.setOnClickListener(checkDoubleClickListener);

        mInflateAssistiveTouchView = mInflater.inflate(R.layout.assistive_touch_inflate_layout, null);
        mIvVolumeIndicator = (ImageView) mInflateAssistiveTouchView.findViewById(R.id.lude_iv_volume_indicator);
        mIvBrightnessIndicator = (ImageView) mInflateAssistiveTouchView.findViewById(R.id.lude_iv_brightness_indicator);

        sbBrightness = (SeekBar) mInflateAssistiveTouchView.findViewById(R.id.seek_brightness);
        mScreenBrightness = mParams.screenBrightness;
        mBrightnessHelper = new BrightnessHelper(this);
        sbBrightness.setMax(mBrightnessHelper.getMaxBrightness());
        int progress = mBrightnessHelper.getSystemBrightness();
        float i = 1f * progress / mBrightnessHelper.getMaxBrightness();
        if (i < 0.2) {
            mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_low);
        } else if (i < 0.6) {
            mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_mid);
        } else {
            mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_high);
        }
        sbBrightness.setProgress(progress);
        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float i = 1f * progress / mBrightnessHelper.getMaxBrightness();
                if (i < 0.2) {
                    mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_low);
                } else if (i < 0.6) {
                    mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_mid);
                } else {
                    mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_high);
                }
                mBrightnessHelper.setSystemBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                float i = 1f * progress / mBrightnessHelper.getMaxBrightness();
                if (i < 0.2) {
                    mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_low);
                } else if (i < 0.6) {
                    mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_mid);
                } else {
                    mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_high);
                }
                mBrightnessHelper.setSystemBrightness(progress);
            }
        });

        //初始化音频管理器
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        mSeekBar = (SeekBar) mInflateAssistiveTouchView.findViewById(R.id.seek);
        mSeekBar.setMax(maxVolume);
        mSeekBar.setProgress(currentVolume);
        if (currentVolume == 0) {
            mIvVolumeIndicator.setImageResource(R.drawable.lude_ic_volum_mute);
        } else {
            mIvVolumeIndicator.setImageResource(R.drawable.lude_ic_volume_high);

        }
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // fromUser直接来自于用户拖动为true，否则为false
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), AudioManager.FLAG_PLAY_SOUND);

                if (seekBar.getProgress() == 0) {
                    mIvVolumeIndicator.setImageResource(R.drawable.lude_ic_volum_mute);
                } else {
                    mIvVolumeIndicator.setImageResource(R.drawable.lude_ic_volume_high);

                }

//                mHandler.removeCallbacksAndMessages(null);
//                mTimer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        mHandler.removeCallbacksAndMessages(null);
//                        Message msg = mHandler.obtainMessage();
//                        msg.what = 2;
//                        mHandler.sendMessage(msg);
//                    }
//                }, 3000);
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(2, 5000);
            }
        });


    }

    private void calculateForMyPhone() {
        DisplayMetrics displayMetrics = SystemsUtils.getScreenSize(this);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        mStatusBarHeight = SystemsUtils.getStatusBarHeight(this);

        //  mInflateAssistiveTouchView.setLayoutParams(new WindowManager.LayoutParams((int) (mScreenWidth * 0.5), (int) (mScreenWidth * 0.5)));
    }

    public void createAssistiveTouchView() {
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.x = mScreenWidth;
        mParams.y = 520;
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager.addView(mAssistiveTouchView, mParams);
        mAssistiveTouchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rawX = event.getRawX();
                rawY = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMoving = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        setAssitiveTouchViewAlign();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isMoving = true;
                        mParams.x = (int) (rawX - mAssistiveTouchView.getMeasuredWidth() / 2);
                        mParams.y = (int) (rawY - mAssistiveTouchView.getMeasuredHeight() / 2 - mStatusBarHeight);
                        mWindowManager.updateViewLayout(mAssistiveTouchView, mParams);
                }
               /* if (isMoving)
                    return true;
                else
                    return false;*/
                return false;
            }
        });


        if (!isMoving) {


            mAssistiveTouchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    long currentTime = Calendar.getInstance().getTimeInMillis();

                    if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                        lastClickTime = currentTime;

                        mAssistiveTouchView.setAlpha(0);
                        lastAssistiveTouchViewX = mParams.x;
                        lastAssistiveTouchViewY = mParams.y;
                        myAssitiveTouchAnimator(mParams.x, mScreenWidth / 2 - mAssistiveTouchView.getMeasuredWidth() / 2, mParams.y, mScreenHeight / 2 - mAssistiveTouchView.getMeasuredHeight() / 2, true).start();
                        int progress = mBrightnessHelper.getSystemBrightness();
                        float i = 1f * progress / mBrightnessHelper.getMaxBrightness();
                        if (i < 0.2) {
                            mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_low);
                        } else if (i < 0.6) {
                            mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_mid);
                        } else {
                            mIvBrightnessIndicator.setImageResource(R.drawable.lude_ic_brightness_high);
                        }
                        sbBrightness.setProgress(progress);
                        mPopupWindow = new PopupWindow(mInflateAssistiveTouchView, (int) (mScreenWidth * 0.5), (int) (mScreenWidth * 0.25));

                        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                myAssitiveTouchAnimator(mParams.x, lastAssistiveTouchViewX, mParams.y, lastAssistiveTouchViewY, true).start();
                                mAssistiveTouchView.setAlpha(1);
                                mImageView.setAlpha(0.85f);

                            }
                        });
                        mPopupWindow.setFocusable(true);
                        mPopupWindow.setTouchable(true);
                        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                        mPopupWindow.showAtLocation(mAssistiveTouchView, Gravity.CENTER, 0, 0);

                        mImageView.setAlpha(0.0f);


                    }

//                    mTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            mHandler.removeCallbacksAndMessages(null);
//                            Message msg = mHandler.obtainMessage();
//                            msg.what = 2;
//                            mHandler.sendMessage(msg);
//                        }
//                    }, 3000);
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.sendEmptyMessageDelayed(2, 5000);


                }
            });


        }


    }

    private ValueAnimator myAssitiveTouchAnimator(final int fromx, final int tox, int fromy, final int toy, final boolean flag) {
        PropertyValuesHolder p1 = PropertyValuesHolder.ofInt("X", fromx, tox);
        PropertyValuesHolder p2 = PropertyValuesHolder.ofInt("Y", fromy, toy);
        ValueAnimator v1 = ValueAnimator.ofPropertyValuesHolder(p1, p2);
        v1.setDuration(100L);
        v1.setInterpolator(new DecelerateInterpolator());
        v1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer x = (Integer) animation.getAnimatedValue("X");
                Integer y = (Integer) animation.getAnimatedValue("Y");
                mParams.x = x;
                mParams.y = y;
                mWindowManager.updateViewLayout(mAssistiveTouchView, mParams);
            }
        });
        v1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (flag)
                    mAssistiveTouchView.setAlpha(0.85f);
            }
        });
        return v1;
    }


    private void setAssitiveTouchViewAlign() {
        int mAssistiveTouchViewWidth = mAssistiveTouchView.getMeasuredWidth();
        int mAssistiveTouchViewHeight = mAssistiveTouchView.getMeasuredHeight();
        int top = mParams.y + mAssistiveTouchViewWidth / 2;
        int left = mParams.x + mAssistiveTouchViewHeight / 2;
        int right = mScreenWidth - mParams.x - mAssistiveTouchViewWidth / 2;
        int bottom = mScreenHeight - mParams.y - mAssistiveTouchViewHeight / 2;
        int lor = Math.min(left, right);
        int tob = Math.min(top, bottom);
        int min = Math.min(lor, tob);
        lastAssistiveTouchViewX = mParams.x;
        lastAssistiveTouchViewY = mParams.y;
        if (min == top) mParams.y = 0;
        if (min == left) mParams.x = 0;
        if (min == right) mParams.x = mScreenWidth - mAssistiveTouchViewWidth;
        if (min == bottom) mParams.y = mScreenHeight - mAssistiveTouchViewHeight;
        myAssitiveTouchAnimator(lastAssistiveTouchViewX, mParams.x, lastAssistiveTouchViewY, mParams.y, false).start();
    }

    private void showScreenshot(String name) {
        String path = "/sdcard/Pictures/" + name + ".png";
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        mScreenShotView = mInflater.inflate(R.layout.screen_shot_show, null);
        ImageView imageView = (ImageView) mScreenShotView.findViewById(R.id.screenshot);
        imageView.setImageBitmap(bitmap);

        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mAlertDialog.show();
        WindowManager.LayoutParams alertDialogParams = mAlertDialog.getWindow().getAttributes();
        alertDialogParams.width = mScreenWidth;
        alertDialogParams.height = mScreenHeight;
        mAlertDialog.getWindow().setAttributes(alertDialogParams);
        mAlertDialog.getWindow().setContentView(mScreenShotView);

//        mTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Message msg = mHandler.obtainMessage();
//                msg.what = 2;
//                mHandler.sendMessage(msg);
//            }
//        }, 3000);

    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    showScreenshot((String) msg.obj);
                    break;
                case 2:
                    mAlertDialog.dismiss();
                default:
                    mPopupWindow.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(mAssistiveTouchView);
    }
}
