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

import com.alibaba.fastjson.JSONObject;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.storeOrder.OrderDetailInfo;
import com.huofu.RestaurantOS.bean.storeOrder.StoreOrder;
import com.huofu.RestaurantOS.ui.pannel.delivery.DeliveryActivity;
import com.huofu.RestaurantOS.ui.pannel.delivery.ViewHolderDoing;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****
 * 外送正在备餐列表的适配器
 */
public class GridviewDeliveryGetDoing extends BaseAdapter {


    List<OrderDetailInfo> listODI = null;
    Context ctxt;
    Handler handler;

    public GridviewDeliveryGetDoing(List<OrderDetailInfo> ls, Context ctxt, Handler handler) {
        this.listODI = ls;
        this.ctxt = ctxt;
        this.handler = handler;
    }


    public void add(List<OrderDetailInfo> ls) {
        CommonUtils.LogWuwei(DeliveryActivity.tag, "before 正在备餐ls.size is " + ls.size());
        ;
        if (ls != null) {
            for (int k = 0; k < ls.size(); k++) {
                boolean flagAdd = true;
                for (int t = 0; t < listODI.size(); t++) {
                    if (listODI.get(t).order_id.equals(ls.get(k).order_id)) {
                        flagAdd = false;
                        break;
                    }
                }
                if (flagAdd) {
                    listODI.add(ls.get(k));
                }
            }

            for (int k = 0; k < listODI.size(); k++) {
                boolean flagRemove = true;
                for (int t = 0; t < ls.size(); t++) {
                    if (listODI.get(k).order_id.equals(ls.get(t).order_id)) {
                        flagRemove = false;
                        break;
                    }
                }
                if (flagRemove) {
                    listODI.remove(k);
                }
            }
            CommonUtils.sendMsg(null, DeliveryActivity.NOTIFY_DELIVERY_DOING, handler);
        }
        CommonUtils.LogWuwei(DeliveryActivity.tag, "after 正在备餐ls.size is " + ls.size());
        ;
    }

    public boolean whetherDiff(List<OrderDetailInfo> ls) {

        if (ls != null) {
            if (ls.size() != listODI.size()) {
                return true;
            }
        }

        for (int k = 0; k < listODI.size(); k++) {
            String order_id = listODI.get(k).order_id;

            //假设为false，如果遍历时又相同的，则继续；如果遍历一遍都没有相同的，那么说明ls、listODI是不同的队列，这是否返回true
            boolean flagSame = false;

            for (int t = 0; t < ls.size(); t++) {
                String new_order_id = ls.get(t).order_id;
                if (order_id.equals(new_order_id)) {
                    flagSame = true;
                    break;
                }
            }
            if (!flagSame) {
                return true;
            }
        }

        return false;
    }

    public void clear() {
        listODI.clear();
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


        ViewHolderDoing holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview_delivery_doing, null);

            holder = new ViewHolderDoing();

            /***********  得到widget  *******/
            holder.rlDoingHead = (RelativeLayout) convertView.findViewById(R.id.gridview_delivery_doing_head);
            holder.tvSerial = (TextView) convertView.findViewById(R.id.tv_gridview_delivery_doing_serinal);
            holder.tvLeftTime = (TextView) convertView.findViewById(R.id.tv_gridview_delivery_doing_item_delivery_left_time);

            holder.tvBuildingAddress = (TextView) convertView.findViewById(R.id.tv_gridview_delivery_doing_item_delivery_building_address);

            holder.tvOrderContent = (TextView) convertView.findViewById(R.id.tv_gridview_delivery_doing_item_delivery_order_content);

            holder.buttonCancel = (Button) convertView.findViewById(R.id.button_gridview_delivery_doing_item_cancel_order);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolderDoing) convertView.getTag();
        }


        /***********  widget设置  *******/
        final OrderDetailInfo odi = listODI.get(position);

        holder.rlDoingHead.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommonUtils.sendMsg("获取订单详情", DeliveryActivity.SHOW_LOADING_TEXT, handler);

                ApisManager.getOrderDetailInfoByOrderId(odi.order_id, new ApiCallback() {
                    @Override
                    public void success(Object object) {
                        CommonUtils.sendMsg("", DeliveryActivity.HIDE_LOADING, handler);
                        OrderDetailInfo odi = new OrderDetailInfo();
                        JSONObject obj = (JSONObject) object;
                        JSONObject objSO = obj.getJSONObject("store_order");
                        StoreOrder so = com.alibaba.fastjson.JSONObject.parseObject(CommonUtils.converBooleanToInt(objSO.toString()), StoreOrder.class);
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

        holder.tvSerial.setText(odi.take_serial_number + "");
        holder.tvLeftTime.setText(CommonUtils.getMinutesLeft(odi.store_order.store_order_delivery.delivery_assign_time));

        holder.tvBuildingAddress.setText(odi.store_order.store_order_delivery.delivery_building_name + "  " + odi.store_order.store_order_delivery.delivery_building_address);

        holder.tvOrderContent.setText(odi.orderContent + "");


        holder.buttonCancel.setOnClickListener(new OnClickListener() {

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

        return convertView;
    }


}
