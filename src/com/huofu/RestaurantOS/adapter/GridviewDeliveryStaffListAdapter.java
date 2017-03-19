package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;;
import com.huofu.RestaurantOS.bean.user.DeliveryStaff;
import com.huofu.RestaurantOS.support.shapedImage.CircleImageView;
import com.huofu.RestaurantOS.ui.pannel.delivery.DeliveryActivity;

import java.util.List;

/****
 * 外送中选择派送员时派送员列表对应的适配器
 */

public class GridviewDeliveryStaffListAdapter extends BaseAdapter {

    public static int selectPic = -1;
    private Context context = null;
    private List<DeliveryStaff> list = null;
    private String tag = "adapter";
    LayoutInflater inflater;

    public GridviewDeliveryStaffListAdapter(Context ctxt, List<DeliveryStaff> ls) {
        context = ctxt;
        list = ls;
        inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    public void setNotifyDataChange(int id) {
        selectPic = id;
        super.notifyDataSetChanged();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        View grid = inflater.inflate(R.layout.grid_item, null);
        CircleImageView cv = (CircleImageView) grid.findViewById(R.id.button_gridview_item);

        String imgUrl = list.get(position).head;
        if (imgUrl.equals("") || imgUrl == null) {
            cv.setBackgroundResource(R.drawable.default_head_pic);
            /*if (DeliveryActivity.listStaff.get(position).flagStaffChoosen) {
                cv.setImageResource(R.drawable.head_photo_overlay);
            }*/
        } else {
           /* if (DeliveryActivity.listStaff.get(position).flagStaffChoosen) {
                DeliveryActivity.bitmapUtils.display(cv, imgUrl, DeliveryActivity.bigPicDisplayConfig, DeliveryActivity.callbackSetOverLay);
            } else */{
                DeliveryActivity.bitmapUtils.display(cv, imgUrl, DeliveryActivity.bigPicDisplayConfig, null);
            }
        }

        TextView tv = (TextView) grid.findViewById(R.id.tv_login_staff_name);
        tv.setText(list.get(position).name);

        return grid;
    }


}

