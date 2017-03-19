package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBean;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBeanGroup;
import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.manager.TicketsManager;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*****
 * 出餐中全部待出餐列表对应的适配器
 * 这里处理了单击、长按、滑动出餐
 */
public class ExpandableListviewWaitingPushAdapter extends BaseExpandableListAdapter {

    Context ctxt;
    String tag = PushMealActivity.tag;

    //groupList显示标题,平时只显示“全部待出餐"
    //当选中要统计的菜品时，举个例子，则添加一个“待出餐-鸡汤馄饨”,当点击“全部待出餐”，动态删除“待出餐-鸡汤馄饨”
    public List<String> groupList = new ArrayList<String>();

    public List<List<TicketBeanGroup>> childList = new ArrayList<List<TicketBeanGroup>>();
    public List<MealBucket> mealBucketList = new ArrayList<MealBucket>();
    public Handler handler;
    public ExpandableListView explv;
    public View.OnTouchListener otl = null;

    public ExpandableListviewWaitingPushAdapter(Context context, Handler handler,
                                                ExpandableListView explv,
                                                List<MealBucket> mealBucketList) {
        this.handler = handler;
        this.ctxt = context;
        this.explv = explv;
        this.mealBucketList = mealBucketList;

        childList.clear();
        childList.add(0,new ArrayList<TicketBeanGroup>());
        childList.add(1,new ArrayList<TicketBeanGroup>());
    }


    /**
     * 把所有的view全部屏蔽
     */
    public void ChangeTouchAll(boolean flag) {
        if (childList!= null) {
            if(childList.size() > 0)
            {
                if(childList.get(0) != null)
                {
                    if(childList.get(0).size() > 0)
                    {
                        for (int k = 0; k < childList.get(0).size(); k++) {
                            childList.get(0).get(k).flagEnabled = flag;
                        }
                    }
                }
            }

        }
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        if(groupPosition < groupList.size() && groupPosition < childList.size())
        {
            List<TicketBeanGroup> listTBG = childList.get(groupPosition);
            if(listTBG.size() == 0)
            {
                return 0;
            }
            else
            {
                return listTBG.size();
            }
        }
        else
        {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View grid = inflater.inflate(R.layout.expandable_listview_group_item, null);

        TextView tv = (TextView) grid.findViewById(R.id.tv_expandable_listview_group_item);
        tv.setText(groupList.get(groupPosition));

        //显示未出餐的订单个数（不是小票个数）
        TextView bv = (TextView) grid.findViewById(R.id.tv_expandable_listview_group_item_badget);


        grid.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });

        if (groupPosition == 1) {
            Animation anim = new AnimationUtils().loadAnimation(ctxt, R.anim.fade_in);
            grid.startAnimation(anim);
            bv.setVisibility(View.INVISIBLE);
        } else if (groupPosition == 0) {
            if(childList.size() > 0)
            {
                List<TicketBeanGroup> list = childList.get(0);
                List<String> orderIdList = new ArrayList<String>();
                for (TicketBeanGroup tbg : list) {
                    if (!orderIdList.contains(tbg.order_id)) {
                        orderIdList.add(tbg.order_id);
                    }
                }
                if(orderIdList.size() > 0)
                {
                    bv.setText("" + orderIdList.size());
                    bv.setVisibility(View.VISIBLE);
                }
                else
                {
                    bv.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                bv.setVisibility(View.INVISIBLE);
            }

        }


        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == 0)//如果点击的是全部待出餐数据
                {
                    if (!isExpanded)//如果没有展开
                    {
                        explv.expandGroup(0);
                        if (groupList.size() == 2) {
                            explv.collapseGroup(1);
                            groupList.remove(1);
                            Map map = new HashMap<String, Integer>();
                            map.put("flag", 0);
                            CommonUtils.sendObjMsg(map, PushMealActivity.UPDATE_EXPLV, handler);
                        }
                    }
                }
            }
        });
        return grid;
    }


    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ChangeTouchAll(true);
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View grid = inflater.inflate(R.layout.listview_all_push_rl, null);

        final SwipeLayout swipeLayout = (SwipeLayout) grid;
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        List<SwipeLayout.DragEdge> dragEdges = new ArrayList<SwipeLayout.DragEdge>();
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);

        Button btn_serinal_num = (Button) grid.findViewById(R.id.btn_listview_all_serinal_num);
        final int take_serial_num = -1;

        final Button btn_whether_packaged = (Button) grid.findViewById(R.id.btn_listview_all_whether_packaged);
        final Button btn_chargeItem_name = (Button) grid.findViewById(R.id.btn_listview_all_chargeItem_name);
        View viewNotSame = (View) grid.findViewById(R.id.view_listview_all_push_rl_not_same);
        View viewSame = (View) grid.findViewById(R.id.view_listview_all_push_rl_same);


        //不同的订单之间用一个黑线隔开
        if (childPosition == 0) {
            if (childList.get(groupPosition).size() > 1) {
                TicketBeanGroup tbg = childList.get(groupPosition).get(childPosition);
                TicketBeanGroup tbgNext = childList.get(groupPosition).get(childPosition + 1);
                if (tbg.take_serial_number == tbgNext.take_serial_number) {
                    viewSame.setVisibility(View.VISIBLE);
                    viewNotSame.setVisibility(View.INVISIBLE);
                } else {
                    viewSame.setVisibility(View.INVISIBLE);
                    viewNotSame.setVisibility(View.VISIBLE);
                }
            } else {
                viewNotSame.setVisibility(View.VISIBLE);
            }
        } else {
            if (childPosition < childList.get(groupPosition).size() - 1) {
                TicketBeanGroup tbg = childList.get(groupPosition).get(childPosition);
                TicketBeanGroup tbgNext = childList.get(groupPosition).get(childPosition + 1);
                if (tbg.take_serial_number == tbgNext.take_serial_number) {
                    viewSame.setVisibility(View.VISIBLE);
                    viewNotSame.setVisibility(View.INVISIBLE);
                } else {
                    viewSame.setVisibility(View.INVISIBLE);
                    viewNotSame.setVisibility(View.VISIBLE);
                }
            } else if (childPosition == childList.get(groupPosition).size() - 1) {
                TicketBeanGroup tbg = childList.get(groupPosition).get(childPosition);
                TicketBeanGroup tbgFront = childList.get(groupPosition).get(childPosition - 1);
                if (tbg.take_serial_number == tbgFront.take_serial_number) {
                    viewSame.setVisibility(View.VISIBLE);
                    viewNotSame.setVisibility(View.INVISIBLE);
                } else {
                    viewSame.setVisibility(View.INVISIBLE);
                    viewNotSame.setVisibility(View.VISIBLE);
                }
            }
        }

        //获取要展示的名字
        TicketBeanGroup tbg = childList.get(groupPosition).get(childPosition);
        String name = "";
        for (TicketBean tb : tbg.tickets) {
            String time = "";
            if (!CommonUtils.whetherTheTimestapToday(tb.create_time)) {
                time = "(" + CommonUtils.timeConvert(tb.create_time / 1000) + ")";
            }
            for (ChargItem ci : tb.charge_items) {
                String tmpName = ci.charge_item_name + "×" + ci.charge_item_amount;
                SpannableString s = new SpannableString(tmpName + "\n" + time);
                s.setSpan(new ForegroundColorSpan(Color.RED), tmpName.length(), time.length() + tmpName.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new AbsoluteSizeSpan(35), tmpName.length(), time.length() + tmpName.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                name += s.toString();
            }
        }
        btn_serinal_num.setText(childList.get(groupPosition).get(childPosition).take_serial_number + "");
        btn_chargeItem_name.setText(name);

        //设置打包或者外送图标
        if (tbg.packaged == 1) {
            if (tbg.take_mode == 4) {
                btn_whether_packaged.setBackgroundResource(R.drawable.icon_bicycle);
            } else {
                btn_whether_packaged.setBackgroundResource(R.drawable.icon_packaged);
            }

            if (viewSame.getVisibility() == View.VISIBLE) {
                viewSame.setBackgroundColor(Color.parseColor("#48000000"));
            }
            grid.setBackgroundColor(Color.parseColor("#ccdbf5"));
        }

        View.OnLongClickListener olcl = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //判断类型（是堂食还是打包）
                final TicketBeanGroup tbg = childList.get(groupPosition).get(childPosition);
                int packaged = tbg.packaged;
                String orderId = tbg.order_id;
                Map map = new HashMap();
                map.put("packaged",packaged);
                map.put("orderId",orderId);
                map.put("takeSerialNum",tbg.take_serial_number);
                map.put("groupPosition",groupPosition);
                CommonUtils.sendObjMsg(map,PushMealActivity.SHOW_CHOOSE_MP_DIALOG,handler);
                return false;
            }
        };

        //设置点击事件
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TicketBeanGroup tbg = childList.get(groupPosition).get(childPosition);
                if(!tbg.flagEnabled)
                {
                    return;
                }
                ChangeTouchAll(false);
                JSONArray array = new JSONArray();
                int takeSerialNumber = 0;
                for (TicketBean tb : tbg.tickets) {
                    for (ChargItem ci : tb.charge_items) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("charge_item_id", ci.charge_item_id);
                            obj.put("amount", ci.charge_item_amount);
                            obj.put("orderId", ci.orderId);
                            obj.put("packaged", ci.packaged);
                            array.put(obj);
                            takeSerialNumber = ci.takeSerinalNum;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                clickEffectAndCall(grid, takeSerialNumber, tbg.packaged);
                CommonUtils.sendMsg("正在出餐", PushMealActivity.SHOW_LOADING_TEXT, handler);
                ApisManager.mealCheckout(btn_chargeItem_name.getText().toString(),handler,
                        tbg.order_id, CommonUtils.getFormatDate(0), CommonUtils.getTimeBuckerId(mealBucketList), tbg.packaged,
                        array, 0, LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt), new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                                TicketsManager.getInstance().removeTicket(tbg.tickets);

                                Map map = new HashMap<String, Integer>();
                                if(groupPosition == 0)
                                {
                                    map.put("flag", 0);
                                }
                                else if(groupPosition == 1)
                                {
                                    map.put("flag", 1);
                                    map.put("productId",GridviewAnalysisFoodAdapter.lastChooseProductId);
                                    map.put("name",GridviewAnalysisFoodAdapter.lastName);
                                }

                                CommonUtils.sendObjMsg(map, PushMealActivity.UPDATE_EXPLV, handler);
                                TicketsManager.getInstance().updateAnalysisNumber();
                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {
                                if(response.error_code == 288001)
                                {
                                    CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                                    TicketsManager.getInstance().removeTicket(tbg.tickets);

                                    Map map = new HashMap<String, Integer>();
                                    if(groupPosition == 0)
                                    {
                                        map.put("flag", 0);
                                    }
                                    else if(groupPosition == 1)
                                    {
                                        map.put("flag", 1);
                                        map.put("productId",GridviewAnalysisFoodAdapter.lastChooseProductId);
                                        map.put("name",GridviewAnalysisFoodAdapter.lastName);
                                    }

                                    CommonUtils.sendObjMsg(map, PushMealActivity.UPDATE_EXPLV, handler);
                                    TicketsManager.getInstance().updateAnalysisNumber();
                                    return;
                                }
                                CommonUtils.LogWuwei(PushMealActivity.tag,"ApisManager.mealCheckout:"+response.error_message);
                                CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                                CommonUtils.sendMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handler);
                                ChangeTouchAll(true);
                            }
                        });
            }
        };


        swipeLayout.setOnTouchListener(otl);
        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {

            @Override
            public void onUpdate(SwipeLayout arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
                float alpha = (float) (Math.abs(arg1) / 440.0);
                swipeLayout.findViewById(R.id.bottom_wrapper).setAlpha(alpha);
            }

            @Override
            public void onStartOpen(SwipeLayout arg0) {
                // TODO Auto-generated method stub
                ((TextView) swipeLayout.findViewById(R.id.tvSwipeLayoutBottomTips)).setText("持续右滑出餐");
            }

            @Override
            public void onStartClose(SwipeLayout arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onOpen(SwipeLayout arg0) {
                // TODO Auto-generated method stub
                //全部出餐
                CommonUtils.sendMsg("全部出餐", PushMealActivity.SHOW_LOADING_TEXT, handler);

                final TicketBeanGroup tbg = childList.get(groupPosition).get(childPosition);
                final String orderId = tbg.order_id;
                ApisManager.orderAllCheckout(orderId, CommonUtils.getFormatDate(0), CommonUtils.getTimeBuckerId(mealBucketList), 0,
                        LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt), new ApiCallback() {
                            @Override
                            public void success(Object object) {
                                CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);

                                TicketsManager.getInstance().removeTicket(tbg.order_id);

                                Map map = new HashMap<String, Integer>();
                                if (groupPosition == 0) {
                                    map.put("flag", 0);
                                } else if (groupPosition == 1) {
                                    map.put("flag", 1);
                                    map.put("productId", GridviewAnalysisFoodAdapter.lastChooseProductId);
                                    map.put("name", GridviewAnalysisFoodAdapter.lastName);
                                }

                                CommonUtils.sendObjMsg(map, PushMealActivity.UPDATE_EXPLV, handler);
                                TicketsManager.getInstance().updateAnalysisNumber();

                            }

                            @Override
                            public void error(BaseApi.ApiResponse response) {
                                CommonUtils.LogWuwei(PushMealActivity.tag,"ApisManager.orderAllCheckout:"+response.error_message);
                                CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                                CommonUtils.sendMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handler);
                            }
                        });

            }

            @Override
            public void onHandRelease(SwipeLayout arg0, float arg1, float arg2) {
                // TODO Auto-generated method stub
                CommonUtils.LogWuwei(tag, "onHandRelease");
            }

            @Override
            public void onClose(SwipeLayout arg0) {
                // TODO Auto-generated method stub
            }
        });

        btn_chargeItem_name.setOnClickListener(ocl);
        btn_whether_packaged.setOnClickListener(ocl);
        swipeLayout.setOnClickListener(ocl);

        btn_chargeItem_name.setOnLongClickListener(olcl);
        btn_whether_packaged.setOnLongClickListener(olcl);
        swipeLayout.setOnLongClickListener(olcl);

        String itemName = btn_chargeItem_name.getText().toString();
        int lengthLimit = 0;
        int heigtLimit = 0;
        if (itemName.contains("月") && itemName.contains("日")) {
            lengthLimit = 15;
            heigtLimit = 200;
        } else {
            lengthLimit = 10;
            heigtLimit = 180;
        }

        if (itemName.length() < lengthLimit) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btn_chargeItem_name.getLayoutParams();
            lp.height = heigtLimit;
            btn_chargeItem_name.setLayoutParams(lp);
        } else {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btn_chargeItem_name.getLayoutParams();
            lp.height = heigtLimit * (btn_chargeItem_name.length()) / 10;
            btn_chargeItem_name.setLayoutParams(lp);
        }

        return grid;
    }

    /**
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    /****
     * 点击效果
     * @param view
     */
    public void clickEffectAndCall(View view, int serial_num, int packagedFlag) {

        boolean packaged = packagedFlag == 0 ? false : true;
        view.setBackgroundColor(Color.parseColor("#df6448"));
        Button btn_serinal_num = (Button) view.findViewById(R.id.btn_listview_all_serinal_num);
        Button btn_whether_packaged = (Button) view.findViewById(R.id.btn_listview_all_whether_packaged);
        Button btn_chargeItem_name = (Button) view.findViewById(R.id.btn_listview_all_chargeItem_name);

        btn_chargeItem_name.setTextColor(Color.parseColor("#ffffff"));
        btn_serinal_num.setTextColor(Color.parseColor("#ffffff"));
        btn_whether_packaged.setTextColor(Color.parseColor("#ffffff"));

        view.startAnimation(new AnimationUtils().loadAnimation(ctxt, R.anim.big_2_small));

    }


}
