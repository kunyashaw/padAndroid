package com.huofu.RestaurantOS.api.appcopy;

import android.content.Context;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.AppcopyBean;
import com.huofu.RestaurantOS.bean.MerchantBean;
import com.huofu.RestaurantOS.bean.storeOrder.store;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tim on 7/8/15.
 */
public class AppCopyInfo extends BaseApi {


    public  String  getApiAction(){
        return "appcopy/info";
    }
    @Override
    public ApiResponse responseObjectParse(ApiResponse response) {
        if (this.isResponseOk(response)) {
            HashMap<String ,Object> res = new HashMap<String ,Object>();
            res.put("appcopy",response.jsonObject.getObject("appcopy", AppcopyBean.class));
            res.put("store",response.jsonObject.getObject("store", store.class));
            res.put("merchant",response.jsonObject.getObject("merchant", MerchantBean.class));
            response.parseData = res;

            CommonUtils.LogWuwei(tag,response.toString());

            Map<String, Object> map = (Map<String, Object>) res;
            AppcopyBean appcopy = (AppcopyBean) map.get("appcopy");
            store storeInfo = (store) map.get("store");
            MerchantBean merchantInfo = (MerchantBean) map.get("merchant");

            Context ctxt = MainApplication.getContext();
            LocalDataDeal.writeToLocalStoreName(storeInfo.name, ctxt);
            LocalDataDeal.writeToLocalStoreId(storeInfo.store_id, ctxt);
            LocalDataDeal.writeToLocalMerchantName(merchantInfo.name, ctxt);
            LocalDataDeal.writeToLocalMerchantId(merchantInfo.merchant_id.intValue(), ctxt);
            LocalDataDeal.writeToLocalAppCopyName(appcopy.name, ctxt);
            LocalDataDeal.writeToLocalAppCopyID(appcopy.appcopy_id,ctxt);
        }
        return response;
    }

}
