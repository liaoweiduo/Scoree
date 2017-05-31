package com.sustech.se.scoree;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sustech.se.scoree.Note;
import com.sustech.se.scoree.Song;

/**
 * Created by liaoweiduo on 27/05/2017.
 */

public class FileOperator {
    static private String TAG = "FileOperator";

    public static Song getSongFromInputStream(InputStream source){
        BufferedReader reader = new BufferedReader(new InputStreamReader(source));
        try {
            String name = reader.readLine();
            String[] raw_beats = reader.readLine().split(" ");
            int song_beats[] = new int[2];
            song_beats[0] = Integer.parseInt(raw_beats[0]);
            song_beats[1] = Integer.parseInt(raw_beats[1]);
            String[] raw = reader.readLine().split(" ");
            int numOfLine = Integer.parseInt(raw[0]);
            int numOfNotes = Integer.parseInt(raw[1]);
            int pixPerLine = Integer.parseInt(raw[2]);
            Note[] notes = new Note[numOfNotes];
            Log.d(TAG, String.format("num of notes = %d\n", numOfNotes));
            for (int i=0;i<numOfNotes;i++){
                String r = reader.readLine();
                raw = r.split(" ");
                if (raw.length != 4){
                    Log.e(TAG, String.format("note err format: %s\n", r));
                    continue;
                }
                int pitch = Integer.parseInt(raw[0]);
                int beats = Integer.parseInt(raw[1]);
                int lineNum = Integer.parseInt(raw[2]);
                int position = Integer.parseInt(raw[3]);
                Log.i(TAG, String.format("pitch and beats = (%d, %d)\n", pitch, beats));
                notes[i] = new Note(pitch, beats, lineNum, position);
            }
            reader.close();
            Song song = new Song(name, numOfLine, notes, pixPerLine, song_beats);
            return song;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Bitmap getLocalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
