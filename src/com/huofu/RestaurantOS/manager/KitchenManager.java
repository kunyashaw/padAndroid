package com.huofu.RestaurantOS.manager;

import com.huofu.RestaurantOS.bean.AnalyticNumber;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBean;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBeanGroup;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.ChargeItemSub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tim on 11/11/15.
 */
public class KitchenManager {

    private static KitchenManager ourInstance = new KitchenManager();

    public static KitchenManager getInstance() {
        return ourInstance;
    }

    private KitchenManager() {

    }

    private Map<Long, Set<String>> productCheckMap = new HashMap<Long, Set<String>>();


    /**
     * 实时计算统计数字
     *
     * @param productId
     * @return
     */
    public AnalyticNumber calcAnalyticNumberWithProductId(Long productId) {

        AnalyticNumber an = new AnalyticNumber();

        List<TicketBeanGroup> listTBG = TicketsManager.getInstance().getTicketBeanGroups();
        Set<String> orderIdSet = new HashSet<String>();
        orderIdSet = productCheckMap.get(productId);
        if(orderIdSet == null)
        {
            orderIdSet = new HashSet<String>();
        }

        for (TicketBeanGroup tbg : listTBG) {
            for (TicketBean tb : tbg.tickets) {
                for (ChargItem ci : tb.charge_items) {
                    for (ChargeItemSub ciSub : ci.listSubChargeItem) {
                        Long id = ciSub.mp.product_id;
                        if (id.equals(productId)) {
                            if (orderIdSet.contains(tbg.order_id))//已经通知过
                            {
                                if (ci.packaged == 0) {
                                    an.checkEatIn += ci.charge_item_amount * ciSub.amount;
                                } else if (ci.packaged == 1) {
                                    an.checkEatOut += ci.charge_item_amount * ciSub.amount;
                                }
                            } else {
                                if (ci.packaged == 0) {
                                    an.notCheckEatIn += ci.charge_item_amount * ciSub.amount;
                                } else if (ci.packaged == 1) {
                                    an.notCheckEatOut += ci.charge_item_amount * ciSub.amount;
                                }
                            }
                        }
                    }
                }
            }
        }

        return an;
    }


    /**
     * 点击已通知的数字按钮,把这个productId 相关的 orderid 都保存被通知过
     * @param productId
     */
    public void checkProduct(Long productId) {

        Set<String> set = new HashSet<String>();
        List<TicketBeanGroup> ticketBeanGroupList = TicketsManager.getInstance().getTicketBeanGroups();
        for (TicketBeanGroup tbg : ticketBeanGroupList) {
            boolean flagAdd = false;
            for (TicketBean tb : tbg.tickets) {
                for (ChargItem ci : tb.charge_items) {
                    for (ChargeItemSub chargeItemSub : ci.listSubChargeItem) {
                        if (chargeItemSub.mp.product_id.equals(productId)) {
                            if (!set.contains(tbg.order_id)) {
                                flagAdd = true;
                                set.add(tbg.order_id);
                                break;
                            }
                        }
                    }
                    if (flagAdd) {
                        break;
                    }
                }
                if (flagAdd) {
                    break;
                }
            }
        }
        productCheckMap.put(productId, set);
    }


    /**
     * 保存已叫过号的order
     * 需要持续保存
     * 1. modify mem object
     * 2. save to file
     *
     * @param productId
     * @param orderId
     */
    private void saveCheckOrder(Long productId, String orderId) {
        Set<String> orderIdSet = productCheckMap.get(productId);
        if (!orderIdSet.contains(orderId)) {
            orderIdSet.add(orderId);
        }
    }

    private void saveMapToFile() {

    }

    private void loadMapFromFile() {

    }
}
