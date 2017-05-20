package com.sustech.se.scoree;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;

import com.sustech.se.scoree.audioCapturer.AudioCapturerInterface;


public class Audio2Key_refactor extends AppCompatActivity{

    private static final String PERMISSION_AUDIO="android.permission.RECORD_AUDIO";
    private Data gData;
    private AudioCapturerInterface ac;
    private Detector detector;
    private Decoder decoder;
    AudioAsyncTask audio = null;
    private TextView key_view;
    private Button button;
    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test1);
        gData = ((Data)getApplicationContext());
        detector = new Detector(gData.getAudioCapturerConfig().getBUFFER_SIZE());
        decoder= new Decoder(gData.getAudioCapturerConfig().getBUFFER_SIZE(), gData.getAudioCapturerConfig().getSAMPLE_RATE());

        key_view=(TextView) findViewById(R.id.key);
        ac=gData.getAudioCapturer();
        /*
        ac.setOnAudioFrameCapturedListener(new AudioCapturerInterface.OnAudioFrameCapturedListener() {
            @Override
            public void onAudioFrameCaptured(short[] audioData) {
            }
        });
        */
        button = (Button) findViewById(R.id.audioButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (started){//cancel
                    started = false;
                    ac.stopCapture();
                    button.setText(R.string.start);
                }
                else {
                    int checkPermission=checkCallingOrSelfPermission(PERMISSION_AUDIO);
                    if(checkPermission!= PackageManager.PERMISSION_GRANTED){
                        Log.e("MainActivity","No permission for audio");
                        return;
                    }
                    started = true;
                    ac.startCapture();
                    if (audio == null) audio= new AudioAsyncTask();
                    audio.execute();
                    button.setText(R.string.stop);
                }
            }
        });
    }


    public class AudioAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... datas) {
            while(ac.isCaptureStarted()) {
                short[] buffer = ac.read();
                if (buffer == null) return null;
                double[] detectResult = detector.detect(buffer);
                int key = decoder.decode(detectResult);
                if (detectResult != null) publishProgress(key);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... keys) {
            int key = keys[0];
            key_view.setText("Key:" + String.valueOf(key));
        }
    }
}
