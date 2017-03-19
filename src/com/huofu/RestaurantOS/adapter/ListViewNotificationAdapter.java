package com.huofu.RestaurantOS.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.widget.SimpleSideDrawer;

/**
 * author: Created by zzl on 15/9/23.
 */
public class ListViewNotificationAdapter extends BaseAdapter{

    public Context ctxt;
    public LayoutInflater inflater;

    public ListViewNotificationAdapter(Context ctxt)
    {
        this.ctxt = ctxt;
        inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return MainApplication.notificationList.size();
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
        return MainApplication.notificationList.get(position);
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
        //return null;

        if(MainApplication.notificationList.size() == 0)
        {
            SimpleSideDrawer.rlNotifactions.setVisibility(View.INVISIBLE);
        }
        else
        {
            SimpleSideDrawer.rlNotifactions.setVisibility(View.VISIBLE);
        }


        View grid = inflater.inflate(R.layout.notification,null);
        ImageButton closeButton = (ImageButton)grid.findViewById(R.id.notification_imagebutton_closed);
        View.OnClickListener oclClose = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    MainApplication.LastDealTimeList[MainApplication.notificationList.get(position).type]=System.currentTimeMillis()/1000;
                    MainApplication.notificationList.remove(position);
                    SimpleSideDrawer.adapter.notifyDataSetChanged();
                }
                catch(Exception e)
                {
                    CommonUtils.LogWuwei("","shit in notificationList closeButton clock:"+e.getMessage());
                }

            }
        };
        closeButton.setOnClickListener(oclClose);
        grid.findViewById(R.id.notification_rl_closed).setOnClickListener(oclClose);

        ImageView iv = (ImageView)grid.findViewById(R.id.imageview_notication_icon);
        iv.setOnClickListener(MainApplication.notificationList.get(position).ocl);

        TextView tv= (TextView)grid.findViewById(R.id.tv_notification_tips);
        tv.setText(MainApplication.notificationList.get(position).tips);

        return  grid;
    }
}
