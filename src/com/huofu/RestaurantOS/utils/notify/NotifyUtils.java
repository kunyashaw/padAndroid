package com.huofu.RestaurantOS.utils.notify;

import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;

/**
 * author: Created by zzl on 15/10/21.
 */
public class NotifyUtils {

    public static void webchatNotify(final int CallNum)
    {
        ApisManager.NotifySendWebChat(CallNum, new ApiCallback() {
            @Override
            public void success(Object object) {
                //CommonUtils.LogWuwei(MealDoneActivity.tag, CallNum + "微信通知成功");
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                //CommonUtils.LogWuwei(MealDoneActivity.tag,CallNum+"微信通知失败");
            }
        });
    }

}
