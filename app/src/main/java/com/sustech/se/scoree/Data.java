package com.sustech.se.scoree;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.media.AudioFormat;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import android.os.Environment;
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

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public Song[] getSongs() {
        return songs;
    }

    public void setSongs(Song[] songs) {
        this.songs = songs;
    }

    private void saveInitFiles(String songName){
        String songPath = workingDirectory + "/" + songName;
        File dir = new File(songPath);
        if (!dir.exists()){
            dir.mkdir();
            try {
                //保存txt
                File songTxt = new File(songPath, songName + ".txt");
                if (!songTxt.exists()) {
                    InputStream is =getAssets().open("songs/" + songName + ".txt");
                    FileOutputStream fos = new FileOutputStream(songTxt);
                    int lenght = is.available();
                    byte[]  buffer = new byte[lenght];
                    is.read(buffer);
                    fos.write(buffer);
                    fos.flush();
                    is.close();
                    fos.close();
                }
                //保存图片

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Log.i("Data", "youandme exists");
        }

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
        workingDirectory = Environment.getExternalStorageDirectory().getPath()+"/staff";
        saveInitFiles("youandme");
        // TODO: 31/05/2017 init songs
    }
}
