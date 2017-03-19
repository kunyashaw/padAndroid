package com.huofu.RestaurantOS.ui.pannel.stockPlan;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.ListViewStockSupplyClosedModifyAdapter;
import com.huofu.RestaurantOS.adapter.ListviewStockPlanProductAdapter;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.ProductWeekItem;
import com.huofu.RestaurantOS.bean.StoreProduct;
import com.huofu.RestaurantOS.bean.storeOrder.MealBucket;
import com.huofu.RestaurantOS.ui.BaseActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.widget.Keyboard;
import com.huofu.RestaurantOS.widget.toggleButton.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by wow on 15/8/7.
 */
public class StockPlanActivity extends BaseActivity {

    public Context ctxt = null;
    public ListView listviewAllProduct = null;
    public SearchView searchViewProduct = null;
    public TextView textViewNowChooseProduct = null;
    public LayoutInflater inflater = null;
    public static boolean flagActivte = false;
    public String lastQueryString = "";
    public static String tag = "StockPlanActivity";
    public List<StoreProduct> listStoreProduct = null;
    public List<StoreProduct> listStoreProductBackup = null;
    List<List<StoreProduct>> listAllSP = null;
    public static List<MealBucket> listMealBucket = null;

    public Button buttonMenu = null;


    //public int nowChoocsePostion = 0;
    public ListviewStockPlanProductAdapter listviewAllProcutAdapter = null;
    public static int nowChooseProductListPositon = 0;
    public static int nowChooseProductId = 0;
    public Keyboard keyboard;
    public View.OnClickListener oclKeyboardEnter = null;
    public List<ListViewStockSupplyClosedModifyAdapter> adapterList = null;

    private ToggleButton tbInventory = null;
    private ToggleButton tbPeriodicInventory = null;
    private TextView tvTips = null;
    private RelativeLayout rlPeriodInventory = null;//周期库存
    private LinearLayout llStockSupplyMain = null;
    private RelativeLayout rlNormalInventory = null;//固定库存
    private Button btnModifiedFixedAmount = null;
    private long todatTimeStamp = 0;

    boolean flagFirst = true;//是否首次，如果首次，则在模拟产品列表的第一个产品被点击


    public static final int SHOW_TOAST = 0;
    public static final int SHOW_LOADING_TEXT = 1;
    public static final int HIDE_LOADING = 2;
    public static final int NOTIFY_PRODUCT_LIST = 3;
    public static final int SHOW_START_SELL_SET_WINDOW = 4;
    public static final int UPDATE_KEYBOARD_ENTER_OCL = 5;
    public static final int UPDATE_QUERY_PRODUCT = 6;
    public static final int UPDATE_CHOOSE_PRODUCT_NAME = 7;
    public static final int UPDATE_QUERY_FIXED_PRODUCT = 8;
    public static final int UPDATE_QUERY_PERID_DATA = 9;
    public static final int SLIDING_DRAWDER_TOGGLE_ON = 10;
    public static final int UPDATE_NOW_MODIFED_FIXED_CONTENT = 11;
    public static final int GET_PRODUCT_LIST = 12;
    public static final int GET_TIME_BUCKET_LIST = 13;

    /************************* activity 生命周期******************************/


    /**
     * 开始
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_stock_plan);
        init();
        widgetConfigure();
        loadTimeBucketData();
        getProductList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        flagActivte = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flagActivte = false;
        CommonUtils.sendMailToMe(MainApplication.getContext(),7);
    }


    /************************初始化及控件配置******************************/

    /***
     * 初始化
     */
    public void init() {

        listStoreProduct = new ArrayList<StoreProduct>();
        listStoreProductBackup = new ArrayList<StoreProduct>();
        searchViewProduct = (SearchView) findViewById(R.id.searchView_stock_plan);
        listviewAllProduct = (ListView) findViewById(R.id.listview_product_stock_plan);
        ctxt = MainApplication.getContext();
        tag = "stockPlanActivity";

        adapterList = new ArrayList<ListViewStockSupplyClosedModifyAdapter>();
        inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listAllSP = new ArrayList<List<StoreProduct>>();


        Time time = new Time();
        time.setToNow();
        Calendar cal = Calendar.getInstance();
        cal.set(time.year, time.month, time.monthDay, 0, 0, 0);
        todatTimeStamp = (long) (cal.getTimeInMillis() / 1000);
    }

    /***
     * 控件配置
     */
    public void widgetConfigure() {

        tabConfigure();
        launchPadConfigure();

        searchViewProduct.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)//开启搜索
                {
                    listStoreProductBackup.clear();
                    listStoreProductBackup.addAll(listStoreProduct);
                    listStoreProduct.clear();
                } else//关闭搜索
                {
                    listStoreProduct.clear();
                    ;
                    listStoreProduct.addAll(listStoreProductBackup);
                    listStoreProductBackup.clear();
                }
            }
        });

        searchViewProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                for (int k = 0; k < listStoreProductBackup.size(); k++) {
                    StoreProduct sp = listStoreProductBackup.get(k);
                    if (sp.name.contains(query)) {
                        listStoreProduct.add(sp);
                    }
                }
                sendUIMessage(NOTIFY_PRODUCT_LIST, mUiHandler);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //sendUIMessage(SHOW_TOAST,"newText is "+newText);
                //if(newText.length() < lastQueryString.length())
                {
                    listStoreProduct.clear();
                }

                for (int k = 0; k < listStoreProductBackup.size(); k++) {
                    StoreProduct sp = listStoreProductBackup.get(k);
                    if (sp.name.contains(newText)) {
                        listStoreProduct.add(sp);
                    }
                }
                CommonUtils.LogWuwei(tag, "listStoreProduct size is " + listStoreProduct.size());
                sendUIMessage(NOTIFY_PRODUCT_LIST, mUiHandler);
                lastQueryString = newText;
                return false;
            }
        });

        oclKeyboardEnter = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };//初始化
        keyboard = new Keyboard(ctxt, null, mslidingMenu.mRightBehindBase, mslidingMenu, oclKeyboardEnter);
        mslidingMenu.setRightBehindContentView(keyboard);


        tbInventory = (ToggleButton) findViewById(R.id.toggleButton_stock_plan_open_inventory);
        tbPeriodicInventory = (ToggleButton) findViewById(R.id.toggleButton_stock_plan_open_periodic_inventory);
        tvTips = (TextView) findViewById(R.id.textview_modify_stock_plan_closed_tips);
        rlPeriodInventory = (RelativeLayout) findViewById(R.id.rl_stock_supply_plan_periodec_inventory);
        llStockSupplyMain = (LinearLayout) findViewById(R.id.ll_stock_plan_open_periodec_list_main);
        rlNormalInventory = (RelativeLayout) findViewById(R.id.rl_layout_modify_stock_plan_closed_choose_normal_inventory);
        btnModifiedFixedAmount = (Button) findViewById(R.id.btn_choose_fixed_amount_layout_stock_plan_closed);

        View.OnClickListener oclModifyFixedAmount = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oclKeyboardEnter = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.LogWuwei(tag,"正在将固定库存修改为:"+keyboard.getContent());
                        btnModifiedFixedAmount.setText(keyboard.getContent());
                        keyboard.clearContent();
                        mslidingMenu.closeRightSide();
                        mslidingMenu.flagRightOpen = false;

                        loadUpdateInventoryData(Double.parseDouble(btnModifiedFixedAmount.getText().toString()), true, 2);
                    }
                };
                CommonUtils.LogWuwei(tag,"修改固定库存中...");
                keyboard.setPositivOCL(oclKeyboardEnter);
                mslidingMenu.toggleRightDrawer();

                keyboard.getFoucs();
            }
        };
        btnModifiedFixedAmount.setOnClickListener(oclModifyFixedAmount);
        rlNormalInventory.setOnClickListener(oclModifyFixedAmount);

        tbInventory.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {

                StoreProduct sp = listStoreProduct.get(nowChooseProductListPositon);

                if (on) {
                    CommonUtils.LogWuwei(tag,"++用户开启库存了");
                    sp.inv_enabled = 1;
                    tvTips.setVisibility(View.VISIBLE);
                    rlPeriodInventory.setVisibility(View.VISIBLE);
                    //if (tbPeriodicInventory.isToggleOn())
                    if(sp.inv_type == 1)
                    {
                        loadUpdateInventoryData(Double.parseDouble(btnModifiedFixedAmount.getText().toString()), true, 1);
                        llStockSupplyMain.setVisibility(View.VISIBLE);
                        rlNormalInventory.setVisibility(View.INVISIBLE);
                        tbPeriodicInventory.toggleOn();

                    } else {
                        loadUpdateInventoryData(Double.parseDouble(btnModifiedFixedAmount.getText().toString()), true, 2);
                        llStockSupplyMain.setVisibility(View.INVISIBLE);
                        rlNormalInventory.setVisibility(View.VISIBLE);
                        tbPeriodicInventory.toggleOff();
                    }


                } else {
                    //关掉库存
                    CommonUtils.LogWuwei(tag,"--用户关闭库存了");
                    sp.inv_enabled = 0;
                    loadUpdateInventoryData(Double.parseDouble(btnModifiedFixedAmount.getText().toString()), false, 0);
                    tvTips.setVisibility(View.INVISIBLE);
                    rlPeriodInventory.setVisibility(View.INVISIBLE);
                    llStockSupplyMain.setVisibility(View.INVISIBLE);
                    rlNormalInventory.setVisibility(View.INVISIBLE);
                }
                sendUIMessage(NOTIFY_PRODUCT_LIST,mUiHandler);
            }
        });

        tbPeriodicInventory.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    CommonUtils.LogWuwei(tag,"++用户开启库周期存了");
                    loadUpdateInventoryData(Double.parseDouble(btnModifiedFixedAmount.getText().toString()), true, 1);
                    tvTips.setText(getString(R.string.stockSupplyOpenPeriodicTips));
                    llStockSupplyMain.setVisibility(View.VISIBLE);
                    rlNormalInventory.setVisibility(View.INVISIBLE);
                    if(adapterList.size() == 0)
                    {
                        sendUIMessage(SHOW_TOAST,"开启周期库存失败\n请去产品设置中，找到本产品并设置销售周期,然后再到本页面设置库存信息");
                        sendUIMessage(SHOW_TOAST,"开启周期库存失败\n请去产品设置中，找到本产品并设置销售周期,然后再到本页面设置库存信息");
                        tbPeriodicInventory.setToggleOff();
                        rlNormalInventory.setVisibility(View.VISIBLE);
                    }
                } else {
                    CommonUtils.LogWuwei(tag,"--用户关闭库周期存了");
                    loadUpdateInventoryData(Double.parseDouble(btnModifiedFixedAmount.getText().toString()), true, 2);
                    tvTips.setText(getString(R.string.stockSupplyOpenNormalTips));
                    llStockSupplyMain.setVisibility(View.INVISIBLE);
                    rlNormalInventory.setVisibility(View.VISIBLE);
                }

            }
        });


        listviewAllProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                StockPlanActivity.nowChooseProductListPositon = position;
                nowChooseProductId = listStoreProduct.get(position).product_id;
                CommonUtils.sendMsg("", StockPlanActivity.UPDATE_QUERY_PRODUCT, mUiHandler);
                CommonUtils.sendMsg("", StockPlanActivity.UPDATE_CHOOSE_PRODUCT_NAME, mUiHandler);
                CommonUtils.sendMsg("", StockPlanActivity.UPDATE_QUERY_FIXED_PRODUCT, mUiHandler);
                listviewAllProduct.getAdapter().getView(position, null, null).setBackgroundColor(ctxt.getResources().getColor(R.color.Blue));
                CommonUtils.LogWuwei(tag,"~~~~~~~~~~产品"+listStoreProduct.get(position).name+"被选中~~~~~~~~");

            }
        });
        showRightSetDetailLayout();

    }


    /****
     * 显示单击某个产品后的具体设置信息
     */
    public void showRightSetDetailLayout() {
        adapterList.clear();
        llStockSupplyMain.removeAllViews();
        StoreProduct sp = new StoreProduct();

        rlNormalInventory.setVisibility(View.INVISIBLE);

        for (int k = 0; k < listAllSP.size(); k++) {
            final ListViewStockSupplyClosedModifyAdapter adapter = new ListViewStockSupplyClosedModifyAdapter
                    (listAllSP.get(k), ctxt, mUiHandler, k);
            final String bucketName = listAllSP.get(k).get(0).store_time_bucket.name;
            adapterList.add(adapter);

            ListView lv = new ListView(ctxt);
            lv.setAdapter(adapter);

            View gridTitle = inflater.inflate(R.layout.listview_title_stock_supply_closed_modify, null);
            Button btn = (Button) gridTitle.findViewById(R.id.btn_listview_title_stock_supply_closed_modify);
            TextView tv = (TextView) gridTitle.findViewById(R.id.textview_listview_title_stock_supply_closed_modify);

            String timeStr = "(" +
                    CommonUtils.getStrTime(todatTimeStamp + (listAllSP.get(k).get(0).store_time_bucket.start_time / 1000)) + "-" +
                    CommonUtils.getStrTime(todatTimeStamp + (listAllSP.get(k).get(0).store_time_bucket.end_time) / 1000) + ")";
            tv.setText(listAllSP.get(k).get(0).store_time_bucket.name + " " + timeStr);

            View.OnClickListener ocl = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    oclKeyboardEnter = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            double amount = Double.parseDouble(keyboard.getContent());
                            for (int k = 0; k < adapter.getCount(); k++) {
                                adapter.ls.get(k).amount = amount;
                            }
                            CommonUtils.LogWuwei(tag,bucketName+"正在将周期库存，统一修改库存为"+":"+amount);
                            adapter.notifyDataSetChanged();
                            keyboard.clearContent();
                            mslidingMenu.closeRightSide();
                            mslidingMenu.flagRightOpen = false;

                            modifyPeriodicInventoryData();
                        }
                    };
                    keyboard.setPositivOCL(oclKeyboardEnter);
                    mslidingMenu.toggleRightDrawer();
                }
            };
            btn.setOnClickListener(ocl);
            gridTitle.setOnClickListener(ocl);
            tv.setOnClickListener(ocl);

            int height = 120 * listAllSP.get(k).size();
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.MATCH_PARENT, height);
            if(k != 0)
            {
                View view = new View(ctxt);
                view.setMinimumHeight(80);
                view.setBackgroundColor(getResources().getColor(R.color.BackgroudColor));
                llStockSupplyMain.addView(view);
            }
            llStockSupplyMain.addView(gridTitle);
            llStockSupplyMain.addView(lv, lp);
        }

        View view = new View(ctxt);
        view.setMinimumHeight(80);
        llStockSupplyMain.addView(view);

        if (listStoreProduct != null && listStoreProduct.size() > 0 && nowChooseProductListPositon >= 0) {
            sp = listStoreProduct.get(nowChooseProductListPositon);
            if (sp.inv_enabled == 0) {
                CommonUtils.LogWuwei(tag,"库存默认是关闭的");
                tbInventory.toggleOff();
            } else {
                tbInventory.toggleOn();
                CommonUtils.LogWuwei(tag, "库存默认是开启的");
            }
            btnModifiedFixedAmount.setText(CommonUtils.DoubleDeal(sp.amount));
        }


    }

    /****
     * tab配置
     */
    public void tabConfigure() {

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f0f0f0")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_stock_plan);
        actionBar.setDisplayShowHomeEnabled(false);

        View grid = actionBar.getCustomView();
        textViewNowChooseProduct = (TextView) grid.findViewById(R.id.btn_action_bar_stock_plan_now_choose);
        buttonMenu = (Button) grid.findViewById(R.id.button_action_bar_stock_plan_menu);

        View.OnClickListener oclMenu = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tintManager.setStatusBarDarkMode(false, StockPlanActivity.this);
                tintManager.setStatusBarTintResource(R.drawable.statusbar_bg);
                mslidingMenu.toggleLeftDrawer();
            }
        };
        buttonMenu.setOnClickListener(oclMenu);
        findViewById(R.id.rl_action_bar_stock_plan_menu).setOnClickListener(oclMenu);

    }


    /************************handler 处理消息******************************/
    /***
     * 消息处理
     *
     * @param msg
     */
    @Override
    protected void dealWithmessage(Message msg) {

        switch (msg.what) {
            case SHOW_TOAST:
                HandlerUtils.showToast(ctxt, msg.obj.toString());
                break;
            case NOTIFY_PRODUCT_LIST:

                int index = listviewAllProduct.getFirstVisiblePosition();
                View v = listviewAllProduct.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - listviewAllProduct.getPaddingTop());

                listviewAllProcutAdapter = new ListviewStockPlanProductAdapter(listStoreProduct, ctxt, mUiHandler);
                listviewAllProduct.setAdapter(listviewAllProcutAdapter);
                listviewAllProcutAdapter.notifyDataSetChanged();

                listviewAllProduct.setSelectionFromTop(index, top);

                if(flagFirst)
                {
                    listviewAllProduct.performItemClick(
                            listviewAllProduct.getAdapter().getView(0,null,null),
                            0,
                            listviewAllProduct.getAdapter().getItemId(0));
                    flagFirst = false;
                }
                break;
            case HIDE_LOADING:
                hideLoadingDialog();
                break;
            case SHOW_LOADING_TEXT:
                showLoadingDialog((String) msg.obj);
                break;
            case UPDATE_QUERY_PERID_DATA:
                listAllSP = (List<List<StoreProduct>>) msg.obj;
                sendUIMessage(SHOW_START_SELL_SET_WINDOW, mUiHandler);
                break;
            case SHOW_START_SELL_SET_WINDOW:
                showRightSetDetailLayout();
                break;
            case UPDATE_KEYBOARD_ENTER_OCL:
                Map map = (Map) msg.obj;
                final int k = (Integer) map.get("list_order");
                final int position = (Integer) map.get("position");
                final List<StoreProduct> ls = (List) map.get("ls");
                oclKeyboardEnter = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.LogWuwei(tag,"正在将库存调整为:"+keyboard.getContent());
                        ls.get(position).amount = Double.parseDouble(keyboard.getContent());
                        adapterList.get(k).notifyDataSetChanged();
                        keyboard.clearContent();
                        mslidingMenu.closeRightSide();
                        mslidingMenu.flagRightOpen = false;
                        modifyPeriodicInventoryData();
                    }
                };
                keyboard.setPositivOCL(oclKeyboardEnter);
                break;
            case UPDATE_QUERY_PRODUCT:
                loadQueryResultData();
                break;
            case UPDATE_QUERY_FIXED_PRODUCT:
                loadQueryFixedResultData();
                break;
            case UPDATE_CHOOSE_PRODUCT_NAME:
                textViewNowChooseProduct.setText(listStoreProduct.get(nowChooseProductListPositon).name);
                break;
            case SLIDING_DRAWDER_TOGGLE_ON:
                mslidingMenu.toggleRightDrawer();
                break;
            case UPDATE_NOW_MODIFED_FIXED_CONTENT:
                btnModifiedFixedAmount.setText("" + (String) msg.obj);
                break;
            case GET_PRODUCT_LIST:
                getProductList();
                break;
            case GET_TIME_BUCKET_LIST:
                loadTimeBucketData();
                break;
        }
    }


    /************************ 数据请求和处理******************************/
    /****
     * 获取本店铺的产品列表
     */
    public void getProductList() {
        ApisManager.getProductList(new ApiCallback() {
            @Override
            public void success(Object object) {

                List<StoreProduct> list = new ArrayList<StoreProduct>();
                listStoreProduct = (List<StoreProduct>) object;
                sendUIMessage(NOTIFY_PRODUCT_LIST, null);

            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.LogWuwei(tag, "获取产品列表失败:"+response.error_message);
            }
        });
    }


    /****
     * 更改库存
     *
     * @param amount
     * @param inv_enabled 是否开启库存
     * @param inv_type    0=未开启，1=周期库存，2=固定库存
     */
    public void loadUpdateInventoryData(final double amount, boolean inv_enabled, final int inv_type) {

        if (listStoreProduct == null || listStoreProduct.size() == 0) {
            return;
        }

        StoreProduct sid = listStoreProduct.get(nowChooseProductListPositon);
        ApisManager.UpdateInventory(sid.product_id, inv_enabled, inv_type, amount, new ApiCallback() {
            @Override
            public void success(Object object) {
                sendUIMessage(HIDE_LOADING, null);
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.LogWuwei(tag, "库存修改失败:"+response.error_message);
                sendUIMessage(HIDE_LOADING, null);
            }
        });
    }


    /****
     * 修改周期性库存
     */
    public void modifyPeriodicInventoryData() {
        StoreProduct sp = listStoreProduct.get(nowChooseProductListPositon);
        ApisManager.UpdateInventory(sp.product_id, true, 1, 0, new ApiCallback() {
            @Override
            public void success(Object object) {
                long product_id = listStoreProduct.get(nowChooseProductListPositon).product_id;
                List<ProductWeekItem> list = new ArrayList<ProductWeekItem>();
                for (int k = 0; k < adapterList.size(); k++) {
                    ListViewStockSupplyClosedModifyAdapter adapter = adapterList.get(k);
                    for (int t = 0; t < adapter.getCount(); t++) {
                        StoreProduct sp = adapter.ls.get(t);
                        ProductWeekItem pwi = new ProductWeekItem();
                        pwi.week_day = sp.week_day;
                        pwi.time_bucket_id = sp.time_bucket_id;
                        pwi.amount = sp.amount;
                        if (sp.thisWeek) {
                            pwi.next_week = 0;
                        } else {
                            pwi.next_week = 1;
                        }
                        list.add(pwi);
                    }
                }

                ApisManager.UpdateWeekProductInventory(product_id, list, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        // adapterList.clear();
                        CommonUtils.LogWuwei(tag,"周期库存修改成功");
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        //adapterList.clear();
                        CommonUtils.LogWuwei(tag,"周期库存修改失败"+response.error_message);
                    }
                });
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.LogWuwei(tag, "库存修改失败:"+response.error_message);
            }
        });

    }

    /***
     * 获取营业时间段，并加载对应的周期库存列表
     */
    public void loadTimeBucketData() {
        ApisManager.GetTimeBucketList(new ApiCallback() {
            @Override
            public void success(Object object) {
                List<MealBucket> list = (List<MealBucket>) object;
                listMealBucket = list;
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                sendUIMessage(GET_TIME_BUCKET_LIST,mUiHandler);
                CommonUtils.LogWuwei(tag, "获取营业时间段失败:" + response.error_message);
            }
        });
    }

    /****
     * 查询周期性库存结果
     */
    public void loadQueryResultData() {
        ApisManager.QueryInventoryByProduct(listStoreProduct.get(nowChooseProductListPositon).product_id, new ApiCallback() {
            @Override
            public void success(Object object) {
                adapterList.clear();
                sendUIMessage(UPDATE_QUERY_PERID_DATA, object);//更新数据
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                adapterList.clear();
                CommonUtils.LogWuwei(tag, "产品查询失败:" + response.error_message);
            }
        });
    }

    /****
     * 查询固定库存结果
     */
    public void loadQueryFixedResultData() {

        ApisManager.QueryFixedInventoryByProduct(listStoreProduct.get(nowChooseProductListPositon).product_id, new ApiCallback() {
            @Override
            public void success(Object object) {
                JSONObject obj = (JSONObject) object;

                StoreProduct sp = JSON.parseObject(obj.toString(), StoreProduct.class);
                listStoreProduct.get(nowChooseProductListPositon).amount = sp.amount;
                sendUIMessage(UPDATE_NOW_MODIFED_FIXED_CONTENT, Double.toString(sp.amount));
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.LogWuwei(tag, "固定库存修改失败:"+response.error_message);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
