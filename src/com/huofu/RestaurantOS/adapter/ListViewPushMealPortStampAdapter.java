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
import com.huofu.RestaurantOS.fragment.settings.MealPort.FragmentMealPortSettingManager;

import java.util.List;

/**
 * author: Created by zzl on 15/10/17.
 */
public class ListViewPushMealPortStampAdapter extends BaseAdapter{

    public List<String> list;
    public Context ctxt;
    public Handler handler;
    public ListViewPushMealPortStampAdapter adapter;

    public ListViewPushMealPortStampAdapter(Context ctxt, Handler handler, List<String> list)
    {
        this.ctxt = ctxt;
        this.handler = handler;
        this.list = list;
    }

    public void updateAdapter(ListViewPushMealPortStampAdapter adapter)
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
        View grid = inflater.inflate(R.layout.setting_item_checkbox_standard,null);

        RelativeLayout rl = (RelativeLayout)grid.findViewById(R.id.rl_setting_layout_checkbox_standard_item);
        TextView tvName = (TextView)grid.findViewById(R.id.tv_setting_layout_checkbox_standard_item_name);
        final ImageView iv = (ImageView)grid.findViewById(R.id.imageview_setting_layout_checkbox_choose);

        if(FragmentMealPortSettingManager.letter.equals(list.get(position)))
        {
            iv.setImageResource(R.drawable.checkbox2);
        }
        else
        {
            iv.setImageResource(android.R.color.transparent);
        }

        tvName.setText(list.get(position));
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv.setImageResource(R.drawable.checkbox2);
                FragmentMealPortSettingManager.letter = list.get(position);
                adapter.notifyDataSetChanged();
            }
        });

        return grid;
    }
}
