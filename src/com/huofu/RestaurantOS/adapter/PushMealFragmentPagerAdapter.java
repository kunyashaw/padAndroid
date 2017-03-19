package com.huofu.RestaurantOS.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * author: Created by zzl on 15/11/23.
 */
public class PushMealFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentArrayList;
    public int nowChoosPosition = 0;


    public PushMealFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragmentArrayList = fragments;
    }


    @Override
    public Fragment getItem(int i) {
        return fragmentArrayList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void updateNowChoosePosition(int position)
    {
        nowChoosPosition = position;
    }
}
