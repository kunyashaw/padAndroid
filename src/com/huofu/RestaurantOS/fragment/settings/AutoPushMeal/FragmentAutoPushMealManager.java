package com.huofu.RestaurantOS.fragment.settings.AutoPushMeal;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.adapter.ExpandableListviewSettingAdapter;
import com.huofu.RestaurantOS.fragment.FragmentCustomedManager;

/**
 * author: Created by zzl on 15/8/28.
 */
public class FragmentAutoPushMealManager {

    public static Fragment fragmentAutoPushMeal = null;
    public static FragmentCustomedManager fcm;
    public static FragmentManager fragmentManager;
    public static Handler handler = null;
    public static ExpandableListviewSettingAdapter settingAdapter;
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
    public static void showfragmentAutoPushMeal() {
        fragmentAutoPushMeal = new FragmentAutoPushMeal(handler, MainApplication.getContext());
        fcm.showFragment(fragmentAutoPushMeal, false);
    }
}
