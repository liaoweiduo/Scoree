package com.sustech.se.scoree.UI;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Px;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import devlight.io.library.ntb.NavigationTabBar;

import com.sustech.se.scoree.Data;
import com.sustech.se.scoree.FileOperator;
import com.sustech.se.scoree.R;
import com.sustech.se.scoree.SDCardHelper;
import com.sustech.se.scoree.Song;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final String PERMISSION_WRITE="android.permission.WRITE_EXTERNAL_STORAGE";
    private Data gData;
    private View mainView;
    private View settingView;
    public static final String SONG = "com.sustech.se.scoree.SONG";
    private Song[] songsList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gData = ((Data) getApplicationContext());
        mainView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_main, null, false);
        settingView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_setting, null, false);
        saveInitFiles("youandme");
        saveInitFiles("chongerfei");
        saveInitFiles("qianyuqianxun");
        saveInitFiles("qianlizhiwai");
        saveInitFiles("huanlesong");

        String[] songNameList = FileOperator.getSongs(getExternalFilesDir(null).getAbsolutePath() + "/" + gData.getWorkingDirectory());
        songsList = new Song[songNameList.length];
        for (int i=0;i<songNameList.length;i++) {
            Log.i("Data", "songNameList " + songNameList[i]);
            songsList[i] = FileOperator.getSongFromInputStream(
                    getExternalFilesDir(null).getAbsolutePath() + "/" +
                            gData.getWorkingDirectory() + "/" + songNameList[i] + "/" + songNameList[i] + ".txt");
        }
        gData.setSongs(songsList);
        initUI();
    }

    private void initUI() {
        //init container
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_vertical_ntb);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                if (position == 0) {    //main face and show face
                    initMainFace();
                    container.addView(mainView);
                    return mainView;
                } else {  //setting face
                    initSettingFace();
                    container.addView(settingView);
                    return settingView;
                }
            }
        });

        //init navigation tab bar

        final String[] colors = getResources().getStringArray(R.array.vertical_ntb);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_vertical);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_main),
                        Color.parseColor(colors[0]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_main))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_setting),
                        Color.parseColor(colors[1]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_setting))
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
    }

    private void initMainFace() {
        Button bt = (Button) mainView.findViewById(R.id.bt_search);
        bt.setOnClickListener(this);

        final RecyclerView recyclerView = (RecyclerView) mainView.findViewById(R.id.rv_show_score);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false
                )
        );
        recyclerView.setAdapter(new RecycleAdapter());
    }

    private void initSettingFace() {
        RadioGroup rg = (RadioGroup) settingView.findViewById(R.id.rg_page_turn);
        Spinner sp_num_of_lines = (Spinner) settingView.findViewById(R.id.sp_num_of_lines);
        Spinner sp_split_buffer = (Spinner) settingView.findViewById(R.id.sp_split_buffer);

        List<String> spl = new ArrayList<>();
        for(int i = 5 ;i>=2;i--)
            spl.add(String.valueOf(i));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spl);
        sp_num_of_lines.setAdapter(adapter);

        resetSpSplitAdapter();
        int numOfLines = gData.getNumOfLines();
        int pageTurnSetting = gData.getPageTurnSetting();
        sp_num_of_lines.setSelection(getResources().getStringArray(R.array.spa_num_of_lines).length + 1 - numOfLines);
        if (pageTurnSetting == numOfLines) {
            rg.check(R.id.rb_normal);
            sp_split_buffer.setSelection(0);
        } else if (pageTurnSetting < numOfLines) {
            rg.check(R.id.rb_split);
            sp_split_buffer.setSelection(numOfLines - pageTurnSetting - 1);
        }
        rg.setOnCheckedChangeListener(this);
        sp_num_of_lines.setOnItemSelectedListener(this);
        sp_split_buffer.setOnItemSelectedListener(this);
    }

    private void resetSpSplitAdapter() {
        Spinner sp = (Spinner) settingView.findViewById(R.id.sp_split_buffer);
        List<String> spl = new ArrayList<>();
        for (int i = gData.getNumOfLines() - 1; i > 0; i--) spl.add(String.valueOf(i));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spl);

        sp.setAdapter(adapter);
    }

    private void resetScoreListAdapter() {
        final RecyclerView recyclerView = (RecyclerView) mainView.findViewById(R.id.rv_show_score);
        recyclerView.setAdapter(new RecycleAdapter());
    }

    private Song[] getMatchResult(Pattern pattern) {    //从private file里读取song列表依次和pattern比较，result是true的话就加到Song[]里
//        getResources().openRawResource(R.raw.)

        Log.i("getMatchResult","pattern： " + pattern.toString());
        Song[] songs = gData.getSongs();
        boolean[] match = new boolean[songs.length];
        int numOfMatch=0;
        for (int i = 0 ;i < songs.length; i++) {
            Matcher matcher = pattern.matcher(songs[i].getName());
            match[i] = false;
            if (matcher.matches()){
                match[i] = true;
                numOfMatch++;
            }
        }
        if (numOfMatch == 0){
            return null;
        }
        Song[] result = new Song[numOfMatch];
        int indexOfResult = 0;
        for (int i = 0;i < songs.length ; i++) {
            if (match[i]){
                result[indexOfResult++] = songs[i];
            }
        }
        Log.i("getMatchResult","get " + numOfMatch + " result.(" + indexOfResult + ")");
        return result;
    }

    private void saveInitFiles(String songName){
        Log.i("saveInitFiles", "fileList " +fileList().length);

        String songPath = gData.getWorkingDirectory() + "/" + songName;
        String txtPath = songPath + "/" + songName + ".txt";
        String imgPath = songPath + "/" + songName + "_";
        try {
            File file = new File(getExternalFilesDir(null).getAbsolutePath() + "/" + gData.getWorkingDirectory());
            if (!file.exists()) Log.i("saveInitFiles","staff path: " + file.getAbsolutePath() + " create " + file.mkdir());
            file = new File(getExternalFilesDir(null).getAbsolutePath() + "/" + songPath);
            if (!file.exists()) Log.i("saveInitFiles","songDir path:" + file.getAbsolutePath() + "create " + file.mkdir());

            // 保存 txt
            if (!SDCardHelper.isFileExist(getExternalFilesDir(null).getAbsolutePath() + "/" + txtPath)) {
                InputStream is = getAssets().open("songs/" + songName + ".txt");
                int length = is.available();
                byte[] buffer = new byte[length];
                is.read(buffer);
                if(SDCardHelper.saveFileToSDCardPrivateFilesDir(buffer, null, txtPath, this)){
                    Log.i("saveInitFiles", "txt file create " +
                            String.valueOf(SDCardHelper.isFileExist(getExternalFilesDir(null).getAbsolutePath() + "/" + txtPath)));
                }
                is.close();
            }

            //保存图片
            Song song = FileOperator.getSongFromInputStream(new FileInputStream(
                    new File(getExternalFilesDir(null).getAbsolutePath() + "/" + txtPath)));
            for (int i=0;i<song.getNumOfLine()+1;i++){
                InputStream is = getAssets().open("songs/" + songName + "_" + i + ".png");
                int length = is.available();
                byte[] buffer = new byte[length];
                is.read(buffer);
                if(SDCardHelper.saveFileToSDCardPrivateFilesDir(buffer, null, imgPath + i + ".png", this)){
                    Log.i("saveInitFiles", "img file " + i + " create " +
                            String.valueOf(SDCardHelper.isFileExist(getExternalFilesDir(null).getAbsolutePath() + "/" + imgPath + i + ".png")));
                }
                is.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_search) {
            String et_value = ((EditText) mainView.findViewById(R.id.et_search)).getText().toString();
            Pattern pattern = Pattern.compile(".*" + et_value + ".*");
            songsList = getMatchResult(pattern);
            if (songsList != null)
                for (Song song : songsList){
                    Log.i("onClick","song: "+song.getName());
                }
            resetScoreListAdapter();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.rg_page_turn) {
            if (checkedId == R.id.rb_normal) {
                gData.setPageTurnSetting(gData.getNumOfLines());
            } else if (checkedId == R.id.rb_split) {
                Spinner sp = (Spinner) settingView.findViewById(R.id.sp_split_buffer);
                gData.setPageTurnSetting(Integer.parseInt((String) sp.getSelectedItem()));
            }
        }
        Log.i("onCheckedChanged", "numOfLines=" + gData.getNumOfLines() + " PageTurnSetting=" + gData.getPageTurnSetting());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_num_of_lines) {
            String selectedItem = (String) parent.getSelectedItem();
            int oldNum = gData.getNumOfLines();
            if (oldNum != Integer.parseInt(selectedItem)) {
                gData.setNumOfLines(Integer.parseInt(selectedItem));
                if (((RadioButton) settingView.findViewById(R.id.rb_normal)).isChecked())
                    gData.setPageTurnSetting(Integer.parseInt(selectedItem));
                resetSpSplitAdapter();
            }
        } else if (parent.getId() == R.id.sp_split_buffer && ((RadioButton) settingView.findViewById(R.id.rb_split)).isChecked()) {   //radio button选分屏时
            String selectedItem = (String) parent.getSelectedItem();
            gData.setPageTurnSetting(Integer.parseInt(selectedItem));
        }
        Log.i("onItemSelected", "parent=" + ((parent.getId() == R.id.sp_num_of_lines) ? "num of lines" : "split buffer") + " numOfLines=" + gData.getNumOfLines() + " PageTurnSetting=" + gData.getPageTurnSetting());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onStop() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("numOfLines", gData.getNumOfLines());
        editor.putInt("pageTurnSetting", gData.getPageTurnSetting());
        editor.commit();
        Log.i("mainActivity", "onStop called");
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_list_score, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    Log.i("Recycleadapter on Click","View:" + v.toString() + "/nposition = " + position);
                    Intent intent = new Intent(MainActivity.this, showActivity.class);
                    intent.putExtra(SONG, songsList[position].getFilename());
                    startActivity(intent);
                }
            });
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {  //设置text的内容
            holder.txt.setText(songsList[position].getName());
            holder.fl.setTag(position);
        }

        @Override
        public int getItemCount() {  //设置list总行数
            Log.i("getItemCount","get " + (songsList == null?0:songsList.length) + " 行");
            if (songsList == null) return 0;
            return songsList.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txt;
            public FrameLayout fl;

            public ViewHolder(final View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.text_score);
                fl = (FrameLayout) itemView.findViewById(R.id.fl_score);
            }
        }
    }
}
