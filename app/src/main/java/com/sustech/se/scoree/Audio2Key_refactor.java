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
    Data gData;
    Detector detector;
    Decoder decoder;

    Audio audio;
    TextView key_view;
    Button button;
    public boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test1);
        gData = ((Data)getApplicationContext());
        detector = new Detector(gData.getAudioCapturerConfig().getBUFFER_SIZE());
        decoder= new Decoder(gData.getAudioCapturerConfig().getBUFFER_SIZE(), gData.getAudioCapturerConfig().getSAMPLE_RATE());

        key_view=(TextView) findViewById(R.id.key);
        final AudioCapturerInterface ac=gData.getAudioCapturer();
        ac.setOnAudioFrameCapturedListener(new AudioCapturerInterface.OnAudioFrameCapturedListener() {
            @Override
            public void onAudioFrameCaptured(short[] audioData) {
                audio = new Audio();
                audio.execute(audioData);
            }
        });

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
                    started = true;
                    int checkPermission=checkCallingOrSelfPermission(PERMISSION_AUDIO);
                    if(checkPermission!= PackageManager.PERMISSION_GRANTED){
                        Log.e("MainActivity","No permission for audio");
                        return;
                    }
                    ac.startCapture();
                    button.setText(R.string.stop);
                }
            }
        });
    }


    public class Audio extends AsyncTask<short[], Integer, Void> {

        @Override
        protected Void doInBackground(short[]... datas) {
            short[] data=datas[0];
            double[] detectResult = detector.detect(data);
            int key = decoder.decode(detectResult);
            if (detectResult != null) publishProgress(key);
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... keys) {
            int key = keys[0];
            key_view.setText("Key:"+String.valueOf(key));
        }


    }
}
