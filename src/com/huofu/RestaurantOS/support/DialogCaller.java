package com.huofu.RestaurantOS.support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.utils.StringUtils;

/**
 * author: Created by zzl on 15/12/16.
 */
public class DialogCaller {

    public static void showDialog(String title,String message,
                                  String postiveTips,DialogInterface.OnClickListener onPosClickListener,
                                  String negativeTips,DialogInterface.OnClickListener onNegClickListener
                                  ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainApplication.getmActivity())
                .setTitle(title)
                .setMessage(message);
        if(!StringUtils.isEmpty(postiveTips))
        {
            builder.setPositiveButton(postiveTips, onPosClickListener);
        }

        if(!StringUtils.isEmpty(negativeTips))
        {
            builder.setNegativeButton(negativeTips,onNegClickListener);
        }

        Dialog dialog = builder.create();
        dialog.show();
    }
}
