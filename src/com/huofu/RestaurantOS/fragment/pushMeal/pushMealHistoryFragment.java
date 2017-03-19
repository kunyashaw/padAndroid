package com.huofu.RestaurantOS.fragment.pushMeal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.adapter.GridviewPushMealHistoryAdapter;
import com.huofu.RestaurantOS.api.BaseApi;
import com.huofu.RestaurantOS.api.request.ApiCallback;
import com.huofu.RestaurantOS.bean.pushMeal.PushHistoryFood;
import com.huofu.RestaurantOS.manager.ApisManager;
import com.huofu.RestaurantOS.ui.pannel.pushMeal.PushMealActivity;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;
import com.huofu.RestaurantOS.utils.InputMethodUtils;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/11/19.
 */
@SuppressLint("validFragment")
public class pushMealHistoryFragment extends Fragment {

    String tag = PushMealActivity.tag;
    Context ctxt;
    View gridALl;
    Handler handler;
    List<PushHistoryFood> list_all_history = new ArrayList<PushHistoryFood>();// 所有已经出餐的历史数据列表
    List<PushHistoryFood> list_all_history_save = new ArrayList<PushHistoryFood>();// 所有已经出餐的历史数据列表
    GridviewPushMealHistoryAdapter gridview_push_history_adapter = null;
    SearchView sv;

    public pushMealHistoryFragment(Context ctxt, Handler handler) {
        this.ctxt = ctxt;
        this.handler = handler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        gridALl = inflater.inflate(R.layout.fragment_push_meal_history, null);
        RelativeLayout rl = (RelativeLayout) gridALl.findViewById(R.id.rl_meal_done_sv_parent);

        sv = (SearchView) gridALl.findViewById(R.id.sv_meal_done_search_history);
        gridview_push_history_adapter = new GridviewPushMealHistoryAdapter(handler, ctxt, list_all_history);
        rl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int resId = getResources().getIdentifier("search_button", "id",
                        "android");// icon
                ImageView searchIcon = (ImageView) sv.findViewById(resId);
                searchIcon.performClick();
            }
        });

        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) sv.findViewById(id);
        textView.setTextColor(Color.parseColor("#363636"));
        textView.setTextSize(20);
        textView.setHintTextColor(Color.parseColor("#b1b1b1"));

        sv.onActionViewCollapsed();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                CommonUtils.LogWuwei(tag, "提交时要搜索的是：" + query);

                list_all_history.clear();

                for (int i = 0; i < list_all_history_save.size(); i++) {
                    if (query.equals(list_all_history_save.get(i).take_serial_number + "")) {
                        boolean flagNew = true;
                        for (int k = 0; k < list_all_history.size(); k++) {
                            if (list_all_history_save.get(i).take_serial_number == (list_all_history.get(k).take_serial_number) && list_all_history_save.get(i).take_serial_seq == (list_all_history.get(k).take_serial_seq)) {
                                flagNew = false;
                                break;
                            }
                        }
                        if (flagNew) {
                            list_all_history.add(list_all_history_save.get(i));
                        }

                    }
                }

                if (list_all_history.size() < 1) {
                    HandlerUtils.showToast(ctxt, "没有搜索到流水号为:" + query + "的订单");
                } else {
                    InputMethodUtils.HideKeyboard(sv);
                    InputMethodUtils.TimerHideKeyboard(sv);

                    sv.performClick();
                    gridview_push_history_adapter.notifyDataSetChanged();

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                if (newText.equals("")) {
                    list_all_history.clear();
                }
                return false;
            }
        });

        sv.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // TODO Auto-generated method stub
                CommonUtils.LogWuwei(tag, ".\n.  hehe the sc is closing...\n.");

                list_all_history.clear();

                for (int i = 0; i < list_all_history_save.size(); i++) {
                    list_all_history.add(list_all_history_save.get(i));
                }

                gridview_push_history_adapter.notifyDataSetChanged();
                InputMethodUtils.HideKeyboard(sv);
                InputMethodUtils.TimerHideKeyboard(sv);
                return false;
            }
        });

        sv.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                for (int i = 0; i < list_all_history.size(); i++)// 首先将历时记录信息保存一下
                {
                    boolean flagNew = true;
                    for (int k = 0; k < list_all_history_save.size(); k++) {
                        if ((list_all_history.get(i).take_serial_number)
                                == (list_all_history_save.get(k).take_serial_number) &&
                                list_all_history.get(i).take_serial_seq == list_all_history_save.get(k).take_serial_seq

                                ) {
                            flagNew = false;
                        }
                    }
                    if (flagNew) {
                        list_all_history_save.add(list_all_history.get(i));
                    }
                }

                list_all_history.clear();
            }
        });

        sv.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub

                CommonUtils.LogWuwei(tag, "sv.getQuery() is " + sv.getQuery());

                if (keyCode == event.KEYCODE_DEL) {
                    if (sv.getQuery().equals("")) {
                        CommonUtils.LogWuwei(tag, ".\n.  hehe the sc is closing...\n.");
                        list_all_history.clear();
                        for (int i = 0; i < list_all_history_save.size(); i++) {
                            list_all_history.add(list_all_history_save.get(i));
                        }

                        gridview_push_history_adapter.notifyDataSetChanged();
                    }
                    return true;
                } else {
                    return false;
                }

            }
        });

        sv.clearFocus();

        return gridALl;
    }

    /**
     * 获取出餐历史，更新界面
     */
    public void updateGridview() {
        final GridView gridviewHistory = (GridView) gridALl.findViewById(R.id.gridview_pushing_history);
        final Button btnBackground = (Button) gridALl.findViewById(R.id.empty_meal_done_history_gridview);

        if (LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt) == -1) {
            ApisManager.getMealPushedHistory(CommonUtils.getFormatDate(0), new ApiCallback() {
                @Override
                public void success(final Object object) {
                    CommonUtils.sendObjMsg("", PushMealActivity.HIDE_LOADING, handler);
                    viewUpdate(object,gridviewHistory,btnBackground);
                }

                @Override
                public void error(BaseApi.ApiResponse response) {
                    CommonUtils.sendObjMsg("", PushMealActivity.HIDE_LOADING, handler);
                    CommonUtils.sendObjMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handler);
                }
            });
        } else {
            ApisManager.getMealPushedHistory(LocalDataDeal.readFromLocalMealDoneChooseMealPortId(ctxt), CommonUtils.getFormatDate(0), new ApiCallback() {
                @Override
                public void success(final Object object) {
                    CommonUtils.sendObjMsg("", PushMealActivity.HIDE_LOADING, handler);
                    viewUpdate(object,gridviewHistory,btnBackground);
                }

                @Override
                public void error(BaseApi.ApiResponse response) {
                    CommonUtils.sendObjMsg("", PushMealActivity.HIDE_LOADING, handler);
                    CommonUtils.sendObjMsg(response.error_message, PushMealActivity.SHOW_ERROR_MESSAGE, handler);
                }
            });
        }
    }

    /**
     * 更新界面
     */
    public void viewUpdate(final Object object,final GridView gridviewHistory,final Button btnBackground)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                sv.onActionViewCollapsed();

                List<PushHistoryFood> list = (List<PushHistoryFood>) object;
                list_all_history.clear();
                list_all_history.addAll(list);
                GridviewPushMealHistoryAdapter adapter = new GridviewPushMealHistoryAdapter(handler, ctxt, list_all_history);
                gridviewHistory.setAdapter(adapter);
                if (list.size() == 0) {
                    btnBackground.setVisibility(View.VISIBLE);
                } else {
                    btnBackground.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}
