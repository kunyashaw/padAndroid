package com.huofu.RestaurantOS.api.common;

import com.alibaba.fastjson.JSON;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.utils.StringUtils;

/**
 * author: Created by zzl on 15/9/1.
 */
public class GetRecepitTemplateList extends BaseApi {

    public String defaultTemplateString = "[\n" +
            "    {\n" +
            "        \"purpose\": 1,\n" +
            "        \"purpose_id\": 1,\n" +
            "        \"rows\": [\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 1,\n" +
            "                \"row_string\": \"取餐单 (#TIMEBUCKET#) #STORENAME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"3\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 2,\n" +
            "                \"row_string\": \"#TICKETNUMBER#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"-----------------------------\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 4,\n" +
            "                \"row_string\": \"订单编号: #ORDERID#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 5,\n" +
            "                \"row_string\": \"下单时间: #CREATETIME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 3,\n" +
            "                \"row_string\": \"取号时间: #TAKETIME#\\n\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 6,\n" +
            "                \"row_string\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"-----------------------------\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 7,\n" +
            "                \"row_string\": \"应收: #PRICE#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 8,\n" +
            "                \"row_string\": \"实收: #PAYMENT#  #ODDCHARGE#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"\\n五味，放心吃\\n\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"purpose\": 2,\n" +
            "        \"purpose_id\": 1,\n" +
            "        \"rows\": [\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 1,\n" +
            "                \"row_string\": \"出餐单 (#TIMEBUCKET#) #STORENAME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"3\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 2,\n" +
            "                \"row_string\": \" #TICKETNUMBER#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"——————————————\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 4,\n" +
            "                \"row_string\": \"订单编号: #ORDERID#\\n\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"2\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 6,\n" +
            "                \"row_string\": \"#PRODUCTNAME# x #PRODUCTNUM#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"\\n——————————————\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 7,\n" +
            "                \"row_string\": \"应收: #PRICE#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 8,\n" +
            "                \"row_string\": \"实收: #PAYMENT# #ODDCHARGE#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 11,\n" +
            "                \"row_string\": \"\\n等待时间: #WAITTIME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 3,\n" +
            "                \"row_string\": \"取号时间: #TAKETIME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 5,\n" +
            "                \"row_string\": \"下单时间: #CREATETIME#\\n\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 100,\n" +
            "                \"row_string\": \"打印设备: #CLIENT_NAME#\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"purpose\": 3,\n" +
            "        \"purpose_id\": 2,\n" +
            "        \"rows\": [\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 1,\n" +
            "                \"row_string\": \"打包单 (#TIMEBUCKET#) #STORENAME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"3\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 2,\n" +
            "                \"row_string\": \" #TICKETNUMBER#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"3\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"*打包清单*\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"——————————————\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 4,\n" +
            "                \"row_string\": \"订单编号: #ORDERID#\\n\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"2\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 6,\n" +
            "                \"row_string\": \"#PRODUCTNAME# X #PRODUCTNUM#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"\\n——————————————\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 7,\n" +
            "                \"row_string\": \"应收: #PRICE#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 8,\n" +
            "                \"row_string\": \"实收: #PAYMENT# #ODDCHARGE#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 5,\n" +
            "                \"row_string\": \"下单时间: #CREATETIME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 3,\n" +
            "                \"row_string\": \"取号时间: #TAKETIME#\\n\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 11,\n" +
            "                \"row_string\": \"\\n等待时间: #WAITTIME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 100,\n" +
            "                \"row_string\": \"打印设备: #CLIENT_NAME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"\\n欢迎光临\\n\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"purpose\": 4,\n" +
            "        \"purpose_id\": 3,\n" +
            "        \"rows\": [\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 1,\n" +
            "                \"row_string\": \"外送单 (#TIMEBUCKET#) #STORENAME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"3\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 2,\n" +
            "                \"row_string\": \" #TICKETNUMBER#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"3\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"*外送清单*\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"——————————————\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 4,\n" +
            "                \"row_string\": \"订单编号: #ORDERID#\\n\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"2\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 6,\n" +
            "                \"row_string\": \"#PRODUCTNAME# X #PRODUCTNUM#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"\\n——————————————\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 7,\n" +
            "                \"row_string\": \"应收: #PRICE#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 8,\n" +
            "                \"row_string\": \"实收: #PAYMENT# #ODDCHARGE#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 11,\n" +
            "                \"row_string\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 5,\n" +
            "                \"row_string\": \"下单时间: #CREATETIME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 3,\n" +
            "                \"row_string\": \"取号时间: #TAKETIME#\\n\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 11,\n" +
            "                \"row_string\": \"\\n等待时间: #WAITTIME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 12,\n" +
            "                \"row_string\": \"预约送达时间: #CONTACT_REACH_TIME#\\n联系人：#CONTACT_NAME#\\n联系电话:#CONTACT_PHONE_NUMBER#\\n送餐地址：#CONTACT_ADDRESS#\\n发票信息：#CONTACT_INVOICE#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"0\",\n" +
            "                \"row_type\": 100,\n" +
            "                \"row_string\": \"打印设备: #CLIENT_NAME#\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"font_size\": \"1\",\n" +
            "                \"alignment\": \"1\",\n" +
            "                \"row_type\": 9,\n" +
            "                \"row_string\": \"\\n欢迎光临\\n\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "]";

    @Override
    public String getApiAction() {
        return "receipttpl";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if (this.isResponseOk(response)) {

            //如果小票模板为空，或者json格式不对，这里会自动套用默认模板
            try {
                com.alibaba.fastjson.JSONObject objTmp = response.jsonObject.getJSONObject("receipt_tpl");

                String content = objTmp.getString("content");
                //处理服务器模板为空的情况
                if (StringUtils.isEmpty(content)) {
                    content = defaultTemplateString;
                }
                com.alibaba.fastjson.JSONArray arrayContent = JSON.parseArray(content);//new com.alibaba.fastjson.JSONArray(content);
                if (arrayContent.size() < 1) {
                    arrayContent = JSON.parseArray(defaultTemplateString);
                }

                if (arrayContent != null && arrayContent.size() >= 1) {
                    for (int k = 0; k < arrayContent.size(); k++) {
                        com.alibaba.fastjson.JSONObject objTp = arrayContent.getJSONObject(k);
                        int purpose = objTp.getInteger("purpose");
                        int purpose_id = objTp.getInteger("purpose_id");
                        com.alibaba.fastjson.JSONArray array = objTp.getJSONArray("rows");
                        LocalDataDeal.writeToLocalBasicTemplateInfo(array.toString(), purpose, MainApplication.getContext());
                    }
                }
            } catch (Exception e) {

                //处理服务器模板格式不对的情况
                com.alibaba.fastjson.JSONArray arrayContent = JSON.parseArray(defaultTemplateString);//new com.alibaba.fastjson.JSONArray(content);
                if (arrayContent.size() < 1) {
                    arrayContent = JSON.parseArray(defaultTemplateString);
                }

                if (arrayContent != null && arrayContent.size() >= 1) {
                    for (int k = 0; k < arrayContent.size(); k++) {
                        com.alibaba.fastjson.JSONObject objTp = arrayContent.getJSONObject(k);
                        int purpose = objTp.getInteger("purpose");
                        int purpose_id = objTp.getInteger("purpose_id");
                        com.alibaba.fastjson.JSONArray array = objTp.getJSONArray("rows");
                        LocalDataDeal.writeToLocalBasicTemplateInfo(array.toString(), purpose, MainApplication.getContext());
                    }
                }
            }


        }
        return response;
    }
}
