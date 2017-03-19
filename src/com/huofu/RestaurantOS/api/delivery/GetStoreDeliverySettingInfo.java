package com.huofu.RestaurantOS.api.delivery;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

/**
 * author: Created by zzl on 15/8/28.
 */
public class GetStoreDeliverySettingInfo extends BaseApi{

    @Override
    public String getApiAction() {
        return "5wei/delivery/setting/info";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            CommonUtils.LogWuwei("","result is "+response.jsonObject.toString());
            LocalDataDeal.writeToLocalStoreDeliverySettingInfo(response.jsonObject.toJSONString(), MainApplication.getContext());
        }
        return response;
    }
}
