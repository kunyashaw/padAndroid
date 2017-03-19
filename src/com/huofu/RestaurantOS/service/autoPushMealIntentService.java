package com.huofu.RestaurantOS.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.huofu.RestaurantOS.bean.storeOrder.StoreMealAutoPrintParams;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.AutoPushMealUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/10/27.
 */
public class autoPushMealIntentService extends IntentService{

    public static List<StoreMealAutoPrintParams> listStoreMealPrintParams = new ArrayList<StoreMealAutoPrintParams>();
    public static boolean flagPrintOver = true;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public autoPushMealIntentService() {
        super(autoPushMealIntentService.class.getName());
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        AutoPushMealUtils.autoCheckout(AutoPushMealUtils.appcopy_id, 1, listStoreMealPrintParams);


    }
}
