package com.sustech.se.scoree.audioCapturer;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * Created by liaoweiduo on 14/04/2017.
 */

public interface AudioCapturerInterface {
    boolean audioCaptuerInit(AudioCapturerConfig acc);
    boolean isCaptureStarted();
    void setOnAudioFrameCapturedListener(OnAudioFrameCapturedListener listener);
    boolean startCapture();
    void stopCapture();

    interface OnAudioFrameCapturedListener {
        void onAudioFrameCaptured(short[] audioData);
    }
}
