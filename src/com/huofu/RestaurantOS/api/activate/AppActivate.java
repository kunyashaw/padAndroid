package com.huofu.RestaurantOS.api.activate;

import android.content.Context;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
/**
 * author: Created by zzl on 15/8/21.
 */
public class AppActivate extends BaseApi {

    @Override
    public String getApiAction() {
        return "appcopy/activate";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {

        if (this.isResponseOk(response)) {
            Context ctxt = MainApplication.getContext();
            String client_id = response.jsonObject.getString("client_id");
            String master_key = response.jsonObject.getString("master_key");
            CommonUtils.LogWuwei(tag, "client_id is " + client_id);
            LocalDataDeal.writeToLocalClientId(client_id, ctxt);
            LocalDataDeal.writeToLocalMasterKey(master_key, ctxt);
        }
        return super.responseObjectParse(response);
    }
}
