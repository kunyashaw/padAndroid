package com.huofu.RestaurantOS.fragment.settings.Printer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;

import com.huofu.RestaurantOS.adapter.ListviewSettingPrinterlistAdapter;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.fragment.FragmentCustomedManager;

import java.util.List;

/**
 * author: Created by zzl on 15/8/27.
 */
public class FragmentsPrinterSettingManager {

    public static Fragment fragmentPrinterIpList = null;
    public static Fragment fragmentPrinterIpEdit = null;
    public static Fragment fragmentPrinterChooseSet = null;
    public static Fragment fragmentPrinterIpAdd = null;

    public static FragmentCustomedManager fcm;
    public static FragmentManager fragmentManager;
    public static Handler handler = null;

    public static List<peripheral> listPeripheralPrinter;
    public static ListviewSettingPrinterlistAdapter list_printer_info_adapter;

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
    public static void showfragmentPrinterChooseSet(List<peripheral> list) {
        listPeripheralPrinter = list;
        fragmentPrinterChooseSet = new FragmentPrinterChooseSet(list);
        fcm.showFragment(fragmentPrinterChooseSet, false);
    }


    /***
     * 显示ip地址列表
     */
    public static void showFragmentPrinterIpList(int index) {
        fragmentPrinterIpList = new FragmentPrinterIpList(index, listPeripheralPrinter, handler);
        fcm.showFragment(fragmentPrinterIpList, false);
    }

    /**
     * 编辑ip地址界面
     */
    public static void showFragmentPrinterEdit(int index) {
        fragmentPrinterIpEdit = new FragmentPrinterIpEdit(index, listPeripheralPrinter);
        fcm.showFragment(fragmentPrinterIpEdit, false);
    }


    /***
     * 显示打印机新增界面
     *
     * @param index
     */
    public static void showFragmentPrinterAdd(int index) {
        fragmentPrinterIpAdd = new FragmentPrinterIpAdd(index, listPeripheralPrinter);
        fcm.showFragment(fragmentPrinterIpAdd, false);
    }


}

