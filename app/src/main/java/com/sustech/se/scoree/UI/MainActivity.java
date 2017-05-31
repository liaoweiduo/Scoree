package com.sustech.se.scoree.UI;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import devlight.io.library.ntb.NavigationTabBar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sustech.se.scoree.Data;
import com.sustech.se.scoree.Note;
import com.sustech.se.scoree.R;
import com.sustech.se.scoree.Song;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Data gData;
    private View mainView;
    private View settingView;
    public static final String SONG = "com.sustech.se.scoree.SONG";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gData = ((Data) getApplicationContext());
        mainView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_main, null, false);
        settingView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_setting, null, false);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spl);

        sp.setAdapter(adapter);
    }

    private void resetScoreListAdapter() {
        final RecyclerView recyclerView = (RecyclerView) mainView.findViewById(R.id.rv_show_score);
        recyclerView.setAdapter(new RecycleAdapter());
    }

    private Song[] getMatchResult(Pattern pattern) {    //从private file里读取song列表依次和pattern比较，result是true的话就加到Song[]里
//        getResources().openRawResource(R.raw.)

        Log.i("getMatchResult","pattern： " + pattern.toString());
        Song[] ss = new Song[5];
        boolean[] match = new boolean[5];
        ss[0] = new Song("12",1,new Note[4],3,new int[2]);
        ss[1] = new Song("23",1,new Note[4],3,new int[2]);
        ss[2] = new Song("34",1,new Note[4],3,new int[2]);
        ss[3] = new Song("45",1,new Note[4],3,new int[2]);
        ss[4] = new Song("56",1,new Note[4],3,new int[2]);
        int numOfMatch=0;
        for (int i = 0 ;i < ss.length; i++) {
            Matcher matcher = pattern.matcher(ss[i].getName());
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
        for (int i = 0;i < ss.length ; i++) {
            if (match[i]){
                result[indexOfResult++] = ss[i];
            }
        }
        Log.i("getMatchResult","get " + numOfMatch + " result.(" + indexOfResult + ")");
        return result;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_search) {
            String et_value = ((EditText) mainView.findViewById(R.id.et_search)).getText().toString();
            Pattern pattern = Pattern.compile(".*" + et_value + ".*");
            gData.setSongs(getMatchResult(pattern));
            for (Song song : gData.getSongs()){
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
//                    Intent intent = new Intent(MainActivity.class, showActivity.class);
//                    Intent i = new Intent();
//                    intent.putExtra(SONG, songs[position]);
//                    startActivity(intent);
                }
            });
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {  //设置text的内容
            holder.txt.setText(gData.getSongs()[position].getName());
            holder.fl.setTag(position);
        }

        @Override
        public int getItemCount() {  //设置list总行数
            Log.i("getItemCount","get " + (gData.getSongs() == null?0:gData.getSongs().length) + " 行");
            if (gData.getSongs() == null) return 0;
            return gData.getSongs().length;
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
