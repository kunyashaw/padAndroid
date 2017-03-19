package com.huofu.RestaurantOS.bean.storeOrder;


import com.alibaba.fastjson.JSONObject;

/**
 * 收费子项目
 * @author kunyashaw
 *
 */

public class ChargeItemSub {
	public Long charge_item_id;//收费项目id
	public Long product_id ;//产品id
	public JSONObject product;
	public MealProduct mp = new MealProduct();//产品
	public Double amount;//收费项目的产品组成数量
	
	public String remark = "";

	public Long getCharge_item_id() {
		return charge_item_id;
	}

	public void setCharge_item_id(Long charge_item_id) {
		this.charge_item_id = charge_item_id;
	}

	public Long getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Long product_id) {
		this.product_id = product_id;
	}

	public JSONObject getProduct() {
		return product;
	}

	public void setProduct(JSONObject product) {
		this.product = product;
	}

	public MealProduct getMp() {
		return mp;
	}

	public void setMp(MealProduct mp) {
		this.mp = mp;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
