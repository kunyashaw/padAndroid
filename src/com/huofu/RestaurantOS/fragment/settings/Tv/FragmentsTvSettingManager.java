package com.huofu.RestaurantOS.fragment.settings.Tv;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.adapter.ListviewSettingTvCallAdapter;
import com.huofu.RestaurantOS.fragment.FragmentCustomedManager;

/**
 * author: Created by zzl on 15/8/28.
 */
public class FragmentsTvSettingManager {

    public static Fragment fragmentTvIpEdit = null;
    public static Fragment fragmentTvChooseSet= null;
    public static Fragment fragmentTvCallMethodChoose = null;

    public static FragmentCustomedManager fcm;
    public static FragmentManager fragmentManager;
    public static Handler handler = null;
    public static ListviewSettingTvCallAdapter listviewSettingTvCallWayadapter;


    /***
     * fragmentsAllManager初始化
     */
    public static void init(FragmentManager fm, Handler mUiHandler) {
        fragmentManager = fm;
        fcm = new FragmentCustomedManager(fragmentManager);
        handler = mUiHandler;
    }

    /***
     * 选择要设置哪个位置的打印机
     */
    public static void showfragmentTvChooseSet() {
        fragmentTvChooseSet = new FragmentTvChooseSet();
        fcm.showFragment(fragmentTvChooseSet, false);
    }


    /***
     * 显示ip地址列表
     */
    public static void showFragmentTvIpEdit() {
        fragmentTvIpEdit = new FragmentTvIpEdit(handler, MainApplication.getContext());
        fcm.showFragment(fragmentTvIpEdit, false);
    }

    /**
     * 编辑ip地址界面
     */
    public static void showFragmentTvCallMethodChoose() {
        fragmentTvCallMethodChoose = new FragmentTvCallMethodChoose(handler,MainApplication.getContext());
        fcm.showFragment(fragmentTvCallMethodChoose, false);
    }

}
