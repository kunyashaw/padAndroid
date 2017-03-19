package com.huofu.RestaurantOS.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.bean.SettingTitleComponent;
import com.huofu.RestaurantOS.ui.funcSplash.SettingsActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;

import java.util.List;


/****
 * 设置页面左侧列白数据对应的适配器
 */

public class ExpandableListviewSettingAdapter extends BaseExpandableListAdapter {

    List<List<SettingTitleComponent>> childList = null;
    List<String> groupList = null;
    Context ctxt = null;
    String tag = SettingsActivity.tag;
    Handler handler;
    public static long PERMISSION_DELIVERY_SET = 150001;
    FragmentManager fm;


    public ExpandableListviewSettingAdapter(FragmentManager fm, List<List<SettingTitleComponent>> childList, List<String> groupList,
                                            Context ctxt, Handler handler) {
        this.fm = fm;
        this.childList = childList;
        this.groupList = groupList;
        this.ctxt = ctxt;
        this.handler = handler;

        CommonUtils.LogWuwei(tag, "childList size is " + childList.size() + "groupList size is" + groupList.size());

        for (int i = 0; i < getGroupCount(); i++) {
            for (int k = 0; k < getChildrenCount(i); k++) {
                SettingTitleComponent stc = (SettingTitleComponent) getChild(i, k);
                CommonUtils.LogWuwei(tag, "child is " + stc.title);
            }
        }

    }

    public void initFlagSelected() {
        for (int t = 0; t < childList.size(); t++) {
            for (int k = 0; k < childList.get(t).size(); k++) {
                childList.get(t).get(k).flagSelected = false;
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
        return childList.get(groupPosition).size();
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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        TextView tv = new TextView(ctxt);
        tv.setHeight(48);
        //tv.setText(groupList.get(groupPosition));
        tv.setBackgroundResource(android.R.color.transparent);
        tv.setClickable(false);

        tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });

        return tv;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub


        final SettingTitleComponent stc = childList.get(groupPosition).get(childPosition);


        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View grid = inflater.inflate(R.layout.relativelayout_setting_title, null);


        ImageView ivIcon = (ImageView) grid.findViewById(R.id.imageview_setting_title_icon);
        TextView tvTitle = (TextView) grid.findViewById(R.id.textview_setting_title_text);
        TextView tvHint = (TextView) grid.findViewById(R.id.textview_setting_title_hint);

        ivIcon.setBackgroundResource(stc.icon_id);
        tvTitle.setText(stc.title);
        tvHint.setText(stc.title_hint);


        if (stc.flagSelected) {
            grid.setBackgroundColor(ctxt.getResources().getColor(R.color.Blue));
        } else {
            grid.setBackgroundColor(ctxt.getResources().getColor(R.color.Constrast));
        }
        return grid;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }


}
