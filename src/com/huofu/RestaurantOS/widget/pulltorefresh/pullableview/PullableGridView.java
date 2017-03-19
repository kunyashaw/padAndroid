package com.huofu.RestaurantOS.widget.pulltorefresh.pullableview;

import com.huofu.RestaurantOS.utils.CommonUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class PullableGridView extends GridView implements Pullable
{
	
	public String tag = "PullableGridView";

	public PullableGridView(Context context)
	{
		super(context);
	}

	public PullableGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown()
	{
		if (getCount() == 0)
		{
			// 没有item的时候也可以下拉刷新
			return true;
		}
		else if (getFirstVisiblePosition() == 0&& getChildAt(0).getTop() >= 0)
		{
			CommonUtils.LogWuwei(tag, "PullableGridView 滑到顶部了");
			
			// 滑到顶部了
			return true;
		} 
		else
			return false;
	}

	@Override
	public boolean canPullUp()
	{
		if (getCount() == 0)
		{
			// 没有item的时候也可以上拉加载
			return true;
		}
		else if (getLastVisiblePosition() == (getCount() - 1))
		{
			// 滑到底部了
			if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
					&& getChildAt(getLastVisiblePosition()- getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
			{
				CommonUtils.LogWuwei(tag, "PullableGridView 滑到底部了");
				return true; //由于不需要下拉加载更多 就删除了
				
			}
				
		}
		return false;
	}

}
