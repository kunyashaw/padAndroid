package com.huofu.RestaurantOS.fragment.settings.MealPort;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huofu.RestaurantOS.MainApplication;
import com.huofu.RestaurantOS.R;
import com.huofu.RestaurantOS.utils.LocalDataDeal;

/**
 * author: Created by zzl on 15/10/22.
 */
public class FragmentMealPortCallRule extends Fragment
{

    Context ctxt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctxt = MainApplication.getContext();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Button button_now_setting_content = (Button) getActivity().findViewById(R.id.button_action_bar_setting_detail);
        final Button button_now_setting_back = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_back);
        Button button_now_setting_edit_cancel = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_cancel);
        final Button button_now_setting_edit_save = (Button) getActivity().findViewById(R.id.actionbar_btn_setting_edit_save);

        button_now_setting_back.setVisibility(View.INVISIBLE);
        button_now_setting_content.setVisibility(View.INVISIBLE);
        button_now_setting_edit_save.setVisibility(View.INVISIBLE);
        button_now_setting_edit_cancel.setVisibility(View.VISIBLE);

        button_now_setting_content.setText("");
        button_now_setting_edit_save.setText("");
        button_now_setting_edit_cancel.setText("返回");

        button_now_setting_edit_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //FragmentMealPortSettingManager.showFramgemtMPAdd();
                backToLast();
            }
        });


        View grid = inflater.inflate(R.layout.setting_mp_call_rule_list,null);
        RelativeLayout rlAuto = (RelativeLayout)grid.findViewById(R.id.rl_setting_call_rule_auto_call);
        RelativeLayout rlManual = (RelativeLayout)grid.findViewById(R.id.rl_setting_call_rule_manual_call);
        RelativeLayout rlLast = (RelativeLayout)grid.findViewById(R.id.rl_setting_call_rule_last_call);

        final ImageView ivAuto = (ImageView)grid.findViewById(R.id.imageview_setting_mp_call_rule_auto);
        final ImageView ivManual = (ImageView)grid.findViewById(R.id.imageview_setting_mp_call_rule_manual);
        final ImageView ivLast = (ImageView)grid.findViewById(R.id.imageview_setting_mp_call_rule_last);

        TextView tvAuto = (TextView)grid.findViewById(R.id.textview_setting_mp_call_rule_auto);
        TextView tvManual = (TextView)grid.findViewById(R.id.textview_setting_mp_call_rule_manual);
        TextView tvLast= (TextView)grid.findViewById(R.id.textview_setting_mp_call_rule_last);

        SpannableString ss = new SpannableString("手动叫号(由出餐员手动选择出餐单进行叫号)");
        ss.setSpan(new ForegroundColorSpan(ctxt.getResources().getColor(R.color.Powerful)),
                0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvManual.setText(ss);

        SpannableString s = new SpannableString("自动叫号(出餐打印成功后，立即自动叫号)");
        s.setSpan(new ForegroundColorSpan(ctxt.getResources().getColor(R.color.Powerful)),
                0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvAuto.setText(s);



        SpannableString sss = new SpannableString("尾单自动叫号（每个订单在当前出餐口的最后一个产品出餐时自动叫号）");
        sss.setSpan(new ForegroundColorSpan(ctxt.getResources().getColor(R.color.Powerful)),
                0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLast.setText(sss);




        rlManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMealPortSettingManager.call_type = 2;
                ivAuto.setImageResource(android.R.color.transparent);
                ivManual.setImageResource(R.drawable.checkbox2);
                ivLast.setImageResource(android.R.color.transparent);
                LocalDataDeal.writeToAutoCall(0, ctxt);
                backToLast();

            }
        });

        rlAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMealPortSettingManager.call_type = 1;
                ivAuto.setImageResource(R.drawable.checkbox2);
                ivManual.setImageResource(android.R.color.transparent);
                ivLast.setImageResource(android.R.color.transparent);
                LocalDataDeal.writeToAutoCall(1, ctxt);
                backToLast();
            }
        });

        rlLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMealPortSettingManager.call_type = 3;
                ivAuto.setImageResource(android.R.color.transparent);
                ivManual.setImageResource(android.R.color.transparent);
                ivLast.setImageResource(R.drawable.checkbox2);
                LocalDataDeal.writeToAutoCall(2, ctxt);
                backToLast();
            }
        });

        /**
         * flag->0 出餐不叫号(手动)
         * flag->1 出餐叫号（自动）
         * flag->2 尾单叫号
         */
        int callRule = LocalDataDeal.readFromAutoCall(ctxt);

        ivAuto.setImageResource(android.R.color.transparent);
        ivManual.setImageResource(android.R.color.transparent);
        ivLast.setImageResource(android.R.color.transparent);

        switch (callRule)
        {
            case 0:
                ivManual.setImageResource(R.drawable.checkbox2);
                break;
            case 1:
                ivAuto.setImageResource(R.drawable.checkbox2);
                break;
            case 2:
                ivLast.setImageResource(R.drawable.checkbox2);
                break;

        }
        return  grid;
    }

    public void backToLast()
    {
        if(FragmentMealPortSettingManager.editType == 0)
        {
            FragmentMealPortSettingManager.showFramgemtMPAdd();
        }
        else
        {
            FragmentMealPortSettingManager.showFragmentMpMealPortCheck(FragmentMealPortSettingManager.nowChooseMealPortPositon,false);
        }
    }
}
