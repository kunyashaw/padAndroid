package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.storeOrder.StoreOrder;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.Amount;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.bean.user.DeliveryStaff;
import com.huofu.RestaurantOS.ui.pannel.delivery.DeliveryActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****
 * 外送中搜索后对应结果的适配器
 */

public class GridviewSearchMobileResultAdapter extends BaseAdapter {


    List<OrderDetailInfo> listODI;
    Handler handler;
    Context ctxt;
    String mobile;

    public GridviewSearchMobileResultAdapter(List<OrderDetailInfo> ls, Handler handler, Context ctxt, String mobile) {
        // TODO Auto-generated constructor stub
        this.listODI = ls;
        this.handler = handler;
        this.ctxt = ctxt;
        this.mobile = mobile;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listODI.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listODI.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        View grid = null;

        switch (listODI.get(position).store_order.store_order_delivery.delivery_status) {
            case 1:
                grid = getWaitingToDo(position);
                break;
            case 2:
                grid = getDoing(position);
                break;
            case 3:
                grid = getWaitingDelivery(position);
                break;
            case 4:
                grid = getDeliverying(position);
                break;
            case 5:
                grid = getDeliveried(position);
                break;
            default:
                grid = new TextView(ctxt);
                ((TextView) grid).setText("status is " + listODI.get(position).store_order.store_order_delivery.delivery_status);
                break;
        }

        return grid;
    }


    public View getWaitingToDo(final int position) {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.gridview_delivery_waiting_todo, null);


        /***********  得到widget  *******/
        RelativeLayout rlOrderDetail = (RelativeLayout) grid.findViewById(R.id.gridview_delivery_waiting_todo_head);

        TextView tvUserName = (TextView) grid.findViewById(R.id.tv_gridview_delivery_waiting_todo_item_delivery_user_name);
        TextView tvUserMobile = (TextView) grid.findViewById(R.id.tv_gridview_delivery_waiting_todo_item_delivery_user_mobile);
        TextView tvLeftTime = (TextView) grid.findViewById(R.id.tv_gridview_delivery_waiting_todo_item_delivery_left_time);

        TextView tvBuildingDetailAddrss = (TextView) grid.findViewById(R.id.tv_gridview_delivery_waiting_todo_item_delivery_building_address);

        TextView tvOrderContent = (TextView) grid.findViewById(R.id.tv_gridview_delivery_waiting_todo_item_delivery_order_content);

        Button buttonCancel = (Button) grid.findViewById(R.id.button_gridview_delivery_waiting_todo_item_cancel_order);
        Button buttonPrepare = (Button) grid.findViewById(R.id.button_gridview_delivery_waiting_todo_item_prepare_order);

        /***********  widget设置  *******/
        final OrderDetailInfo odi = listODI.get(position);
        tvBuildingDetailAddrss.setText(odi.store_order.store_order_delivery.delivery_building_name);


        rlOrderDetail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.sendMsg("获取订单详情", DeliveryActivity.SHOW_LOADING_TEXT, handler);

                ApisManager.getOrderDetailInfoByOrderId(odi.order_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        OrderDetailInfo odi = new OrderDetailInfo();
                        com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) object;
                        com.alibaba.fastjson.JSONObject objSO = obj.getJSONObject("store_order");
                        StoreOrder so = com.alibaba.fastjson.JSONObject.parseObject(objSO.toString(), StoreOrder.class);
                        odi.store_order = so;
                        odi.take_serial_number = so.take_serial_number;
                        odi.list_charge_items_all = so.order_items;

                        Message msg = new Message();
                        msg.what = DeliveryActivity.SHOW_ORDER_DETAIL_INFO;
                        msg.obj = odi;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg(response.error_message, DeliveryActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });


            }
        });

        tvUserName.setText(odi.store_order.user.name);
        tvUserMobile.setText(odi.store_order.user.mobile);
        tvLeftTime.setText(CommonUtils.getMinutesLeft(odi.store_order.store_order_delivery.delivery_assign_time));

        tvBuildingDetailAddrss.setText(odi.store_order.store_order_delivery.delivery_building_name + "  " +
                odi.store_order.store_order_delivery.delivery_building_address);

        tvOrderContent.setText(odi.orderContent);

        buttonPrepare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.sendMsg("订单备餐中", DeliveryActivity.SHOW_LOADING_TEXT, handler);

                List<String> list = new ArrayList<String>();
                list.add(odi.order_id);
                setDeliveryOrderDoing(list, position);
            }
        });

        buttonCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Message msg = new Message();
                msg.what = DeliveryActivity.SHOW_ERROR_MESSAGE_CANCEL;
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("errorMsg", "请在取消订单之前，与用户进行沟通，避免不必要的纠纷");
                map.put("odi", odi);
                map.put("refund_status", 2);
                msg.obj = map;
                handler.sendMessage(msg);
            }
        });

        return grid;
    }


    public View getDoing(int position) {

        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.gridview_delivery_doing, null);

        /***********  得到widget  *******/
        RelativeLayout rlDoingHead = (RelativeLayout) grid.findViewById(R.id.gridview_delivery_doing_head);
        TextView tvSerial = (TextView) grid.findViewById(R.id.tv_gridview_delivery_doing_serinal);
        TextView tvLeftTime = (TextView) grid.findViewById(R.id.tv_gridview_delivery_doing_item_delivery_left_time);

        TextView tvBuildingAddress = (TextView) grid.findViewById(R.id.tv_gridview_delivery_doing_item_delivery_building_address);

        TextView tvOrderContent = (TextView) grid.findViewById(R.id.tv_gridview_delivery_doing_item_delivery_order_content);

        Button buttonCancel = (Button) grid.findViewById(R.id.button_gridview_delivery_doing_item_cancel_order);

        /***********  widget设置  *******/
        final OrderDetailInfo odi = listODI.get(position);

        rlDoingHead.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.sendMsg("获取订单详情", DeliveryActivity.SHOW_LOADING_TEXT, handler);
                ApisManager.getOrderDetailInfoByOrderId(odi.order_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        OrderDetailInfo odi = new OrderDetailInfo();
                        com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) object;
                        com.alibaba.fastjson.JSONObject objSO = obj.getJSONObject("store_order");
                        StoreOrder so = com.alibaba.fastjson.JSONObject.parseObject(objSO.toString(), StoreOrder.class);
                        odi.store_order = so;
                        odi.take_serial_number = so.take_serial_number;
                        odi.list_charge_items_all = so.order_items;

                        Message msg = new Message();
                        msg.what = DeliveryActivity.SHOW_ORDER_DETAIL_INFO;
                        msg.obj = odi;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg(response.error_message, DeliveryActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });
            }
        });

        tvSerial.setText(odi.take_serial_number + "");
        tvLeftTime.setText(CommonUtils.getMinutesLeft(odi.store_order.store_order_delivery.delivery_assign_time));

        tvBuildingAddress.setText(odi.store_order.store_order_delivery.delivery_building_name + "  " + odi.store_order.store_order_delivery.delivery_building_address);

        tvOrderContent.setText(odi.orderContent + "");


        buttonCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Message msg = new Message();
                msg.what = DeliveryActivity.SHOW_ERROR_MESSAGE_CANCEL;
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("errorMsg", "请在取消订单之前，与用户进行沟通，避免不必要的纠纷");
                map.put("odi", odi);
                map.put("refund_status", 2);
                msg.obj = map;
                handler.sendMessage(msg);

            }
        });

        return grid;
    }


    public View getWaitingDelivery(final int position) {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.gridview_delivery_waint_delivery_search, null);

        /***********  得到widget  *******/

        RelativeLayout rlWaitingDeliveryHead = (RelativeLayout) grid.findViewById(R.id.gridview_delivery_waiting_delivery_search_head);

        TextView tvSerinal = (TextView) grid.findViewById(R.id.tv_gridview_delivery_waiting_delivery_search_serinal);
        TextView tvLeftTime = (TextView) grid.findViewById(R.id.tv_gridview_delivery_waiting_delivery_item_delivery_search_left_time);

        TextView tvBuildingAddress = (TextView) grid.findViewById(R.id.tv_gridview_delivery_waiting_delivery_item_delivery_search_building_address);
        TextView tvOrderContent = (TextView) grid.findViewById(R.id.tv_gridview_delivery_waiting_delivery_item_delivery_search_order_content);

        Button buttonSendSingle = (Button) grid.findViewById(R.id.button_gridview_delivery_waiting_delivery_item_delivery_search_order_single);


        /***********  widget设置  *******/

        final OrderDetailInfo odi = listODI.get(position);

        rlWaitingDeliveryHead.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.sendMsg("获取订单详情", DeliveryActivity.SHOW_LOADING_TEXT, handler);
                ApisManager.getOrderDetailInfoByOrderId(odi.order_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        OrderDetailInfo odi = new OrderDetailInfo();
                        com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) object;
                        com.alibaba.fastjson.JSONObject objSO = obj.getJSONObject("store_order");
                        StoreOrder so = com.alibaba.fastjson.JSONObject.parseObject(objSO.toString(), StoreOrder.class);
                        odi.store_order = so;
                        odi.take_serial_number = so.take_serial_number;
                        odi.list_charge_items_all = so.order_items;

                        Message msg = new Message();
                        msg.what = DeliveryActivity.SHOW_ORDER_DETAIL_INFO;
                        msg.obj = odi;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg(response.error_message, DeliveryActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });


            }
        });

        tvSerinal.setText("" + odi.store_order.take_serial_number);
        tvLeftTime.setText(CommonUtils.getMinutesLeft(odi.store_order.store_order_delivery.delivery_assign_time));

        tvBuildingAddress.setText(odi.store_order.store_order_delivery.delivery_building_name + " " + odi.store_order.store_order_delivery.delivery_building_address);
        tvOrderContent.setText(odi.orderContent);


        buttonSendSingle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                DeliveryActivity.flagSingDelivery = true;
                DeliveryActivity.odiSingleDelivery = odi;
                ApisManager.getDeliveryStaffs(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);

                        Map map = new HashMap<String, Object>();
                        map.put("orderId", odi.order_id);
                        map.put("listStaff", (List<DeliveryStaff>) object);

                        Message msg = new Message();
                        msg.obj = map;
                        msg.what = DeliveryActivity.SHOW_SATFF_LIST;
                        handler.sendMessage(msg);

                        CommonUtils.sendMsg(null, DeliveryActivity.NOTIFY_DELIVERYING, handler);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg("", DeliveryActivity.SHOW_ERROR_MESSAGE, handler);

                    }
                });
            }
        });

        return grid;
    }


    public View getDeliverying(final int position) {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.gridview_delivery_delirying, null);

        RelativeLayout rlHead = (RelativeLayout) grid.findViewById(R.id.gridview_delivery_deliverying_head);

        TextView tvSerinal = (TextView) grid.findViewById(R.id.tv_gridview_delivery_deliverying_serinal);
        TextView tvLeftTime = (TextView) grid.findViewById(R.id.tv_gridview_delivery_deliverying_item_delivery_left_time);
        TextView tvBuildingAddress = (TextView) grid.findViewById(R.id.tv_gridview_delivery_deliverying_item_delivery_building_address);
        TextView tvUserInfo = (TextView) grid.findViewById(R.id.tv_gridview_delivery_deliverying_item_delivery_send_info);
        TextView tvOrderContent = (TextView) grid.findViewById(R.id.tv_gridview_delivery_deliverying_item_delivery_order_content);

        Button button_marked_deliveried = (Button) grid.findViewById(R.id.button_gridview_deliverying_item_mark_deliveried);
        Button button_deliveriy_again = (Button) grid.findViewById(R.id.button_gridview_deliverying_item_delivery_again);


        final OrderDetailInfo odi = listODI.get(position);

        rlHead.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.sendMsg("获取订单详情", DeliveryActivity.SHOW_LOADING_TEXT, handler);

                ApisManager.getOrderDetailInfoByOrderId(odi.order_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        OrderDetailInfo odi = new OrderDetailInfo();
                        com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) object;
                        com.alibaba.fastjson.JSONObject objSO = obj.getJSONObject("store_order");
                        StoreOrder so = com.alibaba.fastjson.JSONObject.parseObject(objSO.toString(), StoreOrder.class);
                        odi.store_order = so;
                        odi.take_serial_number = so.take_serial_number;
                        odi.list_charge_items_all = so.order_items;

                        Message msg = new Message();
                        msg.what = DeliveryActivity.SHOW_ORDER_DETAIL_INFO;
                        msg.obj = odi;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg(response.error_message, DeliveryActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });
            }
        });
        tvSerinal.setText("" + odi.store_order.take_serial_number);
        tvLeftTime.setText(CommonUtils.getMinutesLeft(odi.store_order.store_order_delivery.delivery_assign_time));

        tvBuildingAddress.setText(odi.store_order.store_order_delivery.delivery_building_name + "  " + odi.store_order.store_order_delivery.delivery_building_address);

        if (odi.store_order.store_order_delivery.delivery_staff != null) {
            tvUserInfo.setText(odi.store_order.store_order_delivery.delivery_staff.name + "(" + CommonUtils.getMinDif(odi.store_order.store_order_delivery.delivery_start_time) + "分钟前)");
        }
        tvOrderContent.setText(odi.orderContent);


        button_marked_deliveried.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                CommonUtils.sendMsg(odi.order_id, DeliveryActivity.SHOW_ERROR_MESSAGE_DELIVERY_MARKED, handler);
            }
        });

        button_deliveriy_again.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //获取员工列表
                CommonUtils.sendMsg("获取职员列表", DeliveryActivity.SHOW_LOADING_TEXT, handler);
                ApisManager.getDeliveryStaffs(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);

                        Map map = new HashMap<String, Object>();
                        map.put("orderId", odi.order_id);
                        map.put("listStaff", (List<DeliveryStaff>) object);

                        Message msg = new Message();
                        msg.obj = map;
                        msg.what = DeliveryActivity.SHOW_SATFF_LIST;
                        handler.sendMessage(msg);

                        CommonUtils.sendMsg(null, DeliveryActivity.NOTIFY_DELIVERYING, handler);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg("", DeliveryActivity.SHOW_ERROR_MESSAGE, handler);

                    }
                });
                DeliveryActivity.flagSingDelivery = true;
                DeliveryActivity.odiSingleDelivery = odi;
            }
        });


        return grid;
    }


    public View getDeliveried(final int position) {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.gridview_delivery_done, null);

        RelativeLayout rlHead = (RelativeLayout) grid.findViewById(R.id.gridview_delivery_done_head);

        TextView tvSerial = (TextView) grid.findViewById(R.id.tv_gridview_delivery_done_serinal);
        TextView tvUserInfo = (TextView) grid.findViewById(R.id.tv_gridview_delivery_done_item_delivery_send_info);


        TextView tvBuildingAddress = (TextView) grid.findViewById(R.id.tv_gridview_delivery_done_item_delivery_building_address);
        TextView tvOrderContent = (TextView) grid.findViewById(R.id.tv_gridview_delivery_done_item_delivery_order_content);


        final OrderDetailInfo odi = listODI.get(position);

        rlHead.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.sendMsg("获取订单详情", DeliveryActivity.SHOW_LOADING_TEXT, handler);
                ApisManager.getOrderDetailInfoByOrderId(odi.order_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        OrderDetailInfo odi = new OrderDetailInfo();
                        com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) object;
                        com.alibaba.fastjson.JSONObject objSO = obj.getJSONObject("store_order");
                        StoreOrder so = com.alibaba.fastjson.JSONObject.parseObject(objSO.toString(), StoreOrder.class);
                        odi.store_order = so;
                        odi.take_serial_number = so.take_serial_number;
                        odi.list_charge_items_all = so.order_items;

                        Message msg = new Message();
                        msg.what = DeliveryActivity.SHOW_ORDER_DETAIL_INFO;
                        msg.obj = odi;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg(response.error_message, DeliveryActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });

            }
        });

        tvSerial.setText(odi.store_order.take_serial_number + "");
        tvUserInfo.setText(odi.store_order.store_order_delivery.delivery_staff.name + "("
                + CommonUtils.getHourMin(odi.store_order.store_order_delivery.delivery_finish_time) + "送达)");

        tvBuildingAddress.setText(odi.store_order.store_order_delivery.delivery_building_name + " " + odi.store_order.store_order_delivery.delivery_building_address);
        tvOrderContent.setText(odi.orderContent);


        return grid;
    }


    /***
     * 设置订单正在备餐
     */
    public void setDeliveryOrderDoing(List<String> list, final int position) {
        ApisManager.setDeliveryOrderPreparing(list, new ApiCallback() {
            @Override
            public void success(Object object) {
                CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                ApisManager.getDeliveryOrderAnalysisInfo(new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        Amount amount = (Amount) object;
                        Message msg = new Message();
                        msg.what = DeliveryActivity.UPDATE_ACTION_BAR_BADGET;
                        msg.obj = amount;
                        handler.sendMessage(msg);
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                    }
                });

                ApisManager.getDeliveryListInfoByMobile(1, DeliveryActivity.mobile, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        CommonUtils.LogWuwei(DeliveryActivity.tag, "object is " + object.toString());
                        List<OrderDetailInfo> list = (List<OrderDetailInfo>) object;
                        Message msg = new Message();
                        Map map = new HashMap<String, Object>();
                        map.put("listODI", list);
                        map.put("mobile", mobile);
                        msg.obj = map;
                        msg.what = DeliveryActivity.SHOW_GET_DELIVERY_ORDER_SEARCH_RESULT;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void error(BaseApi.ApiResponse response) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        CommonUtils.sendMsg(response.error_message, DeliveryActivity.SHOW_ERROR_MESSAGE, handler);
                    }
                });
            }

            @Override
            public void error(BaseApi.ApiResponse response) {
                CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                CommonUtils.sendMsg(response.error_message, DeliveryActivity.SHOW_ERROR_MESSAGE, handler);
            }
        });
    }

}
