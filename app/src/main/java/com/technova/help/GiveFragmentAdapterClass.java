package com.technova.help;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by N on 9/25/2017.
 */

public class GiveFragmentAdapterClass  extends FragmentStatePagerAdapter {

    int TabCount;

    public GiveFragmentAdapterClass(FragmentManager fragmentManager, int CountTabs) {

        super(fragmentManager);

        this.TabCount = CountTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Tab_List_Fragment tab1 = new Tab_List_Fragment();
                return tab1;

            case 1:
                Tab_Map_Fragment tab2 = new Tab_Map_Fragment();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TabCount;
    }
}