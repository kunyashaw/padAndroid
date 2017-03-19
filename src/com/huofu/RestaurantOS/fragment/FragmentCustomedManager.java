package com.huofu.RestaurantOS.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.huofu.RestaurantOS.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/26.
 */
public class FragmentCustomedManager {

    List<Fragment> listAllFragment;
    FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;

    public FragmentCustomedManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        listAllFragment = new ArrayList<Fragment>();
        fragmentTransaction = fragmentManager.beginTransaction();
    }

    /***
     * 显示fragment
     *
     * @param fragment 要显示的fragment
     * @param flagShow 是否直接显示，决定是add还是show
     */
    public void showFragment(Fragment fragment, boolean flagShow) {
        hideFragment();

        fragmentTransaction = fragmentManager.beginTransaction();
        boolean flagAdd = true;
        for (int k = 0; k < listAllFragment.size(); k++) {
            if (listAllFragment.get(k).getId() == fragment.getId()) {
                flagAdd = false;
                break;
            }
        }
        if (flagAdd) {
            listAllFragment.add(fragment);
        }
        fragmentTransaction.add(R.id.framelayout_setting, fragment);
        fragmentTransaction.commit();
    }

    /***
     * 显示fragment
     *
     * @param fragment 要显示的fragment
     * @param flagShow 是否直接显示，决定是add还是show
     */
    public void showFragment(Fragment fragment, boolean flagShow,int id) {
        hideFragment();

        fragmentTransaction = fragmentManager.beginTransaction();
        boolean flagAdd = true;
        for (int k = 0; k < listAllFragment.size(); k++) {
            if (listAllFragment.get(k).getId() == fragment.getId()) {
                flagAdd = false;
                break;
            }
        }
        if (flagAdd) {
            listAllFragment.add(fragment);
        }
        fragmentTransaction.add(id, fragment);
        fragmentTransaction.commit();
    }


    /***
     * 隐藏所有的fragment，否则会出现重叠
     */
    public void hideFragment() {
        for (int k = 0; k < listAllFragment.size(); k++) {
            Fragment fragment = listAllFragment.get(k);
            //if (fragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment).commit();
            //}
        }
        listAllFragment.clear();
    }


    /***
     * 清空所有的fragments
     */
    public void deleteAllFragments() {
        for (Fragment fragment : listAllFragment) {
            try {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment).commit();
            } catch (Exception e) {

            }

        }
    }

}
