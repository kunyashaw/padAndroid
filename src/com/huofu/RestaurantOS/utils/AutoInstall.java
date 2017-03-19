package com.huofu.RestaurantOS.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * author: Created by zzl on 15/9/22.
 */
public class AutoInstall {

    private static String mUrl;
    private static Context mContext;

    public static void setUrl(String url)
    {
        mUrl = url;
    }

    public static void install(Context ctxt)
    {
        mContext = ctxt;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(mUrl)),"application/vnd.android.package-archive");
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

