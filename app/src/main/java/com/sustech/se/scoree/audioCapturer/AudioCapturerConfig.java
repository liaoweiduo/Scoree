package com.sustech.se.scoree.audioCapturer;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * Created by liaoweiduo on 19/04/2017.
 */

public class AudioCapturerConfig {
    private int SOURCE;
    private int SAMPLE_RATE;
    private int CHANNEL_CONFIG;
    private int AUDIO_FORMAT;

    public AudioCapturerConfig(int SOURCE, int SAMPLE_RATE, int CHANNEL_CONFIG, int AUDIO_FORMAT) {
        this.SOURCE = SOURCE;
        this.SAMPLE_RATE = SAMPLE_RATE;
        this.CHANNEL_CONFIG = CHANNEL_CONFIG;
        this.AUDIO_FORMAT = AUDIO_FORMAT;
    }

    public int getSOURCE() {
        return SOURCE;
    }

    public void setSOURCE(int SOURCE) {
        this.SOURCE = SOURCE;
    }

    public int getSAMPLE_RATE() {
        return SAMPLE_RATE;
    }

    public void setSAMPLE_RATE(int SAMPLE_RATE) {
        this.SAMPLE_RATE = SAMPLE_RATE;
    }

    public int getCHANNEL_CONFIG() {
        return CHANNEL_CONFIG;
    }

    public void setCHANNEL_CONFIG(int CHANNEL_CONFIG) {
        this.CHANNEL_CONFIG = CHANNEL_CONFIG;
    }

    public int getAUDIO_FORMAT() {
        return AUDIO_FORMAT;
    }

    public void setAUDIO_FORMAT(int AUDIO_FORMAT) {
        this.AUDIO_FORMAT = AUDIO_FORMAT;
    }
}
