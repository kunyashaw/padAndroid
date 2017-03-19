package com.huofu.RestaurantOS.fragment.settings.Delivery;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Handler;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.adapter.ListviewSettingDeliveryTimebucketAdapter;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.storeOrder.DeliveryBuilding;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.fragment.FragmentCustomedManager;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/28.
 */
public class FragmentsDeliverySettingManager {

    public static Fragment fragmentDeiveryChooseSet = null;

    public static Fragment fragmentBuildingChooseSet = null;
    public static Fragment fragmentDeliveryBuildingAdd = null;
    public static Fragment fragmentDeliveryBuildingEdit = null;

    public static Fragment fragmentDeliveryBucketChooseSet = null;

    public static Fragment fragmentDeliveryDetailChoose = null;

    public static FragmentCustomedManager fcm;
    public static FragmentManager fragmentManager;
    public static Handler handler = null;
    public static List<MealBucket> mealBucketList;
    public static List<DeliveryBuilding> listDeliveryBuilding;
    public static ListviewSettingDeliveryTimebucketAdapter list_setting_delivery_adapter;

    public static int position= 0;//编辑外送楼宇的时候，用来记录是编辑的哪个
    public static int store_id = 0;//选择的店铺id
    public static String stroe_name = "";//选择的店铺名称


    /***
     * fragmentsAllManager初始化
     */
    public static void init(FragmentManager fm, Handler mUiHandler) {
        fragmentManager = fm;
        mealBucketList = new ArrayList<MealBucket>();
        listDeliveryBuilding = new ArrayList<DeliveryBuilding>();

        fcm = new FragmentCustomedManager(fragmentManager);
        handler = mUiHandler;
        Context ctxt = MainApplication.getContext();
        ApisManager.GetTimeBucketList(new ApiCallback() {
            @Override
            public void success(Object object) {
                mealBucketList = (List<MealBucket>)object;
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.sendMsg(response.error_message, SettingsActivity.SHOW_ERROR_MESSAGE,handler);
            }
        });

        ApisManager.GetStoreDeliveryBuildingsList(LocalDataDeal.readFromLocalStoreId(ctxt), new ApiCallback() {
            @Override
            public void success(Object object) {
                listDeliveryBuilding = (List<DeliveryBuilding>) object;
            }

            @Override
            public void error(BaseApi.ApiResponse response) {

            }
        });

        store_id = LocalDataDeal.readFromLocalStoreId(ctxt);
        stroe_name = LocalDataDeal.readFromLocalStoreName(ctxt);
    }

    /***
     * 显示外送设置的主界面
     */
    public static void showDeliveryChooseSet()
    {
        fragmentDeiveryChooseSet = new FragmentDeliveryChooseSet(MainApplication.getContext(),handler);
        fcm.showFragment(fragmentDeiveryChooseSet, false);
    }

    /***
     * 显示外送楼宇列表
     */
    public static void showDeliveryBuildingList()
    {
        fragmentBuildingChooseSet = new FragmentDeliveryBuildingsChooseSet(MainApplication.getContext(),handler);
        fcm.showFragment(fragmentBuildingChooseSet, false);
    }

    /***
     * 显示外送楼宇添加界面
     */
    public static void showDeliveryBuildingAdd()
    {
        fragmentDeliveryBuildingAdd = new FragmentDeliveryBuildingAdd(MainApplication.getContext(),handler);
        fcm.showFragment(fragmentDeliveryBuildingAdd,false);
    }

    /***
     * 显示外送楼宇的编辑界面
     */
    public static void showDeliveryBuildingEdit()
    {
        fragmentDeliveryBuildingEdit = new FragmentDeliveryBuildingEdit(MainApplication.getContext(),handler,position);
        fcm.showFragment(fragmentDeliveryBuildingEdit,false);
    }

    /**
     * 显示营业时间段
     */
    public static void showDeliveryTimeBucketList()
    {
        fragmentDeliveryBucketChooseSet = new FragmentDeliveryBucketChooseSet(MainApplication.getContext(),handler);
        fcm.showFragment(fragmentDeliveryBucketChooseSet,false);
    }

    public static void showDeliveyDetialSet()
    {
        fragmentDeliveryDetailChoose = new FragmentDeliveryDetailSet(MainApplication.getContext(),handler);
        fcm.showFragment(fragmentDeliveryDetailChoose,false);
    }
}
