package com.huofu.RestaurantOS.api.stockSupplyAndPlan;

import com.alibaba.fastjson.JSONArray;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.bean.StoreProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/8/13.
 */

/***
 * 根据产品id，查询产品所属收费项目对应的周期营业时段，设置过库存则显示设置信息，没有设置过则显示默认信息
 */
public class QueryWeekInventory extends BaseApi {

    public String getApiAction() {
        return "5wei/inventory/week_query_by_product";
    }

    @Override
    public BaseApi.ApiResponse responseObjectParse(BaseApi.ApiResponse response) {
        if (this.isResponseOk(response)) {
            List<StoreProduct> lsCurrentWeek = new ArrayList<StoreProduct>();
            List<StoreProduct> lsNextWeek = new ArrayList<StoreProduct>();
            List<StoreProduct> lsAll = new ArrayList<StoreProduct>();

            JSONArray listCurrentWeek = response.jsonObject.getJSONArray("current_inventory_weeks");
            JSONArray listNextWeek = response.jsonObject.getJSONArray("nextweek_inventory_weeks");

            /*if(StockPlanActivity.flagActivte)
            {
                if(listCurrentWeek.size() == 0)
                {
                    for(int k=0;k<StockPlanActivity.listMealBucket.size();k++)
                    {
                        for(int t=1;t<=7;t++)
                        {
                            StoreProduct sp = new StoreProduct();
                            sp.product_id = StockPlanActivity.nowChooseProductId;
                            sp.thisWeek = true;
                            sp.amount = 0;
                            sp.week_day = t;
                            sp.time_bucket_id = StockPlanActivity.listMealBucket.get(k).time_bucket_id;
                            sp.StoreTimeBucket = StockPlanActivity.listMealBucket.get(k);
                            listCurrentWeek.add(JSON.toJSON(sp));

                        }
                    }
                }

                if(listNextWeek.size() == 0)
                {
                    for(int k=0;k<StockPlanActivity.listMealBucket.size();k++)
                    {
                        for(int t=1;t<=7;t++)
                        {
                            StoreProduct sp = new StoreProduct();
                            sp.product_id = StockPlanActivity.nowChooseProductId;
                            sp.thisWeek = false;
                            sp.amount = 0;
                            sp.week_day = t;
                            sp.time_bucket_id = StockPlanActivity.listMealBucket.get(k).time_bucket_id;
                            sp.StoreTimeBucket = StockPlanActivity.listMealBucket.get(k);
                            listNextWeek.add(JSON.toJSON(sp));
                        }
                    }
                }


            }*/

            /************得到本周数据*******************/
            for (int k = 0; k < listCurrentWeek.size(); k++) {
                lsCurrentWeek.add(listCurrentWeek.getObject(k, StoreProduct.class));
                lsAll.add(listCurrentWeek.getObject(k, StoreProduct.class));
            }


            /************得到下周数据*******************/
            for (int k = 0; k < listNextWeek.size(); k++) {
                lsNextWeek.add(listNextWeek.getObject(k, StoreProduct.class));
                lsAll.add(listNextWeek.getObject(k, StoreProduct.class));
            }

            /************得到营业时间段列表*******************/
            List<Integer> listTimeBucket = new ArrayList<Integer>();//存储每个营业时间段

            for (int k = 0; k < lsAll.size(); k++) {
                boolean flagAdd = true;
                for (int t = 0; t < listTimeBucket.size(); t++) {
                    if (listTimeBucket.get(t) == lsAll.get(k).store_time_bucket.time_bucket_id) {
                        flagAdd = false;
                        break;
                    }
                }
                if (flagAdd) {
                    listTimeBucket.add(lsAll.get(k).store_time_bucket.time_bucket_id);
                }
            }

            List<List<StoreProduct>> listAllSP = new ArrayList<List<StoreProduct>>();//分别存储本周各个营业时间段对应的storeProduct

            for (int t = 0; t < listTimeBucket.size(); t++) {
                int timeBucketId = listTimeBucket.get(t);

                List<StoreProduct> listTimeBucketSP = new ArrayList<StoreProduct>();
                for (int k = 0; k < lsCurrentWeek.size(); k++) {
                    if (timeBucketId == lsCurrentWeek.get(k).store_time_bucket.time_bucket_id) {
                        lsCurrentWeek.get(k).thisWeek = true;
                        listTimeBucketSP.add(lsCurrentWeek.get(k));
                    }
                }

                for (int k = 0; k < lsNextWeek.size(); k++) {
                    if (timeBucketId == lsNextWeek.get(k).store_time_bucket.time_bucket_id) {
                        lsNextWeek.get(k).thisWeek = false;
                        listTimeBucketSP.add(lsNextWeek.get(k));
                    }
                }

                listAllSP.add(listTimeBucketSP);
            }

            response.parseData = listAllSP;
        }
        return response;
    }
}
