package com.sustech.se.scoree.UI;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

import com.sustech.se.scoree.Data;
import com.sustech.se.scoree.R;

/**
 * Created by GIGAMOLE on 28.03.2016.
 */
public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    Data gData;
    View settingView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gData = ((Data) getApplicationContext());
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
                    final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_vp, null, false);

                    final TextView txtPage = (TextView) view.findViewById(R.id.txt_vp_item_page);
                    txtPage.setText(String.format("Page #%d", position));

                    container.addView(view);
                    return view;
                } else {  //setting face
                    initSettingFace(settingView);
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

    private void initSettingFace(View view) {
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg_page_turn);
        rg.setOnCheckedChangeListener(this);
        Spinner sp = (Spinner) view.findViewById(R.id.sp_split_buffer);
        sp.setOnItemSelectedListener(this);
        int pageTurnSetting = gData.getPageTurnSetting();
        if (pageTurnSetting == 0) {
            rg.check(R.id.rb_normal);
        } else {
            rg.check(R.id.rb_split);
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.rg_page_turn){
            Spinner sp = (Spinner) settingView.findViewById(R.id.sp_split_buffer);
            if (checkedId == R.id.rb_normal){
                gData.setPageTurnSetting(0);
                sp.setOnItemSelectedListener(null);
            }else if (checkedId == R.id.rb_split){
                gData.setPageTurnSetting(Integer.parseInt((String) sp.getSelectedItem()));
                sp.setOnItemSelectedListener(this);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_split_buffer && gData.getPageTurnSetting() != 0){
            String selectedItem = (String) parent.getSelectedItem();
            if ("1".equals(selectedItem)){
                gData.setPageTurnSetting(1);
            }else if ("2".equals(selectedItem)){
                gData.setPageTurnSetting(2);
            }else if ("3".equals(selectedItem)){
                gData.setPageTurnSetting(3);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
    }
}
