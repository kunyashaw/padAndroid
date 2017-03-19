package com.huofu.RestaurantOS.ui.pannel.pushMeal;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.StoreMealPort;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.bean.pushMeal.StoreMeal;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.ChargeItemSub;
import com.huofu.RestaurantOS.bean.storeOrder.MealPortRelation;
import com.huofu.RestaurantOS.bean.storeOrder.MealProduct;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.bean.storeOrder.StoreMealAutoPrintParams;
import com.huofu.RestaurantOS.bean.storeOrder.StorePort;
import com.huofu.RestaurantOS.bean.storeOrder.call_peripheral;
import com.huofu.RestaurantOS.bean.storeOrder.printer_peripheral;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.service.autoPushMealIntentService;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.StringUtils;
import com.huofu.RestaurantOS.utils.templateModulsParse.TemplateModulsParse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/10/27.
 */
public class AutoPushMealUtils {

    public static List<peripheral> periphealList = new ArrayList<peripheral>();//外接设备列表
    public static long appcopy_id = LocalDataDeal.readFromLocalAppCopyId(MainApplication.getContext());
    public static String tag = PushMealActivity.tag;
    public static Context ctxt = MainApplication.getContext();
    public static boolean flagAutoCheckout = false;
    public static boolean flagRun = false;
    static String msg = "";

    /**
     * 获取空闲的的出餐口
     */
    public static void heatBeatMainDeal() {
        //listIdleMealPorts.clear();
        ApisManager.checkIdleMealPorts(new ApiCallback() {
            @Override
            public void success(Object object) {
                /**********************************1、得到空闲出餐口*******************/
                List<StoreMealPort> listIdleMealPorts = (List<StoreMealPort>) (object);
                for (StoreMealPort smp : listIdleMealPorts) {
                    CommonUtils.LogWuwei(tag, "空闲出餐口 name is " + smp.name + " port_id is " + smp.port_id);
                }


                /***********************************2、得到可用出餐口**********************/
                if (periphealList.size() == 0) {
                    GetPeripheralList();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                List<StoreMealPort> listAvaliableMealPorts = new ArrayList<StoreMealPort>();
                for (int k = 0; k < listIdleMealPorts.size(); k++) {
                    StoreMealPort smp = listIdleMealPorts.get(k);
                    for (int t = 0; t < periphealList.size(); t++) {
                        if (smp.printer_peripheral_id == periphealList.get(t).peripheral_id) {
                            smp.flagCanUse = CommonUtils.executeCammand(periphealList.get(t).con_id);
                            if (smp.flagCanUse) {
                                CommonUtils.LogWuwei(tag, "可用出餐口:name is" + smp.name + " ip is " + periphealList.get(t).con_id);
                                listAvaliableMealPorts.add(smp);
                            }
                        }
                    }
                }

                /***********************************3、登记**********************/
                if (listAvaliableMealPorts.size() > 0)//如果可用打印机数量大于1，则进行关联
                {
                    List<MealPortRelation> listMPR = new ArrayList<MealPortRelation>();

                    for (int k = 0; k < listAvaliableMealPorts.size(); k++) {
                        StoreMealPort smp = listAvaliableMealPorts.get(k);
                        MealPortRelation mpr = new MealPortRelation();
                        mpr.port_id = smp.port_id;
                        mpr.task_status = 1;//0解除任务关系 1建立任务关系
                        mpr.printer_status = 1;//0 未连接 1正常连接 2无法打印
                        mpr.checkout_type = 1;//0 手动 1自动
                        listMPR.add(mpr);
                        CommonUtils.LogWuwei(tag, "准备登记：建立自动的任务关系");
                    }

                    AutoPushMealUtils.registerPorts(listMPR);
                }

            }

            @Override
            public void error(BaseApi.ApiResponse response) {

            }
        });
    }


    /**
     * 获取外接设备列表
     */
    public static void GetPeripheralList() {
        periphealList.clear();
        ApisManager.GetPeripheralList(new ApiCallback() {
            @Override
            public void success(Object object) {
                periphealList = (List<peripheral>) object;
                for (peripheral pp : periphealList) {
                    CommonUtils.LogWuwei(tag, "----peripheral name is " + pp.name);
                }
            }

            @Override
            public void error(BaseApi.ApiResponse response) {

            }
        });
    }


    /**
     * 将出餐口进行登记
     */
    public static void registerPorts(List<MealPortRelation> list) {

        ApisManager.registTaskRelation(appcopy_id, list, new ApiCallback() {
            @Override
            public void success(Object object) {
                CommonUtils.LogWuwei(tag, "登记成功");
                if (!flagRun)//如果没有启动 则启动递归
                {
                    CommonUtils.LogWuwei(tag, "-\n\n\n-----------------启动自动出餐----------------\n\n\n-");
                    flagRun = true;
                    AutoPushMealUtils.autoCheckout(AutoPushMealUtils.appcopy_id, 1, autoPushMealIntentService.listStoreMealPrintParams);
                }
                checkAppTaskPort();
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.LogWuwei(tag, "登记失败" + response.error_message);
            }
        });

    }


    /**
     * 查询APP关联的出餐口
     */
    public static void checkAppTaskPort() {

        ApisManager.checkAppTaskPorts(appcopy_id, new ApiCallback() {
            @Override
            public void success(Object object) {
                List<StoreMealPort> listMatchedMealPorts = (List<StoreMealPort>) (object);

                if (listMatchedMealPorts != null) {
                    for (StoreMealPort smp : listMatchedMealPorts) {
                        if (smp.checkout_type == 0) {
                            msg += "手工出餐 关联到app的出餐口名称:" + smp.name;
                            CommonUtils.LogWuwei(tag, "手工出餐 关联到app的出餐口名称:" + smp.name);
                        } else if (smp.checkout_type == 1) {
                            msg += "自动出餐 关联到app的出餐口名称:" + smp.name;
                            CommonUtils.LogWuwei(tag, "自动出餐 关联到app的出餐口名称:" + smp.name);
                        }
                    }
                }
            }

            @Override
            public void error(BaseApi.ApiResponse response) {

            }
        });
    }


    /**
     * 自动出餐
     */
    public static void autoCheckout(long appcopy_id, int order_num, List<StoreMealAutoPrintParams> list) {


        ApisManager.mealPushAutoPortCheckout(appcopy_id, order_num, list, new ApiCallback() {
            @Override
            public void success(Object object) {

                CommonUtils.LogWuwei(tag, "。。。。。。处于自动出餐循环中。。。。。。。");

                /**********************1、解析服务器返回的订单数据和出餐口数据*********************/
                JSONObject obj = (JSONObject) object;
                JSONArray arryMealPorts = obj.getJSONArray("store_ports");
                JSONArray arrayStoreMeals = obj.getJSONArray("store_meals");

                List<StorePort> listSP = new ArrayList<StorePort>();
                listSP = smpListDeal(arryMealPorts);


                List<OrderDetailInfo> listODI = new ArrayList<OrderDetailInfo>();
                listODI = odiListDeal(arrayStoreMeals);

                if (listSP.size() == 0)//如果服务器返回的出餐口数量为0，则停止自动出餐
                {
                    flagRun = false;
                    CommonUtils.LogWuwei(tag, "-\n\n\n-----------------服务器返回的出餐口数量为0，关闭自动出餐----------------\n\n\n-");
                    return;
                } else {
                    CommonUtils.LogWuwei(tag, "-\n\n\n-----------------listSP.size() is" + listSP.size() + "-----------------");
                }

                if (listODI.size() == 0)//如果服务器没有返回的要打印订单，那就多等一会再进入下次递归
                {
                    CommonUtils.LogWuwei(tag, "---服务器没有返回的要打印订单，那就多等一会再进入下次递归-----");
                    CommonUtils.LogWuwei("checking...", "---服务器没有返回的要打印订单，那就多等一会再进入下次递归-----");
                    CommonUtils.LogWuwei(tag, "listODI size is 0");
                    autoCheckAgain(2, 1);
                    return;
                } else {
                    CommonUtils.LogWuwei(tag, "listODI size is " + listODI.size() + "arrayStoreMeals size is " + arrayStoreMeals.size());
                    CommonUtils.LogWuwei(tag, "listSP size is " + listSP.size() + " arryMealPorts size is " + arryMealPorts.size());
                }

                /**********************2、将获取自动出餐的标志位设为false，同时清除上次的出餐结果*********************/
                if (!autoPushMealIntentService.flagPrintOver) {
                    CommonUtils.LogWuwei(tag, "上次自动出餐任务还没结束。。。");
                    return;
                }
                autoPushMealIntentService.flagPrintOver = false;
                autoPushMealIntentService.listStoreMealPrintParams.clear();

                /**********************3、遍历订单数据，找到对应的打印机ip，并将打印记过放入listStoreMealPrintParams*********************/
                for (OrderDetailInfo odi : listODI) {
                    String number = odi.take_serial_number + "-" + odi.take_serial_seq;
                    CommonUtils.LogWuwei(tag, "content is " + odi.orderContent + " number is " + number);

                    /************得到需要打印的字节码*******************/
                    byte[] bytes = new byte[0];
                    try {
                        bytes = TemplateModulsParse.parseTemplateModuleAutoCheckout(odi, true);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    /************得到需要打印的ip地址*******************/
                    String ip = "";
                    for (StorePort sp : listSP) {
                        if (sp.port_id == odi.port_id) {
                            ip = sp.printerPeripheral.con_id;
                            break;
                        }
                    }
                    /************打印，如果打印失败重试3次，3次都失败则解除自动出餐关系*******************/
                    int result = OrderDetailInfo.writeToPrinterInfo(ctxt, bytes, ip);
                    if (result == BaseApi.PRINTER_ERROR) {
                        result = OrderDetailInfo.writeToPrinterInfo(ctxt, bytes, ip);//重试第一次
                        if (result == BaseApi.PRINTER_ERROR) {
                            result = OrderDetailInfo.writeToPrinterInfo(ctxt, bytes, ip);//重试第二次
                            if (result == BaseApi.PRINTER_ERROR) {
                                result = OrderDetailInfo.writeToPrinterInfo(ctxt, bytes, ip);//重试第三次
                                if (result == BaseApi.PRINTER_ERROR)//如果在重试三次之后每次都失败了，则进行重新登记
                                {
                                    List<MealPortRelation> list = new ArrayList<MealPortRelation>();
                                    MealPortRelation mpr = new MealPortRelation();
                                    mpr.port_id = odi.port_id;
                                    mpr.task_status = 0;//0解除任务关系 1建立任务关系
                                    mpr.printer_status = 2;//0 未连接 1正常连接 2无法打印
                                    mpr.checkout_type = 1;
                                    list.add(mpr);
                                    CommonUtils.LogWuwei(tag, "准备登记：解除自动的任务关系");
                                    registerPorts(list);
                                }
                            }
                        }

                    }

                    /************打印结果拼装*******************/
                    StoreMealAutoPrintParams smapp = new StoreMealAutoPrintParams();
                    smapp.repast_date = odi.repast_date;
                    smapp.take_serial_number = odi.take_serial_number;
                    smapp.take_serial_seq = odi.take_serial_seq;
                    smapp.port_id = odi.port_id;
                    if (result == BaseApi.PRINTER_ERROR) {
                        CommonUtils.LogWuwei(tag, "打印失败");
                        smapp.printer_status = 2;
                        smapp.printed = 0;
                    } else if (result == 0) {
                        CommonUtils.LogWuwei(tag, "打印成功");
                        smapp.printer_status = 1;
                        smapp.printed = 1;
                    }
                    autoPushMealIntentService.listStoreMealPrintParams.add(smapp);
                }

                /************标志位设为true，意味着可以进入下次自动出餐了*******************/
                autoPushMealIntentService.flagPrintOver = true;
                autoCheckAgain(1, 1);
                CommonUtils.LogWuwei(tag, "-------------------------");

            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.LogWuwei(tag,"---mealPushAutoPortCheckout error---"+response.error_message);
            }
        });
    }

    /**
     * 设置等待时间，并进入递归
     *
     * @param time      描述
     * @param order_num 从服务器要几单去打印
     */
    public static void autoCheckAgain(int time, int order_num) {
        CommonUtils.LogWuwei(tag, "------------autoCheckAgain start-------------");
        try {
            Thread.sleep(time * 5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            AutoPushMealUtils.autoCheckout(AutoPushMealUtils.appcopy_id, order_num, autoPushMealIntentService.listStoreMealPrintParams);
            CommonUtils.LogWuwei(tag, "------------autoCheckAgain end-------------");
        }


    }


    /**
     * 解析出餐口
     *
     * @param objArray
     * @return
     */
    public static List<StorePort> smpListDeal(JSONArray objArray) {
        List<StorePort> list = new ArrayList<StorePort>();

        try {
            for (int k = 0; k < objArray.size(); k++) {
                StorePort sp = new StorePort();

                JSONObject obj = objArray.getJSONObject(k);

                sp.printerPeripheral = new printer_peripheral();
                sp.callPeripheral = new call_peripheral();
                JSONObject objCall = obj.getJSONObject("call_peripheral");
                JSONObject objPrinter = obj.getJSONObject("printer_peripheral");

                sp.printerPeripheral.name = objPrinter.getString("name");
                sp.printerPeripheral.con_id = objPrinter.getString("con_id");

                sp.callPeripheral.name = objCall.getString("name");
                sp.callPeripheral.con_id = objCall.getString("con_id");

                sp.name = obj.getString("name");
                sp.port_id = obj.getLong("port_id");
                sp.letter = obj.getString("letter");
                sp.printer_peripheral_id = sp.printerPeripheral.peripheral_id;
                list.add(sp);
            }
        } catch (Exception e) {

        }

        return list;


    }


    /**
     * 解析订单详情
     *
     * @param objArray
     * @return
     */
    public static List<OrderDetailInfo> odiListDeal(JSONArray objArray) {
        List<OrderDetailInfo> orderDetailInfoList = new ArrayList<OrderDetailInfo>();
        CommonUtils.LogWuwei(tag, "objArray is " + objArray.toJSONString());

        for (int i = 0; i < objArray.size(); i++) {
            OrderDetailInfo odi = new OrderDetailInfo();
            JSONObject store_meal = objArray.getJSONObject(i);//得到订单列表中得一个订单

            StoreMeal sm = JSON.parseObject(CommonUtils.converBooleanToInt(((JSONObject) store_meal).toJSONString()), StoreMeal.class);
            odi.order_id = sm.order_id;
            odi.store_order = sm.store_order;
            odi.list_charge_items_all = sm.meal_charges;
            odi.take_serial_number = sm.take_serial_number;
            odi.take_serial_seq = sm.take_serial_seq;
            odi.store_order.take_serial_number = sm.take_serial_number;
            odi.store_order.take_serial_seq = sm.take_serial_seq;
            odi.packaged = sm.packaged;
            odi.take_mode = sm.take_mode;
            odi.timeBucketName = sm.store_order.store_time_bucket.name;
            odi.port_letter = sm.port_letter;
            odi.portCount = sm.port_count;
            odi.count = sm.count;
            odi.port_id = sm.port_id;
            odi.repast_date = sm.repast_date;


            for(ChargItem ci:odi.list_charge_items_all)
            {
                ci.listSubChargeItem = (ArrayList<ChargeItemSub>) JSONObject.parseArray(ci.subItems.toJSONString(), ChargeItemSub.class);
                List<String> nameList = new ArrayList<String>();
                for (ChargeItemSub cisub : ci.listSubChargeItem) {
                    cisub.mp = JSONObject.parseObject(cisub.product.toJSONString(), MealProduct.class);
                    nameList.add(cisub.mp.product_name);
                }
                if(ci.show_products == 1)//该收费项目是否需要显示本出餐口下的产品
                {
                    String addName = "";
                    if(nameList.size() == 1)
                    {
                        addName="("+nameList.get(0)+")";
                    }
                    else if(nameList.size() > 1)
                    {
                        addName+="(";
                        for(int k=0;k<nameList.size();k++)
                        {
                            if(k != nameList.size()-1)
                            {
                                addName+=nameList.get(k)+"+";
                            }
                            else
                            {
                                addName+=nameList.get(k);
                            }
                        }
                        addName+=")";
                    }
                    ci.charge_item_name+=addName;
                }

                String remark="";//该收费项目是否需要显示点餐备注信息
                List<String> remarkList = new ArrayList<String>();
                for(ChargeItemSub ciSub:ci.listSubChargeItem)
                {
                    if(!StringUtils.isEmpty(ciSub.remark))
                    {
                        remarkList.add(ciSub.mp.product_name+ciSub.remark);
                    }

                }
                if(remarkList.size() > 0)
                {
                    remark+="(";
                    for(int k=0;k<remarkList.size();k++)
                    {
                        if(k != remarkList.size()-1)
                        {
                            remark+=remarkList.get(k)+"、";
                        }
                        else
                        {
                            remark+=remarkList.get(k)+")";
                        }
                    }
                    ci.charge_item_name+=remark;
                }
            }

            if (odi.packaged == 1) {
                for (ChargItem ci : sm.meal_charges) {
                    odi.orderContentList += ci.charge_item_name + "×" + CommonUtils.DoubleDeal(ci.charge_item_amount) + "\n";
                }
            }

            //判断是否尾单
            if (sm.count == 0) {
                odi.flagLastItemInOrder = true;
            } else {
                odi.flagLastItemInOrder = false;
            }


            orderDetailInfoList.add(odi);//添加订单信息到订单列表
        }
        return orderDetailInfoList;
    }
}

