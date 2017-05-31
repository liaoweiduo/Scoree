package com.sustech.se.scoree;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by liaoweiduo on 27/05/2017.
 */

public class Song implements Serializable{
    private String name;
    private String filename;
    private int numOfLine;
    private int numOfNotes;
    private Note[] notes;
    private int pixPerLine;
    private ImageView[] imgOfStaffs;
    private int beats[];

    public Song(String name, String filename, int numOfLine, Note[] notes, int pixPerLine, int beats[]){
        this.name = name;
        this.filename = filename;
        this.numOfLine = numOfLine;
        this.numOfNotes = notes.length;
        this.pixPerLine = pixPerLine;
        this.notes = notes;
        this.beats = beats;
    }

    public String getName(){
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public Note getNoteById(int id){
        return notes[id];
    }

    public int getNumOfLine() {
        return numOfLine;
    }

    public int getNumOfNotes() {
        return numOfNotes;
    }

    public int getPixPerLine() {
        return pixPerLine;
    }

    public void setImgOfStaffs(ImageView[] imgs){
        imgOfStaffs = imgs;
    }

    public ImageView getImgOfStaff(int index){
        return imgOfStaffs[index];
    }
}
