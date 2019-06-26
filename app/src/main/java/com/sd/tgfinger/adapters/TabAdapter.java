package com.sd.tgfinger.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created By pq
 * on 2019/4/1
 */
public class TabAdapter extends FragmentStatePagerAdapter {

    private List<String> titles;
    private List<Fragment> frags;

    public TabAdapter(FragmentManager fm, List<String> mTitle, List<Fragment> fragments) {
        super(fm);
        this.titles = mTitle;
        this.frags = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return frags.get(position);
    }

    @Override
    public int getCount() {
        return frags.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
