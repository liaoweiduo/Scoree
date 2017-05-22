package com.sustech.se.scoree;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.ViewGroup.LayoutParams;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RelativeLayout;

public class ShowFace extends AppCompatActivity {

    private final int numofline = 4;
    private final int num_beats = 8;
    ImageView[][] noteImages = new ImageView[numofline][num_beats];
    //ImageView[] accImages = new ImageView[num];
    ImageView[] staff = new ImageView[numofline];

    //define postion params for notes
    private final int BASE = 840;
    private final int FIRST = 85;
    private final int SECOND = 235;
    private final int THIRD = 395;
    private final int FOURTH = 555;
    private final int W_INTERVAL = 18;
    private final int H_INTERVAL = 4;
    private final int BASE_Y = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_face);

        //get the size of staff
        int num_wholebeats = 0; //总的节拍数。这里八分之一拍记为一拍
        int num_staff = num_wholebeats/32; //五线谱的行数
        int num_wholenotes = 0;

        initialNoteImages();
       // FrameLayout framestaff = (FrameLayout)findViewById(R.id.frame_staff);
        Note[] staffnotes = initialNote();
        num_wholenotes = staffnotes.length; //总的音符数
        for(int m = 0; m < staffnotes.length; m++){
            num_wholebeats = num_wholenotes+staffnotes[m].getBeats();
        }
        //num_staff = num_wholebeats/32;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.staff5);
        int staffWidth = bitmap.getWidth();
        int staffHeight = bitmap.getHeight();

        int numofremainstaff = num_wholebeats, numofcurrentstaff ;
        int current_beats = 0;
        int current_notes = 0;
        while(numofremainstaff>0){
            makeInvisible();
            if(numofremainstaff>=128){
                numofcurrentstaff = 4;
            }
            else{
                numofcurrentstaff = numofremainstaff/32+1;
            }
            //numofremainstaff =  numofremainstaff/128;
            for(int k=0; k<numofcurrentstaff; k++){

                staff[k].setVisibility(View.VISIBLE);
            }
            for(int i=0; i<numofcurrentstaff; i++){
                int currentlinebeats = 0;
                int currentline = 0;
                //int formerposition = 0;

                for(int j=0; j<4; j++){ //4表示一共有四行谱
                    int column = 1;
                    while(column<=4&&current_notes<staffnotes.length){
                        //

                        int currentlinenotes = 0;
                        int currentcolumnbeats=0;
                        float formerposition = staff[currentline].getX()+decideBeginX(column)*staffWidth/840;
                        decideNote(noteImages[j][currentlinenotes], staffnotes[current_beats].getBeats());
                        noteImages[j][currentlinenotes].setX(formerposition);
                        decidePostionY(noteImages[j][currentlinenotes], staffnotes[current_notes].getPitch(), currentline,staffHeight);
                       // System.out.println(formerposition);
                        noteImages[j][currentlinenotes].setVisibility(View.VISIBLE);
                        currentcolumnbeats += staffnotes[current_notes].getBeats();
                        current_notes++;
                        currentlinenotes++;

                        while(currentcolumnbeats<8&&current_notes<staffnotes.length){

                            //int tempbeat =
                            noteImages[j][currentlinenotes]=decideNote(noteImages[j][currentlinenotes], staffnotes[current_notes].getBeats());
                            formerposition = decidePositonX(noteImages[j][currentlinenotes],staffWidth,staffnotes[current_notes-1].getBeats(),formerposition,currentline); //currentline是谱当前的行数
                            decidePostionY(noteImages[j][currentlinenotes], staffnotes[current_notes].getPitch(), currentline, staffHeight);
                            System.out.println(formerposition);
                            noteImages[j][currentlinenotes].setVisibility(View.VISIBLE);

                            currentcolumnbeats += staffnotes[current_notes].getBeats();
                            current_notes++;
                            currentlinenotes++;

                        }
                        column++;
                        currentlinenotes++;
                    }
                }
            }
            numofremainstaff =  numofremainstaff-128;

        }



        /*int i = 0;
        noteImages[i][i].setVisibility(View.VISIBLE);
        noteImages[i][i].setX(staff[0].getX()+(520-18)*staffWidth/840);
        Log.d("abc",Integer.toString((int)staff[0].getX()));
        noteImages[++i][i].setY(staff[0].getY()+8*staffHeight/184);
        noteImages[i][i].setX(staff[1].getX()+(520-18)*staffWidth/840);
        noteImages[i][i].setVisibility(View.VISIBLE);
        noteImages[i][i].setY(staff[1].getY()+8*staffHeight/184);*/

    }
    public Note[] initialNote(){

        Note[] notes = new Note[17];
        notes[0] = new Note(54, 2);
        notes[1] = new Note(54,1);
        notes[2] = new Note(54, 1);
        notes[3] = new Note(55,2);
        notes[4] = new Note(56, 2);
        notes[5] = new Note(54, 4);
        notes[6] = new Note(53, 4);
        notes[7] = new Note(52, 2);
        notes[8] = new Note(52, 1);
        notes[9] = new Note(52, 1);
        notes[10] = new Note(53, 2);
        notes[11] = new Note(54, 2);
        notes[12] = new Note(54, 4);
        notes[13] = new Note(52, 4);
        notes[14] = new Note(52, 2);
        notes[15] = new Note(56, 2);
        notes[16] = new Note(55, 4);
        return notes;
    }

    public void initialNoteImages(){

        noteImages[0][0] = (ImageView)findViewById(R.id.note10);
        noteImages[0][1] = (ImageView)findViewById(R.id.note11);
        noteImages[0][2]= (ImageView)findViewById(R.id.note12);
        noteImages[0][3]= (ImageView)findViewById(R.id.note13);
        noteImages[0][4] = (ImageView)findViewById(R.id.note14);
        noteImages[0][5] = (ImageView)findViewById(R.id.note15);
        noteImages[0][6]= (ImageView)findViewById(R.id.note16);
        noteImages[0][7]= (ImageView)findViewById(R.id.note17);
        noteImages[1][0] = (ImageView)findViewById(R.id.note20);
        noteImages[1][1] = (ImageView)findViewById(R.id.note21);
        noteImages[1][2]= (ImageView)findViewById(R.id.note22);
        noteImages[1][3]= (ImageView)findViewById(R.id.note23);
        noteImages[1][4] = (ImageView)findViewById(R.id.note24);
        noteImages[1][5] = (ImageView)findViewById(R.id.note25);
        noteImages[1][6]= (ImageView)findViewById(R.id.note26);
        noteImages[1][7]= (ImageView)findViewById(R.id.note27);
        noteImages[2][0] = (ImageView)findViewById(R.id.note30);
        noteImages[2][1] = (ImageView)findViewById(R.id.note31);
        noteImages[2][2]= (ImageView)findViewById(R.id.note32);
        noteImages[2][3]= (ImageView)findViewById(R.id.note33);
        noteImages[2][4] = (ImageView)findViewById(R.id.note24);
        noteImages[2][5] = (ImageView)findViewById(R.id.note25);
        noteImages[2][6]= (ImageView)findViewById(R.id.note26);
        noteImages[2][7]= (ImageView)findViewById(R.id.note27);
        noteImages[3][0] = (ImageView)findViewById(R.id.note30);
        noteImages[3][1] = (ImageView)findViewById(R.id.note31);
        noteImages[3][2]= (ImageView)findViewById(R.id.note32);
        noteImages[3][3]= (ImageView)findViewById(R.id.note33);
        noteImages[3][4] = (ImageView)findViewById(R.id.note34);
        noteImages[3][5] = (ImageView)findViewById(R.id.note35);
        noteImages[3][6]= (ImageView)findViewById(R.id.note36);
        noteImages[3][7]= (ImageView)findViewById(R.id.note37);

        staff[0] = (ImageView)findViewById(R.id.imgView_staff);
        staff[1] = (ImageView)findViewById(R.id.imgView_staff2);
        staff[2] = (ImageView)findViewById(R.id.imgView_staff3);
        staff[3] = (ImageView)findViewById(R.id.imgView_staff4);
    }
    public void makeInvisible(){
        for(int i=0; i<numofline-3;i++ ){
            staff[i].setVisibility(View.INVISIBLE);
            for(int j=0;j<num_beats;j++){
                noteImages[i][j].setVisibility(View.INVISIBLE);
            }
        }

    }
    public ImageView decideNote(ImageView img, int beat){
        switch (beat){
            case 1: img.setImageResource(R.drawable.eighth);break;
            case 2: img.setImageResource(R.drawable.quarter);break;
            case 4: img.setImageResource(R.drawable.half);break;
            case 8: img.setImageResource(R.drawable.whole);break;
        }
        return  img;
    }
    public void decidePostionY(ImageView img,int pitch, int staffline, int staffHeight){
        img.setY(staff[staffline].getY()+(float)(BASE_Y-pitch)*H_INTERVAL*staffHeight/184);
        //img.setY(staff[staffline].getY()+8*staffHeight/184);
        System.out.println(staff[staffline].getY()+(float)(BASE_Y-pitch)*H_INTERVAL/184);
    }
    public float decidePositonX(ImageView img, int staffWidth, int beat, float formerposition, int staffline){

        float position = formerposition+W_INTERVAL*beat*staffWidth/840;
       // position = staff[staffline].getX()+(85+18*beat)*staffWidth/840;
        img.setX(position);
        return position;
    }
    public int decideBeginX(int column){
        int baseX = 0;
        switch (column){
            case 1: baseX = FIRST; break;
            case 2: baseX = SECOND; break;
            case 3: baseX = THIRD; break;
            case 4: baseX = FOURTH; break;
        }
        return baseX;
    }

    /*public void intialAccImages(int i){

        noteImages[i][0].setImageResource(R.drawable.eighth);
        noteImages[i][1].setImageResource(R.drawable.quarter);
        noteImages[i][2].setImageResource(R.drawable.half);
        noteImages[i][3].setImageResource(R.drawable.whole);
    }*/
    /*public void initializeImageViews()
    {
        noteImages[0] = (ImageView)findViewById(R.id.Note0);
        noteImages[1] = (ImageView)findViewById(R.id.Note1);
        noteImages[1].setScaleX((float)0.5);
        noteImages[1].setScaleY((float)0.5);
        noteImages[2] = (ImageView)findViewById(R.id.Note2);
        noteImages[3] = (ImageView)findViewById(R.id.Note3);
        noteImages[4] = (ImageView)findViewById(R.id.Note4);
        noteImages[5] = (ImageView)findViewById(R.id.Note5);
        noteImages[6] = (ImageView)findViewById(R.id.Note6);
        noteImages[7] = (ImageView)findViewById(R.id.Note7);

        accImages[0] = (ImageView)findViewById(R.id.Accidental0);
        accImages[1] = (ImageView)findViewById(R.id.Accidental1);
        accImages[2] = (ImageView)findViewById(R.id.Accidental2);
        accImages[3] = (ImageView)findViewById(R.id.Accidental3);
        accImages[4] = (ImageView)findViewById(R.id.Accidental4);
        accImages[5] = (ImageView)findViewById(R.id.Accidental5);
        accImages[6] = (ImageView)findViewById(R.id.Accidental6);
        accImages[7] = (ImageView)findViewById(R.id.Accidental7);
    }
<<<<<<< HEAD:app/src/main/java/com/sustech/se/scoree/ShowFace.java
    // Returns the length in eighth notes
    public int fractionToInt(String fraction)
    {
        int intValue = 0;

        switch(fraction)
        {
            case "1" : intValue = 8; break;
            case "1/2" : intValue = 4; break;
            case "1/4" : intValue = 2; break;
            case "1/8" : intValue = 1; break;
        }

        return intValue;
    }
    //front end method to update the staff by a single note
    public void updateNote(int slot, ImageView staff, Note n){
        ImageView note = noteImages[slot];
        ImageView imgAccidental = accImages[slot];
        int staffWidth = staff.getWidth();
        int staffHeight = staff.getHeight();
        int intervalY = (int) (staffHeight * 0.043);
        int staffY = (int) staff.getY();
        int gY = (int) (staff.getY() - staffHeight * 0.09);

        int height = (int) (staff.getHeight() * 0.55);
        int width = (int) (staff.getWidth() * 0.055);
        Log.i("height", width + " " + height);



        Log.i("pitch",n.pitchStr);
        switch (n.pitchStr)
        {
            case "G5" : note.setY(gY); break;
            case "F5" : note.setY(gY + intervalY * 1f); break;
            case "E5" : note.setY(gY + intervalY * 2f); break;
            case "D5" : note.setY(gY + intervalY * 3f); break;
            case "C5" : note.setY(gY + intervalY * 4f); break;
            case "B5" : note.setY(gY + intervalY * 5f); break;
            case "A5" : note.setY(gY + intervalY * 6f); break;
            case "G4" : note.setY(gY + intervalY * 7f); break;
            case "F4" : note.setY(gY + intervalY * 8f); break;
            case "E4" : note.setY(gY + intervalY * 9f); break;
            case "D4" : note.setY(gY + intervalY * 10f); break;
            case "Rest" : note.setY(staff.getY()); note.setImageResource(R.drawable.wholerest); Log.i("rest",""); break;
            default : break;
        }

        if(n.pitchStr.equals("Rest") || n.pitchLetter == 'R')
        {
            switch(n.beatsToFraction())
            {
                case "1" : note.setImageResource(R.drawable.wholerest); break;
                case "1/2" : note.setImageResource(R.drawable.halfrest); break;
                case "1/4" : note.setImageResource(R.drawable.quarterrest); break;
                case "1/8" : note.setImageResource(R.drawable.eighthrest); break;
            }

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) note.getLayoutParams();
            params.height = staffHeight;
            note.setLayoutParams(params);
            note.setVisibility(View.VISIBLE);
            note.setY(staffY);
            Log.i("note height width", note.getHeight() + " " + note.getWidth() + " " + staffHeight + " " + staffWidth + " " + note.getY() + " " + staff.getY());
        }
        else
        {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) note.getLayoutParams();
            params.height = height;
            params.width = width;
            note.setLayoutParams(params);
            note.setVisibility(View.VISIBLE);

            switch(n.beatsToFraction())
            {
                case "1" : note.setImageResource(R.drawable.whole); break;
                case "1/2" : note.setImageResource(R.drawable.half); break;
                case "1/4" : note.setImageResource(R.drawable.quarter); break;
                case "1/8" : note.setImageResource(R.drawable.eighth); break;
            }
        }

        FrameLayout.LayoutParams accparams = (FrameLayout.LayoutParams) imgAccidental.getLayoutParams();
        accparams.height = height;
        accparams.width = width / 2;
        imgAccidental.setLayoutParams(accparams);

        Log.i("accidental", n.accidental.toString());
        switch(n.accidental)
        {
            case Natural : imgAccidental.setVisibility(ImageView.INVISIBLE); break;
            case Flat : imgAccidental.setVisibility(ImageView.VISIBLE); imgAccidental.setImageResource(R.drawable.flat); break;
            case Sharp : imgAccidental.setVisibility(ImageView.VISIBLE); imgAccidental.setImageResource(R.drawable.sharp); break;
        }
        imgAccidental.setY(note.getY());
    }*/
}
=======
}
>>>>>>> parent of 68837b6... Merge pull request #3 from liaoweiduo/pr/2:app/src/main/java/com/sustech/se/scoree/MainActivity.java
