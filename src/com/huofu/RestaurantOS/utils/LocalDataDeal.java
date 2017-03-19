package com.huofu.RestaurantOS.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalDataDeal {

    public static String pathListCache = Environment.getExternalStorageDirectory() + File.separator + "huofu" + File.separator + "DataCache" + File.separator;
    public static String tag = "LocalDataDeal";


    /****
     * 将是否完成初始化写到本地
     *
     * @param ctxt
     */
    public static void writeToLocalFlagFinishInitPad(Context ctxt, Boolean flagFinishInit) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "flagFinishInit", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putBoolean("flagFinishInit", flagFinishInit);

        // 提交当前数据
        editor.commit();
    }


    /****
     * 从本地读取版本型号
     *
     * @param ctxt
     */
    public static boolean readFromLocalFlagFinishInit(Context ctxt) {
        SharedPreferences sharedPreferences = ctxt.getSharedPreferences("flagFinishInit", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return false;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        boolean flagFinishInit = sharedPreferences.getBoolean("flagFinishInit", false);
        return flagFinishInit;
    }


    /****
     * 将app版本写入本地
     *
     * @param ctxt
     * @param versionName
     */
    public static void writeToLocalVersionName(Context ctxt, String versionName) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "VerisonName", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("VerisonName", versionName);

        // 提交当前数据
        editor.commit();
    }


    /****
     * 从本地读取版本型号
     *
     * @param ctxt
     */
    public static String readFromLocalVersionName(Context ctxt) {
        SharedPreferences sharedPreferences = ctxt.getSharedPreferences("VerisonName", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String VerisonName = sharedPreferences.getString("VerisonName", "");
        return VerisonName;
    }


    /**
     * 将菜单电视IP地址写入本地
     */
    public static void writeToLocalMenuIp(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "MenuIpSetting", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("MenuIpSetting", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从入本地读取呼叫菜单电视IP地址
     */
    public static String readFromLocalMenuIp(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "MenuIpSetting", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("MenuIpSetting", "");
        return address;
    }


    /**
     * 将小票机IP地址写入本地
     */
    public static void writeToLocalPrinterIp(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "PrinterSetting", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("printerIp", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从入本地读取呼叫小票机IP地址
     */
    public static String readFromLocalPrinterIp(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "PrinterSetting", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("printerIp", "");
        return address;
    }

    /**
     * 将副本id存到本地
     */
    public static void writeToLocalClientId(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "KeySetClientId", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("client_id", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取副本id
     */
    public static String readFromLocalClientId(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "KeySetClientId", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("client_id", "");
        return address;
    }


    /**
     * 将公共密钥存到本地
     */
    public static void writeToLocalPublicKey(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "KeySetPublicKey", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("publicKey", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取公共密钥
     */
    public static String readFromLocalPublicKey(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "KeySetPublicKey", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("publicKey", "");
        return address;
    }


    /**
     * 将主密钥存到本地
     */
    public static void writeToLocalMasterKey(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "KeySetMasterKey", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("masterKey", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取主密钥
     */
    public static String readFromLocalMasterKey(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "KeySetMasterKey", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("masterKey", "");
        return address;
    }

    /**
     * 将工作密钥存到本地
     */
    public static void writeToLocalWorkKey(String arg0, long expired_time, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "KeySetWorkKey", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("workKey", arg0);
        editor.putLong("expired_time", expired_time);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取工作密钥
     */
    public static Map readFromLocalWorkKey(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "KeySetWorkKey", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String workKey = sharedPreferences.getString("workKey", "");
        Long expired_time = sharedPreferences.getLong("expired_time", -1);

        Map map = new HashMap<String, Object>();
        map.put("expired_time", expired_time);
        map.put("workKey", workKey);

        return map;
    }


    /**
     * 将登录令牌存到本地
     */
    public static void writeToLocalStaffAccessToken(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "StaffAccessToken1", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("StaffAccessToken", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取登录令牌
     */
    public static String readFromLocalStaffAccessToken(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "StaffAccessToken1", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("StaffAccessToken", "");
        return address;
    }


    /**
     * 将叫号电视IP存到本地
     */
    public static void writeToLocalCallTvIp(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "CallTvIp", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("CallTvIp", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取叫号电视的IP
     */
    public static String readFromLocalCallTvIp(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "CallTvIp", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("CallTvIp", "");
        return address;
    }


    /**
     * 将是否自动叫号存到本地,
     * flag->0 出餐不叫号
     * flag->1 出餐叫号
     * flag->2 尾单叫号
     */
    public static void writeToAutoCall(int flag, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "AutoCall", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putInt("autoCall", flag);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取是否自动叫号
     */
    public static int readFromAutoCall(Context ctxt) {

        int flag = -1;
        try {
            SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                    "AutoCall", Activity.MODE_PRIVATE);
            if (sharedPreferences == null) {
                return -1;
            }

            // 使用getString方法获得value，注意第2个参数是value的默认值
            flag = sharedPreferences.getInt("autoCall", -1);
        } catch (Exception e) {
            CommonUtils.LogWuwei(tag, "readFromAutoCall error:" + e.getMessage());
        }

        return flag;
    }


    /**
     * 将设置中是否自动出餐，存到本地
     */
    public static void writeToPushMealSetting(boolean flag, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "AutoPushMeal", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putBoolean("AutoPushMeal", flag);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取设置中是否自动出餐
     */
    public static boolean readFromPushMealSetting(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "AutoPushMeal", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return false;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        boolean flag = sharedPreferences.getBoolean("AutoPushMeal", false);
        return flag;
    }


    /**
     * 出餐页面是否选择了自动出餐，写入本地
     */
    public static void writePushMealWhetherAuto(boolean flag, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "PushMealWhetherAuto", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putBoolean("PushMealWhetherAuto", flag);

        // 提交当前数据
        editor.commit();
    }

    /**
     *  从本地读取，出餐页面是否选择了自动出餐
     */
    public static boolean readPushMealWhetherAuto(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "PushMealWhetherAuto", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return false;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        boolean flag = sharedPreferences.getBoolean("PushMealWhetherAuto", false);
        return flag;
    }


    /**
     * 将手环地址存到本地
     */
    public static void writeToLocalMiBandAddress(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "miBandAddr", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("miBandAddr", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取现在手环的地址
     */
    public static String readFromLocalMiBandAddress(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "miBandAddr", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("miBandAddr", "");
        return address;
    }


    /**
     * 将是否开启手环震动功能存到本地
     */
    public static void writeToMiBand(boolean flag, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "miBandVibrate", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putBoolean("miBandVibrate", flag);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取是否开启手环震动功能
     */
    public static boolean readFromMiBand(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "miBandVibrate", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return false;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        boolean flag = sharedPreferences.getBoolean("miBandVibrate", false);
        return flag;
    }


    /**
     * 将店铺名称存到本地
     */
    public static void writeToLocalStoreName(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "storeName", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("storeName", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取店铺名称
     */
    public static String readFromLocalStoreName(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "storeName", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("storeName", "");
        return address;
    }


    /**
     * 将店铺id存到本地
     */
    public static void writeToLocalStoreId(int store_id, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "storeId", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putInt("storeId", store_id);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取店铺id
     */
    public static int readFromLocalStoreId(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "storeId", Activity.MODE_PRIVATE);

        int id = sharedPreferences.getInt("storeId", -1);
        return id;
    }


    /**
     * 将商户名称存到本地
     */
    public static void writeToLocalMerchantName(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "merchantName", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("merchantName", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取商户名称
     */
    public static String readFromLocalMerchantName(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences("merchantName", Activity.MODE_PRIVATE);

        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("merchantName", "");
        return address;
    }


    /**
     * 将商户名称存到本地
     */
    public static void writeToLocalMerchantId(int arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "merchantId", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putInt("merchantId", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取商户名称
     */
    public static int readFromLocalMerchantId(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "merchantId", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return 0;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        int id = sharedPreferences.getInt("merchantId", 0);
        return id;
    }

       /**
     * 将副本名称存到本地
     */
    public static void writeToLocalAppCopyName(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "appCopyName", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("appCopyName", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取副本名称
     */
    public static String readFromLocalAppCopyName(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "appCopyName", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("appCopyName", "");
        return address;
    }

    /**
     * 将副本名称存到本地
     */
    public static void writeToLocalAppCopyID(long id, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "appCopyId", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putLong("appCopyId", id);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取副本名称
     */
    public static long readFromLocalAppCopyId(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "appCopyId", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return -1;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        long id = sharedPreferences.getLong("appCopyId", -1);
        return id;
    }



    /**
     * 保存当前登录用户的头像和名字、权限列表
     */
    public static void writeToLocalNowLoginUserInfo(String head, String name, List<String> permissionIdList,
                                                    List<String> codeList, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "nowLoginUserInfo", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        Set<String> ls = new HashSet<String>();
        for (int k = 0; k < permissionIdList.size(); k++) {
            ls.add(permissionIdList.get(k));
        }

        Set<String> lsCodes = new HashSet<String>();
        for (int k = 0; k < codeList.size(); k++) {
            lsCodes.add(codeList.get(k));
        }

        // 用putString的方法保存数据
        editor.putString("name", null);
        editor.putString("head", null);
        editor.putStringSet("permissionIdList", null);
        editor.putStringSet("codeList", null);


        // 用putString的方法保存数据
        editor.putString("name", name);
        editor.putString("head", head);
        editor.putStringSet("permissionIdList", ls);
        editor.putStringSet("codeList", lsCodes);

        CommonUtils.LogWuwei(tag, "\n\n\nname is " + name + " head is " + head);

        // 提交当前数据
        editor.commit();
    }


    /**
     * 读取当前登录用户的头像和名字
     */
    public static Map readFromLocalNowLoginUserInfo(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "nowLoginUserInfo", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String name = sharedPreferences.getString("name", "");
        String head = sharedPreferences.getString("head", "");

        Map map = new HashMap<String, String>();
        map.put("name", name);
        map.put("head", head);
        return map;
    }


    /***
     * 判断当前登录的用户是否具备permisson这个权限
     *
     * @param permisson
     * @param ctxt
     * @return
     */
    public static boolean judgeNowLoginUserPermission(long permisson, Context ctxt) {
        SharedPreferences sharedPreferences = ctxt.getSharedPreferences("nowLoginUserInfo", Activity.MODE_PRIVATE);
        boolean flag = false;

        if (sharedPreferences == null) {
            return false;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        Set<String> permissionIdList = sharedPreferences.getStringSet("permissionIdList", new HashSet<String>());

        Iterator it = permissionIdList.iterator();
        String permissonStr = Long.toString(permisson);
        while (it.hasNext()) {
            String str = (String) it.next();
            if (permissonStr.equals(str)) {
                flag = true;
                break;
            }
        }
        return flag;
    }


    /***
     * 判断当前登录的用户是否具备是超级管理员
     *
     * @param
     * @param ctxt
     * @return
     */
    public static boolean judgeNowLoginUserWhetherSuperUser(Context ctxt) {
        SharedPreferences sharedPreferences = ctxt.getSharedPreferences("nowLoginUserInfo", Activity.MODE_PRIVATE);
        boolean flag = false;

        if (sharedPreferences == null) {
            return false;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        Set<String> permissionIdList = sharedPreferences.getStringSet("codeList", new HashSet<String>());

        Iterator it = permissionIdList.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if ("1".equals(str)) {
                flag = true;
                break;
            }
        }
        return flag;
    }


    /**
     * 将后厨打印机IP存到本地
     */
    public static void writeToLocalIpKitchPrinter(String name, String ip, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "KitchenPrinerIp", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("ip", ip);
        editor.putString("name", name);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取后厨打印机的IP地址
     */
    public static Map readFromLocalIpKitchPrinter(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "KitchenPrinerIp", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String ip = sharedPreferences.getString("ip", "");
        String name = sharedPreferences.getString("name", "");
        Map map = new HashMap<String, String>();
        map.put("ip", ip);
        map.put("name", name);
        return map;
    }


    /**
     * 将点餐打印机IP存到本地
     */
    public static void writeToLocalIpCreateOrderPrinter(String name, String ip, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "createOrderIp", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("ip", ip);
        editor.putString("name", name);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取点餐打印机的IP地址
     */
    public static Map readFromLocalIpCreateOrderPrinter(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "createOrderIp", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String ip = sharedPreferences.getString("ip", "");
        String name = sharedPreferences.getString("name", "");
        Map map = new HashMap<String, String>();
        map.put("ip", ip);
        map.put("name", name);
        return map;
    }


    /**
     * 将自助取号打印机IP存到本地
     */
    public static void writeToLocalIpTakeupPrinter(String name, String ip, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "takeupIp", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("ip", ip);
        editor.putString("name", name);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取自助取号打印机的IP地址
     */
    public static Map readFromLocalIpTakeupPrinter(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "takeupIp", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String ip = sharedPreferences.getString("ip", "");
        String name = sharedPreferences.getString("name", "");
        Map map = new HashMap<String, String>();
        map.put("ip", ip);
        map.put("name", name);
        return map;
    }



    /**
     * 将上次选中的员工id写入本地
     */
    public static void writeToLocalLastChooseStaffId(Context ctxt, int LastChooseStaffid) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences("LastChooseStaffid", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putInt("LastChooseStaffid", LastChooseStaffid);

        // 提交当前数据
        editor.commit();
    }


    /**
     * 读取上次选中的员工id
     */
    public static int readFromLocalLastChooseStaffId(Context ctxt) {
        SharedPreferences sharedPreferences = ctxt.getSharedPreferences("LastChooseStaffid", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return -1;
        }
        return sharedPreferences.getInt("LastChooseStaffid", -1);

    }


    /***
     * 将店铺外送设置信息（格式为json转换后的字符串）存入本地
     *
     * @param str
     * @param ctxt
     */
    public static void writeToLocalStoreDeliverySettingInfo(String str, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences("StoreDeliverySettingInfo", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("StoreDeliverySettingInfo", str);

        // 提交当前数据
        editor.commit();
    }


    /**
     * 从本地读取外送设置信息
     */
    public static String readFromLocalStoreDeliverySettingInfo(Context ctxt, String key) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences("StoreDeliverySettingInfo", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String StoreDeliverySettingInfo = sharedPreferences.getString("StoreDeliverySettingInfo", "");

        if (key == null || key.equals("")) {
            return StoreDeliverySettingInfo;
        } else {
            int intReuslt = 0;
            try {
                JSONObject obj = new JSONObject(StoreDeliverySettingInfo);
                JSONObject oj = obj.getJSONObject("store_delivery_setting");
                intReuslt = oj.getInt(key);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return Integer.toString(intReuslt);
        }
    }


    /**
     * 从本地读取并修改外送设置信息
     */
    public static void setLocalStoreDeliverySettingInfo(Context ctxt, String key, int value) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences("StoreDeliverySettingInfo", Activity.MODE_PRIVATE);

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String StoreDeliverySettingInfo = sharedPreferences.getString("StoreDeliverySettingInfo", "");
        CommonUtils.LogWuwei(tag, "after setLocalStoreDeliverySettingInfo result is " + StoreDeliverySettingInfo);

        try {
            JSONObject obj = new JSONObject(StoreDeliverySettingInfo);
            JSONObject oj = obj.getJSONObject("store_delivery_setting");
            oj.remove(key);
            oj.put(key, value);
            writeToLocalStoreDeliverySettingInfo(obj.toString(), ctxt);

            CommonUtils.LogWuwei(tag, "after setLocalStoreDeliverySettingInfo result is " + obj.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 将版本号存到本地
     */
    public static void writeToLocalVersion(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "AppVersion", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("appVersion", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取副本id
     */
    public static String readFromLocalVersion(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "AppVersion", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String version = sharedPreferences.getString("appVersion", "");
        return version;
    }


    /**
     * 将version_code存到本地
     */
    public static void writeToLocalVersionCode(int version_code, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "AppVersionCode", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putInt("appVersionCode", version_code);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 读取app的version_code
     */
    public static int readFromLocalVersionCode(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "AppVersionCode", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return 0;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        int version = sharedPreferences.getInt("appVersionCode", 0);
        return version;
    }


    /**
     * 将模板信息写入本地
     *
     * @param templateInfo
     * @param ctxt
     */
    public static void writeToLocalBasicTemplateInfo(String templateInfo, int purpose, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences("TemplateListInfo", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString(purpose + "", templateInfo);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从本地读取对应purpose和purpose_id的小票模板
     *
     * @param purpose
     * @param ctxt
     * @return
     */
    public static String readFromLocalBaiscTemplateInfo(int purpose, Context ctxt) {
        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "TemplateListInfo", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        } else {
            return sharedPreferences.getString(purpose + "", "");
        }
    }

    /**
     * 将出餐时选择的出餐口id存到本地
     * @param mealPortId
     * @param ctxt
     */
    public static void writeToLocalMealDoneChooseMealPortInfo(int mealPortId,String name,Context ctxt)
    {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "MealDoneChooseMealPortId", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putInt("mealPortId", mealPortId);
        editor.putString("mealPortName",name);

        // 提交当前数据
        editor.commit();
    }

    public static int readFromLocalMealDoneChooseMealPortId(Context ctxt)
    {
        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "MealDoneChooseMealPortId", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return -1;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        int mealPortId = sharedPreferences.getInt("mealPortId", -1);
        return mealPortId;
    }

    public static String readFromLocalMealDoneChooseMealPortName(Context ctxt)
    {
        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "MealDoneChooseMealPortId", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String mealPortName = sharedPreferences.getString("mealPortName", "");
        return mealPortName;
    }


    /**
     * 将小票机IP地址写入本地
     */
    public static void writeToLocalLablePrinterIp(String arg0, Context ctxt) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = ctxt.getSharedPreferences(
                "LabelPrinterSetting", Activity.MODE_PRIVATE);

        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        // 用putString的方法保存数据
        editor.putString("printerIp", arg0);

        // 提交当前数据
        editor.commit();
    }

    /**
     * 从入本地读取呼叫小票机IP地址
     */
    public static String readFromLocalLabelPrinterIp(Context ctxt) {

        SharedPreferences sharedPreferences = ctxt.getSharedPreferences(
                "LabelPrinterSetting", Activity.MODE_PRIVATE);
        if (sharedPreferences == null) {
            return null;
        }

        // 使用getString方法获得value，注意第2个参数是value的默认值
        String address = sharedPreferences.getString("printerIp", "");
        return address;
    }



}