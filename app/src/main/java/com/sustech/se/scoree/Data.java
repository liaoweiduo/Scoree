package com.sustech.se.scoree;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import android.util.Log;

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

        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        int SOURCE = sp.getInt(getString(R.string.source), MediaRecorder.AudioSource.MIC);
        int SAMPLE_RATE = sp.getInt(getString(R.string.sampleRate), 8000);
        int CHANNEL_CONFIG = sp.getInt(getString(R.string.channelConfig), AudioFormat.CHANNEL_IN_STEREO);
        int AUDIO_FORMAT = sp.getInt(getString(R.string.audioFormat), AudioFormat.ENCODING_PCM_16BIT);
        int BUFFER_SIZE = sp.getInt(getString(R.string.bufferSize), 2048);
        audioCapturerConfig = new AudioCapturerConfig(SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

/*
        Editor editor = sp.edit();
        editor.putInt(getString(R.string.source), MediaRecorder.AudioSource.MIC);
        editor.putInt(getString(R.string.sampleRate), 8000);
        editor.putInt(getString(R.string.channelConfig), AudioFormat.CHANNEL_IN_STEREO);
        editor.putInt(getString(R.string.audioFormat), AudioFormat.ENCODING_PCM_16BIT);
        editor.putInt(getString(R.string.bufferSize), 2048);
        if(editor.commit() != ) Log.e;
*/

    }
}
