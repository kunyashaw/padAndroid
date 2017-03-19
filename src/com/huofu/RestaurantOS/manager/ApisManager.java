package com.huofu.RestaurantOS.manager;

import android.os.Handler;

import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.DoHeartBeat;
import com.huofu.RestaurantOS.api.SaveTvMenuToClound;
import com.huofu.RestaurantOS.api.activate.AppActivate;
import com.huofu.RestaurantOS.api.activate.GetAuthQrCode;
import com.huofu.RestaurantOS.api.activate.GetPublicKey;
import com.huofu.RestaurantOS.api.activate.ScanAuthQrCodeStatus;
import com.huofu.RestaurantOS.api.appcopy.AppCopyInfo;
import com.huofu.RestaurantOS.api.common.GetRecepitTemplateList;
import com.huofu.RestaurantOS.api.common.GetTImeBucketList;
import com.huofu.RestaurantOS.api.common.NotifySendWebChat;
import com.huofu.RestaurantOS.api.common.SetOrderRefund;
import com.huofu.RestaurantOS.api.delivery.GetDeliveryOrderAnalysisInfo;
import com.huofu.RestaurantOS.api.delivery.GetDeliveryOrderListInfo;
import com.huofu.RestaurantOS.api.delivery.GetDeliveryStaffs;
import com.huofu.RestaurantOS.api.delivery.GetOrderDetailInfoByOrderId;
import com.huofu.RestaurantOS.api.delivery.GetStoreDeliverySettingInfo;
import com.huofu.RestaurantOS.api.delivery.SetDeliveryOrderDeliveried;
import com.huofu.RestaurantOS.api.delivery.SetDeliveryOrderDeliverying;
import com.huofu.RestaurantOS.api.delivery.SetDeliveryOrderPrepating;
import com.huofu.RestaurantOS.api.initPad.ModifyStoreInfo;
import com.huofu.RestaurantOS.api.login.Checkin;
import com.huofu.RestaurantOS.api.login.GetStaffList;
import com.huofu.RestaurantOS.api.login.Login;
import com.huofu.RestaurantOS.api.login.QrcodeCreate;
import com.huofu.RestaurantOS.api.login.QrcodeScan;
import com.huofu.RestaurantOS.api.pushMeal.GetMealProductList;
import com.huofu.RestaurantOS.api.pushMeal.GetMealPushHistory;
import com.huofu.RestaurantOS.api.pushMeal.GetPortMealPushHistory;
import com.huofu.RestaurantOS.api.pushMeal.GetTakeupListBySerialNum;
import com.huofu.RestaurantOS.api.pushMeal.SetMealProductList;
import com.huofu.RestaurantOS.api.pushMeal.SetOrderAllCheckout;
import com.huofu.RestaurantOS.api.pushMeal.SetPortMealCheckout;
import com.huofu.RestaurantOS.api.pushMeal.SetPortMealCheckoutAuto;
import com.huofu.RestaurantOS.api.pushMeal.mealPort.CheckAPPTaskPorts;
import com.huofu.RestaurantOS.api.pushMeal.mealPort.ChecktIdlePorts;
import com.huofu.RestaurantOS.api.pushMeal.mealPort.DeleteMealPort;
import com.huofu.RestaurantOS.api.pushMeal.mealPort.GetAllMealPorts;
import com.huofu.RestaurantOS.api.pushMeal.mealPort.GetMealPortLetterList;
import com.huofu.RestaurantOS.api.pushMeal.mealPort.RegistMealPortTaskRelation;
import com.huofu.RestaurantOS.api.pushMeal.mealPort.SaveMealPort;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.api.setting.DeleteDeliveryBuilding;
import com.huofu.RestaurantOS.api.setting.DeletePeripheral;
import com.huofu.RestaurantOS.api.setting.GetOwenedStoreList;
import com.huofu.RestaurantOS.api.setting.GetPeripheralList;
import com.huofu.RestaurantOS.api.setting.GetStoreDeliveryBuildingList;
import com.huofu.RestaurantOS.api.setting.SaveOrAddDeliveryBuilding;
import com.huofu.RestaurantOS.api.setting.SaveOrAddPeripheral;
import com.huofu.RestaurantOS.api.setting.SaveStoreDeliverySettingInfo;
import com.huofu.RestaurantOS.api.setting.SetStoreDeliverySupported;
import com.huofu.RestaurantOS.api.setting.SetStoreDeliveryTimeBucket;
import com.huofu.RestaurantOS.api.stockSupplyAndPlan.GetProductList;
import com.huofu.RestaurantOS.api.stockSupplyAndPlan.QueryFixedInventory;
import com.huofu.RestaurantOS.api.stockSupplyAndPlan.QueryWeekInventory;
import com.huofu.RestaurantOS.api.stockSupplyAndPlan.UpdateProductFixedInventory;
import com.huofu.RestaurantOS.api.stockSupplyAndPlan.UpdateProductInventory;
import com.huofu.RestaurantOS.api.stockSupplyAndPlan.UpdateWeekProductInventory;
import com.huofu.RestaurantOS.api.stockSupplyAndPlan.inventoryDateQuery;
import com.huofu.RestaurantOS.api.takeUp.OrderTakeCode;
import com.huofu.RestaurantOS.api.takeUp.QueryOrderTakeout;
import com.huofu.RestaurantOS.api.tvMenu.GetTvMenuDisplay;
import com.huofu.RestaurantOS.api.tvMenu.GetTvMenuFromClound;
import com.huofu.RestaurantOS.api.version.GetAppInfo;
import com.huofu.RestaurantOS.api.version.GetAppVersionInfo;
import com.huofu.RestaurantOS.bean.ProductWeekItem;
import com.huofu.RestaurantOS.bean.storeOrder.MealPortRelation;
import com.huofu.RestaurantOS.bean.storeOrder.StoreMealAutoPrintParams;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tim on 6/8/15.
 */
public class ApisManager {


    /************************************************授权***************************************************/
    /***
     * 获取公共密钥
     */
    public static void GetPublicKey(ApiCallback callback) {
        GetPublicKey getPublicKey = new GetPublicKey();
        getPublicKey.setParamsEnc(false);
        getPublicKey.setReturnPrimaryData(true);
        getPublicKey.request(callback);
    }

    /***
     * 获取授权二维码
     */
    public static void GetAuthQrcode(ApiCallback callback) {
        GetAuthQrCode getAuthQrCode = new GetAuthQrCode();
        getAuthQrCode.setEncKeyType(BaseApi.EncodeKeyType.KEYTYPE_PUBLICKEY);
        getAuthQrCode.request(callback);
    }


    /***
     * 扫描二维码状态
     */
    public static void ScanAuthQrcode(String token, ApiCallback callback) {
        ScanAuthQrCodeStatus scanAuthQrCodeStatus = new ScanAuthQrCodeStatus();
        scanAuthQrCodeStatus.setEncKeyType(BaseApi.EncodeKeyType.KEYTYPE_PUBLICKEY);

        Map map = new HashMap();
        map.put("token", token);
        scanAuthQrCodeStatus.requestWithParams(map, callback);
    }


    /**
     * 副本激活
     *
     * @param callback
     */
    public static void AppAcitvate(String license, ApiCallback callback) {
        AppActivate appActivate = new AppActivate();
        appActivate.setEncKeyType(BaseApi.EncodeKeyType.KEYTYPE_PUBLICKEY);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("license", license);
        appActivate.requestWithParams(map, callback);
    }

    /************************************************登录***************************************************/

    /***
     * 签到
     *
     * @param callback
     */
    public static void checkin(ApiCallback callback) {
        Checkin checkin = new Checkin();
        checkin.setEncKeyType(BaseApi.EncodeKeyType.KEYTYPE_MAIN);
        checkin.request(callback);
    }

    /***
     * 获取副本信息
     *
     * @param callback
     */
    public static void getAppCopy(ApiCallback callback) {
        //new AppCopyInfo().requestWithParams(new ArrayList<NameValuePair>(), callback);
        new AppCopyInfo().request(callback);
    }


    /***
     * 获取员工列表
     *
     * @param callback
     */
    public static void getStaffList(int page, int size, ApiCallback callback) {
        Map map = new HashMap();
        map.put("size", size);
        map.put("page", page);
        new GetStaffList().requestWithParams(map, callback);
    }


    /**
     * 登录
     *
     * @param callback
     */
    public static void Login(int staffId, String password, ApiCallback callback) {
        Map map = new HashMap();
        map.put("staff_id", staffId);
        map.put("password", password);
        new Login().requestWithParams(map, callback);
    }

    /***
     * 获取登录二维码
     *
     * @param callback
     */
    public static void GetLoginQrcode(ApiCallback callback) {
        new QrcodeCreate().request(callback);
    }


    /***
     * 获取登录二维码扫描状态
     *
     * @param callback
     * @param token
     */
    public static void ScanLoginQrcodeStatus(String token, ApiCallback callback) {
        Map map = new HashMap();
        map.put("token", token);
        new QrcodeScan().requestWithParams(map, callback);
    }

    /***
     * 获取app信息
     */
    public static void GetAppInfo(int app_id,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("app_id", app_id);
        GetAppInfo info = new GetAppInfo();
        info.setEncKeyType(BaseApi.EncodeKeyType.KEYTYPE_PUBLICKEY);
        info.requestWithParams(map, callback);
    }

    /**
     * 获取app对应版本的信息
     */
    public static void GetAppVersionInfo(int app_id,Double version,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("version",version);
        map.put("app_id",app_id);
        GetAppVersionInfo info = new GetAppVersionInfo();
        info.setEncKeyType(BaseApi.EncodeKeyType.KEYTYPE_PUBLICKEY);
        info.requestWithParams(map, callback);
    }

    /************************************************店铺初始化***************************************************/

    /****
     * 保存店铺初始化时所填写的店铺名称和收银台名称
     *
     * @param callback
     */
    public static void SaveStoreNewInfo(String appcopy_name, String store_name, ApiCallback callback) {
        Map map = new HashMap();
        map.put("appcopy_name", appcopy_name);
        map.put("store_name", store_name);
        new ModifyStoreInfo().requestWithParams(map, callback);
    }


    /************************************************设置***************************************************/

    /****
     * 获取副本所在店铺的所有可用外接设备信息
     */
    public static void GetPeripheralList(ApiCallback callback) {
        Map map = new HashMap();
        map.put("client_id", LocalDataDeal.readFromLocalClientId(MainApplication.getContext()));
        new GetPeripheralList().requestWithParams(map, callback);
    }

    /****
     * 从云端删除外设
     *
     * @param peripheral_id 要删除的外接设备id
     */
    public static void DeletePeripheralFromClound(int peripheral_id, ApiCallback callback) {

        Map map = new HashMap();
        map.put("client_id", LocalDataDeal.readFromLocalClientId(MainApplication.getContext()));
        map.put("peripheral_id", peripheral_id);
        new DeletePeripheral().requestWithParams(map, callback);
    }


    /***
     * 新增或者保存外设到云端
     *
     * @param name:外接设备名称
     * @param con_id:外接设备标记（比如说ip地址、蓝牙地址等）
     * @param peripheral_id                为服务器自动生成的id（当新增时，将该值做为零传入，当修改时，该值为服务器返回的值）
     * @param type 1打印机 2ipos 3叫号设备
     * @param printer_paper_size 0未知 1:58mm 2:80mm 3:110mm,如果不是打印机类型设置为0
     */
    public static void SaveOrAddPeripheralToClound(String name, String con_id, int peripheral_id,
                                                   int type,int printer_paper_size,
                                                   ApiCallback callback) {
        Map map = new HashMap();
        map.put("name", name);
        map.put("con_id", con_id);
        map.put("peripheral_id", peripheral_id);
        map.put("type",type);
        map.put("printer_paper_size",1);//printer_paper_size);
        new SaveOrAddPeripheral().requestWithParams(map, callback);
    }


    /************************************************自助取号***************************************************/
    /**
     * 取号查询接口
     */
    public static void QueryOrderTakeput(String take_code, ApiCallback callback) {
        Map map = new HashMap();
        map.put("take_code", take_code);
        map.put("repaste_date", CommonUtils.getFormatDate(0));
        new QueryOrderTakeout().requestWithParams(map, callback);
    }

    /**
     * 取号接口
     */
    public static void OrderTakeCode(int take_mode, String order_id, ApiCallback callback) {
        Map map = new HashMap();
        map.put("take_mode", take_mode);
        map.put("order_id", order_id);
        new OrderTakeCode().requestWithParams(map, callback);
    }

    /************************************************外送***************************************************/

    /***
     * 设置店铺是否支持外送
     */
    public static void SetStoreDeliverySupported(int flag, int store_id, ApiCallback callback) {
        Map map = new HashMap();
        map.put("delivery_supported", flag);
        map.put("store_id", store_id);
        new SetStoreDeliverySupported().requestWithParams(map, callback);
    }

    /***
     * 获取具有权限的店铺列表
     */
    public static void GetOwenedStoreList(int permission_id, ApiCallback callback) {
        Map map = new HashMap();
        map.put("permission_id", permission_id);
        new GetOwenedStoreList().requestWithParams(map, callback);
    }

    /***
     * 获得某个店铺的外送设置信息
     */
    public static void GetStoreDeliverySettingInfo(int store_id, ApiCallback callback) {
        Map map = new HashMap();
        map.put("store_id", store_id);
        new GetStoreDeliverySettingInfo().requestWithParams(map, callback);
    }

    /**
     * 获得某个店铺的外送楼宇信息
     */
    public static void GetStoreDeliveryBuildingsList(int store_id, ApiCallback callback) {
        Map map = new HashMap();
        map.put("store_id", store_id);
        new GetStoreDeliveryBuildingList().requestWithParams(map, callback);
    }


    /***
     * 设置店铺营业时间段支持外送
     */
    public static void SetStoreDeliveryTimeBucket(int time_bucket_id, int deivery_supported, int store_id, ApiCallback callback) {
        Map map = new HashMap();
        map.put("time_bucket_id", time_bucket_id);
        map.put("delivery_supported", deivery_supported);
        map.put("store_id", store_id);
        new SetStoreDeliveryTimeBucket().requestWithParams(map, callback);
    }

    /***
     * 保存或者新增外送楼宇
     */
    public static void SaveOrAddDeliveryBuilidng(int building_id, String name, String address, int store_id, ApiCallback callback) {
        Map map = new HashMap();
        map.put("building_id", building_id);
        map.put("name", name);
        map.put("address", address);
        map.put("store_id", store_id);
        new SaveOrAddDeliveryBuilding().requestWithParams(map, callback);
    }


    /***
     * 删除楼宇信息
     */
    public static void DeleteDeliveryBuilding(int store_id, int building_id, ApiCallback callback) {
        Map map = new HashMap();
        map.put("building_id", building_id);
        map.put("store_id", store_id);
        new DeleteDeliveryBuilding().requestWithParams(map, callback);
    }

    /***
     * 保存店铺外送信息
     *
     * @param min_order_delivery_amount      起送金额
     * @param min_order_free_delivery_amount 免外送金额
     * @param delivery_fee                   外送费
     * @param ahead_time                     提前下单时间
     * @param store_id                       店铺id
     * @param delivery_assign_time_supported 是否制定预约送达时间
     * @param callback
     */
    public static void SaveStoreDeliverySettingInfo(
            int min_order_delivery_amount,
            int min_order_free_delivery_amount,
            int delivery_fee,
            int ahead_time,
            int delivery_assign_time_supported,
            int store_id, ApiCallback callback) {
        Map map = new HashMap();
        map.put("min_order_delivery_amount", min_order_delivery_amount);
        map.put("min_order_free_delivery_amount", min_order_free_delivery_amount);
        map.put("delivery_fee", delivery_fee);
        map.put("ahead_time", ahead_time);
        map.put("store_id", store_id);
        map.put("delivery_assign_time_supported", delivery_assign_time_supported);
        new SaveStoreDeliverySettingInfo().requestWithParams(map, callback);

    }


    /***
     * 获取外送订单统计数据
     */
    public static void getDeliveryOrderAnalysisInfo(ApiCallback callback) {
        new GetDeliveryOrderAnalysisInfo().request(callback);
    }

    /**
     * 设置订单备餐中
     */
    public static void setDeliveryOrderPreparing(List<String> listOrderId, ApiCallback callback) {
        org.json.JSONArray arrayList = new org.json.JSONArray();
        for (int k = 0; k < listOrderId.size(); k++) {
            String str = listOrderId.get(k);
            arrayList.put(str);
        }

        Map map = new HashMap();
        map.put("order_ids", listOrderId);
        new SetDeliveryOrderPrepating().requestWithParams(map, callback);

    }

    /***
     * 设置外送订单正在派送
     */
    public static void setDeliveryOrderDeliverying(int delivery_staff_id, List<String> listOrderId, ApiCallback callback) {
        JSONArray arrayList = new JSONArray();
        for (int k = 0; k < listOrderId.size(); k++) {
            String str = listOrderId.get(k);
            arrayList.put(str);
        }
        Map map = new HashMap();
        map.put("order_ids", listOrderId);
        map.put("delivery_staff_id", delivery_staff_id);
        new SetDeliveryOrderDeliverying().requestWithParams(map, callback);
    }


    /**
     * 设置订单已经送达
     */
    public static void setDeliveryOrderDeliveried(List<String> listOrderId, ApiCallback callback) {
        JSONArray arrayList = new JSONArray();
        for (int k = 0; k < listOrderId.size(); k++) {
            String str = listOrderId.get(k);
            arrayList.put(str);
        }
        Map map = new HashMap();
        map.put("order_ids", listOrderId);
        new SetDeliveryOrderDeliveried().requestWithParams(map, callback);
    }

    /**
     * 获得外送订单数据
     */
    public static void getDeliveryListInfo(int status, int page, ApiCallback callback) {
        Map map = new HashMap();
        map.put("status", status);
        map.put("page", page);
        map.put("size", 100);
        new GetDeliveryOrderListInfo().requestWithParams(map, callback);
    }

    /**
     * 根据手机号查询外送订单数据
     */
    public static void getDeliveryListInfoByMobile(int page, String mobile, ApiCallback callback) {
         Map map = new HashMap();
        map.put("page", page);
        map.put("size", 100);
        map.put("mobile", mobile);
        new GetDeliveryOrderListInfo().requestWithParams(map, callback);
    }

    public static void setOrderRefund(double refund_amount,int refund_status,String order_id,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("order_id", order_id);
        map.put("refund_amount", refund_amount);
        map.put("refund_status", refund_status);
        new SetOrderRefund().requestWithParams(map, callback);
    }

    /**
     * 获得外送员列表
     */
    public static void getDeliveryStaffs(ApiCallback callback) {
        new GetDeliveryStaffs().request(callback);
    }

    /***
     * 查询订单详情
     */
    public static void getOrderDetailInfoByOrderId(String order_id, ApiCallback callback) {
        Map map = new HashMap();
        map.put("order_id", order_id);
        new GetOrderDetailInfoByOrderId().requestWithParams(map, callback);
    }

    /************************************************库存计划、盘点***************************************************/
    /**
     * 获取产品列表
     *
     * @param callback
     */
    public static void getProductList(ApiCallback callback) {
        new GetProductList().request(callback);
    }

    /***
     * 产品日常库存盘点信息列表查询
     *
     * @param callback
     * @param time_bucket_id
     * @param inv_type       未开启 0, 计划库存 1, 固定库存 2
     */
    public static void queryInventory(ApiCallback callback, long time_bucket_id, int inv_type, int dayStep) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("repast_date", CommonUtils.getFormatDate(dayStep));
        map.put("time_bucket_id", "" + time_bucket_id);
        map.put("inv_type", "" + inv_type);
        new inventoryDateQuery().requestWithParams(map, callback);

    }

    /***
     * 获取营业时间段
     *
     * @param callback
     */
    public static void GetTimeBucketList(ApiCallback callback) {
        new GetTImeBucketList().request(callback);
    }

    /***
     * 更新固定库存信息
     *
     * @param callback
     */
    public static void UpdateProduceFixedInventory(long product_id, long time_bucket_id, double amount, int dayStep, ApiCallback callback) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("product_id", "" + product_id);
        map.put("repast_date", CommonUtils.getFormatDate(dayStep));
        map.put("time_bucket_id", "" + time_bucket_id);
        map.put("amount", "" + amount);
        new UpdateProductFixedInventory().requestWithParams(map, callback);
    }


    /****
     * 产品自动周期库存信息查询
     */
    public static void QueryInventoryByProduct(long product_id, ApiCallback callback) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("product_id", "" + product_id);
        new QueryWeekInventory().requestWithParams(map, callback);
    }

    /***
     * 产品固定库存信息查询
     *
     * @param product_id
     * @param callback
     */
    public static void QueryFixedInventoryByProduct(long product_id, ApiCallback callback) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("product_id", "" + product_id);
        new QueryFixedInventory().requestWithParams(map, callback);
    }


    /****
     * 修改固定库存(可用于是否开启库存时)
     *
     * @param inv_type    0=未开启，1=周期库存，2=固定库存
     * @param inv_eanbled 0关闭 1开启
     */
    public static void UpdateInventory(long product_id, boolean inv_eanbled, long inv_type, double amount, ApiCallback callback) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("product_id", "" + product_id);
        if (inv_eanbled) {
            map.put("inv_enabled", "" + 1);
        } else {
            map.put("inv_enabled", "" + 0);
        }
        map.put("inv_type", "" + inv_type);
        map.put("amount", "" + amount);
        new UpdateProductInventory().requestWithParams(map, callback);
    }


    /***
     * 更新周期库存
     *
     * @param product_id
     * @param listPWI
     * @param apiCallback
     */
    public static void UpdateWeekProductInventory(long product_id, List<ProductWeekItem> listPWI, ApiCallback apiCallback) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("product_id", product_id);
        map.put("product_week_items", listPWI);
        new UpdateWeekProductInventory().requestWithParams(map, apiCallback);
    }

    /**
     * 从服务器获取小票模板
     * @param callback
     */
    public static void GetReceiptTemplateList(ApiCallback callback)
    {
        new GetRecepitTemplateList().request(callback);
    }

    /**
     * 发微信通知
     */
    public static void NotifySendWebChat(int num,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("repast_date", CommonUtils.getFormatDate(0));
        map.put("take_serial_number", num);
        new NotifySendWebChat().requestWithParams(map, callback);
    }

    /************************************************出餐***************************************************/

    /**
     * 指定出餐口的出餐
     * @param orderId 订单id
     * @param repast_date 日期
     * @param time_bucket_id 时间段id
     * @param packaged 是否打包
     * @param store_meal_checkouts 出餐项目组合
     * @param checkout_type 出餐方式（0手动还是1自动）
     * @param port_id 端口id
     * @param callback 回调
     */
    public static void mealCheckout(String content,Handler handler,
            String orderId,String repast_date,long time_bucket_id,int packaged,JSONArray store_meal_checkouts,int checkout_type,long port_id,ApiCallback callback)
    {
        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
        try
        {
            if(store_meal_checkouts != null) {
                for (int k = 0; k < store_meal_checkouts.length(); k++) {
                    org.json.JSONObject obj = (org.json.JSONObject)store_meal_checkouts.get(k);
                    JSONObject oj = new JSONObject();
                    oj.put("amount",obj.get("amount"));
                    oj.put("charge_item_id",obj.get("charge_item_id"));
                    oj.put("packaged",obj.get("packaged"));
                    oj.put("orderId",orderId);
                    array.add(oj);
                }
            }
        }
        catch (Exception e)
        {

        }
        Map map = new HashMap();
        map.put("order_id",orderId);
        map.put("repast_date",repast_date);
        map.put("time_bucket_id",time_bucket_id);
        map.put("packaged",packaged);
        map.put("store_meal_checkouts",array);//store_meal_checkouts);
        map.put("checkout_type",checkout_type);
        map.put("port_id",port_id);

        CommonUtils.LogWuwei(PushMealActivity.tag,"出餐请求参数为:"+map.toString());

        new SetPortMealCheckout(store_meal_checkouts,content,handler).requestWithParams(map, callback);
    }

    /**
     * 获取在未设置出餐口时的出餐历史
     * @param repast_date
     * @param callback
     */
    public static void getMealPushedHistory(String repast_date,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("repast_date", repast_date);
        new GetMealPushHistory().requestWithParams(map, callback);
    }


    /**
     * 获取在已经设置出餐口时的出餐历史
     * @param repast_date
     * @param callback
     */
    public static void getMealPushedHistory(long port_id,String repast_date,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("repast_date",repast_date);
        map.put("port_id",port_id);
        new GetPortMealPushHistory().requestWithParams(map, callback);
    }

    /***
     *
     * @param port_id
     * @param portName
     * @param letter
     * @param printer_peripheral_id
     * @param call_peripheral_id
     * @param call_type 叫号规则 1:自动叫号 2:手动叫号 3:尾单叫号
     * @param checkout_type 出餐模式 1:手动出餐 2:自动出餐
     * @param has_pack 0堂食 1打包
     * @param callback
     */
    public static void SaveMealPort(long port_id,String portName,String letter,
                                    long printer_peripheral_id,long call_peripheral_id,
                                    int call_type,int checkout_type,int has_pack,ApiCallback callback)
    {
        Map map = new HashMap();
        if(port_id != -1)//如果是保存已有出餐口
        {
            map.put("port_id",port_id);
        }
        map.put("name",portName);
        map.put("letter",letter);
        map.put("printer_peripheral_id",printer_peripheral_id);
        map.put("call_peripheral_id",call_peripheral_id);
        map.put("call_type",call_type);
        map.put("checkout_type",checkout_type);
        map.put("has_pack", has_pack);

        new SaveMealPort().requestWithParams(map, callback);

    }

    /**
     * 删除出餐台
     * @param port_id
     * @param callback
     */
    public static void deleteMealPort(long port_id,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("port_id",port_id);
        new DeleteMealPort().requestWithParams(map, callback);
    }


    /**
     * 获取店铺所有可用出餐台
     * @param callback
     */
    public static void getAllMealPortList(ApiCallback callback)
    {
        new GetAllMealPorts().request(callback);
    }

    /**
     * 获取出餐口所有的标识
     * @param callback
     */
    public static void getMealPortAllLetterList(ApiCallback callback)
    {
        new GetMealPortLetterList().request(callback);
    }

    /**
     * 多出餐口自动出餐接口
     */
    public static void mealPushAutoPortCheckout(long appcopy_id,int order_num,List<StoreMealAutoPrintParams> list,ApiCallback callback)
    {
        Map map = new HashMap();

        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
        if(list != null)
        {
            for(int k=0;k<list.size();k++)
            {
                JSONObject obj = new JSONObject();
                StoreMealAutoPrintParams smapp = list.get(k);
                obj.put("repast_date",smapp.repast_date);
                obj.put("take_serial_number",smapp.take_serial_number);
                obj.put("take_serial_seq",smapp.take_serial_seq);
                obj.put("port_id",smapp.port_id);
                obj.put("printer_status",smapp.printer_status);
                obj.put("printed",smapp.printed);
                array.add(obj);
            }
        }

        map.put("appcopy_id",appcopy_id);
        map.put("order_num",order_num);
        map.put("store_meal_auto_print_params",array);
        new SetPortMealCheckoutAuto().requestWithParams(map, callback);
    }

    /**
     * 查询app关联出餐口
     */
    public static void checkAppTaskPorts(long appcopy_id,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("appcopy_id",appcopy_id);
        new CheckAPPTaskPorts().requestWithParams(map, callback);
    }


    /**
     * 查询空闲的出餐口
     * @param callback
     */
    public static void checkIdleMealPorts(ApiCallback callback)
    {
        new ChecktIdlePorts().request(callback);
    }


    /**
     * 登记App与出餐口的任务关系
     */
    public static void registTaskRelation(long appcopy_id,List<MealPortRelation>list,ApiCallback callback)
    {
        Map map = new HashMap();
        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
        for(int k=0;k<list.size();k++)
        {
            MealPortRelation mpr = list.get(k);
            JSONObject obj = new JSONObject();
            obj.put("port_id",mpr.port_id);
            obj.put("task_status",mpr.task_status);
            obj.put("printer_status",mpr.printer_status);
            obj.put("checkout_type",mpr.checkout_type);
            array.add(obj);
        }

        map.put("appcopy_id",appcopy_id);
        map.put("meal_port_relations",array);
        new RegistMealPortTaskRelation().requestWithParams(map, callback);
    }

    /**
     * 获取需要统计的产品列表
     */
    public static void getMealProdcutList(int time_bucket_id,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("time_bucket_id",time_bucket_id);
        new GetMealProductList().requestWithParams(map, callback);
    }

    public static void setMealProductList(com.alibaba.fastjson.JSONArray array,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("stat_products",array);
        new SetMealProductList().requestWithParams(map,callback);
    }

    /************************************************心跳连接***************************************************/
    public static void doHeartBeat(ApiCallback callback)
    {
        new DoHeartBeat().request(callback);
    }


    /**
     * 获取待出餐列表
     */
    public static void listNewTickets(int portId, int latestSerialNumber,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("port_id",portId);
        map.put("take_serial_number",latestSerialNumber);
        new GetTakeupListBySerialNum().requestWithParams(map, callback);
    }

    /**
     * 订单剩余出完
     * @param orderId
     * @param repast_date
     * @param timebucketId
     * @param checkout_type
     * @param port_id
     * @param callback
     */
    public static void orderAllCheckout(String orderId,String repast_date,long timebucketId,
                                        int checkout_type,long port_id,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("order_id",orderId);
        map.put("repast_date",repast_date);
        map.put("time_bucket_id",timebucketId);
        map.put("checkout_type",checkout_type);
        map.put("port_id",port_id);
        CommonUtils.LogWuwei(PushMealActivity.tag, "全部出餐请求参数为:" + map.toString());
        new SetOrderAllCheckout().requestWithParams(map,callback);
    }

    /************************************************  电视菜单  ***************************************************/

    /**
     * 根据日期、营业时间段、是否需要库存来获取电视菜单排版信息
     * @param time_bucket_id  营业时段id，如果不指定，就选择当前的营业时段
     * @param date          指定日期 格式: yyyy-MM-dd，如果日期不是当天，营业时段id必须是有效
     * @param inventory     0:不需要获取库存, 1:需要获取库存
     * @param callback
     */
    public static void getTvMenu(long time_bucket_id,String date,int inventory,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("date",date);
        map.put("time_bucket_id",time_bucket_id);
        map.put("inventory",inventory);
        new GetTvMenuDisplay().requestWithParams(map,callback);
    }


    /**
     *获得电视菜单
     * @param time_bucket_id
     * @param date
     * @param callback
     */
    public static void getTvMenuFromClound(long time_bucket_id,String date,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("date",date);
        map.put("time_bucket_id",time_bucket_id);
        new GetTvMenuFromClound().requestWithParams(map,callback);
    }

    /**
     * 保存菜单到电视
     */
    public static void SaveMenuToClound(int time_bucket_id,String date,String content,ApiCallback callback)
    {
        Map map = new HashMap();
        map.put("time_bucket_id", time_bucket_id);
        map.put("date", date);
        map.put("content", content);
        new SaveTvMenuToClound().requestWithParams(map, callback);
    }
}

