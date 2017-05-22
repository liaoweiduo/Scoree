package com.sustech.se.scoree;

/**
 * Created by dell-pc on 2017/5/18.
 */

import android.util.Log;

public class Note
{
    private int beats;
    private int pitch;

    public Note(int pitch, int beats)
    {
        this.beats = beats;
        this.pitch = pitch;
    }
    public int getBeats(){
        return beats;
    }
    public int getPitch(){
        return pitch;
    }
}
