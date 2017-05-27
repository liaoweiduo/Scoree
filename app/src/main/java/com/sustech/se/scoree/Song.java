package com.sustech.se.scoree;

/**
 * Created by liaoweiduo on 27/05/2017.
 */

public class Song {
    private int numOfLine;
    private int numOfNotes;
    private Note[] notes;
    private int pixPerLine;

    public Song(int numOfLine, Note[] notes, int pixPerLine){
        this.numOfLine = numOfLine;
        this.numOfNotes = notes.length;
        this.pixPerLine = pixPerLine;
        this.notes = notes;
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
}
