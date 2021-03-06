package com.iflytek.wake;

import android.os.Environment;

import com.gzq.lib_core.base.App;
import com.gzq.lib_core.utils.ToastUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.util.ResourceUtil;

/**
 * Created by lenovo on 2018/4/10.
 */

public class MLVoiceWake {

    public static VoiceWakeuper initVoiceWakeuper() {
        VoiceWakeuper wakeuper = VoiceWakeuper.getWakeuper();
        if (wakeuper == null) {
            wakeuper = VoiceWakeuper.createWakeuper(App.getApp(), null);
        }
        if (wakeuper == null) {
            try {
                throw new Exception("初始化语音唤醒对象失败");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return wakeuper;
    }


    private static VoiceWakeuper setParams() {
        VoiceWakeuper voiceWakeuper = initVoiceWakeuper();
        voiceWakeuper.setParameter(SpeechConstant.PARAMS, null);
        voiceWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:10");
        voiceWakeuper.setParameter(SpeechConstant.IVW_SST, "wakeup");
        voiceWakeuper.setParameter(SpeechConstant.KEEP_ALIVE, "1");
        voiceWakeuper.setParameter(SpeechConstant.IVW_NET_MODE, "1");
        voiceWakeuper.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
        voiceWakeuper.setParameter(SpeechConstant.IVW_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getPath() + "/msc/ivw.wav");
        voiceWakeuper.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        return voiceWakeuper;
    }

    private static String getResource() {
        return ResourceUtil.generateResourcePath(App.getApp(), ResourceUtil.RESOURCE_TYPE.assets, "ivw/59196d96.jet");
    }

    public static void startWakeUp(WakeuperListener listener) {
        VoiceWakeuper voiceWakeuper = setParams();
        if (listener == null) {
            listener = new MLWakeuperListener() {
                @Override
                public void onMLError(int errorCode) {

                }

                @Override
                public void onMLResult() {
                    ToastUtils.showShort("小E被唤醒");
                }
            };
        }
        voiceWakeuper.startListening(listener);
    }


    public static void stopWakeUp() {
        VoiceWakeuper wakeuper = VoiceWakeuper.getWakeuper();
        if (wakeuper != null && wakeuper.isListening()) {
            wakeuper.stopListening();
        }
    }


    public static void destroy() {
        VoiceWakeuper wakeuper = VoiceWakeuper.getWakeuper();
        if (wakeuper != null) {
            wakeuper.destroy();
        }
    }
}
