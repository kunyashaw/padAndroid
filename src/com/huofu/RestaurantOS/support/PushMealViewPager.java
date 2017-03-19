package com.huofu.RestaurantOS.support;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * author: Created by zzl on 15/12/1.
 */
public class PushMealViewPager extends ViewPager{

    private ViewGroup parent;
    public boolean flagCanScroll = false;
    private boolean enabled = true;

    public PushMealViewPager(Context context) {
        super(context);
        this.enabled = true;
    }

    public PushMealViewPager(Context context,AttributeSet attrs)
    {
        super(context,attrs);
        this.enabled = true;
    }


    public void setNestedpParent(ViewGroup parent) {
        this.parent = parent;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return this.enabled && super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return this.enabled && super.onTouchEvent(arg0);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    public void setPagingEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
