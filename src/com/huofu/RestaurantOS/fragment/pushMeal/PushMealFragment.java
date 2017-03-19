package com.huofu.RestaurantOS.fragment.pushMeal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.ExpandableListviewWaitingPushAdapter;
import com.huofu.RestaurantOS.adapter.GridviewAnalysisFoodAdapter;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.pushMeal.TicketBeanGroup;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.bean.storeOrder.MealPortRelation;
import com.huofu.RestaurantOS.bean.storeOrder.StoreProduct;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.manager.TicketsManager;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;
import com.huofu.RestaurantOS.widget.toggleButton.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/11/19.
 */
@SuppressLint("ValidFragment")
public class PushMealFragment extends Fragment {

    Context ctxt;
    View grid;
    Handler handler;
    String tag = PushMealActivity.tag;
    ExpandableListView explv;//全部待出餐列表
    ExpandableListviewWaitingPushAdapter explvAdapter;
    List<MealBucket> listTimeBucket = new ArrayList<MealBucket>();
    List<StoreProduct> listStoreProduct = new ArrayList<StoreProduct>();
    List<StoreProduct> listStoreProductAnalysis = new ArrayList<StoreProduct>();
    int nowChooseGroupPositon=0;

    GridView gridviewAnalysis;//统计产品列表
    GridviewAnalysisFoodAdapter gridviewAnalysisFoodAdapter;
    private static float mDownX;
    private static float mDownY;
    public static boolean isExpandableListviewScroll = false;



    Button btnSetAnalysis;//设置统计产品的按钮
    Button btnEmptyGridview;
    Button btnEmptyExplv;
    LayoutInflater inflater;//可以加载不同的布局
    ToggleButton tbCheckoutTypeTips;//改变出餐状态的按钮（自动或者手动）
    TextView tvCheckoutTypeTips;//显示出餐的状态（自动或者手动）
    RelativeLayout rlMealDone;

    TextView tvNowPushMealEmpty;
    RelativeLayout rlNowPushMealEmpty;
    ToggleButton tbNowPushMealEmpty;




    public PushMealFragment(Context ctxt, Handler handler) {

        this.ctxt = ctxt;
        this.handler = handler;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        grid = inflater.inflate(R.layout.fragment_push_meal_main, null);
        this.inflater = inflater;

        init();

        widgetConfigure();

        dataInit();

        return grid;
    }



    private void init() {
        rlMealDone = (RelativeLayout)grid.findViewById(R.id.rl_mealdone);

        explv = (ExpandableListView) grid.findViewById(R.id.expandableListView_all_push);
        btnEmptyExplv = (Button)grid.findViewById(R.id.empty_meal_done_expandablelistview);

        gridviewAnalysis = (GridView) grid.findViewById(R.id.gridview_special_food);
        btnEmptyGridview = (Button) grid.findViewById(R.id.empty_meal_done_gridview);

        tvNowPushMealEmpty = (TextView)grid.findViewById(R.id.tv_now_push_meal);
        rlNowPushMealEmpty = (RelativeLayout)grid.findViewById(R.id.rl_now_push_meal);
        tbNowPushMealEmpty = (ToggleButton)grid.findViewById(R.id.tb_now_push_meal);
        tbNowPushMealEmpty.setToggleOn();

        btnSetAnalysis = (Button) grid.findViewById(R.id.button_gridview_set_analysis);
        tbCheckoutTypeTips = (ToggleButton)grid.findViewById(R.id.tb_meal_done_whether_auto_push);
        tvCheckoutTypeTips = (TextView)grid.findViewById(R.id.tv_meal_done_auto_push_tips);
    }

    private void widgetConfigure() {

        //配置左侧待出餐列表
        explvAdapter = new ExpandableListviewWaitingPushAdapter(ctxt,handler,explv,listTimeBucket);
        explvAdapter.groupList = new ArrayList<String>();
        explvAdapter.groupList.add("全部待出餐");
        explvAdapter.groupList.add("统计产品");
        explv.setAdapter(explvAdapter);
        explv.setGroupIndicator(null);
        explv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        explv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                nowChooseGroupPositon = groupPosition;
            }
        });

        explv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                // TODO Auto-generated method stub


                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = ev.getX();
                        mDownY = ev.getY();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        return false;

                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(ev.getY() - mDownY) > 50) {
                            isExpandableListviewScroll = true;
                        }
                        break;
                    default:
                        return false;
                }

                return false;
            }
        });

        explv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                CommonUtils.LogWuwei(tag, "-----------onScrollStateChanged---------");
                isExpandableListviewScroll = true;

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                //PushAllWaitingExpandableAdapter.isScroll = true;
                isExpandableListviewScroll = true;
            }
        });


        //配置右侧统计数据
        gridviewAnalysisFoodAdapter = new GridviewAnalysisFoodAdapter(explv,explvAdapter,ctxt,
                handler, listStoreProduct);
        gridviewAnalysis.setAdapter(gridviewAnalysisFoodAdapter);

        //配置统计按钮
        btnSetAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.sendMsg("加载统计产品中...",PushMealActivity.SHOW_LOADING_TEXT,handler);
                if (listTimeBucket.size() == 0) {
                    CommonUtils.sendMsg("加载营业时间段...",PushMealActivity.SHOW_LOADING_TEXT,handler);
                    ApisManager.GetTimeBucketList(new ApiCallback() {
                        @Override
                        public void success(Object object) {
                            listTimeBucket = (List<MealBucket>) object;
                            getStoreProductSetList();
                        }

                        @Override
                        public void error(BaseApi.ApiResponse response) {
                            CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                            CommonUtils.sendMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handler);
                        }
                    });
                } else {
                    getStoreProductSetList();
                }
            }
        });

        tbCheckoutTypeTips.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(final boolean on) {
                dealToggleButton(on);
            }
        });
        tbNowPushMealEmpty.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(on)
                {
                    rlNowPushMealEmpty.setVisibility(View.VISIBLE);
                    rlMealDone.setVisibility(View.INVISIBLE);
                }
                else
                {
                    rlNowPushMealEmpty.setVisibility(View.INVISIBLE);
                    rlMealDone.setVisibility(View.VISIBLE);
                }
                dealToggleButton(on);

            }
        });
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealToggleButton(!tbNowPushMealEmpty.isToggleOn());
            }
        };

        rlNowPushMealEmpty.setOnClickListener(ocl);
        tvNowPushMealEmpty.setOnClickListener(ocl);

    }


    public void dealToggleButton(final boolean on)
    {
        List<MealPortRelation> list = new ArrayList<MealPortRelation>();
        if(on)//手动转自动
        {
            MealPortRelation mpr = new MealPortRelation();
            mpr.checkout_type = 0;
            mpr.port_id = LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt);
            mpr.printer_status = 1;
            mpr.task_status = 0;
            list.add(mpr);
        }
        else //自动转手动
        {
            MealPortRelation mpr = new MealPortRelation();
            mpr.checkout_type = 0;
            mpr.port_id = LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt);
            mpr.printer_status = 1;
            mpr.task_status = 1;
            list.add(mpr);
        }
        CommonUtils.sendMsg("切换中....",PushMealActivity.SHOW_LOADING_TEXT,handler);
        ApisManager.registTaskRelation(LocalDataDeal.readFromLocalAppCopyId(ctxt), list, new ApiCallback() {
            @Override
            public void success(Object object) {
                CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                CommonUtils.sendMsg("切换成功", PushMealActivity.SHOW_TOAST, handler);
                TicketsManager.getInstance().cleanTicketBeanGroups();
                TicketsManager.getInstance().cleanLatestNumber();
                if (on)//由手工出餐切换为自动出餐
                {
                    LocalDataDeal.writePushMealWhetherAuto(true, ctxt);
                    listStoreProduct.clear();
                    TicketsManager.getInstance().stopCheck = true;
                    CommonUtils.sendMsg("自动出餐", PushMealActivity.UPDATE_NOW_PUSH_WAY, handler);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updatePushWayTips(1);
                            explvAdapter.notifyDataSetChanged();
                            gridviewAnalysisFoodAdapter.notifyDataSetChanged();

                            rlNowPushMealEmpty.setVisibility(View.VISIBLE);
                            rlMealDone.setVisibility(View.INVISIBLE);
                            tbNowPushMealEmpty.setToggleOn();
                        }
                    });

                } else//由自动出餐切换为手工出餐
                {
                    LocalDataDeal.writePushMealWhetherAuto(false, ctxt);
                    TicketsManager.getInstance().stopCheck = false;
                    CommonUtils.sendMsg(LocalDataDeal.readFromLocalMealDoneChooseMealPortName(ctxt), PushMealActivity.UPDATE_NOW_PUSH_WAY, handler);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            explvAdapter.notifyDataSetChanged();
                            updatePushWayTips(0);
                            refreshAnalysisFoods();
                        }
                    });
                }
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                CommonUtils.sendMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handler);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (on) {
                            tbCheckoutTypeTips.setToggleOff();
                        } else {
                            tbCheckoutTypeTips.setToggleOn();
                        }
                    }
                });
            }
        });
    }

    public void getStoreProductSetList()
    {
        ApisManager.getMealProdcutList(CommonUtils.getTimeBuckerId(listTimeBucket),
                new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                        listStoreProductAnalysis = (List<StoreProduct>) object;
                        CommonUtils.sendMsg("", PushMealActivity.SHOW_ANALYSIS_SET_DIALOG, handler);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });
    }


    /**
     * 加载营业时间段、统计产品列表、左侧列表全量更新
     */
    public void dataInit() {

        listStoreProduct.clear();
        updateAnalysisGridview();

        TicketsManager.getInstance().cleanLatestNumber();
        TicketsManager.getInstance().cleanTicketBeanGroups();

        explvAdapter.childList.clear();
        explvAdapter.childList.add(0, new ArrayList<TicketBeanGroup>());
        explvAdapter.childList.add(1, new ArrayList<TicketBeanGroup>());
        updateWaitingPushExplv();

        if(listTimeBucket.size() == 0)
        {
            ApisManager.GetTimeBucketList(new ApiCallback() {
                @Override
                public void success(Object object) {
                    listTimeBucket.clear();
                    listTimeBucket = (List<MealBucket>) object;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            explvAdapter.notifyDataSetChanged();
                        }
                    });
                    refreshAnalysisFoods();
                }

                @Override
                public void error(BaseApi.ApiResponse response) {

                }
            });
        }
        else
        {
            CommonUtils.sendMsg("刷新统计数据",PushMealActivity.SHOW_LOADING_TEXT,handler);
            refreshAnalysisFoods();
        }
        TicketsManager.getInstance().startCheckNewTickets(LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt));
    }

    /**
     * 更新统计列表
     */
    public void refreshAnalysisFoods() {

        ApisManager.getMealProdcutList(CommonUtils.getTimeBuckerId(listTimeBucket), new ApiCallback() {
            @Override
            public void success(Object object) {

                listStoreProduct.clear();
                List<StoreProduct> list = (List<StoreProduct>) object;
                for (StoreProduct sap : list) {
                    if (sap.meal_stat == 1) {
                        listStoreProduct.add(sap);
                    }
                }
                TicketsManager.getInstance().updateAnalysisList(listStoreProduct);
                TicketsManager.getInstance().updateAnalysisNumber();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listStoreProduct.size() > 0) {
                            if (btnEmptyGridview.getVisibility() == View.VISIBLE) {
                                btnEmptyGridview.setVisibility(View.INVISIBLE);
                            }
                            gridviewAnalysisFoodAdapter.notifyDataSetChanged();
                        } else {
                            if (btnEmptyGridview.getVisibility() == View.INVISIBLE) {
                                btnEmptyGridview.setText("没有设置统计菜品");
                                btnEmptyGridview.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                });

                TicketsManager.handler = handler;
                TicketsManager.getInstance().stopCheck = false;
                CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.sendMsg("", PushMealActivity.HIDE_LOADING, handler);
                CommonUtils.sendObjMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handler);

            }
        });

    }


    public List<StoreProduct> getAnalysisStoreProduct()
    {
        return listStoreProductAnalysis;
    }

    public void updateWaitingPushExplv()
    {
        explvAdapter.notifyDataSetChanged();
    }

    /**
     * 更新左侧列表
     * @param flag->0 更新全部待出餐数据  flag->1更新某个产品的待出餐数据
     * @param product_id 产品id
     */
    public void updateWaitingPushExplv(int flag,Long product_id,String name,boolean flagScrollToTop) {

        List<TicketBeanGroup> listTBG = new ArrayList<TicketBeanGroup>();
        boolean flagUpdate = false;//是否更新统计产品部分
        if(flag == 0)
        {
            explvAdapter.childList.clear();
            listTBG.addAll(TicketsManager.getInstance().getTicketBeanGroups());
            explvAdapter.childList.add(0, listTBG);
            if(listTBG.size() == 0)
            {
                if(explv.getVisibility() == View.VISIBLE)
                {
                    explv.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                if(explv.getVisibility() == View.INVISIBLE)
                {
                    explv.setVisibility(View.VISIBLE);
                }

                if(explvAdapter.groupList.size() > 1) {
                    explvAdapter.groupList.remove(1);
                }

                if(nowChooseGroupPositon == 0)
                {
                    explv.expandGroup(0);
                }
                else if(nowChooseGroupPositon == 1)
                {
                    flagUpdate = true;
                    product_id = GridviewAnalysisFoodAdapter.lastChooseProductId;
                    name = GridviewAnalysisFoodAdapter.lastName;
                }
            }
        }

        if(flag == 1 || flagUpdate)
        {
            listTBG = TicketsManager.getInstance().getAnalysisTicketBeanGroup(product_id);
            if(listTBG.size() == 0)
            {
                {
                    if(explvAdapter.childList.size()>1)
                    {
                        explvAdapter.childList.set(1,new ArrayList<TicketBeanGroup>());
                    }
                    else
                    {
                        explvAdapter.childList.add(1, new ArrayList<TicketBeanGroup>());
                    }

                    explvAdapter.notifyDataSetChanged();
                    return;
                }
            }
            else
            {
                if(explvAdapter.childList.size()>1)
                {
                    explvAdapter.childList.set(1,listTBG);
                }
                else
                {
                    explvAdapter.childList.add(1,listTBG);
                }
            }

            if(explv.getVisibility() == View.INVISIBLE)
            {
                explv.setVisibility(View.VISIBLE);
            }

            if(explvAdapter.groupList.size() > 1)
            {
                explvAdapter.groupList.set(1,"统计产品-" + name);
            }
            else {
                explvAdapter.groupList.add(1, "统计产品-" + name);
            }
            if(explv.isGroupExpanded(0))
            {
                explv.collapseGroup(0);
            }

            if(!explv.isGroupExpanded(1))
            {
                explv.expandGroup(1);
            }
            if(flagScrollToTop)
            {
                explv.smoothScrollToPosition(0);
            }
        }

        explvAdapter.notifyDataSetChanged();
    }

    public void updateAnalysisGridview()
    {
        gridviewAnalysisFoodAdapter.notifyDataSetChanged();
    }

    /**
     * flag = 0 手工出餐
     * flag = 1 自动出餐
     * @param flag
     */
    public void updatePushWayTips(int flag)
    {
        if(flag == 0)
        {
            tbCheckoutTypeTips.setToggleOff();
            tvCheckoutTypeTips.setText("本出餐口当前处于手工出餐状态");
            rlNowPushMealEmpty.setVisibility(View.INVISIBLE);
            rlMealDone.setVisibility(View.VISIBLE);
        }
        else
        {
            tbCheckoutTypeTips.setToggleOn();
            tvCheckoutTypeTips.setText("本出餐口当前处自动出餐状态");
            rlNowPushMealEmpty.setVisibility(View.VISIBLE);
            rlMealDone.setVisibility(View.INVISIBLE);
        }
    }

}
