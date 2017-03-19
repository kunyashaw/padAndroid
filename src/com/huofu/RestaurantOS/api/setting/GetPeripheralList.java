package com.huofu.RestaurantOS.api.setting;

/**
 * author: Created by zzl on 15/8/27.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;


/***
 * 获取店铺所有的外接设备(这里指打印机)
 */
public class GetPeripheralList extends BaseApi{

    @Override
    public String getApiAction() {
        return "store/peripheral/list";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {

        if(this.isResponseOk(response))
        {
            JSONArray array  = (JSONArray)(response.jsonObject.getJSONArray("peripherals"));
            CommonUtils.LogWuwei(SettingsActivity.tag,CommonUtils.logInfoFormatTojson(array.toString()));

            List<peripheral> listAll = new ArrayList<peripheral>();
            List<peripheral> list = new ArrayList<peripheral>();

            listAll = JSON.parseArray(array.toJSONString(),peripheral.class);
            for(int k=0;k<listAll.size();k++)
            {
                if(listAll.get(k).Ipos ==null)
                {
                    list.add(listAll.get(k));
                }
            }
            CommonUtils.LogWuwei("", "str is " + response.jsonObject.toString());

            response.parseData = list;
        }
        return response;
    }
}
