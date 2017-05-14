package com.sustech.se.scoree;

import android.app.Application;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import com.sustech.se.scoree.audioCapturer.AudioCapturer;
import com.sustech.se.scoree.audioCapturer.AudioCapturerConfig;
import com.sustech.se.scoree.audioCapturer.AudioCapturerInterface;

/**
 * Created by liaoweiduo on 08/04/2017.
 */

public class Data extends Application {
    private Queue<byte[]> dataQueue;
    private Semaphore dataMutex;
    private Semaphore dataFullBuffers;
    private Semaphore dataEmptyBuffers;
    private int buffersMax;

    private AudioCapturerConfig audioCapturerConfig;
    private AudioCapturerInterface audioCapturer=null;

    private Queue<byte[]> getDataQueue() {
        return dataQueue;
    }

    public void offer(byte[] data){
        getDataQueue().offer(data);
    }

    public byte[] poll(){
        return getDataQueue().poll();
    }

    public void acquireDataMutex() throws InterruptedException {
        dataMutex.acquire();
    }

    public void acquireDataFullBuffers() throws InterruptedException {
        dataFullBuffers.acquire();
    }

    public void acquireDataEmptyBuffers() throws InterruptedException {
        dataEmptyBuffers.acquire();
    }

    public void releaseDataMutex() throws InterruptedException {
        dataMutex.release();
    }

    public void releaseDataFullBuffers() throws InterruptedException {
        dataFullBuffers.release();
    }

    public void releaseDataEmptyBuffers() throws InterruptedException {
        dataEmptyBuffers.release();
    }

    private AudioCapturerConfig getAudioCapturerConfig() {
        return audioCapturerConfig;
    }

    public int getAudioCapturerSource() {
        return getAudioCapturerConfig().getSOURCE();
    }

    public void setAudioCapturerSource(int SOURCE) {
        getAudioCapturerConfig().setSOURCE(SOURCE);
    }

    public int getAudioCapturerSampleRate() {
        return getAudioCapturerConfig().getSAMPLE_RATE();
    }

    public void setAudioCapturerSampleRate(int SAMPLE_RATE) {
        getAudioCapturerConfig().setSAMPLE_RATE(SAMPLE_RATE);
    }

    public int getAudioCapturerChannelConfig() {
        return getAudioCapturerConfig().getCHANNEL_CONFIG();
    }

    public void setAudioCapturerChannelConfig(int CHANNEL_CONFIG) {
        getAudioCapturerConfig().setCHANNEL_CONFIG(CHANNEL_CONFIG);
    }

    public int getAudioCapturerAudioFormat() {
        return getAudioCapturerConfig().getAUDIO_FORMAT();
    }

    public void setAudioCapturerAudioFormat(int AUDIO_FORMAT) {
        getAudioCapturerConfig().setAUDIO_FORMAT(AUDIO_FORMAT);
    }

    public AudioCapturerInterface getAudioCapturer(){
        if (audioCapturer==null){
            synchronized (Data.class){
                if (audioCapturer==null){
                    audioCapturer=new AudioCapturer();
                    audioCapturer.audioCaptuerInit(getAudioCapturerSource(),getAudioCapturerSampleRate(),getAudioCapturerChannelConfig(),getAudioCapturerAudioFormat());
                }
            }
        }
        return audioCapturer;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataQueue = new LinkedList<>();
        buffersMax = 1000000; // 1MB
        dataMutex = new Semaphore(1);
        dataFullBuffers = new Semaphore(0);
        dataEmptyBuffers = new Semaphore(buffersMax);

        int SOURCE = MediaRecorder.AudioSource.MIC;
        int SAMPLE_RATE = 44100;   //44100
        int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
        int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
        audioCapturerConfig = new AudioCapturerConfig(SOURCE,SAMPLE_RATE,CHANNEL_CONFIG,AUDIO_FORMAT);
    }
}
