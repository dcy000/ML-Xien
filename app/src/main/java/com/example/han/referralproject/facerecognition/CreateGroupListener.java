package com.example.han.referralproject.facerecognition;

import android.os.Bundle;

import com.iflytek.cloud.IdentityResult;
import com.iflytek.cloud.SpeechError;

/**
 * Created by gzq on 2018/2/28.
 */

public interface CreateGroupListener {
    void onResult(IdentityResult result, boolean islast);
    void onEvent(int eventType, int arg1, int arg2, Bundle obj);
    void onError(SpeechError error);
}
