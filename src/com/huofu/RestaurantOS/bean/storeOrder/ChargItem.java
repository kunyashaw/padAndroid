package com.huofu.RestaurantOS.bean.storeOrder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * 收费项目
 * @author kunyashaw
 *
 */
public class ChargItem implements Cloneable{
	
	public Long charge_item_id;//收费项目id

	@JSONField(name="amount")
	public Integer charge_item_amount;//对应的数目 上面这句话是在使用fastJson解析时，将服务器中『amount』字段解析到本类中的charge_item_amount
	public String charge_item_name ;//收费项目名称

	public Integer packaged ;//是否需要打包

	public JSONArray subItems;
	public ArrayList<ChargeItemSub> listSubChargeItem = null;//收费项目的中子收费项目

	public Boolean analysisFlag = false;//收费项目是否要统计（如果收费子项目有一个需要统计，则该收费项目要统计）
	public Integer type = -1;//0->不统计打包(放在一起)  1->统计打包  2->统计堂食 3->不统计堂食(放在一起)
	public String orderId = null;//收费项目所属订单的编号
	public Integer takeSerinalNum = 0;//收费项目的流水号
	public Integer checkout_num = 0;//在出餐时用来统计本收费项目出了几份

	public Integer getShow_products() {
		return show_products;
	}

	public void setShow_products(Integer show_products) {
		this.show_products = show_products;
	}

	public Integer show_products = 0;//是否显示产品

	public Integer getPacked_amount() {
		return packed_amount;
	}

	public void setPacked_amount(Integer packed_amount) {
		this.packed_amount = packed_amount;
	}

	public Integer packed_amount = 0;

	@JSONField(name="price")
	public Integer charge_item_price = 0;//收费项目价格
	public String remark = "";
	public boolean ischecked = false;

	public Long getCharge_item_id() {
		return charge_item_id;
	}

	public void setCharge_item_id(Long charge_item_id) {
		this.charge_item_id = charge_item_id;
	}

	public Integer getCharge_item_amount() {
		return charge_item_amount;
	}

	public void setCharge_item_amount(Integer charge_item_amount) {
		this.charge_item_amount = charge_item_amount;
	}

	public String getCharge_item_name() {
		return charge_item_name;
	}

	public void setCharge_item_name(String charge_item_name) {
		this.charge_item_name = charge_item_name;
	}

	public Integer getPackaged() {
		return packaged;
	}

	public void setPackaged(Integer packaged) {
		this.packaged = packaged;
	}

	public JSONArray getSubItems() {
		return subItems;
	}

	public void setSubItems(JSONArray subItems) {
		this.subItems = subItems;
	}

	public ArrayList<ChargeItemSub> getListSubChargeItem() {
		return listSubChargeItem;
	}

	public void setListSubChargeItem(ArrayList<ChargeItemSub> listSubChargeItem) {
		this.listSubChargeItem = listSubChargeItem;
	}

	public Boolean getAnalysisFlag() {
		return analysisFlag;
	}

	public void setAnalysisFlag(Boolean analysisFlag) {
		this.analysisFlag = analysisFlag;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getTakeSerinalNum() {
		return takeSerinalNum;
	}

	public void setTakeSerinalNum(Integer takeSerinalNum) {
		this.takeSerinalNum = takeSerinalNum;
	}

	public Integer getCheckout_num() {
		return checkout_num;
	}

	public void setCheckout_num(Integer checkout_num) {
		this.checkout_num = checkout_num;
	}

	public Integer getCharge_item_price() {
		return charge_item_price;
	}

	public void setCharge_item_price(Integer charge_item_price) {
		this.charge_item_price = charge_item_price;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean ischecked() {
		return ischecked;
	}

	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		ChargItem newChargItem = (ChargItem)super.clone();
		newChargItem.listSubChargeItem = (ArrayList<ChargeItemSub>) listSubChargeItem.clone();
		return newChargItem;
	}
	
}
