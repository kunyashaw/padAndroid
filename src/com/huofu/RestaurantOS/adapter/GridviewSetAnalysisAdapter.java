package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.storeOrder.StoreProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/12/3.
 */
public class GridviewSetAnalysisAdapter extends BaseAdapter{

    List<StoreProduct> list_analysis = new ArrayList<StoreProduct>();
    final float scale;
    Context ctxt = null;

    public GridviewSetAnalysisAdapter(Context ctxt,List<StoreProduct> list, float scale)
    {
        this.ctxt = ctxt;
        list_analysis = list;
        this.scale = scale;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list_analysis.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list_analysis.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        CheckBox cb = new CheckBox(ctxt);
        final StoreProduct sp = list_analysis.get(position);
        if(sp.meal_stat == 0)//不需要统计
        {
            cb.setChecked(false);
        }
        else
        {
            cb.setChecked(true);
        }

        cb.setText(sp.name);
        cb.setTextColor(Color.parseColor("#363636"));
        cb.setTextSize(18);
        //cb.setBackgroundResource(R.drawable.checkbox_selector);
        cb.setButtonDrawable(R.drawable.checkbox_selector);

        cb.setPadding(cb.getPaddingLeft() + (int) (40.0f * scale + 0.5f),
                cb.getPaddingTop(),
                cb.getPaddingRight(),
                cb.getPaddingBottom());

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked)//选中
                {
                    sp.meal_stat = 1;
                } else//取消选中
                {
                    sp.meal_stat = 0;
                }
            }
        });
        return cb;
    }

    public List<StoreProduct> getAnalysisListAfterSet()
    {
        return  list_analysis;
    }

}
