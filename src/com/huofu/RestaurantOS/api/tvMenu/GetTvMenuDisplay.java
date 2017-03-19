package com.huofu.RestaurantOS.api.tvMenu;

import com.huofu.RestaurantOS.api.BaseApi;

/**
 * author: Created by zzl on 15/11/20.
 * 获取指定营业时间段的收费项目信息
 */
public class GetTvMenuDisplay extends BaseApi{
    @Override
    public String getApiAction() {
        return "5wei/store/timebucket/menu";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            response.parseData = response.jsonObject;
        }
        return  response;
    }
}
