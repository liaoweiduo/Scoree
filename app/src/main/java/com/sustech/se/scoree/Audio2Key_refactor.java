package com.sustech.se.scoree;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;

import com.sustech.se.scoree.UI.Note;
import com.sustech.se.scoree.audioCapturer.AudioCapturerInterface;
import com.sustech.se.scoree.audioProcesser.Decoder;
import com.sustech.se.scoree.audioProcesser.DecoderInterface;
import com.sustech.se.scoree.audioProcesser.Detector;
import com.sustech.se.scoree.audioProcesser.DetectorInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import devlight.io.library.ntb.NavigationTabBar;

public class Audio2Key_refactor extends AppCompatActivity{

    private static final String PERMISSION_AUDIO="android.permission.RECORD_AUDIO";
    private Data gData;
    private AudioCapturerInterface ac;
    private DetectorInterface detector;
    private DecoderInterface decoder;
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

        int checkPermission=checkCallingOrSelfPermission(PERMISSION_AUDIO);
        if(checkPermission!= PackageManager.PERMISSION_GRANTED){
            Log.e("MainActivity","No permission for audio");
            Toast.makeText(this, "No permission for audio", Toast.LENGTH_SHORT).show();
            return;
        }

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
                    ac.stopCapture();
                    button.setText(R.string.start);
                    started = false;
                }
                else {
                    int checkPermission=checkCallingOrSelfPermission(PERMISSION_AUDIO);
                    if(checkPermission!= PackageManager.PERMISSION_GRANTED){
                        Log.e("MainActivity","No permission for audio");
                        return;
                    }
                    ac.startCapture();
                    audio = new AudioAsyncTask();
                    audio.execute();

                    button.setText(R.string.stop);
                    started = true;
                }
            }
        });


        /*
        songs test
         */
        Toast.makeText(this, "test songs", Toast.LENGTH_SHORT).show();

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                getResources().openRawResource(R.raw.bugfly)));
        int num;
        Note[] notes;
        try {
            num = Integer.parseInt(reader.readLine());
            notes = new Note[num];
            System.out.printf("num=%d\n",num);
            String[] raw = reader.readLine().split(" ");
            int whichfenyingfu = Integer.parseInt(raw[0]);
            int meixiaojiewhich = Integer.parseInt(raw[1]);
            for (int i=0;i<num;i++){
                raw = reader.readLine().split(" ");
                int beats = Integer.parseInt(raw[0]);
                int pitch = Integer.parseInt(raw[1]);
                System.out.printf("%d %d\n",beats,pitch);
                notes[i] = new Note(beats, pitch);
            }
            reader.close();


            OutputStream os = openFileOutput("data", Context.MODE_PRIVATE);
            os.write(num);os.write(" ".getBytes());
            for (int i=0;i<num;i++){
                os.write(notes[i].getBeats());os.write(" ".getBytes());
                os.write(notes[i].getPitch());os.write(" ".getBytes());
            }
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class AudioAsyncTask extends AsyncTask<Void, Integer, Void> {

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

        @Override
        protected void onPostExecute(Void result) {
            Log.i("mainActivity", "onPostExecute() called");
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            Log.i("mainActivity", "onCancelled() called");
        }
    }
}
