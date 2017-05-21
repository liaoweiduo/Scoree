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
/*
    private Queue<short[]> dataQueue;
    private Semaphore dataMutex;
    private Semaphore dataFullBuffers;
    private Semaphore dataEmptyBuffers;
*/
    private AudioCapturerConfig audioCapturerConfig;
    private AudioCapturerInterface audioCapturer=null;

/*
    private Queue<short[]> getDataQueue() {
        return dataQueue;
    }

    public void offer(short[] data){
        getDataQueue().offer(data);
    }

    public short[] poll(){
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
*/
    public AudioCapturerConfig getAudioCapturerConfig() {
        return audioCapturerConfig;
    }
    public void setAudioCapturerConfig(AudioCapturerConfig acc){
        audioCapturerConfig=acc;
    }

    public AudioCapturerInterface getAudioCapturer(){
        if (audioCapturer==null){
            synchronized (Data.class){
                if (audioCapturer==null){
                    audioCapturer=new AudioCapturer();
                    audioCapturer.audioCaptuerInit(audioCapturerConfig);
                }
            }
        }
        return audioCapturer;
    }


    @Override
    public void onCreate() {
        super.onCreate();
/*
        dataQueue = new LinkedList<>();
        int buffersMax = 1000000;
        dataMutex = new Semaphore(1);
        dataFullBuffers = new Semaphore(0);
        dataEmptyBuffers = new Semaphore(buffersMax);
*/
        int SOURCE = MediaRecorder.AudioSource.MIC;
        int SAMPLE_RATE = 8000;   //44100
        int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
        int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
        int BUFFER_SIZE = 2048;
        audioCapturerConfig = new AudioCapturerConfig(SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
    }
}
