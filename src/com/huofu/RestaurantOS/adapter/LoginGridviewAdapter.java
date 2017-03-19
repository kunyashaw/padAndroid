package com.huofu.RestaurantOS.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.user.LoginStaff;
import com.huofu.RestaurantOS.support.shapedImage.CircleImageView;
import com.huofu.RestaurantOS.ui.login.LoginActivity;

import java.util.List;

/****
 * login时员工列表对应的适配器
 */

public class LoginGridviewAdapter extends BaseAdapter {

    public static int selectPic = -1;
    private Context context = null;
    private List<LoginStaff> list = null;
    private String tag = "adapter";

    public LoginGridviewAdapter(Context ctxt, List<LoginStaff> ls) {
        context = ctxt;
        list = ls;
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.grid_item, null);

        grid = inflater.inflate(R.layout.grid_item, null);
        CircleImageView cv = (CircleImageView) grid.findViewById(R.id.button_gridview_item);
        String imgUrl = list.get(position).head;
        if (imgUrl.equals("") || imgUrl == null) {
            cv.setBackgroundResource(R.drawable.default_head_pic);
            if (LoginActivity.listLoginStaff.get(position).flagStaffChoosen) {
                cv.setImageResource(R.drawable.head_photo_overlay);
            }
        } else {

            if (LoginActivity.listLoginStaff.get(position).flagStaffChoosen) {
                LoginActivity.bitmapUtils.display(cv, imgUrl, LoginActivity.bigPicDisplayConfig, LoginActivity.callbackSetOverLay);
            } else {
                LoginActivity.bitmapUtils.display(cv, imgUrl, LoginActivity.bigPicDisplayConfig, null);
            }
        }

        TextView tv = (TextView) grid.findViewById(R.id.tv_login_staff_name);
        tv.setText(list.get(position).name);

        return grid;


    }


}
