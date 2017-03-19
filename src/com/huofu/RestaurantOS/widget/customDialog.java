package com.huofu.RestaurantOS.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;

/**
 * author: Created by zzl on 15/8/14.
 */
public class customDialog extends Dialog{


    public customDialog(Context context) {
        super(context);
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    @Override
    public void setCancelMessage(Message msg) {
        super.setCancelMessage(msg);
    }
}

