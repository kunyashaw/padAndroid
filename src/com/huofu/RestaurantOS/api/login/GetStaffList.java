package com.huofu.RestaurantOS.api.login;

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.user.LoginStaff;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/24.
 */
public class GetStaffList extends BaseApi{

    @Override
    public String getApiAction() {
        return "store/login/staff/list";
    }

    @Override
    protected ApiResponse responseObjectParse(ApiResponse response) {

        if(this.isResponseOk(response))
        {
            JSONArray listStaff = response.jsonObject.getJSONArray("list");
            List<LoginStaff> ls = new ArrayList<LoginStaff>();
            int lastLoginStaffId = LocalDataDeal.readFromLocalLastChooseStaffId(MainApplication.getContext());

            for(int k=0;k<listStaff.size();k++) {
                LoginStaff loginStaff = listStaff.getObject(k, LoginStaff.class);
                loginStaff.flagStaffChoosen = false;
                if(loginStaff.staff_id == lastLoginStaffId)
                {
                    loginStaff.flagStaffChoosen = true;
                }
                ls.add(loginStaff);
            }
            response.parseData = ls;
        }
        return response;
    }
}
