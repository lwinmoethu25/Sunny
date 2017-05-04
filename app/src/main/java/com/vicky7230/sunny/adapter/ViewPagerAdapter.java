package com.vicky7230.sunny.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by agrim on 2/5/17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Fragment> fragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;//highly inefficient code.......
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    public void removeFragments() {

        fragmentList.clear();
    }
}
