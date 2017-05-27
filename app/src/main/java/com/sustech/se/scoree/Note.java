package com.sustech.se.scoree;

/**
 * Created by dell-pc on 2017/5/18.
 */

public class Note {

    private int pitch;
    private int beats;
    private int lineNum;
    private int position;

    public Note(int pitch, int beats, int lineNum, int position) {
        this.pitch = pitch;
        this.beats = beats;
        this.lineNum = lineNum;
        this.position = position;
    }

    public int getPitch() {
        return pitch;
    }

    public int getBeats() {
        return beats;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getPosition() {
        return position;
    }
}