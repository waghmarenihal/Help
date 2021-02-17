package com.technova.help;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by N on 12/14/2017.
 */

public class FollowMeFragmentAdapterClass extends FragmentStatePagerAdapter {

    int TabCount;

    public FollowMeFragmentAdapterClass(FragmentManager fragmentManager, int CountTabs) {

        super(fragmentManager);

        this.TabCount = CountTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Tab_Follow_Contact_Fragment tab1 = new Tab_Follow_Contact_Fragment();
                return tab1;

            case 1:
                Tab_Follow_Req_Fragment tab2 = new Tab_Follow_Req_Fragment();
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
