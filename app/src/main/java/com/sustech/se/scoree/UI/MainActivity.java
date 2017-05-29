package com.sustech.se.scoree.UI;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;

import com.sustech.se.scoree.Data;
import com.sustech.se.scoree.R;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    Data gData;
    View mainView;
    View settingView;

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

    private void initMainFace(){

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
        } else if (pageTurnSetting < numOfLines){
            rg.check(R.id.rb_split);
            sp_split_buffer.setSelection(numOfLines - pageTurnSetting - 1);
        }
        rg.setOnCheckedChangeListener(this);
        sp_num_of_lines.setOnItemSelectedListener(this);
        sp_split_buffer.setOnItemSelectedListener(this);
    }

    private void resetSpSplitAdapter(){
        Spinner sp = (Spinner) settingView.findViewById(R.id.sp_split_buffer);
        List<String> spl = new ArrayList<>();
        for (int i = gData.getNumOfLines() -1 ; i > 0; i--) spl.add(String.valueOf(i));
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, spl);

        sp.setAdapter(adapter);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.rg_page_turn){
            if (checkedId == R.id.rb_normal){
                gData.setPageTurnSetting(gData.getNumOfLines());
            }else if (checkedId == R.id.rb_split){
                Spinner sp = (Spinner) settingView.findViewById(R.id.sp_split_buffer);
                gData.setPageTurnSetting(Integer.parseInt((String) sp.getSelectedItem()));
            }
        }
        Log.i("onCheckedChanged","numOfLines="+gData.getNumOfLines()+" PageTurnSetting="+gData.getPageTurnSetting());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_num_of_lines){
            String selectedItem = (String) parent.getSelectedItem();
            int oldNum = gData.getNumOfLines();
            if(oldNum != Integer.parseInt(selectedItem)) {
                gData.setNumOfLines(Integer.parseInt(selectedItem));
                if (((RadioButton)settingView.findViewById(R.id.rb_normal)).isChecked())
                    gData.setPageTurnSetting(Integer.parseInt(selectedItem));
                resetSpSplitAdapter();
            }
        }else if (parent.getId() == R.id.sp_split_buffer && ((RadioButton)settingView.findViewById(R.id.rb_split)).isChecked()){   //radio button选分屏时
            String selectedItem = (String) parent.getSelectedItem();
            gData.setPageTurnSetting(Integer.parseInt(selectedItem));
        }
        Log.i("onItemSelected","parent="+((parent.getId()==R.id.sp_num_of_lines)?"num of lines":"split buffer")+" numOfLines="+gData.getNumOfLines()+" PageTurnSetting="+gData.getPageTurnSetting());
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
        Log.i("mainActivity","onStop called");
    }
}
