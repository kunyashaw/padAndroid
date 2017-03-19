package com.huofu.RestaurantOS.utils.templateModulsParse;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.Enum.TemplateTypeEnum;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.TemplateModuleKeyValue;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.StringUtils;
import com.huofu.RestaurantOS.utils.printer.printerCmdUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/10/14.
 */
public class TemplateModulsParse {

    public static Context ctxt = MainApplication.getContext();
    public static String storeName = LocalDataDeal.readFromLocalStoreName(ctxt);
    public static List<MealBucket> mealBucketList = new ArrayList<MealBucket>();
    public static String clientName = LocalDataDeal.readFromLocalAppCopyName(ctxt);

    private static TemplateModulsParse ourInstance = new TemplateModulsParse();

    public static TemplateModulsParse getInstance() {
        return ourInstance;
    }


    /***
     * 自动打印小票模板解析
     *
     * @param odi          订单信息
     * @param flagPushMeal 为true则是出餐单，false则为取餐单
     * @return 支持ESC/POS指令集的可打印的字节流
     */
    public synchronized static byte[] parseTemplateModuleAutoCheckout(OrderDetailInfo odi, boolean flagPushMeal) throws UnsupportedEncodingException {

        if (mealBucketList.size() == 0) {
            ApisManager.getAllMealPortList(new ApiCallback() {
                @Override
                public void success(Object object) {
                    mealBucketList = (List<MealBucket>) object;
                }

                @Override
                public void error(BaseApi.ApiResponse response) {

                }
            });
        }

        //1、首先确定用哪个模板
        int purpose = -1;
        if (flagPushMeal) {
            switch (odi.store_order.take_mode) {
                case 1://堂食
                    purpose = 2;
                    break;
                case 2://打包清单
                    purpose = 3;
                    break;
                case 4://外送清单
                    purpose = 4;
                    break;
                case 3://既有堂食又有打包
                    purpose = 2;
                    break;
            }
        } else {
            purpose = 1;
        }

        return parseTemplateModuleManualCheckout(odi, purpose);
    }



    /***
     * 手动打印小票模板解析
     *
     * @param odi          订单信息
     * @param purpose 1为取餐模板 2为出餐模板：非清单性质的堂食、打包、外送 3为打包清单 4为外送清单
     * @return 支持ESC/POS指令集的可打印的字节流
     */
    public synchronized static byte[] parseTemplateModuleManualCheckout(OrderDetailInfo odi, int purpose) throws UnsupportedEncodingException {

        List<byte[]> byteList = new ArrayList<byte[]>();
        if (mealBucketList.size() == 0) {
            ApisManager.getAllMealPortList(new ApiCallback() {
                @Override
                public void success(Object object) {
                    mealBucketList = (List<MealBucket>) object;
                }

                @Override
                public void error(BaseApi.ApiResponse response) {

                }
            });
        }


        //2、得到小票模板
        String rows = (LocalDataDeal.readFromLocalBaiscTemplateInfo(purpose, MainApplication.getContext()));
        JSONArray array = JSONObject.parseArray(rows);

        if (array != null && array.size() > 0) {

            String templateContent = "";
            //遍历小票模板的每一行
            for (int k = 0; k < array.size(); k++) {
                JSONObject obj = array.getJSONObject(k);
                TemplateModuleKeyValue tmkv = JSONObject.parseObject(obj.toJSONString(), TemplateModuleKeyValue.class);
                byteList.add(printerCmdUtils.fontSizeSetBig(Integer.parseInt(tmkv.font_size)));

                //如果是收费项目名称，则判断内容是否为空，如果不为空，则替换#号间的内容；如果为空则自己组织成数量金额那种格式
                if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_ORDER_BODY.getVaule()) {

                    if(purpose == 4 || purpose == 3)
                    {
                        String str = odi.orderContentList;
                        templateContent+=str+"\n";
                        byteList.add(str.getBytes("gb2312"));
                    }
                    else
                    {
                        if (tmkv.row_string.equals(""))//自己组织
                        {
                            String amountMoneyStr = "\n        数量          金额";

                            String str = "";
                            str += (amountMoneyStr + "\n" + "-----------------------------\n");

                            int index = 0;
                            for (int t = 0; t < odi.list_charge_items_all.size(); t++) {
                                ChargItem ci = odi.list_charge_items_all.get(t);
                                str += ci.charge_item_name + "\n";
                                str += ("        " + CommonUtils.DoubleDeal(ci.charge_item_amount) + "            " + ci.charge_item_price * ci.charge_item_amount / 100.0 + "元");
                            }
                            if (odi.store_order.delivery_fee != 0) {
                                str += "外送费\n";
                                str += ("        " + 1 + "            " + (odi.store_order.delivery_fee) / 100.0 + "元\n");
                            }
                            templateContent+=str+"\n";
                            byteList.add(str.getBytes("gb2312"));
                        } else {
                            if(!StringUtils.isEmpty(odi.orderContent))
                            {
                                if(odi.orderContent.length() > 0)
                                {
                                    byteList.add(odi.orderContent.getBytes("gb2312"));
                                    templateContent+=odi.orderContent+"\n";
                                }
                            }
                            else
                            {
                                for (int t = 0; t < odi.list_charge_items_all.size(); t++) {
                                    ChargItem ci = odi.list_charge_items_all.get(t);

                                    String str = tmkv.row_string.replace("#PRODUCTNAME#", ci.charge_item_name);
                                    str = str.replace("#PRODUCTNUM#", CommonUtils.DoubleDeal(ci.charge_item_amount));
                                    byteList.add(printerCmdUtils.fontSizeSetBig(Integer.parseInt(tmkv.font_size)));
                                    int alignment = Integer.parseInt(tmkv.alignment);
                                    switch (alignment) {
                                        case 0:
                                            byteList.add(printerCmdUtils.alignLeft());
                                            break;
                                        case 1:
                                            byteList.add(printerCmdUtils.alignCenter());
                                            break;
                                        case 2:
                                            byteList.add(printerCmdUtils.alignRight());
                                            break;
                                    }
                                    templateContent+=str+"\n";
                                    byteList.add(str.getBytes("gb2312"));
                                    byteList.add(printerCmdUtils.nextLine(1));
                                }
                            }

                        }
                    }


                }

                //营业时间段和店铺名称
                if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_HEAD.getVaule()) {
                    String str = tmkv.row_string.replace("#TIMEBUCKET#", odi.timeBucketName);
                    str = str.replace("#STORENAME#", storeName);
                    templateContent+=str+"\n";
                    byteList.add(str.getBytes("gb2312"));
                }
                //流水号、序号、出餐口信息
                else if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_TAKE_NUMBER.getVaule()) {
                    String str = "";
                    String number = odi.getSerialNumber(purpose);
                    if(purpose == 2)
                    {
                        if(odi.packaged == 1)
                        {
                            if(odi.store_order.take_mode == 4)
                            {
                                number+="\n(外送单)";
                            }
                            else
                            {
                                number+="\n(打包单)";
                            }
                        }
                    }
                    str = tmkv.row_string.replace("#TICKETNUMBER#", number);
                    if(purpose == 3 || purpose == 4)
                    {
                        str = str.replace("-尾","");
                    }
                    templateContent+=str+"\n";
                    byteList.add(str.getBytes("gb2312"));

                }
                //订单id
                else if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_ORDER_ID.getVaule()) {
                    String str = tmkv.row_string.replace("#ORDERID#", odi.order_id);
                    byteList.add(str.getBytes("gb2312"));
                    templateContent+=str+"\n";
                }
                //下单时间
                else if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_ORDER_TIME.getVaule()) {
                    String takeTime = "";
                    if (odi.store_order.take_serial_time != 0) {
                        takeTime = CommonUtils.getStrTime("" + odi.store_order.take_serial_time);
                    } else {
                        takeTime = CommonUtils.getStrTime("" + odi.store_order.update_time);
                    }

                    String str = tmkv.row_string.replace("#CREATETIME#", takeTime);
                    byteList.add(str.getBytes("gb2312"));
                    templateContent+=str+"\n";
                }
                //取号时间
                else if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_TAKE_TIME.getVaule()) {
                    String addTime = CommonUtils.getStrTime("" + odi.store_order.create_time);
                    String str = tmkv.row_string.replace("#TAKETIME#", addTime);
                    byteList.add(str.getBytes("gb2312"));
                    templateContent+=str+"\n";
                }
                //应收价格
                else if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_ORDER_PRICE.getVaule()) {
                    String str = tmkv.row_string.replace("#PRICE#", "" + odi.getOrderPrice());
                    byteList.add(str.getBytes("gb2312"));
                    templateContent+=str+"\n";
                }
                //实收价格
                else if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_ORDER_PAYMENT.getVaule()) {
                    String str = tmkv.row_string.replace("#PAYMENT#", "" + odi.getShouldPrice());
                    if (odi.getGuestBackPrice() > 0) {
                        if (odi.store_order.client_type == 1) {//非网单
                            str = str.replace("#ODDCHARGE#", "找零: " + odi.getGuestBackPrice());
                        } else {//网单
                            str = str.replace("#ODDCHARGE#", "优惠: " + odi.getGuestBackPrice());
                        }
                    }
                    else
                    {
                        str = str.replace("#ODDCHARGE#", "");
                    }
                    byteList.add(str.getBytes("gb2312"));
                    templateContent+=str+"\n";
                }
                //任意字符串
                else if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_FOOTER.getVaule()) {
                    byteList.add(tmkv.row_string.getBytes("gb2312"));
                    templateContent+=tmkv.row_string+"\n";
                }
                //等待时间
                else if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_ORDER_WAITING_TIME.getVaule()) {
                    String waitingTime = "";
                    if (odi.store_order.meal_checkout_time <= 0) {
                        waitingTime = "";
                        continue;
                    } else {
                        waitingTime = CommonUtils.getTimeDiffToNow(odi.store_order.take_serial_time / 1000, odi.store_order.meal_checkout_time / 1000);
                        String str = tmkv.row_string.replace("#WAITTIME#", waitingTime);
                        byteList.add(str.getBytes("gb2312"));
                        templateContent+=str+"\n";
                    }

                }
                //外送信息
                else if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_ORDER_DELIVERY_CONTACT_INFO.getVaule()) {
                    try
                    {
                        String str = tmkv.row_string.replace("#CONTACT_NAME#", odi.store_order.store_order_delivery.contact_name);
                        str = str.replace("#CONTACT_PHONE_NUMBER#", odi.store_order.store_order_delivery.contact_phone);
                        str = str.replace("#CONTACT_ADDRESS#", odi.store_order.store_order_delivery.delivery_building_name + " " + odi.store_order.store_order_delivery.delivery_building_address + " " + odi.store_order.store_order_delivery.user_address);
                        str = str.replace("#CONTACT_INVOICE#", odi.store_order.invoice_demand);
                        str = str.replace("#CONTACT_REACH_TIME#", CommonUtils.getStrTime("" + odi.store_order.store_order_delivery.delivery_assign_time) + "\n");
                        byteList.add(str.getBytes("gb2312"));
                        templateContent+=str+"\n";
                    }
                    catch (Exception e)
                    {

                    }

                }
                //副本信息
                else if (tmkv.row_type == TemplateTypeEnum.PRINTROW_TYPE_CLIENT_NAME.getVaule()) {
                    String str = tmkv.row_string.replace("#CLIENT_NAME#", clientName);
                    byteList.add(str.getBytes("gb2312"));
                    templateContent+=str+"\n";
                }


                int alignment = Integer.parseInt(tmkv.alignment);
                switch (alignment) {
                    case 0:
                        byteList.add(printerCmdUtils.alignLeft());
                        break;
                    case 1:
                        byteList.add(printerCmdUtils.alignCenter());
                        break;
                    case 2:
                        byteList.add(printerCmdUtils.alignRight());
                        break;
                }
                byteList.add(printerCmdUtils.nextLine(1));
                byteList.add(printerCmdUtils.fontSizeSetSmall(0));
            }//结束遍历小票模板的每一行
            CommonUtils.LogWuwei(PushMealActivity.tag,"小票信息如下:\n"+templateContent);
        }
        byteList.add(printerCmdUtils.feedPaperCutAll());
        return printerCmdUtils.byteMerger(byteList);

    }

}
