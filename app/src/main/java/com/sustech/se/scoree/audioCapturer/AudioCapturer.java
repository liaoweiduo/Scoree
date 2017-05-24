/*
 *  COPYRIGHT NOTICE
 *  Copyright (C) 2016, Jhuster <lujun.hust@gmail.com>
 *  https://github.com/Jhuster/Android
 *
 *  @license under the Apache License, Version 2.0
 *
 *  @file    AudioCapturer.java
 *
 *  @version 1.0
 *  @author  Jhuster
 *  @date    2016/03/10
 */
package com.sustech.se.scoree.audioCapturer;

import android.media.AudioRecord;
import android.util.Log;

public class AudioCapturer implements AudioCapturerInterface {

    private static final String TAG = "AudioCapturer";

    private int DEFAULT_SOURCE;
    private int DEFAULT_SAMPLE_RATE;
    private int DEFAULT_CHANNEL_CONFIG;
    private int DEFAULT_AUDIO_FORMAT;
    private int DEFAULT_BUFFER_SIZE;
    private boolean isInit = false;

    private AudioRecord mAudioRecord;
    //private int mMinBufferSize = 0;

    private Thread mCaptureThread;
    private boolean mIsCaptureStarted = false;
    private volatile boolean mIsLoopExit = false;

    private OnAudioFrameCapturedListener mAudioFrameCapturedListener = null;

    @Override
    public boolean audioCaptuerInit(AudioCapturerConfig acc) {
        DEFAULT_SOURCE = acc.getSOURCE();
        DEFAULT_SAMPLE_RATE = acc.getSAMPLE_RATE();
        DEFAULT_CHANNEL_CONFIG = acc.getCHANNEL_CONFIG();
        DEFAULT_AUDIO_FORMAT = acc.getAUDIO_FORMAT();
        DEFAULT_BUFFER_SIZE = acc.getBUFFER_SIZE();
        isInit = true;
        return true;
    }

    @Override
    public boolean isCaptureStarted() {
        return mIsCaptureStarted;
    }

    @Override
    public void setOnAudioFrameCapturedListener(OnAudioFrameCapturedListener listener) {
        mAudioFrameCapturedListener = listener;
    }

    @Override
    public short[] read() {
        short[] buffer = new short[DEFAULT_BUFFER_SIZE];
        int ret = mAudioRecord.read(buffer, 0, DEFAULT_BUFFER_SIZE);
        if (ret == AudioRecord.ERROR_INVALID_OPERATION) {
            Log.e(TAG, "Error ERROR_INVALID_OPERATION");
            return null;
        } else if (ret == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Error ERROR_BAD_VALUE");
            return null;
        }
        Log.d(TAG, "OK, Captured " + ret + " bytes !");
        return buffer;
    }

    @Override
    public boolean startCapture() {
        return isInit && startCapture(DEFAULT_SOURCE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public void stopCapture() {

        if (!mIsCaptureStarted) {
            return;
        }

        mIsLoopExit = true;
        if(mAudioFrameCapturedListener != null) {
            Log.e(TAG, "stop capture with capturedListener");
            try {
                mCaptureThread.interrupt();
                mCaptureThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord.stop();
        }

        mIsCaptureStarted = false;
        mAudioRecord.release();
        mAudioFrameCapturedListener = null;

        Log.d(TAG, "Stop audio capture success !");
    }

    private boolean startCapture(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSize) {

        if (mIsCaptureStarted) {
            Log.e(TAG, "Capture already started !");
            return false;
        }

//        mMinBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
//        if (mMinBufferSize == AudioRecord.ERROR_BAD_VALUE) {
//            Log.e(TAG, "Invalid parameter !");
//            return false;
//        }
        Log.d(TAG, "bufferSize = " + bufferSize + " bytes !");

        mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord initialize fail !");
            return false;
        }

        mAudioRecord.startRecording();

        mIsLoopExit = false;
        mIsCaptureStarted = true;
        if(mAudioFrameCapturedListener != null) {
            Log.d(TAG, "audio capture with capturedListener");
            mCaptureThread = new Thread(new AudioCaptureRunnable());
            mCaptureThread.start();
        }

        Log.d(TAG, "Start audio capture success !");

        return true;
    }

    private class AudioCaptureRunnable implements Runnable {

        @Override
        public void run() {

            while (!mIsLoopExit) {

                short[] buffer = new short[DEFAULT_BUFFER_SIZE];

                int ret = mAudioRecord.read(buffer, 0, DEFAULT_BUFFER_SIZE);
                if (ret == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.e(TAG, "Thread: Error ERROR_INVALID_OPERATION");
                } else if (ret == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "Thread: Error ERROR_BAD_VALUE");
                } else {
                    if (mAudioFrameCapturedListener != null) {
                        mAudioFrameCapturedListener.onAudioFrameCaptured(buffer);
                    }
                    Log.d(TAG, "OK, Captured " + ret + " bytes !");
                }
//                SystemClock.sleep(10);
            }
        }
    }
}