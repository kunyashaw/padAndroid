package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.storeOrder.StoreProduct;
import com.huofu.RestaurantOS.manager.KitchenManager;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Created by zzl on 15/11/25.
 */
public class GridviewAnalysisFoodAdapter extends BaseAdapter{

    Context ctxt;
    Handler handler;
    List<StoreProduct> list = new ArrayList<StoreProduct>();
    ExpandableListView explv;
    ExpandableListviewWaitingPushAdapter explvAdapter ;
    public static Long lastChooseProductId = 0L;
    public static String lastName = "";


    public GridviewAnalysisFoodAdapter(ExpandableListView explv,ExpandableListviewWaitingPushAdapter explvAdater,
                                       Context context, Handler handler,List<StoreProduct> ls)
    {
        ctxt = context;
        list = ls;
        this.explv = explv;
        this.explvAdapter = explvAdater;
        this.handler = handler;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View grid = inflater.inflate(R.layout.grid_waiting_push_item, null);
        if(position>=list.size())
        {
            return convertView;
        }
        final StoreProduct sap = list.get(position);

        Button buttonName = (Button)grid.findViewById(R.id.button_gridview_speacial_name);
        buttonName.setText(sap.name);

        final Button buttonNewInfo = (Button)grid.findViewById(R.id.button_gridview_item_new_info);
        sap.analyticNumber.notCheck = sap.analyticNumber.notCheckEatIn+sap.analyticNumber.notCheckEatOut;
        if(sap.analyticNumber.notCheck != 0.0)
        {
            buttonNewInfo.setText("+"+CommonUtils.DoubleDeal(sap.analyticNumber.notCheck));
            buttonNewInfo.setTextColor(Color.parseColor("#ffffff"));
            buttonNewInfo.setTextSize(18);
            buttonNewInfo.setBackgroundResource(R.drawable.gridview_waiting_push_item_bottom_corner_red);

        }
        else
        {
            buttonNewInfo.setText("无新增");
            buttonNewInfo.setTextColor(Color.GRAY);
        }


        final TextView tv_package = (TextView)grid.findViewById(R.id.tv_gridview_item_package_num);
        tv_package.setText(CommonUtils.DoubleDeal(sap.analyticNumber.checkEatOut));

        final TextView tv_eaten = (TextView)grid.findViewById(R.id.tv_gridview_item_eaten_num);
        tv_eaten.setText(CommonUtils.DoubleDeal(sap.analyticNumber.checkEatIn));

        final RelativeLayout rl = (RelativeLayout)grid.findViewById(R.id.rl_grid_waiting_push_item);
        if(list != null && list.size() > 0)
        {
            buttonNewInfo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    sap.analyticNumber.checkEatIn += sap.analyticNumber.notCheckEatIn;
                    sap.analyticNumber.checkEatOut += sap.analyticNumber.notCheckEatOut;
                    sap.analyticNumber.notCheckEatOut = 0.0;
                    sap.analyticNumber.notCheckEatIn = 0.0;

                    tv_package.setText(CommonUtils.DoubleDeal(sap.analyticNumber.checkEatOut));
                    tv_eaten.setText(CommonUtils.DoubleDeal(sap.analyticNumber.checkEatIn));
                    buttonNewInfo.setText("无新增");
                    buttonNewInfo.setTextColor(Color.GRAY);
                    buttonNewInfo.setEnabled(false);
                    buttonNewInfo.setBackgroundResource(R.drawable.gird_waiting_push_item_bottom_corner);

                    KitchenManager.getInstance().checkProduct(sap.product_id);

                    buttonNewInfo.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.big_2_small));
                    tv_eaten.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));
                    tv_package.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));


                }
            });


            View.OnClickListener ocl  = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    grid.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));

                    lastChooseProductId = list.get(position).product_id;
                    lastName = list.get(position).name;
                    Map map = new HashMap<String,Integer>();
                    map.put("flag",1);
                    map.put("productId",lastChooseProductId);
                    map.put("name",lastName);
                    map.put("scrollToTop",true);
                    CommonUtils.sendObjMsg(map, PushMealActivity.UPDATE_EXPLV, handler);

                }
            };

            tv_package.setOnClickListener(ocl);
            tv_eaten.setOnClickListener(ocl);
            buttonName.setOnClickListener(ocl);
            rl.setOnClickListener(ocl);
        }
        return grid;
    }


    /****
     * 点击效果
     * @param view
     */
    public void clickEffect(View view)
    {
        view.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.small_2_big));
    }
}
