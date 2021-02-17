package com.technova.help;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GiveHelpActivity extends AppCompatActivity {

    TabLayout tabLayout ;
    ViewPager viewPager ;
    GiveFragmentAdapterClass fragmentAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_help);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_GiveHelp);
        viewPager = (ViewPager) findViewById(R.id.pagerGiveHelp);

        tabLayout.addTab(tabLayout.newTab().setText("LIST"));
        tabLayout.addTab(tabLayout.newTab().setText("MAP"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        fragmentAdapter = new GiveFragmentAdapterClass(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(fragmentAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab LayoutTab) {

                viewPager.setCurrentItem(LayoutTab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab LayoutTab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab LayoutTab) {

            }
        });

    }
}
