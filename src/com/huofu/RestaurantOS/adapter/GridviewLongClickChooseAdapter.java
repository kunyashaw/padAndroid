package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Created by zzl on 15/12/4.
 */
public class GridviewLongClickChooseAdapter extends BaseAdapter{

    String tag = "GridviewLongClickChooseAdapter";
    List<ChargItem> ls = null;//这里存储的是订单每个收费项目
    Context ctxt = null;
    float scale ;

    public GridviewLongClickChooseAdapter(Context ctxt, List<ChargItem> ls,float scale)
    {
        this.ls  = ls;
        this.ctxt = ctxt;
        this.scale = scale;
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

        final ChargItem ci = ls.get(position);

        CheckBox cb = new CheckBox(ctxt);

        cb.setText(ls.get(position).charge_item_name+"×"+CommonUtils.DoubleDeal(ls.get(position).charge_item_amount));

        cb.setTextColor(Color.parseColor("#363636"));

        cb.setTextSize(18);

        cb.setButtonDrawable(R.drawable.checkbox_selector);

        cb.setPadding(cb.getPaddingLeft() + (int) (40.0f * scale + 0.5f), cb.getPaddingTop(),
                cb.getPaddingRight(), cb.getPaddingBottom());

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                ci.ischecked = isChecked;
            }
        });

        return cb;
    }

    public List<ChargItem> getChargeItemList()
    {
        Map<Long, Integer> ChargeItemIdAmountMap = new HashMap<Long, Integer>();
        for(ChargItem ci:ls)
        {
            if(ci.ischecked)
            {
                if(ChargeItemIdAmountMap.containsKey(ci.charge_item_id))
                {
                    Integer amountSet = ChargeItemIdAmountMap.get(ci.charge_item_id);
                    amountSet+=ci.charge_item_amount;
                    ChargeItemIdAmountMap.put(ci.charge_item_id,amountSet);
                }
                else
                {
                    ChargeItemIdAmountMap.put(ci.charge_item_id,ci.charge_item_amount);
                }
            }
        }

        List<ChargItem> listCi = new ArrayList<ChargItem>();
        for(Long id:ChargeItemIdAmountMap.keySet())
        {
            ChargItem ci = new ChargItem();
            for(ChargItem ciTmp:ls)
            {
                if(ciTmp.charge_item_id == id)
                {
                    try {
                        ci = (ChargItem)ciTmp.clone();
                        ci.charge_item_amount = ChargeItemIdAmountMap.get(id);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(ci.charge_item_amount > 0)
            {
                listCi.add(ci);
            }
        }

        return listCi;
    }



}
