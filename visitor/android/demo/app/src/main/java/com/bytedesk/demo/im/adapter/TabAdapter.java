package com.bytedesk.demo.im.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;

    public TabAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
        super(fm);
        mFragments = fragments;
    }
    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
}

