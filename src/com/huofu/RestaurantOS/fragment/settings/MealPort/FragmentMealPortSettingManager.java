package com.huofu.RestaurantOS.fragment.settings.MealPort;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;

import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.StoreMealPort;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.fragment.FragmentCustomedManager;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.List;

/**
 * author: Created by zzl on 15/10/17.
 */
public class FragmentMealPortSettingManager {
    public static Fragment fragmentMealPortList = null;
    public static Fragment fragmentMealPortEdit = null;
    public static Fragment fragmentMealPortAdd = null;
    public static Fragment fragmentMealPortPrinterList = null;
    public static Fragment fragmentMealPortCallRule = null;
    public static Fragment fragmentMealPortStampList = null;
    public static Fragment fragmentMealPortCheck = null;
    public static Fragment fragmentMealPortPeripheralEdit = null;
    public static Fragment fragmentMealPortPeripheralAdd = null;

    public static FragmentManager fragmentManager;
    public static FragmentCustomedManager fcm;
    public static Handler handler;

    public static List<StoreMealPort> listMealPorts;
    public static List<peripheral> listPeripheral;


    public static long port_id = -1;//正在查看的出餐口id
    public static String portName="";//出餐口名称
    public static String letter="";//出餐口标记
    public static long printer_peripheral_id = 0;//打印机外设id
    public static long call_peripheral_id = 0;//叫号设备外设id
    public static int call_type = -1;//叫号规则 1:自动叫号 2:手动叫号 3:尾单叫号
    public static int checkout_type = -1;//出餐模式 1:手动出餐 2:自动出餐
    public static int has_pack = -1;//0堂食 1打包


    public static int editType = -1;//0是添加 1是查看
    public static int nowChooseMealPortPositon = -1;
    public static int peripheralType = -1;//是打印机外设还是电视外设


    public static void init(FragmentManager fm, final List<peripheral> peripheralList, Handler mUiHandler) {
        fragmentManager = fm;
        fcm = new FragmentCustomedManager(fm);
        handler = mUiHandler;
        if (peripheralList.size() == 0) {
            ApisManager.GetPeripheralList(new ApiCallback() {
                @Override
                public void success(Object object) {
                    listPeripheral = (List<peripheral>) object;
                }

                @Override
                public void error(BaseApi.ApiResponse response) {

                }
            });
        } else {
            listPeripheral = peripheralList;
        }

    }

    /**
     * 显示出餐台列表
     */
    public static void showFragmentPortList() {
        fragmentMealPortList = new FragmentMealPortList(listMealPorts, handler);
        fcm.showFragment(fragmentMealPortList, false);
    }



    /**
     * 显示添加出餐口界面
     */
    public static void showFramgemtMPAdd() {
        editType = 0;
        fragmentMealPortAdd = new FragmentMealPortAdd(handler);
        fcm.showFragment(fragmentMealPortAdd, false);
    }

    public static void cleanAddedInfo()
    {
        port_id = -1;//正在查看的出餐口id
        portName="";//出餐口名称
        letter="";//出餐口标记
        printer_peripheral_id = 0;//打印机外设id
        call_peripheral_id = 0;//叫号设备外设id
        call_type = 2;//叫号规则 1:自动叫号 2:手动叫号 3:尾单叫号
        checkout_type = -1;//出餐模式 1:手动出餐 2:自动出餐
        has_pack = -1;//0堂食 1打包
    }

    /**
     * 显示外设列表
     * @param  peripheral_type 0>打印机外设 1>电视设备外设
     */
    public static void showFragmentMpPeripheralList(final int peripheral_type) {
        peripheralType = peripheral_type;
        if(peripheral_type == 1)
        {
            for (int k = 0; k < listPeripheral.size(); k++) {
                final int index = k;
                peripheral p = listPeripheral.get(index);
                String ip = p.con_id;
                p.printer_can_connect = true;
            }
            fragmentMealPortPrinterList = new FragmentMealPortPeripheralList(handler, listPeripheral,peripheral_type);
            fcm.showFragment(fragmentMealPortPrinterList, false);
            return;
        }
        CommonUtils.sendMsg("正在获取设备的状态", SettingsActivity.SHOW_LOADING_TEXT, handler);
        new Thread() {
            public void run() {

                for (int k = 0; k < listPeripheral.size(); k++) {
                    final int index = k;
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                peripheral p = listPeripheral.get(index);
                                String ip = p.con_id;
                                p.printer_can_connect = CommonUtils.executeCammand(ip);
                            } catch (Exception e) {
                            }
                        }
                    }.start();
                }

                boolean flagWait = false;
                for(int k=0;k<listPeripheral.size();k++)
                {
                    if(listPeripheral.get(k).type == 1)
                    {
                        flagWait = true;
                        break;
                    }
                }
                if(flagWait)
                {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {

                    }
                }

                CommonUtils.sendMsg("", SettingsActivity.HIDE_LOADING, handler);
                fragmentMealPortPrinterList = new FragmentMealPortPeripheralList(handler, listPeripheral,peripheral_type);
                fcm.showFragment(fragmentMealPortPrinterList, false);
            }
        }.start();
    }


    /**
     * 显示叫号规则
     */
    public static void showFragmentMPCallRule()
    {
        fragmentMealPortCallRule = new FragmentMealPortCallRule();
        fcm.showFragment(fragmentMealPortCallRule, false);
    }


    /**
     * 显示标识列表
     */
    public static void showFragmentMpStampList()
    {
        fragmentMealPortStampList = new FragmentMealPortStampList(handler);
        fcm.showFragment(fragmentMealPortStampList,false);
    }


    /**
     * 查看出餐台
     * @param index
     */
    public static void showFragmentMpMealPortCheck(int index,boolean flag)
    {
        nowChooseMealPortPositon = index;
        editType = 1;
        fragmentMealPortCheck = new FragmentMealPortCheck(index,handler,flag);
        fcm.showFragment(fragmentMealPortCheck,false);
    }

    /***
     *
     *修改外设
     */
    public static void showFragmentMealPortPeripheralEdit(int index)
    {
        fragmentMealPortPeripheralEdit = new FragmentMealPortPeripheralEdit(index);
        fcm.showFragment(fragmentMealPortPeripheralEdit,false);
    }

    /**
     * 新增外设
     */
    public static void showFragmentMealPortPeripheralAdd()
    {
        fragmentMealPortPeripheralAdd = new FragmentMealPortPeripheralAdd();
        fcm.showFragment(fragmentMealPortPeripheralAdd,false);
    }
}
