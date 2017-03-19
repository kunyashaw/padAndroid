package com.huofu.RestaurantOS.api.setting;

import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.utils.CommonUtils;

/**
 * author: Created by zzl on 15/8/27.
 */
public class SaveOrAddPeripheral extends BaseApi{

    @Override
    public String getApiAction() {
        return  "peripheral/save_v2";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {
        if(this.isResponseOk(response))
        {
            CommonUtils.LogWuwei(tag,"result is "+response.jsonObject.toString());
        }
        return response;
    }
}
