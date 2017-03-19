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
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.StoreMealPort;
import com.huofu.RestaurantOS.bean.peripheral.peripheral;
import com.huofu.RestaurantOS.bean.storeOrder.MealPortRelation;
import com.huofu.RestaurantOS.fragment.settings.MealPort.FragmentMealPortSettingManager;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/11/27.
 */
public class ListviewPushMealPortListAdapter extends BaseAdapter{

    public List<StoreMealPort> list;
    public Context ctxt;
    public Handler handler;
    public ListviewPushMealPortListAdapter adapter;
    public Long appcopyId;


    public ListviewPushMealPortListAdapter(Context ctxt, Handler handler, List<StoreMealPort> list)
    {
        this.ctxt = ctxt;
        this.handler = handler;
        this.list = list;
        appcopyId = LocalDataDeal.readFromLocalAppCopyId(ctxt);
    }

    public void updateAdapter(ListviewPushMealPortListAdapter adapter)
    {
        this.adapter = adapter;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.setting_item_checkbox_standard, null);
        long id = LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt);
        RelativeLayout rl = (RelativeLayout)grid.findViewById(R.id.rl_setting_layout_checkbox_standard_item);
        TextView tvName = (TextView)grid.findViewById(R.id.tv_setting_layout_checkbox_standard_item_name);
        final ImageView iv = (ImageView)grid.findViewById(R.id.imageview_setting_layout_checkbox_choose);
        View viewEmpty = grid.findViewById(R.id.view_setting_meal_port);
        ImageView ivArrow = (ImageView)grid.findViewById(R.id.iv_setting_meal_port);

        if(id == list.get(position).port_id)
        {
            iv.setImageResource(R.drawable.checkbox2);
        }
        else
        {
            iv.setImageResource(android.R.color.transparent);
        }

        tvName.setText(list.get(position).name);
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.sendMsg("正在切换出餐口", PushMealActivity.SHOW_LOADING_TEXT, handler);
                iv.setImageResource(R.drawable.checkbox2);
                final StoreMealPort smp = list.get(position);
                int port_id = smp.port_id;
                final long call_peripheral_id = smp.call_peripheral_id;
                final long printer_peripheral_id = smp.printer_peripheral_id;
                LocalDataDeal.writeToLocalMealDoneChooseMealPortInfo(port_id, smp.name, ctxt);

                ApisManager.GetPeripheralList(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        List<peripheral> listP = (List<peripheral>) object;
                        for (int k = 0; k < listP.size(); k++) {
                            if (call_peripheral_id == listP.get(k).peripheral_id) {
                                LocalDataDeal.writeToLocalCallTvIp(listP.get(k).con_id, ctxt);
                                int rule = -1;
                                switch (smp.call_type)//叫号规则 1自动叫号 2手动叫号 3尾单叫号
                                {
                                    case 1:
                                        rule = 1;
                                        break;
                                    case 2:
                                        rule = 0;
                                        break;
                                    case 3:
                                        rule = 2;
                                        break;
                                    default:
                                        rule = 2;
                                        break;
                                }
                                LocalDataDeal.writeToAutoCall(rule, ctxt);
                            }
                            if (printer_peripheral_id == listP.get(k).peripheral_id) {
                                LocalDataDeal.writeToLocalIpKitchPrinter(listP.get(k).name, listP.get(k).con_id, ctxt);
                            }
                            else if(printer_peripheral_id == 0)
                            {
                                LocalDataDeal.writeToLocalIpKitchPrinter("不打印", "", ctxt);
                            }
                        }
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {

                    }
                });

                List<MealPortRelation> listMpr = new ArrayList<MealPortRelation>();
                MealPortRelation mpr = new MealPortRelation();
                mpr.port_id = port_id;
                mpr.task_status = 1;//任务关系状态：0=解除任务关系，1=建立任务关系
                mpr.checkout_type = 0;//出餐方式：0=手动，1=自动
                mpr.printer_status = 1;//打印机连接状态：0=未连接，1=正常连接，2=无法打
                listMpr.add(mpr);
                adapter.notifyDataSetChanged();
                ApisManager.registTaskRelation(appcopyId, listMpr, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", PushMealActivity.HIDE_MEAL_PORT_LIST, handler);
                        CommonUtils.sendMsg(list.get(position).name, PushMealActivity.UPDATE_NOW_PUSH_WAY, handler);
                        CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);

                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });
            }
        };
        tvName.setOnClickListener(ocl);
        iv.setOnClickListener(ocl);


        if(!PushMealActivity.isActive)
        {
            ivArrow.setVisibility(View.VISIBLE);
            View.OnClickListener oclEdit = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentMealPortSettingManager.showFragmentMpMealPortCheck(position,true);
                }
            };
            ivArrow.setOnClickListener(oclEdit);
            viewEmpty.setOnClickListener(oclEdit);
        }

        return grid;
    }

}
