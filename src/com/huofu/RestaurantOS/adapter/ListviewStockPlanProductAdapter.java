package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.StoreProduct;
import com.huofu.RestaurantOS.ui.pannel.stockPlan.StockPlanActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wow on 15/8/8.
 */
public class ListviewStockPlanProductAdapter extends BaseAdapter{


    List<StoreProduct> ls = new ArrayList<StoreProduct>();
    Context ctxt;
    Handler handler;
    public ListviewStockPlanProductAdapter(List<StoreProduct> ls,Context ctxt,Handler handler)
    {
        this.ls = ls;
        this.ctxt = ctxt;
        this.handler = handler;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ls.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return ls.get(position);
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
        View grid = inflater.inflate(R.layout.listview_stock_plan_product,null);

        TextView tvName = (TextView)grid.findViewById(R.id.tv_stock_plan_product_name);
        TextView tvWhetherOpen = (TextView)grid.findViewById(R.id.textview_stock_plan_whether_open);
        ImageView ivWhetherShow = (ImageView)grid.findViewById(R.id.imageview_stock_plan_whether_show);
        final RelativeLayout rl = (RelativeLayout)grid.findViewById(R.id.rl_stock_plan_product);
        int lastChoosePosition = StockPlanActivity.nowChooseProductListPositon;
        final StoreProduct sp = ls.get(position);

        if(position == StockPlanActivity.nowChooseProductListPositon)
        {
            rl.setBackgroundColor(ctxt.getResources().getColor(R.color.Blue));
        }
        else
        {
            rl.setBackgroundColor(ctxt.getResources().getColor(android.R.color.transparent));
        }

        tvName.setText("" + sp.name + "(" + sp.unit + ")");
        if (sp.inv_enabled == 1)
        {
            tvWhetherOpen.setVisibility(View.VISIBLE);
        }
        else
        {
            tvWhetherOpen.setVisibility(View.INVISIBLE);
        }

        return grid;
    }

}
