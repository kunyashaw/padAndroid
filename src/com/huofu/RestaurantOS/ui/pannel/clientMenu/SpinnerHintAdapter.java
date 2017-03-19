package com.huofu.RestaurantOS.ui.pannel.clientMenu;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;



public class SpinnerHintAdapter extends ArrayAdapter<String>{


	public SpinnerHintAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count = super.getCount();
		return count > 0 ? count-1:count;
	}
	

}
