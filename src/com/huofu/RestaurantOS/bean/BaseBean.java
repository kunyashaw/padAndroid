package com.huofu.RestaurantOS.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by tim on 7/8/15.
 */
public  abstract class BaseBean {

    public BaseBean parseWithJsonObject(JSONObject object){
        return this;
    }
}
