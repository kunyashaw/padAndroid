package com.huofu.RestaurantOS.ui.pannel.clientMenu;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;


public class CustomScrollView extends ScrollView{
   
	
	public CustomScrollView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	
	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}


	public CustomScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	// true if we can scroll the ScrollView
    // false if we cannot scroll 
    private boolean scrollable = true;

    public void setScrollingEnabled(boolean scrollable_flag) {
        scrollable = scrollable_flag;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (scrollable) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return scrollable; // scrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if 
        // we are not scrollable
        if (!scrollable) return false;
        else return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    		// TODO Auto-generated method stub
    		
    		super.onSizeChanged(w, h, oldw, oldh);
    		/*if(NewFromHistoryMenuActivity.button_new_choosen != null)
    		{
    			CommonUtils.LogWuwei("onSizeChanged", "h is "+h+" oldh is "+oldh+"now modified content is "+NewFromHistoryMenuActivity.button_new_choosen.getText().toString());
    		}
    		else
    		{
    			CommonUtils.LogWuwei("onSizeChanged", "h is "+h+" oldh is "+oldh);
    		}*/
    		
    		if(oldh != 0)
    		{
    		//	MsgUtils.SendSingleMsg(NewFromHistoryMenuActivity.handler, "", NewFromHistoryMenuActivity.RELOAD_MOBILE_UI);
    		}
    		
    		
    	/*	if(w>oldw)
    		{
    				
    		}*/
    		    			
    }
    
    @Override
    protected int computeVerticalScrollOffset() {
    	// TODO Auto-generated method stub
    	//	CommonUtils.LogWuwei("CustomedScrollView","offset is "+super.computeVerticalScrollOffset());
    		//NewFromHistoryMenuActivity.scrollBarOffset = super.computeVerticalScrollOffset();
    		return super.computeVerticalScrollOffset();
    }
   
}
