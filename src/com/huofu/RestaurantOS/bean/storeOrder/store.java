package com.huofu.RestaurantOS.bean.storeOrder;


import com.huofu.RestaurantOS.bean.BaseBean;

/*****
 * 店铺信息
 * @author kunyashaw
 *
 *"stores": [
        {
            "icomplete": 1,
            "staff_quantity": 7,
            "status": 2,
            "address": "",			//店铺地址
            "name": "test-店铺64",	//店铺名称
            "create_time": 1423224538969,
            "store_id": 64,		//店铺id
            "longitude": 0.0,	//位置经度
            "latitude": 0.0,		//位置纬度
            "merchant_id": 26	//商户id
        }
    ]
 */

public class store extends BaseBean{
	public int icomplete = -1;
	public int staff_quantity = -1;
	public int status = -1;
	
	public String address = "";
	public String name = "";
	
	public long create_time = 0;
	public int store_id = -1;
	public double longitude = 0;
	public double latitude = 0;
	public int merchant_id = 0;


//    public   store loadBeanWithMap(Map map){
//        Double doubleTmp = (Double) map.get("store_id");
//        this.store_id = doubleTmp.intValue();
//        this.name = (String)map.get("name");
//        return this;
//    };
	
}

