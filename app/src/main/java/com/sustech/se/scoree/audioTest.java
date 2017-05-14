package com.sustech.se.scoree;

import android.content.pm.PackageManager;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;

import com.sustech.se.scoree.audioCapturer.*;

public class audioTest extends AppCompatActivity{

    private static final String PERMISSION_AUDIO="android.permission.RECORD_AUDIO";

    TextView tv;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test);


        tv=(TextView) findViewById(R.id.data);

        Data gData= ((Data)getApplicationContext());
        final AudioCapturerInterface ac=gData.getAudioCapturer();

        //final AudioPlayer ap=new AudioPlayer();

        findViewById(R.id.btnStartRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ac.setOnAudioFrameCapturedListener(new AudioCapturerInterface.OnAudioFrameCapturedListener() {
                    @Override
                    public void onAudioFrameCaptured(byte[] audioData) {
                        Data gData= ((Data)getApplicationContext());
                        try {
                            gData.acquireDataEmptyBuffers();
                            gData.acquireDataMutex();
                            gData.offer(audioData);  //符合 less knowledge原则
                            gData.releaseDataMutex();
                            gData.releaseDataFullBuffers();
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        byte[] data=gData.poll();
                        for(int i=0;i<data.length;i++){
                            if (data!=null)
                                System.out.printf("%d ",(int)data[i]);
                        }
                        System.out.println("\n");
                        //ap.play(getAudioData(),0,getAudioData().length);
                    }
                });
                int checkPermission=checkCallingOrSelfPermission(PERMISSION_AUDIO);
                if(checkPermission!= PackageManager.PERMISSION_GRANTED){
                    Log.e("MainActivity","No permission for audio");
                    return;
                }
                if(! ac.isCaptureStarted())  ac.startCapture();
                //ap.startPlayer();

            }
        });
        findViewById(R.id.btnStopRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ac.stopCapture();
                //ap.stopPlayer();
            }
        });
    }


}