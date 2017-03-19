package com.huofu.RestaurantOS.api.pushMeal;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.pushMeal.StoreMeal;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBean;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBeanGroup;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.ChargeItemSub;
import com.huofu.RestaurantOS.bean.storeOrder.MealProduct;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.manager.TicketsManager;
import com.huofu.RestaurantOS.ui.pannel.call.ClientSpeak;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.StringUtils;
import com.huofu.RestaurantOS.utils.templateModulsParse.TemplateModulsParse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Created by zzl on 15/10/23.
 */
public class SetPortMealCheckout extends BaseApi {
    org.json.JSONArray array = new org.json.JSONArray();
    String content = "";
    static Handler handler;
    OrderDetailInfo odiArg;

    public SetPortMealCheckout(org.json.JSONArray array, String content,Handler handler) {
        this.array = array;
        this.content = content;
        this.handler = handler;
    }

    @Override
    public String getApiAction() {
        return "5wei/meal/port/checkout";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {

        if (this.isResponseOk(response)) {

            response =  dealResult(response,0);
        }

        return response;
    }


    /**
     *
     * @param response
     * @param flagAll 1是右滑全部出餐 0是部分出餐或者单击出餐
     * @return
     */
    public static ApiResponse dealResult(ApiResponse response,int flagAll)
    {
        Context ctxt = MainApplication.getContext();
        OrderDetailInfo odi = new OrderDetailInfo();
        com.alibaba.fastjson.JSONArray store_meal_list = new JSONArray();
        String ip = (String) (LocalDataDeal.readFromLocalIpKitchPrinter(ctxt)).get("ip");


        if(flagAll == 1)
        {
            store_meal_list = response.jsonObject.getJSONArray("store_meals");
        }
        else
        {
            com.alibaba.fastjson.JSONObject store_meal = response.jsonObject.getJSONObject("store_meal");
            store_meal_list.add(store_meal);
        }
        CommonUtils.sendMsg("打印中",PushMealActivity.SHOW_LOADING_TEXT,handler);
        for(Object store_meal:store_meal_list)
        {

            StoreMeal sm = JSON.parseObject(CommonUtils.converBooleanToInt(((JSONObject)store_meal).toJSONString()), StoreMeal.class);
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

            for(ChargItem ci:odi.list_charge_items_all)
            {

                ci.listSubChargeItem = (ArrayList<ChargeItemSub>) JSONObject.parseArray(ci.subItems.toJSONString(), ChargeItemSub.class);
                if(ci.show_products == 1)//该收费项目是否需要显示本出餐口下的产品
                {
                    List<String> nameList = new ArrayList<String>();
                    for (ChargeItemSub cisub : ci.listSubChargeItem) {
                        cisub.mp = JSONObject.parseObject(cisub.product.toJSONString(), MealProduct.class);
                        nameList.add(cisub.mp.product_name);
                    }

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


            //判断是否尾单
            if (sm.count == 0) {
                odi.flagLastItemInOrder = true;
            } else {
                odi.flagLastItemInOrder = false;
            }

            int printResult = 0;
            int purpose = 2;
            //判断是否第一个打包或者外送的单子
            if (odi.take_mode != 4) {
                if (odi.packaged == 1)//如果是打包单子或者是take_mode=3的订单的第一个打包订单
                {
                    if (sm.packaged_seq == 1)//打印打包清单
                    {
                        try {
                            odi.flagPrintList = true;
                            odi.orderContentList = getOrderContentList(odi.order_id,odi.take_serial_number);
                            printResult = OrderDetailInfo.writeToPrinterInfo(ctxt,
                                    TemplateModulsParse.getInstance().parseTemplateModuleManualCheckout(odi, 3),
                                    (String) (LocalDataDeal.readFromLocalIpKitchPrinter(ctxt)).get("ip"));
                            purpose = 3;
                        } catch (Exception e) {
                            printResult = -1;
                        }
                        CommonUtils.LogWuwei(tag, "打印打包清单");
                    }
                }

            } else if (odi.take_mode == 4) {

                if (odi.take_serial_seq == 1)//打印外送清单
                {
                    try {
                        odi.flagPrintList = true;
                        odi.orderContentList = getOrderContentList(odi.order_id,odi.take_serial_number);
                        printResult = OrderDetailInfo.writeToPrinterInfo(ctxt,
                                TemplateModulsParse.getInstance().parseTemplateModuleManualCheckout(odi, 4),
                                (String) (LocalDataDeal.readFromLocalIpKitchPrinter(ctxt)).get("ip"));
                        purpose = 4;
                    } catch (Exception e) {
                        printResult = -1;
                    }
                    CommonUtils.LogWuwei(tag, "打印外送清单");
                }
            }

            try {
                printResult = OrderDetailInfo.writeToPrinterInfo(ctxt,
                        TemplateModulsParse.getInstance().parseTemplateModuleManualCheckout(odi, 2),
                        (String) (LocalDataDeal.readFromLocalIpKitchPrinter(ctxt)).get("ip"));
                purpose = 2;
            } catch (Exception e) {
                printResult = -1;
            }

            if(StringUtils.isEmpty(ip))
            {
                CommonUtils.sendMsg("当前出餐口设置为不打印",PushMealActivity.SHOW_TOAST,handler);
            }
            else
            {
                if(printResult == -1)
                {
                    CommonUtils.sendObjMsg("",PushMealActivity.HIDE_LOADING,handler);
                    Map map = new HashMap();
                    map.put("Odi",odi);
                    map.put("purpose", purpose);
                    CommonUtils.sendObjMsg(map, PushMealActivity.SHOW_ERROR_MESSAGE_PRINT_AGAIN, handler);

                }
            }



        }


        //电视叫号
        if(flagAll != 1 && odi.packaged == 0)
        {
            /**
             * 将是否自动叫号存到本地,
             * flag->0 出餐不叫号
             * flag->1 出餐叫号
             * flag->2 尾单叫号
             */
            int way = LocalDataDeal.readFromAutoCall(ctxt);
            switch (way)
            {
                case 1:
                    ClientSpeak.ClientSpeak(odi.take_serial_number,ctxt);
                    break;
                case 2:
                    if(odi.flagLastItemInOrder)
                    {
                        ClientSpeak.ClientSpeak(odi.take_serial_number,ctxt);
                    }
                    break;
            }
        }

        response.parseData = odi;
        return  response;
    }

    public static String getOrderContentList(String orderId,int takeSerialNum)
    {
        List<ChargItem> listCI = new ArrayList<ChargItem>();
        List<TicketBeanGroup> listTBG =  TicketsManager.getInstance().ticketGroups;
        for(TicketBeanGroup tbg:listTBG)
        {
            if(tbg.order_id.equals(orderId) && tbg.packaged == 1 && takeSerialNum == tbg.take_serial_number)
            {
                for(TicketBean tb:tbg.tickets)
                {
                    for(ChargItem ci:tb.charge_items)
                    {
                        listCI.add(ci);
                    }
                }
            }
        }

        Map<Long, Integer> ChargeItemIdNumMap = new HashMap<Long, Integer>();
        for(ChargItem ci:listCI)
        {
            if(ChargeItemIdNumMap.containsKey(ci.charge_item_id))
            {
                ci.charge_item_amount+=ChargeItemIdNumMap.get(ci.charge_item_id);
                ChargeItemIdNumMap.put(ci.charge_item_id,ci.charge_item_amount);
            }
            else
            {
                ChargeItemIdNumMap.put(ci.charge_item_id,ci.charge_item_amount);
            }
        }

        String orderNameList = "";
        for(Long id:ChargeItemIdNumMap.keySet())
        {
            for(ChargItem ci:listCI)
            {
                if(ci.charge_item_id == id)
                {
                    orderNameList+=ci.charge_item_name+"×"+CommonUtils.DoubleDeal(ChargeItemIdNumMap.get(id))+"\n";
                    break;
                }
            }
        }
    return orderNameList;
    }
}
