package com.huofu.RestaurantOS.manager;

import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBean;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBeanGroup;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.ChargeItemSub;
import com.huofu.RestaurantOS.bean.storeOrder.MealProduct;
import com.huofu.RestaurantOS.bean.storeOrder.StoreProduct;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by tim on 12/11/15.
 */
public class TicketsManager {

    public String tag = PushMealActivity.tag;
    public static Handler handler;
    private static TicketsManager ourInstance = new TicketsManager();

    public static TicketsManager getInstance() {
        return ourInstance;
    }

    /**
     * 最新的出餐序列号
     */
    public Integer latestNumber = 0;

    /**
     * 左侧组列表，读写时得加锁
     */
    public List<TicketBeanGroup> ticketGroups = new ArrayList<TicketBeanGroup>();
    List<StoreProduct> listStoreProduct = new ArrayList<StoreProduct>();
    public boolean stopCheck = true;
    public boolean isChecking = false;

    private TicketsManager() {
    }

    /**
     * 监控有没有新的ticket，可由心跳代替
     */
    public synchronized void startCheckNewTickets(final int port_id) {

        try {
            if (stopCheck) {
                this.isChecking = false;
                return;

            } else {
                this.isChecking = true;
                ApisManager.listNewTickets(port_id, this.latestNumber, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        // startCheckNewTickets(port_id);
                        try {
                            CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                            JSONObject objResult = (JSONObject) object;
                            JSONArray arrayStoreMeals = objResult.getJSONArray("store_meals");
                            CommonUtils.LogWuwei(tag,"listNewTickets result is "+arrayStoreMeals.toJSONString());
                            String result = CommonUtils.converBooleanToInt(arrayStoreMeals.toJSONString());//把true，false替换为1，0
                            JSONArray array = JSONObject.parseArray(result);
                            List<TicketBeanGroup> listTBG = parseStoreMealArray(array);

                            for (TicketBeanGroup tbg : listTBG) {
                                ourInstance.addTicketGroup(tbg);
                            }

                            //更新左侧列表
                            Map map = new HashMap<String, Integer>();
                            map.put("flag", 0);
                            CommonUtils.sendObjMsg(map, PushMealActivity.UPDATE_EXPLV, handler);

                            //更新右侧统计列表的统计数据
                            updateAnalysisNumber();
                        } catch (Exception e) {
                            CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                        } finally {
                            isChecking = false;
                        }

                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        //logerror
                        //startCheckNewTickets(port_id);
                        isChecking = false;
                        CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                    }
                });
            }
        } catch (Exception e) {
            //log or alert;
        } finally {
//            this.startCheckNewTickets();
        }
    }


    public synchronized void addTicketGroup(TicketBeanGroup group) {
        ticketGroups.add(group);
    }


    /**
     * 根据收费项目列表清除小票
     *
     * @param listCIRemove 收费项目列表
     * @param orderId      订单ID
     * @param packaged     是否打包
     */
    public synchronized void removeTicket(List<ChargItem> listCIRemove, String orderId, int packaged) {

        String logName = "";
        for(ChargItem ci:listCIRemove)
        {
            logName+=ci.charge_item_name+"×"+ci.charge_item_amount+"\n";
        }
        CommonUtils.LogWuwei(tag,packaged==0?"堂食订单":"打包订单"+orderId+"准备删除："+logName);

        Map<Long, Integer> ChargeItemIdAmountMap = new HashMap<Long, Integer>();
        for (ChargItem ci : listCIRemove) {
            if (ChargeItemIdAmountMap.containsKey(ci.charge_item_id)) {
                Integer amountSet = ChargeItemIdAmountMap.get(ci.charge_item_id);
                amountSet += ci.charge_item_amount;
                ChargeItemIdAmountMap.put(ci.charge_item_id, amountSet);
            } else {
                ChargeItemIdAmountMap.put(ci.charge_item_id, ci.charge_item_amount);
            }
        }


        Iterator iterTBG = ticketGroups.iterator();//小票组合的列表
        List<TicketBeanGroup> listTBG = new ArrayList<TicketBeanGroup>();
        for (TicketBeanGroup tbg : ticketGroups) {
            TicketBeanGroup tbgTmp = new TicketBeanGroup();
            try {
                tbgTmp = (TicketBeanGroup) tbg.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            if (tbg.order_id.equals(orderId) && tbg.packaged == packaged) {
                for (TicketBean tb : tbg.tickets) {
                    for (ChargItem ci : tb.charge_items) {
                        int amount = 0;
                        if (ChargeItemIdAmountMap.get(ci.charge_item_id) != null) {
                            amount = Integer.valueOf(ChargeItemIdAmountMap.get(ci.charge_item_id));
                        }
                        if (amount > 0) {
                            if (ci.charge_item_amount == amount) {
                                tb.isCheckout = true;
                                ChargeItemIdAmountMap.put(ci.charge_item_id, amount - ci.charge_item_amount);
                            } else if (ci.charge_item_amount < amount) {
                                tb.isCheckout = true;
                                ChargeItemIdAmountMap.put(ci.charge_item_id, amount - ci.charge_item_amount);
                            } else if (ci.charge_item_amount > amount) {
                                ci.charge_item_amount -= amount;
                                ChargeItemIdAmountMap.put(ci.charge_item_id, 0);
                            }
                            break;
                        }
                    }
                }
            }

            //把没出的小票添加到小票组合中
            tbgTmp.tickets.clear();
            for (TicketBean tb : tbg.tickets) {
                if (!tb.isCheckout) {
                    tbgTmp.tickets.add(tb);
                }
            }

            //如果小票的个数大于0，则添加到小票组合中
            if (tbgTmp.tickets.size() > 0) {
                listTBG.add(tbgTmp);
            }


        }

        ticketGroups.clear();
        ticketGroups.addAll(listTBG);
        CommonUtils.LogWuwei(tag, "size is " + ticketGroups.size());
    }

    public synchronized void removeTicket(String orderId)
    {
        CommonUtils.LogWuwei(tag,"准备删除订单id为"+orderId+"的所有订单");

        Iterator iterTBG = ticketGroups.iterator();//小票组合的列表
        List<TicketBeanGroup> listTBG = new ArrayList<TicketBeanGroup>();

        while (iterTBG.hasNext()) {
            //得到小票组合
            TicketBeanGroup tbg = (TicketBeanGroup) (iterTBG.next());
            TicketBeanGroup tbgTmp = new TicketBeanGroup();
            try {
                tbgTmp = (TicketBeanGroup) tbg.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            //得到哪些小票是已经出过的
            Iterator iterTb = tbg.tickets.iterator();//小票的列表
            while (iterTb.hasNext()) {
                TicketBean tb = (TicketBean) iterTb.next();
                if(tb.order_id.equals(orderId))
                {
                        tb.isCheckout = true;
                }
            }

            //把没出的小票添加到小票组合中
            tbgTmp.tickets.clear();
            for (TicketBean tb : tbg.tickets) {
                if (!tb.isCheckout) {
                    tbgTmp.tickets.add(tb);
                }
            }

            //如果小票的个数大于0，则添加到小票组合中
            if (tbgTmp.tickets.size() > 0) {
                listTBG.add(tbgTmp);
            }
        }
        ticketGroups.clear();
        ticketGroups.addAll(listTBG);
        CommonUtils.LogWuwei(tag, "size is " + ticketGroups.size());
    }

    /**
     * 从所有的小票组合中，删除参数中的小票
     *
     * @param tickets
     */
    public synchronized void removeTicket(List<TicketBean> tickets) {

        String logName = "";
        for(TicketBean tb:tickets)
        {
            for(ChargItem ci:tb.charge_items)
            {
                logName+=ci.charge_item_name+"×"+ci.charge_item_amount;
            }
            logName+="\n";

        }
        CommonUtils.LogWuwei(tag,"准备删除小票，小票的内容如下:"+logName);

        Iterator iterTBG = ticketGroups.iterator();//小票组合的列表
        List<TicketBeanGroup> listTBG = new ArrayList<TicketBeanGroup>();

        while (iterTBG.hasNext()) {
            //得到小票组合
            TicketBeanGroup tbg = (TicketBeanGroup) (iterTBG.next());
            TicketBeanGroup tbgTmp = new TicketBeanGroup();
            try {
                tbgTmp = (TicketBeanGroup) tbg.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            //得到哪些小票是已经出过的
            Iterator iterTb = tbg.tickets.iterator();//小票的列表
            while (iterTb.hasNext()) {
                TicketBean tb = (TicketBean) iterTb.next();
                for (TicketBean tbTmp : tickets) {
                    if (tb.equals(tbTmp)) {
                        tb.isCheckout = true;
                    }
                }
            }

            //把没出的小票添加到小票组合中
            tbgTmp.tickets.clear();
            for (TicketBean tb : tbg.tickets) {
                if (!tb.isCheckout) {
                    tbgTmp.tickets.add(tb);
                }
            }

            //如果小票的个数大于0，则添加到小票组合中
            if (tbgTmp.tickets.size() > 0) {
                listTBG.add(tbgTmp);
            }
        }
        ticketGroups.clear();
        ticketGroups.addAll(listTBG);
        CommonUtils.LogWuwei(tag, "size is " + ticketGroups.size());
    }


    /**
     * 将从服务器拿到待出餐数据，进行解析，并根据统计产品列表，进行出餐小票的组包
     *
     * @param storeMealsArray
     * @return
     */
    public List<TicketBeanGroup> parseStoreMealArray(JSONArray storeMealsArray) {

        List<TicketBeanGroup> ticketBeanGroupList = new ArrayList<TicketBeanGroup>();

        //这样解析后的小票,内容包含所有的收费项目，需要根据小票的收费项目类型去构建TicketBeanGroup
        List<TicketBean> listTicketBeanTmp = JSONObject.parseArray
                (storeMealsArray.toJSONString(), TicketBean.class);
        for (TicketBean tb : listTicketBeanTmp) {
            tb.charge_items = (ArrayList<ChargItem>) JSONObject.parseArray(tb.meal_charges.toJSONString(), ChargItem.class);
            for (ChargItem ci : tb.charge_items) {
                ci.listSubChargeItem = (ArrayList<ChargeItemSub>) JSONObject.parseArray(ci.subItems.toJSONString(), ChargeItemSub.class);
                List<String> nameList = new ArrayList<String>();
                for (ChargeItemSub cisub : ci.listSubChargeItem) {
                    cisub.mp = JSONObject.parseObject(cisub.product.toJSONString(), MealProduct.class);
                    nameList.add(cisub.mp.product_name);
                }
                if(ci.show_products == 1)//该收费项目是否需要显示本出餐口下的产品
                {
                    String addName = "";
                    if(nameList.size() == 1)
                    {
                        addName="("+nameList.get(0)+")";
                    }
                    else if(nameList.size() > 1)
                    {
                        addName+="(";
                        for(int k=0;k<nameList.size();k++)
                        {
                            if(k != nameList.size()-1)
                            {
                                addName+=nameList.get(k)+"+";
                            }
                            else
                            {
                                addName+=nameList.get(k);
                            }
                        }
                        addName+=")";
                    }
                    ci.charge_item_name+=addName;
                }

                String remark="";//该收费项目是否需要显示点餐备注信息
                List<String> remarkList = new ArrayList<String>();
                for(ChargeItemSub ciSub:ci.listSubChargeItem)
                {
                    if(!StringUtils.isEmpty(ciSub.remark))
                    {
                        remarkList.add(ciSub.mp.product_name+ciSub.remark);
                    }

                }
                if(remarkList.size() > 0)
                {
                    remark+="(";
                    for(int k=0;k<remarkList.size();k++)
                    {
                        if(k != remarkList.size()-1)
                        {
                            remark+=remarkList.get(k)+"、";
                        }
                        else
                        {
                            remark+=remarkList.get(k)+")";
                        }
                    }
                    ci.charge_item_name+=remark;
                }

            }
        }

        List<TicketBean> listTicketBean = new ArrayList<TicketBean>();

        for (TicketBean tb : listTicketBeanTmp) {
            boolean flagAdd = true;
            for (TicketBeanGroup tbg : ticketGroups) {
                if (tbg.order_id.equals(tb.order_id)) {
                    flagAdd = false;
                }
            }
            if (flagAdd) {
                listTicketBean.add(tb);
            }
        }


        //得到流水号数组
        List<Integer> listTakeSerialNumberToday = new ArrayList<Integer>();
        List<Integer> listTakeSerialNumber = new ArrayList<Integer>();
        for (int k = 0; k < listTicketBean.size(); k++) {
            if (!listTakeSerialNumber.contains(listTicketBean.get(k).take_serial_number))
            {
                listTakeSerialNumber.add(listTicketBean.get(k).take_serial_number);
                if(CommonUtils.whetherTheTimestapToday(listTicketBean.get(k).create_time))
                {
                    listTakeSerialNumberToday.add(listTicketBean.get(k).take_serial_number);
                }

            }
        }

        //找到最大的流水号
        for (Integer num : listTakeSerialNumber) {
            if (this.latestNumber < num && listTakeSerialNumberToday.contains(this.latestNumber)) {
                this.latestNumber = num;
            }
        }
        CommonUtils.LogWuwei(tag,"当前最大流水号为:"+this.latestNumber);

        //根据是否统计、是否打包进行组包
        List<TicketBean> listTickerBeanNew = new ArrayList<TicketBean>();
        for (TicketBean tb : listTicketBean) {
            for (ChargItem ci : tb.charge_items) {
                boolean analysis = false;
                for (ChargeItemSub cisub : ci.listSubChargeItem) {
                    for (StoreProduct sap : listStoreProduct) {
                        if (sap.product_id.equals(cisub.product_id)) {
                            analysis = true;
                            if (ci.packaged == 1) {
                                tb.anaylysisType = 2;
                            } else {
                                tb.anaylysisType = 0;
                            }

                            try {
                                for (int k = 0; k < ci.charge_item_amount; k++) {
                                    ChargItem ciSave = (ChargItem) ci.clone();
                                    ciSave.charge_item_amount = 1;
                                    TicketBean tbNew = (TicketBean) tb.clone();
                                    tbNew.charge_items.clear();
                                    tbNew.charge_items.add(ciSave);
                                    listTickerBeanNew.add(tbNew);
                                }
                            } catch (Exception e) {

                            }
                            break;
                        }
                    }
                    if (analysis) {
                        break;
                    }
                }
                if (analysis) {
                    break;
                } else {
                    if (ci.packaged == 1) {
                        tb.anaylysisType = 3;
                    } else {
                        tb.anaylysisType = 1;
                    }

                    try {
                        ChargItem ciSave = (ChargItem) ci.clone();
                        TicketBean tbNew = (TicketBean) tb.clone();
                        tbNew.charge_items.clear();
                        tbNew.charge_items.add(ciSave);
                        listTickerBeanNew.add(tbNew);
                    } catch (Exception e) {

                    }
                }
            }
        }


        try {
            for (int takeSerialNum : listTakeSerialNumber) {
                List<TicketBeanGroup> listAnalysisEatin = new ArrayList<TicketBeanGroup>();
                List<TicketBean> listNoAnalysisPackaged = new ArrayList<TicketBean>();
                List<TicketBeanGroup> listAnalysisPackaged = new ArrayList<TicketBeanGroup>();
                List<TicketBean> listNoAnalysisEatin = new ArrayList<TicketBean>();
                String order_id = "";
                for (TicketBean tb : listTickerBeanNew) {
                    if (tb.take_serial_number == takeSerialNum) {
                        order_id = tb.order_id;
                        TicketBeanGroup tbg = new TicketBeanGroup();
                        tbg.tickets = new ArrayList<TicketBean>();
                        switch (tb.anaylysisType) {
                            case 0://统计堂食
                                tbg.take_serial_number = takeSerialNum;
                                tbg.order_id = tb.order_id;
                                for (int k = 0; k < tb.charge_items.get(0).charge_item_amount; k++) {
                                    ChargItem ciSave = tb.charge_items.get(0);
                                    TicketBean tbNew = (TicketBean) tb.clone();
                                    tbNew.charge_items.clear();
                                    tbNew.charge_items.add(ciSave);
                                    tbg.tickets.add(tbNew);
                                    tbg.take_mode = tb.take_mode;
                                }
                                tbg.packaged = 0;
                                listAnalysisEatin.add(tbg);
                                break;
                            case 1://不统计堂食
                                listNoAnalysisEatin.add(tb);
                                break;
                            case 2://统计打包
                                tbg.take_serial_number = takeSerialNum;
                                tbg.order_id = tb.order_id;
                                for (int k = 0; k < tb.charge_items.get(0).charge_item_amount; k++) {
                                    ChargItem ciSave = tb.charge_items.get(0);
                                    TicketBean tbNew = (TicketBean) tb.clone();
                                    tbNew.charge_items.clear();
                                    tbNew.charge_items.add(ciSave);
                                    tbg.tickets.add(tbNew);
                                    tbg.take_mode = tb.take_mode;
                                }
                                tbg.packaged = 1;
                                listAnalysisPackaged.add(tbg);
                                break;
                            case 3://不统计打包
                                listNoAnalysisPackaged.add(tb);
                                break;
                        }
                    }
                }
                if (listAnalysisEatin.size() > 0)//统计堂食
                {
                    ticketBeanGroupList.addAll(listAnalysisEatin);
                }

                if (listNoAnalysisEatin.size() > 0)//不统计堂食
                {
                    TicketBeanGroup tbgNew = new TicketBeanGroup();
                    tbgNew.order_id = order_id;
                    tbgNew.take_serial_number = takeSerialNum;
                    tbgNew.tickets = new ArrayList<TicketBean>();
                    int todaySize = 0;
                    for (TicketBean tb : listNoAnalysisEatin) {
                        if (!CommonUtils.whetherTheTimestapToday(tb.create_time)) {
                            TicketBeanGroup tbgTmp = new TicketBeanGroup();
                            tbgTmp.order_id = order_id;
                            tbgTmp.take_serial_number = takeSerialNum;
                            tbgTmp.tickets = new ArrayList<TicketBean>();
                            tbgTmp.tickets.add(tb);
                            tbgTmp.packaged = 0;
                            tbgTmp.take_mode = tb.take_mode;
                            ticketBeanGroupList.add(tbgTmp);
                        } else {
                            todaySize++;
                            tbgNew.tickets.add(tb);
                        }
                    }
                    if (todaySize >= 1) {
                        tbgNew.packaged = 0;
                        ticketBeanGroupList.add(tbgNew);
                    }
                }


                if (listAnalysisPackaged.size() > 0)//统计打包
                {
                    ticketBeanGroupList.addAll(listAnalysisPackaged);
                }

                if (listNoAnalysisPackaged.size() > 0)//不统计打包
                {
                    TicketBeanGroup tbgNew = new TicketBeanGroup();
                    tbgNew.order_id = order_id;
                    tbgNew.take_serial_number = takeSerialNum;
                    tbgNew.tickets = new ArrayList<TicketBean>();
                    int todaySize = 0;
                    for (TicketBean tb : listNoAnalysisPackaged) {
                        if (!CommonUtils.whetherTheTimestapToday(tb.create_time)) {
                            TicketBeanGroup tbgTmp = new TicketBeanGroup();
                            tbgTmp.order_id = order_id;
                            tbgTmp.take_serial_number = takeSerialNum;
                            tbgTmp.tickets = new ArrayList<TicketBean>();
                            tbgTmp.tickets.add(tb);
                            tbgTmp.packaged = 1;
                            tbgTmp.take_mode = tb.take_mode;
                            ticketBeanGroupList.add(tbgTmp);
                        } else {
                            todaySize++;
                            tbgNew.take_mode = tb.take_mode;
                            tbgNew.tickets.add(tb);
                        }
                    }
                    if (todaySize >= 1) {
                        tbgNew.packaged = 1;
                        ticketBeanGroupList.add(tbgNew);
                    }
                }


            }

        } catch (Exception e) {
            Log.e("", "错误信息:" + e.getMessage());
        }

        CommonUtils.LogWuwei("", "ticketBeanGroupList.size() is" + ticketBeanGroupList.size());
        return ticketBeanGroupList;
    }

    /**
     * 更新右侧统计列表
     *
     * @param listStoreProduct
     */
    public void updateAnalysisList(List<StoreProduct> listStoreProduct) {
        this.listStoreProduct = listStoreProduct;
    }

    /**
     * 更新统计数据
     */
    public void updateAnalysisNumber() {
        for (StoreProduct sp : listStoreProduct) {
            sp.analyticNumber = KitchenManager.getInstance().calcAnalyticNumberWithProductId(sp.product_id);
        }
        CommonUtils.sendMsg("", PushMealActivity.UPDATE_ANALYSIS_GRIDVIEW, handler);
    }

    /**
     * 获取所有的小票组合
     *
     * @return
     */
    public List<TicketBeanGroup> getTicketBeanGroups() {
        return ticketGroups;
    }

    public void cleanTicketBeanGroups() {
        ticketGroups.clear();
    }

    public void cleanLatestNumber() {
        this.latestNumber = 0;
    }

    /**
     * 获得包含某个产品所有小票组合
     *
     * @return
     */
    public List<TicketBeanGroup> getAnalysisTicketBeanGroup(Long product_id) {
        List<TicketBeanGroup> listTBG = new ArrayList<TicketBeanGroup>();
        for (TicketBeanGroup tbg : ticketGroups) {
            boolean flagAdd = false;
            for (TicketBean tb : tbg.tickets) {
                for (ChargItem ci : tb.charge_items) {
                    for (ChargeItemSub cisub : ci.listSubChargeItem) {
                        if (cisub.mp.product_id.equals(product_id)) {
                            flagAdd = true;
                            break;
                        }
                    }
                    if (flagAdd) {
                        break;
                    }
                }
                if (flagAdd) {
                    listTBG.add(tbg);
                }
            }
        }
        return listTBG;
    }
}
