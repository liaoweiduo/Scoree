package com.sustech.se.scoree;

import android.app.Application;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.MediaRecorder;

import android.util.Log;

import com.sustech.se.scoree.audioCapturer.AudioCapturer;
import com.sustech.se.scoree.audioCapturer.AudioCapturerConfig;
import com.sustech.se.scoree.audioCapturer.AudioCapturerInterface;


/**
 * Created by liaoweiduo on 08/04/2017.
 */

public class Data extends Application {
    private AudioCapturerConfig audioCapturerConfig;
    private AudioCapturerInterface audioCapturer=null;
    private int numOfLines;
    private int pageTurnSetting;
    private String workingDirectory;
    private Song[] songs;

    public AudioCapturerConfig getAudioCapturerConfig() {
        return audioCapturerConfig;
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

    public int getNumOfLines() {
        return numOfLines;
    }

    public void setNumOfLines(int numOfLines) {
        this.numOfLines = numOfLines;
    }

    public int getPageTurnSetting() {
        return pageTurnSetting;
    }

    public void setPageTurnSetting(int pageTurnSetting) {
        this.pageTurnSetting = pageTurnSetting;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public Song[] getSongs() {
        return songs;
    }

    public void setSongs(Song[] songs) {
        this.songs = songs;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        int SOURCE = sp.getInt(getString(R.string.source), MediaRecorder.AudioSource.MIC);
        int SAMPLE_RATE = sp.getInt(getString(R.string.sampleRate), 8000);
        int CHANNEL_CONFIG = sp.getInt(getString(R.string.channelConfig), AudioFormat.CHANNEL_IN_STEREO);
        int AUDIO_FORMAT = sp.getInt(getString(R.string.audioFormat), AudioFormat.ENCODING_PCM_16BIT);
        int BUFFER_SIZE = sp.getInt(getString(R.string.bufferSize), 2048);
        int numOfLines = sp.getInt("numOfLines", getResources().getInteger(R.integer.defaultNumOfLine));
        int pageTurnSetting = sp.getInt("pageTurnSetting", numOfLines);
        Log.i("Data","numoflines="+numOfLines+" pageturnsetting="+pageTurnSetting);
        this.audioCapturerConfig = new AudioCapturerConfig(SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
        this.numOfLines = numOfLines;
        this.pageTurnSetting = pageTurnSetting;
        workingDirectory = "staff";
    }
}
