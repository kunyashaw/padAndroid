package com.huofu.RestaurantOS.bean;

import android.view.View;

/**
 * author: Created by zzl on 15/9/23.
 */
public class Notification {
   public String tips;
   public int type;
   public View.OnClickListener ocl;
   public boolean autoHide=false;//是否自动隐藏
   public long lastClickTime = 0;
   public String number="0";//外从保存个数，打印机报警保存ip地址或者外设id
}
